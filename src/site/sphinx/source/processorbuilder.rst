======================================
独自のProcessorBuilderの作成
======================================

--------------------------------------------------------
CellProcessorを直接組み立てる場合
--------------------------------------------------------

本ライブラリでは、アノテーションを元にCellProcessorを作成しますが、従来の方法で直接作成することもできます。
その場合は、 ``ProcessorBuilder`` [ `JavaDoc <../apidocs/com/github/mygreen/supercsv/builder/ProcessorBuilder.html>`_ ]を実装します。


Integer型に対するCellProcessorをSuper CSVの既存のCellProcessorを使って組み立てる例を示します。

.. sourcecode:: java
    :linenos:
    
    import java.text.DecimalFormat;
    import java.util.List;
    import java.util.Optional;
    
    import org.supercsv.cellprocessor.FmtNumber;
    import org.supercsv.cellprocessor.ParseInt;
    import org.supercsv.cellprocessor.Trim;
    import org.supercsv.cellprocessor.constraint.NotNull;
    import org.supercsv.cellprocessor.ift.CellProcessor;
    
    import com.github.mygreen.supercsv.builder.ProcessorBuilder
    
    
    public class CustomProcessorBuilder implements ProcessorBuilder<Integer> {
        
        // 読み込み時用のCellProcessorの組み立て
        @Override
        public Optional<CellProcessor> buildForReading(Class<Integer> type, FieldAccessor field,
                Configuration config, Class<?>[] groups) {
            
            CellProcessor processor = new NotNull(new Trim(new ParseInt()));
            return Optional.of(processor);
        }
        
        // 書き込み時用のCellProcessorの組み立て
        @Override
        public Optional<CellProcessor> buildForWriting(Class<Integer> type, FieldAccessor field,
                Class<?>[] groups, Configuration config) {
            
            CellProcessor processor = new NotNull(new FmtNumber(new DecimalFormat("#,##0")));
            return Optional.of(processor);
        }
        
    }


作成したProcessorBuilderを使用する際には、アノテーション ``@CsvColumn(builder=<ビルダクラス>)`` で指定します。

.. sourcecode:: java
    :linenos:
    
    import com.github.mygreen.supercsv.annotation.CsvBean;
    import com.github.mygreen.supercsv.annotation.CsvColumn;
    
    @CsvBean
    public class SampleCsv {
        
        @CsvColumn(number=1, label="数値", builder=CustomProcessorBuilder.class)
        private Integer number;
        
    }


--------------------------------------------------------
エラー処理
--------------------------------------------------------

Super CSVの既存のCellProcessorでは、値が不正な場合は、
例外 ``SuperCsvCellProcessorException`` または、その子クラス ``SuperCsvConstraintViolationException`` が
スローされます。

* スローされた例外は、 ``CsvExceptionConverter`` でメッセージに変換することができます。
* *CsvExceptionConverter* は、 *CsvAnnotationBeanReader/CsvAnnotationBeanWriter* に組み込まれており、
  例外発生後に変換したメッセージを取得できます。
    
  * 詳細は、:doc:`値の検証時のエラーメッセージ <validation_message>` を参照してください。



.. sourcecode:: java
    :linenos:
    
    
    import java.nio.charset.Charset;
    import java.nio.file.Files;
    import java.io.File;
    import java.util.ArrayList;
    import java.util.List;
    
    import org.supercsv.prefs.CsvPreference;
    import org.supercsv.exception.SuperCsvCellProcessorException;
    
    import com.github.mygreen.supercsv.io.CsvAnnotationBeanReader;
    import com.github.mygreen.supercsv.validation.CsvExceptionConverter;
    
    
    public class Sample {
        
        public void sampleRead() {
            
            CsvAnnotationBeanReader<UserCsv> csvReader;
            try {
                csvReader = new CsvAnnotationBeanReader<>(
                    UserCsv.class,
                    Files.newBufferedReader(new File("sample.csv").toPath(), Charset.forName("Windows-31j")),
                    CsvPreference.STANDARD_PREFERENCE);
                
                // ファイルの読み込み
                List<UserCsv> list = csvReader.readAll();
            
            } catch(SuperCsvCellProcessorException e) {
                
                // 変換されたエラーメッセージの取得
                List<String> messages = csvReader.getErrorMessages();
                
            } finally {
                if(csvReader != null) {
                    csvReader.close();
                }
            }
        }
        
    }



クラスパスのルートに、プロパティファイル ``SuperCsvMessages.properties`` を配置しておくと、自動的に読み込まれます。

* メッセージキーは、例外がスローされた「CellProcessorのクラス名」「クラス名」「フィールド名」「フィールドのクラスタイプ」を組み合わせて、優先順位の高いものに一致した物が採用されます。

* メッセージ中では変数が利用可能で、予め利用可能な変数は下記が登録されています。
  
  * メッセージ変数は、``{key}`` で参照可能です。

* さらに、``${式}`` の形式だと、式言語として `JEXL <http://commons.apache.org/proper/commons-jexl/>`_ が利用可能です。
* ただし、 :doc:`Spring Frameworkと連携してエラーメッセージの取得方法を変更 <spring_message>` している場合は、定義する箇所は異なります。


.. list-table:: メッセージキーの候補
   :widths: 10 70 20
   :header-rows: 1
   
   * - 優先度
     - 形式
     - 例
     
   * - 1
     - ``<CellProcessorのクラス名>.<Beanのクラス名>.<フィールド名>``
     - *ParseInt.SampleCsv.number*
     
   * - 2
     - ``<CellProcessorのクラス名>.<フィールド名>``
     - *ParseInt.number*
     
   * - 3
     - ``<CellProcessorのクラス名>.<フィールドのクラスパス>``
     - *ParseInt.java.lang.Integer*
     
   * - 4
     - | ``<CellProcessorのクラス名>.<フィールドのクラスタイプの親のクラスパス>``
       | ※数値型と列挙型のみ
     - | *ParseInt.java.lang.Number*
       | *ParseInt.java.lang.Enum*
     
   * - 5
     - ``<CellProcessorのクラス名>``
     - *ParseInt*
     

.. list-table:: メッセージ中で利用可能な変数
   :widths: 30 70
   :header-rows: 1
   
   * - 変数名
     - 説明
     
   * - *lineNumber*
     - | CSVの実ファイル上の行番号。
       | カラムの値に改行が含まれている場合を考慮した実際の行番号なります。
       | 1から始まります。
       
   * - *rowNumber*
     - | CSVの論理上の行番号です。
       | 1から始まります。
       
   * - *columnNumber*
     - | CSVの列番号です。
       | 1から始まります。
       
   * - *label*
     - | ``@CsvColumn(label="<見出し>")`` 出指定したカラムの見出し名です。
       | label属性を指定していない場合は、フィールド名になります。
       
   * - *validatedValue*
     - | 不正となった値です。
       
   * - *formatter*
     - | ``java.util.Formatter`` のインスタンスです。
       | パース済みのオブジェクトをフォーマットするのに利用します。

.. sourcecode:: properties
    :linenos:
    :caption: SuperCsvMessage.propertiesの定義例
    
    ###################################################
    # 独自のエラーメッセージの定義
    ###################################################
    # 定義したキーは、再帰的に{キー名}で参照可能
    
    csvContext=[{rowNumber}行, {columnNumber}列]
    
    # CellProcessorに対するエラーメッセージ
    NotNull={csvContext} : 項目「{label}」の値は必須です。
    ParseInt={csvContext} : 項目「{label}」の値（{validatedValue}）は、整数として不正です。
    
    Unique.java.util.Date={csvContext} : 項目「{label}」の値（${formatter.format('%tF', validatedValue)}）は、ユニークではありません。
    


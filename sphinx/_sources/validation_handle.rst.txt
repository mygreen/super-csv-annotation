--------------------------------------------------------
エラー処理の方法
--------------------------------------------------------

^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
例外の種類とハンドリング
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

読み込み時の書式の不正や値の検証時に失敗した場合、例外 ``org.supercsv.exception.SuperCsvException`` がスローされます。

エラー内容を画面に表示するようなシステムの場合、例外の内容をメッセージに変換する必要があります。
そのような時には、``com.github.mygreen.supercsv.validation.CsvExceptionConverter`` を使い、エラーメッセージに変換します。

CsvExceptionConverter は、CsvAnnotaionBeanReader/CsvAnnotaionBeanWriterに組み込まれており、
例外発生時に自動的にエラーメッセージに変換されたものがため込まれます。

.. sourcecode:: none
   :caption: Super CSV Annotationの例外体系
   
   java.lang.RuntimeException
     └ org.supercsv.exception.SuperCsvException
         ├ org.supercsv.exception.SuperCsvReflectionException
         ├ org.supercsv.exception.SuperCsvCellProcessorException
         │    ├ org.supercsv.exception.SuperCsvConstraintViolationException
         │    │
         │    │ ※Super Csv Annotationの例外
         │    └ com.github.mygreen.supercsv.exception.SuperCsvValidationException
         │
         │※Super Csv Annotationの例外
         ├ com.github.mygreen.supercsv.exception.SuperCsvInvalidAnnotationException
         ├ com.github.mygreen.supercsv.exception.SuperCsvNoMatchHeaderException
         ├ com.github.mygreen.supercsv.exception.SuperCsvNoMatchColumnSizeException
         ├ com.github.mygreen.supercsv.exception.SuperCsvBindingException
         └ com.github.mygreen.supercsv.exception.SuperCsvRowException
   


.. list-table:: Super CSV の例外
   :widths: 40 60
   :header-rows: 1
   
   * - クラス名
     - 説明
     
   * - *SuperCsvException*
     - | Super CSVのルートの例外。
       | Super CSV及び、Super CSV Annotationの例外は、全てこのクラスを継承しています。
     
   * - *SuperCsvReflectionException*
     - | Beanのインスタンスなどの作成やプロパティへの値のマッピング時など、
       | リフレクションを使ってに失敗したときの例外。
     
   * - *SuperCsvCellProcessorException*
     - | CellProcessor内で、処理対象のセルの値のクラスタイプが不正などのときのときにスローされる例外。
       | 例えば、文字列を日時にパースするCellProcessor ``ParseDate`` で、パースに失敗した場合。
     
   * - *SuperCsvConstraintViolationException*
     - | 制約のCellProcessor内で、値が制約違反となり不正のときのにスローされる例外。
       | 例えば、CellProcessor ``LMinMax`` で、セルの値が指定した数値の範囲以外のときにスローされる例外。
     

.. list-table:: Super CSV Annotation の例外
   :widths: 40 60
   :header-rows: 1
   
   * - クラス名
     - 説明
     
   * - *SuperCsvValidationException*
     - | Super CSV Annotationの CellProcessor ``ValidationCellProcessor`` を
       | 実装しているCellProcessorの制約違反の例外。
       | メッセージ変数などの情報が格納されている。
     
   * - *SuperCsvInvalidAnnotationException*
     - | アノテーションの値が不正だったりした場合にスローされる例外。
     
   * - *SuperCsvNoMatchHeaderException*
     - | ヘッダー行を読み込む際に、``@CsvColumn(label="")`` で定義している
       | 値と異なる場合にスローされる例外。
     
   * - *SuperCsvNoMatchColumnSizeException*
     - | ヘッダー行やレコードを読み込む際に、``@CsvColumn`` で定義している
       | カラムサイズと異なる場合にスローされる例外。
       
   * - *SuperCsvRowException*
     - | 各カラムのCellProcesor内で発生した *SuperCsvCellProcessorException* の例外を、
       | レコードを単位にまとめた例外。
     
   * - *SuperCsvBindingException*
     - | 最終的にカラムのマッピングに失敗したときにスローされる例外。
       | CsvValidatorによるBeanの検証時のエラーも格納されている。
     



レコードの値の読み書きを行う場合に業務例外として扱うものは、``SuperCsvNoMatchColumnSizeException`` と ``SuperCsvBindingException`` の2つと考えて処理すればよいです。
他の例外は、設定が不正な場合にスローされるため、基本はシステムエラー(ランタイムエラー)として扱うことになります。
ただし、ヘッダー行の読み込み時は、``SuperCsvNoMatchHeaderException`` も考慮する必要があります。

CsvAnnotationBeanReader/CsvAnnotationBeanWriter は ``AutoCloseable`` が実装されていますが、
try-with-resources 文を使用する場合は注意が必要です。アノテーションの解析などはコンストラクタ内で行うので、
もし、その中で例外が発生するとCSVファイルに関連するリソースが解放されないくなるため分割して定義します。

また、1レコードずつ処理すると、例外発生時に処理が終わってしまうため、全レコードの値を検証したい場合は、
``readAll(...)/readWrite(...)`` メソッドの使用をお勧めします。

.. sourcecode:: java
    :linenos:
    :caption: 読み込み時のエラー処理
    
    import java.nio.charset.Charset;
    import java.nio.file.Files;
    import java.io.File;
    import java.io.IOException;
    import java.io.Reader;
    import java.util.ArrayList;
    import java.util.List;
    
    import org.supercsv.prefs.CsvPreference;
    import org.supercsv.exception.SuperCsvException;
    
    import com.github.mygreen.supercsv.io.CsvAnnotationBeanReader;
    import com.github.mygreen.supercsv.exception.SuperCsvBindingException;
    import com.github.mygreen.supercsv.exception.SuperCsvNoMatchColumnSizeException;
    import com.github.mygreen.supercsv.exception.SuperCsvNoMatchHeaderException;
    
    
    public class Sample {
        
        // 読み込み時の場合（1行づつ処理する場合）
        public void sampleReadEach() {
            
            try(Reader reader = Files.newBufferedReader(
                        new File("sample.csv").toPath(), Charset.forName("Windows-31j"));
                CsvAnnotationBeanReader<SampleCsv> csvReader = new CsvAnnotationBeanReader<>(
                        SampleCsv.class, reader, CsvPreference.STANDARD_PREFERENCE); ) {
                
                // ヘッダー行の読み込み
                String[] headers = csvReader.getHeader(true);
                
                List<SampleCsv> list = new ArrayList<>();
                
                // レコードの読み込み - 1行づつ
                SampleCsv record = null;
                while((record = csvReader.read()) != null) {
                	list.add(record);
                }
            
            } catch(SuperCsvNoMatchColumnSizeException
                    | SuperCsvBindingException
                    | SuperCsvNoMatchHeaderException e) {
                // レコードの値が不正な場合のときのエラー
                
            } catch(SuperCsvException e ) {
                // Super CSVの設定などのエラー
                
            } catch(IOException e) {
                // ファイルI/Oに関する例外
                
            }
        }
        
        // 読み込み時の場合（全件処理する場合）
        public void sampleReadAll() {
            
            try(Reader reader = Files.newBufferedReader(
                        new File("sample.csv").toPath(), Charset.forName("Windows-31j"));
                CsvAnnotationBeanReader<SampleCsv> csvReader = new CsvAnnotationBeanReader<>(
                        SampleCsv.class, reader, CsvPreference.STANDARD_PREFERENCE); ) {
                
                // 全件読み込む - SuperCsvBindingExceptionなどの例外発生しても続けて処理する
                List<SampleCsv> list = csvReader.readAll(true);
                
                // エラーメッセージの取得
                List<String> errorMessages = csvReader.getErrorMessages();
                
            } catch(SuperCsvException e ) {
                // Super CSVの設定などのエラー
                
            } catch(IOException e) {
                // ファイルI/Oに関する例外
                
            }
        }
    }
    

.. sourcecode:: java
    :linenos:
    :caption: 書き込み時のエラー処理
    
    import java.nio.charset.Charset;
    import java.nio.file.Files;
    import java.io.File;
    import java.io.IOException;
    import java.io.Writer;
    import java.util.ArrayList;
    import java.util.List;
    
    import org.supercsv.prefs.CsvPreference;
    import org.supercsv.exception.SuperCsvException;
    
    import com.github.mygreen.supercsv.io.CsvAnnotationBeanWriter;
    import com.github.mygreen.supercsv.exception.SuperCsvBindingException;
    
    
    public class Sample {
        
        // 書き込み時の場合（1行づつ処理する場合）
        public void sampleWriteEach() {
            
            try(Writer writer = Files.newBufferedWriter(
                        new File("sample.csv").toPath(), Charset.forName("Windows-31j"));
                CsvAnnotationBeanWriter<SampleCsv> csvWriter = new CsvAnnotationBeanWriter<>(
                        SampleCsv.class, reader, CsvPreference.STANDARD_PREFERENCE); ) {
                
                // ヘッダー行の書き込み
                csvWriter.writeHeaader();
                
                // レコードの書き込み - 1行づつ
                SampleCsv record1 = /* 省略*/;
                csvWriter.write(record1);
                
                SampleCsv record2 = /* 省略*/;
                csvWriter.write(record2);
                
            
            } catch (SuperCsvBindingException e) {
                // レコードの値が不正な場合のときのエラー
                
            } catch(SuperCsvException e ) {
                // Super CSVの設定などのエラー
                
            } catch(IOException e) {
                // ファイルI/Oに関する例外
                
            }
        }
        
        // 書き込み時の場合（全件処理する場合）
        public void sampleWriteAll() {
            
            try(Writer writer = Files.newBufferedWriter(
                        new File("sample.csv").toPath(), Charset.forName("Windows-31j"));
                CsvAnnotationBeanWriter<SampleCsv> csvWriter = new CsvAnnotationBeanWriter<>(
                        SampleCsv.class, writer, CsvPreference.STANDARD_PREFERENCE); ) {
                
                List<SampleCsv> list = /* 省略 */;
                
                // 全件書き込む - SuperCsvBindingExceptionなどの例外発生しても続けて処理する
                csvWriter.writeAll(list, true);
                
                // エラーメッセージの取得
                List<String> errorMessages = csvWriter.getErrorMessages();
                
            } catch(SuperCsvException e ) {
                // Super CSVの設定などのエラー
                
            } catch(IOException e) {
                // ファイルI/Oに関する例外
                
            }
        }
    }
    

^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
書き込み時の値の検証のスキップ
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

書き込み時の値をスキップしたい場合は、グループによる指定もできますが、システム設定を変更することで一律にスキップすることができます。

``BeanMappingFactory`` から ``Configuration`` を取得し、そのプロパティ ``skipValidationOnWrite`` の値を trueに設定します。

.. sourcecode:: java
    :linenos:
    
    import com.github.mygreen.supercsv.builder.BeanMapping;
    import com.github.mygreen.supercsv.builder.BeanMappingFactory;
    import com.github.mygreen.supercsv.io.CsvAnnotationBeanWriter;
    
    import java.nio.charset.Charset;
    import java.nio.file.Files;
    import java.io.File;
    
    import org.supercsv.prefs.CsvPreference;
    
    public class Sample {
        
        public void sampleWriteWithSkipValidation() {
        
            // システム情報の設定変更
            BeanMappingFactory mappingFactory = new BeanMappingFactory();
            mappingFactory.getConfiguration().setSkipValidationOnWrite(true);
            
            // BeanMappingの作成
            BeanMapping<SampleCsv> beanMapping = mappingFactory.create(SampleCsv.class);
            
            CsvAnnotationBeanWriter<SampleCsv> csvWriter = new CsvAnnotationBeanWriter<>(
                    beanMapping,
                    Files.newBufferedWriter(new File("sample.csv").toPath(), Charset.forName("Windows-31j")),
                    CsvPreference.STANDARD_PREFERENCE);
            
            //... 以下省略
        }
        
    }




--------------------------------------------------------
Bean単位での値の検証方法
--------------------------------------------------------

CellProcessorは、フィールド1つに対する単項目チェックです。

項目間に対する相関チェックを行う場合は、``CsvValidator`` で1レコード分のBeanに対するチェック処理を実装します。


^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
CsvValidatorの実装
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

``CsvValidator`` のメソッド ``validate(...)`` を実装します。

* エラー情報は、``CsvBindingErrors`` に格納し管理されています。
  
  * フィールドに対してエラーメッセージを追加する場合は、``CsvBindingErrors#rejectValue(...)`` で追加します。
  * 項目間などのグローバルなエラーメッセージを追加する場合は、``CsvBindingErrors#reject(...)`` で追加します。

* CsvValidatorは、CellProcessorの後に実行され、もしエラーがある場合、そのフィールドの値は空が設定されており、
  ``CsvBindingErrors#getFieldErrors(<フィールド名>)`` でフィールドに対するエラーがあるかどうか判定する必要があります。
  
  * 既にエラーがある場合などの判定処理を巻単位するため、``CsvField`` クラスを利用します。
  * 

.. sourcecode:: java
    :linenos:
    :caption: CsvValidatorの実装例
    
    import java.util.Map;
    
    import com.github.mygreen.supercsv.validation.CsvBindingErrors;
    import com.github.mygreen.supercsv.validation.CsvField;
    import com.github.mygreen.supercsv.validation.CsvFieldValidator;
    import com.github.mygreen.supercsv.validation.CsvValidator;
    import com.github.mygreen.supercsv.validation.ValidationContext;
    
    // SampleCsvに対するValidator
    public class SampleValidator implements CsvValidator<SampleCsv> {
        
        @Override
        public void validate(final SampleCsv record, final CsvBindingErrors bindingErrors,
                final ValidationContext<SampleCsv> validationContext) {
            
            // フィールド ageの定義
            final CsvField<Integer> ageField = new CsvField<>(bindingErrors, validationContext, record, "age");
            
            // フィールド salaryの定義
            final CsvField<Integer> salaryField = new CsvField<>(bindingErrors, validationContext, record, "salary");
            salaryField
                .add(new CsvFieldValidator<Integer>() {
                    
                    @Override
                    public void validate(final CsvBindingErrors bindingErrors, final CsvField<Integer> field) {
                        if(ageField.isEmpty()) {
                            return;
                        }
                        
                        // カラム「age(年齢)」が20以上の場合、カラム「給料(salary)」が設定されているかチェックする。
                        if(ageField.isNotEmpty() && ageField.getValue() >= 20 && field.isEmpty()) {
                            // メッセージ中の変数の作成
                            final Map<String, Object> vars = createMessageVariables(field);
                            vars.put("maxAge", 20);
                            
                            // ageに関するフィールドエラーの追加
                            bindingErrors.rejectValue(field.getName(), field.getType(), "age.required", vars);
                        }
                    }
                })
                .add(new MaxValidator(10_000_000))
                .validate(bindingErrors);
            
            
        }
        
        // CsvFieldValidator を別に実装
        public static class MaxValidator implements CsvFieldValidator<Integer> {
        
            private final int max;
            
            public MaxValidator(final int max) {
                this.max = max;
            }
            
            @Override
            public void validate(CsvBindingErrors bindingErrors, CsvField<Integer> field) {
                if(field.isEmpty()) {
                    return;
                }
                
                if(field.getValue() > max) {
                    // メッセージ変数の組み立て
                    Map<String, Object> vars = createMessageVariables(field);
                    vars.put("max", max);
                    
                    bindingErrors.rejectValue(field.getName(), field.getType(), "fieldError.max", vars);
                }
            }
            
        }
        
    }
    

``CsvFieldValidator#createMessageVariables(...)`` を利用すると、以下の良く使用するCSV情報に関する変数を作ることができます。


.. list-table:: よく使用するな変数
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
       
   * - *printer*
     - | 各フィールドの ``TextFormatter`` のインスタンスです。
       | ``${print#print(validatedValue)}`` でパース済みのオブジェクトをフォーマットするのに利用します。



作成したCsvValidatorは、 ``@CsvBean(validators=)`` で指定します。

.. sourcecode:: java
    :linenos:
    :caption: Beanの実装例
    
    import com.github.mygreen.supercsv.annotation.CsvBean;
    import com.github.mygreen.supercsv.annotation.CsvColumn;
    import com.github.mygreen.supercsv.annotation.constraint.CsvNumberMin;
    import com.github.mygreen.supercsv.annotation.constraint.CsvRequire;
    
    
    @CsvBean(header=true, validators=SampleValidator.class)
    public class SampleCsv {
        
        @CsvColumn(number=1, label="名前")
        @CsvRequire(considerBlank=true)
        private String name;
        
        @CsvColumn(number=2, label="年齢")
        private Integer age;
        
        @CsvColumn(number=3, label="給料")
        @CsvNumberMin("0")
        private Integer salary;
        
        // setter/ getterは省略
        
    }
    


^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
エラーメッセージの定義
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

``CsvBindingErrors#reject(...)/rejectValue(...)`` でエラーメッセージを追加する場合、エラーコードを指定します。

* エラーメッセージは、クラスパスのルートに配置したプロパティファイル ``SuperCsvMessages.properties`` が自動的に読み込まれるため、そこに定義します。
* メッセージキーは、「エラーコード」「クラス名」「フィールド名」「フィールドのクラスタイプ」を組み合わせて、優先順位の高いものに一致した物が採用されます。
* メッセージ中では変数が利用可能で、予め利用可能な変数は下記が登録されています。
  
  * メッセージ変数は、``{key}`` で参照可能です。

* さらに、``${exp}`` の形式だと、式言語として `JEXL <http://commons.apache.org/proper/commons-jexl/>`_ が利用可能です。
* ただし、 :doc:`Spring Frameworkと連携してエラーメッセージの取得方法を変更 <spring_message>` している場合は、定義する箇所は異なります。


.. list-table:: メッセージキーの候補
   :widths: 10 50 40
   :header-rows: 1
   
   * - 優先度
     - 形式
     - 例
     
   * - 1
     - ``<エラーコード>.<Beanのクラス名>.<フィールド名>``
     - | *age.reqired.SampleCsv.age*
       | ※「age.required」がエラーコード
     
   * - 2
     - ``<エラーコード>.<フィールド名>``
     - | *age.reqired.age*
       | ※「age.required」がエラーコード
     
   * - 3
     - ``<エラーコード>.<フィールドのクラスパス>``
     - | *age.reqired.java.lang.Integer*
       | ※「age.required」がエラーコード
     
   * - 4
     - | ``<エラーコード>.<フィールドのクラスタイプの親のクラスパス>``
       | ※数値型と列挙型のみ
     - | *age.reqired.java.lang.Number*
       | *age.reqired.java.lang.Enum*
       | ※「age.required」がエラーコード
     
   * - 5
     - ``<エラーコード>``
     - | *age.reqired*
       | ※「age.required」がエラーコード
     


.. sourcecode:: properties
    :linenos:
    :caption: SuperCsvMessage.propertiesの定義例
    
    ###################################################
    # 独自のエラーメッセージの定義
    ###################################################
    # 定義したキーは、再帰的に{キー名}で参照可能
    
    csvContext=[{rowNumber}行, {columnNumber}列]
    
    ## エラーコードに対するメッセージの定義
    age.required={csvContext} : 項目「{label}」は、年齢が{maxAge}歳以上の場合には必須です。
    fieldError.max={csvContext} : 項目「{label}」の値（${printer.print(validatedValue)}）は、${printer.print(max)}以内で入力してください。
    
    



^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
エラーのハンドリング
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

CsvValidatorの中でCsvBindingErrorsにエラー情報を追加すると、例外 ``SuperCsvBindingException`` としてスローされます。

* 例外クラスは、 ``CsvExceptionConverter`` メッセージに変換します。
* *CsvExceptionConverter* は、``CsvAnnotationBeanReader/CsvAnnotationBeanWriter`` に組み込まれているため、
  メソッド ``#getErrorMessages()`` で取得できます。
  
  * 詳細は、:doc:`値の検証時のエラーメッセージ <validation_message>` を参照してください。


.. sourcecode:: java
    :linenos:
    
    
    import java.nio.charset.Charset;
    import java.nio.file.Files;
    import java.io.File;
    import java.util.ArrayList;
    import java.util.List;
    
    import org.supercsv.prefs.CsvPreference;
    import org.supercsv.exception.SuperCsvException;
    
    import com.github.mygreen.supercsv.io.CsvAnnotationBeanReader;
    import com.github.mygreen.supercsv.validation.CsvExceptionConverter;
    
    
    public class Sample {
        
        public void sampleRead() {
            
            CsvAnnotationBeanReader<SampleCsv> csvReader;
            try {
                csvReader = new CsvAnnotationBeanReader<>(
                    SampleCsv.class,
                    Files.newBufferedReader(new File("sample.csv").toPath(), Charset.forName("Windows-31j")),
                    CsvPreference.STANDARD_PREFERENCE);
                
                // ファイルの読み込み
                List<SampleCsv> list = csvReader.readAll();
            
            } catch(SuperCsvException e) {
                
                // 変換されたエラーメッセージの取得
                List<String> messages = csvReader.getErrorMessages();
                
            } finally {
                if(csvReader != null) {
                    csvReader.close();
                }
            }
        }
        
    }



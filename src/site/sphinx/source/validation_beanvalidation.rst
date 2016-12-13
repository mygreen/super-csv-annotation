--------------------------------------------------------
Bean Validationとの連携
--------------------------------------------------------

Bean Validationによるカラムの値の検証を行う方法を説明します。

^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
ライブラリの追加
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Bean Validationを利用する際には、ライブリを追加します。 
Mavenを利用している場合は、pom.xmlに以下を追加します。

本ライブラリは、Bean Validation1.0/1.1の両方に対応しており、
その参照実装である `Hibernate Validator <http://hibernate.org/validator/>`_ を追加します。

Bean Validation 1.1(JSR-349)を利用する場合は、Hibernate Validator5.x系を利用します。
さらに、メッセージ中にJava EEのEL式が利用可能となっているため、その実装であるライブリを追加します。

* ただし、TomcatやGlassFishなどのWebコンテナ上で使用するのであれば、EL式のライブラリはそれらに組み込まれているため必要ありません。
* また、本ライブラリの機能を利用して、`JEXL <http://commons.apache.org/proper/commons-jexl/>`_ に切り替えるのであれば、式言語の追加は必要ありません。

.. sourcecode:: xml
    :linenos:
    :caption: pom.xmlの依存関係の追加（Bean Validation1.1を利用する場合）
    
    <!-- Bean Validation 1.1 -->
    <dependency>
        <groupId>javax.validation</groupId>
        <artifactId>validation-api</artifactId>
        <version>1.1.0.Final</version>
    </dependency>
    <dependency>
    <groupId>org.hibernate</groupId>
        <artifactId>hibernate-validator</artifactId>
        <version>5.3.3.Final</version>
    </dependency>
    
    <!-- EL式のライブラリが必要であれば追加します -->
    <dependency>
        <groupId>org.glassfish</groupId>
        <artifactId>javax.el</artifactId>
        <version>3.0.1-b08</version>
    </dependency>


Bean Validation 1.0(JSR-303)を利用する場合は、Hibernate Validator4.x系を利用します。
Bean Validation 1.0では、メッセージ中でEL式は利用できませんが、本ライブラリの機能を使用すれば、`JEXL <http://commons.apache.org/proper/commons-jexl/>`_ が利用できます。

.. sourcecode:: xml
    :linenos:
    :caption: pom.xmlの依存関係の追加（Bean Validation1.0を利用する場合）
    
    
    <!-- Bean Validation 1.0 -->
    <dependency>
    <groupId>javax.validation</groupId>
        <artifactId>validation-api</artifactId>
        <version>1.0.0.GA</version>
    </dependency>
    <dependency>
        <groupId>org.hibernate</groupId>
        <artifactId>hibernate-validator</artifactId>
        <version>4.3.2.Final</version>
    </dependency>


^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
Bean Validationの利用方法
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

アノテーション ``@CsvBean(validatosr=CsvBeanValidator.class)`` を指定します。

``CsvBeanValidator`` は、Bean Validation と、本ライブラリの ``CsvValidator`` をブリッジするクラスです。

独自のメッセージソースは、クラスパスのルートに ``HibernateValidation.properties`` を配置しておけば自動的に読み込まれます。

.. sourcecode:: java
    :linenos:
    
    import com.github.mygreen.supercsv.annotation.CsvBean;
    import com.github.mygreen.supercsv.annotation.CsvColumn;
    import com.github.mygreen.supercsv.validation.beanvalidation.CsvBeanValidator;
    
    import javax.validation.constraints.AssertTrue;
    import javax.validation.constraints.DecimalMax;
    import javax.validation.constraints.Pattern;
    
    import org.hibernate.validator.constraints.Length;
    import org.hibernate.validator.constraints.NotEmpty;
    import org.hibernate.validator.constraints.Range;
    
    // Bean Validationの指定方法
    @CsvBean(validators=CsvBeanValidator.class)
    private static class TestCsv {
        
        @CsvColumn(number=1)
        @NotEmpty
        private String id;
        
        @CsvColumn(number=2)
        @Length(max=10)
        @Pattern(regexp="[\\p{Alnum}]+", message="半角英数字で設定してください。")
        private String name;
        
        @CsvColumn(number=3)
        @Range(min=0, max=100)
        private Integer age;
        
        @CsvColumn(number=4)
        boolean used;
        
        // 相関チェック
        @AssertTrue(message="{name}が設定されている際には、{age}は必須です。")
        boolean isValidAgeRequired() {
            if(name != null && !name.isEmpty()) {
                return age != null;
            }
            
            return false;
        }
        
        // setter/gettterは省略
    }


Bean Validation による値の検証でエラーがある場合は、例外 ``SuperCsvBindingException`` としてスローされます。

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


^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
Bean Validationのカスタマイズ
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

本ライブラリ用のメッセージソースや `JEXL <http://commons.apache.org/proper/commons-jexl/>`_ に切り替える場合、
Bean Validationのインスタンスを変更する必要があります。

その場合は、``@CsvBean(validators=CsvBeanValidator.class)`` で指定するのではなく、
メソッド ``CsvAnnotationBeanReader#addValidators(...)`` 、``CsvAnnotationBeanWriter#addValidators(...)`` で直接追加します。


.. sourcecode:: java
    :linenos:
    
    import com.github.mygreen.supercsv.builder.BeanMapping;
    import com.github.mygreen.supercsv.builder.BeanMappingFactory;
    import com.github.mygreen.supercsv.localization.MessageInterpolator;
    import com.github.mygreen.supercsv.localization.MessageResolver;
    import com.github.mygreen.supercsv.localization.ResourceBundleMessageResolver;
    
    import java.nio.charset.Charset;
    import java.nio.file.Files;
    import java.io.File;
    
    import org.supercsv.prefs.CsvPreference;
    
    public class Sample {
        
        // Beanの定義（validatorsの指定は行わない）
        @CsvBean
        public static class SampleCsv {
            // 省略
        }
        
        public void sampleBeanValidationCustom() {
            
            // CsvReaderの作成
            CsvAnnotationBeanReader<SampleCsv> csvReader = new CsvAnnotationBeanReader<>(
                    SampleCsv.class,
                    Files.newBufferedReader(new File("sample.csv").toPath(), Charset.forName("Windows-31j")),
                    CsvPreference.STANDARD_PREFERENCE);
            
            // BeanValidator用のValidatorの作成
            final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
            
            // メッセージ処理クラスを本ライブラリのものに入れ替えてインスタンスを生成する
            final Validator beanValidator = validatorFactory.usingContext()
                .messageInterpolator(new MessageInterpolatorAdapter(
                    new ResourceBundleMessageResolver(),
                    new MessageInterpolator()))
                .getValidator();
            
            // Validatorの追加
            csvReader.addValidators(beanValidator);
            
        }
    
    }


メッセージ中の変数として、既存の変数に加えて、CSV用の次の変数が登録されており利用可能です。

.. list-table:: メッセージ中で利用可能な変数
   :widths: 30 70
   :header-rows: 1
   
   * - 変数名
     - 説明
     
   * - lineNumber
     - | CSVの実ファイル上の行番号。
       | カラムの値に改行が含まれている場合を考慮した実際の行番号なります。
       | 1から始まります。
       
   * - rowNumber
     - | CSVの論理上の行番号です。
       | 1から始まります。
       
   * - columnNumber
     - | CSVの列番号です。
       | 1から始まります。
       
   * - label
     - | ``@CsvColumn(label="<見出し>")`` 出指定したカラムの見出し名です。
       | label属性を指定していない場合は、フィールド名になります。
       
   * - validatedValue
     - | 不正となった値です。
       
   * - printer
     - | 各クラスの ``TextFormatter`` のインスタンスです。
       | ``${printer.print(validatedValue)}`` で、オブジェクトをフォーマットするのに利用します。


.. 以降は、埋め込んで作成する
.. include::  ./validation_beanvalidation_spring.rst



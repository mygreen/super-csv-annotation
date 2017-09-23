^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
BeanValidationとSpring Frameworkとの連携
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Spring Frameworkと連携することで、コードがシンプルになります。
また、独自のフィールド用のValidator内にSpringBeanをインジェクションすることも可能です。

~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
XMLによるコンテナの設定
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

XMLによる設定方法を説明します。

コンテナの定義の基本は次のようになります。

* アノテーションによるDIの有効化を行います。
* コンポーネントスキャン対象のパッケージの指定を行います。
* ``com.github.mygreen.supercsv.builder.SpringBeanFactory`` をSpringBeanとして登録します。
* Springの ``MessageSource`` で、本ライブラリのエラーメッセージ ``com.github.mygreen.supercsv.localization.SuperCsvMessages`` を読み込んでおきます。
  * 独自のエラーメッセージがあれば、追加で定義します。
  
  * ``com.github.mygreen.supercsv.localization.SpringMessageResolver`` に、``MessageSource`` を渡します。

* ``CsvBeanValidator`` に、Springの ``LocalValidatorFactoryBean`` で作成したBeanValidationのValidtorのインスタンスを渡します。

.. sourcecode:: xml
    :linenos:
    
    
    <?xml version="1.0" encoding="UTF-8"?>
    <!-- XMLによるコンテナの定義 -->
    <beans xmlns="http://www.springframework.org/schema/beans"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xmlns:context="http://www.springframework.org/schema/context"
        xsi:schemaLocation="http://www.springframework.org/schema/beans
            http://www.springframework.org/schema/beans/spring-beans.xsd
            http://www.springframework.org/schema/context
            http://www.springframework.org/schema/context/spring-context.xsd
        ">
        
        <!-- アノテーションによるDIの有効化の定義 -->
        <context:annotation-config />
        
        <!-- コンポーネントスキャン対象のパッケージの指定 -->
        <context:component-scan base-package="sample.spring" />
        
        <!-- Springのコンテナを経由するCSV用のBeanFactoryの定義 -->
        <bean id="springBeanFacatory" class="com.github.mygreen.supercsv.builder.SpringBeanFactory" />
        
        <!-- Spring標準のメッセージソースの定義 -->
        <bean id="messageSource" class="org.springframework.context.support.ResourceBundleMessageSource">
            <property name="basenames">
                <list>
                    <value>com.github.mygreen.supercsv.localization.SuperCsvMessages</value>
                    <value>TestMessages</value>
                </list>
            </property>
        </bean>
        
        <!-- Super CSV Annotation 用のMessgeResolverの定義 -->
        <bean id="springMessageResolver" class="com.github.mygreen.supercsv.localization.SpringMessageResolver">
            <property name="messageSource" ref="messageSource" />
        </bean>
        
        <!-- BeanValidation用のCsvValidatorの定義 -->
        <bean id="csvBeanValidator" class="com.github.mygreen.supercsv.validation.beanvalidation.CsvBeanValidator">
            <constructor-arg>
                <bean class="org.springframework.validation.beanvalidation.LocalValidatorFactoryBean">
                    <property name="messageInterpolator">
                        <bean class="com.github.mygreen.supercsv.validation.beanvalidation.MessageInterpolatorAdapter">
                            <constructor-arg ref="springMessageResolver" />
                            <constructor-arg><bean class="com.github.mygreen.supercsv.localization.MessageInterpolator" /></constructor-arg>
                        </bean>
                    </property>
                </bean>
            </constructor-arg>
        </bean>
        
    </beans>


~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
JavaConfigによるコンテナの設定
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Spring Framework3.0から追加された、JavaソースによるSpringBean定義の方法を説明します。

JavaConfigによる設定を使用する場合は、Spring Frameworkのバージョンをできるだけ最新のものを使用してください。
特に、機能が豊富なバージョン4.0以上の使用を推奨します。


.. sourcecode:: java
    :linenos:
    
    
    import javax.validation.Validator;
    
    import org.springframework.context.MessageSource;
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.ComponentScan;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.context.annotation.Description;
    import org.springframework.context.support.ResourceBundleMessageSource;
    import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
    
    import com.github.mygreen.supercsv.builder.SpringBeanFactory;
    import com.github.mygreen.supercsv.localization.MessageInterpolator;
    import com.github.mygreen.supercsv.localization.SpringMessageResolver;
    import com.github.mygreen.supercsv.validation.beanvalidation.CsvBeanValidator;
    import com.github.mygreen.supercsv.validation.beanvalidation.MessageInterpolatorAdapter;
    
    
    // Javaによるコンテナの定義
    @Configuration
    @ComponentScan(basePackages="sample.spring")
    public class SuperCsvConfig {
        
        @Bean
        @Description("Springのコンテナを経由するCSV用のBeanFactoryの定義")
        public SpringBeanFactory springBeanFactory() {
            return new SpringBeanFactory();
        }
        
        @Bean
        @Description("Spring標準のメッセージソースの定義")
        public MessageSource messageSource() {
            ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
            messageSource.addBasenames("com.github.mygreen.supercsv.localization.SuperCsvMessages", "TestMessages");
            return messageSource;
        }
        
        @Bean
        @Description("本ライブラリのSpring用のMessgeResolverの定義")
        public SpringMessageResolver springMessageResolver() {
            return new SpringMessageResolver(messageSource());
        }
        
        @Bean
        @Description("Spring用のBeanValidatorのValidatorの定義")
        public Validator csvLocalValidatorFactoryBean() {
            
            LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
            
            // メッセージなどをカスタマイズ
            validator.setMessageInterpolator(new MessageInterpolatorAdapter(
                    springMessageResolver(), new MessageInterpolator()));
            return validator;
        }
        
        @Bean
        @Description("CSV用のCsvValidaotrの定義")
        public CsvBeanValidator csvBeanValidator() {
            
            // ValidarorのインスタンスをSpring経由で作成したものを利用する
            CsvBeanValidator csvBeanValidator = new CsvBeanValidator(csvLocalValidatorFactoryBean());
            return csvBeanValidator;
        }
        
    }


~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
独自のConstraintValidatorの作成
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Bean Validationの独自のアノテーションを作成する際には、通常の方法と同じです。

* メタアノテーション ``@Constraint`` を付与します。
  
  * 属性 ``validatedBy`` に、 ``ConsraintValidator`` の実装クラスを指定します。

* 複数指定可能できるように、内部クラス ``List`` を定義しておきます。
  
  * Bean Validation 1.1の段階では、Java8から追加された ``@Repeatable`` は対応していませんが、
    従来の定義方法と揃えておくことで、*@Repeatable* を使ってJava8のスタイルで使用することができます。
  * ただし、今後リリース予定のBeanValidator2.0から *@Repeatable* 対応するため、定義しておいても問題はありません。

.. sourcecode:: java
    :linenos:
    
    import java.lang.annotation.Documented;
    import java.lang.annotation.ElementType;
    import java.lang.annotation.Repeatable;
    import java.lang.annotation.Retention;
    import java.lang.annotation.RetentionPolicy;
    import java.lang.annotation.Target;
    
    import javax.validation.Constraint;
    import javax.validation.Payload;
    
    //BeanValidationの制約のアノテーション
    @Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @Repeatable(UserMailPattern.List.class) // 対応していないので、定義しなくても良い。
    @Constraint(validatedBy=UserMailPatternValidator.class)
    public @interface UserMailPattern {
        
        // 共通の属性の定義
        Class<?>[] groups() default {};
        String message() default "{sample.spring.UserMailPattern.message}";
        Class<? extends Payload>[] payload() default {};
        
        // 複数のアノテーションを指定する場合の定義
        @Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
        @Retention(RetentionPolicy.RUNTIME)
        @Documented
        @interface List {
            UserMailPattern[] value();
        }
        
    }


``ConstraintValidator`` の実装中で、SpringBeanのインジェクションを行いたい場合は、アノテーション ``@Resource/@Autowired`` など使います。

*ConstraintValidator* 自身は、SpringBeanとして登録する必要はありません。

.. sourcecode:: java
    :linenos:
    
    import javax.validation.ConstraintValidator;
    import javax.validation.ConstraintValidatorContext;
    
    import org.springframework.beans.factory.annotation.Autowired;
    
    
    // ConstraintValidatorの実装
    public class UserMailPatternValidator implements ConstraintValidator<UserMailPattern, String> {
        
        // SpringBeanをインジェクションします。
        @Autowired
        private UserService userService;
        
        @Override
        public void initialize(final UserMailPattern constraintAnnotation) {
            
        }
        
        @Override
        public boolean isValid(final String value, final ConstraintValidatorContext context) {
            
            // nullの場合は対象外
            if(value == null) {
                return true;
            }
            
            return userService.isMailPattern(value);
        }
        
    }

~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
CsvBeanの定義
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

CSVのBeanの定義では、``@CsvBean(validator=CsvBeanValidator.class)`` で、CsvBeanValidatorを指定します。

.. sourcecode:: java
    :linenos:
    
    import com.github.mygreen.supercsv.annotation.CsvBean;
    import com.github.mygreen.supercsv.annotation.CsvColumn;
    import com.github.mygreen.supercsv.validation.beanvalidation.CsvBeanValidator;
    
    
    @CsvBean(header=true, validator=CsvBeanValidator.class)
    public class UserCsv {
        
        @CsvColumn(number=1, label="メールアドレス")
        @UserMailPattern   // 独自のBeanValidator用のアノテーションの指定
        private String mail;
        
        // setter/getterは省略
        
    }


~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
値の検証方法
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

* ``BeanMappingFactory#getConfiguration()`` 取得できる、システム設定に、SpringBeanFactoryを設定します。
* ``CsvExceptionConverter#setMessageResolver(...)`` に、SpringBeanとして定義した ``SpringMessageResolver`` を設定します。
  
  * さらに、 ``CsvAnnotationBeanReader#setExceptionConverter(...)`` に、作成した *CsvExceptionConverter* を渡します。


.. sourcecode:: java
    :linenos:
    
    
    import com.github.mygreen.supercsv.builder.BeanMapping;
    import com.github.mygreen.supercsv.builder.BeanMappingFactory;
    import com.github.mygreen.supercsv.io.CsvAnnotationBeanReader;
    
    import java.nio.charset.Charset;
    import java.nio.file.Files;
    import java.io.File;
    import java.util.ArrayList;
    import java.util.List;
    
    import org.supercsv.prefs.CsvPreference;
    import org.supercsv.exception.SuperCsvException;
    
    @Service
    public class CsvService {
        
        @Autowired
        private SpringBeanFactory beanFactory;
        
        @Autowired
        private SpringMessageResolver messageResolver;
        
        public void sampleSpring() {
        
            // BeanMappingの作成 - SpringBeanFactoryを設定する
            BeanMappingFactory beanMappingFactory = new BeanMappingFactory();
            beanMappingFactory.getConfiguration().setBeanFactory(beanFactory);
            
            // BeanMappingの作成
            BeanMapping<UserCsv> beanMapping = mappingFactory.create(UserCsv.class);
            
            CsvAnnotationBeanReader<UserCsv> csvReader;
            try {
                csvReader = new CsvAnnotationBeanReader<>(
                        beanMapping,
                        Files.newBufferedReader(new File("user.csv").toPath(), Charset.forName("Windows-31j")),
                        CsvPreference.STANDARD_PREFERENCE);
                
                // CsvExceptionConverterの作成 - SpringMessageResolverを設定する
                CsvExceptionConverter exceptionConverter = new CsvExceptionConverter();
                exceptionConverter.setMessageResolver(messageResolver);
                
                // CsvExceptionConverterを設定する
                svReader.setExceptionConverter(exceptionConverter);
                
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





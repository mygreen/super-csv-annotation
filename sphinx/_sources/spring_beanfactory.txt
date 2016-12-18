--------------------------------------------------------
SpringBeanFactoryの設定方法
--------------------------------------------------------

^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
XMLによるコンテナの設定
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

XMLによる設定方法を説明します。

コンテナの定義の基本は次のようになります。

* アノテーションによるDIの有効化を行います。
* コンポーネントスキャン対象のパッケージの指定を行います。
* ``com.github.mygreen.supercsv.builder.SpringBeanFactory`` をSpringBeanとして登録します。

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
        <bean id="springBeanFactory" class="com.github.mygreen.supercsv.builder.SpringBeanFactory" />
        
    </beans>


^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
JavaConfigによるコンテナの設定
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Spring Framework3.0から追加された、JavaソースによるSpringBean定義の方法を説明します。

JavaConfigによる設定を使用する場合は、Spring Frameworkのバージョンをできるだけ最新のものを使用してください。
特に、機能が豊富なバージョン4.0以上の使用を推奨します。


.. sourcecode:: java
    :linenos:
    
    
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.ComponentScan;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.context.annotation.Description;
    
    import com.github.mygreen.supercsv.builder.SpringBeanFactory;
    
    // Javaによるコンテナの定義
    @Configuration
    @ComponentScan(basePackages="sample.spring")
    public class SuperCsvConfig {
        
        @Bean
        @Description("Springのコンテナを経由するCSV用のBeanFactoryの定義")
        public SpringBeanFactory springBeanFactory() {
            return new SpringBeanFactory();
        }
        
    }



^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
SpringBeanとしての定義
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

ステレオタイプのアノテーション ``@Component/@Service/@Reposition/@Controller`` をサポートしているため、
これらを使いSpringBeanを定義します。


~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
独自の書式の作成
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

独自の書式の ``TextFormatter`` を定義する際には、スコープを *proptotype* にします。
読み込み時のエラーメッセージが、カラムごとに異なる場合があるため、インスタンスを別にします。

.. sourcecode:: java
    :linenos:
    
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.context.annotation.Scope;
    import org.springframework.stereotype.Component;
    
    import com.github.mygreen.supercsv.cellprocessor.format.AbstractTextFormatter;
    import com.github.mygreen.supercsv.cellprocessor.format.TextParseException;
    
    
    /**
     * {@link SampleType}に対するTextFromatterの実装
     */
    @Scope("prototype")
    @Component
    public class SampleTypeFormatter extends AbstractTextFormatter<SampleType> {
        
        // SpringBeanのインジェクション
        @Autowired
        private SampleService sampleService;
        
        @Override
        public SampleType parse(final String text) {
            
            try {
                // 業務ロジックなので省略
            } catch(Exception e) {
                throw new TextParseException(text, SampleType.class, e);
            }
            
        }
        
        @Override
        public String print(final SampleType object) {
            // 業務ロジックなので省略
        }
        
    }


~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
独自の変換/検証の作成
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

検証用の ``ConstraintProcessorFactory`` の例として、DBにユーザが存在するかチェックするCellProcessorで説明します。

変換用の ``ConversionProcessorFactory`` も同様の実装方法になります。

* ``ConstraintProcessorFactory`` はシングルトンで定義します。
* CellProcessorは、カラムごとに固有なインスタンスにするため、Springのコンテナ管理外とします。
* CellProcessor内で、SpringBeanを利用したい場合は、 *ConstraintProcessorFactory* クラスでインジェクションしておき、
  それをコンストラクタやsetterメソッドで渡すようにします。

.. sourcecode:: java
    :linenos:
    
    
    import java.util.Optional;
    
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.stereotype.Component;
    import org.supercsv.cellprocessor.ift.CellProcessor;
    
    import com.github.mygreen.supercsv.builder.BuildType;
    import com.github.mygreen.supercsv.builder.Configuration;
    import com.github.mygreen.supercsv.builder.FieldAccessor;
    import com.github.mygreen.supercsv.cellprocessor.ConstraintProcessorFactory;
    import com.github.mygreen.supercsv.cellprocessor.format.TextFormatter;
    
    /**
     * ユーザ名がDBに存在するか検証するCellProcessorを作成するクラス
     */
    @Component
    public class UserNameExistFactory implements ConstraintProcessorFactory<CsvUserNameExist> {
        
        // SpringBeanのインジェクション
        @Autowired
        private UserService userService;
        
        @Override
        public Optional<CellProcessor> create(final CsvUserNameExist anno, final Optional<CellProcessor> next,
                final FieldAccessor field, final TextFormatter<?> formatter, final Configuration config) {
            
            // UserService はCellProcessorに渡す
            final UserNameExist processor = next.map(n -> new UserNameExist(userService, n))
                    .orElseGet(() -> new UserNameExist(userService));
            processor.setValidationMessage(anno.message());
            
            return Optional.of(processor);
        }
        
    }


*CellProcessor* は、コンストラクタで渡されたSpringBean(UserService)のメソッドを呼び出します。

.. sourcecode:: java
    :linenos:
    
    import org.supercsv.cellprocessor.ift.CellProcessor;
    import org.supercsv.cellprocessor.ift.StringCellProcessor;
    import org.supercsv.util.CsvContext;
    
    import com.github.mygreen.supercsv.cellprocessor.ValidationCellProcessor;
    
    /**
     * ユーザ名の存在チェックを行う制約のCellProcessor
     */
    public class UserNameExist extends ValidationCellProcessor implements StringCellProcessor {
        
        private final UserService userService;
        
        public UserNameExist(final UserService userService) {
            checkPreconditions(userService);
            this.userService = userService;
        }
        
        public UserNameExist(final UserService userService, final CellProcessor next) {
            super(next);
            checkPreconditions(userService);
            this.userService = userService;
        }
        
        private static void checkPreconditions(final UserService userService) {
            if(userService == null) {
                throw new NullPointerException("userService should not be null");
            }
            
        }
        
        @Override
        public <T> T execute(final Object value, final CsvContext context) {
            
            if(value == null) {
                return next.execute(value, context);
            }
            
            final String result = value.toString();
            
            // サービスのメソッドの呼び出し
            if(!userService.existByUserName(result)) {
                throw createValidationException(context)
                    .messageFormat("%s dose not found user name.", result)
                    .rejectedValue(result)
                    .build();
            }
            
            return next.execute(value, context);
        }
        
    }



アノテーションの作成法補は、通常と変わりません。

メタアノテーション *@CsvConstraint* で ConstraintProcessorFactoryを実装したクラスUserNameExistFactoryを指定します。

.. sourcecode:: java
    :linenos:
    
    import java.lang.annotation.Documented;
    import java.lang.annotation.ElementType;
    import java.lang.annotation.Repeatable;
    import java.lang.annotation.Retention;
    import java.lang.annotation.RetentionPolicy;
    import java.lang.annotation.Target;
    
    /**
     * ユーザが存在するかチェックするためのアノテーション
     */
    @Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @Repeatable(CsvUserNameExist.List.class)
    @CsvConstraint(value=UserNameExistFactory.class)
    public @interface CsvUserNameExist {
        
        String message() default "{sample.CsvUserNameExist.message}";
        
        Class<?>[] groups() default {};
        
        int order() default 0;
        
        @Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
        @Retention(RetentionPolicy.RUNTIME)
        @Documented
        @interface List {
            
            CsvUserNameExist[] value();
        }
        
    }


~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
独自のProcessorBuilderクラスの作成
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

独自のクラスタイプのProcessorBuilderは、自身をSpringBeanとして登録します。
通常は、シングルトンでかまいません。

.. sourcecode:: java
    :linenos:
    
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.stereotype.Component;
    
    import com.github.mygreen.supercsv.builder.AbstractProcessorBuilder;
    import com.github.mygreen.supercsv.builder.Configuration;
    import com.github.mygreen.supercsv.builder.FieldAccessor;
    import com.github.mygreen.supercsv.cellprocessor.format.TextFormatter;
    
    @Component
    public class SampleTypeProcessorBuilder extends AbstractProcessorBuilder<SampleType> {
        
        @Autowired
        private SampleTypeFormatter formatter;
        
        @Autowired
        private SampleConstraintFactory sampleConstaintFactory;
        
        @Override
        protected void init() {
            super.init();
            
            // 制約や変換用のアノテーションの登録
            // @CsvConstaint/@CsvCoversionの関連付けを省略し手いる場合に登録する
            registerForConstraint(CsvSampleConstraint.class, sampleConstaintFactory);
        }
        
        @Override
        protected TextFormatter<SampleType> getDefaultFormatter(final FieldAccessor field, final Configuration config) {
            return formatter;
        }
        
    }



~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
CsvValidator の作成
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

*CsvValidator* クラスは、シングルトンで管理します。

SpringBeanをインジェクションしたいものがあれば行います。


.. sourcecode:: java
    :linenos:
    
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.stereotype.Component;
    
    import com.github.mygreen.supercsv.validation.CsvBindingErrors;
    import com.github.mygreen.supercsv.validation.CsvValidator;
    import com.github.mygreen.supercsv.validation.ValidationContext;
    
    /**
     * {@link UserCsv}に対するValidator
     *
     */
    @Component
    public class UserValidator implements CsvValidator<UserCsv> {
        
        // SpringBeanのインジェクション
        @Autowired
        private UserService userService;
        
        @Override
        public void validate(final UserCsv record, final CsvBindingErrors bindingErrors,
                final ValidationContext<UserCsv> validationContext) {
            
            // 業務ロジックなので省略
            
        }
        
    }



~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
リスナクラスの作成
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

リスナクラスは、POJOであるため、SpringBeanをインジェクションしたいものがあれば行います。


.. sourcecode:: java
    :linenos:
    
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.stereotype.Component;
    
    import com.github.mygreen.supercsv.annotation.CsvPostRead;
    import com.github.mygreen.supercsv.annotation.CsvPreWrite;
    
    /**
     * {@link UserCsv}に対するリスナクラス
     * 
     */
    @Component
    public class UserListener {
        
        @Autowired
        private UserService userSerivce;
        
        @CsvPreWrite
        @CsvPostRead
        public void validate(final UserCsv record) {
            
            // 業務ロジックなので省略
            
        }
        
    }


^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
CsvBeanの定義
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

CSVのBeanの定義では、SpringBeanとして定義したクラスを指定します。

.. sourcecode:: java
    :linenos:
    
    import com.github.mygreen.supercsv.annotation.CsvBean;
    import com.github.mygreen.supercsv.annotation.CsvColumn;
    import com.github.mygreen.supercsv.annotation.constraint.CsvRequire;
    import com.github.mygreen.supercsv.annotation.format.CsvFormat;
    import com.github.mygreen.supercsv.validation.beanvalidation.CsvBeanValidator;
    
    @CsvBean(header=true,
        validator=UserValidator.class,   // Spring管理のValidatorの指定
        listener=UserListener.class      // Spring管理のリスナクラスの指定
    )
    public class UserCsv {
        
        // Srping管理のFormatterを指定する場合。
        @CsvColumn(number=1, label="タイプ")
        @CsvFormat(formatter=SampleTypeFormatter.class)
        private SampleType sampleType1;
        
        // Spring管理のConstraintProcessorFactory を利用している検証用アノテーション
        @CsvColumn(number=2, label="名前")
        @CsvUserNameExist
        private String name;
        
        // Spring管理のProcessorBuilderを指定する場合
        @CsvColumn(number=3, label="ホームページ", builder=SampleTypeProcessorBuilder.class)
        private SampleType sampleType2;
        
        // setter/getterは省略
        
    }



.. note::
   
   SpringBeanの管理外のクラスを指定した場合は、通常の方法としてインスタンスが作成されます。
   
   ただし、管理外のクラスでもインジェクション用のアノテーション（@Resource/@Autowired）があれば、
   インジェクションされます。


^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
SpringBeanFactoryの使用方法
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

``BeanMappingFactory#getConfiguration()`` 取得できる、システム設定に、SpringBeanFactoryを設定します。


.. sourcecode:: java
    :linenos:
    
    
    import com.github.mygreen.supercsv.builder.BeanMapping;
    import com.github.mygreen.supercsv.builder.BeanMappingFactory;
    import com.github.mygreen.supercsv.io.CsvAnnotationBeanReader;
    
    import java.nio.charset.Charset;
    import java.nio.file.Files;
    import java.io.File;
    
    import org.supercsv.prefs.CsvPreference;
    
    @Service
    public class CsvService {
        
        @Autowired
        private SpringBeanFactory beanFactory;
        
        public void sampleSpring() {
        
            // BeanMappingの作成 - SpringBeanFactoryを設定する
            BeanMappingFactory beanMappingFactory = new BeanMappingFactory();
            beanMappingFactory.getConfiguration().setBeanFactory(beanFactory);
            
            // BeanMappingの作成
            BeanMapping<UserCsv> beanMapping = mappingFactory.create(UserCsv.class);
            
            CsvAnnotationBeanReader<UserCsv> csvReader = new CsvAnnotationBeanReader<>(
                    beanMapping,
                    Files.newBufferedReader(new File("user.csv").toPath(), Charset.forName("Windows-31j")),
                    CsvPreference.STANDARD_PREFERENCE);
            
            //... 以下省略
        }
    
    }




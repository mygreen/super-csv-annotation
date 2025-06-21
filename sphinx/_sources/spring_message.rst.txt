--------------------------------------------------------
エラーメッセージの設定方法
--------------------------------------------------------

SpringFrameworkの ``MessageSource`` を利用する方法を説明します。

^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
XMLによるコンテナの設定
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

XMLによる設定方法を説明します。

コンテナの定義の基本は次のようになります。

* ``MessageSource`` として、本ライブラリのエラーメッセージ ``com.github.mygreen.supercsv.localization.SuperCsvMessages`` を読み込んでおきます。

  * 独自のエラーメッセージがあれば、追加で定義します。

* ``com.github.mygreen.supercsv.localization.SpringMessageResolver`` に、``MessageSource`` を渡します。

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
        
        <!-- Spring標準のメッセージソースの定義 -->
        <bean id="messageSource" class="org.springframework.context.support.ResourceBundleMessageSource">
            <property name="basenames">
                <list>
                    <value>com.github.mygreen.supercsv.localization.SuperCsvMessages</value>
                    <value>MyMessages</value>
                </list>
            </property>
        </bean>
        
        <!-- 本ライブラリのSpring用のMessgeResolverの定義 -->
        <bean id="springMessageResolver" class="com.github.mygreen.supercsv.localization.SpringMessageResolver">
            <property name="messageSource" ref="messageSource" />
        </bean>
            
    </beans>


^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
JavaConfigによるコンテナの設定
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Spring Framework3.0から追加された、JavaソースによるSpringBean定義の方法を説明します。

JavaConfigによる設定を使用する場合は、Spring Frameworkのバージョンをできるだけ最新のものを使用してください。
特に、機能が豊富なバージョン4.0以上の使用を推奨します。


.. sourcecode:: java
    :linenos:
    
    
    import org.springframework.context.MessageSource;
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.ComponentScan;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.context.annotation.Description;
    import org.springframework.context.support.ResourceBundleMessageSource;
    
    import com.github.mygreen.supercsv.localization.SpringMessageResolver;
    
    
    // Javaによるコンテナの定義
    @Configuration
    @ComponentScan(basePackages="sample.spring")
    public class SuperCsvConfig {
        
        @Bean
        @Description("Spring標準のメッセージソースの定義")
        public MessageSource messageSource() {
            ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
            messageSource.addBasenames(
                    "com.github.mygreen.supercsv.localization.SuperCsvMessages",
                    "MyMessages");
            return messageSource;
        }
        
        @Bean
        @Description("本ライブラリのSpring用のMessgeResolverの定義")
        public SpringMessageResolver springMessageResolver() {
            return new SpringMessageResolver(messageSource());
        }
        
    }


^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
SpringMessageResolverの使用方法
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

``CsvExceptionConverter#setMessageResolver(...)`` に、SpringBeanとして定義した ``SpringMessageResolver`` を設定します。

さらに、 ``CsvAnnotationBeanReader#setExceptionConverter(...)`` に、作成した *CsvExceptionConverter* を渡します。

.. sourcecode:: java
    :linenos:
    
    
    import com.github.mygreen.supercsv.io.CsvAnnotationBeanReader;
    import com.github.mygreen.supercsv.localization.SpringMessageResolver;
    import com.github.mygreen.supercsv.validation.CsvExceptionConverter;
    
    import java.nio.charset.Charset;
    import java.nio.file.Files;
    import java.io.File;
    
    import org.supercsv.prefs.CsvPreference;
    
    @Service
    public class CsvService {
        
        @Autowired
        private SpringMessageResolver messageResolver;
        
        public void sampleSpring() {
        
            CsvAnnotationBeanReader<UserCsv> csvReader = new CsvAnnotationBeanReader<>(
                    UserCsv.class,
                    Files.newBufferedReader(new File("user.csv").toPath(), Charset.forName("Windows-31j")),
                    CsvPreference.STANDARD_PREFERENCE);
            
            // CsvExceptionConverterの作成 - SpringMessageResolverを設定する
            CsvExceptionConverter exceptionConverter = new CsvExceptionConverter();
            exceptionConverter.setMessageResolver(messageResolver);
            
            // CsvExceptionConverterを設定する
            svReader.setExceptionConverter(exceptionConverter);
            
            //... 以下省略
        }
    
    }




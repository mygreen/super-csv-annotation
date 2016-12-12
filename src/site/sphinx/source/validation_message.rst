--------------------------------------------------------
値の検証時のエラーメッセージ
--------------------------------------------------------

セルの値の検証時など例外がスローされ、 ``CsvExceptionConverter`` によりメッセージに変換します。
*CsvExceptionConverter* は、*CsvAnnotationBeanReader/CsvAnnotationBeanWriter* に設定します。

メッセージリソースは ``MessageResolver`` で管理されてます。

* デフォルトでは、 ``ResourceBundleMessageResolver`` が設定されています。
  
  * *ResourceBundleMessageResolver* では、システムのプロパティファイル ``com/github/mygreen/supercsv/localization/SuperCsvMessages.properties`` が読み込まれます。
  * 独自のメッセージは、クラスパスのルート配置した ``SuperCsvMessages.properties`` が読み込まれます。

* 実装を切り替えることで、他の形式のファイルからも取得することができます。

.. list-table:: MessageResolverの実装
   :widths: 40 60
   :header-rows: 1
   
   * - クラス名
     - 説明
     
   * - *ResourceBundleMessageResolver*
     - | ``java.util.ResourceBundle`` を経由してメッセージを参照します。
       | デフォルトでは ``SuperCsvMessages.properties`` を読み込みます。
     
   * - *PropertiesMessageResolver*
     - | ``java.util.Properties`` を経由してメッセージを参照します。
     
   * - *SpringMessageResolver*
     - | ``org.springframework.context.MessageSource`` を経由してメッセージを参照します。
       | 詳細は、 :doc:`Spring Frameworkとの連携（エラーメッセージの設定方法） <spring_message>` を参照してください。


また、メッセージ中には ``{var}`` の形式で変数が可能です。
さらに、``${exp}`` の形式で 式言語の `Java Expression Language (JEXL) <http://commons.apache.org/proper/commons-jexl/>`_ が利用可能です。

デフォルト設定では、式言語ので呼び出し可能な関数が登録されています。
``com.github.mygreen.supercsv.expression.CustomFunction`` のメソッドが接頭語 `f:` を付けて呼び出し可能です。
また、独自の関数も登録可能です。


.. sourcecode:: java
    :linenos:
    :caption: メッセージと式言語の設定
    
    import com.github.mygreen.supercsv.io.CsvAnnotationBeanReader;
    import com.github.mygreen.supercsv.localization.MessageInterpolator;
    import com.github.mygreen.supercsv.localization.ResourceBundleMessageResolver;
    import com.github.mygreen.supercsv.validation.CsvExceptionConverter;
    import com.github.mygreen.supercsv.expression.ExpressionLanguageJEXLImpl;
    
    import java.nio.charset.Charset;
    import java.nio.file.Files;
    import java.io.File;
    import java.util.ResourceBundle;
    
    import org.supercsv.prefs.CsvPreference;
    
    public class Sample
     {
        
        public void customMessageAndExpression() {
        
            // CsvExceptionConverterの作成
            CsvExceptionConverter exceptionConverter = new CsvExceptionConverter();
            
            // メッセージソースを既存の物に対して追加する
            ResourceBundleMessageResolver messageResolver = new ResourceBundleMessageResolver();
            messageResolver.addResourceBundle(ResourceBundle.getBundle("SampleMessages"));
            exceptionConverter.setMessageResolver(messageResolver);
            
            // 式言語に独自の関数を登録
            // 参照可能な関数は、public static である必要があります。
            ExpressionLanguageJEXLImpl el = new ExpressionLanguageJEXLImpl();
            Map<String, Object> funcs = new HashMap<>(); 
            funcs.put("my", SampleFunctions.class);
            el.getJexlEngine().setFunctions(funcs);
            
            // カスタマイズした式言語の登録
            exceptionConverter.setMessageInterpolator(new MessageInterpolator(el));
            
            // CsvExceptionConverterを設定する
            CsvAnnotationBeanReader<UserCsv> csvReader = new CsvAnnotationBeanReader<>(
                    UserCsv.class,
                    Files.newBufferedReader(new File("user.csv").toPath(), Charset.forName("Windows-31j")),
                    CsvPreference.STANDARD_PREFERENCE);
            
            csvReader.setExceptionConverter(exceptionConverter);
            
            //... 以下省略
        }
    
    }







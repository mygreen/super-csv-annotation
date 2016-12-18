--------------------------------------------------------
独自のクラスタイプへの対応
--------------------------------------------------------

独自の変換処理を実装するには、2つのステップを踏む必要があります。

1. ``TextFormatter`` の実装クラスの作成
2. アノテーション ``@CsvFormat`` で作成したTextFormatterの指定


以下、それぞれに対して解説していきます。

^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
TextFormatterの実装クラスの作成
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

サンプルとして、 ``java.net.URL`` にマッピングするTextFromatterを作成します。

* 抽象クラス ``AbstractTextFormatter`` [ `JavaDoc <../apidocs/com/github/mygreen/supercsv/cellprocessor/format/AbstractTextFormatter.html>`_ ] を継承します。
* メソッド ``parse(...)`` で、読み込み時の文字列からオブジェクト型に変換するメソッドの実装を行います。
  
  * 変換に失敗した場合、例外 ``TextParseException`` をスローします。
    既存の例外が発生する場合は、ラップするようにします。
  
* メソッド ``print(...)`` で、書き込みにオブジェクト型から文字列に変換するメソッドの実装を行います。
  
  * 変換に失敗した場合、例外 ``TextPrintException`` をスローします。
    既存の例外が発生する場合は、ラップするようにします。
  
* メソッド ``getMessageVariables(...)`` で、エラーメッセージ中の変数を定義することができます。
  
  * 必要がなければ実装する必要はありません。


.. sourcecode:: java
    :linenos:
    
    import java.net.MalformedURLException;
    import java.net.URL;
    import java.util.HashMap;
    import java.util.Map;
    
    import com.github.mygreen.supercsv.cellprocessor.format.AbstractTextFormatter;
    import com.github.mygreen.supercsv.cellprocessor.format.TextParseException;
    
    // URLにマッピングするクラス
    public class UrlFormatter extends AbstractTextFormatter<URL> {
        
        // 読み込み時の文字列からオブジェクト型に変換するメソッドの実装
        @Override
        public URL parse(final String text) {
            
            try {
                return new URL(text);
            } catch(MalformedURLException e) {
                throw new TextParseException(text, URL.class, e);
            }
            
        }
        
        // 書き込みにオブジェクト型から文字列に変換するメソッドの実装
        @Override
        public String print(final URL object) {
            return object.toExternalForm();
        }
        
        // 読み込み時のエラーメッセージ中で使用可能な変数の定義
        // 必要があればオーバライドして実装します。
        @Override
        public Map<String, Object> getMessageVariables() {
        
            final Map<String, Object> vars = new HashMap<>();
            
            vars.put("key", "vars");
            
            return vars;
        }
        
    }


^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
作成したTextFormatterの指定
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

作成したTextFormatterを指定するには、アノテーション ``@CsvFormat`` [ `JavaDoc <../apidocs/com/github/mygreen/supercsv/annotation/format/CsvFormat.html>`_ ]を使用します。

* 属性 ``formatter`` で、TextFormatterの実装クラスを指定します。
* 属性 ``message`` で、読み込み時のエラーメッセージを指定することができます。

.. sourcecode:: java
    :linenos:
    
    import java.net.URL;
    
    import com.github.mygreen.supercsv.annotation.CsvBean;
    import com.github.mygreen.supercsv.annotation.CsvColumn;
    import com.github.mygreen.supercsv.annotation.format.CsvFormat;
    
    @CsvBean
    public class SampleCsv {
        
        @CsvColumn(number=1, label="ホームページ")
        @CsvFormat(formatter=UrlFormatter.class,
                message="[{rowNumber}行, {columnNumber}列] : 項目「{label}」の値（{validatedValue}）は、URLの形式として不正です。"
        private URL homepage;
        
        // setter/getterは省略
        
    }






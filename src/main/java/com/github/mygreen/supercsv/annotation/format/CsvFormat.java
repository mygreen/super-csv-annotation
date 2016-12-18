package com.github.mygreen.supercsv.annotation.format;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.supercsv.cellprocessor.ift.CellProcessor;

import com.github.mygreen.supercsv.cellprocessor.format.AbstractTextFormatter;
import com.github.mygreen.supercsv.cellprocessor.format.TextFormatter;

/**
 * 任意のタイプの書式を指定するためのアノテーションです。
 * <p>このライブラリに対応していない書式や独自のタイプに対応する際に使用します。</p>
 * 
 * <h3 class="description">基本的な使い方</h3>
 * <ul>
 *   <li>属性{@link #formatter()}で、{@link TextFormatter}の実装クラスを指定します。</li>
 *   <li>{@link TextFormatter}は、文字列とオブジェクトの相互に変換するためのインタフェースになります。
 *    <br>通常は、抽象クラス{@link AbstractTextFormatter}を継承して作成します。
 *   </li>
 * </ul>
 * 
 * <p>以下に、{@literal java.net.URL}にマッピングする例を示します。</p>
 * 
 * <pre class="highlight"><code class="java">
 * {@literal @CsvBean}
 * public class SampleCsv {
 *     
 *     {@literal @CsvColumn(number=1, label="ホームページ")}
 *     {@literal @CsvFormat(formatter=UrlFormatter.class)}  // 実装したTextFormatterの指定
 *     private URL homepage;
 *     
 *     // getter/setterは省略
 * }
 * 
 * // TextFormatterの実装クラス - URLにマッピングするクラス
 * public class UrlFormatter extends AbstractTextFormatter{@literal <URL>} {
 *       
 *       // 読み込み時の文字列からオブジェクト型に変換するメソッドの実装
 *       {@literal @Override}
 *       public URL parse(final String text) {
 *           
 *           try {
 *               return new URL(text);
 *           } catch(MalformedURLException e) {
 *               throw new TextParseException(text, URL.class, e);
 *           }
 *           
 *       }
 *       
 *       // 書き込みにオブジェクト型から文字列に変換するメソッドの実装
 *       {@literal @Override}
 *       public String print(final URL object) {
 *           return object.toExternalForm();
 *       }
 *       
 *       // 読み込み時のエラーメッセージ中で使用可能な変数の定義
 *       // 必要があればオーバライドして実装します。
 *       {@literal @Override}
 *       public {@literal Map<String, Object>} getMessageVariables() {
 *       
 *           final {@literal Map<String, Object>} vars = new {@literal HashMap<>}();
 *           
 *           vars.put("key", "vars");
 *           
 *           return vars;
 *       }
 *       
 *   }
 * </code></pre>
 * 
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CsvFormat {
    
    /**
     * フォーマッタのクラスを指定します。
     * @return {@link TextFormatter}の実装クラスを指定します。
     */
    @SuppressWarnings("rawtypes")
    Class<? extends TextFormatter> formatter();
    
    /**
     * エラー時のメッセージを指定します。
     * <p>{@literal {key}}の書式の場合、プロパティファイルから取得した値を指定できます。</p>
     * <p>このメッセージは、文字列をブール型にパースする際に失敗したときに適用されるメッセージに使用します。</p>
     * @return 省略した場合は、適用された{@link CellProcessor}に基づいたメッセージが出力されます。
     */
    String message() default "";
}

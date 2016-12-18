package com.github.mygreen.supercsv.annotation.format;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Currency;

import org.supercsv.cellprocessor.ift.CellProcessor;


/**
 * 数値型の書式を定義するためのアノテーションです。
 * 
 * <p>対応する型は以下の通りです。</p>
 * <ul>
 *  <li>プリミティブ型「byte/short/int/long/float/double」および、そのラッパークラス。</li>
 *  <li>java.math.BigDecimal/java.math.BigInteger</li>
 * </ul>
 * 
 * <p>プリミティブ型に対して読み込む際に、CSVのカラムの値が空の場合、それぞれのプリミティブ型の初期値が設定されます。
 *  <br>整数型の場合は {@literal 0} が、小数型の場合は {@literal 0.0} が設定されます。
 * </p>
 * 
 * <h3 class="description">基本的な使い方</h3>
 * 
 * <ul>
 *   <li>属性{@link #pattern()}で、書式を指定します。
 *     <br>省略した場合は、各クラスの標準の形式で解析します。
 *   </li>
 *   <li>属性{@link #locale()}で、ロケールを指定します。
 *     <ul>
 *       <li>言語コードのみを指定する場合、{@literal ja} の2桁で指定します。</li>
 *       <li>言語コードと国コードを指定する場合、{@literal ja_JP} のようにアンダーバーで区切り指定します。</li>
 *       <li>和暦を扱う時など、バリアントを指定する場合も同様に、{@literal ja_JP_JP} のようにさらにアンダーバーで区切り指定します。</li>
 *     </ul>
 *    </li>
 *    <li>属性{@link #currency()}で、通貨コード(<a class="externalink" href="https://ja.wikipedia.org/wiki/ISO_4217" target="_blank">ISO-4217コード</a>)を指定します。</li>
 * </ul>
 * 
 * <pre class="highlight"><code class="java">
 * {@literal @CsvBean}
 * public class SampleCsv {
 *     
 *     {@literal @CsvColumn(number=1)}
 *     {@literal @CsvNumberFormat(pattern="#,##0")}
 *     private int number;
 *     
 *     {@literal @CsvColumn(number=2, label="給与")}
 *     {@literal @CsvNumberFormat(pattern="\u00A4\u00A4 #,##0.0000", locale="ja_JP", currency="USD")}
 *     private Double salary;
 *     
 *     // getter/setterは省略
 * }
 * </code></pre>
 * 
 * @version 2.0
 * @author T.TSUCHIE
 *
 */
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CsvNumberFormat {
    
    /**
     * 数値の書式を指定します。
     * <p>{@link DecimalFormat}で解釈可能な値を指定する必要があります。</p>
     * @return 指定しない場合は、クラスタイプごとの標準の書式が適用されます。
     */
    String pattern() default "";
    
    /**
     * 読み込み時に数値の解析を曖昧に行うか指定します。
     * <p>例えば、 {@literal 12.51} と小数を整数型にマッピングする場合、{@literal 13}と丸めの補正が行われます。
     *   <br>また、{@literal 123,456.0ab} のように、途中から数値以外の文字が出現した場合、それまでの文字 {@literal 123,456.0} を抽出して処理が行われます。
     * </p>
     *  
     * @return trueの場合、曖昧に解析を行います。
     */
    boolean lenient() default false;
    
    /**
     * 通貨コード {@link Currency} を指定します。(<a class="externalink" href="https://ja.wikipedia.org/wiki/ISO_4217" target="_blank">ISO 4217 Code</a>)
     * <p>属性{@link #pattern()}を指定したときのみ有効になります。
     * @return 通貨コード (ISO 4217 コード)
     */
    String currency() default "";
    
    /**
     * ロケールを指定します。
     * <p>{@literal <言語コード>}、{@literal <言語コード>_<国コード>}、{@literal <言語コード>_<国コード>_<バリアント>}の3つの書式で指定します。</p>
     * <p>例 'ja'、'ja_JP'、'ja_JP_JP'</p>
     * <p>属性{@link #pattern()}を指定したときのみ有効になります。
     * @return 省略した場合、システム標準の値を使用します。
     */
    String locale() default "";
    
    /**
     * 丸めの方法を指定します。
     * <p>詳細は、<a class="externalink" href="https://docs.oracle.com/javase/jp/8/docs/api/java/math/RoundingMode.html" target="_blank">RoundingModeのJavaDoc</a>を参照してください。</p>
     * @since 1.2
     * @return パース時やフォーマット時の桁の丸めの方法。
     */
    RoundingMode rounding() default RoundingMode.HALF_EVEN;
    
    /**
     * 丸めの精度を指定します。
     * <p>属性{@link #pattern()}を指定しない場合に有効になります。
     * <p>主に小数の場合に有効桁数を揃える際に利用します。
         <br>例えば、{@literal precision=4}で、文字列 {@literal 123.45} を double型にマッピングする場合、結果は {@literal 123.4} として読み込まれます。
       </p>
     * <p>丸めの方法は、属性{@link #rounding()}で指定します。</p>
     * @since 1.2
     * @return 0以上の値を指定すると有効になります。
     */
    int precision() default -1;
    
    /**
     * エラー時のメッセージを指定します。
     * <p>{@literal {key}}の書式の場合、プロパティファイルから取得した値を指定できます。</p>
     * <p>このメッセージは、文字列を数値型にパースする際に失敗したときに適用されるメッセージに使用します。</p>
     * 
     * <ul>
     *   <li>lineNumber : カラムの値に改行が含まれている場合を考慮した実際の行番号です。1から始まります。</li>
     *   <li>rowNumber : CSVの行番号です。1から始まります。</li>
     *   <li>columnNumber : CSVの列番号です。1から始まります。</li>
     *   <li>label : カラムの見出し名です。</li>
     *   <li>validatedValue : 実際のカラムの値です。</li>
     *   <li>pattern : アノテーションの属性{@link #pattern()}の値です。指定されている場合のみ設定されます。</li>
     * </ul>
     * 
     * @return 省略した場合は、適用された{@link CellProcessor}に基づいたメッセージが出力されます。
     */
    String message() default "{com.github.mygreen.supercsv.annotation.format.CsvNumberFormat.message}";
    
}

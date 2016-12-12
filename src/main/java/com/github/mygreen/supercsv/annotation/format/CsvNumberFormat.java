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
     * @return 省略した場合、各クラスの標準の形式で解析します。
     */
    String pattern() default "";
    
    /**
     * 読み込み時に数値の解析を厳密に行うか判定します。
     * @return trueの場合、非厳密に判定を行います。
     */
    boolean lenient() default false;
    
    /**
     * 通貨コード {@link Currency} を指定します。(<a href="https://ja.wikipedia.org/wiki/ISO_4217" target="_blank">ISO 4217 Code</a>)
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
     * @since 1.2
     * @return パース時やフォーマット時の桁の丸めの方法。
     */
    RoundingMode rounding() default RoundingMode.HALF_EVEN;
    
    /**
     * 丸めの精度を指定します。
     * <p>属性{@link #pattern()}を指定しない場合に有効になります。
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

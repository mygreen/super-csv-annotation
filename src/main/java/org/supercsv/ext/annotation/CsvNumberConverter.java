package org.supercsv.ext.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Currency;

import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.ext.cellprocessor.constraint.Max;
import org.supercsv.ext.cellprocessor.constraint.Min;


/**
 * 数値型の変換規則を定義するアノテーション。
 * 
 * <p>対応する型は以下の通りです。</p>
 * <ul>
 *  <li>プリミティブ型「byte/short/int/long/float/double」および、そのラッパークラス。</li>
 *  <li>java.math.BigDecimal/java.math.BigInteger</li>
 * </ul>
 * 
 * @version 1.2
 * @author T.TSUCHIE
 *
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CsvNumberConverter {
    
    /**
     * 数値の書式を指定します。
     * <p>{@link DecimalFormat}で解釈可能な値を指定する必要があります。</p>
     */
    String pattern() default "";
    
    /**
     * 読み込み時に数値の解析を厳密に行うか判定します。
     * <p>属性{@link #pattern()}を指定した時のみ有効になります。
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
     * 丸め動作を指定します。
     * <p>属性{@link #pattern()}を指定したときのみ有効になります。
     * @since 1.2
     * @return フォーマットする際の桁の丸めの方法。
     */
    RoundingMode rounding() default RoundingMode.HALF_EVEN;
    
    /**
     * カラムの値が指定した値以上（最小値）かどうかチェックします。
     * <p>{@link CellProcessor}の{@link Min}が設定されます。</p>
     * @return 値は、クラスタイプやアノテーションで指定した書式に沿った値を指定する必要があります。
     */
    String min() default "";
    
    /**
     * カラムの値が指定した値以下（最大値）かどうかチェックします。
     * <p>{@link CellProcessor}の{@link Max}が設定されます。</p>
     * @return 値は、クラスタイプやアノテーションで指定した書式に沿った値を指定する必要があります。
     */
    String max() default "";
    
}

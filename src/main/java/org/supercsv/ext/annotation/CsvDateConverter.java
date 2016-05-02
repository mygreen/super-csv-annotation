package org.supercsv.ext.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.TimeZone;

import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.ext.cellprocessor.constraint.FutureDate;
import org.supercsv.ext.cellprocessor.constraint.PastDate;
import org.supercsv.ext.cellprocessor.joda.FutureJoda;
import org.supercsv.ext.cellprocessor.joda.PastJoda;
import org.supercsv.ext.cellprocessor.time.FutureTemporal;
import org.supercsv.ext.cellprocessor.time.PastTemporal;


/**
 * 日時型の変換規則を定義するアノテーション。
 * 
 * <p>対応しているクラスタイプと標準の書式は以下の通りです。<p>
 * <table>
 *  <caption>対応している日時のクラスタイプ</caption>
 *  <tr>
 *   <th>クラスタイプ</th>
 *   <th>標準の書式</th>
 *  </td>
 *  <tr>
 *   <td>java.util.Date</td>
 *   <td>{@literal yyyy-MM-dd HH:mm:ss}</td>
 *  </tr>
 *  <tr>
 *   <td>java.sql.Date</td>
 *   <td>{@literal yyyy-MM-dd}</td>
 *  </tr>
 *  <tr>
 *   <td>java.sql.Time</td>
 *   <td>{@literal HH:mm:ss}</td>
 *  </tr>
 *  <tr>
 *   <td>java.sql.Timestamp</td>
 *   <td>{@literal yyyy-MM-dd HH:mm:ss.SSS}</td>
 *  </tr>
 *  <tr>
 *   <td>java.time.LocalDateTime</td>
 *   <td>{@literal uuuu-MM-dd HH:mm:ss}</td>
 *  </tr>
 *  <tr>
 *   <td>java.time.LocalDate</td>
 *   <td>{@literal uuuu-MM-dd}</td>
 *  </tr>
 *  <tr>
 *   <td>java.time.LocalTime</td>
 *   <td>{@literal HH:mm:ss}</td>
 *  </tr>
 *  <tr>
 *   <td>java.time.ZonedDateTime</td>
 *   <td>{@literal uuuu-MM-dd HH:mm:ssxxx'['VV']'}</td>
 *  </tr>
 *   <td>org.joda.time.LocalDateTime</td>
 *   <td>{@literal yyyy-MM-dd HH:mm:ss}</td>
 *  </tr>
 *  <tr>
 *   <td>org.joda.time.LocalDate</td>
 *   <td>{@literal yyyy-MM-dd}</td>
 *  </tr>
 *  <tr>
 *   <td>org.joda.time.LocalTime</td>
 *   <td>{@literal HH:mm:ss}</td>
 *  </tr>
 *
 * </table>
 * 
 * @author T.TSUCHIE
 *
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CsvDateConverter {
    
    /**
     * 日時の書式を指定します。
     * <p>クラスタイプごとに、指定可能な書式はことなります。</p>
     * <p>{@literal java.util.Date/java.sql.Date/java.sql.Time/java.sql.Timestamp}の場合、{@link java.text.SimpleDateFormat}で解釈可能な書式を指定します。</p>
     * <p>{@literal java.time.LocalDateTime/java.time.LocalDate/java.time.LocalTime/java.time.ZonedDateTime}の場合、{@link java.time.format.DateTimeFormatter}で解釈可能な書式を指定します。</p>
     * <p>{@literal org.joda.time.LocalDateTime/org.joda.time.LocalDate/org.joda.time.LocalTime/}の場合、{@link org.joda.time.format.DateTimeFormat}で解釈可能な書式を指定します。</p>
     * @return 指定しない場合は、クラスタイプにより自動的に決まります。
     */
    String pattern() default "";
    
    /**
     * 読み込み時に日時の解析を厳密に行うか判定します。
     * @return trueの場合、非厳密に判定を行います。
     */
    boolean lenient() default true;
    
    /**
     * タイムゾーンを指定します。
     * <p>{@link TimeZone#getTimeZone(String)}で解釈可能な値を指定する必要があります。</p>
     * <p>{@link Asia/Tokyo, GMT, GMT+09:00}などの値を指定します。</p>
     * <p>ただし、オフセットを持たないクラス対タイプ{@literal LocalDateTime, LocalDate, LocalTime}の時は、指定しても意味がありません。</p>
     * @return 省略した場合、システム標準の値を使用します。
     */
    String timezone() default "";
    
    /**
     * ロケールを指定します。
     * <p>{@literal <言語コード>}、{@literal <言語コード>_<国コード>}、{@literal <言語コード>_<国コード>_<バリアント>}の3つの書式で指定します。</p>
     * <p>例 'ja'、'ja_JP'、'ja_JP_JP'</p>
     * @return 省略した場合、システム標準の値を使用します。
     */
    String locale() default "";
    
    /**
     * カラムの値が指定した値以上（最小値）の未来日かどうかチェックします。
     * <p>{@literal java.util.Date/java.sql.Date/java.sql.Time/java.sql.Timestamp}の場合、{@link CellProcessor}の{@link FutureDate}が設定されます。</p>
     * <p>{@literal java.time.LocalDateTime/java.time.LocalDate/java.time.LocalTime/java.time.ZonedDateTime}の場合、{@link CellProcessor}の{@link FutureTemporal}が設定されます。</p>
     * <p>{@literal org.joda.time.LocalDateTime/org.joda.time.LocalDate/org.joda.time.LocalTime/}の場合、{@link CellProcessor}の{@link FutureJoda}が設定されます。</p>
     * @return 値は、アノテーションで指定した書式に沿った値を指定する必要があります。
     */
    String min() default "";
    
    /**
     * カラムの値が指定した値以下（最大値）の過去日かどうかチェックします。
     * <p>{@literal java.util.Date/java.sql.Date/java.sql.Time/java.sql.Timestamp}の場合、{@link CellProcessor}の{@link PastDate}が設定されます。</p>
     * <p>{@literal java.time.LocalDateTime/java.time.LocalDate/java.time.LocalTime/java.time.ZonedDateTime}の場合、{@link CellProcessor}の{@link PastTemporal}が設定されます。</p>
     * <p>{@literal org.joda.time.LocalDateTime/org.joda.time.LocalDate/org.joda.time.LocalTime/}の場合、{@link CellProcessor}の{@link PastJoda}が設定されます。</p>
     * @return 値は、アノテーションで指定した書式に沿った値を指定する必要があります。
     */
    String max() default "";
}

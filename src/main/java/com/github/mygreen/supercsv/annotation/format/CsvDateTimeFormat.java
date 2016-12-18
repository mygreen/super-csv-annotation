package com.github.mygreen.supercsv.annotation.format;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.TimeZone;

import org.supercsv.cellprocessor.ift.CellProcessor;


/**
 * 日時型の書式を定義するためのアノテーションです。
 * 
 * <p>アノテーションを付与しないときや属性{@link #pattern()}を指定しないときは、クラスタイプごとに決まった標準の書式が適用されます。
 *  <br>対応しているクラスタイプと標準の書式は以下の通りです。
 * </p>
 * 
 * <table class="description">
 *  <caption>対応する日時のクラスタイプと標準の書式</caption>
 *  <thead>
 *  <tr>
 *   <th>クラスタイプ</th>
 *   <th>標準の書式</th>
 *  </tr>
 *  </thead>
 *  <tbody>
 *  <tr>
 *   <td>java.util.Date</td>
 *   <td>{@literal yyyy-MM-dd HH:mm:ss}</td>
 *  </tr>
 *  <tr>
 *   <td>java.util.Calendar</td>
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
 *  <tr>
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
 *  <tr>
 *   <td>org.joda.time.DateTime</td>
 *   <td>{@literal yyyy-MM-dd HH:mm:ssZZ}</td>
 *  </tr>
 *  
 *  </tbody>
 * </table>
 * 
 * <h3 class="description">基本的な使い方</h3>
 * 
 * <ul>
 *   <li>属性{@link #pattern()}で、書式を指定します。
 *     <br>省略した場合は、標準の書式が適用されます。
 *   </li>
 *   <li>属性{@link #locale()}でロケールを指定します。
 *     <ul>
 *       <li>言語コードのみを指定する場合、{@literal ja} の2桁で指定します。</li>
 *       <li>言語コードと国コードを指定する場合、{@literal ja_JP} のようにアンダーバーで区切り指定します。</li>
 *       <li>和暦を扱う時など、バリアントを指定する場合も同様に、{@literal ja_JP_JP} のようにさらにアンダーバーで区切り指定します。</li>
 *     </ul>
 *    </li>
 * </ul>
 * 
 * <pre class="highlight"><code class="java">
 * {@literal @CsvBean}
 * public class SampleCsv {
 *     
 *     // 和暦を扱う場合
 *     {@literal @CsvColumn(number=1)}
 *     {@literal @CsvDateTimeFormat(pattern="GGGGyy年MM月dd日", locale="ja_JP_JP")}
 *     private Date japaneseDate;
 *     
 *     {@literal @CsvColumn(number=2, label="更新日時")}
 *     {@literal @CsvDateTimeFormat(pattern="uuuu/MM/dd HH:mm:ss")}
 *     private LocalDateTime updateTime;
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
public @interface CsvDateTimeFormat {
    
    /**
     * 日時の書式を指定します。
     * <p>クラスタイプごとに、指定可能な書式は異なります。</p>
     * <p>{@literal java.util.Date/java.util.Calendar/java.sql.Date/java.sql.Time/java.sql.Timestamp}の場合、{@link java.text.SimpleDateFormat}で解釈可能な書式を指定します。</p>
     * <p>{@literal java.time.LocalDateTime/java.time.LocalDate/java.time.LocalTime/java.time.ZonedDateTime}の場合、{@link java.time.format.DateTimeFormatter}で解釈可能な書式を指定します。</p>
     * <p>{@literal org.joda.time.LocalDateTime/org.joda.time.LocalDate/org.joda.time.LocalTime}の場合、{@link org.joda.time.format.DateTimeFormat}で解釈可能な書式を指定します。</p>
     * @return 指定しない場合は、クラスタイプごとの標準の書式が適用されます。
     */
    String pattern() default "";
    
    /**
     * 読み込み時に日時の解析を曖昧に行うか指定します。
     * <p>曖昧に解析する場合、例えば、{@literal 2016-02-31} と存在しない日を解析すると、{@literal 2016-03-02} と自動的に補正が行われます。</p>
     * @return trueの場合、曖昧に解析を行います。
     */
    boolean lenient() default false;
    
    /**
     * タイムゾーンを指定します。
     * <p>{@link TimeZone#getTimeZone(String)}で解釈可能な値を指定する必要があります。</p>
     * <p>{@literal Asia/Tokyo, GMT, GMT+09:00}などの値を指定します。</p>
     * <p>ただし、オフセットを持たないクラスタイプ{@literal LocalDateTime, LocalDate, LocalTime}の時は、指定しても意味がありません。</p>
     * @return 省略した場合、システム標準の値を使用します。
     */
    String timezone() default "";
    
    /**
     * ロケールを指定します。
     * <p>{@literal <言語コード>}、{@literal <言語コード>_<国コード>}、{@literal <言語コード>_<国コード>_<バリアント>}の3つの何れかで書式を指定します。</p>
     * <p>例 'ja'、'ja_JP'、'ja_JP_JP'</p>
     * @return 省略した場合、システム標準の値を使用します。
     */
    String locale() default "";
    
    /**
     * エラー時のメッセージを指定します。
     * <p>{@literal ${key}}の書式の場合、プロパティファイルから取得した値を指定できます。</p>
     * <p>このメッセージは、文字列を日時型にパースする際に失敗したときに適用されるメッセージに使用します。</p>
     * 
     * <ul>
     *   <li>lineNumber : カラムの値に改行が含まれている場合を考慮した実際の行番号です。1から始まります。</li>
     *   <li>rowNumber : CSVの行番号です。1から始まります。</li>
     *   <li>columnNumber : CSVの列番号です。1から始まります。</li>
     *   <li>label : カラムの見出し名です。</li>
     *   <li>validatedValue : 実際のカラムの値です。</li>
     *   <li>pattern : アノテーションの属性{@link #pattern()}の値です。指定されていない場合はデフォルトの値が設定されます。</li>
     * </ul>
     * 
     * @return 省略した場合は、適用された{@link CellProcessor}に基づいたメッセージが出力されます。
     */
    String message() default "{com.github.mygreen.supercsv.annotation.format.CsvDateTimeFormat.message}";
    
}

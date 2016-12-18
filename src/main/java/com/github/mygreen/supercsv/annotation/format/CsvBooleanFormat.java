package com.github.mygreen.supercsv.annotation.format;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.LinkedHashSet;

import org.supercsv.cellprocessor.ift.CellProcessor;


/**
 * boolean/Boolean型の書式を定義するためのアノテーションです。
 * 
 * <h3 class="description">基本的な使い方</h3>
 * 
 * <ul>
 *   <li>属性{@link #readForTrue()}、{@link #readForFalse()}で、読み込み時のtrueまたはfalseと判定する候補の値を指定します。</li>
 *   <li>属性{@link #writeAsTrue()}、{@link #writeAsFalse()}で、書き込み時のtrueまたはfalse値に該当する文字を指定します。</li>
 *   <li>属性{@link #ignoreCase()}で、読み込み時に大文字・小文字の区別なく候補の値と比較するか指定します。</li>
 * </ul>
 * 
 * <pre class="highlight"><code class="java">
 * {@literal @CsvBean}
 * public class SampleCsv {
 *     
 *     // boolean型の読み込み時のtrueとfalseの値の変換規則を指定します。
 *     {@literal @CsvColumn(number=1)}
 *     {@literal @CsvBooleanFormat(}
 *           readForTrue={"○", "有効", "レ"}, readForFalse={"×", "無効", "-", ""},
 *           writeAsTrue="○", writeAsFalse="×")
 *     private boolean availaled;
 *     
 *     // 読み込み時の大文字・小文字の区別を行わない
 *     {@literal @CsvColumn(number=2, label="チェック")}
 *     {@literal @CsvBooleanFormat(readForTrue={"OK"}, readForFalse={"NO"}, ignoreCase=true)}
 *     private Boolean checked;
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
public @interface CsvBooleanFormat {
    
    /**
     * 読み込み時に{@literal true} とする候補を指定します。
     * <p>{@link #readForFalse()} と重複する値がある場合は、{@link #readForTrue()} の値が優先されます。</p>
     * 
     * @return trueとして読み込む候補。
     */
    String[] readForTrue() default {"true", "1", "yes", "on", "y", "t"};
    
    /**
     * 読み込み時に{@literal false} とする候補を指定します。
     * <p>{@link #readForTrue()} と重複する値がある場合は、{@link #readForTrue()} の値が優先されます。</p>
     * 
     * @return falseとして読み込む候補。
     */
    String[] readForFalse() default {"false", "0", "no", "off", "f", "n"};
    
    /**
     * 書き込み時に{@literal true}の値を表現する値を指定します。
     * <p>`true`以外の`○`など他の値として出力したい場合に指定します。</p>
     * 
     * <p>使用可能なメッセージ中の変数は下記の通りです。</p>
     * <ul>
     *   <li>lineNumber : カラムの値に改行が含まれている場合を考慮した実際の行番号です。1から始まります。</li>
     *   <li>rowNumber : CSVの行番号です。1から始まります。</li>
     *   <li>columnNumber : CSVの列番号です。1から始まります。</li>
     *   <li>label : カラムの見出し名です。</li>
     *   <li>validatedValue : 実際のカラムの値です。</li>
     *   <li>ignoreCase : アノテーションの属性{@link #ignoreCase()}の値です。</li>
     *   <li>failToFalse : アノテーションの属性{@link #failToFalse()}の値です。</li>
     *   <li>trueValues : アノテーションの属性{@link #readForTrue()}を{@link LinkedHashSet}型にした値です。</li>
     *   <li>falseValues : アノテーションの属性{@link #readForFalse()}を{@link LinkedHashSet}型にした値です。</li>
     * </ul>
     * 
     * @return trueの値の代替として出力される値。
     */
    String writeAsTrue() default "true";
    
    /**
     * 書き込み時に{@literal false}の値を表現する値を指定します。
     * <p>`false`以外の`×`など他の値として出力したい場合に指定します。</p>
     * 
     * @return falseの値の代替として出力される値。
     */
    String writeAsFalse() default "false";
    
    /**
     * 読み込み時に、大文字・小文字を区別なく候補の値と比較して判定するか指定します。
     * @return trueの場合、大文字・小文字の区別は行いません。
     */
    boolean ignoreCase() default false;
    
    /**
     * 読み込み時に {@link #readForTrue()}、{@link #readForFalse()} で指定した候補の値と一致しない場合、値をfalseとして読み込み込むか指定します。
     * @return trueの場合、読み込み用の候補の値と一致しない場合、falseとして読み込みます。
     */
    boolean failToFalse() default false;
    
    /**
     * エラー時のメッセージを指定します。
     * <p>{@literal {key}}の書式の場合、プロパティファイルから取得した値を指定できます。</p>
     * <p>このメッセージは、文字列をブール型にパースする際に失敗したときに適用されるメッセージに使用します。</p>
     * @return 省略した場合は、適用された{@link CellProcessor}に基づいたメッセージが出力されます。
     */
    String message() default "{com.github.mygreen.supercsv.annotation.format.CsvBooleanFormat.message}";
    
}

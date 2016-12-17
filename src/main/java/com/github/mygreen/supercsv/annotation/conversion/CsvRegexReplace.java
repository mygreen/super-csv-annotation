package com.github.mygreen.supercsv.annotation.conversion;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.regex.Pattern;

import com.github.mygreen.supercsv.annotation.DefaultGroup;
import com.github.mygreen.supercsv.annotation.PatternFlag;
import com.github.mygreen.supercsv.builder.BuildCase;

/**
 * 正規表現に一致した文字列を置換するためのアノテーションです。
 * 
 * <h3 class="description">基本的な使い方</h3>
 * 
 * <ul>
 *   <li>属性 {@link #regex()}で、正規表現を指定します。
 *     <br>{@link Pattern}で解釈可能な値を指定する必要があります。。
 *   </li>
 *   <li>属性{@link #replacement()}で置換後の値を指定します。</li>
 *   <li>属性{@link #flags()}で、正規表現をコンパイルする際のフラグを列挙型{@link PatternFlag}で指定します。
 *     <br>複数指定すると、ビットOR演算した結果と同じ意味になります。
 *   </li>
 * </ul>
 * 
 * <pre class="highlight"><code class="java">
 * {@literal @CsvBean}
 * public class SampleCsv {
 *     
 *     // 日時の「2016/12/17」の形式を「2016-12-17」の形式に変換します。
 *     {@literal @CsvColumn(number=1)}
 *     {@literal @CsvRegexReplace(regex="([0-9]{4})/([0-9]{1,2})/([0-9]{1,2})", replacement="$1-$2-$3")}
 *     private LocalDate date;
 *     
 *     // コンパイル用のフラグを指定する場合
 *     {@literal @CsvColumn(number=2)}
 *     {@literal @CsvRegexReplace(regex="^comment(.+)", replacement='$1', flags={PatternFlag.CASE_INSENSITIVE})}
 *     private String comment;
 *     
 *     
 *     // getter/setterは省略
 * }
 * </code></pre>
 * 
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Repeatable(CsvRegexReplace.List.class)
@CsvConversion(value={})
public @interface CsvRegexReplace {
    
    /**
     * 正規表現の値を指定します。
     * <p>{@link Pattern}で解釈可能な値を指定してください。
     * @return 正規表現の値。
     */
    String regex();
    
    /**
     * 置換後の値を指定します。
     * <p>一致した場合に入れ替える値となります。
     * @return 置換後の値。
     */
    String replacement();
    
    /**
     * 正規表現をコンパイルする際のフラグを指定します。
     * <p>列挙型{@link PatternFlag}で指定します。</p>
     * @return 正規表現のコンパイルする際のフラグ。
     */
    PatternFlag[] flags() default {};
    
    /**
     * 適用するケースを指定します。
     * @return 何も指定しない場合は全てのケースに適用されます。
     */
    BuildCase[] cases() default {};
    
    /**
     * グループのクラスを指定します。
     * <p>処理ごとに適用するアノテーションを切り替えたい場合に指定します。
     * @return 指定しない場合は、{@link DefaultGroup}が適用され全ての処理に適用されます。
     */
    Class<?>[] groups() default {};
    
    /**
     * アノテーションの処理順序の定義。
     * @return 値が大きいほど後に実行されます。
     *         値が同じ場合は、アノテーションのクラス名の昇順になります。
     */
    int order() default 0;
    
    /**
     * アノテーションを複数個指定する際の要素です。
     */
    @Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface List {
        
        CsvRegexReplace[] value();
    }
    
}

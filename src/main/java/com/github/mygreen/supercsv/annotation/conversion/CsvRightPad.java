package com.github.mygreen.supercsv.annotation.conversion;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.github.mygreen.supercsv.annotation.DefaultGroup;
import com.github.mygreen.supercsv.builder.BuildCase;

/**
 * 右側にパディングするためのアノテーションです。
 * 
 * <h3 class="description">基本的な使い方</h3>
 * 
 * <ul>
 *   <li>属性 {@link #size()}で、パディングするサイズを指定します。
 *     <br>変換対象の文字長が属性{@link #size()}の値を超える場合、パディングは行いません。
 *   </li>
 *   <li>属性{@link #padChar()}でパディング文字を指定することができます。
 *     <br>デフォルトでは、半角空白がパディング文字です。
 *   </li>
 *   <li>アノテーション{@link CsvTrim}と併用する際には、処理結果が互いに変換対象となるため、
 *      属性{@link #cases()}や{@link #groups()}で適用するケースを分けるようにしてください。
 *   </li>
 * </ul>
 * 
 * <pre class="highlight"><code class="java">
 * {@literal @CsvBean}
 * public class SampleCsv {
 *     
 *     // 値が[10]の場合、結果は、[10&nbsp;&nbsp;&nbsp;]となります。
 *     {@literal @CsvColumn(number=1)}
 *     {@literal @CsvRightPad(size=5)}
 *     private Integer id;
 *     
 *     // パディング文字を変更する場合
 *     {@literal @CsvColumn(number=2)}
 *     {@literal @CsvRightPad(size=5, padChar='_')}
 *     private Integer number;
 *     
 *     // 書き込み時のみ適用する場合
 *     {@literal @CsvColumn(number=3)}
 *     {@literal @CsvTrim(cases=BuildCase.Read)}
 *     {@literal @CsvRightPad(size=20, cases=BuildCase.Write)}
 *     private String name;
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
@Repeatable(CsvRightPad.List.class)
@CsvConversion(value={})
public @interface CsvRightPad {
    
    /**
     * パディングするサイズを指定します。
     * <p>値は1以上を設定する必要があります。</p>
     * @return パディングのサイズ。
     */
    int size();
    
    /**
     * パディングする際の文字を指定します。
     * @return パディングの文字
     */
    char padChar() default ' ';
    
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
        
        CsvRightPad[] value();
    }
}

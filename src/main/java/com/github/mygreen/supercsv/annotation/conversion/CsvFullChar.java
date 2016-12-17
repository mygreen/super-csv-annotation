package com.github.mygreen.supercsv.annotation.conversion;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.github.mygreen.supercsv.annotation.DefaultGroup;
import com.github.mygreen.supercsv.builder.BuildCase;
import com.github.mygreen.supercsv.cellprocessor.conversion.CharCategory;

/**
 * 半角文字を日本語の全角文字に変換するためのアノテーションです。
 * 
 * <h3 class="description">基本的な使い方</h3>
 * 
 * <ul>
 *   <li>属性 {@link #categories()}で、変換対象の文字の種類を指定することができます。
 *      <ul>
 *        <li>指定しない場合は、全ての文字が変換対象となります。</li>
 *        <li>文字の種類は、「英字のアルファベット」「数値」「空白」「記号」「片仮名」があります。</li>
 *        <li>特に片仮名については、濁点・半濁点の場合、半角から全角に変換すると文字数が変わるので注意してください。</li>
 *      </ul>
 *   </li>
 *   <li>アノテーション{@link CsvHalfChar}と併用する際には、処理結果が互いに変換対象となるため、
 *      属性{@link #cases()}や{@link #groups()}で適用するケースを分けるようにしてください。
 *   </li>
 * </ul>
 * 
 * <pre class="highlight"><code class="java">
 * {@literal @CsvBean}
 * public class SampleCsv {
 *     
 *     {@literal @CsvColumn(number=1)}
 *     {@literal @CsvFullChar}
 *     private Integer number;
 *     
 *     // 文字種別を限定する場合
 *     {@literal @CsvColumn(number=2)}
 *     {@literal @CsvFullChar(categories={CharCategory.Alpha, CharCategory.Number})}
 *     private String name;
 *     
 *     // 書き込み時のみ適用する場合
 *     {@literal @CsvColumn(number=3)}
 *     {@literal @CsvFullChar(cases=BuildCase.Write)}
 *     private String comment;
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
@Repeatable(CsvFullChar.List.class)
@CsvConversion(value={})
public @interface CsvFullChar {
    
    /**
     * 変換対象の文字の種類を指定します。
     * @return 指定しない場合は、全ての文字が対象となります。
     */
    CharCategory[] categories() default {};
    
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
        
        CsvFullChar[] value();
    }
}

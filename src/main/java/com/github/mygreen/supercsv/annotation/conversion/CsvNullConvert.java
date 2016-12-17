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
 * 指定した値と一致したときにnullに変換するためのアノテーションです。
 * 
 * <h3 class="description">基本的な使い方</h3>
 * 
 * <ul>
 *   <li>属性{@link #value()}で、変換対象の値を指定します。
 *   <br>複数指定可能です。
 *   </li>
 *   <li>比較する際に、大文字・小文字の区別を付けたくない場合は、属性{@link #ignoreCase()}の値を{@literal true}にします。</li>
 *   <li>アノテーション{@link CsvDefaultValue}と併用する際には、処理結果が互いに変換対象となるため、
 *      属性{@link #cases()}や{@link #groups()}で適用するケースを分けるようにしてください。
 *   </li>
 * </ul>
 * 
 * <pre class="highlight"><code class="java">
 * {@literal @CsvBean}
 * public class SampleCsv {
 *     
 *     // 複数指定可能です
 *     {@literal @CsvColumn(number=1)}
 *     {@literal @CsvNullConvert({"-", "N/A"})}
 *     private Integer value;
 *     
 *     // 大文字・小文字の区別を行わない場合
 *     {@literal @CsvColumn(number=2)}
 *     {@literal @CsvNullConvert(value={"-", "N/A"}, ignoreCase=true)}
 *     private Integer salary;
 *     
 *     // 読み込み時のみ適用する場合
 *     {@literal @CsvColumn(number=3)}
 *     {@literal @CsvNullConvert(value="-", cases=BuildCase.Read)}
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
@Repeatable(CsvNullConvert.List.class)
@CsvConversion(value={})
public @interface CsvNullConvert {
    
    /**
     * 変換対象の値を指定します。
     * @return 変換対象となる値を指定します。
     * 
     */
    String[] value();
    
    /**
     * 値を比較する際に大文字・小文字の区別を行わないかどうか指定します。
     * @return trueの場合、大文字・小文字の区別を行いません。
     */
    boolean ignoreCase() default false;
    
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
    
    @Target({ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface List {
        
        CsvNullConvert[] value();
    }
    
}

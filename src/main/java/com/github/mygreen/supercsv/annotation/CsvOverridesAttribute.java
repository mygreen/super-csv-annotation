package com.github.mygreen.supercsv.annotation;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 合成のアノテーションを作成する際に、アノテーションの属性を特定して上書きするためのアノテーションです。
 * 
 * <h3 class="description">基本的な使い方</h3>
 * <ul>
 *   <li>属性{@link #annotation()}で上書き対象のアノテーションを指定します。</li>
 *   <li>属性{@link #name()}で上書き対象のアノテーションの属性名を指定します。</li>
 *   <li>上書きする属性のクラスタイプは合わせる必要があります。</li>
 *   <li>上書き対象のアノテーションが複数指定されている場合、属性{@link #index()}で指定します。
 *     <ul>
 *       <li>値0から始まります。</li>
 *       <li>インデックスを指定しない場合は、該当するアノテーションの属性が全て上書きされます。</li>
 *     </ul>
 *   </li>
 * </ul>
 * 
 * <pre class="highlight"><code class="java">
 * {@literal @Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})}
 * {@literal @Retention(RetentionPolicy.RUNTIME)}
 * {@literal @Documented}
 * {@literal @Repeatable(CsvCustomComposition.List.class)}
 * {@literal @CsvComposition}  // 合成のアノテーションであることを示すためのメタアノテーション
 * {@literal @CsvDefaultValue(value="0", cases=BuildCase.Read)}
 * {@literal @CsvRequire}
 * {@literal @CsvNumberRange(min="0", max="100,000,000", groups=NormalGroup.class)}
 * {@literal @CsvNumberRange(min="0", max="100,000,000,000", groups=ManagerGroup.class)} // 上書き対象のアノテーション
 * public {@literal @interface} CsvCustomComposition {
 *     
 *     // 2番目（インデックスが1）の{@literal @CsvNumberRange}の属性maxの上書き
 *     {@literal @CsvOverridesAttribute(annotation=CsvNumberRange.class, name="max", index=1)}
 *     String managerSalaryMax() default "100,000,000,000,000";
 *     
 *     // 繰り返しのアノテーションの格納用アノテーションの定義
 *     {@literal @Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})}
 *     {@literal @Retention(RetentionPolicy.RUNTIME)}
 *     {@literal @Documented}
 *     {@literal @interface} List {
 *     
 *         CsvCustomComposition[] value();
 *     }
 * }
 * </code></pre>
 * 
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Repeatable(CsvOverridesAttribute.List.class)
public @interface CsvOverridesAttribute {
    
    /**
     * 上書き対象のアノテーションのクラスタイプを指定します。
     * @return 上書き対象のアノテーションのクラスタイプ
     */
    Class<? extends Annotation> annotation();
    
    /**
     * 上書き対象のアノテーションの属性名を指定します。
     * <p>省略した場合、付与した属性名が採用されます。</p>
     * @return 上書き対象のアノテーションの属性名。
     */
    String name() default "";
    
    /**
     * 複数同じアノテーションが指定されている場合に、インデックス番号で特定するために指定します。
     * @return 指定する場合は0から始めます。
     */
    int index() default -1;
    
    /**
     * アノテーションを複数個指定する際の要素です。
     */
    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface List {
        
        CsvOverridesAttribute[] value();
    }
    
}

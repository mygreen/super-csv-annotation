package com.github.mygreen.supercsv.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * アノテーションを組み合わせた合成のアノテーションを表現するためのメタアノテーションです。
 * <p>合成したのアノテーションには必ず付与します。</p>
 * 
 * <h3 class="description">基本的な使い方</h3>
 * <ul>
 *  <li>{@literal @Target}として、{@link ElementType#FIELD}と{@link ElementType#ANNOTATION_TYPE}の2つを指定します。
 *     <br>アノテーションを合成する際にはアノテーションにも付与するため、{@link ElementType#ANNOTATION_TYPE}を追加しておきます。
 *  </li>
 *  <li>繰り返しのアノテーションとして利用できるよう {@literal @Repeatable}を付与します。
 *     <br>繰り返しのアノテーションを格納する内部アノテーションとして、{@literal List}を定義します。
 *  </li>
 *  <li>合成のアノテーションであることを示すためのメタアノテーション {@literal @CsvComposition}を指定します。</li>
 *  <li>アノテーションの属性を上書きする場合は、{@literal @}{@link CsvOverridesAttribute}を使用します。</li>
 *  <li>共通の属性 cases、groups、messageは、アノテーション{@literal @CsvOverridesAttribute}を付与していなくても一律で上書きされます。
 *  </li>
 * </ul>
 * 
 * <pre class="highlight"><code class="java">
 * {@literal @Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})}
 * {@literal @Retention(RetentionPolicy.RUNTIME)}
 * {@literal @Documented}
 * {@literal @Repeatable(CsvCustomComposition.List.class)}
 * {@literal @CsvComposition}  // 合成のアノテーションであることを示すためのメタアノテーション
 * {@literal @CsvDefaultValue(value="0", cases=BuildCase.Read)}  // 上書き対象のアノテーション
 * {@literal @CsvRequire}
 * {@literal @CsvNumberRange(min="0", max="100,000,000")}
 * public {@literal @interface} CsvCustomComposition {
 *     
 *     // {@literal @CsvDefaultValueの属性valueの上書き}
 *     {@literal @CsvOverridesAttribute(annotation=CsvDefaultValue.class, name="value")}
 *     String defaultValueRead();
 *     
 *     // 共通の属性 - グループ (groups属性を持つアノテーションは上書きされます)
 *     {@literal Class<?>[]} groups() default {};
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
@Target({ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CsvComposition {
    
}

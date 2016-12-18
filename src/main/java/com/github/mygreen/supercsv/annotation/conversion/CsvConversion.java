package com.github.mygreen.supercsv.annotation.conversion;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.github.mygreen.supercsv.builder.AbstractProcessorBuilder;
import com.github.mygreen.supercsv.cellprocessor.ConversionProcessorFactory;

/**
 * 変換のアノテーションを表現するためのメタアノテーションです。
 * <p>変換のアノテーションには必ず付与します。</p>
 * 
 * <h3 class="description">基本的な使い方</h3>
 * <ul>
 *  <li>{@literal @Target}として、{@link ElementType#FIELD}と{@link ElementType#ANNOTATION_TYPE}の2つを指定します。
 *     <br>アノテーションを合成する際にはアノテーションにも付与するため、{@link ElementType#ANNOTATION_TYPE}を追加しておきます。
 *  </li>
 *  <li>繰り返しのアノテーションとして利用できるよう {@literal @Repeatable}を付与します。
 *     <br>繰り返しのアノテーションを格納する内部アノテーションとして、{@literal List}を定義します。
 *  </li>
 *  <li>検証用のアノテーションであることを示すためのメタアノテーション {@literal @CsvContraint}を指定します。
 *    <br>属性{@link #value()}で、{@link ConversionProcessorFactory}の実装クラスを指定します。
 *  </li>
 *  <li>共通の属性として、{@literal cases, groups, order}を定義します。
 *    <br>省略した場合は、それぞれのデフォルト値が適用されます。
 *  </li>
 *  <li>必要であれば、固有の属性を定義します。</li>
 * </ul>
 * 
 * <pre class="highlight"><code class="java">
 * // 独自の値の検証用のアノテーション
 * {@literal @Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})}
 * {@literal @Retention(RetentionPolicy.RUNTIME)}
 * {@literal @Documented}
 * {@literal @Repeatable(CsvCustomConversion.List.class)}
 * {@literal @CsvConstraint(CustomConversionFactory.class)}  // ファクトリクラスを指定
 * public {@literal @interface} CsvCustomConversion {
 *     
 *     // 固有の属性 - 必要であれば定義します。
 *     String text();
 *     
 *     // 共通の属性 - ケース
 *     BuildCase[] cases() default {};
 *    
 *     // 共通の属性 - グループ
 *     {@literal Class<?>[]} groups() default {};
 *     
 *     // 共通の属性 - 並び順
 *     int order() default 0;
 *     
 *     // 繰り返しのアノテーションの格納用アノテーションの定義
 *     {@literal @Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})}
 *     {@literal @Retention(RetentionPolicy.RUNTIME)}
 *     {@literal @Documented}
 *     {@literal @interface} List {
 *     
 *         CsvCustomConversion[] value();
 *     }
 * }
 * </code></pre>
 * 
 * 
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
@Target({ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CsvConversion {
    
    /**
     * アノテーションに対応したCellProcessorを作成するファクトリクラスを指定します。
     * <p>省略した場合、{@link AbstractProcessorBuilder#registerForConversion(Class, ConversionProcessorFactory)}で手動で登録する必要があります。</p>
     * @return {@link ConversionProcessorFactory}を実装したクラスを指定します。
     */
    Class<? extends ConversionProcessorFactory<?>>[] value();
    
}

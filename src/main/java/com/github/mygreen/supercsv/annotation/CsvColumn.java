package com.github.mygreen.supercsv.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.supercsv.cellprocessor.ift.CellProcessor;

import com.github.mygreen.supercsv.builder.ProcessorBuilder;


/**
 * CSVのカラムであることを表現するためのアノテーションです。
 * <p>フィールドに付与します。</p>
 * 
 * <h3 class="description">基本的な使い方</h3>
 * <ul>
 *   <li>属性{@link #number()}でカラムの番号を指定します。
 *     <br>値は1から始まります。
 *   </li>
 *   <li>ヘッダー行が存在する場合、属性{@link #label()}で見出しの値を指定します。
 *     <br>省略した場合、フィールド名が適用されます。
 *   </li>
 * </ul>
 * 
 * <pre class="highlight"><code class="java">
 * {@literal @CsvBean}
 * public class SampleCsv {
 *     
 *     {@literal @CsvColumn(number=1)}
 *     private int no;
 *     
 *     {@literal @CsvColumn(number=2, label="名前")}
 *     private String name;
 *     
 *     // getter/setterは省略
 * }
 * </code></pre>
 * 
 * @version 2.1
 * @author T.TSUCHIE
 *
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CsvColumn {
    
    /**
     * 列番号を指定します。
     * <p>他のカラムの値との重複は許可しません。</p>
     * @return 番号は1から始まります。
     */
    int number() default 0;
    
    /**
     * 見出しとなるラベルを指定します。
     * @return 指定しない場合、フィールド名が適用されます。
     */
    String label() default "";
    
    /**
     * 独自の{@link ProcessorBuilder}を指定して{@link CellProcessor} を組み立てたい場合に指定します。
     * <p>サポートしていないクラスタイプに対応するときなどに指定します。</p>
     * @return {@link ProcessorBuilder}を実装したクラスを指定します。
     */
    @SuppressWarnings("rawtypes")
    Class<? extends ProcessorBuilder>[] builder() default {};
    
}

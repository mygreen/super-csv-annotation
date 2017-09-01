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
 * 片方だけトリムするためのアノテーションです。
 * 
 * <h3 class="description">基本的な使い方</h3>
 * 
 * <ul>
 *   <li>処理対象の値がnullの場合は、変換は行いません。</li>
 *   <li>文字列以外の数値型に対して付与した場合、読み込み時にトリミングの結果として空文字となった場合は、
 *       値が空として処理されます。
 *       <br>例えば、Integer型にマッピングする際にはnullとなります。</li>
 * </ul>
 * 
 * <pre class="highlight"><code class="java">
 * {@literal @CsvBean}
 * public class SampleCsv {
 *     
 *     // デフォルトは、右側の半角文字をトリムします。
 *     {@literal @CsvColumn(number=1)}
 *     {@literal @CsvOneSideTrim}
 *     private Integer value;
 *     
 *     // 左側の全角空白をトリムする場合。
 *     {@literal @CsvColumn(number=2)}
 *     {@literal @CsvOneSideTrim(trimChar='　', leftAlign=true)}
 *     private String comment;
 *     
 *     // getter/setterは省略
 * }
 * </code></pre>
 * 
 * @since 2.1
 * @author T.TSUCHIE
 *
 */
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Repeatable(CsvOneSideTrim.List.class)
@CsvConversion(value={})
public @interface CsvOneSideTrim {
    
    /**
     * トリミング対象の文字を指定します。
     *
     * @return トリミング対象の文字。
     */
    char trimChar() default ' ';
    
    /**
     * 左側をトリムするかどうか指定します。
     *
     * @return falseの場合は右側をトリムします。
     */
    boolean leftAlign() default false;
    
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
    
    @Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface List {
        
        CsvOneSideTrim[] value();
    }
}

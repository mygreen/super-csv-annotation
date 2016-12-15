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
 * 値がnullの場合に、代替となる値に変換するためのアノテーションです。
 * 
 * <h3 class="description">基本的な使い方</h3>
 * 
 * <ul>
 *   <li>属性 {@link #value()}で、変換する値を指定します。</li>
 *   <li>読み込むときには、それぞれのタイプの書式に沿った形式を指定する必要があります。
 *      <br>もし、間違った書式で定義した場合は、CSVファイルの値が不正なときと同様にエラーとなります。
 *   </li>
 *   <li>アノテーション{@link CsvNullConvert}と併用する際には、処理結果が互いに変換対象となるため、
 *      属性{@link #cases()}や{@link #groups()}で適用するケースを分けるようにしてください。
 *   </li>
 * </ul>
 * 
 * <pre class="highlight"><code class="java">
 * {@literal @CsvBean}
 * public class SampleCsv {
 *     
 *     // クラスタイプに沿った値を指定します。
 *     {@literal @CsvColumn(number=1)}
 *     {@literal @CsvDefalutValue("0")}
 *     private Integer id;
 *     
 *     // 書式が指定されている場合は、書式に沿った値を指定します。
 *     {@literal @CsvColumn(number=2)}
 *     {@literal @CsvNumberFomat(pattern="#,##0")}
 *     {@literal @CsvDefalutValue("1,000")}
 *     private Integer salary;
 *     
 *     // 書き込み時のみ適用する場合
 *     {@literal @CsvColumn(number=3)}
 *     {@literal @CsvDefalutValue(value="-", cases=BuildCase.Write)}
 *     private String comment;
 *     
 *     // getter/setterは省略
 * }
 * </code></pre>
 * 
 * 
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Repeatable(CsvDefaultValue.List.class)
@CsvConversion(value={})
public @interface CsvDefaultValue {
    
    /**
     * nullの代替となる値を指定します。
     * @return ブール型、数値や日時型の場合は、アノテーションで指定した書式に沿った値を指定する必要があります。
     * 
     */
    String value();
    
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
        
        CsvDefaultValue[] value();
    }
    
}

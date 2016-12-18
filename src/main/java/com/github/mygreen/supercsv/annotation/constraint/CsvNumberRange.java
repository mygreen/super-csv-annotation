package com.github.mygreen.supercsv.annotation.constraint;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.supercsv.cellprocessor.ift.CellProcessor;

import com.github.mygreen.supercsv.annotation.DefaultGroup;
import com.github.mygreen.supercsv.builder.BuildCase;
import com.github.mygreen.supercsv.cellprocessor.format.TextPrinter;

/**
 * 値が指定した範囲内かどうか検証するためのアノテーションです。
 * <p>数値型に指定可能です。</p>
 * 
 * <h3 class="description">基本的な使い方</h3>
 * 
 * <ul>
 *   <li>属性{@link #min()}で、下限値を指定します。
 *     <br>値は、クラスタイプや指定した書式に沿った形式で指定する必要があります。
 *   </li>
 *   <li>属性{@link #max()}で、上限値を指定します。
 *     <br>値は、クラスタイプや指定した書式に沿った形式で指定する必要があります。
 *   </li>
 *   <li>属性{@link #inclusive()}で、値の検証時に属性{@link #min()}と{@link #min()}の値を含むかどうか指定します。
 *     <br>初期値はtrueで、指定した値を含みます。</li>
 * </ul>
 * 
 * <pre class="highlight"><code class="java">
 * {@literal @CsvBean}
 * public class SampleCsv {
 *     
 *     {@literal @CsvColumn(number=1)}
 *     {@literal @CsvNumberRange(min="0", max="1000")}
 *     private long id;
 *     
 *     // 値は書式に沿った形式で入力します。
 *     {@literal @CsvColumn(number=2)}
 *     {@literal @CsvNumberFormat(pattern="#,##0")}
 *     {@literal @CsvNumberRange(min="1,000", max="1,000,000,000")}
 *     private Integer salary;
 *     
 *     // 比較する際には指定した値の範囲内（境界値を含まない）どうかで判定します。
 *     {@literal @CsvColumn(number=3)}
 *     {@literal @CsvNumberRange(min="0.0", max="100.0", inclusive=false)}
 *     private Double rate;
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
@Repeatable(CsvNumberRange.List.class)
@CsvConstraint(value={})
public @interface CsvNumberRange {
    
    /**
     * 下限値を指定します。
     * @return 値は、クラスタイプやアノテーションで指定した書式に沿った値を指定する必要があります。
     */
    String min();
    
    /**
     * 上限値を指定します。
     * @return 値は、クラスタイプやアノテーションで指定した書式に沿った値を指定する必要があります。
     */
    String max();
    
    /**
     * 値を比較する際に指定した値を含むかどうかを指定します。
     * @return {@code true}の場合、値以下かどうかとして比較します。
     *         {@code false}の場合、値より小さいかどうかとして比較します。
     */
    boolean inclusive() default true;
    
    /**
     * エラー時のメッセージを指定します。
     * <p>{@literal {key}}の書式の場合、プロパティファイルから取得した値を指定できます。</p>
     * 
     * <p>使用可能なメッセージ中の変数は下記の通りです。</p>
     * <ul>
     *   <li>lineNumber : カラムの値に改行が含まれている場合を考慮した実際の行番号です。1から始まります。</li>
     *   <li>rowNumber : CSVの行番号です。1から始まります。</li>
     *   <li>columnNumber : CSVの列番号です。1から始まります。</li>
     *   <li>label : カラムの見出し名です。</li>
     *   <li>validatedValue : 実際のカラムの値です。</li>
     *   <li>min : アノテーションの属性{@link #min()}をフィールドの型に変換した値です。</li>
     *   <li>max : アノテーションの属性{@link #max()}をフィールドの型に変換した値です。</li>
     *   <li>inclusive : アノテーションの属性{@link #inclusive()}の値です。</li>
     *   <li>printer : カラムの値に対数するフォーマッタです。{@link TextPrinter#print(Object)}でvalidatedValue, min, maxの値を文字列に変換します。</li>
     * </ul>
     * 
     * @return 省略した場合は、適用された{@link CellProcessor}に基づいたメッセージが出力されます。
     */
    String message() default "{com.github.mygreen.supercsv.annotation.constraint.CsvNumberRange.message}";
    
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
        
        CsvNumberRange[] value();
    }
}

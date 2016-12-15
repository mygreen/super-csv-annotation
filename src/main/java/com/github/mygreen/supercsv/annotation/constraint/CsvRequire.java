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

/**
 * 値が必須かどうか検証するためのアノテーションです。
 * <p>全ての型に指定可能です。</p>
 * 
 * <h3 class="description">基本的な使い方</h3>
 * <ul>
 *   <li>オブジェクト型の場合、nullでないかどうか検証します。
 *     <br>プリミティブ型のように必ず初期値が存在する場合は、エラーとなりません。
 *   </li>
 *   <li>文字列型の場合、属性{@link #considerEmpty()}、{@link #considerBlank()}を指定することで、
 *     空文字、空白文字かどうか判定することができます。
 *     <ul>
 *       <li>属性{@link #considerEmpty()}で、長さが0の空文字も考慮するか指定します。
 *         <br>trueの場合、空文字でもエラーとなります。</li>
 *       <li>属性{@link #considerBlank()}で、半角スペースのみの空白文字も考慮するか指定します。
 *         <br>trueの場合、空白文字でもエラーとなります。
 *       </li>
 *     </ul>
 *   </li>
 * </ul>
 * 
 * 
 * <pre class="highlight"><code class="java">
 * {@literal @CsvBean}
 * public class SampleCsv {
 *     
 *     {@literal @CsvColumn(number=1)}
 *     {@literal @CsvRequire}
 *     private Integer id;
 *     
 *     {@literal @CsvColumn(number=2)}
 *     {@literal @CsvRequire(considerBlank=true)}
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
@Repeatable(CsvRequire.List.class)
@CsvConstraint(value={})
public @interface CsvRequire {
    
    /**
     * 空文字（長さが0）の時も考慮するかどうか指定します。
     * @return trueに設定すると、値がnullまたは空文字のときもエラーとなります。
     */
    boolean considerEmpty() default false;
    
    /**
     * 空白文字の時も考慮するかどうかを指定します。
     * @return trueに設定すると、値がnullまたは空文字、空白文字のときもエラーとなります。
     */
    boolean considerBlank() default false;
    
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
     *   <li>considerEmpty : アノテーションの属性{@link #considerEmpty()}の値</li>
     *   <li>considerBlank : アノテーションの属性{@link #considerBlank()}の値</li>
     * </ul>
     * 
     * @return 省略した場合は、適用された{@link CellProcessor}に基づいたメッセージが出力されます。
     */
    String message() default "{com.github.mygreen.supercsv.annotation.constraint.CsvRequire.message}";
    
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
    int order() default Integer.MIN_VALUE;
    
    /**
     * アノテーションを複数個指定する際の要素です。
     */
    @Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface List {
        
        CsvRequire[] value();
    }
    
}

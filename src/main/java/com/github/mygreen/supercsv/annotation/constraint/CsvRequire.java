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
 * カラムの値を必須に指定します。
 * <p>全ての型に指定可能です。</p>
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

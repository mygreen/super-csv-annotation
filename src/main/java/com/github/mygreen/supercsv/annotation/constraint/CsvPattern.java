package com.github.mygreen.supercsv.annotation.constraint;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.regex.Pattern;

import org.supercsv.cellprocessor.ift.CellProcessor;

import com.github.mygreen.supercsv.annotation.DefaultGroup;
import com.github.mygreen.supercsv.annotation.PatternFlag;
import com.github.mygreen.supercsv.builder.BuildCase;

/**
 * カラムの値が指定した正規表現に一致するかどうかチェックします。
 * <p>文字列型に指定可能です。</p>
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Repeatable(CsvPattern.List.class)
@CsvConstraint(value={})
public @interface CsvPattern {
    
    /**
     * 正規表現の値。
     * @return {@link Pattern}で解釈可能な値。
     */
    String regex();
    
    /**
     * 正規表現をコンパイルする際のフラグを指定します。
     * @return 列挙型{@link PatternFlag}で指定します。
     */
    PatternFlag[] flags() default {};
    
    /**
     * 正規表現の説明。
     * @return エラー時のメッセージ中の変数として使用可能。
     */
    String description() default "";
    
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
     *   <li>regex : アノテーションの属性{@link #regex()}の値です。</li>
     *   <li>flags : アノテーションの属性{@link #flags()}の値をint型に変換した値です。</li>
     *   <li>description : アノテーションの属性{@link #description()}の値です。</li>
     * </ul>
     * 
     * 
     * @return 省略した場合は、適用された{@link CellProcessor}に基づいたメッセージが出力されます。
     */
    String message() default "{com.github.mygreen.supercsv.annotation.constraint.CsvPattern.message}";
    
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
        
        CsvPattern[] value();
    }
}

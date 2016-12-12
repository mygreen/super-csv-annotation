package com.github.mygreen.supercsv.annotation.conversion;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.regex.Pattern;

import com.github.mygreen.supercsv.annotation.DefaultGroup;
import com.github.mygreen.supercsv.annotation.PatternFlag;
import com.github.mygreen.supercsv.builder.BuildCase;

/**
 * 正規表現に一致した文字列を置換します。
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Repeatable(CsvRegexReplace.List.class)
@CsvConversion(value={})
public @interface CsvRegexReplace {
    
    /**
     * 適用するケースを指定します。
     * @return 何も指定しない場合は全てのケースに適用されます。
     */
    BuildCase[] cases() default {};
    
    /**
     * 正規表現の値。
     * @return {@link Pattern}で解釈可能な値。
     */
    String regex();
    
    /**
     * 置換後の値。
     * @return 一致した場合に入れ替える値。
     */
    String replacement();
    
    /**
     * 正規表現をコンパイルする際のフラグを指定します。
     * @return 列挙型{@link PatternFlag}で指定します。
     */
    PatternFlag[] flags() default {};
    
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
        
        CsvRegexReplace[] value();
    }
    
}

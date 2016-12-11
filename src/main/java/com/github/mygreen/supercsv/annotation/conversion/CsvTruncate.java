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
 * 指定した文字長よりも長い場合、先頭から最大文字長分切り取り、さらに、接尾語を追加します。
 * <p>例えば、入力が「あいうえお」の場合、最大文字長を3、接尾語を「...」と指定したとき、「あいう...」という値となります。</p>
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Repeatable(CsvTruncate.List.class)
@CsvConversion(value={})
public @interface CsvTruncate {
    
    /**
     * 最大文字長を指定します。
     * 
     * @return 1以上の値を指定します。
     */
    int maxSize();
    
    /**
     * 接尾語を指定します。
     * 
     * @return 省略可能です。
     */
    String suffix() default "";
    
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
        
        CsvTruncate[] value();
    }
    
}

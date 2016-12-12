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
 * 指定した値と一致したときにnullに変換します。
 * 
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Repeatable(CsvNullConvert.List.class)
@CsvConversion(value={})
public @interface CsvNullConvert {
    
    /**
     * 変換対象の値を指定します。
     * @return 変換対象となる値を指定します。
     * 
     */
    String[] value();
    
    /**
     * 値を比較する際に大文字・小文字の区別を行わないかどうか。
     * @return trueの場合、大文字・小文字の区別を行いません。
     */
    boolean ignoreCase() default false;
    
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
    
    @Target({ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface List {
        
        CsvNullConvert[] value();
    }
    
}

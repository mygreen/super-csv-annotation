package com.github.mygreen.supercsv.annotation;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 合成のアノテーションを作成する場合、アノテーションの属性を特定して上書きするために使用するアノテーションです。
 * 
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Repeatable(CsvOverridesAttribute.List.class)
public @interface CsvOverridesAttribute {
    
    /**
     * 上書き対象のアノテーションのクラスタイプを指定します。
     * @return 上書き対象のアノテーションのクラスタイプ
     */
    Class<? extends Annotation> annotation();
    
    /**
     * 上書き対象のアノテーションの属性名を指定します。
     * <p>省略した場合、付与した属性名が採用されます。</p>
     * @return 上書き対象のアノテーションの属性名。
     */
    String name() default "";
    
    /**
     * 繰り返しのアノテーションが指定されている場合、番号で指定します。
     * @return 指定する場合は0から始めます。
     */
    int index() default -1;
    
    /**
     * アノテーションを複数個指定する際の要素です。
     */
    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface List {
        
        CsvOverridesAttribute[] value();
    }
    
}

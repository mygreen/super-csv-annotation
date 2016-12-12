package com.github.mygreen.supercsv.builder.spring;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * BeanValidationの制約のアノテーション
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Repeatable(UserMailPattern.List.class)
@Constraint(validatedBy=UserMailPatternValidator.class)
public @interface UserMailPattern {
    
    // 共通の属性の定義
    Class<?>[] groups() default {};
    String message() default "{com.github.mygreen.supercsv.builder.spring.UserMailPattern.message}";
    Class<? extends Payload>[] payload() default {};
    
    // 複数のアノテーションを指定する場合の定義
    @Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface List {
        UserMailPattern[] value();
    }
    
}

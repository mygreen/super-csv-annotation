package com.github.mygreen.supercsv.annotation.constraint;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.github.mygreen.supercsv.cellprocessor.ConstraintProcessorFactory;

/**
 * 制約のアノテーションを表現するためのメタアノテーションです。
 * <p>独自にアノテーションを作成する際に利用します。</p>
 * 
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
@Target({ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CsvConstraint {
    
    /**
     * アノテーションに対応するCellProcessorを作成するクラスを指定します。
     * @return {@link ConstraintProcessorFactory}を実装したクラスを指定します。
     */
    Class<? extends ConstraintProcessorFactory<?>>[] value();
    
}

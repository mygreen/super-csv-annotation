package com.github.mygreen.supercsv.annotation.conversion;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.github.mygreen.supercsv.cellprocessor.ConversionProcessorFactory;

/**
 * 変換のアノテーションを表現するためのメタアノテーションです。
 * <p>独自にアノテーションを作成する際に利用します。</p>
 * 
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
@Target({ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CsvConversion {
    
    /**
     * アノテーションに対応するCellProcessorを作成するクラスを指定します。
     * @return {@link ConversionProcessorFactory}を実装したクラスを指定します。
     */
    Class<? extends ConversionProcessorFactory<?>>[] value();
    
}

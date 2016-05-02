package org.supercsv.ext.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * CSVをマッピングするクラスに付与するアノテーション。
 * 
 * @author T.TSUCHIE
 *
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CsvBean {
    
    /**
     * ヘッダー行を持つかどうか指定する。
     * <p>
     * @return true ヘッダー行を持つ。
     */
    boolean header() default false;
    
}

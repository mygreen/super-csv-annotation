package org.supercsv.ext.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Annotation for CSV "Bean".
 * 
 * @version 1.2
 * @author T.TSUCHIE
 *
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CsvBean {
    
    /**
     * has CSV Header column.
     * @return
     */
    boolean header() default false;
    
}

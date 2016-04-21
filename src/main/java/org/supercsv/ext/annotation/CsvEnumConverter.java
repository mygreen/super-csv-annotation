package org.supercsv.ext.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Enum formmating annotation.
 *
 * @version 1.2
 * @author T.TSUCHIE
 *
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CsvEnumConverter {
    
    /**
     * ignore lower/upper case.
     * @return
     */
    boolean ignoreCase() default false;
    
    /**
     * your customize method name. ex. Color.label()
     */
    String valueMethodName() default "";
}

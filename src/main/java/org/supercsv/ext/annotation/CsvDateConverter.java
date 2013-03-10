/*
 * CsvDateConverter.java
 * created in 2013/03/05
 *
 * (C) Copyright 2003-2013 GreenDay Project. All rights reserved.
 */
package org.supercsv.ext.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Locale;


/**
 * Date formmating annotation.
 *
 * @author T.TSUCHIE
 *
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CsvDateConverter {
    
    /**
     * format pattern
     * <p>default 'yyyy-MM-dd HH:mm:ss'
     * @return
     */
    String pattern() default "yyyy-MM-dd HH:mm:ss";
    
    /**
     * parsing date, lenient.
     * <p>default : false
     * @return
     */
    boolean lenient() default true;
    
    /**
     * timezone id
     * @return
     */
    String timezone() default "";
    
    /**
     * formatting {@link Locale} for laugage code.
     * <p>example. ja, en
     * @return
     */
    String language() default "";
    
    /**
     * formatting {@link Locale} for country code.
     * <p>example. JP, US
     * @return
     */
    String country() default "";
    
    /**
     * start date 
     * @return
     */
    String min() default "";
    
    /**
     * end date 
     * @return
     */
    String max() default "";
}

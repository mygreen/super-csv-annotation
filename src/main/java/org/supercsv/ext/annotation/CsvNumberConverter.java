/*
 * CsvNumberConverter.java
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
import java.util.Currency;
import java.util.Locale;


/**
 * Number formmating annotation.
 *
 * @author T.TSUCHIE
 *
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CsvNumberConverter {
    
    /**
     * format pattern
     * <p> set for CellProcessor 'FormatLocaleNumber'
     * @return
     */
    String pattern() default "";
    
    /**
     * number formatting lenient.
     * <p>false : format exactly
     * @return
     */
    boolean lenient() default false;
    
    /**
     * formatting {@link Currency} Code(ISO 4217 Code)
     * @return
     */
    String currency() default "";
    
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
     * <p>set for CellProcessro 'Min'
     * @return
     */
    String min() default "";
    
    /**
     * 
     * <p>set for CellProcessro 'Max'
     * @return
     */
    String max() default "";
    
}

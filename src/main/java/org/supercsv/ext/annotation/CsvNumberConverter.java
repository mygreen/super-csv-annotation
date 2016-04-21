package org.supercsv.ext.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Locale;


/**
 * Number formmating annotation.
 * 
 * @version 1.2
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
     * formatting {@link Currency} Code(<a href="https://ja.wikipedia.org/wiki/ISO_4217" target="_blank">ISO 4217 Code</a>)
     * 
     * @return
     */
    String currency() default "";
    
    /**
     * formatting {@link Locale}.
     * ex. 'ja', 'ja_JP'
     * @since 1.2
     * @return
     */
    String locale() default "";
    
    /**
     * Rounding mode
     * @since 1.2
     * @return
     */
    RoundingMode roundingMode() default RoundingMode.HALF_EVEN;
    
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

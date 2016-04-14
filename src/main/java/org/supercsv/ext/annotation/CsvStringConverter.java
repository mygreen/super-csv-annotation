package org.supercsv.ext.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * String formmating annotation.
 *
 * @author T.TSUCHIE
 *
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CsvStringConverter {
    
    int minLength() default -1;
    
    int maxLength() default -1;
    
    /**
     * one or more required lengths
     * <p>set CellProcessor for 'StrLen'
     * @return
     */
    int[] exactLength() default {};
    
    /**
     * regular expression pattern.
     * <p>set CellProcessor for 'StrRegEx'
     * @return
     */
    String regex() default "";
    
    /**
     * forbidden string.
     * <p>set CellProcessor for 'ForbidSubStr'
     * @return
     */
    String[] forbid() default {};
    
    /**
     * contain string.
     * <p>set CellProcessor for 'RequireSubStr' 
     * @return
     */
    String[] contain() default {};
    
    /**
     * not empty
     * <p>set CellProcessor for 'StrNotNullOrEmpty'
     * @return
     */
    boolean notEmpty() default false;
    
}

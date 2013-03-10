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


/**
 * Boolen formmating annotation.
 *
 * @author T.TSUCHIE
 *
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CsvBooleanConverter {
    
    /**
     * candidate string with pare string as true.
     * <p>set CellProcessor for 'ParseBool'
     * @return 
     */
    String[] inputTrueValue() default {"true", "1", "yes", "on"};
    
    /**
     * candidate string with pare string as true.
     * <p>set CellProcessor for 'ParseBool'
     * @return
     */
    String[] inputFalseValue() default {"false", "0", "no", "off"};
    
    /**
     * candidate string with format 'true' as string.
     * <p>set CellProcessor for 'FmtBool'
     * @return
     */
    String outputTrueValue() default "true";
    
    /**
     * candidate string with format 'true' as string.
     * <p>set CellProcessor for 'FmtBool'
     * @return
     */
    String outputFalseValue() default "false";
}

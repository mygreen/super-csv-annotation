/*
 * CsvStringConverter.java
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
 * String formmating annotation.
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
     * <p>default = false
     * @return
     */
    boolean ignoreCase() default false;
    
    /**
     * your customze method name. ex. Color.label()
     */
    String valueMethodName() default "";
}

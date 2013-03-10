/*
 * CsvBean.java
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
 * Annotation for CSV "Bean".
 * 
 * @version 01-00
 * @since 01-00
 * @author T.TSUCHIE
 *
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CsvBean {
    
    /**
     * has CSV Header column
     * @return
     */
    boolean header() default false;
    
}

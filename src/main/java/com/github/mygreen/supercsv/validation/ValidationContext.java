package com.github.mygreen.supercsv.validation;

import org.supercsv.util.CsvContext;

import com.github.mygreen.supercsv.builder.BeanMapping;

/**
 * 入力値検証する際のContext.
 * 
 * @param <T> Beanのタイプ。
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class ValidationContext<T> {

    private final CsvContext csvContext;
    
    private final BeanMapping<T> beanMapping;
    
    public ValidationContext(final CsvContext csvContext, final BeanMapping<T> beanMapping) {
        this.csvContext = csvContext;
        this.beanMapping = beanMapping;
    }
    
    public CsvContext getCsvContext() {
        return csvContext;
    }
    
    public BeanMapping<T> getBeanMapping() {
        return beanMapping;
    }
    
}

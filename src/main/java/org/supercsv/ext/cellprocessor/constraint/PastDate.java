/*
 * PastDate.java
 * created in 2012/09/22
 *
 * (C) Copyright 2003-2012 GreenDay Project. All rights reserved.
 */
package org.supercsv.ext.cellprocessor.constraint;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.ift.DateCellProcessor;
import org.supercsv.exception.SuperCsvConstraintViolationException;
import org.supercsv.ext.cellprocessor.ift.ValidationCellProcessor;
import org.supercsv.util.CsvContext;


/**
 * 
 * @see {@link Max}
 * @author T.TSUCHIE
 *
 */
public class PastDate<T extends Date> extends CellProcessorAdaptor implements DateCellProcessor, ValidationCellProcessor {
    
    protected final T max;
    
    public PastDate(final T max) {
        super();
        checkPreconditions(max);
        this.max = max;
    }
    
    public PastDate(T max, final CellProcessor next) {
        super(next);
        checkPreconditions(max);
        this.max = max;
    }
    
    protected static <T extends Date> void checkPreconditions(final T max) {
        if(max == null) {
            throw new IllegalArgumentException("max should not be null");
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object execute(Object value, CsvContext context) {
        
        validateInputNotNull(value, context);
        
        if(!(value instanceof Comparable)) {
            throw new SuperCsvConstraintViolationException(String.format(
                    "the value '%s' could not implement Comparable interface.",
                    value), context, this);
        }
        
        final T result = ((T) value);
        
        if(result.compareTo(max) < 0) {
            throw new SuperCsvConstraintViolationException(
                    String.format("%s does not lie the max (%s) values (inclusive)", result, max),
                    context, this);
        }
            
        return next.execute(result, context);
    }
    
    public T getMax() {
        return max;
    }
    
    @Override
    public String getMessageCode() {
        return PastDate.class.getCanonicalName() + ".violated";
    }
    
    @Override
    public Map<String, ?> getMessageVariable() {
        Map<String, Object> vars = new HashMap<String, Object>();
        vars.put("max", getMax());
        return vars;
    }
    
    @Override
    public String formateValue(Object value) {
        if(value == null) {
            return "";
        }
        return value.toString();
    }
    
}

/*
 * FutureDate.java
 * created in 2012/09/22
 *
 * (C) Copyright 2003-2012 GreenDay Project. All rights reserved.
 */
package org.supercsv.ext.cellprocessor.constraint;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.ift.DateCellProcessor;
import org.supercsv.exception.SuperCsvConstraintViolationException;
import org.supercsv.ext.cellprocessor.DateFormatWrapper;
import org.supercsv.ext.cellprocessor.ift.ValidationCellProcessor;
import org.supercsv.util.CsvContext;


/**
 * 
 * @see {@link Min}
 * @author T.TSUCHIE
 *
 */
public class FutureDate<T extends Date> extends CellProcessorAdaptor
        implements DateCellProcessor, ValidationCellProcessor {
    
    protected final T min;
    
    protected DateFormat formatter;
    
    public FutureDate(final T min) {
        super();
        checkPreconditions(min);
        this.min = min;
    }
    
    public FutureDate(final T min, final CellProcessor next) {
        super(next);
        checkPreconditions(min);
        this.min = min;
    }
    
    protected static <T extends Date> void checkPreconditions(final T min) {
        if(min == null) {
            throw new NullPointerException("min should not be null");
        }
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public Object execute(final Object value, final CsvContext context) {
        
        validateInputNotNull(value, context);
        
        if(!Date.class.isAssignableFrom(value.getClass())) {
            throw new SuperCsvConstraintViolationException(String.format(
                    "the value '%s' could not implement '%s' class.", value, Date.class.getCanonicalName()),
                    context, this);
        }
        
        final T result = ((T) value);
        
        if(result.compareTo(min) < 0) {
            throw new SuperCsvConstraintViolationException(
                    String.format("%s does not lie the min (%s) values (inclusive)", result, min),
                    context, this);
        }
            
        return next.execute(result, context);
    }
    
    public T getMin() {
        return min;
    }
    
    public DateFormat getFormatter() {
        return formatter;
    }
    
    public FutureDate<T> setFormatter(DateFormat formatter) {
        this.formatter = formatter;
        return this;
    }
    
    @Override
    public String getMessageCode() {
        return FutureDate.class.getCanonicalName() + ".violated";
    }
    
    @Override
    public Map<String, ?> getMessageVariable() {
        final Map<String, Object> vars = new HashMap<String, Object>();
        vars.put("min", getMin());
        return vars;
    }
    
    @Override
    public String formatValue(final Object value) {
        if(value == null) {
            return "";
        }
        
        if(value instanceof Date) {
            final Date date = (Date) value;
            final DateFormatWrapper df;
            if(getFormatter() != null) {
                df = new DateFormatWrapper(getFormatter());
            } else {
                df = new DateFormatWrapper(date.getClass());
            }
            
            return df.format(date);
            
        }
        
        return value.toString();
    }
    
}

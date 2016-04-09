/*
 * Max.java
 * created in 2012/09/22
 *
 * (C) Copyright 2003-2012 GreenDay Project. All rights reserved.
 */
package org.supercsv.ext.cellprocessor.constraint;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.ift.DoubleCellProcessor;
import org.supercsv.cellprocessor.ift.LongCellProcessor;
import org.supercsv.exception.SuperCsvCellProcessorException;
import org.supercsv.exception.SuperCsvConstraintViolationException;
import org.supercsv.ext.cellprocessor.ift.ValidationCellProcessor;
import org.supercsv.ext.util.NumberFormatWrapper;
import org.supercsv.util.CsvContext;


/**
 * @author T.TSUCHIE
 *
 */
public class Max<T extends Number & Comparable<T>> extends CellProcessorAdaptor
        implements LongCellProcessor, DoubleCellProcessor, ValidationCellProcessor {
    
    protected final T max;
    
    protected NumberFormat formatter;
    
    public Max(final T max) {
        super();
        checkPreconditions(max);
        this.max = max;
    }
    
    public Max(final T max, final CellProcessor next) {
        super(next);
        checkPreconditions(max);
        this.max = max;
    }
    
    protected static <T extends Number & Comparable<T>> void checkPreconditions(final T max) {
        if(max == null) {
            throw new NullPointerException("max should not be null");
        }
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public Object execute(final Object value, final CsvContext context) {
        
        validateInputNotNull(value, context);
        
        final Class<?> exepectedClass = getMax().getClass();
        if(!exepectedClass.isAssignableFrom(value.getClass())) {
            throw new SuperCsvCellProcessorException(exepectedClass, value, context, this);
        }
        
        final T result = ((T) value);
        
        if(result.compareTo(max) > 0) {
            throw new SuperCsvConstraintViolationException(
                    String.format("%s does not lie the max (%s) values (inclusive)", result, max),
                    context, this);
        }
            
        return next.execute(result, context);
    }
    
    @Override
    public String getMessageCode() {
        return Max.class.getCanonicalName() + ".violated";
    }
    
    @Override
    public Map<String, ?> getMessageVariable() {
        Map<String, Object> vars = new HashMap<String, Object>();
        vars.put("max", getMax());
        return vars;
    }
    
    @Override
    public String formatValue(Object value) {
        if(value == null) {
            return "";
        }
        
        if(value instanceof Number) {
            final Number number = (Number) value;
            if(getFormatter() != null) {
                return new NumberFormatWrapper(getFormatter()).format(number);
            }
            
        }
        return value.toString();
    }
    
    public T getMax() {
        return max;
    }
    
    public NumberFormat getFormatter() {
        return formatter;
    }
    
    public Max<T> setFormatter(NumberFormat formatter) {
        this.formatter = formatter;
        return this;
    }
    
}

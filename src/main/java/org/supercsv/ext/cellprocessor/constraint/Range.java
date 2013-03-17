/*
 * Range.java
 * created in 2012/09/22
 *
 * (C) Copyright 2003-2012 GreenDay Project. All rights reserved.
 */
package org.supercsv.ext.cellprocessor.constraint;

import java.util.HashMap;
import java.util.Map;

import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.ift.DoubleCellProcessor;
import org.supercsv.cellprocessor.ift.LongCellProcessor;
import org.supercsv.exception.SuperCsvConstraintViolationException;
import org.supercsv.ext.cellprocessor.ift.ValidationCellProcessor;
import org.supercsv.util.CsvContext;


/**
 * validate with an inclusive range of Number objects of the same type.
 * 
 * @param <T> inherit number object. ex, int, Integer, double, Double,...
 * @author T.TSUCHIE
 *
 */
public class Range<T extends Number & Comparable<T>> extends CellProcessorAdaptor
        implements LongCellProcessor, DoubleCellProcessor, ValidationCellProcessor {
    
    protected final T min;
    
    protected final T max;
    
    public static <T extends Number & Comparable<T>> Range<T> range(final T min, final T max) {
        return new Range<T>(min, max);
    }
    
    public static <T extends Number & Comparable<T>> Range<T> range(final T min, final T max, final CellProcessor next) {
        return new Range<T>(min, max, next);
    }
    
    public Range(final T min, final T max) {
        super();
        checkPreconditions(min, max);
        this.min = min;
        this.max = max;
    }
    
    public Range(final T min, final T max, final CellProcessor next) {
        super(next);
        checkPreconditions(min, max);
        this.min = min;
        this.max = max;
    }
    
    protected static <T extends Number & Comparable<T>> void checkPreconditions(final T min, final T max) {
        if(min == null || max == null) {
            throw new NullPointerException("min and max should not be null");
        }
        
        if(min.compareTo(max) > 0) {
            throw new IllegalArgumentException(String.format("max (%s) should not be < min (%s)", max, min));
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object execute(final Object value, final CsvContext context) {
        
        validateInputNotNull(value, context);
        
        if(!(value instanceof Comparable)) {
            throw new SuperCsvConstraintViolationException(String.format(
                    "the value '%s' could not implement Comparable interface.",
                    value), context, this);
        }
        
        final T result = ((T) value);
        if(result.compareTo(min) < 0 || result.compareTo(max) > 0) {
            throw new SuperCsvConstraintViolationException(
                    String.format("%s does not lie between the min (%s) and max (%s) values (inclusive)", result, min, max),
                    context, this);
        }   
        
        return next.execute(result, context);
    }
    
    public T getMin() {
        return min;
    }
    
    public T getMax() {
        return max;
    }
    @Override
    public String getMessageCode() {
        return Range.class.getCanonicalName()+ ".violated";
    }
    
    @Override
    public Map<String, ?> getMessageVariable() {
        Map<String, Object> vars = new HashMap<String, Object>();
        vars.put("min", getMin());
        vars.put("max", getMax());
        return vars;
    }
    
    @Override
    public String formatValue(final Object value) {
        if(value == null) {
            return "";
        }
        return value.toString();
    }
    
}

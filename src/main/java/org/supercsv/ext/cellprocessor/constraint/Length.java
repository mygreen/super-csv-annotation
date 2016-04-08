/*
 * Length.java
 * created in 2013/03/10
 *
 * (C) Copyright 2003-2013 GreenDay Project. All rights reserved.
 */
package org.supercsv.ext.cellprocessor.constraint;

import java.util.HashMap;
import java.util.Map;

import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;
import org.supercsv.exception.SuperCsvCellProcessorException;
import org.supercsv.exception.SuperCsvConstraintViolationException;
import org.supercsv.ext.cellprocessor.ift.ValidationCellProcessor;
import org.supercsv.util.CsvContext;


/**
 *
 * @see 'StrMaxMin'
 * @author T.TSUCHIE
 *
 */
public class Length extends CellProcessorAdaptor implements StringCellProcessor, ValidationCellProcessor {
    
    protected final int min;
    
    protected final int max;
    
    public Length(final int min, final int max) {
        super();
        checkPreconditions(min, max);
        this.min = min;
        this.max = max;
    }
    
    public Length(final int min, final int max, final CellProcessor next) {
        super(next);
        checkPreconditions(min, max);
        this.min = min;
        this.max = max;
    }
    
    /**
     * Checks the preconditions for creating a new {@link Length} processor.
     * 
     * @param min
     *            the minimum String length
     * @param max
     *            the maximum String length
     * @throws IllegalArgumentException
     *             if max < min, or min is < 0
     */
    private static void checkPreconditions(final int min, final int max) {
        if( max < min ) {
            throw new IllegalArgumentException(String.format("max (%d) should not be < min (%d)", max, min));
        }
        
        if( min < 0 ) {
            throw new IllegalArgumentException(String.format("min length (%d) should not be < 0", min));
        }
    }
    
    /**
     * {@inheritDoc}
     * 
     * @throws SuperCsvCellProcessorException
     *             if value is null
     * @throws SuperCsvConstraintViolationException
     *             if length is < min or length > max
     */
    @SuppressWarnings("unchecked")
    public Object execute(final Object value, final CsvContext context) {
        validateInputNotNull(value, context);
        
        final String stringValue = value.toString();
        final int length = stringValue.length();
        if( length < min || length > max ) {
            throw new SuperCsvConstraintViolationException(String.format(
                "the length (%d) of value '%s' does not lie between the min (%d) and max (%d) values (inclusive)",
                length, stringValue, min, max), context, this);
        }
        
        return next.execute(stringValue, context);
    }
    
    @Override
    public String getMessageCode() {
        return Length.class.getCanonicalName() + ".violated";
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
    
    public int getMin() {
        return min;
    }
    
    public int getMax() {
        return max;
    }

}

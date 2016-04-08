/*
 * MinLength.java
 * created in 2013/03/06
 *
 * (C) Copyright 2003-2013 GreenDay Project. All rights reserved.
 */
package org.supercsv.ext.cellprocessor.constraint;

import java.util.HashMap;
import java.util.Map;

import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;
import org.supercsv.exception.SuperCsvConstraintViolationException;
import org.supercsv.ext.cellprocessor.ift.ValidationCellProcessor;
import org.supercsv.util.CsvContext;


/**
 * validate string min length
 *
 * @author T.TSUCHIE
 *
 */
public class MinLength extends CellProcessorAdaptor implements StringCellProcessor, ValidationCellProcessor {
    
    protected final int min;
    
    public MinLength(final int min) {
        super();
        checkPreconditions(min);
        this.min = min;
    }
    
    public MinLength(final int min, final CellProcessor next) {
        super(next);
        checkPreconditions(min);
        this.min = min;
    }
    
    private static void checkPreconditions(final int min) {
        
        if(min < 0) {
            throw new IllegalArgumentException(String.format("min (%d) should not be >= 0", min));
        }
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public Object execute(final Object value, final CsvContext context) {
        validateInputNotNull(value, context);
        
        final String stringValue = value.toString();
        final int length = stringValue.length();
        
        if( length < min ) {
            throw new SuperCsvConstraintViolationException(String.format(
                "the length (%d) of value '%s' does not lie the min (%d) values (inclusive)",
                length, stringValue, min), context, this);
        }
        
        return next.execute(stringValue, context);
    }
    
    public int getMin() {
        return min;
    }
    
    @Override
    public String getMessageCode() {
        return MinLength.class.getCanonicalName() + ".violated";
    }
    
    @Override
    public Map<String, ?> getMessageVariable() {
        Map<String, Object> vars = new HashMap<String, Object>();
        vars.put("min", getMin());
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

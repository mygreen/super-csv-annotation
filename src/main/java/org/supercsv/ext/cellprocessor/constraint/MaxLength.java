/*
 * MaxLength.java
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
 * validate string max length
 *
 * @author T.TSUCHIE
 *
 */
public class MaxLength extends CellProcessorAdaptor implements StringCellProcessor, ValidationCellProcessor {
    
    protected final int max;
    
    public MaxLength(final int max) {
        super();
        checkPreconditions(max);
        this.max = max;
    }
    
    public MaxLength(final int max, final CellProcessor next) {
        super(next);
        checkPreconditions(max);
        this.max = max;
    }
    
    private static void checkPreconditions(final int max) {
        
        if(max <= 0) {
            throw new IllegalArgumentException(String.format("max (%d) should not be < 0", max));
        }
    }
    
    @Override
    public Object execute(final Object value, final CsvContext context) {
        validateInputNotNull(value, context);
        
        final String stringValue = value.toString();
        final int length = stringValue.length();
        
        if( length > max ) {
            throw new SuperCsvConstraintViolationException(String.format(
                "the length (%d) of value '%s' does not lie the max (%d) values (inclusive)",
                length, stringValue, max), context, this);
        }
        
        return next.execute(stringValue, context);
    }
    
    public int getMax() {
        return max;
    }
    
    @Override
    public String getMessageCode() {
        return MaxLength.class.getCanonicalName() + ".violated";
    }
    
    @Override
    public Map<String, ?> getMessageVariable() {
        Map<String, Object> vars = new HashMap<String, Object>();
        vars.put("max", getMax());
        return vars;
    }
    
    @Override
    public String formateValue(final Object value) {
        if(value == null) {
            return "";
        }
        return value.toString();
    }
    
}

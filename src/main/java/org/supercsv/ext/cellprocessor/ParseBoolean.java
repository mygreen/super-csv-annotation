/*
 * ParseBoolean.java
 * created in 2013/03/12
 *
 * (C) Copyright 2003-2013 GreenDay Project. All rights reserved.
 */
package org.supercsv.ext.cellprocessor;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.cellprocessor.ift.BoolCellProcessor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;
import org.supercsv.exception.SuperCsvCellProcessorException;
import org.supercsv.ext.cellprocessor.ift.ValidationCellProcessor;
import org.supercsv.util.CsvContext;


/**
 *
 *
 * @author T.TSUCHIE
 *
 */
public class ParseBoolean extends CellProcessorAdaptor implements StringCellProcessor, ValidationCellProcessor {
    
    protected static final String[] DEFAULT_TRUE_VALUES = new String[] {"true", "1", "yes", "on", "y", "t"};
    protected static final String[] DEFAULT_FALSE_VALUES = new String[] {"false", "0", "no", "off", "f", "n"};
    
    protected final Set<String> trueValues;
    
    protected final Set<String> falseValues;
    
    /** ignore low / upper case. */
    protected final boolean lenient;
    
    /** if fail to pase, return */
    protected boolean failToFalse;
    
    public ParseBoolean() {
        this(DEFAULT_TRUE_VALUES, DEFAULT_FALSE_VALUES, false);
    }
    
    public ParseBoolean(final boolean lenient) {
        this(DEFAULT_TRUE_VALUES, DEFAULT_FALSE_VALUES, lenient);
    }
    
    public ParseBoolean(final String trueValue, final String falseValue) {
        this(trueValue, falseValue, false);
    }
    
    public ParseBoolean(final String trueValue, final String falseValue, final boolean lenient) {
        super();
        checkPreconditions(trueValue, falseValue);
        this.trueValues = createBooleanValuesSet(trueValue, lenient);
        this.falseValues = createBooleanValuesSet(falseValue, lenient);
        this.lenient = lenient;
    }
    
    public ParseBoolean(final String[] trueValues, final String[] falseValues) {
        this(trueValues, falseValues, false);
    }
    
    public ParseBoolean(final String[] trueValues, final String[] falseValues, final boolean lenient) {
        super();
        checkPreconditions(trueValues, falseValues);
        this.trueValues = createBooleanValuesSet(trueValues, lenient);
        this.falseValues = createBooleanValuesSet(falseValues, lenient);
        this.lenient = lenient;
    }
    
    public ParseBoolean(final String trueValue, final String falseValue, final BoolCellProcessor next) {
        this(trueValue, falseValue, false, next);
    }
    
    public ParseBoolean(final String trueValue, final String falseValue, final boolean lenient, final BoolCellProcessor next) {
        super(next);
        checkPreconditions(trueValue, falseValue);
        this.trueValues = createBooleanValuesSet(trueValue, lenient);
        this.falseValues = createBooleanValuesSet(falseValue, lenient);
        this.lenient = lenient;
    }
    
    public ParseBoolean(final String[] trueValues, final String[] falseValues, final BoolCellProcessor next) {
        this(trueValues, falseValues, false, next);
    }
    
    public ParseBoolean(final String[] trueValues, final String[] falseValues, final boolean lenient, final BoolCellProcessor next) {
        super(next);
        checkPreconditions(trueValues, falseValues);
        this.trueValues = createBooleanValuesSet(trueValues, lenient);
        this.falseValues = createBooleanValuesSet(falseValues, lenient);
        this.lenient = lenient;
    }
    
    protected Set<String> createBooleanValuesSet(final String value, final boolean lenient) {
        return createBooleanValuesSet(new String[]{value}, lenient);
        
    }
    
    protected Set<String> createBooleanValuesSet(final String[] values, final boolean lenient) {
        
        Set<String> set = new LinkedHashSet<String>();
        if(lenient) {
            for(String str : values) {
                // to lower
                set.add(str.toLowerCase());
            }
        } else {
            Collections.addAll(set, values);
        }
        return Collections.unmodifiableSet(set);
        
    }
    
    protected static void checkPreconditions(final String trueValue, final String falseValue) {
        if( trueValue == null ) {
            throw new NullPointerException("trueValue should not be null");
        }
        if( falseValue == null ) {
            throw new IllegalArgumentException("falseValue should not be null");
        }
    }
    
    protected static void checkPreconditions(final String[] trueValues, final String[] falseValues) {
        
        if( trueValues == null ) {
            throw new NullPointerException("trueValues should not be null");
        } else if( trueValues.length == 0 ) {
            throw new IllegalArgumentException("trueValues should not be empty");
        }
        
        if( falseValues == null ) {
            throw new NullPointerException("falseValues should not be null");
        } else if( falseValues.length == 0 ) {
            throw new IllegalArgumentException("falseValues should not be empty");
        }
        
    }
    
    @Override
    public Object execute(final Object value, final CsvContext context) {
        validateInputNotNull(value, context);
        
        if( !(value instanceof String) ) {
            throw new SuperCsvCellProcessorException(String.class, value, context, this);
        }
        
        final String stringValue = lenient ? ((String) value).toLowerCase() : (String) value;
        final Boolean result;
        if( trueValues.contains(stringValue) ) {
            result = Boolean.TRUE;
        } else if( falseValues.contains(stringValue) ) {
            result = Boolean.FALSE;
        } else {
            if(failToFalse) {
                result = Boolean.FALSE;
            } else {
                throw new SuperCsvCellProcessorException(String.format("'%s' could not be parsed as a Boolean", value),
                        context, this);
            }
        }
        
        return next.execute(result, context);
    }
    
    @Override
    public String getMessageCode() {
        return ParseBoolean.class.getCanonicalName() + ".violated";
    }
    
    @Override
    public Map<String, ?> getMessageVariable() {
        Map<String, Object> vars = new HashMap<String, Object>();
        vars.put("trueValues", getTrueValues());
        vars.put("falseValues", getFalseValues());
        vars.put("lenient", isLenient());
        vars.put("failToFalse", isFailToFalse());
        return vars;
    }
    
    @Override
    public String formatValue(final Object value) {
        if(value == null) {
            return "";
        }
        return value.toString();
    }
    
    public Set<String> getTrueValues() {
        return trueValues;
    }
    
    public Set<String> getFalseValues() {
        return falseValues;
    }
    
    public boolean isLenient() {
        return lenient;
    }
    
    public boolean isFailToFalse() {
        return failToFalse;
    }
    
    public ParseBoolean setFailToFalse(final boolean failToFalse) {
        this.failToFalse = failToFalse;
        return this;
    }
}

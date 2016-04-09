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
 * @version 1.2
 * @author T.TSUCHIE
 *
 */
public class ParseBoolean extends CellProcessorAdaptor implements StringCellProcessor, ValidationCellProcessor {
    
    protected static final String[] DEFAULT_TRUE_VALUES = new String[] {"true", "1", "yes", "on", "y", "t"};
    protected static final String[] DEFAULT_FALSE_VALUES = new String[] {"false", "0", "no", "off", "f", "n"};
    
    protected final Set<String> trueValues;
    
    protected final Set<String> falseValues;
    
    /** ignore low / upper case. */
    protected final boolean ignoreCase;
    
    /** if fail to pase, return */
    protected boolean failToFalse;
    
    public ParseBoolean() {
        this(DEFAULT_TRUE_VALUES, DEFAULT_FALSE_VALUES, false);
    }
    
    public ParseBoolean(final BoolCellProcessor next) {
        this(DEFAULT_TRUE_VALUES, DEFAULT_FALSE_VALUES, false, next);
    }
    
    public ParseBoolean(final boolean ignoreCase) {
        this(DEFAULT_TRUE_VALUES, DEFAULT_FALSE_VALUES, ignoreCase);
    }
    
    public ParseBoolean(final boolean ignoreCase, final BoolCellProcessor next) {
        this(DEFAULT_TRUE_VALUES, DEFAULT_FALSE_VALUES, ignoreCase, next);
    }
    
//    public ParseBoolean(final String trueValue, final String falseValue) {
//        this(trueValue, falseValue, false);
//    }
//    
//    public ParseBoolean(final String trueValue, final String falseValue, final boolean ignoreCase) {
//        super();
//        checkPreconditions(trueValue, falseValue);
//        this.trueValues = createBooleanValuesSet(trueValue, ignoreCase);
//        this.falseValues = createBooleanValuesSet(falseValue, ignoreCase);
//        this.ignoreCase = ignoreCase;
//    }
//    
    public ParseBoolean(final String[] trueValues, final String[] falseValues) {
        this(trueValues, falseValues, false);
    }
    
    public ParseBoolean(final String[] trueValues, final String[] falseValues, final boolean ignoreCase) {
        super();
        checkPreconditions(trueValues, falseValues);
        this.trueValues = createBooleanValuesSet(trueValues);
        this.falseValues = createBooleanValuesSet(falseValues);
        this.ignoreCase = ignoreCase;
    }
    
//    public ParseBoolean(final String trueValue, final String falseValue, final BoolCellProcessor next) {
//        this(trueValue, falseValue, false, next);
//    }
//    
//    public ParseBoolean(final String trueValue, final String falseValue, final boolean ignoreCase, final BoolCellProcessor next) {
//        super(next);
//        checkPreconditions(trueValue, falseValue);
//        this.trueValues = createBooleanValuesSet(trueValue, ignoreCase);
//        this.falseValues = createBooleanValuesSet(falseValue, ignoreCase);
//        this.ignoreCase = ignoreCase;
//    }
    
    public ParseBoolean(final String[] trueValues, final String[] falseValues, final BoolCellProcessor next) {
        this(trueValues, falseValues, false, next);
    }
    
    public ParseBoolean(final String[] trueValues, final String[] falseValues, final boolean ignoreCase, final BoolCellProcessor next) {
        super(next);
        checkPreconditions(trueValues, falseValues);
        this.trueValues = createBooleanValuesSet(trueValues);
        this.falseValues = createBooleanValuesSet(falseValues);
        this.ignoreCase = ignoreCase;
    }
    
//    protected Set<String> createBooleanValuesSet(final String value, final boolean ignoreCase) {
//        return createBooleanValuesSet(new String[]{value}, ignoreCase);
//        
//    }
    
    private Set<String> createBooleanValuesSet(final String[] values) {
        
        Set<String> set = new LinkedHashSet<>();
        Collections.addAll(set, values);
        return Collections.unmodifiableSet(set);
        
    }
    
//    protected static void checkPreconditions(final String trueValue, final String falseValue) {
//        if( trueValue == null ) {
//            throw new NullPointerException("trueValue should not be null");
//        }
//        if( falseValue == null ) {
//            throw new IllegalArgumentException("falseValue should not be null");
//        }
//    }
    
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
    
    @SuppressWarnings("unchecked")
    @Override
    public Object execute(final Object value, final CsvContext context) {
        
        validateInputNotNull(value, context);
        
        if( !(value instanceof String) ) {
            throw new SuperCsvCellProcessorException(String.class, value, context, this);
        }
        
        final String stringValue = (String) value;
        final Boolean result;
        if( contains(trueValues, stringValue, ignoreCase) ) {
            result = Boolean.TRUE;
            
        } else if( contains(falseValues, stringValue, ignoreCase) ) {
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
    
    private static boolean contains(final Set<String> set, final String value, final boolean ignoreCase) {
        
        if(ignoreCase) {
            for(String element : set) {
                if(element.equalsIgnoreCase(value)) {
                    return true;
                }
            }
            
            return false;
        } else {
            return set.contains(value);
        }
        
    }
    
    @Override
    public String getMessageCode() {
        return ParseBoolean.class.getCanonicalName() + ".violated";
    }
    
    @Override
    public Map<String, ?> getMessageVariable() {
        Map<String, Object> vars = new HashMap<String, Object>();
        vars.put("trueValues", getTrueValues());
        vars.put("trueStr", String.join(", ", getTrueValues()));
        vars.put("falseValues", getFalseValues());
        vars.put("falseStr", String.join(", ", getFalseValues()));
        vars.put("ignoreCase", isIgnoreCase());
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
    
    public boolean isIgnoreCase() {
        return ignoreCase;
    }
    
    public boolean isFailToFalse() {
        return failToFalse;
    }
    
    public ParseBoolean setFailToFalse(final boolean failToFalse) {
        this.failToFalse = failToFalse;
        return this;
    }
}

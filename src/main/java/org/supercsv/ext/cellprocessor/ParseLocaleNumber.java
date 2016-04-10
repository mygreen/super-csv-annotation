/*
 * ParseLocaleNumber.java
 * created in 2013/03/06
 *
 * (C) Copyright 2003-2013 GreenDay Project. All rights reserved.
 */
package org.supercsv.ext.cellprocessor;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;
import org.supercsv.exception.SuperCsvCellProcessorException;
import org.supercsv.ext.cellprocessor.ift.ValidationCellProcessor;
import org.supercsv.ext.util.ConversionException;
import org.supercsv.ext.util.NumberFormatWrapper;
import org.supercsv.util.CsvContext;


/**
 *
 * 
 * @author T.TSUCHIE
 *
 */
public class ParseLocaleNumber<N extends Number> extends CellProcessorAdaptor
        implements StringCellProcessor, ValidationCellProcessor {
    
    private Class<N> type;
    
    protected final NumberFormatWrapper formatter;
    
    public ParseLocaleNumber(final Class<N> type, final NumberFormat formatter, final boolean lenient) {
        super();
        checkPreconditions(type, formatter);
        this.type = type;
        this.formatter = new NumberFormatWrapper(formatter, lenient);
    }
    
    public ParseLocaleNumber(final Class<N> type, final NumberFormat formatter, final boolean lenient, final CellProcessor next) {
        super(next);
        checkPreconditions(type, formatter);
        this.type = type;
        this.formatter = new NumberFormatWrapper(formatter, lenient);
        
    }
    
    public ParseLocaleNumber(final Class<N> type, final NumberFormat formatter) {
        this(type, formatter, false);
    }
    
    public ParseLocaleNumber(final Class<N> type, final NumberFormat formatter, final CellProcessor next) {
        this(type, formatter, false, next);
        
    }
    
    /**
     * Checks the preconditions for creating a new ParseDate processor.
     * @throws NullPointerException type == null || formatter == null.
     * 
     */
    protected static <N extends Number> void checkPreconditions(final Class<N> type, final NumberFormat formatter) {
        
        if(type == null) {
            throw new NullPointerException("formatter is null.");
        }
        
        if(formatter == null) {
            throw new NullPointerException("formatter is null.");
        }
        
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public Object execute(final Object value, final CsvContext context) {
        validateInputNotNull(value, context);
        
        if(!(value instanceof String)) {
            throw new SuperCsvCellProcessorException(String.class, value, context, this);
        }
        
        try {
            Object result = formatter.parse(type, (String) value);
            return next.execute(result, context);
            
        } catch(ParseException | ConversionException e) {
            throw new SuperCsvCellProcessorException(
                    String.format("'%s' could not be parsed as a BigDecimal", value),
                    context, this, e);
        }
    }
    
    public Class<N> getType() {
        return type;
    }
    
    public String getPattern() {
        return formatter.getPattern();
    }
    
    @Override
    public String getMessageCode() {
        return ParseLocaleNumber.class.getCanonicalName() + ".violated";
    }

    @Override
    public Map<String, ?> getMessageVariable() {
        final Map<String, Object> vars = new HashMap<String, Object>();
        vars.put("type", getType().getCanonicalName());
        vars.put("pattern", getPattern());
        
        return vars;
    }
    
    @Override
    public String formatValue(final Object value) {
        if(value == null) {
            return "";
        }
        
        if(value instanceof Number) {
            final Number number = (Number) value;
            return formatter.format(number);
            
        }
        
        return value.toString();
    }
    
}

/*
 * FormatLocaleNumber.java
 * created in 2013/03/06
 *
 * (C) Copyright 2003-2013 GreenDay Project. All rights reserved.
 */
package org.supercsv.ext.cellprocessor;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.cellprocessor.FmtNumber;
import org.supercsv.cellprocessor.ift.DoubleCellProcessor;
import org.supercsv.cellprocessor.ift.LongCellProcessor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;
import org.supercsv.exception.SuperCsvCellProcessorException;
import org.supercsv.ext.cellprocessor.ift.ValidationCellProcessor;
import org.supercsv.util.CsvContext;


/**
 *
 * @see {@link FmtNumber}
 * @author T.TSUCHIE
 *
 */
public class FormatLocaleNumber extends CellProcessorAdaptor
        implements DoubleCellProcessor, LongCellProcessor, ValidationCellProcessor {
    
    protected final String pattern;
    
    protected final Currency currency;
    
    protected final DecimalFormatSymbols symbols;
    
    protected final ThreadLocal<NumberFormat> formatter;
    
    public FormatLocaleNumber(final String pattern) {
        this(pattern, null, null);
    }
    
    public FormatLocaleNumber(final String pattern, final StringCellProcessor next) {
        this(pattern, null, null, next);
    }
    
    public FormatLocaleNumber(final NumberFormat formatter) {
        super();
        this.pattern = null;
        this.currency = null;
        this.symbols = null;
        this.formatter = new ThreadLocal<NumberFormat>() {
            
            @Override
            protected NumberFormat initialValue() {
                return formatter;
            }
        };
    }
    
    public FormatLocaleNumber(final NumberFormat formatter, final StringCellProcessor next) {
        super(next);
        this.pattern = null;
        this.currency = null;
        this.symbols = null;
        
        this.formatter = new ThreadLocal<NumberFormat>() {
            
            @Override
            protected NumberFormat initialValue() {
                return formatter;
            }
        };
    }
    
    public FormatLocaleNumber(final String pattern, final Currency currency, final DecimalFormatSymbols symbols) {
        super();
        checkPreconditions(pattern);
        this.pattern = pattern;
        this.currency = currency;
        this.symbols = symbols;
        this.formatter = createNumberFormatter(pattern, currency, symbols);
    }
    
    public FormatLocaleNumber(final String pattern, final Currency currency, final DecimalFormatSymbols symbols, final StringCellProcessor next) {
        super(next);
        checkPreconditions(pattern);
        this.pattern = pattern;
        this.currency = currency;
        this.symbols = symbols;
        this.formatter = createNumberFormatter(pattern, currency, symbols);
    }
    
    public static ThreadLocal<NumberFormat> createNumberFormatter(final String pattern, final Currency currency, final DecimalFormatSymbols symbols) {
        return new ThreadLocal<NumberFormat>(){
            
            @Override
            protected NumberFormat initialValue() {
                DecimalFormat value = null;
                if(symbols != null) {
                    value = new DecimalFormat(pattern, symbols);
                } else {
                    value = new DecimalFormat(pattern);
                }
                
                value.setParseBigDecimal(true);
                
                if(currency != null) {
                    value.setCurrency(currency);
                }
                
                return value;
            }
        };
    }
    
    /**
     * Checks the preconditions for creating a new ParseDate processor.
     * @throws IllegalArgumentException
     * 
     */
    protected static void checkPreconditions(final String pattern) {
        if(pattern == null || pattern.isEmpty() ) {
            throw new IllegalArgumentException("pattern should not be null");
        }
    }
    
    @Override
    public Object execute(final Object value, final CsvContext context) {
        
        validateInputNotNull(value, context);
        
        if(!(value instanceof Number)) {
            throw new SuperCsvCellProcessorException(Number.class, value, context, this);
        }
        
        String result = formatter.get().format((Date) value);
        return next.execute(result, context);
    }
    
    public String getPattern() {
        return pattern;
    }
    
    public Currency getCurrency() {
        return currency;
    }
    
    public DecimalFormatSymbols getSymbols() {
        return symbols;
    }
    
    public ThreadLocal<NumberFormat> getFormatter() {
        return formatter;
    }
    
    @Override
    public String getMessageCode() {
        return FormatLocaleNumber.class.getCanonicalName() + ".violated";
    }
    
    @Override
    public Map<String, ?> getMessageVariable() {
        Map<String, Object> vars = new HashMap<String, Object>();
        vars.put("pattern", getPattern());
        vars.put("currency", getCurrency());
        vars.put("symbols", getSymbols());
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

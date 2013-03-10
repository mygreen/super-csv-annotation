/*
 * ParseLocaleDate.java
 * created in 2013/03/06
 *
 * (C) Copyright 2003-2013 GreenDay Project. All rights reserved.
 */
package org.supercsv.ext.cellprocessor;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.cellprocessor.ParseDate;
import org.supercsv.cellprocessor.ift.DateCellProcessor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;
import org.supercsv.exception.SuperCsvCellProcessorException;
import org.supercsv.ext.cellprocessor.ift.ValidationCellProcessor;
import org.supercsv.util.CsvContext;


/**
 *
 * @see {@link ParseDate}
 * @author T.TSUCHIE
 *
 */
public class ParseLocaleDate  extends CellProcessorAdaptor
        implements StringCellProcessor, ValidationCellProcessor {
    
    protected final String pattern;
    
    protected final boolean lenient;
    
    protected final Locale locale;
    
    protected final TimeZone timeZone;
    
    protected final ThreadLocal<DateFormat> formatter = new ThreadLocal<DateFormat>() {
        
        @Override
        protected DateFormat initialValue() {
            DateFormat value = new SimpleDateFormat(pattern, locale);
            value.setLenient(lenient);
            
            if(timeZone != null) {
                value.setTimeZone(timeZone);
            }
            
            return value;
        }
    };
    
    public ParseLocaleDate(final String pattern) {
        this(pattern, true, Locale.getDefault(), null);
    }
    
    public ParseLocaleDate(final String pattern, final DateCellProcessor next) {
        this(pattern, true, Locale.getDefault(), null, next);
    }
    
    public ParseLocaleDate(final String pattern, final boolean lenient, final Locale locale, final TimeZone timeZone) {
        super();
        checkPreconditions(pattern, locale);
        this.pattern = pattern;
        this.lenient = lenient;
        this.locale = locale;
        this.timeZone = timeZone;
    }
    
    public ParseLocaleDate(final String pattern, final boolean lenient, final Locale locale, final TimeZone timeZone, final DateCellProcessor next) {
        super(next);
        checkPreconditions(pattern, locale);
        this.pattern = pattern;
        this.lenient = lenient;
        this.locale = locale;
        this.timeZone = timeZone;
    }
    
    /**
     * Checks the preconditions for creating a new ParseDate processor.
     * @throws IllegalArgumentException
     * 
     */
    protected void checkPreconditions(final String pattern, final Locale locale) {
        if(pattern == null || pattern.isEmpty() ) {
            throw new IllegalArgumentException("pattern should not be null");
        }
        
        if(locale == null) {
            throw new IllegalArgumentException("locale should not be null");
        }
    }
    
    /**
     * {@inheritDoc}
     * 
     * @throws SuperCsvCellProcessorException
     *             if value is null, isn't a String, or can't be parsed to a Date
     */
    @Override
    public Object execute(final Object value, final CsvContext context) {
        validateInputNotNull(value, context);
        
        if( !(value instanceof String) ) {
            throw new SuperCsvCellProcessorException(String.class, value, context, this);
        }
        
        try {
            Object result = parse((String) value);
            return next.execute(result, context);
            
        } catch(ParseException e) {
            throw new SuperCsvCellProcessorException(String.format("'%s' could not be parsed as a Date", value),
                    context, this, e);
        }
    }
    
    protected Object parse(final String value) throws ParseException {
        
        final Date result = formatter.get().parse(value);
        return result;
    }
    
    public String getPattern() {
        return pattern;
    }
    
    public Locale getLocale() {
        return locale;
    }
    
    public boolean isLenient() {
        return lenient;
    }
    
    public TimeZone getTimeZone() {
        return timeZone;
    }
    
    public ThreadLocal<DateFormat> getFormatter() {
        return formatter;
    }
    
    @Override
    public String getMessageCode() {
        return this.getClass().getCanonicalName() + ".violated";
    }
    
    @Override
    public Map<String, ?> getMessageVariable() {
        Map<String, Object> vars = new HashMap<String, Object>();
        vars.put("pattern", getPattern());
        vars.put("locale", getLocale());
        vars.put("timeZone", getTimeZone());
        
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

/*
 * FormatLocaleDate.java
 * created in 2013/03/06
 *
 * (C) Copyright 2003-2013 GreenDay Project. All rights reserved.
 */
package org.supercsv.ext.cellprocessor;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.cellprocessor.FmtDate;
import org.supercsv.cellprocessor.ift.DateCellProcessor;
import org.supercsv.exception.SuperCsvCellProcessorException;
import org.supercsv.ext.cellprocessor.ift.ValidationCellProcessor;
import org.supercsv.util.CsvContext;


/**
 *
 * @see {@link FmtDate}
 * @author T.TSUCHIE
 *
 */
public class FormatLocaleDate extends CellProcessorAdaptor
        implements DateCellProcessor, ValidationCellProcessor {
    
    protected final String pattern;
    
    protected final Locale locale;
    
    protected final TimeZone timeZone;
    
    protected final ThreadLocal<DateFormat> formatter;
    
    public FormatLocaleDate(final String pattern) {
        this(pattern, Locale.getDefault(), null);
    }
    
    public FormatLocaleDate(final String pattern, final DateCellProcessor next) {
        this(pattern, Locale.getDefault(), null, next);
    }
    
    public FormatLocaleDate(final DateFormat formatter) {
        super();
        this.pattern = null;
        this.locale = null;
        this.timeZone = null;
        this.formatter = new ThreadLocal<DateFormat>() {
            @Override
            protected DateFormat initialValue() {
                return formatter;
            }
        };
    }
    
    public FormatLocaleDate(final DateFormat formatter, final DateCellProcessor next) {
        super();
        this.pattern = null;
        this.locale = null;
        this.timeZone = null;
        this.formatter = new ThreadLocal<DateFormat>() {
            @Override
            protected DateFormat initialValue() {
                return formatter;
            }
        };
    }
    
    public FormatLocaleDate(final String pattern, final Locale locale, final TimeZone timeZone) {
        super();
        checkPreconditions(pattern, locale);
        this.pattern = pattern;
        this.locale = locale;
        this.timeZone = timeZone;
        this.formatter = createDateFormatter(pattern, locale, timeZone);
    }
    
    public FormatLocaleDate(final String pattern, final Locale locale, final TimeZone timeZone, final DateCellProcessor next) {
        super(next);
        checkPreconditions(pattern, locale);
        this.pattern = pattern;
        this.locale = locale;
        this.timeZone = timeZone;
        this.formatter = createDateFormatter(pattern, locale, timeZone);
    }
    
    protected static ThreadLocal<DateFormat> createDateFormatter(final String pattern, final Locale locale, final TimeZone timeZone) {
        return new ThreadLocal<DateFormat>() {
            
            @Override
            protected DateFormat initialValue() {
                DateFormat value = new SimpleDateFormat(pattern, locale);
//                value.setLenient(lenient);
                
                if(timeZone != null) {
                    value.setTimeZone(timeZone);
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
    protected void checkPreconditions(final String pattern, final Locale locale) {
        if(pattern == null || pattern.isEmpty() ) {
            throw new IllegalArgumentException("pattern should not be null");
        }
        
        if(locale == null) {
            throw new IllegalArgumentException("locale should not be null");
        }
    }
    
    @Override
    public Object execute(Object value, CsvContext context) {
        
        validateInputNotNull(value, context);
        
        if(!(value instanceof Date)) {
            throw new SuperCsvCellProcessorException(Date.class, value, context, this);
        }
        
        String result = formatter.get().format((Date) value);
        return next.execute(result, context);
    }
    
    public String getPattern() {
        return pattern;
    }
    
    public Locale getLocale() {
        return locale;
    }
    
    public TimeZone getTimeZone() {
        return timeZone;
    }
    
    public ThreadLocal<DateFormat> getFormatter() {
        return formatter;
    }
    
    @Override
    public String getMessageCode() {
        return FormatLocaleDate.class.getCanonicalName() + ".violated";
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

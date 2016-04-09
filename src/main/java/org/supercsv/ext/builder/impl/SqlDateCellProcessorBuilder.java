package org.supercsv.ext.builder.impl;

import java.lang.annotation.Annotation;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.ift.DateCellProcessor;
import org.supercsv.ext.annotation.CsvDateConverter;
import org.supercsv.ext.cellprocessor.ParseLocaleSqlDate;

public class SqlDateCellProcessorBuilder extends DateCellProcessorBuilder {
    
    @Override
    protected String getPattern(final CsvDateConverter converterAnno) {
        if(converterAnno == null) {
            return "yyyy-MM-dd";
        }
        
        return converterAnno.pattern();
    }
    
    @Override
    public CellProcessor buildInputCellProcessor(final Class<Date> type, final Annotation[] annos, 
            final CellProcessor processor) {
        
        final CsvDateConverter converterAnno = getAnnotation(annos);
        final String pattern = getPattern(converterAnno);
        final boolean lenient = getLenient(converterAnno);
        final Locale locale = getLocale(converterAnno);
        final TimeZone timeZone = getTimeZone(converterAnno);
        
        final DateFormat formatter = createDateFormat(pattern, lenient, locale, timeZone);
        
        final java.sql.Date min = parseDate(getMin(converterAnno), formatter);
        final java.sql.Date max = parseDate(getMax(converterAnno), formatter);
        
        CellProcessor cellProcessor = processor;
        cellProcessor = prependRangeProcessor(min, max, formatter, cellProcessor);
        
        cellProcessor = (cellProcessor == null ?
                new ParseLocaleSqlDate(formatter) :
                    new ParseLocaleSqlDate(formatter, (DateCellProcessor)cellProcessor));
        
        return cellProcessor;
        
    }
    
    @Override
    protected java.sql.Date parseDate(final String value, final DateFormat formatter) {
        Date date = super.parseDate(value, formatter);
        return date == null ? null : new java.sql.Date(date.getTime());
    }
    
    @Override
    public Date getParseValue(final Class<Date> type, final Annotation[] annos, final String defaultValue) {
        return new java.sql.Date(super.getParseValue(type, annos, defaultValue).getTime());
    }
    
}

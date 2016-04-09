package org.supercsv.ext.builder.impl;

import java.lang.annotation.Annotation;
import java.sql.Time;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.ift.DateCellProcessor;
import org.supercsv.ext.annotation.CsvDateConverter;
import org.supercsv.ext.cellprocessor.ParseLocaleTime;

public class TimeCellProcessorBuilder extends DateCellProcessorBuilder {
    
    @Override
    protected String getPattern(final CsvDateConverter converterAnno) {
        if(converterAnno == null) {
            return "HH:mm";
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
        
        final Time min = parseDate(getMin(converterAnno), formatter);
        final Time max = parseDate(getMax(converterAnno), formatter);
        
        CellProcessor cellProcessor = processor;
        cellProcessor = prependRangeProcessor(min, max, formatter, cellProcessor);
        cellProcessor = (cellProcessor == null ?
                new ParseLocaleTime(formatter) :
                    new ParseLocaleTime(formatter, (DateCellProcessor)cellProcessor));
        
        return cellProcessor;
        
    }
    
    @Override
    protected Time parseDate(final String value, final DateFormat formatter) {
        Date date = super.parseDate(value, formatter);
        return date == null ? null : new Time(date.getTime());
    }
    
    @Override
    public Date getParseValue(final Class<Date> type, final Annotation[] annos, final String defaultValue) {
        return new Time(super.getParseValue(type, annos, defaultValue).getTime());
    }
}

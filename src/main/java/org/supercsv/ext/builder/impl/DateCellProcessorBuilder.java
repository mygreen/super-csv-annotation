package org.supercsv.ext.builder.impl;

import java.lang.annotation.Annotation;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.ift.DateCellProcessor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;
import org.supercsv.ext.annotation.CsvDateConverter;
import org.supercsv.ext.builder.AbstractCellProcessorBuilder;
import org.supercsv.ext.cellprocessor.FormatLocaleDate;
import org.supercsv.ext.cellprocessor.ParseLocaleDate;
import org.supercsv.ext.cellprocessor.constraint.DateRange;
import org.supercsv.ext.cellprocessor.constraint.FutureDate;
import org.supercsv.ext.cellprocessor.constraint.PastDate;
import org.supercsv.ext.exception.SuperCsvInvalidAnnotationException;
import org.supercsv.ext.util.Utils;


/**
 *
 * @version 1.2
 * @author T.TSUCHIE
 *
 */
public class DateCellProcessorBuilder extends AbstractCellProcessorBuilder<Date> {
    
    protected DateFormat createDateFormatter(final CsvDateConverter converterAnno) {
        
        final String pattern = getPattern(converterAnno);
        final boolean lenient = getLenient(converterAnno);
        final Locale locale = getLocale(converterAnno);
        final TimeZone timeZone = getTimeZone(converterAnno);
        
        return createDateFormatter(pattern, lenient, locale, timeZone);
    }
    
    protected DateFormat createDateFormatter(final String pattern, boolean lenient,
            final Locale locale, final TimeZone timeZone) {
        
        final DateFormat value = new SimpleDateFormat(pattern, locale);
        value.setLenient(lenient);
        value.setTimeZone(timeZone);
        
        return value;
    }
    
    protected CsvDateConverter getAnnotation(final Annotation[] annos) {
        
        for(Annotation anno : annos) {
            if(anno instanceof CsvDateConverter) {
                return (CsvDateConverter) anno;
            }
        }
        
        return null;
        
    }
    
    protected String getPattern(final CsvDateConverter converterAnno) {
        if(converterAnno == null || converterAnno.pattern().isEmpty()) {
            return "yyyy-MM-dd HH:mm:ss";
        }
        
        return converterAnno.pattern();
    }
    
    protected boolean getLenient(final CsvDateConverter converterAnno) {
        if(converterAnno == null) {
            return true;
        }
        
        return converterAnno.lenient();
    }
    
    protected Locale getLocale(final CsvDateConverter converterAnno) {
        if(converterAnno == null || converterAnno.locale().isEmpty()) {
            return Locale.getDefault();
        }
        
        return Utils.getLocale(converterAnno.locale());
    }
    
    protected TimeZone getTimeZone(final CsvDateConverter converterAnno) {
        if(converterAnno == null || converterAnno.timezone().isEmpty()) {
            return TimeZone.getDefault();
        }
        
        return TimeZone.getTimeZone(converterAnno.timezone());
    }
    
    protected String getMin(final CsvDateConverter converterAnno) {
        if(converterAnno == null) {
            return "";
        }
        
        return converterAnno.min();
    }
    
    protected String getMax(final CsvDateConverter converterAnno) {
        if(converterAnno == null) {
            return "";
        }
        
        return converterAnno.max();
    }
    
    protected CellProcessor prependRangeProcessor(final Date min, final Date max, final DateFormat formatter, final CellProcessor processor) {
        
        CellProcessor cp = processor;
        if(min != null && max != null) {
            if(cp == null) {
                cp = new DateRange<Date>(min, max).setFormatter(formatter);
            } else {
                cp = new DateRange<Date>(min, max, cp).setFormatter(formatter);
            }
        } else if(min != null) {
            if(cp == null) {
                cp = new FutureDate<Date>(min).setFormatter(formatter);
            } else {
                cp = new FutureDate<Date>(min, cp).setFormatter(formatter);
            }
        } else if(max != null) {
            if(cp == null) {
                cp = new PastDate<Date>(max).setFormatter(formatter);
            } else {
                cp = new PastDate<Date>(max, cp).setFormatter(formatter);
            }
        }
        
        return cp;
    }
    
    @Override
    public CellProcessor buildOutputCellProcessor(final Class<Date> type, final Annotation[] annos,
            final CellProcessor processor, final boolean ignoreValidationProcessor) {
        
        final CsvDateConverter converterAnno = getAnnotation(annos);
        final DateFormat formatter = createDateFormatter(converterAnno);
        
        final Date min = getParseValue(type, annos, getMin(converterAnno));
        final Date max = getParseValue(type, annos, getMax(converterAnno));
        
        CellProcessor cp = processor;
        cp = (cp == null ? 
                new FormatLocaleDate(formatter) : 
                    new FormatLocaleDate(formatter, (StringCellProcessor) cp));
        
        if(!ignoreValidationProcessor) {
            cp = prependRangeProcessor(min, max, formatter, cp);
        }
        return cp;
    }
    
    @Override
    public CellProcessor buildInputCellProcessor(final Class<Date> type, final Annotation[] annos,
            final CellProcessor processor) {
        
        final CsvDateConverter converterAnno = getAnnotation(annos);
        final DateFormat formatter = createDateFormatter(converterAnno);
        
        final Date min = getParseValue(type, annos, getMin(converterAnno));
        final Date max = getParseValue(type, annos, getMax(converterAnno));
        
        CellProcessor cp = processor;
        cp = prependRangeProcessor(min, max, formatter, cp);
        
        cp = (cp == null ?
                new ParseLocaleDate(formatter) :
                    new ParseLocaleDate(formatter, (DateCellProcessor)cp));
        
        return cp;
        
    }
    
    @Override
    public Date getParseValue(final Class<Date> type, final Annotation[] annos, final String strValue) {
        
        if(strValue.isEmpty()) {
            return null;
        }
        
        final CsvDateConverter converterAnno = getAnnotation(annos);
        final DateFormat formatter = createDateFormatter(converterAnno);
        final String pattern = getPattern(converterAnno);
        
        try {
            return formatter.parse(strValue);
        } catch (ParseException e) {
            throw new SuperCsvInvalidAnnotationException(
                    String.format(" default '%s' value cannot parse to Date with pattern '%s'", strValue, pattern),
                    e);
        }
    }
    
}

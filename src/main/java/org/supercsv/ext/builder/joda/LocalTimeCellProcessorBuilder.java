package org.supercsv.ext.builder.joda;

import java.lang.annotation.Annotation;
import java.util.Locale;
import java.util.Optional;

import org.joda.time.DateTimeZone;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormatter;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.joda.FmtLocalTime;
import org.supercsv.cellprocessor.joda.ParseLocalTime;
import org.supercsv.ext.annotation.CsvDateConverter;
import org.supercsv.ext.exception.SuperCsvInvalidAnnotationException;

/**
 * The cell processor builder for {@link LocalTime} with Joda-Time.
 * 
 * @since 1.2
 * @author T.TSUCHIE
 *
 */
public class LocalTimeCellProcessorBuilder extends AbstractJodaCellProcessorBuilder<LocalTime> {
    
    @Override
    protected String getDefaultPattern() {
        return "HH:mm:ss";
    }
    
    @Override
    protected LocalTime parseJoda(final String value, final DateTimeFormatter formatter) {
        return formatter.parseLocalTime(value);
    }
    
    @Override
    public LocalTime getParseValue(final Class<LocalTime> type, final Annotation[] annos, final String strValue) {
        
        final Optional<CsvDateConverter> converterAnno = getAnnotation(annos);
        
        final String pattern = getPattern(converterAnno);
        final Locale locale = getLocale(converterAnno);
        final DateTimeZone zone = getDateTimeZone(converterAnno);
        
        final DateTimeFormatter formatter = createDateTimeFormatter(pattern, locale, zone);
        
        try {
            return LocalTime.parse(strValue, formatter);
            
        } catch(IllegalArgumentException e) {
            throw new SuperCsvInvalidAnnotationException(
                    String.format("default '%s' value cannot parse to LocalTime with pattern '%s'",
                            strValue, pattern), e);
            
        }
    }
    
    @Override
    public CellProcessor buildOutputCellProcessor(final Class<LocalTime> type,final  Annotation[] annos,
            final CellProcessor processor, final boolean ignoreValidationProcessor) {
        
        final Optional<CsvDateConverter> converterAnno = getAnnotation(annos);
        final String pattern = getPattern(converterAnno);
        final Locale locale = getLocale(converterAnno);
        final DateTimeZone zone = getDateTimeZone(converterAnno);
        
        final DateTimeFormatter formatter = createDateTimeFormatter(pattern, locale, zone);
        
        final Optional<LocalTime> min = getMin(converterAnno).map(s -> parseJoda(s, formatter));
        final Optional<LocalTime> max = getMax(converterAnno).map(s -> parseJoda(s, formatter));
        
        CellProcessor cp = processor;
        cp = (cp == null ? new FmtLocalTime(formatter) : new FmtLocalTime(formatter, cp));
        
        if(!ignoreValidationProcessor) {
            cp = prependRangeProcessor(min, max, cp);
        }
        
        return cp;
        
    }
    
    @Override
    public CellProcessor buildInputCellProcessor(final Class<LocalTime> type, final Annotation[] annos,
            final CellProcessor processor) {
        
        final Optional<CsvDateConverter> converterAnno = getAnnotation(annos);
        final String pattern = getPattern(converterAnno);
        final Locale locale = getLocale(converterAnno);
        final DateTimeZone zone = getDateTimeZone(converterAnno);
        
        final DateTimeFormatter formatter = createDateTimeFormatter(pattern, locale, zone);
        
        final Optional<LocalTime> min = getMin(converterAnno).map(s -> parseJoda(s, formatter));
        final Optional<LocalTime> max = getMax(converterAnno).map(s -> parseJoda(s, formatter));
        
        CellProcessor cp = processor;
        cp = prependRangeProcessor(min, max, cp);
        cp = (cp == null ? new ParseLocalTime(formatter) : new ParseLocalTime(formatter, cp));
        
        return cp;
    }
    
    
}

package org.supercsv.ext.builder.joda;

import java.lang.annotation.Annotation;
import java.util.Locale;
import java.util.Optional;

import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormatter;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.joda.FmtLocalDate;
import org.supercsv.cellprocessor.joda.ParseLocalDate;
import org.supercsv.ext.annotation.CsvDateConverter;
import org.supercsv.ext.exception.SuperCsvInvalidAnnotationException;

/**
 * The cell processor builder for {@link LocalDate} with Joda-Time.
 * 
 * @since 1.2
 * @author T.TSUCHIE
 *
 */
public class LocalDateCellProcessorBuilder extends AbstractJodaCellProcessorBuilder<LocalDate> {
    
    @Override
    protected String getDefaultPattern() {
        return "yyyy/MM/dd";
    }
    
    @Override
    protected LocalDate parseJoda(final String value, final DateTimeFormatter formatter) {
        return formatter.parseLocalDate(value);
    }
    
    @Override
    public LocalDate getParseValue(final Class<LocalDate> type, final Annotation[] annos, final String strValue) {
        
        final Optional<CsvDateConverter> converterAnno = getAnnotation(annos);
        
        final String pattern = getPattern(converterAnno);
        final Locale locale = getLocale(converterAnno);
        final DateTimeZone zone = getDateTimeZone(converterAnno);
        
        final DateTimeFormatter formatter = createDateTimeFormatter(pattern, locale, zone);
        
        try {
            return LocalDate.parse(strValue, formatter);
            
        } catch(IllegalArgumentException e) {
            throw new SuperCsvInvalidAnnotationException(
                    String.format("default '%s' value cannot parse to LocalDate with pattern '%s'",
                            strValue, pattern), e);
            
        }
    }
    
    @Override
    public CellProcessor buildOutputCellProcessor(final Class<LocalDate> type,final  Annotation[] annos,
            final CellProcessor processor, final boolean ignoreValidationProcessor) {
        
        final Optional<CsvDateConverter> converterAnno = getAnnotation(annos);
        final String pattern = getPattern(converterAnno);
        final Locale locale = getLocale(converterAnno);
        final DateTimeZone zone = getDateTimeZone(converterAnno);
        
        final DateTimeFormatter formatter = createDateTimeFormatter(pattern, locale, zone);
        
        final Optional<LocalDate> min = getMin(converterAnno).map(s -> parseJoda(s, formatter));
        final Optional<LocalDate> max = getMax(converterAnno).map(s -> parseJoda(s, formatter));
        
        CellProcessor cp = processor;
        cp = (cp == null ? new FmtLocalDate(formatter) : new FmtLocalDate(formatter, cp));
        
        if(!ignoreValidationProcessor) {
            cp = prependRangeProcessor(min, max, cp);
        }
        
        return cp;
        
    }
    
    @Override
    public CellProcessor buildInputCellProcessor(final Class<LocalDate> type, final Annotation[] annos,
            final CellProcessor processor) {
        
        final Optional<CsvDateConverter> converterAnno = getAnnotation(annos);
        final String pattern = getPattern(converterAnno);
        final Locale locale = getLocale(converterAnno);
        final DateTimeZone zone = getDateTimeZone(converterAnno);
        
        final DateTimeFormatter formatter = createDateTimeFormatter(pattern, locale, zone);
        
        final Optional<LocalDate> min = getMin(converterAnno).map(s -> parseJoda(s, formatter));
        final Optional<LocalDate> max = getMax(converterAnno).map(s -> parseJoda(s, formatter));
        
        CellProcessor cp = processor;
        cp = prependRangeProcessor(min, max, cp);
        cp = (cp == null ? new ParseLocalDate(formatter) : new ParseLocalDate(formatter, cp));
        
        return cp;
    }
    
    
}

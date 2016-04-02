package org.supercsv.ext.builder.time;

import java.lang.annotation.Annotation;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;
import java.util.Optional;

import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.time.FmtLocalTime;
import org.supercsv.cellprocessor.time.ParseLocalTime;
import org.supercsv.ext.annotation.CsvDateConverter;
import org.supercsv.ext.exception.SuperCsvInvalidAnnotationException;

/**
 * The cell processor builder for {@link LocalTime}.
 *
 * @since 1.2
 * @author T.TSUCHIE
 *
 */
public class LocalTimeCellProcessorBuilder extends AbstractTemporalAccessorCellProcessorBuilder<LocalTime> {
    
    @Override
    protected String getDefaultPattern() {
        return "HH:mm:ss";
    }
    
    @Override
    protected LocalTime parseTemporal(final String value, final DateTimeFormatter formatter) {
        return LocalTime.parse(value, formatter);
    }
    
    @Override
    public LocalTime getParseValue(final Class<LocalTime> type, final Annotation[] annos, final String strValue) {
        
        final Optional<CsvDateConverter> converterAnno = getAnnotation(annos);
        
        final String pattern = getPattern(converterAnno);
        final ResolverStyle style = getResolverStyle(converterAnno);
        final Locale locale = getLocale(converterAnno);
        final ZoneId zone = getZoneId(converterAnno);
        final DateTimeFormatter formatter = createDateTimeFormatter(pattern, style, locale, zone);
        
        try {
            return LocalTime.parse(strValue, formatter);
            
        } catch(DateTimeParseException e) {
            throw new SuperCsvInvalidAnnotationException(
                    String.format("default '%s' value cannot parse to Date with pattern '%s'",
                            strValue, pattern), e);
            
        }
    }
    
    @Override
    public CellProcessor buildOutputCellProcessor(final Class<LocalTime> type, final Annotation[] annos,
            final CellProcessor processor, final boolean ignoreValidationProcessor) {
        
        final Optional<CsvDateConverter> converterAnno = getAnnotation(annos);
        final String pattern = getPattern(converterAnno);
        final ResolverStyle style = getResolverStyle(converterAnno);
        final Locale locale = getLocale(converterAnno);
        final ZoneId zone = getZoneId(converterAnno);
        
        final DateTimeFormatter formatter = createDateTimeFormatter(pattern, style, locale, zone);
        
        final Optional<LocalTime> min = getMin(converterAnno).map(s -> parseTemporal(s, formatter));
        final Optional<LocalTime> max = getMax(converterAnno).map(s -> parseTemporal(s, formatter));
        
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
        final ResolverStyle style = getResolverStyle(converterAnno);
        final Locale locale = getLocale(converterAnno);
        final ZoneId zone = getZoneId(converterAnno);
        
        final DateTimeFormatter formatter = createDateTimeFormatter(pattern, style, locale, zone);
        
        CellProcessor cp = processor;
        cp = (cp == null ? new ParseLocalTime(formatter) : new ParseLocalTime(formatter, cp));
        
        return cp;
        
    }
    
}

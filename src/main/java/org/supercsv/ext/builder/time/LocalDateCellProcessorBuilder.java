package org.supercsv.ext.builder.time;

import java.lang.annotation.Annotation;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;
import java.util.Optional;

import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.time.FmtLocalDate;
import org.supercsv.cellprocessor.time.ParseLocalDate;
import org.supercsv.ext.annotation.CsvDateConverter;
import org.supercsv.ext.exception.SuperCsvInvalidAnnotationException;

/**
 * The cell processor builder for {@link LocalDate}.
 *
 * @since 1.2
 * @author T.TSUCHIE
 *
 */
public class LocalDateCellProcessorBuilder extends AbstractTemporalAccessorCellProcessorBuilder<LocalDate> {
    
    @Override
    protected String getDefaultPattern() {
        return "yyyy/MM/dd";
    }
    
    @Override
    protected LocalDate parseTemporal(final String value, final DateTimeFormatter formatter) {
        return LocalDate.parse(value, formatter);
    }
    
    @Override
    public LocalDate getParseValue(final Class<LocalDate> type, final Annotation[] annos, final String strValue) {
        
        final Optional<CsvDateConverter> converterAnno = getAnnotation(annos);
        
        final String pattern = getPattern(converterAnno);
        final ResolverStyle style = getResolverStyle(converterAnno);
        final Locale locale = getLocale(converterAnno);
        final ZoneId zone = getZoneId(converterAnno);
        final DateTimeFormatter formatter = createDateTimeFormatter(pattern, style, locale, zone);
        
        try {
            return LocalDate.parse(strValue, formatter);
            
        } catch(DateTimeParseException e) {
            throw new SuperCsvInvalidAnnotationException(
                    String.format("default '%s' value cannot parse to Date with pattern '%s'",
                            strValue, pattern), e);
            
        }
    }
    
    @Override
    public CellProcessor buildOutputCellProcessor(final Class<LocalDate> type, final Annotation[] annos,
            final CellProcessor processor, final boolean ignoreValidationProcessor) {
        
        final Optional<CsvDateConverter> converterAnno = getAnnotation(annos);
        final String pattern = getPattern(converterAnno);
        final ResolverStyle style = getResolverStyle(converterAnno);
        final Locale locale = getLocale(converterAnno);
        final ZoneId zone = getZoneId(converterAnno);
        
        final DateTimeFormatter formatter = createDateTimeFormatter(pattern, style, locale, zone);
        
        final Optional<LocalDate> min = getMin(converterAnno).map(s -> parseTemporal(s, formatter));
        final Optional<LocalDate> max = getMax(converterAnno).map(s -> parseTemporal(s, formatter));
        
        CellProcessor cp = processor;
        cp = (cp == null ? new FmtLocalDate(formatter) : new FmtLocalDate(formatter, cp));
        
        if(!ignoreValidationProcessor) {
            cp = prependRangeProcessor(min, max, formatter, cp);
        }
        
        return cp;
    }
    
    @Override
    public CellProcessor buildInputCellProcessor(final Class<LocalDate> type, final Annotation[] annos,
                final CellProcessor processor) {
        
        final Optional<CsvDateConverter> converterAnno = getAnnotation(annos);
        final String pattern = getPattern(converterAnno);
        final ResolverStyle style = getResolverStyle(converterAnno);
        final Locale locale = getLocale(converterAnno);
        final ZoneId zone = getZoneId(converterAnno);
        
        final DateTimeFormatter formatter = createDateTimeFormatter(pattern, style, locale, zone);
        
        CellProcessor cp = processor;
        cp = (cp == null ? new ParseLocalDate(formatter) : new ParseLocalDate(formatter, cp));
        
        return cp;
        
    }
    
}

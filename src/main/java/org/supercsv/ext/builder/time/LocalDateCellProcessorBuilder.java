package org.supercsv.ext.builder.time;

import java.lang.annotation.Annotation;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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
        return "uuuu-MM-dd";
    }
    
    @Override
    public LocalDate getParseValue(final Class<LocalDate> type, final Annotation[] annos, final String strValue) {
        
        final Optional<CsvDateConverter> converterAnno = getAnnotation(annos);
        final DateTimeFormatter formatter = createDateTimeFormatter(converterAnno);
        
        final String pattern = getPattern(converterAnno);
        
        try {
            return LocalDate.parse(strValue, formatter);
            
        } catch(DateTimeParseException e) {
            throw new SuperCsvInvalidAnnotationException(
                    String.format("default '%s' value cannot parse to LocalDate with pattern '%s'",
                            strValue, pattern), e);
            
        }
    }
    
    @Override
    public CellProcessor buildOutputCellProcessor(final Class<LocalDate> type, final Annotation[] annos,
            final CellProcessor processor, final boolean ignoreValidationProcessor) {
        
        final Optional<CsvDateConverter> converterAnno = getAnnotation(annos);
        final DateTimeFormatter formatter = createDateTimeFormatter(converterAnno);
        
        final Optional<LocalDate> min = getMin(converterAnno).map(s -> getParseValue(type, annos, s));
        final Optional<LocalDate> max = getMax(converterAnno).map(s -> getParseValue(type, annos, s));
        
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
        final DateTimeFormatter formatter = createDateTimeFormatter(converterAnno);
        
        final Optional<LocalDate> min = getMin(converterAnno).map(s -> getParseValue(type, annos, s));
        final Optional<LocalDate> max = getMax(converterAnno).map(s -> getParseValue(type, annos, s));
        
        CellProcessor cp = processor;
        cp = prependRangeProcessor(min, max, formatter, cp);
        
        cp = (cp == null ? new ParseLocalDate(formatter) : new ParseLocalDate(formatter, cp));
        
        return cp;
        
    }
    
}

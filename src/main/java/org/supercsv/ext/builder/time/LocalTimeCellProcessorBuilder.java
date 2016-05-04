package org.supercsv.ext.builder.time;

import java.lang.annotation.Annotation;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;

import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.time.FmtLocalTime;
import org.supercsv.cellprocessor.time.ParseLocalTime;
import org.supercsv.ext.annotation.CsvDateConverter;
import org.supercsv.ext.builder.CellProcessorBuilder;
import org.supercsv.ext.exception.SuperCsvInvalidAnnotationException;
import org.supercsv.ext.util.Utils;

/**
 * {@link LocalTime}型の{@link CellProcessorBuilder}クラス。
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
    public Optional<LocalTime> parseValue(final Class<LocalTime> type, final Annotation[] annos, final String strValue) {
        
        if(Utils.isEmpty(strValue)) {
            return Optional.empty();
        }
        
        final Optional<CsvDateConverter> converterAnno = getDateConverterAnnotation(annos);
        final DateTimeFormatter formatter = createDateTimeFormatter(converterAnno);
        
        final String pattern = getPattern(converterAnno);
        
        try {
            return Optional.of(LocalTime.parse(strValue, formatter));
            
        } catch(DateTimeParseException e) {
            throw new SuperCsvInvalidAnnotationException(
                    String.format("default '%s' value cannot parse to LocalTime with pattern '%s'",
                            strValue, pattern), e);
            
        }
    }
    
    @Override
    public CellProcessor buildOutputCellProcessor(final Class<LocalTime> type, final Annotation[] annos,
            final CellProcessor processor, final boolean ignoreValidationProcessor) {
        
        final Optional<CsvDateConverter> converterAnno = getDateConverterAnnotation(annos);
        final DateTimeFormatter formatter = createDateTimeFormatter(converterAnno);
        
        final Optional<LocalTime> min = getMin(converterAnno).map(s -> parseValue(type, annos, s).get());
        final Optional<LocalTime> max = getMax(converterAnno).map(s -> parseValue(type, annos, s).get());
        
        CellProcessor cp = processor;
        cp = (cp == null ? new FmtLocalTime(formatter) : new FmtLocalTime(formatter, cp));
        
        if(!ignoreValidationProcessor) {
            cp = prependRangeProcessor(type, annos, cp, min, max);
        }
        
        return cp;
    }
    
    @Override
    public CellProcessor buildInputCellProcessor(final Class<LocalTime> type, final Annotation[] annos,
                final CellProcessor processor) {
        
        final Optional<CsvDateConverter> converterAnno = getDateConverterAnnotation(annos);
        final DateTimeFormatter formatter = createDateTimeFormatter(converterAnno);
        
        final Optional<LocalTime> min = getMin(converterAnno).map(s -> parseValue(type, annos, s).get());
        final Optional<LocalTime> max = getMax(converterAnno).map(s -> parseValue(type, annos, s).get());
        
        CellProcessor cp = processor;
        cp = prependRangeProcessor(type, annos, cp, min, max);
        
        cp = (cp == null ? new ParseLocalTime(formatter) : new ParseLocalTime(formatter, cp));
        
        return cp;
        
    }
    
}

package org.supercsv.ext.builder.joda;

import java.lang.annotation.Annotation;
import java.util.Optional;

import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormatter;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.joda.FmtLocalDateTime;
import org.supercsv.cellprocessor.joda.ParseLocalDateTime;
import org.supercsv.ext.annotation.CsvDateConverter;
import org.supercsv.ext.exception.SuperCsvInvalidAnnotationException;
import org.supercsv.ext.util.Utils;

/**
 * Joda-Timeの{@link LocalDateTime}型の{@link CellProcessor}のビルダクラス。
 * 
 * @since 1.2
 * @author T.TSUCHIE
 *
 */
public class LocalDateTimeCellProcessorBuilder extends AbstractJodaCellProcessorBuilder<LocalDateTime> {
    
    @Override
    protected String getDefaultPattern() {
        return "yyyy-MM-dd HH:mm:ss";
    }
    
    @Override
    public Optional<LocalDateTime> parseValue(final Class<LocalDateTime> type, final Annotation[] annos, final String strValue) {
        
        if(Utils.isEmpty(strValue)) {
            return Optional.empty();
        }
        
        final Optional<CsvDateConverter> converterAnno = getDateConverterAnnotation(annos);
        final DateTimeFormatter formatter = createDateTimeFormatter(converterAnno);
        
        final String pattern = getPattern(converterAnno);
        
        try {
            return Optional.of(LocalDateTime.parse(strValue, formatter));
            
        } catch(IllegalArgumentException e) {
            throw new SuperCsvInvalidAnnotationException(
                    String.format("default '%s' value cannot parse to LocalDateTime with pattern '%s'",
                            strValue, pattern), e);
            
        }
    }
    
    @Override
    public CellProcessor buildOutputCellProcessor(final Class<LocalDateTime> type,final  Annotation[] annos,
            final CellProcessor processor, final boolean ignoreValidationProcessor) {
        
        final Optional<CsvDateConverter> converterAnno = getDateConverterAnnotation(annos);
        final DateTimeFormatter formatter = createDateTimeFormatter(converterAnno);
        
        final Optional<LocalDateTime> min = getMin(converterAnno).map(s -> parseValue(type, annos, s).get());
        final Optional<LocalDateTime> max = getMax(converterAnno).map(s -> parseValue(type, annos, s).get());
        
        CellProcessor cp = processor;
        cp = (cp == null ? new FmtLocalDateTime(formatter) : new FmtLocalDateTime(formatter, cp));
        
        if(!ignoreValidationProcessor) {
            cp = prependRangeProcessor(type, annos, cp, min, max);
        }
        
        return cp;
        
    }
    
    @Override
    public CellProcessor buildInputCellProcessor(final Class<LocalDateTime> type, final Annotation[] annos,
            final CellProcessor processor) {
        
        final Optional<CsvDateConverter> converterAnno = getDateConverterAnnotation(annos);
        final DateTimeFormatter formatter = createDateTimeFormatter(converterAnno);
        
        final Optional<LocalDateTime> min = getMin(converterAnno).map(s -> parseValue(type, annos, s).get());
        final Optional<LocalDateTime> max = getMax(converterAnno).map(s -> parseValue(type, annos, s).get());
        
        CellProcessor cp = processor;
        cp = prependRangeProcessor(type, annos, cp, min, max);
        
        cp = (cp == null ? new ParseLocalDateTime(formatter) : new ParseLocalDateTime(formatter, cp));
        
        return cp;
    }
    
    
}

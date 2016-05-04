package org.supercsv.ext.builder.joda;

import java.lang.annotation.Annotation;
import java.util.Optional;

import org.joda.time.DateTimeZone;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.joda.FmtDateTimeZone;
import org.supercsv.cellprocessor.joda.ParseDateTimeZone;
import org.supercsv.ext.builder.AbstractCellProcessorBuilder;
import org.supercsv.ext.exception.SuperCsvInvalidAnnotationException;

/**
 * Joda-Timeの{@link DateTimeZone}型の{@link CellProcessor}のビルダ。
 * @since 1.2
 * @author T.TSUCHIE
 *
 */
public class DateTimeZoneCellProcessorBuilder extends AbstractCellProcessorBuilder<DateTimeZone> {
    
    @Override
    public CellProcessor buildOutputCellProcessor(final Class<DateTimeZone> type, final Annotation[] annos,
            final CellProcessor processor, final boolean ignoreValidationProcessor) {
        
        CellProcessor cp = processor;
        cp = (cp == null ? new FmtDateTimeZone() : new FmtDateTimeZone(cp));
        
        return cp;
    }
    
    @Override
    public CellProcessor buildInputCellProcessor(final Class<DateTimeZone> type, final Annotation[] annos,
            final CellProcessor processor) {
        
        CellProcessor cp = processor;
        cp = (cp == null ? new ParseDateTimeZone() : new ParseDateTimeZone(cp));
        
        return cp;
    }
    
    @Override
    public Optional<DateTimeZone> parseValue(final Class<DateTimeZone> type, final Annotation[] annos, final String strValue) {
        
        try {
            return Optional.of(DateTimeZone.forID(strValue));
            
        } catch(IllegalArgumentException e) {
            throw new SuperCsvInvalidAnnotationException(
                    String.format("default '%s' value cannot parse to DateTimeZone.", strValue), e);
            
        }
    }
}

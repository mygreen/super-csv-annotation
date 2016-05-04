package org.supercsv.ext.builder.joda;

import java.lang.annotation.Annotation;
import java.util.Optional;

import org.joda.time.Duration;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.joda.FmtDuration;
import org.supercsv.cellprocessor.joda.ParseDuration;
import org.supercsv.ext.builder.AbstractCellProcessorBuilder;
import org.supercsv.ext.exception.SuperCsvInvalidAnnotationException;
import org.supercsv.ext.util.Utils;

/**
 * The cell processor builder for {@link Duration} with Joda-Time.
 * @since 1.2
 * @author T.TSUCHIE
 *
 */
public class DurationCellProcessorBuilder extends AbstractCellProcessorBuilder<Duration> {
    
    @Override
    public CellProcessor buildOutputCellProcessor(final Class<Duration> type, final Annotation[] annos,
            CellProcessor processor, boolean ignoreValidationProcessor) {
        
        CellProcessor cp = processor;
        cp = (cp == null ? new FmtDuration() : new FmtDuration(cp));
        
        return cp;
    }
    
    @Override
    public CellProcessor buildInputCellProcessor(final Class<Duration> type, final Annotation[] annos,
            final CellProcessor processor) {
        
        CellProcessor cp = processor;
        cp = (cp == null ? new ParseDuration() : new ParseDuration(cp));
        
        return cp;
    }
    
    @Override
    public Optional<Duration> parseValue(final Class<Duration> type, final Annotation[] annos, final String strValue) {
        
        if(Utils.isEmpty(strValue)) {
            return Optional.empty();
        }
        
        try {
            return Optional.of(Duration.parse(strValue));
            
        } catch(IllegalArgumentException e) {
            throw new SuperCsvInvalidAnnotationException(
                    String.format("default '%s' value cannot parse to Duration.", strValue), e);
            
        }
    }
   
}

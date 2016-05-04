package org.supercsv.ext.builder.joda;

import java.lang.annotation.Annotation;
import java.util.Optional;

import org.joda.time.Interval;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.joda.FmtInterval;
import org.supercsv.cellprocessor.joda.ParseInterval;
import org.supercsv.ext.builder.AbstractCellProcessorBuilder;
import org.supercsv.ext.exception.SuperCsvInvalidAnnotationException;
import org.supercsv.ext.util.Utils;

/**
 * The cell processor builder for {@link Interval} with Joda-Time
 * @since 1.2
 * @author T.TSUCHIE
 *
 */
public class IntervalCellProcessorBuilder extends AbstractCellProcessorBuilder<Interval> {
    
    @Override
    public CellProcessor buildOutputCellProcessor(final Class<Interval> type, final Annotation[] annos,
            final CellProcessor processor, final boolean ignoreValidationProcessor) {
        
        CellProcessor cp = processor;
        cp = (cp == null ? new FmtInterval() : new FmtInterval(cp));
        
        return cp;
    }
    
    @Override
    public CellProcessor buildInputCellProcessor(final Class<Interval> type, final Annotation[] annos,
            final CellProcessor processor) {
        
        CellProcessor cp = processor;
        cp = (cp == null ? new ParseInterval() : new ParseInterval(cp));
        
        return cp;
    }
    
    @Override
    public Optional<Interval> parseValue(final Class<Interval> type, final Annotation[] annos, final String strValue) {
        
        if(Utils.isEmpty(strValue)) {
            return Optional.empty();
        }
        
        try {
            return Optional.of(Interval.parse(strValue));
            
        } catch(IllegalArgumentException e) {
            throw new SuperCsvInvalidAnnotationException(
                    String.format("default '%s' value cannot parse to Interval.", strValue), e);
            
        }
    }
}

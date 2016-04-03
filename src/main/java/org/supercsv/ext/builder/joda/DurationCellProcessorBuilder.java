package org.supercsv.ext.builder.joda;

import java.lang.annotation.Annotation;

import org.joda.time.Duration;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.joda.FmtDuration;
import org.supercsv.cellprocessor.joda.ParseDuration;
import org.supercsv.ext.builder.AbstractCellProcessorBuilder;

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
    public Duration getParseValue(final Class<Duration> type, final Annotation[] annos, final String strValue) {
        return Duration.parse(strValue);
    }
   
}

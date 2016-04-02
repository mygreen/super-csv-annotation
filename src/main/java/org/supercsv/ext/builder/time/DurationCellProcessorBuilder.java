package org.supercsv.ext.builder.time;

import java.lang.annotation.Annotation;
import java.time.Duration;

import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.time.FmtDuration;
import org.supercsv.cellprocessor.time.ParseDuration;
import org.supercsv.ext.builder.AbstractCellProcessorBuilder;

/**
 *
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

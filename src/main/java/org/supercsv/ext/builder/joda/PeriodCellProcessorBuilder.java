package org.supercsv.ext.builder.joda;

import java.lang.annotation.Annotation;

import org.joda.time.Interval;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.joda.FmtInterval;
import org.supercsv.cellprocessor.joda.ParseInterval;
import org.supercsv.ext.builder.AbstractCellProcessorBuilder;

/**
 * The cell processor builder for {@link Interval} with Joda-Time
 * @since 1.2
 * @author T.TSUCHIE
 *
 */
public class PeriodCellProcessorBuilder extends AbstractCellProcessorBuilder<Interval> {

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
    public Interval getParseValue(final Class<Interval> type, final Annotation[] annos, final String strValue) {
        return Interval.parse(strValue);
    }
}

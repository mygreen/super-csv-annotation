package org.supercsv.ext.builder.time;

import java.lang.annotation.Annotation;
import java.time.ZoneId;

import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.time.FmtZoneId;
import org.supercsv.cellprocessor.time.ParseZoneId;
import org.supercsv.ext.builder.AbstractCellProcessorBuilder;

/**
 *
 * @since 1.2
 * @author T.TSUCHIE
 *
 */
public class ZoneIdCellProcessorBuilder extends AbstractCellProcessorBuilder<ZoneId> {
    
    @Override
    public CellProcessor buildOutputCellProcessor(final Class<ZoneId> type, final Annotation[] annos,
            final CellProcessor processor, boolean ignoreValidationProcessor) {
        
        CellProcessor cp = processor;
        cp = (cp == null ? new FmtZoneId() : new FmtZoneId(cp));
        
        return cp;
    }
    
    @Override
    public CellProcessor buildInputCellProcessor(final Class<ZoneId> type, final Annotation[] annos,
            final CellProcessor processor) {
        
        CellProcessor cp = processor;
        cp = (cp == null ? new ParseZoneId() : new ParseZoneId(cp));
        
        return cp;
    }
    
    @Override
    public ZoneId getParseValue(final Class<ZoneId> type, final Annotation[] annos, final String strValue) {
        return ZoneId.of(strValue);
    }
}

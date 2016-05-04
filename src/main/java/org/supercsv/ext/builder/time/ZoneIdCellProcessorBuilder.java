package org.supercsv.ext.builder.time;

import java.lang.annotation.Annotation;
import java.time.DateTimeException;
import java.time.ZoneId;
import java.util.Optional;

import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.time.FmtZoneId;
import org.supercsv.cellprocessor.time.ParseZoneId;
import org.supercsv.ext.builder.AbstractCellProcessorBuilder;
import org.supercsv.ext.exception.SuperCsvInvalidAnnotationException;
import org.supercsv.ext.util.Utils;

/**
 * {@link ZonedId}型の{@link CellProcessorBuilder}クラス。
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
    public Optional<ZoneId> parseValue(final Class<ZoneId> type, final Annotation[] annos, final String strValue) {
        
        if(Utils.isEmpty(strValue)) {
            return Optional.empty();
        }
        
        try {
            return Optional.of(ZoneId.of(strValue));
            
        } catch(DateTimeException e) {
            throw new SuperCsvInvalidAnnotationException(
                    String.format("default '%s' value cannot parse to ZoneId.", strValue), e);
            
        }
    }
}

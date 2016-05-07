package org.supercsv.ext.cellprocessor.constraint;

import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.cellprocessor.ift.DateCellProcessor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;
import org.supercsv.util.CsvContext;

/**
 * CellProcessor to user for unit tests that test chaining.
 * <p>simply return whatever is passed into it.
 * 
 * @since 1.2
 * @author T.TSUCHIE
 *
 */
public class NextCellProcessor extends CellProcessorAdaptor implements DateCellProcessor, StringCellProcessor {
    
    public NextCellProcessor() {
        super();
    }
    
    @Override
    public <T> T execute(final Object value, final CsvContext context) {
        return next.execute(value, context);
    }
}

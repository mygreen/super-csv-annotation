package com.github.mygreen.supercsv.cellprocessor.conversion;

import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;
import org.supercsv.util.CsvContext;

/**
 * 大文字に変換するCellProcessor
 * 
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class Upper extends CellProcessorAdaptor implements StringCellProcessor {
    
    public Upper() {
        super();
    }
    
    public Upper(final StringCellProcessor next) {
        super(next);
    }
    
    @Override
    public <T> T execute(final Object value, final CsvContext context) {
        
        if(value == null) {
            return next.execute(value, context);
        }
        
        final String result = value.toString().toUpperCase();
        return next.execute(result, context);
    }
    
}

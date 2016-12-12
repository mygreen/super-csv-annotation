package com.github.mygreen.supercsv.cellprocessor.conversion;

import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;
import org.supercsv.util.CsvContext;

/**
 * 一致する語彙を置換するCellProcessor。
 * 
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class WordReplace extends CellProcessorAdaptor implements StringCellProcessor {
    
    private final CharReplacer replacer;
    
    public WordReplace(final CharReplacer replacer) {
        super();
        checkPreconditions(replacer);
        this.replacer = replacer;
    }
    
    public WordReplace(final CharReplacer replacer, final StringCellProcessor next) {
        super(next);
        checkPreconditions(replacer);
        this.replacer = replacer;
    }
    
    private static void checkPreconditions(final CharReplacer replacer) {
        if(replacer == null) {
            throw new NullPointerException("replacer should not be null.");
        }
    }
    
    @Override
    public <T> T execute(final Object value, final CsvContext context) {
        
        if(value == null) {
            return next.execute(value, context);
        }
        
        final String result = replacer.replace(value.toString());
        return next.execute(result, context);
    }
    
}

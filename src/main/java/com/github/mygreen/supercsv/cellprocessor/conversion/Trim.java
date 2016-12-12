package com.github.mygreen.supercsv.cellprocessor.conversion;

import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;
import org.supercsv.util.CsvContext;


/**
 * 文字列をトリムするCellProcessor。
 * <p>値がNullの時も処理を続行する</p>
 * 
 * @since 1.0.2
 * @author T.TSUCHIE
 *
 */
public class Trim extends CellProcessorAdaptor implements StringCellProcessor {
    
    /**
     * Constructs a new <tt>Trim</tt> processor, which trims a String to ensure it has no surrounding whitespace.
     */
    public Trim() {
        super();
    }
    
    /**
     * Constructs a new <tt>Trim</tt> processor, which trims a String to ensure it has no surrounding whitespace then
     * calls the next processor in the chain.
     * 
     * @param next the next processor in the chain
     * @throws NullPointerException {@literal if next is null}
     */
    public Trim(final StringCellProcessor next) {
        super(next);
    }
    
    @SuppressWarnings("unchecked")
    public Object execute(final Object value, final CsvContext context) {
        
        if(value == null) {
            return next.execute(value, context);
        }
        
        final String result = value.toString().trim();
        return next.execute(result, context);
    }
    
}

package org.supercsv.ext.cellprocessor;

import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.cellprocessor.ift.BoolCellProcessor;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.ift.DateCellProcessor;
import org.supercsv.cellprocessor.ift.DoubleCellProcessor;
import org.supercsv.cellprocessor.ift.LongCellProcessor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;
import org.supercsv.util.CsvContext;


/**
 * Ensure that Strings or String-representations of objects are trimmed (contain no surrounding whitespace).
 * <p>can be used {@link StringCellProcessor} but also{@link CellProcessor}.
 * 
 * @since 1.0.2
 * @author T.TSUCHIE
 *
 */
public class Trim extends CellProcessorAdaptor implements BoolCellProcessor, DateCellProcessor, DoubleCellProcessor,
        LongCellProcessor, StringCellProcessor {
    
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
     * @throws NullPointerException if next is null
     */
    public Trim(final CellProcessor next) {
        super(next);
    }
    
    /**
     * {@inheritDoc}
     * 
     * @throws SuperCsvCellProcessorException if value is null
     */
    @SuppressWarnings("unchecked")
    public Object execute(final Object value, final CsvContext context) {
        validateInputNotNull(value, context);
        
        final String result = value.toString().trim();
        return next.execute(result, context);
    }
    
}

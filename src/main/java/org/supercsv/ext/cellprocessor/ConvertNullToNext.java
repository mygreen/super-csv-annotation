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
 * Converts null to given value.
 * 
 * @author T.TSUCHIE
 *
 */
public class ConvertNullToNext extends CellProcessorAdaptor implements BoolCellProcessor, DateCellProcessor,
        DoubleCellProcessor, LongCellProcessor, StringCellProcessor {
    
    protected final Object returnValue;
    
    /**
     * 
     * @param returnValue converts value.
     */
    public ConvertNullToNext(final Object returnValue) {
        super();
        this.returnValue = returnValue;
    }
    
    /**
     * 
     * @param returnValue converts value.
     * @param next the next processor in the chain
     */
    public ConvertNullToNext(final Object returnValue, final CellProcessor next) {
        super(next);
        this.returnValue = returnValue;
    }
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public Object execute(final Object value, final CsvContext context) {
        if( value == null ) {
            return next.execute(returnValue, context);
        }
        
        return next.execute(value, context);
    }
    
    
}

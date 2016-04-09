package org.supercsv.ext.cellprocessor;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.cellprocessor.FmtNumber;
import org.supercsv.cellprocessor.ift.DoubleCellProcessor;
import org.supercsv.cellprocessor.ift.LongCellProcessor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;
import org.supercsv.exception.SuperCsvCellProcessorException;
import org.supercsv.ext.cellprocessor.ift.ValidationCellProcessor;
import org.supercsv.ext.util.NumberFormatWrapper;
import org.supercsv.util.CsvContext;


/**
 *
 * @see {@link FmtNumber}
 * @author T.TSUCHIE
 *
 */
public class FormatLocaleNumber extends CellProcessorAdaptor
        implements DoubleCellProcessor, LongCellProcessor, ValidationCellProcessor {
    
    protected final NumberFormatWrapper formatter;
    
    public FormatLocaleNumber(final NumberFormat formatter) {
        super();
        checkPreconditions(formatter);
        this.formatter = new NumberFormatWrapper(formatter);
    }
    
    public FormatLocaleNumber(final NumberFormat formatter, final StringCellProcessor next) {
        super(next);
        checkPreconditions(formatter);
        this.formatter = new NumberFormatWrapper(formatter);
        
    }
    
    /**
     * Checks the preconditions for creating a new ParseDate processor.
     * @throws NullPointerException formatter is null.
     * 
     */
    protected static void checkPreconditions(final NumberFormat formatter) {
        if(formatter == null) {
            throw new NullPointerException("formatter is null.");
        }
        
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public Object execute(final Object value, final CsvContext context) {
        
        validateInputNotNull(value, context);
        
        if(!(value instanceof Number)) {
            throw new SuperCsvCellProcessorException(Number.class, value, context, this);
        }
        
        String result = formatter.format((Number) value);
        return next.execute(result, context);
    }
    
    @Override
    public String getMessageCode() {
        return FormatLocaleNumber.class.getCanonicalName() + ".violated";
    }
    
    @Override
    public Map<String, ?> getMessageVariable() {
        Map<String, Object> vars = new HashMap<String, Object>();
        
        return vars;
    }
    
    @Override
    public String formatValue(final Object value) {
        if(value == null) {
            return "";
        }
        
        if(value instanceof Number) {
            final Number number = (Number) value;
            return formatter.format(number);
            
        }
        
        return value.toString();
        
    }

}

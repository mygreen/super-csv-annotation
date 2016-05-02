package org.supercsv.ext.cellprocessor;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.cellprocessor.ParseDate;
import org.supercsv.cellprocessor.ift.DateCellProcessor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;
import org.supercsv.exception.SuperCsvCellProcessorException;
import org.supercsv.ext.cellprocessor.ift.ValidationCellProcessor;
import org.supercsv.ext.util.DateFormatWrapper;
import org.supercsv.util.CsvContext;


/**
 * 
 * @version 1.2
 * @since 1.0.0
 * @see {@link ParseDate}
 * @author T.TSUCHIE
 *
 */
public class ParseLocaleDate  extends CellProcessorAdaptor
        implements StringCellProcessor, ValidationCellProcessor {
    
    protected final DateFormatWrapper formatter;
    
    public ParseLocaleDate(final DateFormat formatter) {
        super();
        checkPreconditions(formatter);
        this.formatter = new DateFormatWrapper(formatter);
    }
    
    public ParseLocaleDate(final DateFormat formatter, final DateCellProcessor next) {
        super(next);
        checkPreconditions(formatter);
        this.formatter = new DateFormatWrapper(formatter);
    }
    
    /**
     * Checks the preconditions for creating a new ParseDate processor.
     * @throws NullPointerException formatter is null.
     * 
     */
    protected static void checkPreconditions(final DateFormat formatter) {
        if(formatter == null) {
            throw new NullPointerException("formatter is null.");
        }
    }
    
    /**
     * {@inheritDoc}
     * 
     * @throws SuperCsvCellProcessorException
     *             if value is null, isn't a String, or can't be parsed to a Date.a
     */
    @SuppressWarnings("unchecked")
    @Override
    public Object execute(final Object value, final CsvContext context) {
        validateInputNotNull(value, context);
        
        if( !(value instanceof String) ) {
            throw new SuperCsvCellProcessorException(String.class, value, context, this);
        }
        
        try {
            Date result = parse((String) value);
            return next.execute(result, context);
            
        } catch(ParseException e) {
            throw new SuperCsvCellProcessorException(
                    String.format("'%s' could not be parsed as a Date", value),
                    context, this, e);
        }
    }
    
    protected Date parse(final String value) throws ParseException {
        return formatter.parse((String) value);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, ?> getMessageVariable() {
        Map<String, Object> vars = new HashMap<String, Object>();
        vars.put("pattern", formatter.getPattern());
        
        return vars;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String formatValue(final Object value) {
        if(value == null) {
            return "";
        }
        
        if(value instanceof Date) {
            final Date date = (Date) value;
            return formatter.format(date);
            
        }
        
        return value.toString();
    }

}

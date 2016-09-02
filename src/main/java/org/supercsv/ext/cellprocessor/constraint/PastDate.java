package org.supercsv.ext.cellprocessor.constraint;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.ift.DateCellProcessor;
import org.supercsv.exception.SuperCsvCellProcessorException;
import org.supercsv.exception.SuperCsvConstraintViolationException;
import org.supercsv.ext.cellprocessor.ift.ValidationCellProcessor;
import org.supercsv.ext.util.DateFormatWrapper;
import org.supercsv.util.CsvContext;


/**
 * 指定した日時より過去かどうかチェックするプロセッサ。
 * <p>{@link Max}の日時用。
 * 
 * @author T.TSUCHIE
 *
 */
public class PastDate<T extends Date> extends CellProcessorAdaptor implements DateCellProcessor, ValidationCellProcessor {
    
    protected final T max;
    
    protected DateFormat formatter;
    
    public PastDate(final T max) {
        super();
        checkPreconditions(max);
        this.max = max;
    }
    
    public PastDate(final T max, final CellProcessor next) {
        super(next);
        checkPreconditions(max);
        this.max = max;
    }
    
    protected static <T extends Date> void checkPreconditions(final T max) {
        if(max == null) {
            throw new NullPointerException("max should not be null");
        }
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public Object execute(final Object value, final CsvContext context) {
        
        validateInputNotNull(value, context);
        
        if(!Date.class.isAssignableFrom(value.getClass())) {
            throw new SuperCsvCellProcessorException(Date.class, value, context, this);
        }
        
        final T result = ((T) value);
        
        if(result.compareTo(max) > 0) {
            throw new SuperCsvConstraintViolationException(
                    String.format("%s does not lie the max (%s) values (inclusive)", result, max),
                    context, this);
        }
            
        return next.execute(result, context);
    }
    
    public T getMax() {
        return max;
    }
    
    public DateFormat getFormatter() {
        return formatter;
    }
    
    public void setFormatter(DateFormat formatter) {
        this.formatter = formatter;
    }
    
    @Override
    public Map<String, ?> getMessageVariable() {
        final Map<String, Object> vars = new HashMap<String, Object>();
        vars.put("max", getMax());
        return vars;
    }
    
    @Override
    public String formatValue(final Object value) {
        if(value == null) {
            return "";
        }
        
        if(value instanceof Date) {
            final Date date = (Date) value;
            final DateFormatWrapper df;
            if(getFormatter() != null) {
                df = new DateFormatWrapper(getFormatter());
            } else {
                df = new DateFormatWrapper(date.getClass());
            }
            
            return df.format(date);
            
        }
        
        return value.toString();
        
    }
    
}

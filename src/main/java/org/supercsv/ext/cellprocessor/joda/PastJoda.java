package org.supercsv.ext.cellprocessor.joda;

import java.util.HashMap;
import java.util.Map;

import org.joda.time.ReadablePartial;
import org.joda.time.format.DateTimeFormatter;
import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.ift.DateCellProcessor;
import org.supercsv.exception.SuperCsvCellProcessorException;
import org.supercsv.exception.SuperCsvConstraintViolationException;
import org.supercsv.ext.cellprocessor.ift.ValidationCellProcessor;
import org.supercsv.util.CsvContext;

/**
 *
 * @since 1.2
 * @author T.TSUCHIE
 *
 */
public class PastJoda <T extends ReadablePartial>
        extends CellProcessorAdaptor implements DateCellProcessor, ValidationCellProcessor {
    
    private final T max;
    
    private DateTimeFormatter formatter;
    
    public PastJoda(final T max) {
        super();
        checkPreconditions(max);
        this.max = max;
    }
    
    public PastJoda(final T max, final CellProcessor next) {
        super(next);
        checkPreconditions(max);
        this.max = max;
    }
    
    private static <T extends ReadablePartial> void checkPreconditions(final T max) {
        if(max == null) {
            throw new NullPointerException("max should not be null");
        }
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public Object execute(final Object value, final CsvContext context) {
        
        validateInputNotNull(value, context);
        
        final Class<?> exepectedClass = getMax().getClass();
        if(!exepectedClass.isAssignableFrom(value.getClass())) {
            throw new SuperCsvCellProcessorException(exepectedClass, value, context, this);
        }
        
        final T result = (T) value;
        
        if(result.compareTo(max) > 0) {
            throw new SuperCsvConstraintViolationException(
                    String.format("%s does not lie the max (%s) values (inclusive)", result, max),
                    context, this);
        }
        
        return next.execute(result, context);
        
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
        
        if(value instanceof ReadablePartial) {
            final ReadablePartial rp = (ReadablePartial) value;
            if(getFormatter() != null) {
                return getFormatter().print(rp);
            }
        }
        
        return value.toString();
    }
    
    /**
     * the value for maximum.
     * @return
     */
    public T getMax() {
        return max;
    }
    
    public DateTimeFormatter getFormatter() {
        return formatter;
    }
    
    public void setFormatter(DateTimeFormatter formatter) {
        this.formatter = formatter;
    }
}

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
public class FutureJoda <T extends ReadablePartial>
        extends CellProcessorAdaptor implements DateCellProcessor, ValidationCellProcessor {
    
    private final T min;
    
    private DateTimeFormatter formatter;
    
    public FutureJoda(final T min) {
        super();
        checkPreconditions(min);
        this.min = min;
    }
    
    public FutureJoda(final T min, final CellProcessor next) {
        super(next);
        checkPreconditions(min);
        this.min = min;
    }
    
    private static <T extends ReadablePartial> void checkPreconditions(final T min) {
        if(min == null) {
            throw new NullPointerException("min should not be null");
        }
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public T execute(final Object value, final CsvContext context) {
        
        validateInputNotNull(value, context);
        
        final Class<?> exepectedClass = getMin().getClass();
        if(!exepectedClass.isAssignableFrom(value.getClass())) {
            throw new SuperCsvCellProcessorException(exepectedClass, value, context, this);
        }
        
        final T result = ((T) value);
        
        if(result.compareTo(min) < 0) {
            throw new SuperCsvConstraintViolationException(
                    String.format("%s does not lie the min (%s) values (inclusive)", result, min),
                    context, this);
        }
        
        return next.execute(result, context);
        
    }
    
    @Override
    public String getMessageCode() {
        return FutureJoda.class.getCanonicalName() + ".violated";
    }
    
    @Override
    public Map<String, ?> getMessageVariable() {
        final Map<String, Object> vars = new HashMap<String, Object>();
        vars.put("min", getMin());
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
     * the value for minimum.
     * @return
     */
    public T getMin() {
        return min;
    }
    
    public DateTimeFormatter getFormatter() {
        return formatter;
    }
    
    public FutureJoda<T> setFormatter(DateTimeFormatter formatter) {
        this.formatter = formatter;
        return this;
    }
}

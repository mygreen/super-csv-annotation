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
public class JodaRange <T extends ReadablePartial>
        extends CellProcessorAdaptor implements DateCellProcessor, ValidationCellProcessor {
    
    private final T min;
    
    private final T max;
    
    private DateTimeFormatter formatter;
    
    public JodaRange(final T min, final T max) {
        super();
        checkPreconditions(min, max);
        this.min = min;
        this.max = max;
    }
    
    public JodaRange(final T min, final T max, final CellProcessor next) {
        super(next);
        checkPreconditions(min, max);
        this.min = min;
        this.max = max;
    }
    
    private static <T extends ReadablePartial> void checkPreconditions(final T min, final T max) {
        if(min == null || max == null) {
            throw new NullPointerException("min and max should not be null");
        }
        
        if(min.compareTo(max) > 0) {
            throw new IllegalArgumentException(String.format("max (%s) should not be < min (%s)", max, min));
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
        
        final T result = ((T) value);
        
        if(result.compareTo(min) < 0 || result.compareTo(max) > 0) {
            throw new SuperCsvConstraintViolationException(
                    String.format("%s does not lie between the min (%s) and max (%s) values (inclusive)", result, min, max),
                    context, this);
        }
        
        return next.execute(result, context);
        
    }
    
    @Override
    public Map<String, ?> getMessageVariable() {
        final Map<String, Object> vars = new HashMap<String, Object>();
        vars.put("min", getMin());
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
     * the value for minimum.
     * @return
     */
    public T getMin() {
        return min;
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

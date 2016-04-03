package org.supercsv.ext.cellprocessor.joda;

import java.util.HashMap;
import java.util.Map;

import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.ift.DateCellProcessor;
import org.supercsv.exception.SuperCsvConstraintViolationException;
import org.supercsv.ext.cellprocessor.ift.ValidationCellProcessor;
import org.supercsv.util.CsvContext;

/**
 *
 * @since 1.2
 * @author T.TSUCHIE
 *
 */
public class FutureJoda <T extends Comparable<? super T>>
        extends CellProcessorAdaptor implements DateCellProcessor, ValidationCellProcessor {
    
    private final T min;
    
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
    
    private static <T extends Comparable<? super T>> void checkPreconditions(final T min) {
        if(min == null) {
            throw new IllegalArgumentException("min should not be null");
        }
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public T execute(final Object value, final CsvContext context) {
        
        validateInputNotNull(value, context);
        
        final Class<?> exepectedClass = getMin().getClass();
        if(!exepectedClass.isAssignableFrom(value.getClass())) {
            throw new SuperCsvConstraintViolationException(
                    String.format("the value '%s' could not implements '%s' class.", value, exepectedClass.getCanonicalName()),
                    context, this);
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
        
        return value.toString();
    }
    
    /**
     * the value for minimum.
     * @return
     */
    public T getMin() {
        return min;
    }
}

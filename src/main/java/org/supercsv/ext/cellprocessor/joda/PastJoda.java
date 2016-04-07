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
public class PastJoda <T extends Comparable<? super T>>
        extends CellProcessorAdaptor implements DateCellProcessor, ValidationCellProcessor {
    
    private final T max;
    
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
    
    private static <T extends Comparable<? super T>> void checkPreconditions(final T max) {
        if(max == null) {
            throw new NullPointerException("max should not be null");
        }
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public T execute(final Object value, final CsvContext context) {
        
        validateInputNotNull(value, context);
        
        final Class<?> exepectedClass = getMax().getClass();
        if(!exepectedClass.isAssignableFrom(value.getClass())) {
            throw new SuperCsvConstraintViolationException(
                    String.format("the value '%s' could not implements '%s' class.", value, exepectedClass.getCanonicalName()),
                    context, this);
        }
        
        final T result = ((T) value);
        
        if(result.compareTo(max) > 0) {
            throw new SuperCsvConstraintViolationException(
                    String.format("%s does not lie the max (%s) values (inclusive)", result, max),
                    context, this);
        }
        
        return next.execute(result, context);
        
    }
    
    @Override
    public String getMessageCode() {
        return PastJoda.class.getCanonicalName() + ".violated";
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
        
        return value.toString();
    }
    
    /**
     * the value for maximum.
     * @return
     */
    public T getMax() {
        return max;
    }
}

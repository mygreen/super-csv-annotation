package org.supercsv.ext.cellprocessor.time;

import java.time.temporal.TemporalAccessor;
import java.util.HashMap;
import java.util.Map;

import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.ift.DateCellProcessor;
import org.supercsv.exception.SuperCsvConstraintViolationException;
import org.supercsv.ext.cellprocessor.ift.ValidationCellProcessor;
import org.supercsv.util.CsvContext;

/**
 * Constraint CellProcessor that {@link TemporalAccessor}(ex LocalDate, LocaleTime, LocalDateTime) is before date (inclusive).
 * @since 1.4
 * @author T.TSUCHIE
 *
 */
public class PastTemporal<T extends TemporalAccessor & Comparable<? super T>>
        extends CellProcessorAdaptor implements DateCellProcessor, ValidationCellProcessor {
    
    protected final T max;
    
    public PastTemporal(final T max) {
        super();
        checkPreconditions(max);
        this.max = max;
    }
    
    public PastTemporal(final T max, final CellProcessor next) {
        super(next);
        checkPreconditions(max);
        this.max = max;
    }
    
    protected static <T extends TemporalAccessor & Comparable<? super T>> void checkPreconditions(final T max) {
        if(max == null) {
            throw new IllegalArgumentException("max should not be null");
        }
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public Object execute(final Object value, final CsvContext context) {
        
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
        return PastTemporal.class.getCanonicalName() + ".violated";
    }
    
    @Override
    public Map<String, ?> getMessageVariable() {
        Map<String, Object> vars = new HashMap<String, Object>();
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
    
    public T getMax() {
        return max;
    }
    
}

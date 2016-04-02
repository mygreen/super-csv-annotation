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
 * Constraint CellProcessor that {@link TemporalAccessor}(ex LocalDate, LocaleTime, LocalDateTime is after date (inclusive). 
 * 
 * @param <T> type of {@link TemporalAccessor}(ex LocalDate, LocaleTime, LocalDateTime)
 * @since 1.2
 * @author T.TSUCHIE
 *
 */
public class FutureTemporal<T extends TemporalAccessor & Comparable<? super T>>
        extends CellProcessorAdaptor implements DateCellProcessor, ValidationCellProcessor {
    
    private final T min;
    
    /**
     * Constructs a new <tt>{@link FutureTemporal}</tt> processor.
     * @param min the minimum date (lower bound) (invalusive).
     * @throws IllegalArgumentException if {@literal min == null}
     */
    public FutureTemporal(final T min) {
        super();
        checkPreconditions(min);
        this.min = min;
    }
    
    /**
     * Constructs a new <tt>{@link FutureTemporal}</tt> processor. then calls the next processor in the chain.
     * @param min the minimum date (lower bound) (invalusive).
     * @param next the next processor in the chain
     * @throws IllegalArgumentException if min is null
     * @throws NullPointerException if next is null.
     */
    public FutureTemporal(final T min, final CellProcessor next) {
        super(next);
        checkPreconditions(min);
        this.min = min;
    }
    
    private static <T extends TemporalAccessor & Comparable<? super T>> void checkPreconditions(final T min) {
        if(min == null) {
            throw new IllegalArgumentException("min should not be null");
        }
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public Object execute(final Object value, final CsvContext context) {
        
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
        return FutureTemporal.class.getCanonicalName() + ".violated";
    }
    
    @Override
    public Map<String, ?> getMessageVariable() {
        Map<String, Object> vars = new HashMap<String, Object>();
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

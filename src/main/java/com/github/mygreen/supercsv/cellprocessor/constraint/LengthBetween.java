package com.github.mygreen.supercsv.cellprocessor.constraint;

import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;
import org.supercsv.exception.SuperCsvCellProcessorException;
import org.supercsv.exception.SuperCsvConstraintViolationException;
import org.supercsv.util.CsvContext;

import com.github.mygreen.supercsv.cellprocessor.ValidationCellProcessor;


/**
 * 文字列長が範囲であるか検証するCellProcessor.
 * 
 * @version 2.0
 * @author T.TSUCHIE
 *
 */
public class LengthBetween extends ValidationCellProcessor implements StringCellProcessor {
    
    private final int min;
    
    private final int max;
    
    public LengthBetween(final int min, final int max) {
        super();
        checkPreconditions(min, max);
        this.min = min;
        this.max = max;
    }
    
    public LengthBetween(final int min, final int max, final CellProcessor next) {
        super(next);
        checkPreconditions(min, max);
        this.min = min;
        this.max = max;
    }
    
    /**
     * Checks the preconditions for creating a new {@link LengthBetween} processor.
     * 
     * @param min
     *            the minimum String length
     * @param max
     *            the maximum String length
     * @throws IllegalArgumentException
     *             {@literal if max < min, or min is < 0}
     */
    private static void checkPreconditions(final int min, final int max) {
        if( min > max ) {
            throw new IllegalArgumentException(String.format("max (%d) should not be < min (%d)", max, min));
        }
        
        if( min < 0 ) {
            throw new IllegalArgumentException(String.format("min length (%d) should not be < 0", min));
        }
    }
    
    /**
     * {@inheritDoc}
     * 
     * @throws SuperCsvCellProcessorException
     *             {@literal if value is null}
     * @throws SuperCsvConstraintViolationException
     *             {@literal if length is < min or length > max}
     */
    @SuppressWarnings("unchecked")
    public Object execute(final Object value, final CsvContext context) {
        
        if(value == null) {
            return next.execute(value, context);
        }
        
        final String stringValue = value.toString();
        final int length = stringValue.length();
        if( length < min || length > max ) {
            throw createValidationException(context)
                .messageFormat("the length (%d) of value '%s' does not lie between the min (%d) and max (%d) values (inclusive)",
                        length, stringValue, min, max)
                .rejectedValue(stringValue)
                .messageVariables("min", getMin())
                .messageVariables("max", getMax())
                .messageVariables("length", length)
                .build();
                
        }
        
        return next.execute(stringValue, context);
    }
    
    /**
     * 
     * @return 設定された最小文字長を取得する
     */
    public int getMin() {
        return min;
    }
    
    /**
     * 
     * @return 設定された最大文字長を取得する
     */
    public int getMax() {
        return max;
    }

}

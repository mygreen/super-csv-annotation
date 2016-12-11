package com.github.mygreen.supercsv.cellprocessor.constraint;

import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;
import org.supercsv.exception.SuperCsvCellProcessorException;
import org.supercsv.exception.SuperCsvConstraintViolationException;
import org.supercsv.util.CsvContext;

import com.github.mygreen.supercsv.cellprocessor.ValidationCellProcessor;


/**
 * 文字列が最小文字以上か検証するCellProcessor.
 * 
 * @version 2.0
 * @author T.TSUCHIE
 *
 */
public class LengthMin extends ValidationCellProcessor implements StringCellProcessor {
    
    private final int min;
    
    public LengthMin(final int min) {
        super();
        checkPreconditions(min);
        this.min = min;
    }
    
    public LengthMin(final int min, final CellProcessor next) {
        super(next);
        checkPreconditions(min);
        this.min = min;
    }
    
    /**
     * Checks the preconditions for creating a new {@link LengthMin} processor.
     * 
     * @param min
     *            the minimum String length
     * @throws IllegalArgumentException
     *             {@literal if min is < 0}
     */
    private static void checkPreconditions(final int min) {
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
        if( length < min ) {
            throw createValidationException(context)
                .messageFormat("the length (%d) of value '%s' does not lie the min (%d) values (inclusive)",
                        length, stringValue, min)
                .rejectedValue(stringValue)
                .messageVariables("min", getMin())
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
    
}

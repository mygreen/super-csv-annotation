package com.github.mygreen.supercsv.cellprocessor.constraint;

import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;
import org.supercsv.exception.SuperCsvCellProcessorException;
import org.supercsv.exception.SuperCsvConstraintViolationException;
import org.supercsv.util.CsvContext;

import com.github.mygreen.supercsv.cellprocessor.ValidationCellProcessor;


/**
 * 文字列が最大長以下か検証するCellProcessor.
 * 
 * @version 2.0
 * @author T.TSUCHIE
 *
 */
public class LengthMax extends ValidationCellProcessor implements StringCellProcessor {
    
    private final int max;
    
    public LengthMax(final int max) {
        super();
        checkPreconditions(max);
        this.max = max;
    }
    
    public LengthMax(final int max, final CellProcessor next) {
        super(next);
        checkPreconditions(max);
        this.max = max;
    }
    
    /**
     * Checks the preconditions for creating a new {@link LengthMax} processor.
     * 
     * @param max
     *            the maximum String length
     * @throws IllegalArgumentException
     *             {@literal if min is < 0}
     */
    private static void checkPreconditions(final int max) {
        if( max <= 0 ) {
            throw new IllegalArgumentException(String.format("max length (%d) should not be <= 0", max));
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
        if( length > max ) {
            throw createValidationException(context)
                .messageFormat("the length (%d) of value '%s' does not lie the max (%d) values (inclusive)",
                        length, stringValue, max)
                .rejectedValue(stringValue)
                .messageVariables("max", getMax())
                .messageVariables("length", length)
                .build();
                
        }
        
        return next.execute(stringValue, context);
    }
    
    /**
     * 
     * @return 設定された最大文字長を取得する
     */
    public int getMax() {
        return max;
    }

}

package com.github.mygreen.supercsv.cellprocessor.constraint;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;
import org.supercsv.exception.SuperCsvCellProcessorException;
import org.supercsv.exception.SuperCsvConstraintViolationException;
import org.supercsv.util.CsvContext;

import com.github.mygreen.supercsv.cellprocessor.ValidationCellProcessor;
import com.github.mygreen.supercsv.util.Utils;


/**
 * 文字列の長さを検証するCellProcessor.
 * 
 * @version 2.0
 * @author T.TSUCHIE
 *
 */
public class LengthExact extends ValidationCellProcessor implements StringCellProcessor {
    
    private final Set<Integer> requriedLengths = new TreeSet<>();
    
    public LengthExact(final Collection<Integer> requriedLengths) {
        super();
        checkPreconditions(requriedLengths);
        this.requriedLengths.addAll(requriedLengths);
    }
    
    public LengthExact(final Collection<Integer> requriedLengths, final CellProcessor next) {
        super(next);
        checkPreconditions(requriedLengths);
        this.requriedLengths.addAll(requriedLengths);
    }
    
    /**
     * Checks the preconditions for creating a new {@link LengthExact} processor.
     * 
     * @param requriedLengths
     *            one or more required lengths
     * @throws NullPointerException
     *             if requiredLengths is null
     * @throws IllegalArgumentException
     *             if requiredLengths is empty
     */
    private static void checkPreconditions(final Collection<Integer> requriedLengths) {
        if( requriedLengths == null ) {
            throw new NullPointerException("requriedLengths should not be null");
        } else if( requriedLengths.isEmpty()) {
            throw new IllegalArgumentException("requriedLengths should not be empty");
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
        
        if( !requriedLengths.contains(length)) {
            final String joinedLength = requriedLengths.stream()
                    .map(String::valueOf).collect(Collectors.joining(", "));
            
            throw createValidationException(context)
                .messageFormat("the length (%d) of value '%s' not any of required lengths (%s)",
                        length, stringValue, joinedLength)
                .rejectedValue(stringValue)
                .messageVariables("length", length)
                .messageVariables("requiredLengths", getRequiredLengths())
                .build();
                
        }
        
        return next.execute(stringValue, context);
    }
    
    /**
     * 比較対象の文字長の候補を取得する。
     * @return 文字長の候補を取得する。
     */
    public int[] getRequiredLengths() {
        return Utils.toArray(requriedLengths);
    }

}

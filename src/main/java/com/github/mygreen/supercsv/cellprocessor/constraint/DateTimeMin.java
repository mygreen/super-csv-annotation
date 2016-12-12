package com.github.mygreen.supercsv.cellprocessor.constraint;

import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.ift.DateCellProcessor;
import org.supercsv.exception.SuperCsvCellProcessorException;
import org.supercsv.util.CsvContext;

import com.github.mygreen.supercsv.cellprocessor.ValidationCellProcessor;
import com.github.mygreen.supercsv.cellprocessor.format.TextPrinter;


/**
 * 日時が指定した値より未来日（最小値）かどうか検証するCellProcessor.
 * 
 * @version 2.0
 * @author T.TSUCHIE
 *
 */
public class DateTimeMin<T extends Comparable<T>> extends ValidationCellProcessor implements DateCellProcessor {
    
    private final T min;
    
    private final boolean inclusive;
    
    private final TextPrinter<T> printer;
    
    public DateTimeMin(final T min, final boolean inclusive, final TextPrinter<T> printer) {
        super();
        checkPreconditions(min, printer);
        this.min = min;
        this.inclusive = inclusive;
        this.printer = printer;
    }
    
    public DateTimeMin(final T min, final boolean inclusive, final TextPrinter<T> printer, final CellProcessor next) {
        super(next);
        checkPreconditions(min, printer);
        this.min = min;
        this.inclusive = inclusive;
        this.printer = printer;
    }
    
    private static <T extends Comparable<T>> void checkPreconditions(final T min, 
            final TextPrinter<T> printer) {
        if(min == null || printer == null) {
            throw new NullPointerException("min or past or printer should not be null");
        }
        
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public Object execute(final Object value, final CsvContext context) {
        
        if(value == null) {
            return next.execute(value, context);
        }
        
        final Class<?> exepectedClass = getMin().getClass();
        if(!exepectedClass.isAssignableFrom(value.getClass())) {
            throw new SuperCsvCellProcessorException(exepectedClass, value, context, this);
        }
        
        final T result = (T) value;
        if(!validate(result)) {
            throw createValidationException(context)
                .messageFormat("%s does not lie the min (%s) value.", 
                        printValue(result), printValue(min))
                .rejectedValue(value)
                .messageVariables("min", getMin())
                .messageVariables("inclusive", isInclusive())
                .messageVariables("printer", getPrinter())
                .build();
                
        }   
        
        return next.execute(result, context);
    }
    
    private boolean validate(final T value) {
        final int compared = value.compareTo(min);
        if(compared > 0) {
            return true;
        }
        
        if(inclusive && compared == 0) {
            return true;
        }
        
        return false;
        
    }
    
    private String printValue(final T value) {
        return getPrinter().print(value);
    }
    
    /**
     * 
     * @return 設定された未来日（最小値）を取得する。
     */
    public T getMin() {
        return min;
    }
    
    /**
     *  値を比較する際に指定した値を含むかどうか。
     * @return
     */
    public boolean isInclusive() {
        return inclusive;
    }
    
    /**
     * 
     * @return フォーマッタを取得する
     */
    public TextPrinter<T> getPrinter() {
        return printer;
    }
    
}

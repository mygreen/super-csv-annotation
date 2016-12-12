package com.github.mygreen.supercsv.cellprocessor.constraint;

import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.ift.DoubleCellProcessor;
import org.supercsv.cellprocessor.ift.LongCellProcessor;
import org.supercsv.exception.SuperCsvCellProcessorException;
import org.supercsv.util.CsvContext;

import com.github.mygreen.supercsv.cellprocessor.ValidationCellProcessor;
import com.github.mygreen.supercsv.cellprocessor.format.TextPrinter;


/**
 * 数値が指定した値以下かどうか検証するCellProcessor.
 * 
 * @version 2.0
 * @param <T> inherit number object. ex, int, Integer, double, Double,...
 * @author T.TSUCHIE
 *
 */
public class NumberMax<T extends Number & Comparable<T>> extends ValidationCellProcessor
        implements LongCellProcessor, DoubleCellProcessor {
    
    private final T max;
    
    private final boolean inclusive;
    
    private final TextPrinter<T> printer; 
    
    public NumberMax(final T max, final boolean inclusive, final TextPrinter<T> printer) {
        super();
        checkPreconditions(max, printer);
        this.max = max;
        this.inclusive = inclusive;
        this.printer = printer;
    }
    
    public NumberMax(final T max, final boolean inclusive, final TextPrinter<T> printer, final CellProcessor next) {
        super(next);
        checkPreconditions(max, printer);
        this.max = max;
        this.inclusive = inclusive;
        this.printer = printer;
    }
    
    private static <T extends Number & Comparable<T>> void checkPreconditions(final T max, 
            final TextPrinter<T> printer) {
        if(max == null || printer == null) {
            throw new NullPointerException("max and printer should not be null");
        }
        
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object execute(final Object value, final CsvContext context) {
        
        if(value == null) {
            return next.execute(value, context);
        }
        
        final Class<?> exepectedClass = getMax().getClass();
        if(!exepectedClass.isAssignableFrom(value.getClass())) {
            throw new SuperCsvCellProcessorException(exepectedClass, value, context, this);
        }
        
        final T result = (T) value;
        if(!validate(result)) {
            throw createValidationException(context)
                .messageFormat("%s does not lie the max (%s) value.", 
                        printValue(result), printValue(max))
                .rejectedValue(result)
                .messageVariables("max", getMax())
                .messageVariables("inclusive", isInclusive())
                .messageVariables("printer", getPrinter())
                .build();
        }   
        
        return next.execute(result, context);
    }
    
    private boolean validate(final T value) {
        final int compared = value.compareTo(max);
        if(compared < 0) {
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
     * @return 設定された最大値を取得する
     */
    public T getMax() {
        return max;
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
     * @return 値のフォーマッタを取得する。
     */
    public TextPrinter<T> getPrinter() {
        return printer;
    }
    
}

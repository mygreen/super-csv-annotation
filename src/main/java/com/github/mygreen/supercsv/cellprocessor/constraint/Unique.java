package com.github.mygreen.supercsv.cellprocessor.constraint;

import java.util.HashMap;
import java.util.Map;

import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.util.CsvContext;

import com.github.mygreen.supercsv.cellprocessor.ValidationCellProcessor;
import com.github.mygreen.supercsv.cellprocessor.format.TextPrinter;

/**
 * 値がユニークかチェックするCellProcessor.
 * 
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class Unique<T> extends ValidationCellProcessor {
    
    private final Map<T, ValueObject> encounteredElements = new HashMap<>();
    
    private final TextPrinter<T> printer;
    
    public Unique(final TextPrinter<T> printer) {
        super();
        checkPreconditions(printer);
        this.printer = printer;
    }
    
    public Unique(final TextPrinter<T> printer, final CellProcessor next) {
        super(next);
        checkPreconditions(printer);
        this.printer = printer;
    }
    
    private static <T> void checkPreconditions(final TextPrinter<T> printer) {
        if(printer == null) {
            throw new NullPointerException("printer should not be null.");
        }
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public Object execute(final Object value, final CsvContext context) {
        
        if(value == null) {
            return next.execute(value, context);
        }
        
        final T result = (T)value;
        
        if(encounteredElements.containsKey(result)) {
            
            final String formattedValue = printer.print(result);
            final ValueObject duplicatedObject = encounteredElements.get(result);
            throw createValidationException(context)
                .messageFormat("duplicate value '%s' encountered.", formattedValue)
                .rejectedValue(result)
                .messageVariables("duplicatedLineNumber", duplicatedObject.lineNumber)
                .messageVariables("duplicatedRowNumber", duplicatedObject.rowNumber)
                .messageVariables("printer", getPrinter())
                .build();
            
        } else {
            final ValueObject object = new ValueObject(result, context.getLineNumber(), context.getRowNumber());
            encounteredElements.put(object.value, object);
        }
        
        return next.execute(value, context);
    }
    
    private class ValueObject {
        
        final T value;
        
        final int lineNumber;
        
        final int rowNumber;
        
        
        ValueObject(final T value, final int lineNumber, final int rowNumber) {
            this.value = value;
            this.lineNumber = lineNumber;
            this.rowNumber = rowNumber;
        }
    }
    
    /**
     * 
     * @return 値のフォーマッタを取得する。
     */
    public TextPrinter<T> getPrinter() {
        return printer;
    }
    
}

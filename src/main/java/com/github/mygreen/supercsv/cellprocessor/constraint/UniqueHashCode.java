package com.github.mygreen.supercsv.cellprocessor.constraint;

import java.util.HashMap;
import java.util.Map;

import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.util.CsvContext;

import com.github.mygreen.supercsv.cellprocessor.ValidationCellProcessor;
import com.github.mygreen.supercsv.cellprocessor.format.TextPrinter;

/**
 * 値がユニークかハッシュコードを元にチェックするCellProcessor.
 * 
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class UniqueHashCode<T> extends ValidationCellProcessor {
    
    private final Map<Integer, ValueObject> encounteredElements = new HashMap<>();
    
    private final TextPrinter<T> printer;
    
    public UniqueHashCode(final TextPrinter<T> printer) {
        super();
        checkPreconditions(printer);
        this.printer = printer;
    }
    
    public UniqueHashCode(final TextPrinter<T> printer, final CellProcessor next) {
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
        final int hashCode = value.hashCode();
        
        if(encounteredElements.containsKey(hashCode)) {
            
            final ValueObject duplicatedObject = encounteredElements.get(result);
            throw createValidationException(context)
                .messageFormat("duplicate hashCode '%s' encountered.", hashCode)
                .rejectedValue(result)
                .messageVariables("hashCode", hashCode)
                .messageVariables("duplicatedRowNumber", duplicatedObject.rowNumber)
                .messageVariables("duplicatedLineNumber", duplicatedObject.lineNumber)
                .messageVariables("printer", getPrinter())
                .build();
            
        } else {
            final ValueObject object = new ValueObject(hashCode, context.getRowNumber(), context.getLineNumber());
            encounteredElements.put(object.hashCode, object);
        }
        
        return next.execute(value, context);
    }
    
    private static class ValueObject {
        
        final int hashCode;
        
        final int rowNumber;
        
        final int lineNumber;
        
        ValueObject(final int hashCode, final int rowNumber, final int lineNumber) {
            this.hashCode = hashCode;
            this.rowNumber = rowNumber;
            this.lineNumber = lineNumber;
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

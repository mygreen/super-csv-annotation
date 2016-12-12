package com.github.mygreen.supercsv.cellprocessor.constraint;

import java.util.Collection;
import java.util.stream.Collectors;

import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.exception.SuperCsvCellProcessorException;
import org.supercsv.util.CsvContext;

import com.github.mygreen.supercsv.cellprocessor.ValidationCellProcessor;
import com.github.mygreen.supercsv.cellprocessor.format.TextPrinter;

/**
 * 指定した値と等しいか検証するCellProcessor
 * 
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class Equals<T> extends ValidationCellProcessor {
    
    private final Class<T> type;
    
    private final Collection<T> equaledValues;
    
    private final TextPrinter<T> printer; 
    
    public Equals(final Class<T> type, final Collection<T> equaledValues, final TextPrinter<T> printer) {
        super();
        checkPreconditions(type, equaledValues, printer);
        this.type = type;
        this.equaledValues = equaledValues.stream()
                .distinct()
                .collect(Collectors.toList());
        this.printer = printer;
    }
    
    public Equals(final Class<T> type, final Collection<T> equaledValues, final TextPrinter<T> printer, final CellProcessor next) {
        super(next);
        checkPreconditions(type, equaledValues, printer);
        this.type = type;
        this.equaledValues = equaledValues.stream()
                .distinct()
                .collect(Collectors.toList());
        this.printer = printer;
    }
    
    private static <T> void checkPreconditions(final Class<T> type, final Collection<T> equaledValues, final TextPrinter<T> printer) {
        if(type == null || equaledValues == null || printer == null) {
            throw new NullPointerException("type or equaledValues or printer, field should not be null.");
        }
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public Object execute(final Object value, final CsvContext context) {
        if(value == null) {
            return next.execute(value, context);
        }
        
        if(!type.isAssignableFrom(value.getClass())) {
            throw new SuperCsvCellProcessorException(type, value, context, this);
        }
        
        final T result = (T) value;
        
        if(!equaledValues.isEmpty() && !equaledValues.contains(value)) {
            final String formattedValue = printer.print(result);
            final String joinedFormattedValues = equaledValues.stream()
                    .map(v -> printer.print(v))
                    .collect(Collectors.joining(", "));
            
            throw createValidationException(context)
                .rejectedValue(result)
                .messageFormat("'%s' is not equals any of [%s].", formattedValue, joinedFormattedValues)
                .messageVariables("equalsValues", equaledValues)
                .messageVariables("printer", getPrinter())
                .build();
        }
        
        return next.execute(value, context);
    }
    
    /**
     * 値のプロバイダを取得する。
     * @return コンストラクタで渡されたプロバイダ。
     */
    public Collection<T> getEqualedValues() {
        return equaledValues;
    }
    
    /**
     * フォーマッタを取得する
     * @return コンストラクタで渡されたフォーマッタ。
     */
    public TextPrinter<T> getPrinter() {
        return printer;
    }
    
}

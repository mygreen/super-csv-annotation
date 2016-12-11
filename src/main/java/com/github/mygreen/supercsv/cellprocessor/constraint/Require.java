package com.github.mygreen.supercsv.cellprocessor.constraint;

import org.supercsv.cellprocessor.ift.BoolCellProcessor;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.ift.DateCellProcessor;
import org.supercsv.cellprocessor.ift.DoubleCellProcessor;
import org.supercsv.cellprocessor.ift.LongCellProcessor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;
import org.supercsv.util.CsvContext;

import com.github.mygreen.supercsv.cellprocessor.ValidationCellProcessor;

/**
 * 値が必須かどうかチェックする制約のCellProcessor。
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class Require extends ValidationCellProcessor 
        implements BoolCellProcessor, DateCellProcessor, DoubleCellProcessor, LongCellProcessor, StringCellProcessor {
    
    private final boolean considerEmpty;
    
    private final boolean considerBlank;
    
    public Require(final boolean considerEmpty, final boolean considerBlank) {
        super();
        this.considerEmpty = considerEmpty;
        this.considerBlank = considerBlank;
    }
    
    public Require(final boolean considerEmpty, final boolean considerBlank, final CellProcessor next) {
        super(next);
        this.considerEmpty = considerEmpty;
        this.considerBlank = considerBlank;
    }
    
    @Override
    public <T> T execute(final Object value, final CsvContext context) {
        
        if (!validate(value)){
            throw createValidationException(context)
                .message("null or empty value encountered")
                .messageVariables("considerEmpty", considerEmpty)
                .messageVariables("considerBlank", considerBlank)
                .rejectedValue(value)
                .build();
        }
        
        return next.execute(value, context);
    }
    
    private boolean validate(final Object value) {
        
        if(value == null) {
            return false;
        }
        
        if(value instanceof String) {
            final String strValue = (String)value;
            if(considerEmpty && strValue.isEmpty()) {
                return false;
            }
            
            if(considerBlank && strValue.trim().isEmpty()) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * 空文字を考慮するかどうか。
     * @return trueの場合、空文字を考慮します。
     */
    public boolean isConsiderEmpty() {
        return considerEmpty;
    }
    
    /**
     * 空白文字を考慮するかどうか。
     * @return trueの場合、空白文字を考慮します。
     */
    public boolean isConsiderBlank() {
        return considerBlank;
    }
    
}

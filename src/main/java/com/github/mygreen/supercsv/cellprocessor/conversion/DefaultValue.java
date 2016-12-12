package com.github.mygreen.supercsv.cellprocessor.conversion;

import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;
import org.supercsv.util.CsvContext;

/**
 * nullの場合に、指定した値に置換するCellProcessor。
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class DefaultValue extends CellProcessorAdaptor implements StringCellProcessor {
    
    private final String returnValue;
    
    public DefaultValue(final String returnValue) {
        super();
        this.returnValue = returnValue;
    }
    
    public DefaultValue(final String returnValue, final StringCellProcessor next) {
        super(next);
        this.returnValue = returnValue;
    }
    
    @Override
    public <T> T execute(final Object value, final CsvContext context) {
        
        if(value == null) {
            return next.execute(returnValue, context);
        }
        
        return next.execute(value, context);
    }
    
    /**
     * 置換する値を取得する。
     * @return 置換する値を返します。
     */
    public String getReturnValue() {
        return returnValue;
    }
    
}

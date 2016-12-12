package com.github.mygreen.supercsv.exception;

import org.supercsv.exception.SuperCsvException;
import org.supercsv.util.CsvContext;


/**
 * The number of columns to be processed must match the number of CellProcessors
 * <p>列のサイズが、CellProcessorやマッピングで定義したサイズと異なる場合にスローされる例外。</p>
 * 
 * @author T.TSUCHIE
 *
 */
public class SuperCsvNoMatchHeaderException extends SuperCsvException {
    
    /** serialVersionUID */
    private static final long serialVersionUID = 1L;
    
    protected final String[] actualHeaders;
    
    protected final String[] expectedHeaders;
    
    public SuperCsvNoMatchHeaderException(final String[] actualHeaders, final String[] expectedHeaders, final CsvContext context) {
        super(String.format("'%s' is not equals to '%s'",
                String.join(",", actualHeaders), String.join(",", expectedHeaders)), context);
        
        this.actualHeaders = actualHeaders;
        this.expectedHeaders = expectedHeaders;
    }
    
    public String[] getActualHeaders() {
        return actualHeaders;
    }
    
    public String[] getExpectedHeaders() {
        return expectedHeaders;
    }
    
}

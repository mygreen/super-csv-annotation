package org.supercsv.ext.exception;

import org.supercsv.exception.SuperCsvException;
import org.supercsv.util.CsvContext;


/**
 * The number of columns to be processed must match the number of CellProcessors
 * <p>列のサイズが、CellProcessorやマッピングで定義したサイズと異なる場合にスローされる例外。
 * 
 * @author T.TSUCHIE
 *
 */
public class SuperCsvNoMatchHeaderException extends SuperCsvException {
    
    /** serialVersionUID */
    private static final long serialVersionUID = 1L;
    
    protected final String[] actualHeaders;
    
    protected final String[] expectedHeaders;
    
    public SuperCsvNoMatchHeaderException(final String[] actualHeaders, String[] expectedHeaders, final CsvContext context) {
        super(String.format("'%s' is not equals to '%s'",
                joinArray(actualHeaders, ","), joinArray(expectedHeaders, ",")), context);
        
        this.actualHeaders = actualHeaders;
        this.expectedHeaders = expectedHeaders;
    }
    
    public String[] getActualHeaders() {
        return actualHeaders;
    }
    
    public String[] getExpectedHeaders() {
        return expectedHeaders;
    }
    
    public String getActualHeadersWithJoin(final String seperator) {
        return joinArray(actualHeaders, seperator);
    }
    
    public String getExpectedHeadersWithJoin(final String seperator) {
        return joinArray(expectedHeaders, seperator);
    }
    
    private static String joinArray(final String[] arrays, final String seperator) {
        
        StringBuilder sb = new StringBuilder();
        for(int i=0; i < arrays.length; i++) {
            sb.append(arrays[i]);
            
            if(i < arrays.length-1) {
                sb.append(seperator);
            }
        }
        return sb.toString();
        
    }
    
}

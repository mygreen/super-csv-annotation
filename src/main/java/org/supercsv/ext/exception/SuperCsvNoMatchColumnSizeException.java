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
public class SuperCsvNoMatchColumnSizeException extends SuperCsvException {
    
    /** serialVersionUID */
    private static final long serialVersionUID = 1L;
    
    protected final int actualColumnSize;
    
    protected final int epxpectedColumnSize;
    
    public SuperCsvNoMatchColumnSizeException(final int actualColumnSize, final int epxpectedColumnSize, final CsvContext context) {
        this("", actualColumnSize, epxpectedColumnSize, context);
    }
    
    public SuperCsvNoMatchColumnSizeException(final String message, final int actualColumnSize, final int epxpectedColumnSize, final CsvContext context) {
        super(message + String.format("The number of columns to be processed (%d) must match the number of CellProcessors (%d): check that the number"
                + " of CellProcessors you have defined matches the expected number of columns being read/written",
            actualColumnSize, epxpectedColumnSize), context);
        
        this.actualColumnSize = actualColumnSize;
        this.epxpectedColumnSize = epxpectedColumnSize;
    }
    
    public int getActualColumnSize() {
        return actualColumnSize;
    }
    
    public int getEpxpectedColumnSize() {
        return epxpectedColumnSize;
    }
    
}

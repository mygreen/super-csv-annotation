package org.supercsv.ext.exception;

import java.util.ArrayList;
import java.util.List;

import org.supercsv.exception.SuperCsvCellProcessorException;
import org.supercsv.exception.SuperCsvException;
import org.supercsv.util.CsvContext;


/**
 *
 *
 * @author T.TSUCHIE
 *
 */
public class SuperCsvRowException extends SuperCsvException {
    
    /** serialVersionUID */
    private static final long serialVersionUID = 1L;
    
    protected final List<SuperCsvException> columnErrors = new ArrayList<SuperCsvException>();
    
    public SuperCsvRowException(final String msg, final CsvContext context) {
        super(msg, context);
    }
    
    public void addError(final SuperCsvException error) {
        this.columnErrors.add(error);
    }
    
    public void addError(final SuperCsvCellProcessorException error) {
        SuperCsvCellProcessorException cloned = new SuperCsvCellProcessorException(error.getMessage(),
                cloneCsvContext(error.getCsvContext()),
                error.getProcessor());
        
        this.columnErrors.add(cloned);
    }
    
    private CsvContext cloneCsvContext(final CsvContext context) {
        
        CsvContext cloned = new CsvContext(
                context.getLineNumber(),
                context.getRowNumber(),
                context.getColumnNumber());
        
        // shallow copy
        List<Object> destRowSource = new ArrayList<Object>(context.getRowSource().size());
        for(Object obj : context.getRowSource()) {
            destRowSource.add(obj);
        }
        cloned.setRowSource(destRowSource);
        
        return cloned;
    }
    
    public void addAllErrors(final List<SuperCsvException> errors) {
        for(SuperCsvException error : errors) {
            addError(error);
        }
    }
    
    public List<SuperCsvException> getColumnErrors() {
        return columnErrors;
    }
    
    public boolean isEmptyColumnErrors() {
        return getColumnErrors().isEmpty();
    }
    
    public boolean isNotEmptyColumnErrors() {
        return !isEmptyColumnErrors();
    }
    
}

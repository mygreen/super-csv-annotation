package com.github.mygreen.supercsv.exception;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.supercsv.exception.SuperCsvCellProcessorException;
import org.supercsv.exception.SuperCsvException;
import org.supercsv.util.CsvContext;


/**
 * 行のエラーをまとめた例外。
 *
 * @author T.TSUCHIE
 *
 */
public class SuperCsvRowException extends SuperCsvException {
    
    /** serialVersionUID */
    private static final long serialVersionUID = 1L;
    
    private final List<SuperCsvException> columnErrors = new ArrayList<>();
    
    public SuperCsvRowException(final String msg, final CsvContext context) {
        super(msg, context);
    }
    
    public void addError(final SuperCsvException error) {
        this.columnErrors.add(error);
    }
    
    public void addError(final SuperCsvCellProcessorException error) {
        
        final SuperCsvCellProcessorException cloned;
        if(error instanceof SuperCsvValidationException) {
            cloned = ((SuperCsvValidationException)error).clone();
        } else {
            cloned = new SuperCsvCellProcessorException(error.getMessage(),
                    cloneCsvContext(error.getCsvContext()),
                    error.getProcessor());
        }
        
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
    
    @Override
    public void printStackTrace(final PrintStream s) {
        
        super.printStackTrace(s);
        
        int count = 1;
        for(SuperCsvException e : columnErrors) {
            s.printf("[ColumnError-%d] : ", count);
            e.printStackTrace(s);
            count++;
        }
        
    }
    
    @Override
    public void printStackTrace(final PrintWriter s) {
        
        super.printStackTrace(s);
        
        int count = 1;
        for(SuperCsvException e : columnErrors) {
            s.printf("[ColumnError-%d] : ", count);
            e.printStackTrace(s);
            count++;
        }
        
    }
    
    
}

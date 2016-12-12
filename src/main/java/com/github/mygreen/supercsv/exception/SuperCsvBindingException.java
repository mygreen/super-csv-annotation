package com.github.mygreen.supercsv.exception;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.supercsv.exception.SuperCsvException;
import org.supercsv.util.CsvContext;

import com.github.mygreen.supercsv.validation.CsvBindingErrors;

/**
 * CellProcessorとは別に、値を検証した結果、エラーが存在する場合にスローされる例外。
 * 
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class SuperCsvBindingException extends SuperCsvException {

    /** serialVersionUID */
    private static final long serialVersionUID = -7961092348053869573L;
    
    private final CsvBindingErrors bingingErrors;
    
    /**
     * CellProcessor内で発生したエラーがある場合
     */
    private final List<SuperCsvException> processingErrors = new ArrayList<>();
    
    public SuperCsvBindingException(final String msg, final CsvContext context, final CsvBindingErrors bingingErrors) {
        super(msg, context);
        this.bingingErrors = bingingErrors;
    }
    
    /**
     * エラー情報を取得する。
     * @return
     */
    public CsvBindingErrors getBindingErrors() {
        return bingingErrors;
    }
    
    /**
     * CellProcessor内で発生したエラー情報を追加する。
     * @param errors
     */
    public void addAllProcessingErrors(final Collection<SuperCsvException> errors) {
        this.processingErrors.addAll(errors);
    }
    
    /**
     * CellProcessor内で発生したエラー情報を取得する。
     * @return
     */
    public List<SuperCsvException> getProcessingErrors() {
        return processingErrors;
    }
    
    @Override
    public void printStackTrace(final PrintStream s) {
        
        super.printStackTrace(s);
        
        int count = 1;
        for(SuperCsvException e : processingErrors) {
            s.printf("[ProcessingError-%d] : ", count);
            e.printStackTrace(s);
            count++;
        }
        
    }
    
    @Override
    public void printStackTrace(final PrintWriter s) {
        
        super.printStackTrace(s);
        
        int count = 1;
        for(SuperCsvException e : processingErrors) {
            s.printf("[ProcessingError-%d] : ", count);
            e.printStackTrace(s);
            count++;
        }
    }
    
    
}

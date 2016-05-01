package org.supercsv.ext.exception;



/**
 *
 *
 * @author T.TSUCHIE
 *
 */
public class SuperCsvInvalidAnnotationException extends RuntimeException {
    
    /** serialVersionUID */
    private static final long serialVersionUID = 1L;
    
    public SuperCsvInvalidAnnotationException(final String message) {
        super(message);
    }
    
    public SuperCsvInvalidAnnotationException(final String message, Throwable e) {
        super(message, e);
    }
    
}

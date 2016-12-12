package com.github.mygreen.supercsv.exception;

import java.lang.annotation.Annotation;

import org.supercsv.exception.SuperCsvException;

/**
 * アノテーションの値が不正なときにスローされる例外。
 * 
 * @version 2.0
 * @author T.TSUCHIE
 *
 */
public class SuperCsvInvalidAnnotationException extends SuperCsvException {
    
    /** serialVersionUID */
    private static final long serialVersionUID = 1L;
    
    private final Annotation targetAnnotation;
    
    public SuperCsvInvalidAnnotationException(final String message) {
        super(message);
        this.targetAnnotation = null;
    }
    
    public SuperCsvInvalidAnnotationException(final Annotation targetAnnotation, final String message) {
        super(message);
        this.targetAnnotation = targetAnnotation;
    }
    
    public SuperCsvInvalidAnnotationException(final Annotation targetAnnotation, final String message, final Throwable e) {
        super(message, null, e);
        this.targetAnnotation = targetAnnotation;
    }
    
    /**
     * エラーの元となったアノテーションを取得する。
     * @return 必要なアノテーションが付与されていない時など、nullを返すときもあります。
     */
    public Annotation getTargetAnnotation() {
        return targetAnnotation;
    }
    
}

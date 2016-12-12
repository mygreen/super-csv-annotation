package com.github.mygreen.supercsv.cellprocessor.format;


/**
 * オブジェクトを文字列にフォーマットする際にスローされる例外。
 * 
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class TextPrintException extends RuntimeException {
    
    /** serialVersionUID */
    private static final long serialVersionUID = -7227981380538624937L;
    
    /**
     * 変換対象のオブジェクトの値
     */
    private final Object targetObject;
    
    public TextPrintException(final Object targetObject, final String message) {
        super(message);
        this.targetObject = targetObject;
    }
    
    public TextPrintException(final Object targetObject, final Throwable exception) {
        super(exception);
        this.targetObject = targetObject;
    }
    
    public TextPrintException(final Object targetObject, final String message, final Throwable exception) {
        super(message, exception);
        this.targetObject = targetObject;
    }
    
    /**
     * フォーマットに失敗したオブジェクトの値を取得する。
     * @return
     */
    public Object getTargetObject() {
        return targetObject;
    }
    
}

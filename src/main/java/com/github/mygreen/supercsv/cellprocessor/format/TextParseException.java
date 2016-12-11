package com.github.mygreen.supercsv.cellprocessor.format;

/**
* 文字列をパースしてオブジェクトの変換した際にスローされる例外です。
* 
* @since 1.2
* @author T.TSUCHIE
*
*/
public class TextParseException extends RuntimeException {
   
    /** serialVersionUID */
    private static final long serialVersionUID = 7389770363413465673L;
    
    /**
     * パース対象の文字列
     */
    private final String targetText;
    
    /**
     * パース先のクラスタイプ
     */
    private final Class<?> toType;
    
    public TextParseException(final String targetText, final Class<?> toType) {
        this(targetText, toType, String.format("fail parse from '%s' to type '%s'", targetText, toType.getCanonicalName()));
    }
    
    public TextParseException(final String targetText, final Class<?> toType, final String message) {
        super(message);
        this.targetText = targetText;
        this.toType = toType;
    }
    
    public TextParseException(final String targetText, final Class<?> toType, final Throwable exception) {
        super(exception);
        this.targetText = targetText;
        this.toType = toType;
    }
    
    public TextParseException(final String targetText, final Class<?> toType, final String message, final Throwable exception) {
        super(message, exception);
        this.targetText = targetText;
        this.toType = toType;
    }
    
    /**
     * パース対象の文字列を取得する。
     * @return パースに失敗した文字列。
     */
    public String getTargetText() {
        return targetText;
    }
    
    /**
     * パース先のクラスタイプを取得する。
     * @return
     */
    public Class<?> getToType() {
        return toType;
    }
    
}

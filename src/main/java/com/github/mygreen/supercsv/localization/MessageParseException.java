package com.github.mygreen.supercsv.localization;


/**
 * メッセージのフォーマットに失敗した際にスローされる例外
 * @author T.TSUCHIE
 *
 */
public class MessageParseException extends RuntimeException {

    /** serialVersionUID */
    private static final long serialVersionUID = 1L;
    
    private final String value;
    
    public MessageParseException(final String value, final String message) {
        super(message);
        this.value = value;
    }
    
    /**
     * value を取得する
     * @return the value
     */
    public String getValue() {
        return value;
    }
    
}

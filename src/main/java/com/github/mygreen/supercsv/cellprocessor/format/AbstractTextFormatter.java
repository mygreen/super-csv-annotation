package com.github.mygreen.supercsv.cellprocessor.format;

import java.util.Optional;

/**
 * 文字列とオブジェクトの相互変換を行う抽象クラス。
 *
 * @param <T> オブジェクトのタイプ
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public abstract class AbstractTextFormatter<T> implements TextFormatter<T> {
    
    /**
     * 読み込みに失敗したときのメッセージ
     */
    protected String validationMessage;
    
    @Override
    public Optional<String> getValidationMessage() {
        if(validationMessage == null || validationMessage.isEmpty()) {
            return Optional.empty();
        }
        
        return Optional.of(validationMessage);
    }
    
    @Override
    public void setValidationMessage(String validationMessage) {
        this.validationMessage = validationMessage;
        
    }
    
}

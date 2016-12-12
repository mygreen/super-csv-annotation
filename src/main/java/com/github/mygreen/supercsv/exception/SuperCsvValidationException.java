package com.github.mygreen.supercsv.exception;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.exception.SuperCsvCellProcessorException;
import org.supercsv.util.CsvContext;

import com.github.mygreen.supercsv.util.Utils;

/**
 * {@link CellProcessor}の実行に失敗（入力値が不正）などの時にスローされる例外。
 * <p>フォーマット用のメッセージや、変数が設定可能。</p>
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class SuperCsvValidationException extends SuperCsvCellProcessorException implements Cloneable {
    
    /** serialVersionUID */
    private static final long serialVersionUID = 3448638872019192862L;
    
    private boolean parsedError;
    
    private Object rejectedValue;
    
    private String validationMessage;
    
    private Map<String, Object> messageVariables = new HashMap<>();
    
    public SuperCsvValidationException(final String msg, final CsvContext context, final CellProcessor processor) {
        super(msg, context, processor);
    }
    
    public SuperCsvValidationException(final String msg, final CsvContext context, final CellProcessor processor, final Throwable t) {
        super(msg, context, processor, t);
    }
    
    /**
     * 検証に失敗した値を取得する。
     * @return
     */
    public Object getRejectedValue() {
        return rejectedValue;
    }
    
    /**
     * 検証用のメッセージを取得する。
     * @return 検証用のメッセージ。
     */
    public String getValidationMessage() {
        return validationMessage;
    }
    
    /**
     * 検証用のメッセージ変数を取得する。
     * @return 検証用のメッセージ変数
     */
    public Map<String, Object> getMessageVariables() {
        return messageVariables;
    }
    
    /**
     * パース時などの型変換エラーかどうか。
     * @return trueの場合、型変換エラー。
     */
    public boolean isParedError() {
        return parsedError;
    }
    
    @Override
    public SuperCsvValidationException clone() {
        return new Builder(new CsvContext(getCsvContext()), getProcessor())
                .message(getMessage())
                .exception(getCause())
                .rejectedValue(rejectedValue)
                .validationMessage(validationMessage)
                .messageVariables(messageVariables)
                .parsedError(parsedError)
                .build();
    }
    
    /**
     * {@link SuperCsvValidationException}のインスタンスを作成するビルダクラス。
     *
     */
    public final static class Builder {
        
        private final CsvContext context;
        
        private final CellProcessor processor;
        
        private String message;
        
        private Throwable exception;
        
        private Object rejectedValue;
        
        private String validationMessage;
        
        private Map<String, Object> messageVariables = new HashMap<>();
        
        private boolean parsedError;
        
        public Builder(CsvContext context, CellProcessor processor) {
            this.context = context;
            this.processor = processor;
        }
        
        /**
         * 例外用のメッセージを設定する。
         * @param message 例外用のメッセージ。
         * @return
         */
        public Builder message(String message) {
            this.message = message;
            return this;
        }
        
        /**
         * 例外用のメッセージをフォーマットして設定する。
         * 
         * @see String#format(String, Object...)
         * @param format フォーマット。
         * @param args 書式の引数。
         * @return
         */
        public Builder messageFormat(final String format, final Object... args) {
            return message(String.format(format, args));
        }
        
        /**
         * 例外を設定する。
         * @param exception 例外。
         * @return
         */
        public Builder exception(Throwable exception) {
            this.exception = exception;
            return this;
            
        }
        
        /**
         * 検証に失敗した値
         * @param rejectedValue
         * @return
         */
        public Builder rejectedValue(Object rejectedValue) {
            this.rejectedValue = rejectedValue;
            return this;
        }
        
        /**
         * 検証エラー用メッセージを設定する
         * @param validationMessage 検証エラー時のメッセージ
         * @return
         */
        public Builder validationMessage(String validationMessage) {
            this.validationMessage = validationMessage;
            return this;
        }
        
        /**
         * 値が存在する場合に検証エラー用メッセージを設定する
         * @param validationMessage 検証エラー時のメッセージ
         * @return
         */
        public Builder validationMessageIfPresent(Optional<String> validationMessage) {
            validationMessage.ifPresent(m -> validationMessage(m));
            return this;
        }
        
        /**
         * 検証エラー用メッセージの引数を追加する。
         * @param key キー.
         * @param value 値
         * @return
         */
        public Builder messageVariables(String key, Object value) {
            this.messageVariables.put(key, value);
            return this;
        }
        
        /**
         * 値が存在する場合に検証エラー用メッセージの引数を追加する。
         * @param key キー.
         * @param value 値。
         * @return
         */
        public Builder messageVariablesIfPresent(String key, Optional<?> value) {
            value.ifPresent(v -> messageVariables.put(key, v));
            return this;
        }
        
        /**
         * 値が存在する場合に検証エラー用メッセージの引数を追加する。
         * @param arguments メッセージ変数のマップ。
         * @return
         */
        public Builder messageVariables(Map<String, Object> arguments) {
            this.messageVariables.putAll(arguments);
            return this;
        }
        
        /**
         * パース時のエラーかどうか設定する。
         * @param parsedError
         * @return
         */
        public Builder parsedError(boolean parsedError) {
            this.parsedError = parsedError;
            return this;
        }
        
        /**
         * {@link SuperCsvValidationException}のインスタンスを作成する。
         * @return
         */
        public SuperCsvValidationException build() {
            
            final String msg = Optional.ofNullable(message)
                    .orElseGet(() -> processor.getClass().getName() + " error.");
            
            final SuperCsvValidationException error;
            if(exception == null) {
                error = new SuperCsvValidationException(msg, context, processor);
            } else {
                error = new SuperCsvValidationException(msg, context, processor, exception);
            }
            
            error.rejectedValue = rejectedValue;
            
            if(Utils.isNotEmpty(validationMessage)) {
                error.validationMessage = validationMessage;
            }
            
            if(messageVariables.size() > 0) {
                error.messageVariables = messageVariables;
            }
            
            error.parsedError = parsedError;
            
            return error;
            
        }
        
    }
    

}

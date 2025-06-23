package com.github.mygreen.supercsv.exception;

import java.util.HashMap;
import java.util.Map;

import org.supercsv.exception.SuperCsvException;
import org.supercsv.util.CsvContext;

/**
 * 固定長カラムの処理に失敗したときのエラーです。
 *
 * @since 2.5
 * @author T.TSUCHIE
 *
 */
public class SuperCsvFixedSizeException extends SuperCsvException {

    /**
     * メッセージコード。
     */
    private String messageCode;
    
    /**
     * メッセージ変数。
     */
    private Map<String, Object> messageVariables = new HashMap<>();    
    
    private SuperCsvFixedSizeException(final String msg, final CsvContext context) {
        super(msg, context);
    }
    
    /**
     * メッセージコードを取得する。
     * @return メッセージコード。
     */
    public String getMessageCode() {
        return messageCode;
    }
    
    /**
     * 検証用のメッセージ変数を取得する。
     * @return 検証用のメッセージ変数
     */
    public Map<String, Object> getMessageVariables() {
        return messageVariables;
    }
    
    /**
     * {@link SuperCsvFixedSizeException} のインスタンスを組み立てるビルダ。
     *
     *
     */
    public static class Builder {
        
        private final String messageCode;
        
        private final CsvContext context;
        
        /** 例外用のメッセージ */
        private String message;
        
        private Map<String, Object> messageVariables = new HashMap<>();
        
        /**
         * ビルダクラスのコンストラクタ。
         * @param messageCode メッセージコード。
         * @param context CSVのコンテキスト情報。
         */
        public Builder(String messageCode, CsvContext context) {
            this.messageCode = messageCode;
            this.context = context;
        }
        
        /**
         * 例外用のメッセージを設定する。
         * @param message 例外用のメッセージ。
         * @return ビルダ自身のインスタンス。
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
         * @return ビルダ自身のインスタンス。
         */
        public Builder messageFormat(final String format, final Object... args) {
            return message(String.format(format, args));
        }
        
        /**
         * 検証エラー用メッセージの引数を追加する。
         * @param key キー.
         * @param value 値
         * @return ビルダ自身のインスタンス。
         */
        public Builder messageVariables(String key, Object value) {
            this.messageVariables.put(key, value);
            return this;
        }
        
        /**
         * 値が存在する場合に検証エラー用メッセージの引数を追加する。
         * @param arguments メッセージ変数のマップ。
         * @return ビルダ自身のインスタンス。
         */
        public Builder messageVariables(Map<String, Object> arguments) {
            this.messageVariables.putAll(arguments);
            return this;
        }
        
        /**
         * {@link SuperCsvFixedSizeException}のインスタンスを作成する。
         */
        public SuperCsvFixedSizeException build() {
            SuperCsvFixedSizeException exception = new SuperCsvFixedSizeException(message != null ? message : "", context);
            exception.messageCode = messageCode;
            exception.messageVariables.putAll(messageVariables);
            return exception;
        }
        
    }

}

package com.github.mygreen.supercsv.cellprocessor.format;

import com.github.mygreen.supercsv.annotation.format.CsvFormat;

/**
 * 文字列とオブジェクトの相互変換を行うインタフェース。
 * 
 * @param <T> オブジェクトのタイプ
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public interface TextFormatter<T> extends TextParser<T>, TextPrinter<T> {
    
    /**
     * パース時のエラーメッセージを設定します。
     * <p>{@link CsvFormat#message()}で指定されたメッセージを渡す場合に実装します。
     *   <br>このメソッドを実装する際には、{@link TextParser#getValidationMessage()}も実装してください。
     * </p>
     * 
     * @param validationMessage パース時のエラーメッセージ
     */
    void setValidationMessage(String validationMessage);
    
}

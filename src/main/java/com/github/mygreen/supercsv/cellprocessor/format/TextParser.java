package com.github.mygreen.supercsv.cellprocessor.format;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

/**
 * 文字列をパースしてオブジェクトに変換するインタフェース。
 * 
 * @since 2.0
 * @param <T> オブジェクトのタイプ
 * @author T.TSUCHIE
 *
 */
@FunctionalInterface
public interface TextParser<T> {
    
    /**
     * 書式を取得します。
     * @return 書式を持たない場合は空を返します。
     */
    default Optional<String> getPattern() {
        return Optional.empty();
    }
    
    /**
     * パース時のエラーメッセージ中の変数を取得します。
     * @return 
     */
    default Map<String, Object> getMessageVariables() {
        return Collections.emptyMap();
    }
    
    /**
     * パース時のエラーメッセージを取得します。
     * @return メッセージが内場合は空を返します。
     */
    default Optional<String> getValidationMessage() {
        return Optional.empty();
    }
    
    /**
     * 文字列をパースして、オブジェクトに変換する。
     * <p>実装する際には、API経由などでパースした際に発生した例外は、{@link TextParseException}でラップするしてください。
     *   <br>{@link TextParseException}でラップすると、{@link ParseProcessor}でエラーオブジェクトに変換されます。
     * </p>
     * 
     * @param text パース対象の文字列。
     * @return 変換された値。
     * @throws TextParseException パースに失敗した際にスローされます。
     */
    T parse(String text);
    
}

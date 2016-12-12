package com.github.mygreen.supercsv.cellprocessor.format;


/**
 * オブジェクトをフォーマットして文字列に変換するインタフェース。
 * 
 * @since 2.0
 * @param <T> オブジェクトのタイプ
 * @author T.TSUCHIE
 *
 */
@FunctionalInterface
public interface TextPrinter<T> {
    
    /**
     * オブジェクトをフォーマットして、文字列に変換する。
     * <p>実装する際には、API経由などでパースした際に発生した例外は、{@link TextPrintException}でラップするしてください。
     *   <br>{@link TextPrintException}でラップすると、{@link PrintProcessor}でエラーオブジェクトに変換されます。
     * </p>
     * 
     * @param object フォーマット対象のオブジェクト
     * @return フォーマットした文字列
     * @throws TextPrintException 文字列への変換に失敗した際にスローされます。
     */
    String print(T object);
    
}

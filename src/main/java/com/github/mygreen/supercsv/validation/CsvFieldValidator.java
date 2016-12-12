package com.github.mygreen.supercsv.validation;

import java.util.HashMap;
import java.util.Map;

import org.supercsv.util.CsvContext;

import com.github.mygreen.supercsv.cellprocessor.format.TextPrinter;

/**
 * フィールドに対するValidator
 * 
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
@FunctionalInterface
public interface CsvFieldValidator<T> {
    
    /**
     * メッセージ変数を取得する。
     * <p>デフォルトの場合、下記の値が設定される。</p>
     * <ul>
     *   <li>lineNumber : カラムの値に改行が含まれている場合を考慮した実際の行番号です。1から始まります。</li>
     *   <li>rowNumber : CSVの行番号です。1から始まります。</li>
     *   <li>columnNumber : CSVの列番号です。1から始まります。</li>
     *   <li>label : カラムの見出し名です。</li>
     *   <li>value : 実際のカラムの値です。</li>
     *   <li>printer : カラムの値に対数するフォーマッタです。{@link TextPrinter#print(Object)}でvalue値を文字列に変換します。</li>
     * </ul>
     * 
     * @param field フィールド情報
     * @return メッセージ変数のマップ。
     */
    default Map<String, Object> createMessageVariables(final CsvField<T> field) {
        
        final CsvContext csvContext = field.getValidationContext().getCsvContext();
        
        final Map<String, Object> variables = new HashMap<>();
        variables.put("lineNumber", csvContext.getLineNumber());
        variables.put("rowNumber", csvContext.getRowNumber());
        variables.put("columnNumber", field.getColumnNumber());
        variables.put("label", field.getLabel());
        variables.put("validatedValue", field.getValue());
        variables.put("printer", field.getColumnMapping().getFormatter());
        
        return variables;
        
    }
    
    /**
     * フィールドの値の入力値検証を行います。
     * @param bindingErrors エラー情報。
     * @param field フィールド情報
     */
    void validate(CsvBindingErrors bindingErrors, CsvField<T> field);
    
}

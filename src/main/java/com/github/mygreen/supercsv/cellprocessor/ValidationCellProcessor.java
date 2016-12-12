package com.github.mygreen.supercsv.cellprocessor;

import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.util.CsvContext;

import com.github.mygreen.supercsv.exception.SuperCsvValidationException;
import com.github.mygreen.supercsv.util.Utils;

/**
 * 入力値検証用の{@link CellProcessor}のベースとなるクラス。
 * 
 * @version 2.0
 * @author T.TSUCHIE
 *
 */
public abstract class ValidationCellProcessor extends CellProcessorAdaptor {
    
    /**
     * 検証エラー時のメッセージ
     */
    protected String validationMessage;
    
    /**
     * チェインの最後に実行される{@link CellProcessor}のインスタンスを作成するコンストラクタ。
     */
    protected ValidationCellProcessor() {
        super();
    }
    
    /**
     * チェインの次に実行される{@link CellProcessor}を指定してインスタンスを作成するコンストラクタ。 
     * 
     * @param next チェインで次に実行される{@link CellProcessor}
     * @throws NullPointerException {@literal if next is null.}
     */
    protected ValidationCellProcessor(final CellProcessor next) {
        super(next);
    }
    
    /**
     * 検証エラー時のメッセージを取得します。
     * @return エラーメッセージ。
     */
    public String getValidationMessage() {
        return validationMessage;
    }
    
    /**
     * 検証エラー時のメッセージを設定する。
     * @param validationMessage エラー時のメッセージ。
     */
    public void setValidationMessage(String validationMessage) {
        this.validationMessage = validationMessage;
    }
    
    /**
     * 標準の検証用のメッセージを取得する。
     * @return {@literal {クラス名.violated}}の形式。
     */
    public String getDefaultValidationMessage() {
        return String.format("{%s.violated}", getClass().getName());
    }
    
    /**
     * {@link SuperCsvValidationException.Builder}のインスタンスを作成する。
     * @param context CsvContext CSVのコンテキスト。
     * @return 予め検証用のメッセージは設定された状態で作成される。
     *         ただし、{@link #getValidationMessage()}がnullまたは空の場合、{@link #getDefaultValidationMessage()}が設定される。
     */
    public SuperCsvValidationException.Builder createValidationException(final CsvContext context) {
        
        String message = getValidationMessage();
        if(Utils.isEmpty(message)) {
            message = getDefaultValidationMessage();
        }
        
        return new SuperCsvValidationException.Builder(context, this)
                .validationMessage(message);
    }
    
}

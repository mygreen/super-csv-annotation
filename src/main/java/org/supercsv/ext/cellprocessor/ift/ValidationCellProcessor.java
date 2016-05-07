package org.supercsv.ext.cellprocessor.ift;

import java.util.Map;


/**
 * Validation系のCellProcessor.
 * <p>エラー時のメッセージ出力する場合に、このインタフェースを実装する。</p>
 *
 * @author T.TSUCHIE
 *
 */
public interface ValidationCellProcessor {
    
    /**
     * メッセージのコードを取得する。
     * <p>基本は、<CellProcessorのクラス名> + ".violated"
     * <P>例. org.supercsv.contrib.cellprocessor.constraint.StrMax.violated
     * @return
     */
    default String getMessageCode() {
        return this.getClass().getCanonicalName() + ".violated";
    }
    
    /**
     * メッセージ用の変数を取得する。
     * <p>Map.key = message variable key
     * <p>Map.value = message variable value
     * <p>ex. CellProcessor:NumberRange, Map.key = max, Map.vale = 5.
     * @return
     */
    Map<String, ?> getMessageVariable();
    
    /**
     * メッセージに埋め込む際の値を取得する。
     * <p>java.util.Date型の時など、表記とそぐわない場合があるため。
     * @param value フォーマット対象の値。
     * @return フォーマットした値。
     */
    default String formatValue(final Object value) {
        if(value == null) {
            return "";
        }
        
        return value.toString();
    }
}

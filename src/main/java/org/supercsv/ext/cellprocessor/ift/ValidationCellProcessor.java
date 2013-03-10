/*
 * ValidationCellProcessor.java
 * created in 2013/03/08
 *
 * (C) Copyright 2003-2013 GreenDay Project. All rights reserved.
 */
package org.supercsv.ext.cellprocessor.ift;

import java.util.Map;


/**
 * Validation系のCellProcessor.
 * <p>エラー時のメッセージ出力時に使用する。
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
    String getMessageCode();
    
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
     * @param value
     * @return
     */
    String formateValue(Object value);
}

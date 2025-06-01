package com.github.mygreen.supercsv.builder;

import com.github.mygreen.supercsv.annotation.conversion.CsvFixedSize;
import com.github.mygreen.supercsv.cellprocessor.conversion.PaddingProcessor;

/**
 * アノテーション{@link CsvFixedSize}を元に、ヘッダーラベル情報を処理します。
 * <p>パディング文字がゼロ埋めのときや、文字数を超えたとき切り出す設定の場合、
 *    意図した結果とならない場合があるため、このクラスを参考に各自実装してください。
 * </p>
 * 
 * @version 2.5
 * @since 2.1
 * @author T.TSUCHIE
 *
 */
public class FixedSizeHeaderMapper implements HeaderMapper {
    
    @Override
    public String toMap(final ColumnMapping column, final Configuration config, final Class<?>[] groups) {
        
        FixedSizeColumnProperty fixedSizeProperty = column.getFixedSizeProperty();
        if (fixedSizeProperty == null) {
            // 固定長カラムでない場合
            return column.getLabel();
        }
        
        final PaddingProcessor paddingProcessor = fixedSizeProperty.getPaddingProcessor();
        String label = paddingProcessor.pad(column.getLabel(),
                fixedSizeProperty.getSize(), fixedSizeProperty.getPadChar(),
                fixedSizeProperty.isRightAlign(), fixedSizeProperty.isChopped());
        
        return label;
    }
    
}

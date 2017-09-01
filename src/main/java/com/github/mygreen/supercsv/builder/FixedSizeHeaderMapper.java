package com.github.mygreen.supercsv.builder;

import java.util.Optional;

import com.github.mygreen.supercsv.annotation.conversion.CsvFixedSize;
import com.github.mygreen.supercsv.cellprocessor.conversion.PaddingProcessor;

/**
 * アノテーション{@link CsvFixedSize}を元に、ヘッダーラベル情報を処理します。
 * <p>パディング文字がゼロ埋めのときや、文字数を超えたとき切り出す設定の場合、
 *    意図した結果とならない場合があるため、このクラスを参考に各自実装してください。
 * </p>
 * 
 * @since 2.1
 * @author T.TSUCHIE
 *
 */
public class FixedSizeHeaderMapper implements HeaderMapper {
    
    @Override
    public String toMap(final ColumnMapping column, final Configuration config, final Class<?>[] groups) {
        
        // @CsvFixedSizeアノテーションを取得する
        final Optional<CsvFixedSize> fixedLengthAnno = column.getField().getAnnotationsByGroup(CsvFixedSize.class, groups)
                .stream().findFirst();
        
        if(!fixedLengthAnno.isPresent()) {
            return column.getLabel();
        }
        
        final PaddingProcessor paddingProcessor = (PaddingProcessor)config.getBeanFactory()
                .create(fixedLengthAnno.get().paddingProcessor());
        
        // アノテーションが存在する場合は、その情報を使ってパディングする。
        String label = fixedLengthAnno.map(anno -> paddingProcessor.pad(column.getLabel(),
                anno.size(), anno.padChar(), anno.rightAlign(), anno.chopped()))
                .orElse(column.getLabel());
        
        return label;
    }
    
}

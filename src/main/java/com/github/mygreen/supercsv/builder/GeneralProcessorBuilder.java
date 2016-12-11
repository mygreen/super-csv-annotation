package com.github.mygreen.supercsv.builder;

import com.github.mygreen.supercsv.annotation.format.CsvFormat;
import com.github.mygreen.supercsv.cellprocessor.format.TextFormatter;
import com.github.mygreen.supercsv.exception.SuperCsvInvalidAnnotationException;
import com.github.mygreen.supercsv.localization.MessageBuilder;

/**
 * 不明なタイプの時の汎用のビルダクラス。
 * <p>アノテーション{@link CsvFormat}の指定は必須。</p>
 * 
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class GeneralProcessorBuilder<T> extends AbstractProcessorBuilder<T> {
    
    /**
     * {@inheritDoc}
     * @throws SuperCsvInvalidAnnotationException 必ず例外をスローする。
     */
    @Override
    protected TextFormatter<T> getDefaultFormatter(final FieldAccessor field, final Configuration config) {
        
        throw new SuperCsvInvalidAnnotationException(MessageBuilder.create("anno.required")
                .var("property", field.getNameWithClass())
                .varWithAnno("anno", CsvFormat.class)
                .format());
    }
    
}

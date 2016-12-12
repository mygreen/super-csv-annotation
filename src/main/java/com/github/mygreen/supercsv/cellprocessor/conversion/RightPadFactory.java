package com.github.mygreen.supercsv.cellprocessor.conversion;

import java.util.Optional;

import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;

import com.github.mygreen.supercsv.annotation.conversion.CsvRightPad;
import com.github.mygreen.supercsv.builder.Configuration;
import com.github.mygreen.supercsv.builder.FieldAccessor;
import com.github.mygreen.supercsv.cellprocessor.ConversionProcessorFactory;
import com.github.mygreen.supercsv.cellprocessor.format.TextFormatter;
import com.github.mygreen.supercsv.exception.SuperCsvInvalidAnnotationException;
import com.github.mygreen.supercsv.localization.MessageBuilder;

/**
 * アノテーション {@link CsvRightPad}をハンドリングして、{@link RightPad}を作成します。
 * 
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class RightPadFactory implements ConversionProcessorFactory<CsvRightPad> {
    
    @Override
    public Optional<CellProcessor> create(final CsvRightPad anno, final Optional<CellProcessor> next,
            final FieldAccessor field, final TextFormatter<?> formatter, final Configuration config) {
        
        if(anno.size() <= 0) {
            throw new SuperCsvInvalidAnnotationException(anno, MessageBuilder.create("anno.attr.min")
                    .var("property", field.getNameWithClass())
                    .varWithAnno("anno", anno.annotationType())
                    .var("attrName", "size")
                    .var("attrValue", anno.size())
                    .var("min", 1)
                    .format());
        }
        
        final RightPad processor = next.map(n -> new RightPad(anno.size(), anno.padChar(), (StringCellProcessor)n))
                .orElseGet(() -> new RightPad(anno.size(), anno.padChar()));
        
        return Optional.of(processor);
    }
    
}

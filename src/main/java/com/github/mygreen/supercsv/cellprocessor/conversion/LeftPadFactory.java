package com.github.mygreen.supercsv.cellprocessor.conversion;

import java.util.Optional;

import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;

import com.github.mygreen.supercsv.annotation.conversion.CsvLeftPad;
import com.github.mygreen.supercsv.builder.Configuration;
import com.github.mygreen.supercsv.builder.FieldAccessor;
import com.github.mygreen.supercsv.cellprocessor.ConversionProcessorFactory;
import com.github.mygreen.supercsv.cellprocessor.format.TextFormatter;
import com.github.mygreen.supercsv.exception.SuperCsvInvalidAnnotationException;
import com.github.mygreen.supercsv.localization.MessageBuilder;

/**
 * アノテーション {@link CsvLeftPad}をハンドリングして、{@link LeftPad}を作成します。
 * 
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class LeftPadFactory implements ConversionProcessorFactory<CsvLeftPad> {
    
    @Override
    public Optional<CellProcessor> create(final CsvLeftPad anno, final Optional<CellProcessor> next,
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
        
        final LeftPad processor = next.map(n -> new LeftPad(anno.size(), anno.padChar(), (StringCellProcessor)n))
                .orElseGet(() -> new LeftPad(anno.size(), anno.padChar()));
        
        return Optional.of(processor);
    }
    
}

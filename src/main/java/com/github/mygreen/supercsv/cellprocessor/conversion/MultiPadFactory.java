package com.github.mygreen.supercsv.cellprocessor.conversion;

import java.util.Optional;

import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;

import com.github.mygreen.supercsv.annotation.conversion.CsvMultiPad;
import com.github.mygreen.supercsv.builder.Configuration;
import com.github.mygreen.supercsv.builder.FieldAccessor;
import com.github.mygreen.supercsv.cellprocessor.ConversionProcessorFactory;
import com.github.mygreen.supercsv.cellprocessor.format.TextFormatter;
import com.github.mygreen.supercsv.exception.SuperCsvInvalidAnnotationException;
import com.github.mygreen.supercsv.localization.MessageBuilder;

/**
 * アノテーション{@link CsvMultiPad}を元に、CellProcesssor{@link MultiPad}を作成します。
 *
 * @since 2.1
 * @author T.TSUCHIE
 *
 */
public class MultiPadFactory implements ConversionProcessorFactory<CsvMultiPad> {
    
    @Override
    public Optional<CellProcessor> create(final CsvMultiPad anno, final Optional<CellProcessor> next,
            final FieldAccessor field, final TextFormatter<?> formatter, final Configuration config) {
        
        if(anno.size() <= 0) {
            throw new SuperCsvInvalidAnnotationException(anno,
                    MessageBuilder.create("anno.attr.min")
                        .var("property", field.getNameWithClass())
                        .varWithAnno("anno", anno.annotationType())
                        .var("attrName", "size")
                        .var("attrValue", anno.size())
                        .var("min", 1)
                        .format());
        }
        
        final PaddingProcessor paddingProcessor = (PaddingProcessor)config.getBeanFactory().create(anno.paddingProcessor());
        
        final MultiPad processor = next.map(n -> new MultiPad(anno.size(), anno.padChar(), anno.rightAlign(),
                    anno.chopped(), paddingProcessor, (StringCellProcessor)n))
                .orElseGet(() -> new MultiPad(anno.size(), anno.padChar(), anno.rightAlign(),
                        anno.chopped(), paddingProcessor));
        
        return Optional.of(processor);
    }
}

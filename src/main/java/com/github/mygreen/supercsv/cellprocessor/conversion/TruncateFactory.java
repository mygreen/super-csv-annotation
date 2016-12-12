package com.github.mygreen.supercsv.cellprocessor.conversion;

import java.util.Optional;

import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;

import com.github.mygreen.supercsv.annotation.conversion.CsvTruncate;
import com.github.mygreen.supercsv.builder.Configuration;
import com.github.mygreen.supercsv.builder.FieldAccessor;
import com.github.mygreen.supercsv.cellprocessor.ConversionProcessorFactory;
import com.github.mygreen.supercsv.cellprocessor.format.TextFormatter;
import com.github.mygreen.supercsv.exception.SuperCsvInvalidAnnotationException;
import com.github.mygreen.supercsv.localization.MessageBuilder;

/**
 * {@link CsvTruncate}をハンドリングして、{@link Truncate}を作成する。
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class TruncateFactory implements ConversionProcessorFactory<CsvTruncate> {
    
    @Override
    public Optional<CellProcessor> create(final CsvTruncate anno, final Optional<CellProcessor> next,
            final FieldAccessor field, final TextFormatter<?> formatter, final Configuration config) {
        
        if(anno.maxSize() <= 0) {
            throw new SuperCsvInvalidAnnotationException(anno, MessageBuilder.create("anno.attr.min")
                    .var("property", field.getNameWithClass())
                    .varWithAnno("anno", anno.annotationType())
                    .var("attrName", "maxSize")
                    .var("attrValue", anno.maxSize())
                    .var("min", 1)
                    .format());
        }
        
        final Truncate processor = next.map(n -> new Truncate(anno.maxSize(), anno.suffix(), (StringCellProcessor)n))
                .orElseGet(() -> new Truncate(anno.maxSize(), anno.suffix()));
        
        return Optional.of(processor);
    }
    
}

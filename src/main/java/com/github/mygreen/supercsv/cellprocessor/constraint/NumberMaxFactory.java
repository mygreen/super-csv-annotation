package com.github.mygreen.supercsv.cellprocessor.constraint;

import java.util.Optional;

import org.supercsv.cellprocessor.ift.CellProcessor;

import com.github.mygreen.supercsv.annotation.constraint.CsvNumberMax;
import com.github.mygreen.supercsv.builder.Configuration;
import com.github.mygreen.supercsv.builder.FieldAccessor;
import com.github.mygreen.supercsv.cellprocessor.ConstraintProcessorFactory;
import com.github.mygreen.supercsv.cellprocessor.format.TextFormatter;
import com.github.mygreen.supercsv.cellprocessor.format.TextParseException;
import com.github.mygreen.supercsv.exception.SuperCsvInvalidAnnotationException;
import com.github.mygreen.supercsv.localization.MessageBuilder;


/**
 * アノテーション{@link CsvNumberMax}をハンドリングして、{@link NumberMax}を作成する。
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class NumberMaxFactory<N extends Number & Comparable<N>> implements ConstraintProcessorFactory<CsvNumberMax> {
    
    @Override
    public Optional<CellProcessor> create(final CsvNumberMax anno, final Optional<CellProcessor> next,
            final FieldAccessor field, final TextFormatter<?> formatter, final Configuration config) {
        
        @SuppressWarnings("unchecked")
        final TextFormatter<N> typeFormatter = (TextFormatter<N>)formatter;
        
        final N max;
        try {
            max = typeFormatter.parse(anno.value());
            
        } catch(TextParseException e) {
            throw new SuperCsvInvalidAnnotationException(anno, MessageBuilder.create("anno.attr.invalidType")
                    .var("property", field.getNameWithClass())
                    .varWithAnno("anno", anno.annotationType())
                    .var("attrName", "value")
                    .var("attrValue", anno.value())
                    .varWithClass("type", field.getType())
                    .var("pattern", typeFormatter.getPattern().orElseGet(null))
                    .format(true), e);
        }
        
        final NumberMax<N> processor = next.map(n -> new NumberMax<N>(max, anno.inclusive(), typeFormatter, n))
                .orElseGet(() -> new NumberMax<N>(max, anno.inclusive(), typeFormatter));
        
        processor.setValidationMessage(anno.message());
        
        return Optional.of(processor);
    }
    
}
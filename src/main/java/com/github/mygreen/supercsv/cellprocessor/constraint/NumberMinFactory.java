package com.github.mygreen.supercsv.cellprocessor.constraint;

import java.util.Optional;

import org.supercsv.cellprocessor.ift.CellProcessor;

import com.github.mygreen.supercsv.annotation.constraint.CsvNumberMin;
import com.github.mygreen.supercsv.builder.Configuration;
import com.github.mygreen.supercsv.builder.FieldAccessor;
import com.github.mygreen.supercsv.cellprocessor.ConstraintProcessorFactory;
import com.github.mygreen.supercsv.cellprocessor.format.TextFormatter;
import com.github.mygreen.supercsv.cellprocessor.format.TextParseException;
import com.github.mygreen.supercsv.exception.SuperCsvInvalidAnnotationException;
import com.github.mygreen.supercsv.localization.MessageBuilder;


/**
 * アノテーション{@link CsvNumberMin}をハンドリングして、{@link NumberMin}を作成する。
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class NumberMinFactory<N extends Number & Comparable<N>> implements ConstraintProcessorFactory<CsvNumberMin> {
    
    @Override
    public Optional<CellProcessor> create(final CsvNumberMin anno, final Optional<CellProcessor> next,
            final FieldAccessor field, final TextFormatter<?> formatter, final Configuration config) {
        
        @SuppressWarnings("unchecked")
        final TextFormatter<N> typeFormatter = (TextFormatter<N>)formatter;
                
        final N min;
        try {
            min = typeFormatter.parse(anno.value());
            
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
        
        final NumberMin<N> processor = next.map(n -> new NumberMin<N>(min, anno.inclusive(), typeFormatter, n))
                .orElseGet(() -> new NumberMin<N>(min, anno.inclusive(), typeFormatter));
        
        processor.setValidationMessage(anno.message());
        
        return Optional.of(processor);
    }
    
}
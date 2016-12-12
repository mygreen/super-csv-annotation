package com.github.mygreen.supercsv.cellprocessor.constraint;

import java.util.Optional;

import org.supercsv.cellprocessor.ift.CellProcessor;

import com.github.mygreen.supercsv.annotation.constraint.CsvDateTimeMax;
import com.github.mygreen.supercsv.builder.Configuration;
import com.github.mygreen.supercsv.builder.FieldAccessor;
import com.github.mygreen.supercsv.cellprocessor.ConstraintProcessorFactory;
import com.github.mygreen.supercsv.cellprocessor.format.TextFormatter;
import com.github.mygreen.supercsv.cellprocessor.format.TextParseException;
import com.github.mygreen.supercsv.exception.SuperCsvInvalidAnnotationException;
import com.github.mygreen.supercsv.localization.MessageBuilder;

/**
 * アノテーション{@link CsvDateTimeMax}をハンドリングして、{@link DateTimeMax}を作成する。
 * 
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class DateTimeMaxFactory<T extends Comparable<T>> implements ConstraintProcessorFactory<CsvDateTimeMax> {
    
    @Override
    public Optional<CellProcessor> create(final CsvDateTimeMax anno, final Optional<CellProcessor> next,
            final FieldAccessor field, final TextFormatter<?> formatter, final Configuration config) {
        
        @SuppressWarnings("unchecked")
        final TextFormatter<T> typeFormatter = (TextFormatter<T>)formatter;
        
        final T max;
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
        
        final DateTimeMax<T> processor = next.map(n -> new DateTimeMax<T>(max, anno.inclusive(), typeFormatter, n))
                .orElseGet(() -> new DateTimeMax<T>(max, anno.inclusive(), typeFormatter));
        processor.setValidationMessage(anno.message());
        
        return Optional.of(processor);
    }
    
}

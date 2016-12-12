package com.github.mygreen.supercsv.cellprocessor.constraint;

import java.util.Optional;

import org.supercsv.cellprocessor.ift.CellProcessor;

import com.github.mygreen.supercsv.annotation.constraint.CsvDateTimeMin;
import com.github.mygreen.supercsv.builder.Configuration;
import com.github.mygreen.supercsv.builder.FieldAccessor;
import com.github.mygreen.supercsv.cellprocessor.ConstraintProcessorFactory;
import com.github.mygreen.supercsv.cellprocessor.format.TextFormatter;
import com.github.mygreen.supercsv.cellprocessor.format.TextParseException;
import com.github.mygreen.supercsv.exception.SuperCsvInvalidAnnotationException;
import com.github.mygreen.supercsv.localization.MessageBuilder;

/**
 * アノテーション{@link CsvDateTimeMin}をハンドリングして、{@link DateTimeMin}を作成する。
 * 
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class DateTimeMinFactory<T extends Comparable<T>> implements ConstraintProcessorFactory<CsvDateTimeMin> {
    
    @Override
    public Optional<CellProcessor> create(final CsvDateTimeMin anno, final Optional<CellProcessor> next,
            final FieldAccessor field, final TextFormatter<?> formatter, final Configuration config) {
        
        @SuppressWarnings("unchecked")
        final TextFormatter<T> typeFormatter = (TextFormatter<T>)formatter;
        
        final T min;
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
        
        final DateTimeMin<T> processor = next.map(n -> new DateTimeMin<T>(min, anno.inclusive(), typeFormatter, n))
                .orElseGet(() -> new DateTimeMin<T>(min, anno.inclusive(), typeFormatter));
        processor.setValidationMessage(anno.message());
        
        return Optional.of(processor);
    }
    
}

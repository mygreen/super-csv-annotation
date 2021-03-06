package com.github.mygreen.supercsv.cellprocessor.constraint;

import java.util.Optional;

import org.supercsv.cellprocessor.ift.CellProcessor;

import com.github.mygreen.supercsv.annotation.constraint.CsvDateTimeRange;
import com.github.mygreen.supercsv.builder.Configuration;
import com.github.mygreen.supercsv.builder.FieldAccessor;
import com.github.mygreen.supercsv.cellprocessor.ConstraintProcessorFactory;
import com.github.mygreen.supercsv.cellprocessor.format.TextFormatter;
import com.github.mygreen.supercsv.cellprocessor.format.TextParseException;
import com.github.mygreen.supercsv.exception.SuperCsvInvalidAnnotationException;
import com.github.mygreen.supercsv.localization.MessageBuilder;

/**
 * アノテーション{@link CsvDateTimeRange}をハンドリングして、{@link DateTimeRange}を作成する。
 * 
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class DateTimeRangeFactory<T extends Comparable<T>> implements ConstraintProcessorFactory<CsvDateTimeRange> {
    
    @Override
    public Optional<CellProcessor> create(final CsvDateTimeRange anno, final Optional<CellProcessor> next,
            final FieldAccessor field, final TextFormatter<?> formatter, final Configuration config) {
        
        @SuppressWarnings("unchecked")
        final TextFormatter<T> typeFormatter = (TextFormatter<T>)formatter;
        
        final T min;
        try {
            min = typeFormatter.parse(anno.min());
            
        } catch(TextParseException e) {
            throw new SuperCsvInvalidAnnotationException(anno, MessageBuilder.create("anno.attr.invalidType")
                    .var("property", field.getNameWithClass())
                    .varWithAnno("anno", anno.annotationType())
                    .var("attrName", "min")
                    .var("attrValue", anno.min())
                    .varWithClass("type", field.getType())
                    .var("pattern", typeFormatter.getPattern().orElseGet(null))
                    .format(true), e);
        }
        
        final T max;
        try {
            max = typeFormatter.parse(anno.max());
            
        } catch(TextParseException e) {
            throw new SuperCsvInvalidAnnotationException(anno, MessageBuilder.create("anno.attr.invalidType")
                    .var("property", field.getNameWithClass())
                    .varWithAnno("anno", anno.annotationType())
                    .var("attrName", "max")
                    .var("attrValue", anno.max())
                    .varWithClass("type", field.getType())
                    .var("pattern", typeFormatter.getPattern().orElseGet(null))
                    .format(true), e);
        }
        
        if(min.compareTo(max) > 0) {
            throw new SuperCsvInvalidAnnotationException(anno, MessageBuilder.create("anno.CsvDateTimeRange.minMaxWrong")
                    .var("property", field.getNameWithClass())
                    .varWithAnno("anno", anno.annotationType())
                    .var("minValue", anno.min())
                    .var("maxValue", anno.max())
                    .format(true));
            
        }
        
        final DateTimeRange<T> processor = next.map(n -> new DateTimeRange<T>(min, max, anno.inclusive(), typeFormatter, n))
                .orElseGet(() -> new DateTimeRange<T>(min, max, anno.inclusive(), typeFormatter));
        processor.setValidationMessage(anno.message());
        
        return Optional.of(processor);
    }
    
}

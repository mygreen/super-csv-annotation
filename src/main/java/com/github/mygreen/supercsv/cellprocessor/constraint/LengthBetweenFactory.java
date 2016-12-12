package com.github.mygreen.supercsv.cellprocessor.constraint;

import java.util.Optional;

import org.supercsv.cellprocessor.ift.CellProcessor;

import com.github.mygreen.supercsv.annotation.constraint.CsvLengthBetween;
import com.github.mygreen.supercsv.builder.Configuration;
import com.github.mygreen.supercsv.builder.FieldAccessor;
import com.github.mygreen.supercsv.cellprocessor.ConstraintProcessorFactory;
import com.github.mygreen.supercsv.cellprocessor.format.TextFormatter;
import com.github.mygreen.supercsv.exception.SuperCsvInvalidAnnotationException;
import com.github.mygreen.supercsv.localization.MessageBuilder;


/**
 * アノテーション{@link CsvLengthBetween}をハンドリングして、CellProcessorの{@link LengthBetween}を作成する。
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class LengthBetweenFactory implements ConstraintProcessorFactory<CsvLengthBetween> {
    
    @Override
    public Optional<CellProcessor> create(final CsvLengthBetween anno, final Optional<CellProcessor> next,
            final FieldAccessor field, final TextFormatter<?> formatter, final Configuration config) {
        
        final int min = anno.min();
        final int max = anno.max();
        
        if(min > max) {
            throw new SuperCsvInvalidAnnotationException(anno, MessageBuilder.create("anno.CsvLengthBetween.minMaxWrong")
                    .var("property", field.getNameWithClass())
                    .varWithAnno("anno", anno.annotationType())
                    .var("minValue", min)
                    .var("maxValue", max)
                    .format());
        }
        
        if(min < 0) {
            throw new SuperCsvInvalidAnnotationException(anno, MessageBuilder.create("anno.attr.min")
                    .var("property", field.getNameWithClass())
                    .varWithAnno("anno", anno.annotationType())
                    .var("attrName", "min")
                    .var("attrValue", min)
                    .var("min", 0)
                    .format());
        }
        
        final LengthBetween processor = next.map(n -> new LengthBetween(min, max, n))
                .orElseGet(() -> new LengthBetween(min, max));
        processor.setValidationMessage(anno.message());
        
        return Optional.of(processor);
    }
    
}

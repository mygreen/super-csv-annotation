package com.github.mygreen.supercsv.cellprocessor.constraint;

import java.util.Optional;

import org.supercsv.cellprocessor.ift.CellProcessor;

import com.github.mygreen.supercsv.annotation.constraint.CsvLengthMax;
import com.github.mygreen.supercsv.builder.Configuration;
import com.github.mygreen.supercsv.builder.FieldAccessor;
import com.github.mygreen.supercsv.cellprocessor.ConstraintProcessorFactory;
import com.github.mygreen.supercsv.cellprocessor.format.TextFormatter;
import com.github.mygreen.supercsv.exception.SuperCsvInvalidAnnotationException;
import com.github.mygreen.supercsv.localization.MessageBuilder;


/**
 * アノテーション{@link CsvLengthMax}をハンドリングして、CellProcessorの{@link LengthMax}を作成する。
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class LengthMaxFactory implements ConstraintProcessorFactory<CsvLengthMax> {
    
    @Override
    public Optional<CellProcessor> create(final CsvLengthMax anno, final Optional<CellProcessor> next,
            final FieldAccessor field, final TextFormatter<?> formatter, final Configuration config) {
        
        final int max = anno.value();
        
        if(max <= 0) {
            throw new SuperCsvInvalidAnnotationException(anno, MessageBuilder.create("anno.attr.min")
                    .var("property", field.getNameWithClass())
                    .varWithAnno("anno", anno.annotationType())
                    .var("attrName", "value")
                    .var("attrValue", max)
                    .var("min", 1)
                    .format());
        }
        
        final LengthMax processor = next.map(n -> new LengthMax(max, n))
                .orElseGet(() -> new LengthMax(max));
        processor.setValidationMessage(anno.message());
        
        return Optional.of(processor);
    }
    
}

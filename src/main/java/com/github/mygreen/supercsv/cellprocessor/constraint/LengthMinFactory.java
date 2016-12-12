package com.github.mygreen.supercsv.cellprocessor.constraint;

import java.util.Optional;

import org.supercsv.cellprocessor.ift.CellProcessor;

import com.github.mygreen.supercsv.annotation.constraint.CsvLengthMin;
import com.github.mygreen.supercsv.builder.Configuration;
import com.github.mygreen.supercsv.builder.FieldAccessor;
import com.github.mygreen.supercsv.cellprocessor.ConstraintProcessorFactory;
import com.github.mygreen.supercsv.cellprocessor.format.TextFormatter;
import com.github.mygreen.supercsv.exception.SuperCsvInvalidAnnotationException;
import com.github.mygreen.supercsv.localization.MessageBuilder;


/**
 * アノテーション{@link CsvLengthMin}をハンドリングして、CellProcessorの{@link LengthMin}を作成する。
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class LengthMinFactory implements ConstraintProcessorFactory<CsvLengthMin> {
    
    @Override
    public Optional<CellProcessor> create(final CsvLengthMin anno, final Optional<CellProcessor> next,
            final FieldAccessor field, final TextFormatter<?> formatter, final Configuration config) {
        
        final int min = anno.value();
        
        if(min < 0) {
            throw new SuperCsvInvalidAnnotationException(anno, MessageBuilder.create("anno.attr.min")
                    .var("property", field.getNameWithClass())
                    .varWithAnno("anno", anno.annotationType())
                    .var("attrName", "value")
                    .var("attrValue", min)
                    .var("min", 0)
                    .format());
        }
        
        final LengthMin processor = next.map(n -> new LengthMin(min, n))
                .orElseGet(() -> new LengthMin(min));
        processor.setValidationMessage(anno.message());
        
        return Optional.of(processor);
    }
    
}

package com.github.mygreen.supercsv.cellprocessor.constraint;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.supercsv.cellprocessor.ift.CellProcessor;

import com.github.mygreen.supercsv.annotation.constraint.CsvLengthExact;
import com.github.mygreen.supercsv.builder.Configuration;
import com.github.mygreen.supercsv.builder.FieldAccessor;
import com.github.mygreen.supercsv.cellprocessor.ConstraintProcessorFactory;
import com.github.mygreen.supercsv.cellprocessor.format.TextFormatter;
import com.github.mygreen.supercsv.exception.SuperCsvInvalidAnnotationException;
import com.github.mygreen.supercsv.localization.MessageBuilder;


/**
 * アノテーション{@link CsvLengthExact}をハンドリングして、CellProcessorの{@link LengthExact}を作成する。
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class LengthExactFactory implements ConstraintProcessorFactory<CsvLengthExact> {
    
    @Override
    public Optional<CellProcessor> create(final CsvLengthExact anno, final Optional<CellProcessor> next,
            final FieldAccessor field, final TextFormatter<?> formatter, final Configuration config) {
        
        final List<Integer> requriedLength = Arrays.stream(anno.value())
                .filter(l -> l >= 0)
                .sorted()
                .distinct()
                .boxed()
                .collect(Collectors.toList());
        
        if(requriedLength.isEmpty()) {
            throw new SuperCsvInvalidAnnotationException(anno, MessageBuilder.create("anno.attr.required")
                    .var("property", field.getNameWithClass())
                    .varWithAnno("anno", anno.annotationType())
                    .var("attrName", "value")
                    .format());
        }
        
        final LengthExact processor = next.map(n -> new LengthExact(requriedLength, n))
                .orElseGet(() -> new LengthExact(requriedLength));
        processor.setValidationMessage(anno.message());
        
        return Optional.of(processor);
    }
    
}

package com.github.mygreen.supercsv.cellprocessor.constraint;

import java.util.Optional;

import org.supercsv.cellprocessor.ift.CellProcessor;

import com.github.mygreen.supercsv.annotation.constraint.CsvUnique;
import com.github.mygreen.supercsv.builder.Configuration;
import com.github.mygreen.supercsv.builder.FieldAccessor;
import com.github.mygreen.supercsv.cellprocessor.ConstraintProcessorFactory;
import com.github.mygreen.supercsv.cellprocessor.format.TextFormatter;

/**
 * アノテーション{@link CsvUnique}をハンドリングして、CellProcessorの{@link Unique}を作成する。
 * 
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class UniqueFactory<T> implements ConstraintProcessorFactory<CsvUnique> {
    
    @Override
    public Optional<CellProcessor> create(final CsvUnique anno, final Optional<CellProcessor> next,
            final FieldAccessor field, final TextFormatter<?> formatter, final Configuration config) {
        
        @SuppressWarnings("unchecked")
        final TextFormatter<T> typeFormatter = (TextFormatter<T>)formatter;
        
        final Unique<T> processor = next.map(n -> new Unique<T>(typeFormatter, n))
                .orElseGet(() -> new Unique<T>(typeFormatter));
        
        processor.setValidationMessage(anno.message());
        
        return Optional.of(processor);
    }
    
}

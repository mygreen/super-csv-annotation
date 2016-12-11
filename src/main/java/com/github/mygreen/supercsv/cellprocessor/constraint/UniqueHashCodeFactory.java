package com.github.mygreen.supercsv.cellprocessor.constraint;

import java.util.Optional;

import org.supercsv.cellprocessor.ift.CellProcessor;

import com.github.mygreen.supercsv.annotation.constraint.CsvUniqueHashCode;
import com.github.mygreen.supercsv.builder.Configuration;
import com.github.mygreen.supercsv.builder.FieldAccessor;
import com.github.mygreen.supercsv.cellprocessor.ConstraintProcessorFactory;
import com.github.mygreen.supercsv.cellprocessor.format.TextFormatter;

/**
 * アノテーション{@link CsvUniqueHashCode}をハンドリングして、CellProcessorの{@link UniqueHashCode}を作成する。
 * 
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class UniqueHashCodeFactory<T> implements ConstraintProcessorFactory<CsvUniqueHashCode> {
    
    @Override
    public Optional<CellProcessor> create(final CsvUniqueHashCode anno, final Optional<CellProcessor> next,
            final FieldAccessor field, final TextFormatter<?> formatter, final Configuration config) {
        
        @SuppressWarnings("unchecked")
        final TextFormatter<T> typeFormatter = (TextFormatter<T>)formatter;
        
        final UniqueHashCode<T> processor = next.map(n -> new UniqueHashCode<T>(typeFormatter, n))
                .orElseGet(() -> new UniqueHashCode<T>(typeFormatter));
        
        processor.setValidationMessage(anno.message());
        
        return Optional.of(processor);
    }
    
}

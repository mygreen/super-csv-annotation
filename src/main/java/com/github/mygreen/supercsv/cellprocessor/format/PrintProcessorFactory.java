package com.github.mygreen.supercsv.cellprocessor.format;

import java.util.Optional;

import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;

import com.github.mygreen.supercsv.builder.BuildCase;
import com.github.mygreen.supercsv.builder.Configuration;
import com.github.mygreen.supercsv.builder.FieldAccessor;
import com.github.mygreen.supercsv.cellprocessor.ProcessorFactory;

/**
 * 各オブジェクトを文字列に変換するCellProcessor {@link PrintProcessor} を作成します。
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class PrintProcessorFactory<T> implements ProcessorFactory {

    @Override
    public Optional<CellProcessor> create(final Optional<CellProcessor> next, final FieldAccessor field,
            final TextFormatter<?> formatter, final Configuration config,
            final BuildCase buildCase, final Class<?>[] groups) {
        
        @SuppressWarnings("unchecked")
        final TextFormatter<T> typeFormatter = (TextFormatter<T>)formatter;
        
        final PrintProcessor<T> processor = next.map(n -> new PrintProcessor<>(typeFormatter, (StringCellProcessor)n))
                .orElseGet(() -> new PrintProcessor<>(typeFormatter));
        
        return Optional.of(processor);
        
    }
    
}

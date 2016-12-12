package com.github.mygreen.supercsv.cellprocessor.format;

import java.util.Optional;

import org.supercsv.cellprocessor.ift.CellProcessor;

import com.github.mygreen.supercsv.builder.BuildCase;
import com.github.mygreen.supercsv.builder.Configuration;
import com.github.mygreen.supercsv.builder.FieldAccessor;
import com.github.mygreen.supercsv.cellprocessor.ProcessorFactory;

/**
 * 文字列をパースして、各オブジェクト型に変換するCellProcessor {@link ParseProcessor}を作成する。
 * 
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class ParseProcessorFactory<T> implements ProcessorFactory {
    
    @SuppressWarnings("unchecked")
    @Override
    public Optional<CellProcessor> create(final Optional<CellProcessor> next, final FieldAccessor field,
            final TextFormatter<?> formatter, final Configuration config,
            final BuildCase buildCase, final Class<?>[] groups) {
        
        final Class<T> fieldType = (Class<T>)field.getType();
        final TextFormatter<T> typeFormatter = (TextFormatter<T>)formatter;
        
        final ParseProcessor<T> processor = next.map(n -> new ParseProcessor<>(fieldType, typeFormatter, n))
                .orElseGet(() -> new ParseProcessor<>(fieldType, typeFormatter));
        
        return Optional.of(processor);
        
    }
    
}

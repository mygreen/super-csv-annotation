package com.github.mygreen.supercsv.cellprocessor.conversion;

import java.util.Optional;

import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;

import com.github.mygreen.supercsv.annotation.conversion.CsvTrim;
import com.github.mygreen.supercsv.builder.Configuration;
import com.github.mygreen.supercsv.builder.FieldAccessor;
import com.github.mygreen.supercsv.cellprocessor.ConversionProcessorFactory;
import com.github.mygreen.supercsv.cellprocessor.format.TextFormatter;


/**
 * アノテーション{@link CsvTrim}をハンドリングして、{@link Trim}を作成する。
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class TrimFactory implements ConversionProcessorFactory<CsvTrim> {
    
    @Override
    public Optional<CellProcessor> create(final CsvTrim anno, final Optional<CellProcessor> next,
            final FieldAccessor field, final TextFormatter<?> formatter, final Configuration config) {
        
        final Trim processor = next.map(n -> new Trim((StringCellProcessor)n))
                .orElseGet(() -> new Trim());
        
        return Optional.of(processor);
    }
}

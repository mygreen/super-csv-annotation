package com.github.mygreen.supercsv.cellprocessor.conversion;

import java.util.Optional;

import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;

import com.github.mygreen.supercsv.annotation.conversion.CsvLower;
import com.github.mygreen.supercsv.builder.Configuration;
import com.github.mygreen.supercsv.builder.FieldAccessor;
import com.github.mygreen.supercsv.cellprocessor.ConversionProcessorFactory;
import com.github.mygreen.supercsv.cellprocessor.format.TextFormatter;

/**
 * アノテーション{@link CsvLower}をハンドリングして、{@link Lower}を作成する。
 * 
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class LowerFactory implements ConversionProcessorFactory<CsvLower> {
    
    @Override
    public Optional<CellProcessor> create(final CsvLower anno, final Optional<CellProcessor> next,
            final FieldAccessor field, final TextFormatter<?> formatter, final Configuration config) {
        
        final Lower processor = next.map(n ->  new Lower((StringCellProcessor) n))
                .orElseGet(() -> new Lower());
        
        return Optional.of(processor);
    }
    
}

package com.github.mygreen.supercsv.cellprocessor.conversion;

import java.util.Optional;

import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;

import com.github.mygreen.supercsv.annotation.conversion.CsvDefaultValue;
import com.github.mygreen.supercsv.builder.Configuration;
import com.github.mygreen.supercsv.builder.FieldAccessor;
import com.github.mygreen.supercsv.cellprocessor.ConversionProcessorFactory;
import com.github.mygreen.supercsv.cellprocessor.format.TextFormatter;

/**
 * アノテーション{@link CsvDefaultValue}ハンドリングして、{@link DefaultValue}を作成する。
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class DefaultValueFactory implements ConversionProcessorFactory<CsvDefaultValue> {
    
    @Override
    public Optional<CellProcessor> create(final CsvDefaultValue anno, final Optional<CellProcessor> next,
            final FieldAccessor field, final TextFormatter<?> formatter, final Configuration config) {
        
        final DefaultValue processor = next.map(n ->  new DefaultValue(anno.value(), (StringCellProcessor) n))
                .orElseGet(() -> new DefaultValue(anno.value()));
        
        return Optional.of(processor);
    }
    
}

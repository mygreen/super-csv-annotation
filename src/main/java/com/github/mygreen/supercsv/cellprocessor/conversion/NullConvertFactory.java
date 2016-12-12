package com.github.mygreen.supercsv.cellprocessor.conversion;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;

import com.github.mygreen.supercsv.annotation.conversion.CsvNullConvert;
import com.github.mygreen.supercsv.builder.Configuration;
import com.github.mygreen.supercsv.builder.FieldAccessor;
import com.github.mygreen.supercsv.cellprocessor.ConversionProcessorFactory;
import com.github.mygreen.supercsv.cellprocessor.format.TextFormatter;

/**
 * アノテーション{@link CsvNullConvert}をハンドリングして、任意の値をnull変換するためのCellProcessor{@link NullConvert}を作成する。
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class NullConvertFactory implements ConversionProcessorFactory<CsvNullConvert> {
    
    @Override
    public Optional<CellProcessor> create(final CsvNullConvert anno, final Optional<CellProcessor> next,
            final FieldAccessor field, final TextFormatter<?> formatter, final Configuration config) {
        
        final Set<String> tokens = Arrays.stream(anno.value())
                .collect(Collectors.toSet());
        
        final NullConvert processor = next.map(n -> new NullConvert(tokens, anno.ignoreCase(), (StringCellProcessor)n))
                .orElseGet(() -> new NullConvert(tokens, anno.ignoreCase()));
        
        return Optional.of(processor);
    }
    
}

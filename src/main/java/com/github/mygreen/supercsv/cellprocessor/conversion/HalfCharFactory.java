package com.github.mygreen.supercsv.cellprocessor.conversion;

import java.util.Optional;

import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;

import com.github.mygreen.supercsv.annotation.conversion.CsvHalfChar;
import com.github.mygreen.supercsv.builder.Configuration;
import com.github.mygreen.supercsv.builder.FieldAccessor;
import com.github.mygreen.supercsv.cellprocessor.ConversionProcessorFactory;
import com.github.mygreen.supercsv.cellprocessor.format.TextFormatter;

/**
 * アノテーション{@link CsvHalfChar}をハンドリングして、CellProcessorの{@link HalfChar}を作成する。
 * 
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class HalfCharFactory implements ConversionProcessorFactory<CsvHalfChar> {
    
    @Override
    public Optional<CellProcessor> create(final CsvHalfChar anno, final Optional<CellProcessor> next,
            final FieldAccessor field, final TextFormatter<?> formatter, final Configuration config) {
        
        final CharCategory[] categories;
        if(anno.categories().length == 0) {
            categories = CharCategory.values();
        } else {
            categories = anno.categories();
        }
        
        final HalfChar processor = next.map(n -> new HalfChar(categories, (StringCellProcessor) n))
                .orElseGet(() -> new HalfChar(categories));
        
        return Optional.of(processor);
    }
    
}

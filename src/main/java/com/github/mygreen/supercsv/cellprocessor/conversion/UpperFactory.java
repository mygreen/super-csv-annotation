package com.github.mygreen.supercsv.cellprocessor.conversion;

import java.util.Optional;

import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;

import com.github.mygreen.supercsv.annotation.conversion.CsvUpper;
import com.github.mygreen.supercsv.builder.Configuration;
import com.github.mygreen.supercsv.builder.FieldAccessor;
import com.github.mygreen.supercsv.cellprocessor.ConversionProcessorFactory;
import com.github.mygreen.supercsv.cellprocessor.format.TextFormatter;

/**
 * {@link Upper}を作成する
 * 
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class UpperFactory implements ConversionProcessorFactory<CsvUpper> {
    
    @Override
    public Optional<CellProcessor> create(final CsvUpper anno, final Optional<CellProcessor> next,
            final FieldAccessor field, final TextFormatter<?> formatter, final Configuration config) {
        
        final Upper processor = next.map(n ->  new Upper((StringCellProcessor) n))
                .orElseGet(() -> new Upper());
        
        return Optional.of(processor);
        
    }
    
}

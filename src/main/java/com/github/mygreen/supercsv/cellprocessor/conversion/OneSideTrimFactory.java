package com.github.mygreen.supercsv.cellprocessor.conversion;

import java.util.Optional;

import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;

import com.github.mygreen.supercsv.annotation.conversion.CsvOneSideTrim;
import com.github.mygreen.supercsv.builder.Configuration;
import com.github.mygreen.supercsv.builder.FieldAccessor;
import com.github.mygreen.supercsv.cellprocessor.ConversionProcessorFactory;
import com.github.mygreen.supercsv.cellprocessor.format.TextFormatter;

/**
 * アノテーション{@link CsvOneSideTrim}を元にCellProcessor{@link OneSideTrim}を作成するクラス。
 *
 * @since 2.1
 * @author T.TSUCHIE
 *
 */
public class OneSideTrimFactory implements ConversionProcessorFactory<CsvOneSideTrim> {

    @Override
    public Optional<CellProcessor> create(final CsvOneSideTrim anno, final Optional<CellProcessor> next,
            final FieldAccessor field, final TextFormatter<?> formatter, final Configuration config) {
        
        final OneSideTrim processor = next.map(n -> new OneSideTrim(anno.trimChar(), anno.leftAlign(), (StringCellProcessor)n))
                .orElseGet(() -> new OneSideTrim(anno.trimChar(), anno.leftAlign()));
        
        return Optional.of(processor);
    }
    
}

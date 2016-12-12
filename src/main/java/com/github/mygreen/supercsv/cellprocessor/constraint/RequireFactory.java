package com.github.mygreen.supercsv.cellprocessor.constraint;

import java.util.Optional;

import org.supercsv.cellprocessor.ift.CellProcessor;

import com.github.mygreen.supercsv.annotation.constraint.CsvRequire;
import com.github.mygreen.supercsv.builder.Configuration;
import com.github.mygreen.supercsv.builder.FieldAccessor;
import com.github.mygreen.supercsv.cellprocessor.ConstraintProcessorFactory;
import com.github.mygreen.supercsv.cellprocessor.format.TextFormatter;


/**
 * アノテーション{@link CsvRequire}をハンドリングして、CellProcessorの{@link Require}を作成する。
 * 
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class RequireFactory implements ConstraintProcessorFactory<CsvRequire> {
    
    @Override
    public Optional<CellProcessor> create(final CsvRequire anno, final Optional<CellProcessor> next,
            final FieldAccessor field, final TextFormatter<?> formatter, final Configuration config) {
        
        final Require processor = next.map(n -> new Require(anno.considerEmpty(), anno.considerBlank(), n))
            .orElseGet(() -> new Require(anno.considerEmpty(), anno.considerBlank()));
        
        processor.setValidationMessage(anno.message());
        
        return Optional.of(processor);
    }
    
}

package com.github.mygreen.supercsv.cellprocessor.constraint;

import java.util.Optional;
import java.util.regex.PatternSyntaxException;

import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;

import com.github.mygreen.supercsv.annotation.constraint.CsvPattern;
import com.github.mygreen.supercsv.builder.Configuration;
import com.github.mygreen.supercsv.builder.FieldAccessor;
import com.github.mygreen.supercsv.cellprocessor.ConstraintProcessorFactory;
import com.github.mygreen.supercsv.cellprocessor.format.TextFormatter;
import com.github.mygreen.supercsv.exception.SuperCsvInvalidAnnotationException;
import com.github.mygreen.supercsv.localization.MessageBuilder;
import com.github.mygreen.supercsv.util.Utils;

/**
 * アノテーション{@link CsvPattern}をハンドリングして、CellProcessorの{@link Pattern}を作成する。
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class PatternFactory implements ConstraintProcessorFactory<CsvPattern> {
    
    @Override
    public Optional<CellProcessor> create(final CsvPattern anno, final Optional<CellProcessor> next,
            final FieldAccessor field, final TextFormatter<?> formatter, final Configuration config) {
        
        final int flags = Utils.buildRegexFlags(anno.flags());
        final java.util.regex.Pattern pattern;
        try {
            pattern = java.util.regex.Pattern.compile(anno.regex(), flags);
            
        } catch(PatternSyntaxException e) {
            throw new SuperCsvInvalidAnnotationException(anno, MessageBuilder.create("anno.attr.invalidType")
                    .var("property", field.getNameWithClass())
                    .varWithAnno("anno", anno.annotationType())
                    .var("attrName", "regex")
                    .var("attrValue", anno.regex())
                    .var("type", "{key.regex}")
                    .format(true));
        }
        
        final Pattern processor = next.map(n -> new Pattern(pattern, anno.description(), (StringCellProcessor)n))
                .orElseGet(() -> new Pattern(pattern, anno.description()));
        
        processor.setValidationMessage(anno.message());
        
        return Optional.of(processor);
    }
    
}

package com.github.mygreen.supercsv.cellprocessor.conversion;

import java.util.Optional;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;

import com.github.mygreen.supercsv.annotation.conversion.CsvRegexReplace;
import com.github.mygreen.supercsv.builder.Configuration;
import com.github.mygreen.supercsv.builder.FieldAccessor;
import com.github.mygreen.supercsv.cellprocessor.ConversionProcessorFactory;
import com.github.mygreen.supercsv.cellprocessor.format.TextFormatter;
import com.github.mygreen.supercsv.exception.SuperCsvInvalidAnnotationException;
import com.github.mygreen.supercsv.localization.MessageBuilder;
import com.github.mygreen.supercsv.util.Utils;


/**
 * アノテーション{@link CsvRegexReplace}をハンドリングして、値を置換するCellProcessorの{@link RegexReplace}を追加する。
 * 
 * @version 2.2
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class RegexReplaceFactory implements ConversionProcessorFactory<CsvRegexReplace> {
    
    @Override
    public Optional<CellProcessor> create(final CsvRegexReplace anno, final Optional<CellProcessor> next,
            final FieldAccessor field, final TextFormatter<?> formatter, final Configuration config) {
        
        final int flags = Utils.buildRegexFlags(anno.flags());
        final Pattern pattern;
        try {
            pattern = Pattern.compile(anno.regex(), flags);
        } catch(PatternSyntaxException e) {
            throw new SuperCsvInvalidAnnotationException(anno, MessageBuilder.create("anno.attr.invalidType")
                    .var("property", field.getNameWithClass())
                    .varWithAnno("anno", anno.annotationType())
                    .var("attrName", "regex")
                    .var("attrValue", anno.regex())
                    .var("type", MessageBuilder.create("key.regex").format())
                    .format(true));
        }
        
        final String replacement = anno.replacement();
        final boolean partialMatched = anno.partialMatched();
        
        final RegexReplace processor = next.map(n ->  new RegexReplace(pattern, replacement, partialMatched, (StringCellProcessor) n))
                .orElseGet(() -> new RegexReplace(pattern, replacement, partialMatched));
        
        return Optional.of(processor);
    }
    
}

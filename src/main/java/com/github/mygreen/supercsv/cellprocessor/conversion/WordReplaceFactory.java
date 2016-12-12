package com.github.mygreen.supercsv.cellprocessor.conversion;

import java.util.Optional;

import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;

import com.github.mygreen.supercsv.annotation.conversion.CsvWordReplace;
import com.github.mygreen.supercsv.builder.Configuration;
import com.github.mygreen.supercsv.builder.FieldAccessor;
import com.github.mygreen.supercsv.cellprocessor.ConversionProcessorFactory;
import com.github.mygreen.supercsv.cellprocessor.format.TextFormatter;
import com.github.mygreen.supercsv.exception.SuperCsvInvalidAnnotationException;
import com.github.mygreen.supercsv.localization.MessageBuilder;

/**
 * アノテーション{@link CsvWordReplace}をハンドリングして、CellProcessorの{@link WordReplace}を作成する。
 * 
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class WordReplaceFactory implements ConversionProcessorFactory<CsvWordReplace> {
    
    @Override
    public Optional<CellProcessor> create(final CsvWordReplace anno, final Optional<CellProcessor> next,
            final FieldAccessor field, final TextFormatter<?> formatter, final Configuration config) {
        
        final CharReplacer replacer = new CharReplacer();
        
        final String[] words = anno.words();
        final String[] replacements = anno.replacements();
        
        if(words.length != replacements.length) {
            throw new SuperCsvInvalidAnnotationException(anno, MessageBuilder.create("anno.CsvWordReplace.invalidSize")
                    .var("property", field.getNameWithClass())
                    .varWithAnno("anno", anno.annotationType())
                    .var("wordsSize", words.length)
                    .var("replacementsSize", replacements.length)
                    .format());
        }
        
        final int size = words.length;
        for(int i=0; i < size; i++) {
            replacer.register(words[i], replacements[i]);
        }
        
        if(anno.provider().length > 0) {
            final ReplacedWordProvider provider = (ReplacedWordProvider) config.getBeanFactory().create(anno.provider()[0]);
            provider.getReplacedWords(field)
                .stream()
                .forEach(word -> replacer.register(word));
        }
        
        if(words.length == 0 && anno.provider().length == 0) {
            throw new SuperCsvInvalidAnnotationException(anno, MessageBuilder.create("anno.attr.required")
                    .var("property", field.getNameWithClass())
                    .varWithAnno("anno", anno.annotationType())
                    .var("attrName", "value or provider")
                    .format());
        }
        
        replacer.ready();
        
        final WordReplace processor = next.map(n -> new WordReplace(replacer, (StringCellProcessor) n))
                .orElseGet(() -> new WordReplace(replacer));
        
        return Optional.of(processor);
    }
    
}

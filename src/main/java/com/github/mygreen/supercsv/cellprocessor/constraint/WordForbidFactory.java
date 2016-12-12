package com.github.mygreen.supercsv.cellprocessor.constraint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.supercsv.cellprocessor.ift.CellProcessor;

import com.github.mygreen.supercsv.annotation.constraint.CsvWordForbid;
import com.github.mygreen.supercsv.builder.Configuration;
import com.github.mygreen.supercsv.builder.FieldAccessor;
import com.github.mygreen.supercsv.cellprocessor.ConstraintProcessorFactory;
import com.github.mygreen.supercsv.cellprocessor.format.TextFormatter;
import com.github.mygreen.supercsv.exception.SuperCsvInvalidAnnotationException;
import com.github.mygreen.supercsv.localization.MessageBuilder;

/**
 * アノテーション{@link CsvWordForbid}をハンドリングして、CellProcessorの{@link WordForbid}を作成する。
 * 
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class WordForbidFactory implements ConstraintProcessorFactory<CsvWordForbid> {
    
    @Override
    public Optional<CellProcessor> create(final CsvWordForbid anno, final Optional<CellProcessor> next,
            final FieldAccessor field, final TextFormatter<?> formatter, final Configuration config) {
        
        final List<String> words = new ArrayList<>();
        if(anno.value().length > 0) {
            words.addAll(Arrays.asList(anno.value()));
        }
            
        if(anno.provider().length > 0) {
            final ForbiddenWordProvider provider = (ForbiddenWordProvider) config.getBeanFactory().create(anno.provider()[0]);
            words.addAll(provider.getForbiddenWords(field));
            
        }
        
        if(anno.value().length == 0 && anno.provider().length == 0) {
            throw new SuperCsvInvalidAnnotationException(anno, MessageBuilder.create("anno.attr.required")
                    .var("property", field.getNameWithClass())
                    .varWithAnno("anno", anno.annotationType())
                    .var("attrName", "value or provider")
                    .format());
        }
        
        final WordForbid processor = next.map(n -> new WordForbid(words, n))
                .orElseGet(() -> new WordForbid(words));
        processor.setValidationMessage(anno.message());
        
        return Optional.of(processor);
    }
    
}

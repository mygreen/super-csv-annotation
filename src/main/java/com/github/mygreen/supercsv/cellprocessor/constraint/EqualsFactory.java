package com.github.mygreen.supercsv.cellprocessor.constraint;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.supercsv.cellprocessor.ift.CellProcessor;

import com.github.mygreen.supercsv.annotation.constraint.CsvEquals;
import com.github.mygreen.supercsv.builder.Configuration;
import com.github.mygreen.supercsv.builder.FieldAccessor;
import com.github.mygreen.supercsv.cellprocessor.ConstraintProcessorFactory;
import com.github.mygreen.supercsv.cellprocessor.format.TextFormatter;
import com.github.mygreen.supercsv.cellprocessor.format.TextParseException;
import com.github.mygreen.supercsv.exception.SuperCsvInvalidAnnotationException;
import com.github.mygreen.supercsv.localization.MessageBuilder;


/**
 * アノテーション{@link CsvEquals}をハンドリングして、CellProcessorの{@link Equals}を作成する。
 * 
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class EqualsFactory<T> implements ConstraintProcessorFactory<CsvEquals> {
    
    @SuppressWarnings("unchecked")
    @Override
    public Optional<CellProcessor> create(final CsvEquals anno, final Optional<CellProcessor> next,
            final FieldAccessor field, final TextFormatter<?> formatter, final Configuration config) {
        
        final TextFormatter<T> typeFormatter = (TextFormatter<T>)formatter;
        
        final List<T> equaledValues = new ArrayList<>();
        
        if(anno.value().length > 0) {
            for(String str : anno.value()) {
                try {
                    equaledValues.add(typeFormatter.parse(str));
                
                } catch(TextParseException e) {
                    throw new SuperCsvInvalidAnnotationException(anno, MessageBuilder.create("anno.attr.invalidType")
                            .var("property", field.getNameWithClass())
                            .varWithAnno("anno", anno.annotationType())
                            .var("attrName", "value")
                            .var("attrValue", str)
                            .varWithClass("type", field.getType())
                            .var("pattern", typeFormatter.getPattern().orElseGet(null))
                            .format(), e);
                }
            }
            
        }
        
        if(anno.provider().length > 0) {
            final EqualedValueProvider<T> provider = (EqualedValueProvider<T>) config.getBeanFactory().create(anno.provider()[0]);
            equaledValues.addAll(provider.getEqualedValues(field));
            
        }
        
        if(anno.value().length == 0 && anno.provider().length == 0) {
            throw new SuperCsvInvalidAnnotationException(anno, MessageBuilder.create("anno.attr.required")
                    .var("property", field.getNameWithClass())
                    .varWithAnno("anno", anno.annotationType())
                    .var("attrName", "value or provider")
                    .format());
        }
        
        final Class<T> fieldType = (Class<T>)field.getType();
        final Equals<T> processor = next.map(n -> new Equals<>(fieldType, equaledValues, typeFormatter, n))
                .orElseGet(() -> new Equals<>(fieldType, equaledValues, typeFormatter));
        
        processor.setValidationMessage(anno.message());
        
        return Optional.of(processor);
    }
    
}

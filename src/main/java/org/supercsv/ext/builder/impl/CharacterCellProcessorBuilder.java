package org.supercsv.ext.builder.impl;

import java.lang.annotation.Annotation;
import java.util.Optional;

import org.supercsv.cellprocessor.ParseChar;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.ift.DoubleCellProcessor;
import org.supercsv.ext.annotation.CsvColumn;
import org.supercsv.ext.builder.AbstractCellProcessorBuilder;
import org.supercsv.ext.util.Utils;import javafx.scene.control.Cell;


/**
 * char/Character型の{@link CellProcessor}のビルダクラス。
 * 
 * @since 1.2
 * @author T.TSUCHIE
 *
 */
public class CharacterCellProcessorBuilder extends AbstractCellProcessorBuilder<Character> {
    
    @Override
    protected CellProcessor buildInputCellProcessorWithConvertNullTo(final Class<Character> type, final Annotation[] annos,
            final CellProcessor cellProcessor, final CsvColumn csvColumnAnno) {
        
        // プリミティブ型の場合、オプションかつ初期値が与えられていない場合、'\u0000' に変換する。
        if(type.isPrimitive() && csvColumnAnno.optional() && csvColumnAnno.inputDefaultValue().isEmpty()) {
            return prependConvertNullToProcessor(type, annos, cellProcessor, '\u0000');
            
        } else if(!csvColumnAnno.inputDefaultValue().isEmpty()) {
            Optional<Character> value = parseValue(type, annos, csvColumnAnno.inputDefaultValue());
            return prependConvertNullToProcessor(type, annos, cellProcessor, value.get());
        }
        
        return cellProcessor;
    }
    
    @Override
    public CellProcessor buildOutputCellProcessor(final Class<Character> type, final Annotation[] annos,
            final CellProcessor processor, final boolean ignoreValidationProcessor) {
        return processor;
    }
    
    @Override
    public CellProcessor buildInputCellProcessor(final Class<Character> type, final Annotation[] annos, 
            final CellProcessor processor) {
        
        CellProcessor cp = processor;
        cp = (cp == null ? new ParseChar() : new ParseChar((DoubleCellProcessor) cp));
        
        return cp;
    }
    
    @Override
    public Optional<Character> parseValue(final Class<Character> type, final Annotation[] annos, final String strValue) {
        
        if(Utils.isEmpty(strValue)) {
            return Optional.empty();
        }
        
        return Optional.of(strValue.charAt(0));
    }
}

package com.github.mygreen.supercsv.cellprocessor.constraint;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;
import org.supercsv.util.CsvContext;

import com.github.mygreen.supercsv.cellprocessor.ValidationCellProcessor;


/**
 * 必須語彙を含んでいるか検証するCellProcessor.
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class WordRequire extends ValidationCellProcessor implements StringCellProcessor {
    
    private final Collection<String> words;
    
    public WordRequire(final Collection<String> words) {
        super();
        checkPreconditions(words);
        this.words = words.stream()
                .distinct()
                .collect(Collectors.toList());
        
    }
    
    public WordRequire(final Collection<String> words, final CellProcessor next) {
        super(next);
        checkPreconditions(words);
        this.words = words.stream()
                .distinct()
                .collect(Collectors.toList());
        
    }
    
    private static void checkPreconditions(final Collection<String> words) {
        if(words == null) {
            throw new NullPointerException("words and field should not be null.");
        }
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public Object execute(final Object value, final CsvContext context) {
        
        if(value == null) {
            return next.execute(value, context);
        }
        
        if(!words.isEmpty()) {
            final String stringValue = value.toString();
            
            final List<String> requiredWords = words.stream()
                    .filter(word -> !stringValue.contains(word))
                    .collect(Collectors.toList());
            
            if(!requiredWords.isEmpty()) {
                final String joinedWords = String.join(", ", requiredWords);
                throw createValidationException(context)
                    .messageFormat("'%s' does not contain any of the required substirng '%s'", stringValue, joinedWords)
                    .rejectedValue(stringValue)
                    .messageVariables("words", requiredWords)
                    .build();
            }
        }
        
        return next.execute(value, context);
    }
    
    /**
     * 必須語彙のを取得する。
     * @return 必須語彙
     */
    public Collection<String> getWords() {
        return words;
    }
    
}

package com.github.mygreen.supercsv.cellprocessor.constraint;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;
import org.supercsv.util.CsvContext;

import com.github.mygreen.supercsv.cellprocessor.ValidationCellProcessor;


/**
 * 禁止語彙を含んでいないか検証するCellProcessor.
 * 
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class WordForbid extends ValidationCellProcessor implements StringCellProcessor {
    
    private final Collection<String> words;
    
    public WordForbid(final Collection<String> words) {
        super();
        checkPreconditions(words);
        this.words = words.stream()
                .distinct()
                .collect(Collectors.toList());
    }
    
    public WordForbid(final Collection<String> words, final CellProcessor next) {
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
        
        final String stringValue = value.toString();
        
        final List<String> hitWords = words.stream()
                .filter(word -> stringValue.contains(word))
                .collect(Collectors.toList());
        
        if(!hitWords.isEmpty()) {
            final String joinedWords = String.join(", ", hitWords);
            throw createValidationException(context)
                .messageFormat("'%s' contains the forbidden substring '%s'", stringValue, joinedWords)
                .rejectedValue(stringValue)
                .messageVariables("words", hitWords)
                .build();
        }
        
        return next.execute(value, context);
    }
    
    /**
     * 禁止語彙を取得する。
     * @return 禁止語彙
     */
    public Collection<String> getWords() {
        return words;
    }
    
}

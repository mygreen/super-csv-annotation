package com.github.mygreen.supercsv.cellprocessor.conversion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.github.mygreen.supercsv.util.ArgUtils;
import com.github.mygreen.supercsv.util.Utils;

/**
 * 文字を置換するクラス
 * 
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class CharReplacer {
    
    /** 置換元が1文字の場合 */
    private final Map<Character, String> singles = new HashMap<>();
    
    /** 置換元が複数の文字の場合 */
    private final List<ReplacedWord> multi = new ArrayList<>();
    
    /**
     * 置換対象の文字を登録する。
     * @param word 置換対象の文字
     * @throws NullPointerException word is null.
     */
    public void register(final ReplacedWord word) {
        ArgUtils.notNull(word, "replacement");
        
        if(word.getWord().length() == 1) {
            singles.computeIfAbsent(word.getWord().charAt(0), key -> word.getReplacement());
            
        } else {
            multi.add(word);
        }
        
    }
    
    /**
     * 置換対象の文字を登録する。
     * @param word 置換対象の文字
     * @param replacement 置換後の文字
     * @throws IllegalArgumentException word is empty.
     * @throws NullPointerException replacement is null.
     */
    public void register(final String word, final String replacement) {
        ArgUtils.notEmpty(word, "word");
        ArgUtils.notNull(replacement, "replacement");
        
        if(word.length() == 1) {
            singles.computeIfAbsent(word.charAt(0), key -> replacement);
            
        } else {
            multi.add(new ReplacedWord(word, replacement));
        }
        
    }
    
    /**
     * 登録後に置換文字の整理を行うため、必ず実行すること。
     */
    public void ready() {
        
        // 複数の文字の場合、重複を排除する。
        final Set<String> duplicatedWords = new HashSet<>();
        final List<ReplacedWord> newMulti = new ArrayList<>();
        for(ReplacedWord word : multi) {
            if(duplicatedWords.contains(word.getWord())) {
                continue;
            }
            duplicatedWords.add(word.getWord());
            newMulti.add(word);
        }
        
        // 複数の文字の場合、文字長が長い順に並び替える。
        Collections.sort(newMulti, new Comparator<ReplacedWord>() {
            
            @Override
            public int compare(final ReplacedWord o1, final ReplacedWord o2) {
                
                final int length1 = o1.getWord().length();
                final int length2 = o2.getWord().length();
                if(length1 < length2) {
                    return 1;
                    
                } else if(length1 > length2) {
                    return -1;
                    
                } else {
                    return o1.getWord().compareTo(o2.getWord());
                }
            }
        });
        
        multi.clear();
        multi.addAll(newMulti);
        
    }
    
    /**
     * 登録された文字を元に置換する。
     * @param text 置換対象の文字
     * @return 置換した文字。置換対象の文字がnullまたは空文字の場合、置換しない。
     */
    public String replace(final String text) {
        
        if(Utils.isEmpty(text)) {
            return text;
        }
        
        final int length = text.length();
        StringBuilder replaced = new StringBuilder();
        int index = 0;
        
        while(index < length) {
            int multiIndex = replaceMulti(text, index, replaced);
            if(multiIndex > index) {
                index = multiIndex;
                continue;
            }
            
            int singleIndex = replaceSingle(text, index, replaced);
            if(singleIndex > index) {
                index = singleIndex;
                continue;
            }
            
            // 置換できるものがない場合
            replaced.append(text.charAt(index));
            index++;
            
        }
        
        return replaced.toString();
    }
    
    private int replaceSingle(final String source, final int index, final StringBuilder replaced) {
        
        final char c = source.charAt(index);
        if(singles.containsKey(c)) {
            replaced.append(singles.get(c));
            return index + 1;
        }
        
        return index;
        
    }
    
    private int replaceMulti(final String source, final int index, final StringBuilder replaced) {
        
        for(ReplacedWord word : multi) {
            if(source.indexOf(word.getWord(), index) == index) {
                replaced.append(word.getReplacement());
                return index + word.getWord().length();
            }
        }
        
        return index;
    }
    
}

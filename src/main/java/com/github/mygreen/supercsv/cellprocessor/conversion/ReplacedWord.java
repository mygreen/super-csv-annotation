package com.github.mygreen.supercsv.cellprocessor.conversion;

import java.io.Serializable;

import com.github.mygreen.supercsv.util.ArgUtils;

/**
 * 置換語彙を表現するクラス。
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class ReplacedWord implements Serializable {
    
    /** serialVersionUID */
    private static final long serialVersionUID = 1L;
    
    private final String word;
    
    private final String replacement;
    
    /**
     * 置換語彙のコンストラクタ。
     * @param word 置換対象の文字。
     * @param replacement 置換後の文字。
     * @throws NullPointerException word or replacement is null.
     * @throws IllegalArgumentException word is empty.
     */
    public ReplacedWord(final String word, final String replacement) {
        ArgUtils.notEmpty(word, "word");
        ArgUtils.notNull(replacement, "replacement");
        
        this.word = word;
        this.replacement = replacement;
    }
    
    /**
     * 置換対象の語彙を取得する。
     * @return 置換対象の語彙。
     */
    public String getWord() {
        return word;
    }
    
    /**
     * 置換後の文字列を返します。
     * @return 置換語彙の文字列
     */
    public String getReplacement() {
        return replacement;
    }
    
}

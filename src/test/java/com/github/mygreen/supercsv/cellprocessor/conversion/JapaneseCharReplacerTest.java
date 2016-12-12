package com.github.mygreen.supercsv.cellprocessor.conversion;

import static org.junit.Assert.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.Before;


import org.junit.Test;

/**
 * {@link JapaneseCharReplacer}のテスタ
 * 
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class JapaneseCharReplacerTest {
    
    private JapaneseCharReplacer replacerAll;
    private JapaneseCharReplacer replacerNoChar;
    private JapaneseCharReplacer replacerAlphaNumeric;
    
    @Before
    public void setUp() throws Exception {
        this.replacerAll = new JapaneseCharReplacer(CharCategory.values());
        this.replacerNoChar = new JapaneseCharReplacer();
        
        this.replacerAlphaNumeric = new JapaneseCharReplacer(CharCategory.Number, CharCategory.Alpha);
        
    }
    
    @Test
    public void convertFullChar_all() {
        
        assertThat(replacerAll.replaceToFullChar(null)).isEqualTo(null);
        assertThat(replacerAll.replaceToFullChar("")).isEqualTo("");
        
        assertThat(replacerAll.replaceToFullChar("abc_ABC_012 ｶﾞﾋﾟﾌﾟ")).isEqualTo("ａｂｃ＿ＡＢＣ＿０１２　カﾞピプ");
        
        assertThat(replacerAll.replaceToFullChar("こんにちは。Ｈｅｌｌｏ！")).isEqualTo("こんにちは。Ｈｅｌｌｏ！");
        
    }
    
    @Test
    public void convertHalfChar_all() {
        
        assertThat(replacerAll.replaceToHalfChar(null)).isEqualTo(null);
        assertThat(replacerAll.replaceToHalfChar("")).isEqualTo("");
        
        assertThat(replacerAll.replaceToHalfChar("ａｂｃ＿ＡＢＣ＿０１２　カﾞピプ")).isEqualTo("abc_ABC_012 ｶﾞﾋﾟﾌﾟ");
        
        assertThat(replacerAll.replaceToHalfChar("こんにちは。Hello!")).isEqualTo("こんにちは。Hello!");
        
    }
    
    @Test
    public void convertFullChar_noChar() {
        
        assertThat(replacerNoChar.replaceToFullChar(null)).isEqualTo(null);
        assertThat(replacerNoChar.replaceToFullChar("")).isEqualTo("");
        
        assertThat(replacerNoChar.replaceToFullChar("abc_ABC_012 ｶﾞﾋﾟﾌﾟ")).isEqualTo("abc_ABC_012 ｶﾞﾋﾟﾌﾟ");
        
    }
    
    @Test
    public void convertHalfChar_noChar() {
        
        assertThat(replacerNoChar.replaceToFullChar(null)).isEqualTo(null);
        assertThat(replacerNoChar.replaceToFullChar("")).isEqualTo("");
        
        assertThat(replacerNoChar.replaceToFullChar("ａｂｃ＿ＡＢＣ＿０１２　カﾞピプ")).isEqualTo("ａｂｃ＿ＡＢＣ＿０１２　カﾞピプ");
        
    }
    
    @Test
    public void convertFullChar_alphaNumeric() {
        
        assertThat(replacerAlphaNumeric.replaceToFullChar(null)).isEqualTo(null);
        assertThat(replacerAlphaNumeric.replaceToFullChar("")).isEqualTo("");
        
        assertThat(replacerAlphaNumeric.replaceToFullChar("abc_ABC_012 ｶﾞﾋﾟﾌﾟ")).isEqualTo("ａｂｃ_ＡＢＣ_０１２ ｶﾞﾋﾟﾌﾟ");
        
        assertThat(replacerAlphaNumeric.replaceToFullChar("こんにちは。Ｈｅｌｌｏ！")).isEqualTo("こんにちは。Ｈｅｌｌｏ！");
        
    }
    
    @Test
    public void convertHalfChar_alphaNumeric() {
        
        assertThat(replacerAlphaNumeric.replaceToHalfChar(null)).isEqualTo(null);
        assertThat(replacerAlphaNumeric.replaceToHalfChar("")).isEqualTo("");
        
        assertThat(replacerAlphaNumeric.replaceToHalfChar("ａｂｃ＿ＡＢＣ＿０１２　カﾞピプ")).isEqualTo("abc＿ABC＿012　カﾞピプ");
        
        assertThat(replacerAlphaNumeric.replaceToHalfChar("こんにちは。Hello!")).isEqualTo("こんにちは。Hello!");
        
    }
    
}

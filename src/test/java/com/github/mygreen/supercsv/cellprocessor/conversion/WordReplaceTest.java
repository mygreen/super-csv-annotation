package com.github.mygreen.supercsv.cellprocessor.conversion;

import static org.junit.Assert.*;
import static org.assertj.core.api.Assertions.*;
import static com.github.mygreen.supercsv.tool.TestUtils.*;

import org.junit.Before;
import org.junit.Test;

/**
 * {@link WordReplace}のテスタ
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class WordReplaceTest {
    
    private WordReplace processor;
    private WordReplace processorChain;
    
    private final CharReplacer replacer = new CharReplacer();
    {
        replacer.register("下さい", "ください");
        replacer.register("御願い", "お願い");
        replacer.ready();
    }
    
    @Before
    public void setUp() throws Exception {
        
        this.processor = new WordReplace(replacer);
        this.processorChain = new WordReplace(replacer, new NextCellProcessor());
        
    }
    
    @Test(expected=NullPointerException.class)
    public void testConstructor_replacerNull() {
        new WordReplace(null);
        fail();
    }
    
    @Test
    public void testExecute_inputNull() {
        assertThat((Object)processor.execute(null, ANONYMOUS_CSVCONTEXT)).isNull();
    }
    
    @Test
    public void testExecute_inputEmpty() {
        assertThat((Object)processor.execute("", ANONYMOUS_CSVCONTEXT)).isEqualTo("");
    }
    
    @Test
    public void testExecute_match() {
        
        String input = "送信をして下さい。御願い致します。";
        String expected = "送信をしてください。お願い致します。";
        
        assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
        assertThat((Object)processorChain.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
        
    }
    
    @Test
    public void testExecute_no_match() {
        
        String input = "こんにちは";
        String expected = "こんにちは";
        
        assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
        assertThat((Object)processorChain.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
        
    }
}

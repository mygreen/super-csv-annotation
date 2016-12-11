package com.github.mygreen.supercsv.cellprocessor.conversion;

import static org.junit.Assert.*;
import static org.assertj.core.api.Assertions.*;
import static com.github.mygreen.supercsv.tool.TestUtils.*;

import org.junit.Before;
import org.junit.Test;

/**
 * {@link FullChar}のテスタ
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class FullCharTest {
    
    private FullChar processor;
    private FullChar processorChain;
    
    private FullChar processorNumberAlpha;
    
    @Before
    public void setUp() throws Exception {
        this.processor = new FullChar(CharCategory.values());
        this.processorChain = new FullChar(CharCategory.values(), new NextCellProcessor());
        
        this.processorNumberAlpha = new FullChar(new CharCategory[]{CharCategory.Number, CharCategory.Alpha});
    }
    
    @Test(expected=NullPointerException.class)
    public void testConstructor_null() {
        new FullChar(null);
        fail();
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testConstructor_empty() {
        new FullChar(new CharCategory[]{});
        fail();
    }
    
    @Test
    public void testExecute_inputNull() {
        
        assertThat((Object)processor.execute(null, ANONYMOUS_CSVCONTEXT)).isNull();
        
    }
    
    @Test
    public void testExecute() {
        
        String input = "abc_ABC_012 !@";
        String expected = "ａｂｃ＿ＡＢＣ＿０１２　！＠";
        
        assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
        assertThat((Object)processorChain.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
        
        
    }
    
    @Test
    public void testExecute_numberAlpha() {
        
        String input = "abc_ABC_012 !@";
        String expected = "ａｂｃ_ＡＢＣ_０１２ !@";
        
        assertThat((Object)processorNumberAlpha.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
        
        
    }
    
    @Test
    public void testGetCategories() {
        
        assertThat(processor.getCategories()).containsExactly(CharCategory.values());
        
        assertThat(processorNumberAlpha.getCategories()).containsExactly(CharCategory.Number, CharCategory.Alpha);
    }
    
    
}

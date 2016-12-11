package com.github.mygreen.supercsv.cellprocessor.conversion;

import static org.junit.Assert.*;
import static org.assertj.core.api.Assertions.*;
import static com.github.mygreen.supercsv.tool.TestUtils.*;

import org.junit.Before;
import org.junit.Test;

/**
 * {@link HalfChar}のテスタ
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class HalfCharTest {
    
    private HalfChar processor;
    private HalfChar processorChain;
    
    private HalfChar processorNumberAlpha;
    
    @Before
    public void setUp() throws Exception {
        this.processor = new HalfChar(CharCategory.values());
        this.processorChain = new HalfChar(CharCategory.values(), new NextCellProcessor());
        
        this.processorNumberAlpha = new HalfChar(new CharCategory[]{CharCategory.Number, CharCategory.Alpha});
    }
    
    @Test(expected=NullPointerException.class)
    public void testConstructor_null() {
        new HalfChar(null);
        fail();
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testConstructor_empty() {
        new HalfChar(new CharCategory[]{});
        fail();
    }
    
    @Test
    public void testExecute_inputNull() {
        
        assertThat((Object)processor.execute(null, ANONYMOUS_CSVCONTEXT)).isNull();
        
    }
    
    @Test
    public void testExecute() {
        
        String input = "ａｂｃ＿ＡＢＣ＿０１２　！＠";
        String expected = "abc_ABC_012 !@";
        
        assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
        assertThat((Object)processorChain.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
        
        
    }
    
    @Test
    public void testExecute_numberAlpha() {
        
        String input = "ａｂｃ＿ＡＢＣ＿０１２　！＠";
        String expected = "abc＿ABC＿012　！＠";
        
        assertThat((Object)processorNumberAlpha.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
        
        
    }
    
    @Test
    public void testGetCategories() {
        
        assertThat(processor.getCategories()).containsExactly(CharCategory.values());
        
        assertThat(processorNumberAlpha.getCategories()).containsExactly(CharCategory.Number, CharCategory.Alpha);
    }
    
    
}

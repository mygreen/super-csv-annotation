package com.github.mygreen.supercsv.cellprocessor.conversion;

import static org.junit.Assert.*;
import static org.assertj.core.api.Assertions.*;
import static com.github.mygreen.supercsv.tool.TestUtils.*;

import org.junit.Before;
import org.junit.Test;
import org.supercsv.cellprocessor.ift.CellProcessor;


/**
 * {@link RightPad}のテスタ
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class RightPadTest {
    
    private CellProcessor processor;
    private CellProcessor processorChain;
    
    @Before
    public void setUp() throws Exception {
        this.processor = new RightPad(5, ' ');
        this.processorChain = new RightPad(5, ' ', new NextCellProcessor());
        
    }
    
    @Test
    public void testConstuctor_nextNull() {
        
        assertThatThrownBy(() -> new RightPad(-1, '_', null)).isInstanceOf(NullPointerException.class);
        
    }
    
    @Test
    public void testConstuctor_padSizeLessThanZero() {
        
        assertThatThrownBy(() -> new RightPad(0, '_')).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new RightPad(-1, '_')).isInstanceOf(IllegalArgumentException.class);
        
    }
    
    @Test
    public void testExecute_inputNull() {
        
        String input = null;
        String epxected = null;
        
        assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(epxected);
        assertThat((Object)processorChain.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(epxected);
        
    }
    
    /**
     * 入力文字がpadSizよりも大きい場合
     */
    @Test
    public void testExecute_inputGreaterThanPadSize() {
        
        String input = "abcdef";
        String epxected = "abcdef";
        
        assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(epxected);
        assertThat((Object)processorChain.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(epxected);
        
    }
    
    /**
     * 入力文字がpadSizと同じサイズの場合
     */
    @Test
    public void testExecute_inputSamePadSize() {
        
        String input = "abcde";
        String epxected = "abcde";
        
        assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(epxected);
        assertThat((Object)processorChain.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(epxected);
        
    }
    
    /**
     * 入力文字がpadSizよりも小さい場合
     */
    @Test
    public void testExecute_inputLessThanPadSize() {
        
        {
            String input = "abcd";
            String epxected = "abcd ";
            
            assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(epxected);
            assertThat((Object)processorChain.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(epxected);
        }
        
        {
            String input = "ab";
            String epxected = "ab   ";
            
            assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(epxected);
            assertThat((Object)processorChain.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(epxected);
        }
        
    }
    
    /**
     * 入力文字が空文字の場合
     */
    @Test
    public void testExecute_inputEmpty() {
        
        String input = "";
        String epxected = "     ";
        
        assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(epxected);
        assertThat((Object)processorChain.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(epxected);
        
    }
}

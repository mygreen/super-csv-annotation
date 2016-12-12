package com.github.mygreen.supercsv.cellprocessor.conversion;

import static org.junit.Assert.*;
import static org.assertj.core.api.Assertions.*;
import static com.github.mygreen.supercsv.tool.TestUtils.*;

import org.junit.Before;
import org.junit.Test;
import org.supercsv.cellprocessor.ift.CellProcessor;

/**
 * {@link Trim}のテスタ
 *
 * @version 2.0
 * @since 1.2
 * @author T.TSUCHIE
 *
 */
public class TrimTest {
    
    private CellProcessor processor;
    private CellProcessor processorChain;
    
    @Before
    public void setUp() {
        processor = new Trim();
        processorChain = new Trim(new NextCellProcessor());
    }
    
    @Test(expected=NullPointerException.class)
    public void testConstructor_nextNull() {
        new Trim(null);
        fail();
    }
    
    @Test
    public void testExecute_inputNoWhitespace() {
        String input = "abc";
        
        assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(input);
        assertThat((Object)processorChain.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(input);
    }
    
    @Test
    public void testExecute_inputSurroundingSpace() {
        String input = "  abc  ";
        String expected = "abc";
        
        assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
        assertThat((Object)processorChain.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
    }
    
    @Test
    public void testExecute_inputSurroundingWhitespace() {
        String input = "\tabc  \n";
        String expected = "abc";
        
        assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
        assertThat((Object)processorChain.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
    }
    
    @Test
    public void testExecute_inputNull() {
        assertThat((Object)processor.execute(null, ANONYMOUS_CSVCONTEXT)).isNull();
    }
}

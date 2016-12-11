package com.github.mygreen.supercsv.cellprocessor.conversion;

import static org.junit.Assert.*;
import static org.assertj.core.api.Assertions.*;
import static com.github.mygreen.supercsv.tool.TestUtils.*;

import org.junit.Before;
import org.junit.Test;
import org.supercsv.cellprocessor.ift.CellProcessor;

/**
 * {@link Lower}のテスタ。
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class LowerTest {
    
    private CellProcessor processor;
    private CellProcessor processorChain;
    
    @Before
    public void setUp() throws Exception {
        this.processor = new Lower();
        this.processorChain = new Lower(new NextCellProcessor());
    }
    
    @Test
    public void testExecute_inputNull() {
        
        assertThat((Object)processor.execute(null, ANONYMOUS_CSVCONTEXT)).isNull();;
        
    }
    
    @Test
    public void testExecute() {
        
        String input = "AbCd";
        String expected = "abcd";
        
        assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
        assertThat((Object)processorChain.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
        
    }
}

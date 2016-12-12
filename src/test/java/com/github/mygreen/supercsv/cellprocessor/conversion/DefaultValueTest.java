package com.github.mygreen.supercsv.cellprocessor.conversion;

import static org.junit.Assert.*;
import static org.assertj.core.api.Assertions.*;
import static com.github.mygreen.supercsv.tool.TestUtils.*;

import org.junit.Before;
import org.junit.Test;

/**
 * {@link DefaultValue}のテスタ
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class DefaultValueTest {
    
    private DefaultValue processor;
    private DefaultValue processorChain;
    
    private String returnValue = "123";
    
    @Before
    public void setUp() throws Exception {
        this.processor = new DefaultValue(returnValue);
        this.processorChain = new DefaultValue(returnValue, new NextCellProcessor());
    }
    
    @Test
    public void testConstructor() {
        
        assertThat(processor.getReturnValue()).isEqualTo(returnValue);
        
    }
    
    @Test
    public void testConstuctor_returnValueNull() {
        assertThat(new DefaultValue(null).getReturnValue()).isNull();
        
    }
    
    @Test
    public void testExecute_inputNull() {
        
        String input = null;
        
        assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(returnValue);
        assertThat((Object)processorChain.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(returnValue);
        
    }
    
    @Test
    public void testExecute_inputNotNull() {
        
        String input = "abc";
        
        assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(input);
        assertThat((Object)processorChain.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(input);
        
    }
    
    
}

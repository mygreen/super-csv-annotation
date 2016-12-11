package com.github.mygreen.supercsv.cellprocessor.conversion;

import static org.junit.Assert.*;
import static org.assertj.core.api.Assertions.*;
import static com.github.mygreen.supercsv.tool.TestUtils.*;

import org.junit.Before;
import org.junit.Test;
import org.supercsv.cellprocessor.ift.CellProcessor;

/**
 * {@link Truncate}のテスタ
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class TruncateTest {
    
    private static final int MAX_SIZE = 3;
    private static final String SUFFIX = "...";
    
    private CellProcessor processor;
    private CellProcessor processorChain;
    
    private CellProcessor processorSuffix;
    private CellProcessor processorSuffixChain;
    
    @Before
    public void setUp() {
        processor = new Truncate(MAX_SIZE, "");
        processorChain = new Truncate(MAX_SIZE, "", new NextCellProcessor());
        
        processorSuffix = new Truncate(MAX_SIZE, SUFFIX);
        processorSuffixChain = new Truncate(MAX_SIZE, SUFFIX, new NextCellProcessor());
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testConstructor_maxSizeZero() {
        new Truncate(0, SUFFIX);
        fail();
        
    }
    
    @Test(expected=NullPointerException.class)
    public void testConstructor_suffixNull() {
        new Truncate(MAX_SIZE, null);
        fail();
        
    }
    
    @Test(expected=NullPointerException.class)
    public void testConstructor_nextNull() {
        new Truncate(MAX_SIZE, SUFFIX, null);
        fail();
        
    }
    
    @Test
    public void testExecute_inputNull() {
        
        {
            String input = null;
            String expected = null;
            
            assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
            assertThat((Object)processorChain.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
        
        }
        
        {
            String input = null;
            String expected = null;
            
            assertThat((Object)processorSuffix.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
            assertThat((Object)processorSuffixChain.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
        
        }
    }
    
    @Test
    public void testExecute_inputSameAsMax() {
        
        {
            String input = "abc";
            String expected = "abc";
            
            assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
            assertThat((Object)processorChain.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
        
        }
        
        {
            String input = "abc";
            String expected = "abc";
            
            assertThat((Object)processorSuffix.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
            assertThat((Object)processorSuffixChain.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
        
        }
    }
    
    @Test
    public void testExecute_inputLongerThanMax() {
        
        {
            String input = "abcd";
            String expected = "abc";
            
            assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
            assertThat((Object)processorChain.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
        
        }
        
        {
            String input = "abcd";
            String expected = "abc...";
            
            assertThat((Object)processorSuffix.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
            assertThat((Object)processorSuffixChain.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
        
        }
    }
    
    
}

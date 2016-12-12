package com.github.mygreen.supercsv.cellprocessor.conversion;

import static org.junit.Assert.*;
import static org.assertj.core.api.Assertions.*;
import static com.github.mygreen.supercsv.tool.TestUtils.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.supercsv.cellprocessor.ift.CellProcessor;

import com.github.mygreen.supercsv.cellprocessor.NextCellProcessor;
import com.github.mygreen.supercsv.cellprocessor.conversion.NullConvert;

/**
 * {@link NullConvert}のテスタ
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class NullConvertTest {
    
    private CellProcessor processor;
    private CellProcessor processorChain;
    
    private CellProcessor ignoreProcessor;
    
    private static final List<String> TOKENS = Arrays.asList("-", "N/A");
    
    @Before
    public void setUp() throws Exception {
        this.processor = new NullConvert(TOKENS, false);
        this.processorChain = new NullConvert(TOKENS, false, new NextCellProcessor());
        
        this.ignoreProcessor = new NullConvert(TOKENS, true);
    }
    
    @Test(expected=NullPointerException.class)
    public void testConstructor_tokenNull() {
        
        new NullConvert(null, false);
        fail();
        
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testConstructor_tokenEmpty() {
        
        new NullConvert(Collections.emptyList(), false);
        fail();
        
    }
    
    @Test
    public void testConstructor_tokenDuplicate() {
        
        List<String> tokens = Arrays.asList("ABC", "efg", "ABC", "EFG");
        {
            NullConvert processor = new NullConvert(tokens, false);
            assertThat(processor.isIgnoreCase()).isEqualTo(false);
            assertThat(processor.getTokens()).containsExactly("ABC", "efg", "EFG");
        }
        
        {
            // ignoreCase
            NullConvert processor = new NullConvert(tokens, true);
            assertThat(processor.isIgnoreCase()).isEqualTo(true);
            assertThat(processor.getTokens()).containsExactly("abc", "efg");
        }
        
    }
    
    @Test
    public void testExecute_inputNull() {
        
        String input = null;
        
        assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isNull();
        assertThat((Object)processorChain.execute(input, ANONYMOUS_CSVCONTEXT)).isNull();
    }
    
    @Test
    public void testExecute_inputMatch() {
        
        String input = "N/A";
        
        assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isNull();
        assertThat((Object)processorChain.execute(input, ANONYMOUS_CSVCONTEXT)).isNull();
        
    }
    
    @Test
    public void testExecute_inputNotMatch() {
        
        String input = "Hello";
        
        assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(input);
        assertThat((Object)processorChain.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(input);
        
    }
    
    @Test
    public void testExecute_inputIgnoreCase() {
        
        String input = "N/a";
        
        assertThat((Object)ignoreProcessor.execute(input, ANONYMOUS_CSVCONTEXT)).isNull();
        
        
    }
    
}

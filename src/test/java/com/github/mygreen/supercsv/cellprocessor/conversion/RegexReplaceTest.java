package com.github.mygreen.supercsv.cellprocessor.conversion;

import static org.junit.Assert.*;

import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.*;
import static com.github.mygreen.supercsv.tool.TestUtils.*;

import org.junit.Before;
import org.junit.Test;
import org.supercsv.cellprocessor.ift.CellProcessor;

/**
 * {@link RegexReplace}のテスタ
 * 
 * @version 2.0
 * @since 1.2
 * @author T.TSUCHIE
 *
 */
public class RegexReplaceTest {
    
    private CellProcessor processor;
    private CellProcessor processorChain;
    
    private Pattern pattern = Pattern.compile("([0-9]{4})/([0-9]{1,2})/([0-9]{1,2})");
    private String replacement = "$1年$2月$3日";
    
    @Before
    public void setUp() throws Exception {
        
        this.processor = new RegexReplace(pattern, replacement);
        this.processorChain = new RegexReplace(pattern, replacement, new NextCellProcessor());
        
    }
    
    /**
     * Tests construction of the processor with a null
     */
    @Test(expected = NullPointerException.class)
    public void testCheckcondition_PatternNull() {
        
        new RegexReplace(null, replacement);
        fail();
    }
    
    /**
     * Tests construction of the processor with a null
     */
    @Test(expected = NullPointerException.class)
    public void testCheckcondition_ReplacementNull() {
        
        new RegexReplace(pattern, null);
        fail();
    }
    
    /**
     * Tests unchained/chained execution with matching.
     */
    @Test
    public void testExecute_match() {
        String input = "2011/1/25";
        String output = "2011年1月25日";
        
        assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(output);
        assertThat((Object)processorChain.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(output);
        
    }
    
    /**
     * Tests unchained/chained execution with matching.
     */
    @Test
    public void testExecute_no_match() {
        String input = "2011-1-25";
        String output = input;
        
        assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(output);
        assertThat((Object)processorChain.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(output);
        
    }
    
    /**
     * Tests execution with a null input (should throw an Exception).
     */
    @Test
    public void testExecute_inputNull() {
        
        assertThat((Object)processor.execute(null, ANONYMOUS_CSVCONTEXT)).isNull();
    }
}

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
    
    private CellProcessor partialProcessor;
    private CellProcessor partialProcessorChain;
    
    private Pattern pattern = Pattern.compile("([0-9]{4})/([0-9]{1,2})/([0-9]{1,2})");
    private String replacement = "$1年$2月$3日";
    
    private Pattern partialPattern = Pattern.compile("Word");
    private String partialReplacement = "Replace";
    
    @Before
    public void setUp() throws Exception {
        
        this.processor = new RegexReplace(pattern, replacement, false);
        this.processorChain = new RegexReplace(pattern, replacement, false, new NextCellProcessor());
        
        this.partialProcessor = new RegexReplace(partialPattern, partialReplacement, true);
        this.partialProcessorChain = new RegexReplace(partialPattern, partialReplacement, true, new NextCellProcessor());
        
    }
    
    /**
     * Tests construction of the processor with a null
     */
    @Test(expected = NullPointerException.class)
    public void testCheckcondition_PatternNull() {
        
        new RegexReplace(null, replacement, false);
        fail();
    }
    
    /**
     * Tests construction of the processor with a null
     */
    @Test(expected = NullPointerException.class)
    public void testCheckcondition_ReplacementNull() {
        
        new RegexReplace(pattern, null, false);
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
     * Tests unchained/chained execution with partial matching.
     * @since 2.2
     */
    @Test
    public void testExecute_partial_match() {
        
        String input = "xxxWordyyyWordzzz";
        String output = "xxxReplaceyyyReplacezzz";

        assertThat((Object)partialProcessor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(output);
        assertThat((Object)partialProcessorChain.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(output);
    }
    
    /**
     * Tests unchained/chained execution with partial matching.
     * @since 2.2
     */
    @Test
    public void testExecute_partial_no_match() {
        
        String input = "xxxwwwyyyy";
        String output = input;

        assertThat((Object)partialProcessor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(output);
        assertThat((Object)partialProcessorChain.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(output);
    }
    
    /**
     * Tests execution with a null input (should throw an Exception).
     */
    @Test
    public void testExecute_inputNull() {
        
        assertThat((Object)processor.execute(null, ANONYMOUS_CSVCONTEXT)).isNull();
    }
    
}

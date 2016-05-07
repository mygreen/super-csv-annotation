package org.supercsv.ext.cellprocessor;

import static org.junit.Assert.*;
import static org.supercsv.ext.tool.TestUtils.*;
import static org.hamcrest.Matchers.*;

import java.util.regex.Pattern;
import org.junit.Before;
import org.junit.Test;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.exception.SuperCsvCellProcessorException;

/**
 * {@link StrReplace}のテスタ。
 *
 * @since 1.2
 * @author T.TSUCHIE
 *
 */
public class StrReplaceTest {
    
    private CellProcessor processor;
    private CellProcessor processorChain;
    
    private Pattern pattern = Pattern.compile("([0-9]{4})/([0-9]{1,2})/([0-9]{1,2})");
    private String replacement = "$1年$2月$3日";
    
    /**
     * Sets up the processor for the test using Combinations
     */
    @Before
    public void setUp() {
        
        processor = new StrReplace(pattern, replacement);
        processorChain = new StrReplace(pattern, replacement, new NextCellProcessor());
    }
    
    /**
     * Tests construction of the processor with a null
     */
    @Test(expected = NullPointerException.class)
    public void testCheckcondition_PatternNull() {
        
        new StrReplace(null, replacement);
        fail();
    }
    
    /**
     * Tests construction of the processor with a null
     */
    @Test(expected = NullPointerException.class)
    public void testCheckcondition_ReplacementNull() {
        
        new StrReplace(pattern, null);
        fail();
    }
    
    /**
     * Tests unchained/chained execution with matching.
     */
    @Test
    public void testExecute_match() {
        String input = "2011/1/25";
        String output = "2011年1月25日";
        
        assertThat(processor.execute(input, ANONYMOUS_CSVCONTEXT), is(output));
        assertThat(processorChain.execute(input, ANONYMOUS_CSVCONTEXT), is(output));
        
    }
    
    /**
     * Tests unchained/chained execution with matching.
     */
    @Test
    public void testExecute_no_match() {
        String input = "2011-1-25";
        String output = input;
        
        assertThat(processor.execute(input, ANONYMOUS_CSVCONTEXT), is(output));
        assertThat(processorChain.execute(input, ANONYMOUS_CSVCONTEXT), is(output));
        
    }
    
    /**
     * Tests execution with a null input (should throw an Exception).
     */
    @Test(expected = SuperCsvCellProcessorException.class)
    public void testExecuteWithNull() {
        
        processor.execute(null, ANONYMOUS_CSVCONTEXT);
        fail();
    }
    
    
}

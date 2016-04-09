package org.supercsv.ext.cellprocessor;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;
import static org.supercsv.ext.TestUtils.*;

import org.junit.Before;
import org.junit.Test;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.exception.SuperCsvCellProcessorException;

/**
 * Tests the {@link Trim} processor.
 *
 * @since 1.2
 * @author T.TSUCHIE
 *
 */
public class TrimTest {
    
    private CellProcessor processor;
    private CellProcessor processorChain;
    
    /**
     * Sets up the processors for the test using all constructor combinations.
     */
    @Before
    public void setUp() {
        processor = new Trim();
        processorChain = new Trim(new NextCellProcessor());
    }
    
    /**
     * Tests unchained/chained execution with input containing no surrounding whitespace.
     */
    @Test
    public void testInputNoWhitespace() {
        String input = "abc";
        
        assertThat(processor.execute(input, ANONYMOUS_CSVCONTEXT), is(input));
        assertThat(processorChain.execute(input, ANONYMOUS_CSVCONTEXT), is(input));
    }
    
    /**
     * Tests unchained/chained execution with input containing surrounding spaces.
     */
    @Test
    public void testInputSurroundingSpace() {
        String input = "  abc  ";
        String expected = "abc";
        
        assertThat(processor.execute(input, ANONYMOUS_CSVCONTEXT), is(expected));
        assertThat(processorChain.execute(input, ANONYMOUS_CSVCONTEXT), is(expected));
    }
    
    /**
     * Tests unchained/chained execution with input containing surrounding whitespace.
     */
    @Test
    public void testInputSurroundingWhitespace() {
        String input = "\tabc  \n";
        String expected = "abc";
        
        assertThat(processor.execute(input, ANONYMOUS_CSVCONTEXT), is(expected));
        assertThat(processorChain.execute(input, ANONYMOUS_CSVCONTEXT), is(expected));
    }
    
    /**
     * Tests execution with a null input (should throw an Exception).
     */
    @Test(expected = SuperCsvCellProcessorException.class)
    public void testWithNull() {
        processor.execute(null, ANONYMOUS_CSVCONTEXT);
        fail();
    }
    
}

package org.supercsv.ext.cellprocessor;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;
import static org.supercsv.ext.TestUtils.*;

import java.math.BigInteger;

import org.junit.Before;
import org.junit.Test;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.exception.SuperCsvCellProcessorException;

/**
 * Tests the {@link ParseBigInteger} processor.
 *
 * @since 1.2
 * @author T.TSUCHIE
 *
 */
public class ParseBigIntegerTest {
    
    private CellProcessor processor;
    private CellProcessor processorChain;
    
    private BigInteger positiveVal = new BigInteger("12345678910123456789");
    private String positiveStr = "12345678910123456789";
    
    private BigInteger negativeVal = new BigInteger("-2468013579");
    private String negativeStr = "-2468013579";
    
    /**
     * Sets up the processor for the test using Combinations
     */
    @Before
    public void setUp() {
        
        processor = new ParseBigInteger();
        processorChain = new ParseBigInteger(new NextCellProcessor());
    }
    
    /**
     * Tests unchained/chained execution with valid floats as input.
     */
    @Test
    public void testExecuteWithValidAtBigInteger() {
        
        assertThat(processor.execute(positiveStr, ANONYMOUS_CSVCONTEXT), is(positiveVal));
        assertThat(processorChain.execute(positiveStr, ANONYMOUS_CSVCONTEXT), is(positiveVal));
        
        assertThat(processor.execute(negativeStr, ANONYMOUS_CSVCONTEXT), is(negativeVal));
        assertThat(processorChain.execute(negativeStr, ANONYMOUS_CSVCONTEXT), is(negativeVal));
    }
    
    /**
     * Tests unchained/chained execution with valid floats as input.
     */
    @Test
    public void testExecuteWithValidAtString() {
        
        assertThat(processor.execute(positiveVal, ANONYMOUS_CSVCONTEXT), is(positiveVal));
        assertThat(processorChain.execute(positiveVal, ANONYMOUS_CSVCONTEXT), is(positiveVal));
        
        assertThat(processor.execute(negativeVal, ANONYMOUS_CSVCONTEXT), is(negativeVal));
        assertThat(processorChain.execute(negativeVal, ANONYMOUS_CSVCONTEXT), is(negativeVal));
    }
    
    /**
     * Tests unchained/chained execution with valid floats as input.
     */
    @Test(expected = SuperCsvCellProcessorException.class)
    public void testExecuteWithInvalidAtFormatString() {
        
        processor.execute("17.3", ANONYMOUS_CSVCONTEXT);
        fail();
    }
    
    /**
     * Tests unchained/chained execution with valid floats as input.
     */
    @Test(expected = SuperCsvCellProcessorException.class)
    public void testExecuteWithInvalidAtNonBigInteger() {
        
        processor.execute(1.23, ANONYMOUS_CSVCONTEXT);
        fail();
    }
    
    /**
     * Tests execution with a null input (should throw an Exception).
     */
    @Test(expected = SuperCsvCellProcessorException.class)
    public void testExecuteWithInvalidAtNull() {
        
        processor.execute(null, ANONYMOUS_CSVCONTEXT);
        fail();
    }
    
}

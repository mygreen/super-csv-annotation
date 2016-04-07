package org.supercsv.ext.cellprocessor.constraint;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;
import static org.supercsv.ext.TestUtils.*;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.exception.SuperCsvCellProcessorException;
import org.supercsv.exception.SuperCsvConstraintViolationException;

/**
 * Tests the {@link MinLength} constraint.
 *
 * @since 1.2
 * @author T.TSUCHIE
 *
 */
public class MinLengthTest {
    
    private CellProcessor processor;
    private CellProcessor processorChain;
    
    private String minStr = "abcdefghij";
    private int minLength = minStr.length();
    
    /**
     * Sets up the processor for the test using Combinations
     */
    @Before
    public void setUp() {
        processor = new MinLength(minLength);
        processorChain = new MinLength(minLength, new NextCellProcessor());
    }
    
    /**
     * Test constructor argument's with wrong values.
     * max is null.
     */
    @Test(expected=IllegalArgumentException.class)
    public void testCheckConditionsWithWrong_max0() {
        
        new MinLength(-1);
        
        fail();
        
    }
    
    /**
     * Test constructor argument's with wrong values.
     * next is null.
     */
    @Test(expected=NullPointerException.class)
    public void testCheckConditionsWithWrong_nextNull() {
        
        new MinLength(minLength, null);
        
        fail();
        
    }
    
    /**
     * Test execusion with a null input.
     */
    @Test(expected=SuperCsvCellProcessorException.class)
    public void testExecuteWithNull() {
        
        processor.execute(null, ANONYMOUS_CSVCONTEXT);
        fail();
        
    }
    
    /**
     * Test execusion with valid value.
     */
    @Test
    public void testExecuteWithValid() {
        
        processor.execute(minStr, ANONYMOUS_CSVCONTEXT);
        processor.execute(minStr + "k", ANONYMOUS_CSVCONTEXT);
        
        processorChain.execute(minStr, ANONYMOUS_CSVCONTEXT);
        processorChain.execute(minStr + "k", ANONYMOUS_CSVCONTEXT);
        
    }
    
    /**
     * Test execusion with invalidValue.
     */
    @Test(expected=SuperCsvConstraintViolationException.class)
    public void testExecuteWithAvovbMinLength() {
        
        processor.execute(minStr.substring(0, minLength -1), ANONYMOUS_CSVCONTEXT);
        
        fail();
        
    }
    
    /**
     * Tests min value.
     */
    @Test
    public void testMin() {
        
        MinLength cp = (MinLength) processor;
        assertThat(cp.getMin(), is(minLength));
        
    }
    
    /**
     * Tests message code.
     */
    @Test
    public void testMessageCode() {
        
        MinLength cp = (MinLength) processor;
        assertThat(cp.getMessageCode(), is("org.supercsv.ext.cellprocessor.constraint.MinLength.violated"));
        
    }
    
    /**
     * Tests message variables
     */
    @Test
    public void testMessageVariable() {
        
        MinLength cp = (MinLength) processor;
        Map<String, ?> vars = cp.getMessageVariable();
        assertThat(vars.get("min"), is(minLength));
        
    }
    
    /**
     * Tests format values
     */
    @Test
    public void testFormatValue() {
        
        MinLength cp = (MinLength) processor;
        assertThat(cp.formatValue(null), is(""));
        assertThat(cp.formatValue(minLength), is("10"));
    }
    
}

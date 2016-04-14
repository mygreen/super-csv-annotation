package org.supercsv.ext.cellprocessor.constraint;

import static org.junit.Assert.*;
import static org.supercsv.ext.tool.TestUtils.*;
import static org.hamcrest.Matchers.*;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.exception.SuperCsvCellProcessorException;
import org.supercsv.exception.SuperCsvConstraintViolationException;

/**
 * Tests the {@link MaxLength} constraint.
 *
 * @since 1.2
 * @author T.TSUCHIE
 *
 */
public class MaxLengthTest {
    
    private CellProcessor processor;
    private CellProcessor processorChain;
    
    private String maxStr = "abcdefghij";
    private int maxLength = maxStr.length();
    
    /**
     * Sets up the processor for the test using Combinations
     */
    @Before
    public void setUp() {
        processor = new MaxLength(maxLength);
        processorChain = new MaxLength(maxLength, new NextCellProcessor());
    }
    
    /**
     * Test constructor argument's with wrong values.
     * max is null.
     */
    @Test(expected=IllegalArgumentException.class)
    public void testCheckConditionsWithWrong_max0() {
        
        new MaxLength(0);
        
        fail();
        
    }
    
    /**
     * Test constructor argument's with wrong values.
     * next is null.
     */
    @Test(expected=NullPointerException.class)
    public void testCheckConditionsWithWrong_nextNull() {
        
        new MaxLength(maxLength, null);
        
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
        
        processor.execute(maxStr, ANONYMOUS_CSVCONTEXT);
        processor.execute(maxStr.substring(0, maxLength-1), ANONYMOUS_CSVCONTEXT);
        
        processorChain.execute(maxStr, ANONYMOUS_CSVCONTEXT);
        processorChain.execute(maxStr.substring(0, maxLength-1) , ANONYMOUS_CSVCONTEXT);
        
    }
    
    /**
     * Test execusion with invalidValue.
     */
    @Test(expected=SuperCsvConstraintViolationException.class)
    public void testExecuteWithAvovbMaxLength() {
        
        processor.execute(maxStr + "k", ANONYMOUS_CSVCONTEXT);
        
        fail();
        
    }
    
    /**
     * Tests max value.
     */
    @Test
    public void testMax() {
        
        MaxLength cp = (MaxLength) processor;
        assertThat(cp.getMax(), is(maxLength));
        
    }
    
    /**
     * Tests message code.
     */
    @Test
    public void testMessageCode() {
        
        MaxLength cp = (MaxLength) processor;
        assertThat(cp.getMessageCode(), is("org.supercsv.ext.cellprocessor.constraint.MaxLength.violated"));
        
    }
    
    /**
     * Tests message variables
     */
    @Test
    public void testMessageVariable() {
        
        MaxLength cp = (MaxLength) processor;
        Map<String, ?> vars = cp.getMessageVariable();
        assertThat(vars, hasEntry("max", maxLength));
        
    }
    
    /**
     * Tests format values
     */
    @Test
    public void testFormatValue() {
        
        MaxLength cp = (MaxLength) processor;
        assertThat(cp.formatValue(null), is(""));
        assertThat(cp.formatValue(maxLength), is("10"));
    }
    
}

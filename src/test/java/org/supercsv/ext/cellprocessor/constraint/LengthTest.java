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


public class LengthTest {
    
    private CellProcessor processor;
    private CellProcessor processorChain;
    
    private String minStr = "abcde";
    private int minLength = minStr.length();
    
    private String maxStr = "abcdefghij";
    private int maxLength = maxStr.length();
    
    /**
     * Sets up the processor for the test using Combinations
     */
    @Before
    public void setUp() {
        processor = new Length(minLength, maxLength);
        processorChain = new Length(minLength, maxLength, new NextCellProcessor());
    }
    
    /**
     * Test constructor argument's with wrong values.
     * min is null.
     */
    @Test(expected=IllegalArgumentException.class)
    public void testCheckConditionsWithWrong_maxLessThanMin() {
        
        new Length(maxLength, minLength);
        
        fail();
        
    }
    
    /**
     * Test constructor argument's with wrong values.
     * min is null.
     */
    @Test(expected=IllegalArgumentException.class)
    public void testCheckConditionsWithWrong_minLessThan0() {
        
        new Length(-1, maxLength);
        
        fail();
        
    }
    
    /**
     * Test constructor argument's with wrong values.
     * next is null.
     */
    @Test(expected=NullPointerException.class)
    public void testCheckConditionsWithWrong_nextNull() {
        
        new Length(minLength, maxLength, null);
        
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
        processor.execute(minStr + "f", ANONYMOUS_CSVCONTEXT);
        processor.execute(maxStr, ANONYMOUS_CSVCONTEXT);
        processor.execute(maxStr.substring(0, maxLength-1), ANONYMOUS_CSVCONTEXT);
        
        processorChain.execute(minStr, ANONYMOUS_CSVCONTEXT);
        processorChain.execute(minStr + "f", ANONYMOUS_CSVCONTEXT);
        processorChain.execute(maxStr, ANONYMOUS_CSVCONTEXT);
        processorChain.execute(maxStr.substring(0, maxLength-1), ANONYMOUS_CSVCONTEXT);
        
    }
    
    /**
     * Test execusion with invalidValue.
     */
    @Test(expected=SuperCsvConstraintViolationException.class)
    public void testExecuteWithAvovbeMax() {
        
        processor.execute(maxStr + "k", ANONYMOUS_CSVCONTEXT);
        
        fail();
        
    }
    
    /**
     * Test execusion with invalidValue.
     */
    @Test(expected=SuperCsvConstraintViolationException.class)
    public void testExecuteWithBelowMin() {
        
        processor.execute(minStr.substring(0, minLength-1), ANONYMOUS_CSVCONTEXT);
        
        fail();
        
    }
    
    /**
     * Tests min value.
     */
    @Test
    public void testMin() {
        
        Length cp = (Length) processor;
        assertThat(cp.getMin(), is(minLength));
        
    }
    
    /**
     * Tests max value.
     */
    @Test
    public void testMax() {
        
        Length cp = (Length) processor;
        assertThat(cp.getMax(), is(maxLength));
        
    }
    
    /**
     * Tests message code.
     */
    @Test
    public void testMessageCode() {
        
        Length cp = (Length) processor;
        assertThat(cp.getMessageCode(), is("org.supercsv.ext.cellprocessor.constraint.Length.violated"));
        
    }
    
    /**
     * Tests message variables..
     */
    @Test
    public void testMessageVariable() {
        
        Length cp = (Length) processor;
        Map<String, ?> vars = cp.getMessageVariable();
        assertThat(vars.get("min"), is(minLength));
        assertThat(vars.get("max"), is(maxLength));
        
    }
    
    /**
     * Tests format value
     */
    @Test
    public void testFormatValue() {
        
        Length cp = (Length) processor;
        assertThat(cp.formatValue(null), is(""));
        assertThat(cp.formatValue(minLength), is("5"));
    }
}

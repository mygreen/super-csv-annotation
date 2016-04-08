package org.supercsv.ext.cellprocessor.constraint;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;
import static org.supercsv.ext.TestUtils.*;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.exception.SuperCsvCellProcessorException;
import org.supercsv.exception.SuperCsvConstraintViolationException;

/**
 * Tests the {@link Max} constraint.
 *
 * @since 1.2
 * @author T.TSUCHIE
 *
 */
public class MaxTest {
    
    private CellProcessor processor;
    private CellProcessor processorChain;
    private Integer max = new Integer(100);
    
    /**
     * Sets up the processor for the test using Combinations
     */
    @Before
    public void setUp() {
        processor = new Max<Integer>(max);
        processorChain = new Max<Integer>(max, new NextCellProcessor());
    }
    
    /**
     * Test constructor argument's with wrong values.
     * max is null.
     */
    @Test(expected=NullPointerException.class)
    public void testCheckConditionsWithWrong_maxNull() {
        
        new Max<Integer>(null);
        
        fail();
        
    }
    
    /**
     * Test constructor argument's with wrong values.
     * next is null.
     */
    @Test(expected=NullPointerException.class)
    public void testCheckConditionsWithWrong_nextNull() {
        
        new Max<Integer>(max, null);
        
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
     * Test execusion with a not same instance input.
     */
    @Test(expected=SuperCsvCellProcessorException.class)
    public void testExecuteWithNonSameInstance() {
        
        processor.execute("abc", ANONYMOUS_CSVCONTEXT);
        fail();
        
    }
    
    /**
     * Test execusion with valid value.
     */
    @Test
    public void testExecuteWithValid() {
        
        processor.execute(max, ANONYMOUS_CSVCONTEXT);
        processor.execute(max -1, ANONYMOUS_CSVCONTEXT);
        
        processorChain.execute(max, ANONYMOUS_CSVCONTEXT);
        processorChain.execute(max -1 , ANONYMOUS_CSVCONTEXT);
        
    }
    
    /**
     * Test execusion with invalidValue.
     */
    @Test(expected=SuperCsvConstraintViolationException.class)
    public void testExecuteWithAvovbMax() {
        
        processor.execute(max + 1, ANONYMOUS_CSVCONTEXT);
        
        fail();
        
    }
    
    /**
     * Tests max value.
     */
    @Test
    public void testMax() {
        
        Max<Integer> cp = (Max<Integer>) processor;
        assertThat(cp.getMax(), is(max));
        
    }
    
    /**
     * Tests message code.
     */
    @Test
    public void testMessageCode() {
        
        Max<Integer> cp = (Max<Integer>) processor;
        assertThat(cp.getMessageCode(), is("org.supercsv.ext.cellprocessor.constraint.Max.violated"));
        
    }
    
    /**
     * Tests message variables
     */
    @Test
    public void testMessageVariable() {
        
        Max<Integer> cp = (Max<Integer>) processor;
        Map<String, ?> vars = cp.getMessageVariable();
        assertThat(vars.get("max"), is(max));
        
    }
    
    /**
     * Tests format values
     */
    @Test
    public void testFormatValue() {
        
        Max<Integer> cp = (Max<Integer>) processor;
        assertThat(cp.formatValue(null), is(""));
        assertThat(cp.formatValue(max), is("100"));
        assertThat(cp.formatValue("abc"), is("abc"));
    }
    
    /**
     * Tests set formatter
     */
    @Test
    public void testFormatter() {
        
        Max<Integer> cp = (Max<Integer>) processor;
        NumberFormat formatter = new DecimalFormat("###,###");
        cp.setFormatter(formatter);
        
        assertThat(cp.formatValue(123456), is("123,456"));
        
    }
}

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
 * Tests the {@link Min} constraint.
 *
 * @since 1.2
 * @author T.TSUCHIE
 *
 */
public class MinTest {
    
    private CellProcessor processor;
    private CellProcessor processorChain;
    private Integer min = new Integer(100);
    
    /**
     * Sets up the processor for the test using Combinations
     */
    @Before
    public void setUp() {
        processor = new Min<Integer>(min);
        processorChain = new Min<Integer>(min, new NextCellProcessor());
    }
    
    /**
     * Test constructor argument's with wrong values.
     * min is null.
     */
    @Test(expected=NullPointerException.class)
    public void testCheckConditionsWithWrong_minNull() {
        
        new Min<Integer>(null);
        
        fail();
        
    }
    
    /**
     * Test constructor argument's with wrong values.
     * next is null.
     */
    @Test(expected=NullPointerException.class)
    public void testCheckConditionsWithWrong_nextNull() {
        
        new Min<Integer>(min, null);
        
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
        
        processor.execute(min, ANONYMOUS_CSVCONTEXT);
        processor.execute(min + 1, ANONYMOUS_CSVCONTEXT);
        
        processorChain.execute(min, ANONYMOUS_CSVCONTEXT);
        processorChain.execute(min + 1, ANONYMOUS_CSVCONTEXT);
        
    }
    
    /**
     * Test execusion with invalidValue.
     */
    @Test(expected=SuperCsvConstraintViolationException.class)
    public void testExecuteWithBelowMin() {
        
        processor.execute(min - 1, ANONYMOUS_CSVCONTEXT);
        
        fail();
        
    }
    
    /**
     * Tests min value.
     */
    @Test
    public void testMin() {
        
        Min<Integer> cp = (Min<Integer>) processor;
        assertThat(cp.getMin(), is(min));
        
    }
    
    /**
     * Tests message code.
     */
    @Test
    public void testMessageCode() {
        
        Min<Integer> cp = (Min<Integer>) processor;
        assertThat(cp.getMessageCode(), is("org.supercsv.ext.cellprocessor.constraint.Min.violated"));
        
    }
    
    /**
     * Tests message variables
     */
    @Test
    public void testMessageVariable() {
        
        Min<Integer> cp = (Min<Integer>) processor;
        Map<String, ?> vars = cp.getMessageVariable();
        assertThat(vars.get("min"), is(min));
        
    }
    
    /**
     * Tests format value.
     */
    @Test
    public void testFormatValue() {
        
        Min<Integer> cp = (Min<Integer>) processor;
        assertThat(cp.formatValue(null), is(""));
        assertThat(cp.formatValue(min), is("100"));
        assertThat(cp.formatValue("abc"), is("abc"));
    }
    
    /**
     * Tests set formatter
     */
    @Test
    public void testFormatter() {
        
        Min<Integer> cp = (Min<Integer>) processor;
        NumberFormat formatter = new DecimalFormat("###,###");
        cp.setFormatter(formatter);
        
        assertThat(cp.formatValue(123456), is("123,456"));
        
    }
}

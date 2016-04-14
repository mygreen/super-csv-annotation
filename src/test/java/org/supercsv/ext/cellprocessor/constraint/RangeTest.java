package org.supercsv.ext.cellprocessor.constraint;

import static org.junit.Assert.*;
import static org.supercsv.ext.tool.TestUtils.*;
import static org.hamcrest.Matchers.*;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.exception.SuperCsvCellProcessorException;
import org.supercsv.exception.SuperCsvConstraintViolationException;

/**
 * Tests the {@link Range} constraint.
 *
 * @since 1.2
 * @author T.TSUCHIE
 *
 */
public class RangeTest {
    
    private CellProcessor processor;
    private CellProcessor processorChain;
    private Integer min = new Integer(100);
    private Integer max = new Integer(110);
    
    /**
     * Sets up the processor for the test using Combinations
     */
    @Before
    public void setUp() {
        processor = new Range<Integer>(min, max);
        processorChain = new Range<Integer>(min, max, new NextCellProcessor());
    }
    
    /**
     * Test constructor argument's with wrong values.
     * min is null.
     */
    @Test(expected=NullPointerException.class)
    public void testCheckConditionsWithWrong_minNull() {
        
        new Range<Integer>(null, max);
        
        fail();
        
    }
    
    /**
     * Test constructor argument's with wrong values.
     * next is null.
     */
    @Test(expected=NullPointerException.class)
    public void testCheckConditionsWithWrong_nextNull() {
        
        new Range<Integer>(min, max, null);
        
        fail();
        
    }
    
    /**
     * Test constructor argument's with wrong values.
     * min > max
     */
    @Test(expected=IllegalArgumentException.class)
    public void testCheckConditionsWithWrong_maxLessThanMin() {
        
        new Range<Integer>(max, min);
        
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
        processor.execute(max, ANONYMOUS_CSVCONTEXT);
        processor.execute(max - 1, ANONYMOUS_CSVCONTEXT);
        
        processorChain.execute(min, ANONYMOUS_CSVCONTEXT);
        processorChain.execute(min + 1, ANONYMOUS_CSVCONTEXT);
        processorChain.execute(max, ANONYMOUS_CSVCONTEXT);
        processorChain.execute(max - 1, ANONYMOUS_CSVCONTEXT);
        
    }
    
    /**
     * Test execusion with invalidValue.
     */
    @Test(expected=SuperCsvConstraintViolationException.class)
    public void testExecuteWithAvovbeMax() {
        
        processor.execute(max + 1, ANONYMOUS_CSVCONTEXT);
        
        fail();
        
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
        
        Range<Integer> cp = (Range<Integer>) processor;
        assertThat(cp.getMin(), is(min));
        
    }
    
    /**
     * Tests max value.
     */
    @Test
    public void testMax() {
        
        Range<Integer> cp = (Range<Integer>) processor;
        assertThat(cp.getMax(), is(max));
        
    }
    
    /**
     * Tests message code.
     */
    @Test
    public void testMessageCode() {
        
        Range<Integer> cp = (Range<Integer>) processor;
        assertThat(cp.getMessageCode(), is("org.supercsv.ext.cellprocessor.constraint.Range.violated"));
        
    }
    
    /**
     * Tests message variables..
     */
    @Test
    public void testMessageVariable() {
        
        Range<Integer> cp = (Range<Integer>) processor;
        Map<String, ?> vars = cp.getMessageVariable();
        assertThat(vars, hasEntry("min", min));
        assertThat(vars, hasEntry("max", max));
        
    }
    
    /**
     * Tests format value
     */
    @Test
    public void testFormatValue() {
        
        Range<Integer> cp = (Range<Integer>) processor;
        assertThat(cp.formatValue(null), is(""));
        assertThat(cp.formatValue(min), is("100"));
        assertThat(cp.formatValue("abc"), is("abc"));
    }
    
    /**
     * Tests set formatter
     */
    @Test
    public void testFormatter() {
        
        Range<Integer> cp = (Range<Integer>) processor;
        NumberFormat formatter = new DecimalFormat("###,###");
        cp.setFormatter(formatter);
        
        assertThat(cp.formatValue(123456), is("123,456"));
        
    }
    
}

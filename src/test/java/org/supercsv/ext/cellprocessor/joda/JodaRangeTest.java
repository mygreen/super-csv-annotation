package org.supercsv.ext.cellprocessor.joda;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;
import static org.supercsv.ext.TestUtils.*;

import java.util.Map;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.exception.SuperCsvCellProcessorException;
import org.supercsv.exception.SuperCsvConstraintViolationException;

/**
 * Tests the {@link JodaRange} constraint.
 *
 * @since 1.2
 * @author T.TSUCHIE
 *
 */
public class JodaRangeTest {
    
    private CellProcessor processor;
    private CellProcessor processorChain;
    private LocalDate min = new LocalDate(2000, 1, 1);
    private LocalDate max = new LocalDate(2000, 1, 5);
    
    /**
     * Sets up the processor for the test using Combinations
     */
    @Before
    public void setUp() {
        processor = new JodaRange<LocalDate>(min, max);
        processorChain = new JodaRange<LocalDate>(min, max, new NextCellProcessor());
    }
    
    /**
     * Test constructor argument's with wrong values.
     * min is null.
     */
    @Test(expected=IllegalArgumentException.class)
    public void testCheckConditionsWithWrong_minNull() {
        
        new JodaRange<LocalDate>(null, max);
        
        fail();
        
    }
    
    /**
     * Test constructor argument's with wrong values.
     * next is null.
     */
    @Test(expected=NullPointerException.class)
    public void testCheckConditionsWithWrong_nextNull() {
        
        new JodaRange<LocalDate>(min, max, null);
        
        fail();
        
    }
    
    /**
     * Test constructor argument's with wrong values.
     * min > max
     */
    @Test(expected=IllegalArgumentException.class)
    public void testCheckConditionsWithWrong_maxLessThanMin() {
        
        new JodaRange<LocalDate>(max, min);
        
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
        processor.execute(min.plusDays(1), ANONYMOUS_CSVCONTEXT);
        processor.execute(max, ANONYMOUS_CSVCONTEXT);
        processor.execute(max.minusDays(1), ANONYMOUS_CSVCONTEXT);
        
        processorChain.execute(min, ANONYMOUS_CSVCONTEXT);
        processorChain.execute(min.plusDays(1), ANONYMOUS_CSVCONTEXT);
        processorChain.execute(max, ANONYMOUS_CSVCONTEXT);
        processorChain.execute(max.minusDays(1), ANONYMOUS_CSVCONTEXT);
        
    }
    
    /**
     * Test execusion with invalidValue.
     */
    @Test(expected=SuperCsvConstraintViolationException.class)
    public void testExecuteWithAvovbeMax() {
        
        processor.execute(max.plusDays(1), ANONYMOUS_CSVCONTEXT);
        
        fail();
        
    }
    
    /**
     * Test execusion with invalidValue.
     */
    @Test(expected=SuperCsvConstraintViolationException.class)
    public void testExecuteWithBelowMin() {
        
        processor.execute(min.minusDays(1), ANONYMOUS_CSVCONTEXT);
        
        fail();
        
    }
    
    /**
     * Tests min value.
     */
    @Test
    public void testMin() {
        
        JodaRange<LocalDate> cp = (JodaRange<LocalDate>) processor;
        assertThat(cp.getMin(), is(min));
        
    }
    
    /**
     * Tests max value.
     */
    @Test
    public void testTax() {
        
        JodaRange<LocalDate> cp = (JodaRange<LocalDate>) processor;
        assertThat(cp.getMax(), is(max));
        
    }
    
    /**
     * Tests message code.
     */
    @Test
    public void testMessageCode() {
        
        JodaRange<LocalDate> cp = (JodaRange<LocalDate>) processor;
        assertThat(cp.getMessageCode(), is("org.supercsv.ext.cellprocessor.joda.JodaRange.violated"));
        
    }
    
    /**
     * Tests message variables..
     */
    @Test
    public void testMessageVariable() {
        
        JodaRange<LocalDate> cp = (JodaRange<LocalDate>) processor;
        Map<String, ?> vars = cp.getMessageVariable();
        assertThat(vars.get("min"), is(min));
        assertThat(vars.get("max"), is(max));
        
    }
    
    /**
     * Tests format value
     */
    @Test
    public void testFormatValue() {
        
        JodaRange<LocalDate> cp = (JodaRange<LocalDate>) processor;
        assertThat(cp.formatValue(null), is(""));
        assertThat(cp.formatValue(min), is("2000-01-01"));
    }
}

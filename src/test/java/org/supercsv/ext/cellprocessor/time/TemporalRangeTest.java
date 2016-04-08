package org.supercsv.ext.cellprocessor.time;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;
import static org.supercsv.ext.TestUtils.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.exception.SuperCsvCellProcessorException;
import org.supercsv.exception.SuperCsvConstraintViolationException;

/**
 * Tests the {@link TemporalRange} constraint.
 *
 * @since 1.2
 * @author T.TSUCHIE
 *
 */
public class TemporalRangeTest {
    
    private CellProcessor processor;
    private CellProcessor processorChain;
    private LocalDate min = LocalDate.of(2000, 1, 1);
    private LocalDate max = LocalDate.of(2000, 1, 5);
    
    /**
     * Sets up the processor for the test using Combinations
     */
    @Before
    public void setUp() {
        processor = new TemporalRange<LocalDate>(min, max);
        processorChain = new TemporalRange<LocalDate>(min, max, new NextCellProcessor());
    }
    
    /**
     * Test constructor argument's with wrong values.
     * min is null.
     */
    @Test(expected=NullPointerException.class)
    public void testCheckConditionsWithWrong_minNull() {
        
        new TemporalRange<LocalDate>(null, max);
        
        fail();
        
    }
    
    /**
     * Test constructor argument's with wrong values.
     * next is null.
     */
    @Test(expected=NullPointerException.class)
    public void testCheckConditionsWithWrong_nextNull() {
        
        new TemporalRange<LocalDate>(min, max, null);
        
        fail();
        
    }
    
    /**
     * Test constructor argument's with wrong values.
     * min > max
     */
    @Test(expected=IllegalArgumentException.class)
    public void testCheckConditionsWithWrong_maxLessThanMin() {
        
        new TemporalRange<LocalDate>(max, min);
        
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
        
        TemporalRange<LocalDate> cp = (TemporalRange<LocalDate>) processor;
        assertThat(cp.getMin(), is(min));
        
    }
    
    /**
     * Tests max value.
     */
    @Test
    public void tesMTax() {
        
        TemporalRange<LocalDate> cp = (TemporalRange<LocalDate>) processor;
        assertThat(cp.getMax(), is(max));
        
    }
    
    /**
     * Tests message code.
     */
    @Test
    public void testMessageCode() {
        
        TemporalRange<LocalDate> cp = (TemporalRange<LocalDate>) processor;
        assertThat(cp.getMessageCode(), is("org.supercsv.ext.cellprocessor.time.TemporalRange.violated"));
        
    }
    
    /**
     * Tests message variables..
     */
    @Test
    public void testMessageVariable() {
        
        TemporalRange<LocalDate> cp = (TemporalRange<LocalDate>) processor;
        Map<String, ?> vars = cp.getMessageVariable();
        assertThat(vars.get("min"), is(min));
        assertThat(vars.get("max"), is(max));
        
    }
    
    /**
     * Tests format value
     */
    @Test
    public void testFormatValue() {
        
        TemporalRange<LocalDate> cp = (TemporalRange<LocalDate>) processor;
        assertThat(cp.formatValue(null), is(""));
        assertThat(cp.formatValue(min), is("2000-01-01"));
        assertThat(cp.formatValue(123), is("123"));
    }
    
    /**
     * Tests setFormatter
     */
    @Test
    public void testFormatter() {
        
        TemporalRange<LocalDate> cp = (TemporalRange<LocalDate>) processor;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        cp.setFormatter(formatter);
        assertThat(cp.formatValue(min), is("2000/01/01"));
    }
}

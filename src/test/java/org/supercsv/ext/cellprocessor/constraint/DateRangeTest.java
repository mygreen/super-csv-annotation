package org.supercsv.ext.cellprocessor.constraint;

import static org.junit.Assert.*;
import static org.supercsv.ext.tool.TestUtils.*;
import static org.hamcrest.Matchers.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.exception.SuperCsvCellProcessorException;
import org.supercsv.exception.SuperCsvConstraintViolationException;

/**
 * Tests the {@link DateRange} constraint.
 *
 * @since 1.2
 * @author T.TSUCHIE
 *
 */
public class DateRangeTest {
    
    private CellProcessor processor;
    private CellProcessor processorChain;
    private Date min = toDate(2000, 1, 1);
    private Date max = toDate(2000, 1, 5);
    
    /**
     * Sets up the processor for the test using Combinations
     */
    @Before
    public void setUp() {
        processor = new DateRange<Date>(min, max);
        processorChain = new DateRange<Date>(min, max, new NextCellProcessor());
    }
    
    /**
     * Test constructor argument's with wrong values.
     * min is null.
     */
    @Test(expected=NullPointerException.class)
    public void testCheckConditionsWithWrong_minNull() {
        
        new DateRange<Date>(null, max);
        
        fail();
        
    }
    
    /**
     * Test constructor argument's with wrong values.
     * next is null.
     */
    @Test(expected=NullPointerException.class)
    public void testCheckConditionsWithWrong_nextNull() {
        
        new DateRange<Date>(min, max, null);
        
        fail();
        
    }
    
    /**
     * Test constructor argument's with wrong values.
     * {@literal min > max}
     */
    @Test(expected=IllegalArgumentException.class)
    public void testCheckConditionsWithWrong_maxLessThanMin() {
        
        new DateRange<Date>(max, min);
        
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
        processor.execute(plusDays(min, 1), ANONYMOUS_CSVCONTEXT);
        processor.execute(max, ANONYMOUS_CSVCONTEXT);
        processor.execute(minusDays(max, 1), ANONYMOUS_CSVCONTEXT);
        
        processorChain.execute(min, ANONYMOUS_CSVCONTEXT);
        processorChain.execute(plusDays(min, 1), ANONYMOUS_CSVCONTEXT);
        processorChain.execute(max, ANONYMOUS_CSVCONTEXT);
        processorChain.execute(minusDays(max, 1), ANONYMOUS_CSVCONTEXT);
        
    }
    
    /**
     * Test execusion with invalidValue.
     */
    @Test(expected=SuperCsvConstraintViolationException.class)
    public void testExecuteWithAvovbeMax() {
        
        processor.execute(plusDays(max, 1), ANONYMOUS_CSVCONTEXT);
        
        fail();
        
    }
    
    /**
     * Test execusion with invalidValue.
     */
    @Test(expected=SuperCsvConstraintViolationException.class)
    public void testExecuteWithBelowMin() {
        
        processor.execute(minusDays(min, 1), ANONYMOUS_CSVCONTEXT);
        
        fail();
        
    }
    
    /**
     * Tests min value.
     */
    @Test
    public void testMin() {
        
        DateRange<Date> cp = (DateRange<Date>) processor;
        assertThat(cp.getMin(), is(min));
        
    }
    
    /**
     * Tests max value.
     */
    @Test
    public void testMax() {
        
        DateRange<Date> cp = (DateRange<Date>) processor;
        assertThat(cp.getMax(), is(max));
        
    }
    
    /**
     * Tests message code.
     */
    @Test
    public void testMessageCode() {
        
        DateRange<Date> cp = (DateRange<Date>) processor;
        assertThat(cp.getMessageCode(), is("org.supercsv.ext.cellprocessor.constraint.DateRange.violated"));
        
    }
    
    /**
     * Tests message variables..
     */
    @Test
    public void testMessageVariable() {
        
        DateRange<Date> cp = (DateRange<Date>) processor;
        Map<String, ?> vars = cp.getMessageVariable();
        assertThat(vars, hasEntry("min", min));
        assertThat(vars, hasEntry("max", max));
        
    }
    
    /**
     * Tests format value
     */
    @Test
    public void testFormatValue() {
        
        DateRange<Date> cp = (DateRange<Date>) processor;
        assertThat(cp.formatValue(null), is(""));
        assertThat(cp.formatValue(min), is("2000-01-01 00:00:00"));
        assertThat(cp.formatValue(123), is("123"));
    }
    
    /**
     * Tests set formatter
     */
    @Test
    public void testFormatter() {
        
        DateRange<Date> cp = (DateRange<Date>) processor;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        cp.setFormatter(formatter);
        
        assertThat(cp.formatValue(min), is("2000/01/01"));
    }
}

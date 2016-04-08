package org.supercsv.ext.cellprocessor.joda;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;
import static org.supercsv.ext.TestUtils.*;

import java.util.Map;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Before;
import org.junit.Test;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.exception.SuperCsvCellProcessorException;
import org.supercsv.exception.SuperCsvConstraintViolationException;

/**
 * Tests the {@link FutureJoda} constraint.
 *
 * @since 1.2
 * @author T.TSUCHIE
 *
 */
public class FutureJodaTest {
    
    private CellProcessor processor;
    private CellProcessor processorChain;
    private LocalDate min = new LocalDate(2000, 1, 1);
    
    /**
     * Sets up the processor for the test using Combinations
     */
    @Before
    public void setUp() {
        processor = new FutureJoda<LocalDate>(min);
        processorChain = new FutureJoda<LocalDate>(min, new NextCellProcessor());
    }
    
    /**
     * Test constructor argument's with wrong values.
     * min is null.
     */
    @Test(expected=NullPointerException.class)
    public void testCheckConditionsWithWrong_minNull() {
        
        new FutureJoda<LocalDate>(null);
        
        fail();
        
    }
    
    /**
     * Test constructor argument's with wrong values.
     * next is null.
     */
    @Test(expected=NullPointerException.class)
    public void testCheckConditionsWithWrong_nextNull() {
        
        new FutureJoda<LocalDate>(min, null);
        
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
        
        processorChain.execute(min, ANONYMOUS_CSVCONTEXT);
        processorChain.execute(min.plusDays(1), ANONYMOUS_CSVCONTEXT);
        
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
        
        FutureJoda<LocalDate> cp = (FutureJoda<LocalDate>) processor;
        assertThat(cp.getMin(), is(min));
        
    }
    
    /**
     * Tests message code.
     */
    @Test
    public void testMessageCode() {
        
        FutureJoda<LocalDate> cp = (FutureJoda<LocalDate>) processor;
        assertThat(cp.getMessageCode(), is("org.supercsv.ext.cellprocessor.joda.FutureJoda.violated"));
        
    }
    
    /**
     * Tests message variables
     */
    @Test
    public void testMessageVariable() {
        
        FutureJoda<LocalDate> cp = (FutureJoda<LocalDate>) processor;
        Map<String, ?> vars = cp.getMessageVariable();
        assertThat(vars.get("min"), is(min));
        
    }
    
    /**
     * Tests format value.
     */
    @Test
    public void testFormatValue() {
        
        FutureJoda<LocalDate> cp = (FutureJoda<LocalDate>) processor;
        assertThat(cp.formatValue(null), is(""));
        assertThat(cp.formatValue(min), is("2000-01-01"));
        assertThat(cp.formatValue(123), is("123"));
    }
    
    /**
     * Tests setFormatter
     */
    @Test
    public void testFormatter() {
        
        FutureJoda<LocalDate> cp = (FutureJoda<LocalDate>) processor;
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy/MM/dd");
        cp.setFormatter(formatter);
        assertThat(cp.formatValue(min), is("2000/01/01"));
    }
    
}

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
 * Tests the {@link PastTemporal} constraint.
 *
 * @since 1.2
 * @author T.TSUCHIE
 *
 */
public class PastTemporalTest {
    
    private CellProcessor processor;
    private CellProcessor processorChain;
    private LocalDate max = LocalDate.of(2000, 1, 1);
    
    /**
     * Sets up the processor for the test using Combinations
     */
    @Before
    public void setUp() {
        processor = new PastTemporal<LocalDate>(max);
        processorChain = new PastTemporal<LocalDate>(max, new NextCellProcessor());
    }
    
    /**
     * Test constructor argument's with wrong values.
     * max is null.
     */
    @Test(expected=NullPointerException.class)
    public void testCheckConditionsWithWrong_maxNull() {
        
        new PastTemporal<LocalDate>(null);
        
        fail();
        
    }
    
    /**
     * Test constructor argument's with wrong values.
     * next is null.
     */
    @Test(expected=NullPointerException.class)
    public void testCheckConditionsWithWrong_nextNull() {
        
        new PastTemporal<LocalDate>(max, null);
        
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
        processor.execute(max.minusDays(1), ANONYMOUS_CSVCONTEXT);
        
        processorChain.execute(max, ANONYMOUS_CSVCONTEXT);
        processorChain.execute(max.minusDays(1), ANONYMOUS_CSVCONTEXT);
        
    }
    
    /**
     * Test execusion with invalidValue.
     */
    @Test(expected=SuperCsvConstraintViolationException.class)
    public void testExecuteWithAvovbMax() {
        
        processor.execute(max.plusDays(1), ANONYMOUS_CSVCONTEXT);
        
        fail();
        
    }
    
    /**
     * Tests max value.
     */
    @Test
    public void testMax() {
        
        PastTemporal<LocalDate> cp = (PastTemporal<LocalDate>) processor;
        assertThat(cp.getMax(), is(max));
        
    }
    
    /**
     * Tests message code.
     */
    @Test
    public void testMessageCode() {
        
        PastTemporal<LocalDate> cp = (PastTemporal<LocalDate>) processor;
        assertThat(cp.getMessageCode(), is("org.supercsv.ext.cellprocessor.time.PastTemporal.violated"));
        
    }
    
    /**
     * Tests message variables
     */
    @Test
    public void testMessageVariable() {
        
        PastTemporal<LocalDate> cp = (PastTemporal<LocalDate>) processor;
        Map<String, ?> vars = cp.getMessageVariable();
        assertThat(vars.get("max"), is(max));
        
    }
    
    /**
     * Tests format values
     */
    @Test
    public void testFormatValue() {
        
        PastTemporal<LocalDate> cp = (PastTemporal<LocalDate>) processor;
        assertThat(cp.formatValue(null), is(""));
        assertThat(cp.formatValue(max), is("2000-01-01"));
        assertThat(cp.formatValue(123), is("123"));
    }
    
    /**
     * Tests setFormatter
     */
    @Test
    public void testFormatter() {
        
        PastTemporal<LocalDate> cp = (PastTemporal<LocalDate>) processor;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        cp.setFormatter(formatter);
        assertThat(cp.formatValue(max), is("2000/01/01"));
    }
    
}

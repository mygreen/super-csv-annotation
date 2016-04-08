package org.supercsv.ext.cellprocessor.constraint;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;
import static org.supercsv.ext.TestUtils.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.exception.SuperCsvCellProcessorException;
import org.supercsv.exception.SuperCsvConstraintViolationException;

/**
 * Tests the {@link PatDate} constraint.
 *
 * @since 1.2
 * @author T.TSUCHIE
 */
public class PastDateTest {
    
    private CellProcessor processor;
    private CellProcessor processorChain;
    private Date max = toDate(2000, 1, 1);
    
    /**
     * Sets up the processor for the test using Combinations
     */
    @Before
    public void setUp() {
        processor = new PastDate<Date>(max);
        processorChain = new PastDate<Date>(max, new NextCellProcessor());
    }
    
    /**
     * Test constructor argument's with wrong values.
     * max is null.
     */
    @Test(expected=NullPointerException.class)
    public void testCheckConditionsWithWrong_maxNull() {
        
        new PastDate<Date>(null);
        
        fail();
        
    }
    
    /**
     * Test constructor argument's with wrong values.
     * next is null.
     */
    @Test(expected=NullPointerException.class)
    public void testCheckConditionsWithWrong_nextNull() {
        
        new PastDate<Date>(max, null);
        
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
        processor.execute(minusDays(max, 1), ANONYMOUS_CSVCONTEXT);
        
        processorChain.execute(max, ANONYMOUS_CSVCONTEXT);
        processorChain.execute(minusDays(max, 1), ANONYMOUS_CSVCONTEXT);
        
    }
    
    /**
     * Test execusion with invalidValue.
     */
    @Test(expected=SuperCsvConstraintViolationException.class)
    public void testExecuteWithAvovbMax() {
        
        processor.execute(plusDays(max, 1), ANONYMOUS_CSVCONTEXT);
        
        fail();
        
    }
    
    /**
     * Tests max value.
     */
    @Test
    public void testTax() {
        
        PastDate<Date> cp = (PastDate<Date>) processor;
        assertThat(cp.getMax(), is(max));
        
    }
    
    /**
     * Tests message code.
     */
    @Test
    public void testMessageCode() {
        
        PastDate<Date> cp = (PastDate<Date>) processor;
        assertThat(cp.getMessageCode(), is("org.supercsv.ext.cellprocessor.constraint.PastDate.violated"));
        
    }
    
    /**
     * Tests message variables
     */
    @Test
    public void testMessageVariable() {
        
        PastDate<Date> cp = (PastDate<Date>) processor;
        Map<String, ?> vars = cp.getMessageVariable();
        assertThat(vars.get("max"), is(max));
        
    }
    
    /**
     * Tests format values
     */
    @Test
    public void testFormatValue() {
        
        PastDate<Date> cp = (PastDate<Date>) processor;
        assertThat(cp.formatValue(null), is(""));
        assertThat(cp.formatValue(max), is("2000-01-01 00:00:00"));
        assertThat(cp.formatValue(123), is("123"));
    }
    
    /**
     * Tests set formatter
     */
    @Test
    public void testFormatter() {
        
        PastDate<Date> cp = (PastDate<Date>) processor;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        cp.setFormatter(formatter);
        
        assertThat(cp.formatValue(max), is("2000/01/01"));
    }
}

package org.supercsv.ext.cellprocessor.constraint;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;
import static org.supercsv.ext.TestUtils.*;

import java.util.Date;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.exception.SuperCsvCellProcessorException;
import org.supercsv.exception.SuperCsvConstraintViolationException;

/**
 * Tests the {@link FutureDate} constraint.
 *
 * @since 1.2
 * @author T.TSUCHIE
 *
 */
public class FutureDateTest {
    
    private CellProcessor processor;
    private CellProcessor processorChain;
    private Date min = toDate(2000, 1, 1);
    
    /**
     * Sets up the processor for the test using Combinations
     */
    @Before
    public void setUp() {
        processor = new FutureDate<Date>(min);
        processorChain = new FutureDate<Date>(min, new NextCellProcessor());
    }
    
    /**
     * Test constructor argument's with wrong values.
     * min is null.
     */
    @Test(expected=NullPointerException.class)
    public void testCheckConditionsWithWrong_minNull() {
        
        new FutureDate<Date>(null);
        
        fail();
        
    }
    
    /**
     * Test constructor argument's with wrong values.
     * next is null.
     */
    @Test(expected=NullPointerException.class)
    public void testCheckConditionsWithWrong_nextNull() {
        
        new FutureDate<Date>(min, null);
        
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
        
        processorChain.execute(min, ANONYMOUS_CSVCONTEXT);
        processorChain.execute(plusDays(min, 1), ANONYMOUS_CSVCONTEXT);
        
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
        
        FutureDate<Date> cp = (FutureDate<Date>) processor;
        assertThat(cp.getMin(), is(min));
        
    }
    
    /**
     * Tests message code.
     */
    @Test
    public void testMessageCode() {
        
        FutureDate<Date> cp = (FutureDate<Date>) processor;
        assertThat(cp.getMessageCode(), is("org.supercsv.ext.cellprocessor.constraint.FutureDate.violated"));
        
    }
    
    /**
     * Tests message variables
     */
    @Test
    public void testMessageVariable() {
        
        FutureDate<Date> cp = (FutureDate<Date>) processor;
        Map<String, ?> vars = cp.getMessageVariable();
        assertThat(vars.get("min"), is(min));
        
    }
    
    /**
     * Tests format value.
     */
    @Test
    public void testFormatValue() {
        
        FutureDate<Date> cp = (FutureDate<Date>) processor;
        assertThat(cp.formatValue(null), is(""));
        assertThat(cp.formatValue(min), is("2000-01-01 00:00:00"));
    }
}

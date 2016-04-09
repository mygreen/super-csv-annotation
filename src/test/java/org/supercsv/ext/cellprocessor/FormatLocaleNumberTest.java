package org.supercsv.ext.cellprocessor;

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


public class FormatLocaleNumberTest {
    
    private CellProcessor processor;
    private CellProcessor processorChain;
    
    private NumberFormat formatter = new DecimalFormat("###,###");
    private Number number = 123456;
    
    /**
     * Sets up the processor for the test using Combinations
     */
    @Before
    public void setUp() {
        
        processor = new FormatLocaleNumber(formatter);
        processorChain = new FormatLocaleNumber(formatter, new NextCellProcessor());
    }
    
    /**
     * Tests construction of the processor with a null date format (should throw an Exception).
     */
    @Test(expected = NullPointerException.class)
    public void testCheckconditionWithNull() {
        
        new FormatLocaleNumber((NumberFormat) null);
        fail();
    }
    
    /**
     * Tests unchained/chained execution with a valid date.
     */
    @Test
    public void testExecuteWithValid() {
        
        assertThat(processor.execute(number, ANONYMOUS_CSVCONTEXT), is("123,456"));
        assertThat(processorChain.execute(number, ANONYMOUS_CSVCONTEXT), is("123,456"));
        
    }
    
    /**
     * Tests execution with a null input (should throw an Exception).
     */
    @Test(expected = SuperCsvCellProcessorException.class)
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
     * Tests message code.
     */
    @Test
    public void testMessageCode() {
        
        FormatLocaleNumber cp = (FormatLocaleNumber) processor;
        assertThat(cp.getMessageCode(), is("org.supercsv.ext.cellprocessor.FormatLocaleNumber.violated"));
        
    }
    
    /**
     * Tests message variables..
     */
    @Test
    public void testMessageVariable() {
        
        FormatLocaleNumber cp = (FormatLocaleNumber) processor;
        Map<String, ?> vars = cp.getMessageVariable();
        assertThat(vars.size(), is(0));
        
    }
    
    /**
     * Tests format value
     */
    @Test
    public void testFormatValue() {
        
        FormatLocaleNumber cp = (FormatLocaleNumber) processor;
        assertThat(cp.formatValue(null), is(""));
        assertThat(cp.formatValue(number), is("123,456"));
        assertThat(cp.formatValue("abc"), is("abc"));
    }
}

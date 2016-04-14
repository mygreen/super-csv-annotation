package org.supercsv.ext.cellprocessor;

import static org.junit.Assert.*;
import static org.supercsv.ext.tool.TestUtils.*;
import static org.hamcrest.Matchers.*;

import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.exception.SuperCsvCellProcessorException;

/**
 * Tests the {@link ParseLocaleTime} processor.
 *
 * @since 1.2
 * @author T.TSUCHIE
 *
 */
public class ParseLocaleTimeTest {
    
    private CellProcessor processor;
    private CellProcessor processorChain;
    
    private DateFormat formatter = new SimpleDateFormat("HH:mm:ss");
    
    /**
     * Sets up the processor for the test using Combinations
     */
    @Before
    public void setUp() {
        
        processor = new ParseLocaleTime(formatter);
        processorChain = new ParseLocaleTime(formatter, new NextCellProcessor());
    }
    
    /**
     * Tests construction of the processor with a null date format (should throw an Exception).
     */
    @Test(expected = NullPointerException.class)
    public void testCheckconditionWithNull() {
        
        new ParseLocaleTime((DateFormat) null);
        fail();
    }
    
    /**
     * Tests unchained/chained execution with a valid date.
     */
    @Test
    public void testExecuteWithValid() {
        String input = "01:20:13";
        Time output = toTime(1, 20, 13);
        
        assertThat(processor.execute(input, ANONYMOUS_CSVCONTEXT), is(output));
        assertThat(processorChain.execute(input, ANONYMOUS_CSVCONTEXT), is(output));
        
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
    public void testExecuteWithNonString() {
        
        processor.execute(123, ANONYMOUS_CSVCONTEXT);
        fail();
    }
    
    /**
     * Test execusion with a not same instance input.
     */
    @Test(expected=SuperCsvCellProcessorException.class)
    public void testExecuteWithWrongPattern() {
        
        processor.execute("01 20 13", ANONYMOUS_CSVCONTEXT);
        fail();
    }
    
    /**
     * Tests message code.
     */
    @Test
    public void testMessageCode() {
        
        ParseLocaleTime cp = (ParseLocaleTime) processor;
        assertThat(cp.getMessageCode(), is("org.supercsv.ext.cellprocessor.ParseLocaleTime.violated"));
        
    }
    
    /**
     * Tests message variables.
     */
    @Test
    public void testMessageVariable() {
        
        ParseLocaleTime cp = (ParseLocaleTime) processor;
        Map<String, ?> vars = cp.getMessageVariable();
        assertThat(vars, hasEntry("pattern", "HH:mm:ss"));
        
    }
    
    /**
     * Tests format value
     */
    @Test
    public void testFormatValue() {
        
        ParseLocaleTime cp = (ParseLocaleTime) processor;
        assertThat(cp.formatValue(null), is(""));
        assertThat(cp.formatValue(toTime(1, 20, 13)), is("01:20:13"));
        assertThat(cp.formatValue(123), is("123"));
    }
    
    
}

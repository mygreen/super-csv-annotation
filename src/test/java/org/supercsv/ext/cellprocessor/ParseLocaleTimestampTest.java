package org.supercsv.ext.cellprocessor;

import static org.junit.Assert.*;
import static org.supercsv.ext.tool.TestUtils.*;
import static org.hamcrest.Matchers.*;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.exception.SuperCsvCellProcessorException;

/**
 * Tests the {@link ParseLocaleTimestamp} processor.
 *
 * @since 1.2
 * @author T.TSUCHIE
 *
 */
public class ParseLocaleTimestampTest {
    
    private CellProcessor processor;
    private CellProcessor processorChain;
    
    private DateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
    
    /**
     * Sets up the processor for the test using Combinations
     */
    @Before
    public void setUp() {
        
        processor = new ParseLocaleTimestamp(formatter);
        processorChain = new ParseLocaleTimestamp(formatter, new NextCellProcessor());
    }
    
    /**
     * Tests construction of the processor with a null date format (should throw an Exception).
     */
    @Test(expected = NullPointerException.class)
    public void testCheckconditionWithNull() {
        
        new ParseLocaleTimestamp((DateFormat) null);
        fail();
    }
    
    /**
     * Tests unchained/chained execution with a valid date.
     */
    @Test
    public void testExecuteWithValid() {
        String input = "2011/12/25 01:20:13.123";
        Timestamp output = toTimestamp(2011, 12, 25, 1, 20, 13, 123);
        
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
        
        processor.execute("2011-12-25 01:20:13.123", ANONYMOUS_CSVCONTEXT);
        fail();
    }
    
    /**
     * Tests message code.
     */
    @Test
    public void testMessageCode() {
        
        ParseLocaleTimestamp cp = (ParseLocaleTimestamp) processor;
        assertThat(cp.getMessageCode(), is("org.supercsv.ext.cellprocessor.ParseLocaleTimestamp.violated"));
        
    }
    
    /**
     * Tests message variables.
     */
    @Test
    public void testMessageVariable() {
        
        ParseLocaleTimestamp cp = (ParseLocaleTimestamp) processor;
        Map<String, ?> vars = cp.getMessageVariable();
        assertThat(vars, hasEntry("pattern", "yyyy/MM/dd HH:mm:ss.SSS"));
        
    }
    
    /**
     * Tests format value
     */
    @Test
    public void testFormatValue() {
        
        ParseLocaleTimestamp cp = (ParseLocaleTimestamp) processor;
        assertThat(cp.formatValue(null), is(""));
        assertThat(cp.formatValue(toTimestamp(2011, 12, 25, 1, 20, 13, 123)), is("2011/12/25 01:20:13.123"));
        assertThat(cp.formatValue(123), is("123"));
    }
    
    
}

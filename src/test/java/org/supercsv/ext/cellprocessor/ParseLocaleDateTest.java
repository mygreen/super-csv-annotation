package org.supercsv.ext.cellprocessor;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;
import static org.supercsv.ext.TestUtils.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.exception.SuperCsvCellProcessorException;

/**
 * Tests the {@link ParseLocaleDate} processor.
 *
 * @since 1.2
 * @author T.TSUCHIE
 *
 */
public class ParseLocaleDateTest {
    
    private CellProcessor processor;
    private CellProcessor processorChain;
    
    private DateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
    
    /**
     * Sets up the processor for the test using Combinations
     */
    @Before
    public void setUp() {
        
        processor = new ParseLocaleDate(formatter);
        processorChain = new ParseLocaleDate(formatter, new NextCellProcessor());
    }
    
    /**
     * Tests construction of the processor with a null date format (should throw an Exception).
     */
    @Test(expected = NullPointerException.class)
    public void testCheckconditionWithNull() {
        
        new ParseLocaleDate((DateFormat) null);
        fail();
    }
    
    /**
     * Tests unchained/chained execution with a valid date.
     */
    @Test
    public void testExecuteWithValid() {
        String input = "2011/12/25";
        Date output = toDate(2011, 12, 25);
        
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
        
        processor.execute("2011-12-25", ANONYMOUS_CSVCONTEXT);
        fail();
    }
    
    /**
     * Tests message code.
     */
    @Test
    public void testMessageCode() {
        
        ParseLocaleDate cp = (ParseLocaleDate) processor;
        assertThat(cp.getMessageCode(), is("org.supercsv.ext.cellprocessor.ParseLocaleDate.violated"));
        
    }
    
    /**
     * Tests message variables..
     */
    @Test
    public void testMessageVariable() {
        
        ParseLocaleDate cp = (ParseLocaleDate) processor;
        Map<String, ?> vars = cp.getMessageVariable();
        assertThat(vars, hasEntry("pattern", "yyyy/MM/dd"));
        
    }
    
    /**
     * Tests format value
     */
    @Test
    public void testFormatValue() {
        
        ParseLocaleDate cp = (ParseLocaleDate) processor;
        assertThat(cp.formatValue(null), is(""));
        assertThat(cp.formatValue(toDate(2011, 12, 25)), is("2011/12/25"));
        assertThat(cp.formatValue(123), is("123"));
    }
    
    
}

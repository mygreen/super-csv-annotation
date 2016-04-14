package org.supercsv.ext.cellprocessor;

import static org.junit.Assert.*;
import static org.supercsv.ext.tool.TestUtils.*;
import static org.hamcrest.Matchers.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.exception.SuperCsvCellProcessorException;

/**
 * Tests the {@link FormatLocaleDate} processor.
 *
 * @since 1.2
 * @author T.TSUCHIE
 *
 */
public class FormatLocaleDateTest {
    
    private CellProcessor processor;
    private CellProcessor processorChain;
    
    private DateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
    private Date date = toDate(2011, 12, 25);
    
    /**
     * Sets up the processor for the test using Combinations
     */
    @Before
    public void setUp() {
        
        processor = new FormatLocaleDate(formatter);
        processorChain = new FormatLocaleDate(formatter, new NextCellProcessor());
    }
    
    /**
     * Tests construction of the processor with a null date format (should throw an Exception).
     */
    @Test(expected = NullPointerException.class)
    public void testCheckconditionWithNull() {
        
        new FormatLocaleDate((DateFormat) null);
        fail();
    }
    
    /**
     * Tests unchained/chained execution with a valid date.
     */
    @Test
    public void testExecuteWithValid() {
        
        assertThat(processor.execute(date, ANONYMOUS_CSVCONTEXT), is("2011/12/25"));
        assertThat(processorChain.execute(date, ANONYMOUS_CSVCONTEXT), is("2011/12/25"));
        
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
        
        processor.execute(123, ANONYMOUS_CSVCONTEXT);
        fail();
    }
    
    /**
     * Tests message code.
     */
    @Test
    public void testMessageCode() {
        
        FormatLocaleDate cp = (FormatLocaleDate) processor;
        assertThat(cp.getMessageCode(), is("org.supercsv.ext.cellprocessor.FormatLocaleDate.violated"));
        
    }
    
    /**
     * Tests message variables..
     */
    @Test
    public void testMessageVariable() {
        
        FormatLocaleDate cp = (FormatLocaleDate) processor;
        Map<String, ?> vars = cp.getMessageVariable();
        assertThat(vars.size(), is(0));
        
    }
    
    /**
     * Tests format value
     */
    @Test
    public void testFormatValue() {
        
        FormatLocaleDate cp = (FormatLocaleDate) processor;
        assertThat(cp.formatValue(null), is(""));
        assertThat(cp.formatValue(date), is("2011/12/25"));
        assertThat(cp.formatValue(123), is("123"));
    }
    
}

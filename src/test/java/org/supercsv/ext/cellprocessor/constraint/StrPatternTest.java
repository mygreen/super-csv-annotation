package org.supercsv.ext.cellprocessor.constraint;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;
import static org.supercsv.ext.tool.TestUtils.*;

import java.util.Map;
import java.util.regex.Pattern;

import org.junit.Before;
import org.junit.Test;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.exception.SuperCsvCellProcessorException;
import org.supercsv.exception.SuperCsvConstraintViolationException;

/**
 * {@link StrPattern}のテスタ。
 * 
 * @since 1.2
 * @author T.TSUCHIE
 *
 */
public class StrPatternTest {
    
    private CellProcessor processor;
    private CellProcessor processorChain;
    
    private Pattern pattern = Pattern.compile("[a-c]-\\$[0-9]+\\.[0-9]{2}", Pattern.CASE_INSENSITIVE);
    
    /**
     * Sets up the processor for the test using Combinations
     */
    @Before
    public void setUp() {
        processor = new StrPattern(pattern);
        processorChain = new StrPattern(pattern, new NextCellProcessor());
    }
    
    /**
     * Test constructor argument's with wrong values.
     * min is null.
     */
    @Test(expected=NullPointerException.class)
    public void testCheckConditions_patternNull() {
        
        new StrPattern(null);
        
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
     * Test execusion with valid value.
     */
    @Test
    public void testExecute_validInput() {
        
        String input = "A-$123.45";
        assertThat(processor.execute(input, ANONYMOUS_CSVCONTEXT), is(input));
        
        assertThat(processorChain.execute(input, ANONYMOUS_CSVCONTEXT), is(input));
        
        
    }
    
    /**
     * Test execusion with invalid value.
     */
    @Test
    public void testExecute_invalidInput() {
        
        String input = "123.45";
        
        try {
            assertThat(processor.execute(input, ANONYMOUS_CSVCONTEXT), is(input));
            fail();
            
        } catch(SuperCsvConstraintViolationException  e) {
            assertThat(e.getMessage(), containsString(pattern.pattern()));
        }
        
    }
    
    /**
     * Tests message code.
     */
    @Test
    public void testMessageCode() {
        
        StrPattern cp = (StrPattern) processor;
        assertThat(cp.getMessageCode(), is("org.supercsv.ext.cellprocessor.constraint.StrPattern.violated"));
        
    }
    
    /**
     * Tests message variables
     */
    @Test
    public void testMessageVariable() {
        
        StrPattern cp = (StrPattern) processor;
        Map<String, ?> vars = cp.getMessageVariable();
        assertThat(vars, hasEntry("regex", pattern.pattern()));
        
    }
}

package org.supercsv.ext.cellprocessor;

import static org.junit.Assert.*;
import static org.supercsv.ext.tool.TestUtils.*;
import static org.hamcrest.Matchers.*;

import org.junit.Before;
import org.junit.Test;
import org.supercsv.cellprocessor.ift.CellProcessor;

/**
 * Tests the {@link ConvertNullToNext} processor.
 *
 * @since 1.2
 * @author T.TSUCHIE
 *
 */
public class ConvertNullToNextTest {
    
    private CellProcessor processor;
    private CellProcessor processorChain;
    
    private Object value = "Hello!";
    
    /**
     * Sets up the processor for the test using Combinations
     */
    @Before
    public void setUp() {
        
        processor = new ConvertNullToNext(value);
        processorChain = new ConvertNullToNext(value, new NextCellProcessor());
    }
    
    /**
     * Tests unchained/chained execution with a valid date.
     */
    @Test
    public void testExecuteWithValid() {
        
        assertThat(processor.execute(123, ANONYMOUS_CSVCONTEXT), is(123));
        assertThat(processorChain.execute(null, ANONYMOUS_CSVCONTEXT), is("Hello!"));
        
    }
    
}

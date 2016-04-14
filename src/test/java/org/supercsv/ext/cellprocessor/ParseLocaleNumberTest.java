package org.supercsv.ext.cellprocessor;

import static org.junit.Assert.*;
import static org.supercsv.ext.tool.TestUtils.*;
import static org.hamcrest.Matchers.*;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.exception.SuperCsvCellProcessorException;

/**
 * Tests the {@link ParseLocaleNumber} processor.
 *
 * @since 1.2
 * @author T.TSUCHIE
 *
 */
public class ParseLocaleNumberTest {
    
    private CellProcessor processor;
    private CellProcessor processorChain;
    
    private CellProcessor lenientProcessor;
    private CellProcessor lenientProcessorChain;
    
    private NumberFormat formatter = new DecimalFormat("#,###");
    
    /**
     * Sets up the processor for the test using Combinations
     */
    @Before
    public void setUp() {
        
        processor = new ParseLocaleNumber<Integer>(Integer.class, formatter);
        processorChain = new ParseLocaleNumber<Integer>(Integer.class, formatter, new NextCellProcessor());
        
        lenientProcessor = new ParseLocaleNumber<Integer>(Integer.class, formatter, true);
        lenientProcessorChain = new ParseLocaleNumber<Integer>(Integer.class, formatter, true, new NextCellProcessor());
        
    }
    
    /**
     * Tests construction of the processor with a null type (should throw an Exception).
     */
    @Test(expected = NullPointerException.class)
    public void testCheckconditionWithAtTypeNull() {
        
        new ParseLocaleNumber<Integer>(null, formatter);
        fail();
    }
    
    /**
     * Tests construction of the processor with a null format (should throw an Exception).
     */
    @Test(expected = NullPointerException.class)
    public void testCheckconditionWithAtFomatNull() {
        
        new ParseLocaleNumber<Integer>(Integer.class, (NumberFormat) null);
        fail();
    }
    
    /**
     * Tests unchained/chained execution with a valid number.
     */
    @Test
    public void testExecuteWithValid() {
        String input = "123,456";
        int output = 123456;
        
        assertThat(processor.execute(input, ANONYMOUS_CSVCONTEXT), is(output));
        assertThat(processorChain.execute(input, ANONYMOUS_CSVCONTEXT), is(output));
       
    }
    
    /**
     * Tests unchained/chained execution with a valid number.
     */
    @Test
    public void testExecuteWithLenient() {
        String input = "123,456a";
        int output = 123456;
        
        assertThat(lenientProcessor.execute(input, ANONYMOUS_CSVCONTEXT), is(output));
        assertThat(lenientProcessorChain.execute(input, ANONYMOUS_CSVCONTEXT), is(output));
       
    }
    
   /** Tests execution with a null input (should throw an Exception).
    */
   @Test(expected = SuperCsvCellProcessorException.class)
   public void testExecuteAtNull() {
       
       processor.execute(null, ANONYMOUS_CSVCONTEXT);
       fail();
   }
   
   /**
    * Test execusion with a not string input
    */
   @Test(expected=SuperCsvCellProcessorException.class)
   public void testExecuteAtNotString() {
       
       processor.execute(123, ANONYMOUS_CSVCONTEXT);
       fail();
   }
   
   /**
    * Test execusion with a invalid input
    */
   @Test(expected=SuperCsvCellProcessorException.class)
   public void testExecuteAtNotInvalid() {
       
       processor.execute("123,456a", ANONYMOUS_CSVCONTEXT);
       fail();
   }
   
   /**
    * Test execusion with a invalid input
    */
   @Test(expected=SuperCsvCellProcessorException.class)
   public void testExecuteWihtLenientAtNotInvalid() {
       
       lenientProcessor.execute("a123,456a", ANONYMOUS_CSVCONTEXT);
       fail();
   }
   
   /**
    * Tests message code.
    */
   @Test
   public void testMessageCode() {
       
       ParseLocaleNumber<?> cp = (ParseLocaleNumber<?>) processor;
       assertThat(cp.getMessageCode(), is("org.supercsv.ext.cellprocessor.ParseLocaleNumber.violated"));
       
   }
   
   /**
    * Tests message variables.
    */
   @Test
   public void testMessageVariable() {
       
       ParseLocaleNumber<?> cp = (ParseLocaleNumber<?>) processor;
       Map<String, ?> vars = cp.getMessageVariable();
       assertThat(vars, hasEntry("type", "java.lang.Integer"));
       assertThat(vars, hasEntry("pattern", "#,###"));
       
   }
   
   /**
    * Tests format value
    */
   @Test
   public void testFormatValue() {
       
       ParseLocaleNumber<?> cp = (ParseLocaleNumber<?>) processor;
       assertThat(cp.formatValue(null), is(""));
       assertThat(cp.formatValue("abc"), is("abc"));
       assertThat(cp.formatValue(123456), is("123,456"));
   }
   
}

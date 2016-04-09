package org.supercsv.ext.cellprocessor;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;
import static org.supercsv.ext.TestUtils.*;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.exception.SuperCsvCellProcessorException;

/**
 * Tests the {@link FormatEnum} processor.
 *
 * @version 1.2
 * @author T.TSUCHIE
 *
 */
public class FormatEnumTest {
    
    private CellProcessor processor;
    private CellProcessor processorChain;
    
    private CellProcessor valueMethodProcessor;
    private CellProcessor valueMethodProcessorChain;
    
    public enum TestEnum {
        Red("赤"), Blue("青"), Yellow("黄");
        
        final String aliasName;
        
        private TestEnum(String aliasName) {
            this.aliasName = aliasName;
        }
        
        public String aliasName() {
            return aliasName;
        }
    }
    
    /**
     * Sets up the processor for the test using Combinations
     */
    @Before
    public void setUp() {
        
        processor = new FormatEnum(TestEnum.class);
        processorChain = new FormatEnum(TestEnum.class, new NextCellProcessor());
        
        valueMethodProcessor = new FormatEnum(TestEnum.class, "aliasName");
        valueMethodProcessorChain = new FormatEnum(TestEnum.class, "aliasName", new NextCellProcessor());
    }
    
    /**
     * Tests construction of the processor with a null date format (should throw an Exception).
     */
    @Test(expected = NullPointerException.class)
    public void testCheckconditionWithNull() {
        
        new FormatEnum(null);
        fail();
    }
    
    /**
     * Tests construction of the processor with a null date format (should throw an Exception).
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCheckconditionWithNotExistMethod() {
        
        new FormatEnum(TestEnum.class, "aaa");
        fail();
    }
    
    /**
     * Tests unchained/chained execution with a valid date.
     */
    @Test
    public void testExecuteWithNormal() {
        
        assertThat(processor.execute(TestEnum.Blue, ANONYMOUS_CSVCONTEXT), is("Blue"));
        assertThat(processorChain.execute(TestEnum.Blue, ANONYMOUS_CSVCONTEXT), is("Blue"));
        
    }
    
    /**
     * Tests unchained/chained execution with a valid date.
     */
    @Test
    public void testExecuteWithValueMethod() {
        
        assertThat(valueMethodProcessor.execute(TestEnum.Blue, ANONYMOUS_CSVCONTEXT), is("青"));
        assertThat(valueMethodProcessorChain.execute(TestEnum.Blue, ANONYMOUS_CSVCONTEXT), is("青"));
        
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
        
        FormatEnum cp = (FormatEnum) processor;
        assertThat(cp.getMessageCode(), is("org.supercsv.ext.cellprocessor.FormatEnum.violated"));
        
    }
    
    /**
     * Tests message variables.
     */
    @Test
    public void testMessageVariable() {
        
        FormatEnum cp = (FormatEnum) processor;
        Map<String, ?> vars = cp.getMessageVariable();
        
        assertThat(vars, hasEntry("type", TestEnum.class.getCanonicalName()));
        assertThat(vars, hasEntry("valueMethod", ""));
        assertThat(vars, hasEntry("enumsStr", "Red, Blue, Yellow"));
        assertThat(vars, hasKey("enumValues"));
        
    }
    
    /**
     * Tests message variables.
     */
    @Test
    public void testMessageVariableWithValueMethod() {
        
        FormatEnum cp = (FormatEnum) valueMethodProcessor;
        Map<String, ?> vars = cp.getMessageVariable();
        
        assertThat(vars, hasEntry("type", TestEnum.class.getCanonicalName()));
        assertThat(vars, hasEntry("valueMethod", "aliasName"));
        assertThat(vars, hasEntry("enumsStr", "赤, 青, 黄"));
        assertThat(vars, hasKey("enumValues"));
        
    }
    
    /**
     * Tests format value
     */
    @Test
    public void testFormatValue() {
        
        FormatEnum cp = (FormatEnum) processor;
        assertThat(cp.formatValue(null), is(""));
        assertThat(cp.formatValue(TestEnum.Red), is("Red"));
        assertThat(cp.formatValue("abc"), is("abc"));
        
    }
    
    /**
     * Tests format value
     */
    @Test
    public void testFormatValueWithValueMethod() {
        
        FormatEnum cp = (FormatEnum) valueMethodProcessor;
        assertThat(cp.formatValue(null), is(""));
        assertThat(cp.formatValue(TestEnum.Red), is("赤"));
        assertThat(cp.formatValue("abc"), is("abc"));
        
    }
    
}

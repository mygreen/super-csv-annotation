package org.supercsv.ext.cellprocessor;

import static org.junit.Assert.*;
import static org.supercsv.ext.tool.TestUtils.*;
import static org.hamcrest.Matchers.*;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.exception.SuperCsvCellProcessorException;

/**
 * Tests the {@link ParseEnum} processor.
 *
 * @version 1.2
 * @author T.TSUCHIE
 *
 */
public class ParseEnumTest {
    
    private CellProcessor processor;
    private CellProcessor processorChain;
    
    private CellProcessor valueMethodProcessor;
    private CellProcessor valueMethodProcessorChain;
    
    private CellProcessor ignoreCaseProcessor;
    private CellProcessor ignoreCaseProcessorChain;
    
    private CellProcessor ignoreCaseValueMethodProcessor;
    private CellProcessor ignoreCaseValueMethodProcessorChain;
    
    public enum TestEnum {
        Red("赤(RED)"), Blue("青(BLUE)"), Yellow("黄(Yellow)");
        
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
        
        processor = new ParseEnum(TestEnum.class);
        processorChain = new ParseEnum(TestEnum.class, new NextCellProcessor());
        
        valueMethodProcessor = new ParseEnum(TestEnum.class, "aliasName");
        valueMethodProcessorChain = new ParseEnum(TestEnum.class, "aliasName", new NextCellProcessor());
        
        ignoreCaseProcessor = new ParseEnum(TestEnum.class, true);
        ignoreCaseProcessorChain = new ParseEnum(TestEnum.class, true, new NextCellProcessor());
        
        ignoreCaseValueMethodProcessor = new ParseEnum(TestEnum.class, true, "aliasName");
        ignoreCaseValueMethodProcessorChain = new ParseEnum(TestEnum.class, true, "aliasName", new NextCellProcessor());
    }
    
    /**
     * Tests construction of the processor with a null date format (should throw an Exception).
     */
    @Test(expected = NullPointerException.class)
    public void testCheckconditionWithNull() {
        
        new ParseEnum(null);
        fail();
    }
    
    /**
     * Tests construction of the processor with a null type (should throw an Exception).
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCheckconditionWithNotExistMethod() {
        
        new ParseEnum(TestEnum.class, "aaa");
        fail();
    }
    
    /**
     * Tests unchained/chained execution with a valid date.
     */
    @Test
    public void testExecuteWithNormal() {
        
        assertThat(processor.execute("Blue", ANONYMOUS_CSVCONTEXT), is(TestEnum.Blue));
        assertThat(processorChain.execute("Blue", ANONYMOUS_CSVCONTEXT), is(TestEnum.Blue));
        
    }
    
    /**
     * Tests unchained/chained execution with a valid date.
     */
    @Test
    public void testExecuteWithValueMethod() {
        
        assertThat(valueMethodProcessor.execute("青(BLUE)", ANONYMOUS_CSVCONTEXT), is(TestEnum.Blue));
        assertThat(valueMethodProcessorChain.execute("青(BLUE)", ANONYMOUS_CSVCONTEXT), is(TestEnum.Blue));
        
    }
    
    /**
     * Tests unchained/chained execution with a valid date.
     */
    @Test
    public void testExecuteWithIgnoreCase() {
        
        assertThat(ignoreCaseProcessor.execute("ReD", ANONYMOUS_CSVCONTEXT), is(TestEnum.Red));
        assertThat(ignoreCaseProcessorChain.execute("ReD", ANONYMOUS_CSVCONTEXT), is(TestEnum.Red));
        
    }
    
    /**
     * Tests unchained/chained execution with a valid date.
     */
    @Test
    public void testExecuteWithIgnoreCaseValueMethod() {
        
        assertThat(ignoreCaseValueMethodProcessor.execute("黄(YEllOW)", ANONYMOUS_CSVCONTEXT), is(TestEnum.Yellow));
        assertThat(ignoreCaseValueMethodProcessorChain.execute("黄(YEllOW)", ANONYMOUS_CSVCONTEXT), is(TestEnum.Yellow));
        
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
     * Test execution with not parsed enum value.
     */
    @Test(expected=SuperCsvCellProcessorException.class)
    public void testExecuteWithNormalAtWorng() {
        processor.execute("blue", ANONYMOUS_CSVCONTEXT);
        fail();
    }
    
    /**
     * Test execution with enum object.
     */
    @Test(expected=SuperCsvCellProcessorException.class)
    public void testExecuteWithNormalAtNotString() {
        processor.execute(123, ANONYMOUS_CSVCONTEXT);
        fail();
    }
    
    /**
     * Tests message code.
     */
    @Test
    public void testMessageCode() {
        
        ParseEnum cp = (ParseEnum) processor;
        assertThat(cp.getMessageCode(), is("org.supercsv.ext.cellprocessor.ParseEnum.violated"));
        
    }
    
    /**
     * Tests message variables.
     */
    @Test
    public void testMessageVariable() {
        
        ParseEnum cp = (ParseEnum) processor;
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
        
        ParseEnum cp = (ParseEnum) valueMethodProcessor;
        Map<String, ?> vars = cp.getMessageVariable();
        
        assertThat(vars, hasEntry("type", TestEnum.class.getCanonicalName()));
        assertThat(vars, hasEntry("valueMethod", "aliasName"));
        assertThat(vars, hasEntry("enumsStr", "赤(RED), 青(BLUE), 黄(Yellow)"));
        assertThat(vars, hasKey("enumValues"));
        
    }
    
    /**
     * Tests format value
     */
    @Test
    public void testFormatValue() {
        
        ParseEnum cp = (ParseEnum) processor;
        assertThat(cp.formatValue(null), is(""));
        assertThat(cp.formatValue(TestEnum.Red), is("Red"));
        assertThat(cp.formatValue("abc"), is("abc"));
        
    }
    
    /**
     * Tests format value
     */
    @Test
    public void testFormatValueWithValueMethod() {
        
        ParseEnum cp = (ParseEnum) valueMethodProcessor;
        assertThat(cp.formatValue(null), is(""));
        assertThat(cp.formatValue(TestEnum.Red), is("赤(RED)"));
        assertThat(cp.formatValue("abc"), is("abc"));
        
    }
    
}

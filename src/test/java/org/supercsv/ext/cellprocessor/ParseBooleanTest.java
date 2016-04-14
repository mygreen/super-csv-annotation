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
 * Tests the {@link ParseBoolean} processor.
 *
 * @version 1.2
 * @author T.TSUCHIE
 *
 */
public class ParseBooleanTest {
    
    private CellProcessor processor;
    private CellProcessor processorChain;
    
    private CellProcessor ignoreCaseProcessor;
    private CellProcessor ignoreCaseProcessorChain;
    
    private CellProcessor customValueProcessor;
    private CellProcessor customValueProcessorChain;
    
    private CellProcessor customValueIgnoreCaseProcessor;
    private CellProcessor customValueIgnoreCaseProcessorChain;
    
    private String[] customTrues = {"○", "レ", "Ok"};
    private String[] customFalses = {"×", "ー", "Cancel", ""};
    
    /**
     * Sets up the processor for the test using Combinations
     */
    @Before
    public void setUp() {
        
        processor = new ParseBoolean();
        processorChain = new ParseBoolean(new NextCellProcessor());
        
        ignoreCaseProcessor = new ParseBoolean(true);
        ignoreCaseProcessorChain = new ParseBoolean(true, new NextCellProcessor());
        
        customValueProcessor = new ParseBoolean(customTrues, customFalses);
        customValueProcessorChain = new ParseBoolean(customTrues, customFalses, new NextCellProcessor());
        
        customValueIgnoreCaseProcessor = new ParseBoolean(customTrues, customFalses, true);
        customValueIgnoreCaseProcessorChain = new ParseBoolean(customTrues, customFalses, true, new NextCellProcessor());
    }
    
    /**
     * Tests construction of the processor with a null true values (should throw an Exception).
     */
    @Test(expected = NullPointerException.class)
    public void testCheckconditionWithTrueNull() {
        
        new ParseBoolean(null, customFalses);
        fail();
    }
    
    /**
     * Tests construction of the processor with a empty true values (should throw an Exception).
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCheckconditionWithTrueEmpty() {
        
        new ParseBoolean(new String[]{}, customFalses);
        fail();
    }
    
    /**
     * Tests construction of the processor with a null true values (should throw an Exception).
     */
    @Test(expected = NullPointerException.class)
    public void testCheckconditionWithFalseNull() {
        
        new ParseBoolean(customTrues, null);
        fail();
    }
    
    /**
     * Tests construction of the processor with a empty true values (should throw an Exception).
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCheckconditionWithFalseEmpty() {
        
        new ParseBoolean(customTrues, new String[]{});
        fail();
    }
    
    @Test
    public void testExecuteWithNormal() {
        
        assertThat(processor.execute("true", ANONYMOUS_CSVCONTEXT), is(true));
        assertThat(processorChain.execute("false", ANONYMOUS_CSVCONTEXT), is(false));
        
        assertThat(ignoreCaseProcessor.execute("yes", ANONYMOUS_CSVCONTEXT), is(true));
        assertThat(ignoreCaseProcessorChain.execute("no", ANONYMOUS_CSVCONTEXT), is(false));
    }
    
    @Test
    public void testExecuteWithIgnoreCase() {
        
        assertThat(ignoreCaseProcessor.execute("YES", ANONYMOUS_CSVCONTEXT), is(true));
        assertThat(ignoreCaseProcessorChain.execute("NO", ANONYMOUS_CSVCONTEXT), is(false));
    }
    
    @Test
    public void testExecuteWithCustomValue() {
        
        assertThat(customValueProcessor.execute("○", ANONYMOUS_CSVCONTEXT), is(true));
        assertThat(customValueProcessorChain.execute("×", ANONYMOUS_CSVCONTEXT), is(false));
        assertThat(customValueProcessorChain.execute("", ANONYMOUS_CSVCONTEXT), is(false));
        
        assertThat(customValueIgnoreCaseProcessor.execute("Ok", ANONYMOUS_CSVCONTEXT), is(true));
        assertThat(customValueIgnoreCaseProcessorChain.execute("Cancel", ANONYMOUS_CSVCONTEXT), is(false));
    }
    
    @Test
    public void testExecuteWithCustomValueIgnoreCase() {
        
        assertThat(customValueIgnoreCaseProcessor.execute("OK", ANONYMOUS_CSVCONTEXT), is(true));
        assertThat(customValueIgnoreCaseProcessorChain.execute("CANCEL", ANONYMOUS_CSVCONTEXT), is(false));
    }
    
    /**
     * Tests execution with a null input (should throw an Exception).
     */
    @Test(expected = SuperCsvCellProcessorException.class)
    public void testExecuteWithNull() {
        
        processor.execute(null, ANONYMOUS_CSVCONTEXT);
        fail();
    }
    
    @Test(expected=SuperCsvCellProcessorException.class)
    public void testExecuteWithNormalAtWrong() {
        
        processor.execute("True", ANONYMOUS_CSVCONTEXT);
        fail();
    }
    
    @Test(expected=SuperCsvCellProcessorException.class)
    public void testExecuteWithNormalAtNotString() {
        
        processor.execute(1, ANONYMOUS_CSVCONTEXT);
        fail();
    }
    
    @Test
    public void testExecuteWithNormalAtFailToFalse() {
        ParseBoolean cp = (ParseBoolean) processor;
        cp.setFailToFalse(true);
        assertThat(cp.execute("True", ANONYMOUS_CSVCONTEXT), is(false));
        
    }
    
    @Test(expected=SuperCsvCellProcessorException.class)
    public void testExecuteWithIgnoreCaseAtWrong() {
        
        processor.execute("abc", ANONYMOUS_CSVCONTEXT);
        fail();
    }
    
    @Test(expected=SuperCsvCellProcessorException.class)
    public void testExecuteWithCustomValueAtWrong() {
        
        processor.execute("OK", ANONYMOUS_CSVCONTEXT);
        fail();
    }
    
    @Test(expected=SuperCsvCellProcessorException.class)
    public void testExecuteWithCustomValueIgnoreCaseAtWrong() {
        
        processor.execute("ABC", ANONYMOUS_CSVCONTEXT);
        fail();
    }
    
    /**
     * Tests message code.
     */
    @Test
    public void testMessageCode() {
        
        ParseBoolean cp = (ParseBoolean) processor;
        assertThat(cp.getMessageCode(), is("org.supercsv.ext.cellprocessor.ParseBoolean.violated"));
        
    }
    
    /**
     * Tests message variables.
     */
    @Test
    public void testMessageVariable() {
        
        ParseBoolean cp = (ParseBoolean) processor;
        Map<String, ?> vars = cp.getMessageVariable();
        
        assertThat(vars, hasKey("trueValues"));
        assertThat(vars, hasEntry("trueStr", String.join(", ", ParseBoolean.DEFAULT_TRUE_VALUES)));
        assertThat(vars, hasKey("falseValues"));
        assertThat(vars, hasEntry("falseStr", String.join(", ", ParseBoolean.DEFAULT_FALSE_VALUES)));
        
        assertThat(vars, hasEntry("ignoreCase", false));
        assertThat(vars, hasEntry("failToFalse", false));
        
    }
    
    /**
     * Tests message variables.
     */
    @Test
    public void testMessageVariableWithCustomValue() {
        
        ParseBoolean cp = (ParseBoolean) customValueProcessor;
        Map<String, ?> vars = cp.getMessageVariable();
        
        assertThat(vars, hasKey("trueValues"));
        assertThat(vars, hasEntry("trueStr", String.join(", ", customTrues)));
        assertThat(vars, hasKey("falseValues"));
        assertThat(vars, hasEntry("falseStr", String.join(", ", customFalses)));
        
        assertThat(vars, hasEntry("ignoreCase", false));
        assertThat(vars, hasEntry("failToFalse", false));
        
    }
    
    /**
     * Tests format value
     */
    @Test
    public void testFormatValue() {
        
        ParseBoolean cp = (ParseBoolean) processor;
        assertThat(cp.formatValue(null), is(""));
        assertThat(cp.formatValue(true), is("true"));
        assertThat(cp.formatValue(false), is("false"));
        assertThat(cp.formatValue(Boolean.TRUE), is("true"));
        assertThat(cp.formatValue(Boolean.FALSE), is("false"));
        assertThat(cp.formatValue("abc"), is("abc"));
        
    }
    
    
}

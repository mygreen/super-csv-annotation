package com.github.mygreen.supercsv.cellprocessor.constraint;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;
import static org.assertj.core.api.Assertions.*;
import static com.github.mygreen.supercsv.tool.TestUtils.*;

import org.junit.Before;
import org.junit.Test;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.exception.SuperCsvCellProcessorException;

import com.github.mygreen.supercsv.exception.SuperCsvValidationException;

/**
 * {@link Pattern}のテスタ。
 *
 * @version 2.0
 * @since 1.2
 * @author T.TSUCHIE
 *
 */
public class PatternTest {
    
    private CellProcessor processor;
    private CellProcessor processorChain;
    
    private java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("[a-c]-\\$[0-9]+\\.[0-9]{2}", java.util.regex.Pattern.CASE_INSENSITIVE);
    private String description = "説明";
    
    @Before
    public void setUp() throws Exception {
        this.processor = new Pattern(pattern, description);
        this.processorChain = new Pattern(pattern, description, new NextCellProcessor());
        
    }
    
    @Test(expected=NullPointerException.class)
    public void testCheckConditions_patternNull() {
        
        new Pattern(null, description);
        fail();
        
    }
    
    @Test
    public void testExecute_inputNull() {
        
        assertThat((Object)processor.execute(null, ANONYMOUS_CSVCONTEXT)).isNull();
        
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
            
        } catch(Exception e) {
            assertThat(e).isInstanceOf(SuperCsvValidationException.class);
            
            SuperCsvValidationException exception = (SuperCsvValidationException)e;
            assertThat(exception.getMessageVariables())
                .containsEntry("regex", pattern.pattern())
                .containsEntry("flags", pattern.flags())
                .containsEntry("description", description);
            
            assertThat(exception.getRejectedValue()).isEqualTo(input);
            assertThat(exception.getValidationMessage()).isEqualTo(String.format("{%s.violated}", Pattern.class.getName()));
        }
        
    }
    
    
}

package com.github.mygreen.supercsv.cellprocessor.constraint;

import static org.junit.Assert.*;
import static org.assertj.core.api.Assertions.*;
import static com.github.mygreen.supercsv.tool.TestUtils.*;

import org.junit.Before;
import org.junit.Test;
import org.supercsv.cellprocessor.ift.CellProcessor;

import com.github.mygreen.supercsv.cellprocessor.NextCellProcessor;
import com.github.mygreen.supercsv.exception.SuperCsvValidationException;

/**
 * {@link Require}のテスタ
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class RequireTest {
    
    private CellProcessor processor;
    private CellProcessor processorChain;
    
    private CellProcessor emptyProcessor;
    private CellProcessor blankProcessor;
    private CellProcessor emptyBlankProcessor;
    
    @Before
    public void setUp() throws Exception {
        
        this.processor = new Require(false, false);
        this.processorChain = new Require(false, false, new NextCellProcessor());
        
        this.emptyProcessor = new Require(true, false);
        this.blankProcessor = new Require(false, true);
        this.emptyBlankProcessor = new Require(true, true);
    }
    
    @Test
    public void testExecute_integer() {
        
        Integer input = 1;
        
        assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(input);
        assertThat((Object)processorChain.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(input);
        
    }
    
    @Test
    public void testExecute_integer_inputNull() {
        
        Integer input = null;
        
        try {
            processor.execute(input, ANONYMOUS_CSVCONTEXT);
            fail();
            
        } catch(Exception e) {
            assertThat(e).isInstanceOf(SuperCsvValidationException.class);
            
            SuperCsvValidationException exception = (SuperCsvValidationException)e;
            assertThat(exception.getMessageVariables())
                .containsEntry("considerEmpty", false)
                .containsEntry("considerBlank", false);
            
            assertThat(exception.getRejectedValue()).isEqualTo(input);
            assertThat(exception.getValidationMessage()).isEqualTo(String.format("{%s.violated}", Require.class.getName()));
        }
    }
    
    @Test
    public void testExecute_string() {
        
        String input = "abc";
        
        assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(input);
        assertThat((Object)processorChain.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(input);
        
    }
    
    @Test
    public void testExecute_string_inputNull() {
        
        String input = null;
        
        try {
            processor.execute(input, ANONYMOUS_CSVCONTEXT);
            fail();
            
        } catch(Exception e) {
            assertThat(e).isInstanceOf(SuperCsvValidationException.class);
            
            SuperCsvValidationException exception = (SuperCsvValidationException)e;
            assertThat(exception.getMessageVariables())
                .containsEntry("considerEmpty", false)
                .containsEntry("considerBlank", false);
            
            assertThat(exception.getRejectedValue()).isEqualTo(input);
        }
        
        try {
            emptyProcessor.execute(input, ANONYMOUS_CSVCONTEXT);
            fail();
            
        } catch(Exception e) {
            assertThat(e).isInstanceOf(SuperCsvValidationException.class);
            
            SuperCsvValidationException exception = (SuperCsvValidationException)e;
            assertThat(exception.getMessageVariables())
                .containsEntry("considerEmpty", true)
                .containsEntry("considerBlank", false);
            
            assertThat(exception.getRejectedValue()).isEqualTo(input);
        }
        
        try {
            blankProcessor.execute(input, ANONYMOUS_CSVCONTEXT);
            fail();
            
        } catch(Exception e) {
            assertThat(e).isInstanceOf(SuperCsvValidationException.class);
            
            SuperCsvValidationException exception = (SuperCsvValidationException)e;
            assertThat(exception.getMessageVariables())
                .containsEntry("considerEmpty", false)
                .containsEntry("considerBlank", true);
            
            assertThat(exception.getRejectedValue()).isEqualTo(input);
        }
        
        try {
            emptyBlankProcessor.execute(input, ANONYMOUS_CSVCONTEXT);
            fail();
            
        } catch(Exception e) {
            assertThat(e).isInstanceOf(SuperCsvValidationException.class);
            
            SuperCsvValidationException exception = (SuperCsvValidationException)e;
            assertThat(exception.getMessageVariables())
                .containsEntry("considerEmpty", true)
                .containsEntry("considerBlank", true);
            
            assertThat(exception.getRejectedValue()).isEqualTo(input);
        }
        
    }
    
    @Test
    public void testExecute_string_inputEmpty() {
        
        String input = "";
        
        assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(input);
        
        try {
            emptyProcessor.execute(input, ANONYMOUS_CSVCONTEXT);
            fail();
            
        } catch(Exception e) {
            assertThat(e).isInstanceOf(SuperCsvValidationException.class);
            
            SuperCsvValidationException exception = (SuperCsvValidationException)e;
            assertThat(exception.getMessageVariables())
                .containsEntry("considerEmpty", true)
                .containsEntry("considerBlank", false);
            
            assertThat(exception.getRejectedValue()).isEqualTo(input);
        }
        
        try {
            blankProcessor.execute(input, ANONYMOUS_CSVCONTEXT);
            fail();
            
        } catch(Exception e) {
            assertThat(e).isInstanceOf(SuperCsvValidationException.class);
            
            SuperCsvValidationException exception = (SuperCsvValidationException)e;
            assertThat(exception.getMessageVariables())
                .containsEntry("considerEmpty", false)
                .containsEntry("considerBlank", true);
            
            assertThat(exception.getRejectedValue()).isEqualTo(input);
        }
        
        try {
            emptyBlankProcessor.execute(input, ANONYMOUS_CSVCONTEXT);
            fail();
            
        } catch(Exception e) {
            assertThat(e).isInstanceOf(SuperCsvValidationException.class);
            
            SuperCsvValidationException exception = (SuperCsvValidationException)e;
            assertThat(exception.getMessageVariables())
                .containsEntry("considerEmpty", true)
                .containsEntry("considerBlank", true);
            
            assertThat(exception.getRejectedValue()).isEqualTo(input);
        }
        
    }
    
    @Test
    public void testExecute_string_inputBlank() {
        
        String input = "  ";
        
        assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(input);
        
        assertThat((Object)emptyProcessor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(input);
        
        try {
            blankProcessor.execute(input, ANONYMOUS_CSVCONTEXT);
            fail();
            
        } catch(Exception e) {
            assertThat(e).isInstanceOf(SuperCsvValidationException.class);
            
            SuperCsvValidationException exception = (SuperCsvValidationException)e;
            assertThat(exception.getMessageVariables())
                .containsEntry("considerEmpty", false)
                .containsEntry("considerBlank", true);
            
            assertThat(exception.getRejectedValue()).isEqualTo(input);
        }
        
        try {
            emptyBlankProcessor.execute(input, ANONYMOUS_CSVCONTEXT);
            fail();
            
        } catch(Exception e) {
            assertThat(e).isInstanceOf(SuperCsvValidationException.class);
            
            SuperCsvValidationException exception = (SuperCsvValidationException)e;
            assertThat(exception.getMessageVariables())
                .containsEntry("considerEmpty", true)
                .containsEntry("considerBlank", true);
            
            assertThat(exception.getRejectedValue()).isEqualTo(input);
        }
        
    }
    
}

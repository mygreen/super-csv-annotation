package com.github.mygreen.supercsv.cellprocessor.constraint;

import static org.junit.Assert.*;
import static org.assertj.core.api.Assertions.*;
import static com.github.mygreen.supercsv.tool.TestUtils.*;

import org.junit.Before;
import org.junit.Test;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.exception.SuperCsvCellProcessorException;

import com.github.mygreen.supercsv.exception.SuperCsvValidationException;

/**
 * {@link LengthMin}のテスタ
 *
 * @version 2.0
 * @since 1.2
 * @author T.TSUCHIE
 *
 */
public class LengthMinTest {
    
    private CellProcessor processor;
    private CellProcessor processorChain;
    
    private String minStr = "abcdefghij";
    private int min = minStr.length();
    
    @Before
    public void setUp() throws Exception {
        
        this.processor = new LengthMin(min);
        this.processorChain = new LengthMin(min, new NextCellProcessor());
        
    }
    
    /**
     * Test constructor argument's with wrong values.
     * <p>min minus</p>
     */
    @Test(expected=IllegalArgumentException.class)
    public void testConstructor_minMinus() {
        
        new LengthMin(-1);
        
        fail();
        
    }
    
    /**
     * Test constructor argument's with wrong values.
     * next is null.
     */
    @Test(expected=NullPointerException.class)
    public void testConstructor_nextNull() {
        
        new LengthMin(min, null);
        
        fail();
        
    }
    
    /**
     * Test execusion with a null input.
     */
    @Test
    public void testExecute_inputNull() {
        
        assertThat((Object)processor.execute(null, ANONYMOUS_CSVCONTEXT)).isNull();
        
    }
    
    /**
     * Test execusion with valid value.
     */
    @Test
    public void testExecute_inputValid() {
        
        {
            String input = minStr;
            
            assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(input);
            assertThat((Object)processorChain.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(input);
            
        }
        
        {
            String input = minStr + "k";
            
            assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(input);
            assertThat((Object)processorChain.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(input);
            
        }
        
    }
    
    @Test
    public void testExecute_inputInvalid() {
        
        String input = minStr.substring(0, min -1);
        try {
            processor.execute(input, ANONYMOUS_CSVCONTEXT);
            
            fail();
        
        } catch(Exception e) {
            
            assertThat(e).isInstanceOf(SuperCsvValidationException.class);
            
            SuperCsvValidationException exception = (SuperCsvValidationException)e;
            assertThat(exception.getMessageVariables())
                .containsEntry("min", min)
                .containsEntry("length", input.length());
            
            assertThat(exception.getRejectedValue()).isEqualTo(input);
            assertThat(exception.getValidationMessage()).isEqualTo(String.format("{%s.violated}", LengthMin.class.getName()));
            
        }
        
    }
    
}

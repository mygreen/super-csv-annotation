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
 * {@link LengthBetween}のテスタ
 *
 * @version 2.0
 * @author T.TSUCHIE
 *
 */
public class LengthBetweenTest {
    
    private CellProcessor processor;
    private CellProcessor processorChain;
    
    private String minStr = "abcde";
    private int min = minStr.length();
    
    private String maxStr = "abcdefghij";
    private int max = maxStr.length();
    
    @Before
    public void setUp() throws Exception {
        
        this.processor = new LengthBetween(min, max);
        this.processorChain = new LengthBetween(min, max, new NextCellProcessor());
        
    }
    
    /**
     * Test constructor argument's with wrong values.
     * <p>{@literal max < min}</p>
     */
    @Test(expected=IllegalArgumentException.class)
    public void testConstuctor_maxLessThanMin() {
        
        new LengthBetween(max, min);
        
        fail();
        
    }
    
    /**
     * Test constructor argument's with wrong values.
     * <p>{@literal min < 0}</p>
     */
    @Test(expected=IllegalArgumentException.class)
    public void testConstructor_minLessThan0() {
        
        new LengthBetween(-1, max);
        
        fail();
        
    }
    
    /**
     * Test constructor argument's with wrong values.
     * <p>next is null</p>
     */
    @Test(expected=NullPointerException.class)
    public void testConstructor_nexNull() {
        
        new LengthBetween(min, max, null);
        
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
            String input = minStr + "f";
            
            assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(input);
            assertThat((Object)processorChain.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(input);
        }
        
        {
            String input = maxStr;
            
            assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(input);
            assertThat((Object)processorChain.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(input);
        }
        
        {
            String input = maxStr.substring(0, max-1);
            
            assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(input);
            assertThat((Object)processorChain.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(input);
        }
        
        
    }
    
    /**
     * Test execusion with invalid value.
     * <p>最大長よりも大きい場合</p>
     */
    @Test
    public void testExecute_inputAvovbeMax() {
        
        String input = maxStr + "k";
        try {
            processor.execute(input, ANONYMOUS_CSVCONTEXT);
            
            fail();
            
        } catch(Exception e) {
            
            assertThat(e).isInstanceOf(SuperCsvValidationException.class);
            
            SuperCsvValidationException exception = (SuperCsvValidationException)e;
            assertThat(exception.getMessageVariables())
                .containsEntry("min", min)
                .containsEntry("max", max)
                .containsEntry("length", input.length());
            
            assertThat(exception.getRejectedValue()).isEqualTo(input);
            assertThat(exception.getValidationMessage()).isEqualTo(String.format("{%s.violated}", LengthBetween.class.getName()));
            
        }
        
    }
    
    /**
     * Test execusion with invalid value.
     * <p>最小長よりも小さい場合</p>
     */
    @Test
    public void testExecute_inputBelowMin() {
        
        String input = minStr.substring(0, min-1);
        try {
            processor.execute(input, ANONYMOUS_CSVCONTEXT);
            
            fail();
            
        } catch(Exception e) {
            
            assertThat(e).isInstanceOf(SuperCsvValidationException.class);
            
            SuperCsvValidationException exception = (SuperCsvValidationException)e;
            assertThat(exception.getMessageVariables())
                .containsEntry("min", min)
                .containsEntry("max", max)
                .containsEntry("length", input.length());
            
            assertThat(exception.getRejectedValue()).isEqualTo(input);
            assertThat(exception.getValidationMessage()).isEqualTo(String.format("{%s.violated}", LengthBetween.class.getName()));
            
        }
        
    }
}

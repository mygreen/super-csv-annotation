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
 * {@link LengthMax}のテスタ
 *
 * @version 2.0
 * @since 1.2
 * @author T.TSUCHIE
 *
 */
public class LengthMaxTest {
    
    private CellProcessor processor;
    private CellProcessor processorChain;
    
    private String maxStr = "abcdefghij";
    private int max = maxStr.length();
    
    @Before
    public void setUp() throws Exception {
        
        this.processor = new LengthMax(max);
        this.processorChain = new LengthMax(max, new NextCellProcessor());
        
    }
    
    /**
     * Test constructor argument's with wrong values.
     * <p>max is zero</p>
     */
    @Test(expected=IllegalArgumentException.class)
    public void testConstructor_max0() {
        
        new LengthMax(0);
        fail();
        
    }
    
    /**
     * Test constructor argument's with wrong values.
     * next is null.
     */
    @Test(expected=NullPointerException.class)
    public void testConstructor_nextNull() {
        
        new LengthMax(max, null);
        
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
    
    @Test
    public void testExecute_inputInvalid() {
        
        String input = maxStr + "k";
        try {
            processor.execute(input, ANONYMOUS_CSVCONTEXT);
            
            fail();
        
        } catch(Exception e) {
            
            assertThat(e).isInstanceOf(SuperCsvValidationException.class);
            
            SuperCsvValidationException exception = (SuperCsvValidationException)e;
            assertThat(exception.getMessageVariables())
                .containsEntry("max", max)
                .containsEntry("length", input.length());
            
            assertThat(exception.getRejectedValue()).isEqualTo(input);
            assertThat(exception.getValidationMessage()).isEqualTo(String.format("{%s.violated}", LengthMax.class.getName()));
            
        }
        
    }
    
}

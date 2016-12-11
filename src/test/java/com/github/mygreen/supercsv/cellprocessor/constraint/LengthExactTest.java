package com.github.mygreen.supercsv.cellprocessor.constraint;

import static org.junit.Assert.*;
import static org.assertj.core.api.Assertions.*;
import static com.github.mygreen.supercsv.tool.TestUtils.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.exception.SuperCsvCellProcessorException;

import com.github.mygreen.supercsv.exception.SuperCsvValidationException;

/**
 * {@link LengthExact}のテスタ
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class LengthExactTest {
    
    private CellProcessor processor;
    private CellProcessor processorChain;
    
    private String str3 = "abc";
    private String str5 = "abcde";
    private List<Integer> requiredLengths = Arrays.asList(3, 5, 7);
    
    @Before
    public void setUp() throws Exception {
        
        this.processor = new LengthExact(requiredLengths);
        this.processorChain = new LengthExact(requiredLengths, new NextCellProcessor());
        
    }
    
    /**
     * {@literal requiredLengths is null}
     */
    @Test(expected=NullPointerException.class)
    public void testConstuctor_requiredLengthsNull() {
        
        new LengthExact(null);
        
        fail();
        
    }
    
    /**
     * {@literal requiredLengths is empty}
     */
    @Test(expected=IllegalArgumentException.class)
    public void testConstuctor_requiredLengthsEmpty() {
        
        new LengthExact(Collections.emptyList());
        
        fail();
        
    }
    
    /**
     * {@literal next is null}
     */
    @Test(expected=NullPointerException.class)
    public void testConstuctor_nextNull() {
        
        new LengthExact(requiredLengths, null);
        
        fail();
        
    }
    
    /**
     * Test execusion with a null input.
     */
    @Test
    public void testExecute_inputNull() {
        
        assertThat((Object)processor.execute(null, ANONYMOUS_CSVCONTEXT)).isNull();
        
    }
    
    @Test
    public void testExecut_inputValid() {
        
        {
            String input = str3;
            
            assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(input);
            assertThat((Object)processorChain.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(input);
        }
        
        {
            String input = str5;
            
            assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(input);
            assertThat((Object)processorChain.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(input);
        }
        
    }
    
    @Test
    public void testExecute_inputInvalid() {
        
        String input = "abcd";
        try {
            processor.execute(input, ANONYMOUS_CSVCONTEXT);
            
            fail();
        
        } catch(Exception e) {
            
            assertThat(e).isInstanceOf(SuperCsvValidationException.class);
            
            SuperCsvValidationException exception = (SuperCsvValidationException)e;
            assertThat(exception.getMessageVariables())
                .containsEntry("length", input.length())
                .containsEntry("requiredLengths", new int[]{3, 5, 7});
            
            assertThat(exception.getRejectedValue()).isEqualTo(input);
            assertThat(exception.getValidationMessage()).isEqualTo(String.format("{%s.violated}", LengthExact.class.getName()));
            
        }
        
    }
    
}

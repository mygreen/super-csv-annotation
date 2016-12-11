package com.github.mygreen.supercsv.cellprocessor.constraint;

import static org.junit.Assert.*;
import static org.assertj.core.api.Assertions.*;
import static com.github.mygreen.supercsv.tool.TestUtils.*;


import org.junit.Before;
import org.junit.Test;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.exception.SuperCsvCellProcessorException;

import java.text.DecimalFormat;

import com.github.mygreen.supercsv.cellprocessor.format.NumberFormatWrapper;
import com.github.mygreen.supercsv.exception.SuperCsvValidationException;

/**
 * {@link NumberMin}のテスタ
 *
 * @version 2.0
 * @since 1.2
 * @author T.TSUCHIE
 *
 */
public class NumberMinTest {
    
    private CellProcessor processor;
    private CellProcessor processorChain;
    
    private CellProcessor processorNonInclusive;
    
    private NumberFormatWrapper<Integer> formatter = new NumberFormatWrapper<>(new DecimalFormat("#,###"), Integer.class);
    
    private static final Integer TEST_VALUE_MIN_OBJ = new Integer(1000);
    
    @Before
    public void setUp() throws Exception {
        this.processor = new NumberMin<>(TEST_VALUE_MIN_OBJ, true, formatter);
        this.processorChain = new NumberMin<>(TEST_VALUE_MIN_OBJ, true, formatter, new NextCellProcessor());
        
        this.processorNonInclusive = new NumberMin<>(TEST_VALUE_MIN_OBJ, false, formatter);
    }
    
    @Test(expected=NullPointerException.class)
    public void testConstructor_minNull() {
        
        new NumberMin<Integer>(null, true, formatter);
        
        fail();
    }
    
    @Test(expected=NullPointerException.class)
    public void testConstructor_printerNull() {
        
        new NumberMin<Integer>(TEST_VALUE_MIN_OBJ, true, null);
        
        fail();
    }
    
    @Test(expected=NullPointerException.class)
    public void testConstructor_nextNull() {
        
        new NumberMin<Integer>(TEST_VALUE_MIN_OBJ, true, formatter, null);
        
        fail();
    }
    
    /**
     * 入力値がnullの場合
     */
    @Test
    public void testExecute_inputNull() {
        
        assertThat((Object)processor.execute(null, ANONYMOUS_CSVCONTEXT)).isNull();
       
    }
    
    /**
     * 入力値のクラスタイプが不正な場合
     */
    @Test(expected=SuperCsvCellProcessorException.class)
    public void testExecute_inputNonSameInstance() {
        
        processor.execute("abc", ANONYMOUS_CSVCONTEXT);
        fail();
        
    }
    
    @Test
    public void testExecute_inputValid() {
        
        {
            int input = TEST_VALUE_MIN_OBJ;
            assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(input);
            assertThat((Object)processorChain.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(input);
        
        }
        
        {
            int input = TEST_VALUE_MIN_OBJ + 1;
            assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(input);
            assertThat((Object)processorChain.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(input);
        
        }
        
    }
    
    /**
     * 入力値が最小値よりも小さい場合
     */
    @Test
    public void testExecute_inputBelowMin() {
        
        int input = TEST_VALUE_MIN_OBJ -1;
        try {
            processor.execute(input, ANONYMOUS_CSVCONTEXT);
            
            fail();
            
        } catch(Exception e) {
            
            assertThat(e).isInstanceOf(SuperCsvValidationException.class);
            
            SuperCsvValidationException exception = (SuperCsvValidationException)e;
            assertThat(exception.getMessageVariables())
                .containsEntry("min", TEST_VALUE_MIN_OBJ)
                .containsEntry("inclusive", true)
                .containsEntry("printer", formatter);
            
            assertThat(exception.getRejectedValue()).isEqualTo(input);
            assertThat(exception.getValidationMessage()).isEqualTo(String.format("{%s.violated}", NumberMin.class.getName()));
            
        }
        
    }
    
    /**
     * inclusive - falseの場合
     */
    @Test
    public void testExecute_nonInclusive() {
        
        {
            int input = TEST_VALUE_MIN_OBJ;
            try {
                processorNonInclusive.execute(input, ANONYMOUS_CSVCONTEXT);
                
                fail();
                
            } catch(Exception e) {
                
                assertThat(e).isInstanceOf(SuperCsvValidationException.class);
                
                SuperCsvValidationException exception = (SuperCsvValidationException)e;
                assertThat(exception.getMessageVariables())
                    .containsEntry("min", TEST_VALUE_MIN_OBJ)
                    .containsEntry("inclusive", false)
                    .containsEntry("printer", formatter);
                
                assertThat(exception.getRejectedValue()).isEqualTo(input);
                assertThat(exception.getValidationMessage()).isEqualTo(String.format("{%s.violated}", NumberMin.class.getName()));
                
            }
        }
        
        {
            int input = TEST_VALUE_MIN_OBJ + 1;
            assertThat((Object)processorNonInclusive.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(input);
        
        }
        
        {
            int input = TEST_VALUE_MIN_OBJ -1;
            try {
                processorNonInclusive.execute(input, ANONYMOUS_CSVCONTEXT);
                
                fail();
                
            } catch(Exception e) {
                
                assertThat(e).isInstanceOf(SuperCsvValidationException.class);
                
                SuperCsvValidationException exception = (SuperCsvValidationException)e;
                assertThat(exception.getMessageVariables())
                    .containsEntry("min", TEST_VALUE_MIN_OBJ)
                    .containsEntry("inclusive", false)
                    .containsEntry("printer", formatter);
                
                assertThat(exception.getRejectedValue()).isEqualTo(input);
                assertThat(exception.getValidationMessage()).isEqualTo(String.format("{%s.violated}", NumberMin.class.getName()));
                
            }
        }
        
    }
    
}

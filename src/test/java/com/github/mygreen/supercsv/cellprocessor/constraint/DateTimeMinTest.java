package com.github.mygreen.supercsv.cellprocessor.constraint;

import static org.junit.Assert.*;
import static org.assertj.core.api.Assertions.*;
import static com.github.mygreen.supercsv.tool.TestUtils.*;


import org.junit.Before;
import org.junit.Test;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.exception.SuperCsvCellProcessorException;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.github.mygreen.supercsv.cellprocessor.format.DateFormatWrapper;
import com.github.mygreen.supercsv.exception.SuperCsvValidationException;

/**
 * {@link DateTimeMin}のテスタ
 *
 * @version 2.0
 * @since 1.2
 * @author T.TSUCHIE
 *
 */
public class DateTimeMinTest {
    
    private CellProcessor processor;
    private CellProcessor processorChain;
    
    private CellProcessor processorNonInclusive;
    
    private DateFormatWrapper<Date> formatter = new DateFormatWrapper<>(new SimpleDateFormat("yyyy/MM/dd"), Date.class);
    
    private static final Date TEST_VALUE_MIN_OBJ = toDate(2000, 1, 1);
    
    @Before
    public void setUp() throws Exception {
        this.processor = new DateTimeMin<>(TEST_VALUE_MIN_OBJ, true, formatter);
        this.processorChain = new DateTimeMin<>(TEST_VALUE_MIN_OBJ, true, formatter, new NextCellProcessor());
        
        this.processorNonInclusive = new DateTimeMin<>(TEST_VALUE_MIN_OBJ, false, formatter);
    }
    
    @Test(expected=NullPointerException.class)
    public void testConstructor_minNull() {
        
        new DateTimeMin<Date>(null, true, formatter);
        
        fail();
    }
    
    @Test(expected=NullPointerException.class)
    public void testConstructor_printerNull() {
        
        new DateTimeMin<Date>(TEST_VALUE_MIN_OBJ, true, null);
        
        fail();
    }
    
    @Test(expected=NullPointerException.class)
    public void testConstructor_nextNull() {
        
        new DateTimeMin<Date>(TEST_VALUE_MIN_OBJ, true, formatter, null);
        
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
            Date input = TEST_VALUE_MIN_OBJ;
            assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(input);
            assertThat((Object)processorChain.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(input);
        
        }
        
        {
            Date input = plusDays(TEST_VALUE_MIN_OBJ, 1);
            assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(input);
            assertThat((Object)processorChain.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(input);
        
        }
        
    }
    
    /**
     * 入力値が最小値よりも小さい場合
     */
    @Test
    public void testExecute_inputBelowMin() {
        
        Date input = minusDays(TEST_VALUE_MIN_OBJ, 1);
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
            assertThat(exception.getValidationMessage()).isEqualTo(String.format("{%s.violated}", DateTimeMin.class.getName()));
            
        }
        
    }
    
    @Test
    public void testExecute_nonInclusive() {
        
        {
            Date input = TEST_VALUE_MIN_OBJ;
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
                assertThat(exception.getValidationMessage()).isEqualTo(String.format("{%s.violated}", DateTimeMin.class.getName()));
                
            }
        
        }
        
        {
            Date input = plusDays(TEST_VALUE_MIN_OBJ, 1);
            assertThat((Object)processorNonInclusive.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(input);
        
        }
        
        {
            Date input = minusDays(TEST_VALUE_MIN_OBJ, 1);
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
                assertThat(exception.getValidationMessage()).isEqualTo(String.format("{%s.violated}", DateTimeMin.class.getName()));
                
            }
        }
        
    }
    
}

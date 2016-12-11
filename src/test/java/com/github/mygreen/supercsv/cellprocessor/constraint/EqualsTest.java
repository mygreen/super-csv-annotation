package com.github.mygreen.supercsv.cellprocessor.constraint;

import static org.junit.Assert.*;
import static org.assertj.core.api.Assertions.*;
import static com.github.mygreen.supercsv.tool.TestUtils.*;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.exception.SuperCsvCellProcessorException;

import com.github.mygreen.supercsv.annotation.CsvBean;
import com.github.mygreen.supercsv.annotation.CsvColumn;
import com.github.mygreen.supercsv.cellprocessor.format.NumberFormatWrapper;
import com.github.mygreen.supercsv.exception.SuperCsvValidationException;

/**
 * {@link Equals}のテスタ
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class EqualsTest {
    
    private CellProcessor processor;
    private CellProcessor processorChain;
    
    private CellProcessor processorValuesEmpty;
    
    private NumberFormatWrapper<Integer> formatter = new NumberFormatWrapper<>(new DecimalFormat("#,###"), Integer.class);
    
    private List<Integer> VALUES = Arrays.asList(1000, -1000);
    
    @Before
    public void setUp() throws Exception {
        this.processor = new Equals<>(Integer.class, VALUES, formatter);
        this.processorChain = new Equals<>(Integer.class, VALUES, formatter, new NextCellProcessor());
        
        this.processorValuesEmpty = new Equals<>(Integer.class, Collections.emptyList(), formatter);
    }
    
    @Test(expected=NullPointerException.class)
    public void testConstuctor_typeNull() {
        
        new Equals<Integer>(null, VALUES, formatter);
        fail();
        
    }
    
    @Test(expected=NullPointerException.class)
    public void testConstuctor_valuesNull() {
        
        new Equals<Integer>(Integer.class, null, formatter);
        fail();
        
    }
    
    @Test(expected=NullPointerException.class)
    public void testConstuctor_printerNull() {
        
        new Equals<Integer>(Integer.class, VALUES, null);
        fail();
        
    }
    
    @Test(expected=NullPointerException.class)
    public void testConstuctor_nextNull() {
        
        new Equals<Integer>(Integer.class, VALUES, formatter, null);
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
            int input = 1000;
            
            assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(input);
            assertThat((Object)processorChain.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(input);
        }
        
    }
    
    @Test
    public void testExecute_inputInvalid() {
        
        int input = 9999;
        try {
            processor.execute(input, ANONYMOUS_CSVCONTEXT);
            
        } catch(Exception e) {
            assertThat(e).isInstanceOf(SuperCsvValidationException.class);
            
            SuperCsvValidationException exception = (SuperCsvValidationException)e;
            assertThat(exception.getMessageVariables())
                .containsEntry("equalsValues", VALUES)
                .containsEntry("printer", formatter);
            
            assertThat(exception.getRejectedValue()).isEqualTo(input);
            assertThat(exception.getValidationMessage()).isEqualTo(String.format("{%s.violated}", Equals.class.getName()));
        }
        
    }
    
    /**
     * 登録されている値が0件の場合
     */
    @Test
    public void testExecute_valueEmpty() {
        
        {
            int input = 1000;
            
            assertThat((Object)processorValuesEmpty.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(input);
        }
        
    }
    
    @CsvBean
    private static class TestCsv {
        
        @CsvColumn(number=1)
        Integer column1;
        
    }
}

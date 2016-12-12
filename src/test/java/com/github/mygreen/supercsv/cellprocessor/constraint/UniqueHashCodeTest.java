package com.github.mygreen.supercsv.cellprocessor.constraint;

import static org.junit.Assert.*;
import static org.assertj.core.api.Assertions.*;
import static com.github.mygreen.supercsv.tool.TestUtils.*;

import java.text.DecimalFormat;

import org.junit.Before;
import org.junit.Test;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.exception.SuperCsvCellProcessorException;
import org.supercsv.util.CsvContext;

import com.github.mygreen.supercsv.cellprocessor.format.NumberFormatWrapper;
import com.github.mygreen.supercsv.exception.SuperCsvValidationException;

/**
 * {@link UniqueHashCode}のテスタ
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class UniqueHashCodeTest {
    
    private CellProcessor processor;
    private CellProcessor processorChain;
    
    private NumberFormatWrapper<Integer> formatter = new NumberFormatWrapper<>(new DecimalFormat("#,###"), Integer.class);
    
    private CsvContext ANONYMOUS_CSVCONTEXT2 = new CsvContext(2, 3, 3);
    
    @Before
    public void setUp() throws Exception {
        this.processor = new UniqueHashCode<>(formatter);
        this.processorChain = new UniqueHashCode<>(formatter, new NextCellProcessor());
    }
    
    @Test(expected=NullPointerException.class)
    public void testConstuctor_printerNull() {
        
        new UniqueHashCode<Integer>(null);
        fail();
        
    }
    
    @Test(expected=NullPointerException.class)
    public void testConstuctor_nextNull() {
        
        new UniqueHashCode<Integer>(formatter, null);
        fail();
        
    }
    
    /**
     * 入力値がnullの場合
     */
    @Test
    public void testExecute_inputNull() {
        
        assertThat((Object)processor.execute(null, ANONYMOUS_CSVCONTEXT)).isNull();
       
    }
    
    @Test
    public void testExecute_inputValid() {
        
        {
            Integer input = 1000;
            assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(input);
            assertThat((Object)processorChain.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(input);
            
            input = 2000;
            assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT2)).isEqualTo(input);
            assertThat((Object)processorChain.execute(input, ANONYMOUS_CSVCONTEXT2)).isEqualTo(input);
        }
        
    }
    
    @Test
    public void testExecute_inputInvalid() {
        
        Integer input = 1000;
        try {
            assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(input);
            
            // 2回目の読み込み
            processor.execute(input, ANONYMOUS_CSVCONTEXT2);
            fail();
        
        } catch(Exception e) {
            
            assertThat(e).isInstanceOf(SuperCsvValidationException.class);
            
            SuperCsvValidationException exception = (SuperCsvValidationException)e;
            assertThat(exception.getMessageVariables())
                .containsEntry("hashCode", input.hashCode())
                .containsEntry("duplicatedLineNumber", 1)
                .containsEntry("duplicatedRowNumber", 2)
                .containsEntry("printer", formatter);
            
            assertThat(exception.getRejectedValue()).isEqualTo(input);
            assertThat(exception.getValidationMessage()).isEqualTo(String.format("{%s.violated}", UniqueHashCode.class.getName()));
            
        }
        
    }
    
    
}

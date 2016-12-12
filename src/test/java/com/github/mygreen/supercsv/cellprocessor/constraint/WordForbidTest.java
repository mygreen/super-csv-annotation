package com.github.mygreen.supercsv.cellprocessor.constraint;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static com.github.mygreen.supercsv.tool.TestUtils.*;

import org.junit.Before;
import org.junit.Test;
import org.supercsv.cellprocessor.ift.CellProcessor;

import com.github.mygreen.supercsv.annotation.CsvBean;
import com.github.mygreen.supercsv.annotation.CsvColumn;
import com.github.mygreen.supercsv.exception.SuperCsvValidationException;

/**
 * {@link WordForbid}のテスタ
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class WordForbidTest {
    
    private CellProcessor processor;
    private CellProcessor processorChain;
    
    private CellProcessor processorWordEmpty;
    
    private List<String> WORDS = Arrays.asList("馬鹿", "阿呆");
    
    @Before
    public void setUp() throws Exception {
        this.processor = new WordForbid(WORDS);
        this.processorChain = new WordForbid(WORDS, new NextCellProcessor());
        
        this.processorWordEmpty = new WordForbid(Collections.emptyList());
    }
    
    @Test(expected=NullPointerException.class)
    public void testConstuctor_wordsNull() {
        
        new WordForbid(null);
        fail();
        
    }
    
    @Test(expected=NullPointerException.class)
    public void testConstuctor_nextNull() {
        
        new WordForbid(WORDS, null);
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
            String input = "今日はいい天気です。";
            
            assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(input);
            assertThat((Object)processorChain.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(input);
        }
        
    }
    
    @Test
    public void testExecute_inputInvalid() {
        
        String input = "今日は馬鹿で阿呆な奴に会った。馬鹿馬鹿しいです。";
        try {
            
            processor.execute(input, ANONYMOUS_CSVCONTEXT);
            fail();
            
        } catch(Exception e) {
            assertThat(e).isInstanceOf(SuperCsvValidationException.class);
            
            SuperCsvValidationException exception = (SuperCsvValidationException)e;
            assertThat(exception.getMessageVariables())
                .containsEntry("words", Arrays.asList("馬鹿", "阿呆"));
            
            assertThat(exception.getRejectedValue()).isEqualTo(input);
            assertThat(exception.getValidationMessage()).isEqualTo(String.format("{%s.violated}", WordForbid.class.getName()));
        }
        
    }
    
    /**
     * 登録されている語彙が空の場合
     */
    @Test
    public void testExecute_wordEmpty() {
        
        {
            String input = "今日はいい天気です。";
            
            assertThat((Object)processorWordEmpty.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(input);
        }
        
    }
    
    @CsvBean
    private static class TestCsv {
        
        @CsvColumn(number=1)
        String column1;
        
    }
    
}

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

import com.github.mygreen.supercsv.annotation.CsvBean;
import com.github.mygreen.supercsv.annotation.CsvColumn;
import com.github.mygreen.supercsv.exception.SuperCsvValidationException;

/**
 * {@link WordRequire}のテスタ
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class WordRequireTest {
    
    private CellProcessor processor;
    private CellProcessor processorChain;
    
    private CellProcessor processorWordEmpty;
    
    private static List<String> WORDS = Arrays.asList("今日", "天気", "よろしく");
    
    private static final String VALID_INPUT = "今日はいい天気です。よろしくお願いいたします。";
    private static final String INVALID_INPUT = "明日は晴れるといいね。よろしくお願いいたします。";
    
    @Before
    public void setUp() throws Exception {
        this.processor = new WordRequire(WORDS);
        this.processorChain = new WordRequire(WORDS, new NextCellProcessor());
        
        this.processorWordEmpty = new WordRequire(Collections.emptyList());
    }
    
    @Test(expected=NullPointerException.class)
    public void testConstuctor_wordsNull() {
        
        new WordRequire(null);
        fail();
        
    }
    
    @Test(expected=NullPointerException.class)
    public void testConstuctor_nextNull() {
        
        new WordRequire(WORDS, null);
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
            String input = VALID_INPUT;
            
            assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(input);
            assertThat((Object)processorChain.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(input);
        }
        
    }
    
    @Test
    public void testExecute_inputInvalid() {
        
        String input = INVALID_INPUT;
        try {
            
            processor.execute(input, ANONYMOUS_CSVCONTEXT);
            fail();
            
        } catch(Exception e) {
            assertThat(e).isInstanceOf(SuperCsvValidationException.class);
            
            SuperCsvValidationException exception = (SuperCsvValidationException)e;
            assertThat(exception.getMessageVariables())
                .containsEntry("words", Arrays.asList("今日", "天気"));
            
            assertThat(exception.getRejectedValue()).isEqualTo(input);
            assertThat(exception.getValidationMessage()).isEqualTo(String.format("{%s.violated}", WordRequire.class.getName()));
        }
        
    }
    
    /**
     * 登録されている語彙が0件の場合
     */
    @Test
    public void testExecute_wordEmpty() {
        
        {
            String input = VALID_INPUT;
            
            assertThat((Object)processorWordEmpty.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(input);
        }
        
    }
    
    @CsvBean
    private static class TestCsv {
        
        @CsvColumn(number=1)
        String column1;
        
    }
}

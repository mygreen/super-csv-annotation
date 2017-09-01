package com.github.mygreen.supercsv.cellprocessor.conversion;

import static org.junit.Assert.*;
import static org.assertj.core.api.Assertions.*;
import static com.github.mygreen.supercsv.tool.TestUtils.*;

import org.supercsv.cellprocessor.ift.CellProcessor;

import org.junit.Before;
import org.junit.Test;



/**
 * {@link OneSideTrim}のテスタ
 *
 * @since 2.1
 * @author T.TSUCHIE
 *
 */
public class OneSideTrimTest {
    
    private CellProcessor leftProcessor;
    
    private CellProcessor leftProcessorChain;
    
    private CellProcessor rightProcessor;
    
    private CellProcessor rightProcessorChain;
    
    private CellProcessor fullCharLeftProcessor;
    
    private CellProcessor fullCharRightProcessor;
    
    @Before
    public void setUp() {
        
        this.leftProcessor = new OneSideTrim(' ', true);
        this.leftProcessorChain = new OneSideTrim(' ', true, new NextCellProcessor());
        
        this.rightProcessor = new OneSideTrim(' ', false);
        this.rightProcessorChain = new OneSideTrim(' ', false, new NextCellProcessor());
        
        this.fullCharLeftProcessor = new OneSideTrim('　', true);
        this.fullCharRightProcessor = new OneSideTrim('　', false);
        
    }
    
    @Test
    public void testConstructor_nextNull() {
        
        assertThatThrownBy(() -> new OneSideTrim(' ', true, null))
            .isInstanceOf(NullPointerException.class);
        
    }
    
    /**
     * 入力値がnullの場合
     */
    @Test
    public void testExecute_inputNull() {
        
        String input = null;
        String output = null;
        
        assertThat((Object)leftProcessor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(output);
        assertThat((Object)leftProcessorChain.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(output);
        
        assertThat((Object)rightProcessor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(output);
        assertThat((Object)rightProcessorChain.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(output);
        
    }
    
    /**
     * 入力値が空文字の場合
     */
    @Test
    public void testExecute_inputEmpty() {
        
        String input = "";
        String output = "";
        
        assertThat((Object)leftProcessor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(output);
        assertThat((Object)leftProcessorChain.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(output);
        
        assertThat((Object)rightProcessor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(output);
        assertThat((Object)rightProcessorChain.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(output);
        
    }
    
    /**
     * 入力値が全てトリム対象の文字の場合
     */
    @Test
    public void testExecute_inputTrimCharAll() {
        
        String input = "        ";
        String output = "";
        
        assertThat((Object)leftProcessor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(output);
        assertThat((Object)leftProcessorChain.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(output);
        
        assertThat((Object)rightProcessor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(output);
        assertThat((Object)rightProcessorChain.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(output);
        
    }
    
    /**
     * 入力値が全てトリム対象の文字の場合(全角の倍)
     */
    @Test
    public void testExecute_inputTrimCharAll_fullChar() {
        
        String input = "　　　　　　";
        String output = "";
        
        assertThat((Object)fullCharLeftProcessor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(output);
        assertThat((Object)fullCharRightProcessor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(output);
        
    }
    
    /**
     * 左側をトリムする場合
     */
    @Test
    public void testExecute_left() {
        
        {
            String input = "  abc e  ";
            String output = "abc e  ";
            
            assertThat((Object)leftProcessor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(output);
            assertThat((Object)leftProcessorChain.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(output);
        
        }
        
        {
            // 左側にトリム対象の文字がない場合
            String input = "abc e  ";
            String output = "abc e  ";
            
            assertThat((Object)leftProcessor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(output);
            assertThat((Object)leftProcessorChain.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(output);
        
        }
        
    }
    
    /**
     * 右側をトリムする場合
     */
    @Test
    public void testExecute_right() {
        
        {
            String input = "  abc e  ";
            String output = "  abc e";
            
            assertThat((Object)rightProcessor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(output);
            assertThat((Object)rightProcessorChain.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(output);
        
        }
        
        {
            // 右側にトリム対象の文字がない場合
            String input = "  abc e";
            String output = "  abc e";
            
            assertThat((Object)rightProcessor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(output);
            assertThat((Object)rightProcessorChain.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(output);
        
        }
        
    }
    
    /**
     * 左側をトリムする場合 - 全角文字
     */
    @Test
    public void testExecute_left_fullChar() {
        
        {
            String input = "　　abc e　　";
            String output = "abc e　　";
            
            assertThat((Object)fullCharLeftProcessor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(output);
        
        }
        
        {
            // 左側にトリム対象の文字がない場合
            String input = "abc e　　";
            String output = "abc e　　";
            
            assertThat((Object)fullCharLeftProcessor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(output);
        
        }
        
    }
    
    /**
     * 右側をトリムする場合 - 全角文字
     */
    @Test
    public void testExecute_right_fullChar() {
        
        {
            String input = "　　abc e　　";
            String output = "　　abc e";
            
            assertThat((Object)fullCharRightProcessor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(output);
        
        }
        
        {
            // 右側にトリム対象の文字がない場合
            String input = "　　abc e";
            String output = "　　abc e";
            
            assertThat((Object)fullCharRightProcessor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(output);
        
        }
        
    }
    
}

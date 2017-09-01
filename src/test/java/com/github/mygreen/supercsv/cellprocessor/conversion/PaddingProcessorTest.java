package com.github.mygreen.supercsv.cellprocessor.conversion;

import static org.junit.Assert.*;
import static org.assertj.core.api.Assertions.*;
import static com.github.mygreen.supercsv.tool.TestUtils.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

/**
 * {@link PaddingProcessor}ののテスタ
 *
 * @since 2.1
 * @author T.TSUCHIE
 *
 */
@RunWith(Enclosed.class)
public class PaddingProcessorTest {
    
    /**
     * {@link SimplePaddingProcessor}のテスタ
     *
     */
    public static class SimplePaddingProcessorTest {
        
        private SimplePaddingProcessor paddingProcessor;
        
        @Before
        public void setUp() throws Exception {
            this.paddingProcessor = new SimplePaddingProcessor();
        }
        
        @Test
        public void pad() {
            
            // サイズオーバー（切り出しなし）
            assertThat(paddingProcessor.pad("abcde", 3, '_', false, false)).isEqualTo("abcde");
            
            // サイズオーバー（切り出しあり）
            assertThat(paddingProcessor.pad("abcde", 3, '_', false, true)).isEqualTo("abc");
            
            // サイズ同じ
            assertThat(paddingProcessor.pad("abc", 3, '_', false, false)).isEqualTo("abc");
            
            // パディング対象 - 左
            assertThat(paddingProcessor.pad("abc", 5, '_', false, false)).isEqualTo("abc__");
            
            // パディング対象 - 右
            assertThat(paddingProcessor.pad("abc", 5, '_', true, false)).isEqualTo("__abc");
            
        }
        
        @Test
        public void count() {
            
            assertThat(paddingProcessor.count("abc")).isEqualTo(3);
            assertThat(paddingProcessor.count("あいう")).isEqualTo(3);
            assertThat(paddingProcessor.count("ｱｲｳ")).isEqualTo(3);
            assertThat(paddingProcessor.count("!#a")).isEqualTo(3);
            assertThat(paddingProcessor.count("𡌛")).isEqualTo(1);
            
        }
    
    }
    
    /**
     * {@link CharWidthPaddingProcessor}のテスタ
     *
     */
    public static class CharWidthPaddingProcessorTest {
        
        private CharWidthPaddingProcessor paddingProcessor;
        
        @Before
        public void setUp() throws Exception {
            this.paddingProcessor = new CharWidthPaddingProcessor();
        }
        
        /** 
         * 半角文字
         */
        @Test
        public void pad_half() {
            // サイズオーバー（切り出しなし）
            assertThat(paddingProcessor.pad("abcde", 3, '_', false, false)).isEqualTo("abcde");
            
            // サイズオーバー（切り出しあり）
            assertThat(paddingProcessor.pad("abcde", 3, '_', false, true)).isEqualTo("abc");
            
            // サイズ同じ
            assertThat(paddingProcessor.pad("abc", 3, '_', false, false)).isEqualTo("abc");
            
            // パディング対象 - 左
            assertThat(paddingProcessor.pad("abc", 5, '_', false, false)).isEqualTo("abc__");
            
            // パディング対象 - 右
            assertThat(paddingProcessor.pad("abc", 5, '_', true, false)).isEqualTo("__abc");
            
        }
        
        /** 
         * 全角文字
         */
        @Test
        public void pad_full() {
            // サイズオーバー（切り出しなし）
            assertThat(paddingProcessor.pad("あいう", 4, '_', false, false)).isEqualTo("あいう");
            
            // サイズオーバー（切り出しあり）
            assertThat(paddingProcessor.pad("あいう", 4, '_', false, true)).isEqualTo("あい");
            
            // サイズオーバー（切り出し後に、文字数が足りない場合）
            assertThat(paddingProcessor.pad("あいう", 5, '_', false, true)).isEqualTo("あい_");
            
            // サイズ同じ
            assertThat(paddingProcessor.pad("あいう", 6, '_', false, false)).isEqualTo("あいう");
            
            // パディング対象 - 左
            assertThat(paddingProcessor.pad("あい", 6, '_', false, false)).isEqualTo("あい__");
            
            // パディング対象 - 右
            assertThat(paddingProcessor.pad("あい", 6, '_', true, false)).isEqualTo("__あい");
            
            // パディング対象 - 左 - パディング文字が全角
            assertThat(paddingProcessor.pad("あい", 6, '■', false, false)).isEqualTo("あい■");
            
            // パディング対象 - 右 - パディング文字が全角
            assertThat(paddingProcessor.pad("あい", 6, '■', true, false)).isEqualTo("■あい");
            
            // パディング対象 - 左 - パディング文字が全角のため、サイズオーバー
            assertThat(paddingProcessor.pad("あい", 5, '■', false, false)).isEqualTo("あい");
            assertThat(paddingProcessor.pad("あい", 7, '■', false, false)).isEqualTo("あい■");
            
            // パディング対象 - 右 - パディング文字が全角のため、サイズオーバー
            assertThat(paddingProcessor.pad("あい", 5, '■', true, false)).isEqualTo("あい");
            assertThat(paddingProcessor.pad("あい", 7, '■', true, false)).isEqualTo("■あい");
            
        }
        
        @Test
        public void count() {
            
            assertThat(paddingProcessor.count("abc")).isEqualTo(3);
            assertThat(paddingProcessor.count("あいう")).isEqualTo(6);
            assertThat(paddingProcessor.count("ｱｲｳ")).isEqualTo(3);
            assertThat(paddingProcessor.count("!#a")).isEqualTo(3);
            assertThat(paddingProcessor.count("𡌛")).isEqualTo(2);
            
        }
    
    }
    
    /**
     * {@link ByteSizePaddingProcessor}のテスタ
     *
     */
    public static class ByteSizePaddingProcessorTest {
        
        private ByteSizePaddingProcessor paddingProcessorUtf8;
        private ByteSizePaddingProcessor paddingProcessorWindows31j;
        private ByteSizePaddingProcessor paddingProcessorEucJP;
        
        @Before
        public void setUp() throws Exception {
            this.paddingProcessorUtf8 = new ByteSizePaddingProcessor.Utf8();
            this.paddingProcessorWindows31j = new ByteSizePaddingProcessor.Windows31j();
            this.paddingProcessorEucJP = new ByteSizePaddingProcessor.EucJp();
        }
        
        @Test
        public void pad_utf8() {
            
            // サイズオーバー（切り出しなし）
            assertThat(paddingProcessorUtf8.pad("abcde", 3, '_', false, false)).isEqualTo("abcde");
            
            // サイズオーバー（切り出しあり）
            assertThat(paddingProcessorUtf8.pad("abcde", 3, '_', false, true)).isEqualTo("abc");
            
            // サイズ同じ
            assertThat(paddingProcessorUtf8.pad("abc", 3, '_', false, false)).isEqualTo("abc");
            
            // パディング対象 - 左
            assertThat(paddingProcessorUtf8.pad("abc", 5, '_', false, false)).isEqualTo("abc__");
            
            // パディング対象 - 右
            assertThat(paddingProcessorUtf8.pad("abc", 5, '_', true, false)).isEqualTo("__abc");
            
        }
        
        @Test
        public void count_UTF8() {
            
            assertThat(paddingProcessorUtf8.count("abc")).isEqualTo(3);
            assertThat(paddingProcessorUtf8.count("あいう")).isEqualTo(9);
            assertThat(paddingProcessorUtf8.count("ｱｲｳ")).isEqualTo(9);
            assertThat(paddingProcessorUtf8.count("!#a")).isEqualTo(3);
            assertThat(paddingProcessorUtf8.count("𡌛")).isEqualTo(4);
            
            
        }
        
        @Test
        public void count_Windows31j() {
            
            assertThat(paddingProcessorWindows31j.count("abc")).isEqualTo(3);
            assertThat(paddingProcessorWindows31j.count("あいう")).isEqualTo(6);
            assertThat(paddingProcessorWindows31j.count("ｱｲｳ")).isEqualTo(3);
            assertThat(paddingProcessorWindows31j.count("!#a")).isEqualTo(3);
            assertThat(paddingProcessorWindows31j.count("𡌛")).isEqualTo(1);    // 文字化けする
            
        }
        
        @Test
        public void count_EUC_JP() {
            
            assertThat(paddingProcessorEucJP.count("abc")).isEqualTo(3);
            assertThat(paddingProcessorEucJP.count("あいう")).isEqualTo(6);
            assertThat(paddingProcessorEucJP.count("ｱｲｳ")).isEqualTo(6);
            assertThat(paddingProcessorEucJP.count("!#a")).isEqualTo(3);
            assertThat(paddingProcessorEucJP.count("𡌛")).isEqualTo(1);    // 文字化けする
            
        }
    
    }
    
}

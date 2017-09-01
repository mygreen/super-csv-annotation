package com.github.mygreen.supercsv.cellprocessor.conversion;

import static com.github.mygreen.supercsv.tool.TestUtils.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.Before;
import org.junit.Test;
import org.supercsv.cellprocessor.ift.CellProcessor;

/**
 * {@link MultiPad}のテスタ
 *
 * @since 2.1
 * @author T.TSUCHIE
 *
 */
public class MultiPadTest {

    private CellProcessor leftProcessor;
    private CellProcessor leftProcessorChain;

    private CellProcessor rightProcessor;
    private CellProcessor rightProcessorChain;

    private CellProcessor choppedLeftProcessor;
    private CellProcessor choppedRightProcessor;

    private CellProcessor chopped1LeftProcessor;
    private CellProcessor chopped1RightProcessor;

    private CellProcessor widthCounterLeftProcessor;
    private CellProcessor widthCounterRightProcessor;

    private CellProcessor byteCounterLeftProcessor;
    private CellProcessor byteCounterRightProcessor;

    @Before
    public void setUp() throws Exception {
        this.leftProcessor = new MultiPad(5, ' ', false, false, new SimplePaddingProcessor());
        this.leftProcessorChain = new MultiPad(5, ' ', false, false, new SimplePaddingProcessor());

        this.rightProcessor = new MultiPad(5, ' ', true, false, new SimplePaddingProcessor());
        this.rightProcessorChain = new MultiPad(5, ' ', true, false, new SimplePaddingProcessor());

        this.choppedLeftProcessor = new MultiPad(5, ' ', false, true, new SimplePaddingProcessor());
        this.choppedRightProcessor = new MultiPad(5, ' ', true, true, new SimplePaddingProcessor());

        this.chopped1LeftProcessor = new MultiPad(1, ' ', false, true, new SimplePaddingProcessor());
        this.chopped1RightProcessor = new MultiPad(1, ' ', true, true, new SimplePaddingProcessor());

        this.widthCounterLeftProcessor = new MultiPad(5, ' ', false, true, new CharWidthPaddingProcessor());
        this.widthCounterRightProcessor = new MultiPad(5, ' ', true, true, new CharWidthPaddingProcessor());

        this.byteCounterLeftProcessor = new MultiPad(5, ' ', false, true, new ByteSizePaddingProcessor.Utf8());
        this.byteCounterRightProcessor = new MultiPad(5, ' ', true, true, new ByteSizePaddingProcessor.Utf8());

    }

    /**
     * コンストラクタのテスト - size = 0の場合
     */
    @Test
    public void testConstructor_sizeZero() {

        assertThatThrownBy(() -> new MultiPad(0, ' ', true, false, new SimplePaddingProcessor()))
            .isInstanceOf(IllegalArgumentException.class);

    }

    /**
     * コンストラクタのテスト - size = -1の場合
     */
    @Test
    public void testConstructor_sizeMinus() {

        assertThatThrownBy(() -> new MultiPad(-1, ' ', true, false, new SimplePaddingProcessor()))
            .isInstanceOf(IllegalArgumentException.class);

    }

    /**
     * コンストラクタのテスト - paddingProcessor == nullの場合
     */
    @Test
    public void testConstructor_paddingProcessorNull() {

        assertThatThrownBy(() -> new MultiPad(2, ' ', true, false, null))
            .isInstanceOf(IllegalArgumentException.class);

    }

    /**
     * コンストラクタのテスト - next == nullの場合
     */
    @Test
    public void testConstructor_nextNull() {

        assertThatThrownBy(() -> new MultiPad(0, ' ', true, false, new SimplePaddingProcessor(), null))
            .isInstanceOf(NullPointerException.class);

    }

    @Test
    public void textExecute_inputNull() {

        String input = null;
        String output = "     ";

        assertThat((Object)leftProcessor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(output);
        assertThat((Object)leftProcessorChain.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(output);

        assertThat((Object)rightProcessor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(output);
        assertThat((Object)rightProcessorChain.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(output);

    }

    @Test
    public void textExecute_inputEmpty() {

        String input = "";
        String output = "     ";

        assertThat((Object)leftProcessor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(output);
        assertThat((Object)leftProcessorChain.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(output);

        assertThat((Object)rightProcessor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(output);
        assertThat((Object)rightProcessorChain.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(output);

    }

    /**
     * 入力文字がlengthと同じ場合 - 左詰
     */
    @Test
    public void testExecute_inputSameLength_left() {

        String input = "aあいbう";
        String output = "aあいbう";

        assertThat((Object)leftProcessor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(output);
        assertThat((Object)leftProcessorChain.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(output);

    }

    /**
     * 入力文字がlengthよりも大きい場合 - 左詰
     */
    @Test
    public void testExecute_inputGreaerThanLength_left() {

        String input = "aあいbcう";
        String output = "aあいbcう";

        assertThat((Object)leftProcessor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(output);
        assertThat((Object)leftProcessorChain.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(output);

    }

    /**
     * 入力文字がlengthよりも大きい場合 - 左詰 - 切り出しを行う場合
     */
    @Test
    public void testExecute_inputGreaerThanLength_left_chopped() {

        {
            String input = "aあいbcう";
            String output = "aあいbc";

            assertThat((Object)choppedLeftProcessor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(output);

        }

        {
            String input = "aあいbcう";
            String output = "a";

            assertThat((Object)chopped1LeftProcessor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(output);

        }

    }

    /**
     * 入力文字がlengthよりも大きい場合 - 左詰 - 切り出しを行う場合 - すべての文字を1文字として扱う
     */
    @Test
    public void testExecute_inputGreaerThanLength_left_defaultCounter() {

        // 切り詰め文字が半角
        {
            String input = "aあｶいbｳ";
            String output = "aあｶいb";

            assertThat((Object)choppedLeftProcessor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(output);

        }

        // 切り詰め文字が全角
        {
            String input = "aあｶいbう";
            String output = "aあｶいb";

            assertThat((Object)choppedLeftProcessor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(output);

        }

        // 切り詰め文字がサロゲートペア
        // 4バイト文字 - 𠀋𡌛
        {
            String input = "a𠀋b𠮷う𡌛";
            String output = "a𠀋b𠮷う";

            assertThat((Object)choppedLeftProcessor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(output);
        }

    }

    /**
     * 入力文字がlengthよりも大きい場合 - 左詰 - 切り出しを行う場合 - 全角を2文字としてカウント
     */
    @Test
    public void testExecute_inputGreaerThanLength_left_widthCounter() {

        // 切り詰め文字が半角
        {
            String input = "aあbcｶ";
            String output = "aあbc";

            assertThat((Object)widthCounterLeftProcessor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(output);

        }

        // 切り詰め文字が全角
        {
            String input = "aあいう";
            String output = "aあい";

            assertThat((Object)widthCounterLeftProcessor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(output);

        }

        // 切り詰め文字が全角 - 切り詰め後にさらにパディング
        {
            String input = "aあbい";
            String output = "aあb ";

            assertThat((Object)widthCounterLeftProcessor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(output);

        }

        // 切り詰め文字がサロゲートペア
        // 4バイト文字 - 𠀋𡌛
        {
            String input = "aあ𡌛𠀋";
            String output = "aあ𡌛";

            assertThat((Object)widthCounterLeftProcessor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(output);
        }

    }

    /**
     * 入力文字がlengthよりも大きい場合 - 左詰 - 切り出しを行う場合 - バイト数でカウント
     */
    @Test
    public void testExecute_inputGreaerThanLength_left_byteCounter() {

        // 切り詰め文字が1byte
        {
            String input = "aあbい";
            String output = "aあb";

            assertThat((Object)byteCounterLeftProcessor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(output);

        }

        // 切り詰め文字が2byte
        {
            String input = "aあbい";
            String output = "aあb";

            assertThat((Object)byteCounterLeftProcessor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(output);

        }

        // 切り詰め文字が2byte - 切り詰め後さらにパディング
        {
            String input = "abcう";
            String output = "abc  ";

            assertThat((Object)byteCounterLeftProcessor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(output);

        }

        // 切り詰め文字が3byte - 切り詰め後にさらにパディング
        {
            String input = "ab𠀋";
            String output = "ab   ";

            assertThat((Object)byteCounterLeftProcessor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(output);

        }

        // 4バイト文字 - 𠀋𡌛
        {
            String input = "a𠀋b";
            String output = "a𠀋";

            assertThat((Object)byteCounterLeftProcessor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(output);

        }

    }

    /**
     * 入力文字がlengthよりも小さい場合 - 左詰
     */
    @Test
    public void testExecute_inputLessThanLength_left() {

        String input = "aあ";
        String output = "aあ   ";

        assertThat((Object)leftProcessor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(output);
        assertThat((Object)leftProcessorChain.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(output);

    }

    /**
     * 入力文字がlengthと同じ場合 - 右詰
     */
    @Test
    public void testExecute_inputSameLength_right() {

        String input = "aあいbう";
        String output = "aあいbう";

        assertThat((Object)rightProcessor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(output);
        assertThat((Object)rightProcessorChain.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(output);

    }

    /**
     * 入力文字がlengthよりも大きい場合 - 右詰
     */
    @Test
    public void testExecute_inputGreaerThanLength_right() {

        String input = "aあいbcう";
        String output = "aあいbcう";

        assertThat((Object)rightProcessor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(output);
        assertThat((Object)rightProcessorChain.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(output);

    }

    /**
     * 入力文字がlengthよりも大きい場合 - 右詰 - 切り出しを行う場合
     */
    @Test
    public void testExecute_inputGreaerThanLength_right_chopped() {

        {
            String input = "aあいbcう";
            String output = "あいbcう";

            assertThat((Object)choppedRightProcessor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(output);

        }

        {
            String input = "aあいbcう";
            String output = "う";

            assertThat((Object)chopped1RightProcessor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(output);

        }
    }

    /**
     * 入力文字がlengthよりも小さい場合 - 右詰
     */
    @Test
    public void testExecute_inputLessThanLength_right() {

        String input = "aあ";
        String output = "   aあ";

        assertThat((Object)rightProcessor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(output);
        assertThat((Object)rightProcessorChain.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(output);

    }

    /**
     * 入力文字がlengthよりも大きい場合 - 右詰 - 切り出しを行う場合 - すべての文字を1文字として扱う
     */
    @Test
    public void testExecute_inputGreaerThanLength_right_defaultCounter() {

        // 切り詰め文字が半角
        {
            String input = "ｳaあｶいb";
            String output = "aあｶいb";

            assertThat((Object)choppedRightProcessor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(output);

        }

        // 切り詰め文字が全角
        {
            String input = "aあbいcう";
            String output = "あbいcう";

            assertThat((Object)choppedRightProcessor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(output);

        }

        // 切り詰め文字がサロゲートペア
        // 4バイト文字 - 𠀋𡌛
        {
            String input = "𡌛a𠀋b𠮷う";
            String output = "a𠀋b𠮷う";

            assertThat((Object)choppedRightProcessor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(output);
        }

    }

    /**
     * 入力文字がlengthよりも大きい場合 - 右詰 - 切り出しを行う場合 - 全角を2文字としてカウント
     */
    @Test
    public void testExecute_inputGreaerThanLength_right_widthCounter() {

        // 切り詰め文字が半角
        {
            String input = "ｶaあbc";
            String output = "aあbc";

            assertThat((Object)widthCounterRightProcessor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(output);

        }

        // 切り詰め文字が全角
        {
            String input = "うaあい";
            String output = "aあい";

            assertThat((Object)widthCounterRightProcessor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(output);

        }

        // 切り詰め文字が全角 - 切り詰め後にさらにパディング
        {
            String input = "いaあb";
            String output = " aあb";

            assertThat((Object)widthCounterRightProcessor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(output);

        }

        // 切り詰め文字がサロゲートペア
        // 4バイト文字 - 𠀋𡌛
        {
            String input = "𠀋aあ𡌛";
            String output = "aあ𡌛";

            assertThat((Object)widthCounterRightProcessor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(output);
        }
    }

    /**
     * 入力文字がlengthよりも大きい場合 - 右詰 - 切り出しを行う場合 - バイト数でカウント
     */
    @Test
    public void testExecute_inputGreaerThanLength_right_byteCounter() throws Exception {

        // 切り詰め文字が半角
        {
            String input = "1aあc";
            String output = "aあc";
            assertThat((Object)byteCounterRightProcessor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(output);

        }

        // 切り詰め文字が全角
        {
            String input = "いaあb";
            String output = "aあb";

            assertThat((Object)byteCounterRightProcessor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(output);

        }

        // 切り詰め文字が4byte - 切り詰め後にさらにパディング
        // 4バイト文字 - 𠀋𡌛
        {
            String input = "𠀋ab";
            String output = "   ab";

            assertThat((Object)byteCounterRightProcessor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(output);

        }

        // 4バイト文字 - 𠀋𡌛
        {
            String input = "a𠀋b𡌛";
            String output = "b𡌛";

            assertThat((Object)byteCounterRightProcessor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(output);

        }

    }

}

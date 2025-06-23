package com.github.mygreen.supercsv.builder;

import com.github.mygreen.supercsv.annotation.conversion.CsvFixedSize;
import com.github.mygreen.supercsv.cellprocessor.conversion.PaddingProcessor;
import com.github.mygreen.supercsv.cellprocessor.conversion.SimplePaddingProcessor;

/**
 * 固定長のカラムのアノテーション{@link CsvFixedSize} を元に作成した情報。
 *
 * @since 2.5
 * @author T.TSUCHIE
 *
 */
public class FixedSizeColumnProperty {

    private static final PaddingProcessor DEFAULT_PADDING_PROCESSOR = new SimplePaddingProcessor();

    /**
     * 固定長カラムのサイズを指定します。
     * <p>値は1以上を指定する必要があります。</p>
     * <p>サイズは考え型によってバイト数、文字幅など異なるため、
     *    属性{@link #paddingProcessor()}によって、変更することができます。
     * </p>
     *
     * @return 固定長カラムのサイズ。
     */
    private final int size;

    /**
     * パディングする際の文字を指定します。
     * <br>読み込み時には、トリム対象の文字となります。
     *
     */
    private char padChar = ' ';

    /**
     * パディング時に指定したカラムの長さを超えた場合、切り出すかどうか指定します。
     * <p>trueの場合、指定したカラムの長さを超えた場合切り出します。
     * <p>falseの場合、指定したカラムの長さを超えた場合何もしません。
     *
     */
    private boolean chopped = false;

    /**
     * パディングの処理方法を指定します。
     */
    private PaddingProcessor paddingProcessor = DEFAULT_PADDING_PROCESSOR;

    /**
     * 右寄せをするかどうか指定します。
     * <br>読み込み時のトリム時は、逆の意味になるので注意。
     * <br>falseの場合は、左詰めです。
     */
    private boolean rightAlign = false;

    /**
     * 固定長カラムのサイズを指定するコンストラクタ。
     * 
     * @param size 固定長カラムのサイズ。1以上を指定する必要があります。
     * @throws IllegalArgumentException sizeが0以下の時にスローされます。
     */
    public FixedSizeColumnProperty(int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("size shoud be greater than or equals 1 (>=1).");
        }
        this.size = size;
    }

    public int getSize() {
        return size;
    }

    public char getPadChar() {
        return padChar;
    }

    public void setPadChar(char padChar) {
        this.padChar = padChar;
    }

    public boolean isChopped() {
        return chopped;
    }

    public void setChopped(boolean chopped) {
        this.chopped = chopped;
    }

    public PaddingProcessor getPaddingProcessor() {
        return paddingProcessor;
    }

    public void setPaddingProcessor(PaddingProcessor paddingProcessor) {
        this.paddingProcessor = paddingProcessor;
    }

    public boolean isRightAlign() {
        return rightAlign;
    }

    public void setRightAlign(boolean rightAlign) {
        this.rightAlign = rightAlign;
    }
}

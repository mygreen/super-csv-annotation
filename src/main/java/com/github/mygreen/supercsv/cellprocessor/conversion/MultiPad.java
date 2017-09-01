package com.github.mygreen.supercsv.cellprocessor.conversion;

import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;
import org.supercsv.util.CsvContext;

/**
 * 固定長にそろえる{@link CellProcessor}。
 *
 * @since 2.1
 * @author T.TSUCHIE
 *
 */
public class MultiPad extends CellProcessorAdaptor implements StringCellProcessor {

    /**
     * パディングのサイズ
     */
    private final int size;

    /**
     * パディング文字
     */
    private final char padChar;

    /**
     * 右詰めかどうか。
     */
    private final boolean rightAlign;

    /**
     * 固定長を超える場合に切り出しをするかどうか。
     */
    private final boolean chopped;

    /**
     * パディング処理の実装
     */
    private final PaddingProcessor paddingProcessor;

    /**
     * コンストラクタ
     *
     * @param size パディングのサイズ
     * @param padChar パディングする文字。
     * @param rightAlign 右詰めするかどうか。
     * @param chopped 処理対象の文字が固定長を超えている場合に、切り出すかどうか。
     * @param paddingProcessor パディング処理の実装
     * @throws IllegalArgumentException {@literal length <= 0.}
     */
    public MultiPad(final int size, final char padChar, final boolean rightAlign, final boolean chopped,
            final PaddingProcessor paddingProcessor) {
        super();

        checkPreconditions(size, paddingProcessor);

        this.size = size;
        this.padChar = padChar;
        this.rightAlign = rightAlign;
        this.chopped = chopped;
        this.paddingProcessor = paddingProcessor;
    }

    /**
     * コンストラクタ
     *
     * @param size パディングのサイズ
     * @param padChar パディングする文字。
     * @param rightAlign 右詰めするかどうか。
     * @param chopped 処理対象の文字が固定長を超えている場合に、切り出すかどうか。
     * @param paddingProcessor パディング処理の実装
     * @param next チェインの中で呼ばれる次の{@link CellProcessor}.
     * @throws IllegalArgumentException {@literal length <= 0.}
     */
    public MultiPad(final int size, final char padChar, final boolean rightAlign, final boolean chopped,
            final PaddingProcessor paddingProcessor, final StringCellProcessor next) {
        super(next);

        checkPreconditions(size, paddingProcessor);

        this.size = size;
        this.padChar = padChar;
        this.rightAlign = rightAlign;
        this.chopped = chopped;
        this.paddingProcessor = paddingProcessor;
    }

    private static void checkPreconditions(final int size, final PaddingProcessor paddingProcessor) {

        if(size <= 0) {
            throw new IllegalArgumentException(String.format("size should be > 0, but was %d.", size));
        }

        if(paddingProcessor == null) {
            throw new IllegalArgumentException("paddingProcessor should not be null.");
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T execute(final Object value, final CsvContext context) {

        final String result = paddingProcessor.pad(value == null ? "" : (String) value,
                size, padChar, rightAlign, chopped);
        return next.execute(result, context);

    }

    /**
     * パディングのサイズを取得する。
     * @return パディングのサイズ
     */
    public int getSize() {
        return size;
    }

    /**
     * パディング文字を取得する。
     * @return パディング文字
     */
    public char getPadChar() {
        return padChar;
    }

    /**
     * 右詰めかどうかを取得する。
     * @return 右詰めかどうか。
     */
    public boolean isRightAlign() {
        return rightAlign;
    }

    /**
     * 固定長を超える場合に切り出しをするかどうか取得する。
     * @return 固定長を超える場合に切り出しをするかどうか。
     */
    public boolean isChopped() {
        return chopped;
    }

    /**
     * パディング処理の実装を取得する。
     * @return パディング処理の実装
     */
    public PaddingProcessor getPaddingProcessor() {
        return paddingProcessor;
    }

}

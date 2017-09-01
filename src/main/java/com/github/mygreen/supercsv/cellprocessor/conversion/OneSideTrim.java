package com.github.mygreen.supercsv.cellprocessor.conversion;

import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;
import org.supercsv.util.CsvContext;

/**
 * 片方だけトリムするCellProcessor。
 *
 * @since 2.1
 * @author T.TSUCHIE
 *
 */
public class OneSideTrim extends CellProcessorAdaptor implements StringCellProcessor {

    /**
     * トリム対象の文字
     */
    private final char trimChar;

    /**
     * 左側をトリムするかどうか。
     * ・トリム対象の文字がある側を指定します。
     */
    private final boolean leftAlign;

    /**
     * コンストラクタ
     *
     * @param trimChar トリム対象の文字
     * @param leftAlign 左側をトリムするかどうか。
     */
    public OneSideTrim(final char trimChar, final boolean leftAlign) {
        super();
        this.trimChar = trimChar;
        this.leftAlign = leftAlign;
    }

    /**
     * コンストラクタ
     *
     * @param trimChar トリム対象の文字
     * @param leftAlign 左側をトリムするかどうか。
     * @param next チェインの中で呼ばれる次の{@link CellProcessor}.
     */
    public OneSideTrim(final char trimChar, final boolean leftAlign, final StringCellProcessor next) {
        super(next);
        this.trimChar = trimChar;
        this.leftAlign = leftAlign;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T execute(final Object value, final CsvContext context) {
        
        if(value == null) {
            return next.execute(value,  context);
        }
        
        final String result = trim(value.toString());
        return next.execute(result, context);
    }

    /**
     * 文字をトリミングする
     * @param str トリミング対象の文字
     * @return トリムした結果
     */
    private String trim(final String str) {

        final int length = str.length();
        if(length == 0) {
            return str;
        }

        if(leftAlign) {
            if(str.charAt(0) != trimChar) {
                // 左端がトリム対象の文字出ない場合、処理終了
                return str;

            }

            // 左側から、trimCharに一致しない位置を探す
            for(int i=0; i < length; i++) {
                char c = str.charAt(i);
                if(c != trimChar) {
                    return str.substring(i);
                }
            }

            // 全ての文字がトリム対象の場合
            return "";

        } else {
            if(str.charAt(length - 1) != trimChar) {
                // 右端がトリム対象の文字出ない場合、処理終了
                return str;

            }

            // 右側から、trimCharに一致しない位置を探す
            for(int i=length - 1; i >= 0; i--) {
                char c = str.charAt(i);
                if(c != trimChar) {
                    return str.substring(0, i + 1);
                }
            }

            // 全ての文字がトリム対象の場合
            return "";

        }

    }

    /**
     * トリム対象の文字を取得する
     * @return トリム対象の文字
     */
    public char getTrimChar() {
        return trimChar;
    }

    /**
     * 左側をトリムするかどうかを取得する。
     * @return 左側をトリムするかどうか
     */
    public boolean isLeftAlign() {
        return leftAlign;
    }



}

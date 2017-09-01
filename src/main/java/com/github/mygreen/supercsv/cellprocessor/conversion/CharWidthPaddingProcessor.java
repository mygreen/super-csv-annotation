package com.github.mygreen.supercsv.cellprocessor.conversion;

import com.github.mygreen.supercsv.util.ArgUtils;

/**
 * 文字の幅によって区別してパディングする。
 * <p>半角は長さ1、全角は長さ2としてカウントして処理します。</p>
 * <p>サロゲートペアは長さ2としてカウントします。</p>
 *
 * @since 2.1
 * @author T.TSUCHIE
 *
 */
public class CharWidthPaddingProcessor extends AbstractPaddingOperator {

    @Override
    public int count(int codePoint) {
        if(Character.charCount(codePoint) >= 2) {
            // サロゲートペアの文字＝全角として2文字としてカウントする。
            return 2;
        }

        final char c = (char)codePoint;
        if(c <= '\u007e'    // 英数字
                || c == '\u00a5'    // \記号
                || c == '\u203e'    // ~記号
                || (c >= '\uff61' && c <= '\uff9f') // 半角カナ
                ) {
            return 1;
        } else {
            return 2;
        }
    }

    @Override
    public int count(final String text) {

        ArgUtils.notNull(text, "text");

        int count=0;

        final int length = text.length();
        for(int i=0, codePoint=0; i < length; i+=Character.charCount(codePoint)) {
            codePoint = text.codePointAt(i);
            count += count(codePoint);
        }

        return count;
    }


}

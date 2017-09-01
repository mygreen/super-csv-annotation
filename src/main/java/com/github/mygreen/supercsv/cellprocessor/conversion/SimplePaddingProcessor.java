package com.github.mygreen.supercsv.cellprocessor.conversion;

import com.github.mygreen.supercsv.util.ArgUtils;

/**
 * 文字の種別にかかわらず１文字としてカウントしてパディング処理する。
 *
 * @since 2.1
 * @author T.TSUCHIE
 *
 */
public class SimplePaddingProcessor extends AbstractPaddingOperator {
    
    @Override
    public int count(int codePoint) {
        return 1;
    }
    
    @Override
    public int count(final String text) {
        ArgUtils.notNull(text, "text");
        return text.codePointCount(0, text.length());
    }
    
}

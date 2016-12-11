package com.github.mygreen.supercsv.cellprocessor.conversion;

import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;
import org.supercsv.util.CsvContext;

/**
 * 左側にパディングする。
 * 
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class LeftPad extends CellProcessorAdaptor implements StringCellProcessor {
    
    private final int padSize;
    
    private final char padChar;
    
    public LeftPad(final int padSize, final char padChar) {
        super();
        
        checkPreconditions(padSize);
        this.padSize = padSize;
        this.padChar = padChar;
    }
    
    public LeftPad(final int padSize, final char padChar, final StringCellProcessor next) {
        super(next);
        
        checkPreconditions(padSize);
        this.padSize = padSize;
        this.padChar = padChar;
    }
    
    private static void checkPreconditions(final int padSize) {
        if(padSize <= 0) {
            throw new IllegalArgumentException(String.format("padSize should be > 0 but was %d", padSize));
        }
    }
    
    @Override
    public <T> T execute(final Object value, final CsvContext context) {
        
        if(value == null) {
            return next.execute(value, context);
        }
        
        final String result = padding((String)value);
        
        return next.execute(result, context);
    }
    
    private String padding(final String str) {
        
        final int pads = padSize - str.length();
        if(pads <= 0) {
            return str;
        }
        
        final StringBuilder sb = new StringBuilder(str.length() + pads);
        
        for(int i=0; i < pads; i++) {
            sb.append(padChar);
        }
        
        sb.append(str);
        
        return sb.toString();
        
    }
    
    /**
     * パディングするサイズを取得します。
     * @return パディングサイズ。
     */
    public int getPadSize() {
        return padSize;
    }
    
    /**
     * パディングする文字を取得します。
     * @return パディング文字。
     */
    public char getPadChar() {
        return padChar;
    }
}

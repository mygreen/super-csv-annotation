package com.github.mygreen.supercsv.cellprocessor.conversion;

import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;
import org.supercsv.util.CsvContext;

/**
 * 文字列を切り詰めるCellProcessor
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class Truncate extends CellProcessorAdaptor implements StringCellProcessor{
    
    private final int maxSize;
    private final String suffix;
    
    public Truncate(final int maxSize, final String suffix) {
        super();
        checkPreconditions(maxSize, suffix);
        this.maxSize = maxSize;
        this.suffix = suffix;
    }
    
    public Truncate(final int maxSize, final String suffix, final StringCellProcessor next) {
        super(next);
        checkPreconditions(maxSize, suffix);
        this.maxSize = maxSize;
        this.suffix = suffix;
    }
    
    private static void checkPreconditions(final int maxSize, final String suffix) {
        if( maxSize <= 0 ) {
            throw new IllegalArgumentException(String.format("maxSize should be > 0 but was %d", maxSize));
        }
        
        if( suffix == null ) {
            throw new NullPointerException("suffix should not be null");
        }
    }
    
    @Override
    public <T> T execute(final Object value, final CsvContext context) {
        if(value == null) {
            return next.execute(value, context);
        }
        
        final String stringValue = value.toString();
        final String result;
        if(stringValue.length() <= maxSize) {
            result = stringValue;
        } else {
            result = stringValue.substring(0, maxSize) + suffix;
        }
        
        return next.execute(result, context);
        
    }
    
    /**
     * 最大文字長を取得する。
     * @return 最大文字長
     */
    public int getMaxSize() {
        return maxSize;
    }
    
    /**
     * 接尾語を取得する
     * @return 接尾語
     */
    public String getSuffix() {
        return suffix;
    }
    
}

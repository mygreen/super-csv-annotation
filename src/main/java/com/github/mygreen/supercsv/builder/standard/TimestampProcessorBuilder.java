package com.github.mygreen.supercsv.builder.standard;

import java.sql.Timestamp;

/**
 *
 * @version 2.0
 * @author T.TSUCHIE
 *
 */
public class TimestampProcessorBuilder extends AbstractDateProcessorBuilder<Timestamp> {
    
    /**
     * 標準の書式を取得する。
     * @return 常に、'{@literal yyyy-MM-dd HH:mm:ss.SSS}' を返す。
     */
    @Override
    protected String getDefaultPattern() {
        return "yyyy-MM-dd HH:mm:ss.SSS";
    }
    
}

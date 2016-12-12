package com.github.mygreen.supercsv.builder.standard;

import java.sql.Time;

/**
 *
 * @version 2.0
 * @author T.TSUCHIE
 *
 */
public class TimeProcessorBuilder extends AbstractDateProcessorBuilder<Time> {
    
    /**
     * 標準の書式を取得する。
     * @return 常に、'{@literal HH:mm:ss}' を返す。
     */
    @Override
    protected String getDefaultPattern() {
        return "HH:mm:ss";
    }
    
}

package com.github.mygreen.supercsv.builder.time;

import java.time.LocalTime;

/**
 *
 * @version 2.0
 * @since 1.2
 * @author T.TSUCHIE
 *
 */
public class LocalTimeProcessorBuilder extends AbstractTemporalProcessorBuilder<LocalTime> {
    
    /**
     * {@literal HH:mm:ss}を返す。
     */
    @Override
    protected String getDefaultPattern() {
        return "HH:mm:ss";
    }
    
}

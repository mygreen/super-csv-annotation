package com.github.mygreen.supercsv.builder.time;

import java.time.OffsetTime;

/**
 * {@link OffsetTime}に対するビルダ。
 * 
 * @since 2.1
 * @author T.TSUCHIE
 *
 */
public class OffsetTimeProcessorBuilder extends AbstractTemporalProcessorBuilder<OffsetTime> {
    
    /**
     * {@literal HH:mm:ssxxx}を返す。
     */
    @Override
    protected String getDefaultPattern() {
        return "HH:mm:ssxxx";
    }
    
}

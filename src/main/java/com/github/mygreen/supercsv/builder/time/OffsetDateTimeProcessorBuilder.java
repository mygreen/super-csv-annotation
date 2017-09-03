package com.github.mygreen.supercsv.builder.time;

import java.time.OffsetDateTime;

/**
 * {@link OffsetDateTime}に対するビルダ。
 * 
 * @since 2.1
 * @author T.TSUCHIE
 *
 */
public class OffsetDateTimeProcessorBuilder extends AbstractTemporalProcessorBuilder<OffsetDateTime> {
    
    /**
     * {@literal uuuu-MM-dd HH:mm:ssxxx}を返す。
     */
    @Override
    protected String getDefaultPattern() {
        return "uuuu-MM-dd HH:mm:ssxxx";
    }
    
}

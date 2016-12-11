package com.github.mygreen.supercsv.builder.time;

import java.time.LocalDateTime;

/**
 * {@link LocalDateTime}に対するビルダ。
 * 
 * @version 2.0
 * @since 1.2
 * @author T.TSUCHIE
 *
 */
public class LocalDateTimeProcessorBuilder extends AbstractTemporalProcessorBuilder<LocalDateTime> {
    
    /**
     * {@literal uuuu-MM-dd HH:mm:ss}を返す。
     */
    @Override
    protected String getDefaultPattern() {
        return "uuuu-MM-dd HH:mm:ss";
    }
    
}

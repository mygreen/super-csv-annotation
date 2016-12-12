package com.github.mygreen.supercsv.builder.time;

import java.time.ZonedDateTime;

/**
 * {@link ZonedDateTime}のビルダクラス
 * 
 * @version 2.0
 * @since 1.2
 * @author T.TSUCHIE
 *
 */
public class ZonedDateTimeProcessorBuilder extends AbstractTemporalProcessorBuilder<ZonedDateTime> {
    
    /**
     * {@literal uuuu-MM-dd HH:mm:ssxxx'['VV']'}を返す。
     */
    @Override
    protected String getDefaultPattern() {
        return "uuuu-MM-dd HH:mm:ssxxx'['VV']'";
    }
    
}

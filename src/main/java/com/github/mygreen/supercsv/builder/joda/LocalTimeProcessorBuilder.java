package com.github.mygreen.supercsv.builder.joda;

import org.joda.time.LocalTime;

import com.github.mygreen.supercsv.builder.ProcessorBuilder;

/**
 * {@link LocalTime}に対する{@link ProcessorBuilder}。
 * @version 2.0
 * @since 1.2
 * @author T.TSUCHIE
 *
 */
public class LocalTimeProcessorBuilder extends AbstractJodaProcessorBuilder<LocalTime> {
    
    /**
     * {@literal HH:mm:ss}を返す。
     */
    @Override
    protected String getDefaultPattern() {
        return "HH:mm:ss";
    }
    
}

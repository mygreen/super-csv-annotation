package com.github.mygreen.supercsv.builder.joda;

import org.joda.time.LocalDateTime;

import com.github.mygreen.supercsv.builder.ProcessorBuilder;

/**
 * {@link LocalDateTime}に対する{@link ProcessorBuilder}。
 * @version 2.0
 * @since 1.2
 * @author T.TSUCHIE
 *
 */
public class LocalDateTimeProcessorBuilder extends AbstractJodaProcessorBuilder<LocalDateTime> {
    
    /**
     * {@literal yyyy-MM-dd HH:mm:ss}を返す。
     */
    @Override
    protected String getDefaultPattern() {
        return "yyyy-MM-dd HH:mm:ss";
    }
    
}

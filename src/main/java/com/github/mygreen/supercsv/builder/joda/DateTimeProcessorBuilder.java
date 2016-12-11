package com.github.mygreen.supercsv.builder.joda;

import org.joda.time.DateTime;

import com.github.mygreen.supercsv.builder.ProcessorBuilder;

/**
 * {@link DateTime}に対する{@link ProcessorBuilder}。
 * @version 2.0
 * @since 1.2
 * @author T.TSUCHIE
 *
 */
public class DateTimeProcessorBuilder extends AbstractJodaProcessorBuilder<DateTime> {
    
    /**
     * {@literal yyyy-MM-dd HH:mm:ssZZ}を返す。
     */
    @Override
    protected String getDefaultPattern() {
        return "yyyy-MM-dd HH:mm:ssZZ";
    }
    
}

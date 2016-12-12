package com.github.mygreen.supercsv.builder.joda;

import org.joda.time.LocalDate;

/**
 * {@link LocalDate}に対するAutoBuilder。
 * @version 2.0
 * @since 1.2
 * @author T.TSUCHIE
 *
 */
public class LocalDateProcessorBuilder extends AbstractJodaProcessorBuilder<LocalDate> {
    
    /**
     * {@literal yyyy-MM-dd}を返す。
     */
    @Override
    protected String getDefaultPattern() {
        return "yyyy-MM-dd";
    }
    
}

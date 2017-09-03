package com.github.mygreen.supercsv.builder.joda;

import org.joda.time.MonthDay;

/**
 * {@link MonthDay}に対するAutoBuilder。
 * @since 2.1
 * @author T.TSUCHIE
 *
 */
public class MonthDayProcessorBuilder extends AbstractJodaProcessorBuilder<MonthDay> {
    
    /**
     * {@literal MM-dd}を返す。
     */
    @Override
    protected String getDefaultPattern() {
        return "MM-dd";
    }
    
}

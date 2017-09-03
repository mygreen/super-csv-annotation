package com.github.mygreen.supercsv.builder.joda;

import org.joda.time.YearMonth;

/**
 * {@link YearMonth}に対するAutoBuilder。
 * @since 2.1
 * @author T.TSUCHIE
 *
 */
public class YearMonthProcessorBuilder extends AbstractJodaProcessorBuilder<YearMonth> {
    
    /**
     * {@literal yyyy-MM}を返す。
     */
    @Override
    protected String getDefaultPattern() {
        return "yyyy-MM";
    }
    
}

package com.github.mygreen.supercsv.builder.time;

import java.time.MonthDay;

/**
 * {@link MonthDay}型のビルダクラス。
 *
 * @since 2.1
 * @author T.TSUCHIE
 *
 */
public class MonthDayProcessorBuilder extends AbstractTemporalProcessorBuilder<MonthDay> {

    /**
     * {@literal MM-dd}を返す。
     */
    @Override
    protected String getDefaultPattern() {
        return "MM-dd";
    }
    
}

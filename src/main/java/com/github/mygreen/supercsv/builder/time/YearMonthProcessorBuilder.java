package com.github.mygreen.supercsv.builder.time;

import java.time.YearMonth;

/**
 * {@link YearMonth}型のビルダクラス。
 *
 * @since 2.1
 * @author T.TSUCHIE
 *
 */
public class YearMonthProcessorBuilder extends AbstractTemporalProcessorBuilder<YearMonth> {

    /**
     * {@literal uuuu-MM}を返す。
     */
    @Override
    protected String getDefaultPattern() {
        return "uuuu-MM";
    }
    
}

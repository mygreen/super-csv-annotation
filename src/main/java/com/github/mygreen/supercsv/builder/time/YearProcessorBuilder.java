package com.github.mygreen.supercsv.builder.time;

import java.time.Year;

/**
 * {@link Year}型のビルダクラス。
 *
 * @since 2.1
 * @author T.TSUCHIE
 *
 */
public class YearProcessorBuilder extends AbstractTemporalProcessorBuilder<Year> {

    /**
     * {@literal uuuu}を返す。
     */
    @Override
    protected String getDefaultPattern() {
        return "uuuu";
    }
    
}

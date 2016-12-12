package com.github.mygreen.supercsv.builder.time;

import java.time.LocalDate;

/**
 * {@link LocalDate}型のビルダクラス。
 * 
 * @version 2.0
 * @since 1.2
 * @author T.TSUCHIE
 *
 */
public class LocalDateProcessorBuilder extends AbstractTemporalProcessorBuilder<LocalDate> {
    
    /**
     * {@literal uuuu-MM-dd}を返す。
     */
    @Override
    protected String getDefaultPattern() {
        return "uuuu-MM-dd";
    }
    
}

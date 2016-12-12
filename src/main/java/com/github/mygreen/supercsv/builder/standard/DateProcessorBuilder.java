package com.github.mygreen.supercsv.builder.standard;

import java.util.Date;

/**
 * {@link Date}型に対するビルダ。
 * 
 * @version 2.0
 * @author T.TSUCHIE
 *
 */
public class DateProcessorBuilder extends AbstractDateProcessorBuilder<Date> {
    
    /**
     * 標準の書式を取得する。
     * @return 常に、'{@literal yyyy-MM-dd HH:mm:ss}' を返す。
     */
    @Override
    protected String getDefaultPattern() {
        return "yyyy-MM-dd HH:mm:ss";
    }
    
}

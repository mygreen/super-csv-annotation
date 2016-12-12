package com.github.mygreen.supercsv.builder.standard;

import java.sql.Date;

/**
 *
 * @version 2.0
 * @author T.TSUCHIE
 *
 */
public class SqlDateProcessorBuilder extends AbstractDateProcessorBuilder<Date> {
    
    /**
     * 標準の書式を取得する。
     * @return 常に、'{@literal yyyy-MM-dd}' を返す。
     */
    @Override
    protected String getDefaultPattern() {
        return "yyyy-MM-dd";
    }
    
}

package com.github.mygreen.supercsv.builder;


/**
 * カラムのラベル情報をヘッダーとする。
 *
 * @since 2.1
 * @author T.TSUCHIE
 *
 */
public class DefaultHeaderMapper implements HeaderMapper {

    @Override
    public String toMap(final ColumnMapping column, final Configuration config, final Class<?>[] groups) {
        return column.getLabel();
    }
    
}

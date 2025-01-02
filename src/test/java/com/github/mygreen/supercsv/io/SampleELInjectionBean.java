package com.github.mygreen.supercsv.io;

import com.github.mygreen.supercsv.annotation.CsvBean;
import com.github.mygreen.supercsv.annotation.CsvColumn;
import com.github.mygreen.supercsv.annotation.constraint.CsvRequire;
import com.github.mygreen.supercsv.annotation.constraint.CsvWordForbid;

/**
 * ELインジェクション確認用のテストBean。
 *
 * @since 2.4
 * @author T.TSUCHIE
 *
 */
@CsvBean(header = true, validateHeader = true)
public class SampleELInjectionBean {
    
    @CsvRequire
    @CsvColumn(number = 1)
    private int id;
    
    @CsvWordForbid("getRuntime")
    @CsvColumn(number = 2)
    private String value;

    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getValue() {
        return value;
    }
    
    public void setValue(String value) {
        this.value = value;
    }
}

package com.github.mygreen.supercsv.io;

import com.github.mygreen.supercsv.annotation.CsvBean;
import com.github.mygreen.supercsv.annotation.CsvColumn;
import com.github.mygreen.supercsv.annotation.CsvPartial;
import com.github.mygreen.supercsv.annotation.constraint.CsvNumberMax;
import com.github.mygreen.supercsv.annotation.constraint.CsvRequire;
import com.github.mygreen.supercsv.annotation.conversion.CsvDefaultValue;
import com.github.mygreen.supercsv.annotation.conversion.CsvFixedSize;
import com.github.mygreen.supercsv.annotation.format.CsvEnumFormat;
import com.github.mygreen.supercsv.annotation.format.CsvNumberFormat;
import com.github.mygreen.supercsv.cellprocessor.conversion.SimplePaddingProcessor;

/**
 * テスト用CsvBean。
 * 固定長のカラム + 部分的なカラム。
 *
 *
 * @author T.TSUCHIE
 *
 */
@CsvPartial(columnSize=5, headers={
        @CsvPartial.Header(number=3, label="生年月日", fixedSize=@CsvFixedSize(size=10)),
        @CsvPartial.Header(number=5, label="備考欄", fixedSize=@CsvFixedSize(size=20, chopped = true)),
})
@CsvBean(header=true, validateHeader=true)
public class SampleFixedColumnPartialBean {
    
    @CsvColumn(number=1, label="氏名")
    @CsvFixedSize(size=15, rightAlign=false)
    @CsvRequire
    private String userName;
    
    @CsvColumn(number=2, label="給料")
    @CsvFixedSize(size=15, rightAlign=true)
    @CsvNumberFormat(pattern="###,###,###")
    @CsvRequire
    @CsvNumberMax(value="999,999")
    private int salary;
    
    @CsvColumn(number=4)
    @CsvFixedSize(size=5, rightAlign=false, paddingProcessor = SimplePaddingProcessor.class)
    @CsvEnumFormat(ignoreCase=true, selector = "aliasName")
    @CsvDefaultValue("青")
    private SampleEnum color;

    public String getUserName() {
        return userName;
    }
    
    public void setUserName(String userName) {
        this.userName = userName;
    }
    
    public int getSalary() {
        return salary;
    }
    
    public void setSalary(int salary) {
        this.salary = salary;
    }
    
    public SampleEnum getColor() {
        return color;
    }
    
    public void setColor(SampleEnum color) {
        this.color = color;
    }
}

package com.github.mygreen.supercsv.io;

import java.util.Date;

import com.github.mygreen.supercsv.annotation.CsvBean;
import com.github.mygreen.supercsv.annotation.CsvColumn;
import com.github.mygreen.supercsv.annotation.CsvPartial;
import com.github.mygreen.supercsv.annotation.constraint.CsvNumberMax;
import com.github.mygreen.supercsv.annotation.constraint.CsvRequire;
import com.github.mygreen.supercsv.annotation.conversion.CsvDefaultValue;
import com.github.mygreen.supercsv.annotation.format.CsvEnumFormat;
import com.github.mygreen.supercsv.annotation.format.CsvNumberFormat;

/**
 * テスト用のBean。
 * 部分的にカラムを読み込む。
 * 
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
@CsvBean(header=true, validateHeader=true)
@CsvPartial(columnSize=11, headers={
        @CsvPartial.Header(number=3, label="number2"),
        @CsvPartial.Header(number=5, label="string2"),
        @CsvPartial.Header(number=7, label="date2"),
        @CsvPartial.Header(number=9, label="列挙型2"),
        @CsvPartial.Header(number=11, label="boolean2"),
        
})
public class SamplePartialBean {
    
    @CsvColumn(number=1)
    @CsvRequire
    private int id;
    
    @CsvColumn(number=2, label="数字1")
    @CsvNumberFormat(pattern="###,###,###")
    @CsvRequire
    @CsvNumberMax(value="999,999")
    private int number1;
    
    @CsvColumn(number=4)
    @CsvRequire
    private String string1;
    
    @CsvColumn(number=6)
    @CsvRequire
    private Date date1;
    
    @CsvColumn(number=8)
    @CsvEnumFormat(ignoreCase=true)
    @CsvDefaultValue("BLUE")
    private SampleEnum enum1;
    
    @CsvColumn(number=10)
    @CsvRequire
    private boolean boolean1;
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getNumber1() {
        return number1;
    }
    
    public void setNumber1(int number1) {
        this.number1 = number1;
    }
    
    public String getString1() {
        return string1;
    }
    
    public void setString1(String string1) {
        this.string1 = string1;
    }
    
    public Date getDate1() {
        return date1;
    }
    
    public void setDate1(Date date1) {
        this.date1 = date1;
    }
    
    public SampleEnum getEnum1() {
        return enum1;
    }
    
    public void setEnum1(SampleEnum enum1) {
        this.enum1 = enum1;
    }
    
    public boolean isBoolean1() {
        return boolean1;
    }
    
    public void setBoolean1(boolean boolean1) {
        this.boolean1 = boolean1;
    }
}

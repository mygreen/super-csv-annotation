package org.supercsv.ext.io;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

import org.supercsv.ext.annotation.CsvBean;
import org.supercsv.ext.annotation.CsvBooleanConverter;
import org.supercsv.ext.annotation.CsvColumn;
import org.supercsv.ext.annotation.CsvDateConverter;
import org.supercsv.ext.annotation.CsvEnumConverter;
import org.supercsv.ext.annotation.CsvNumberConverter;
import org.supercsv.ext.annotation.CsvStringConverter;

/**
 * テスト用の基本的な型のBean
 *
 * @since 1.2
 * @author T.TSUCHIE
 *
 */
@CsvBean(header=true)
public class SampleNormalBean implements Serializable {
    
    /** serialVersionUID */
    private static final long serialVersionUID = 1L;
    
    @CsvColumn(position = 0)
    private int id;
    
    @CsvColumn(position = 1, label="数字1")
    @CsvNumberConverter(pattern="###,###,###", max="999,999")
    private int number1;
    
    @CsvColumn(position = 2, optional=true)
    private Double number2;
    
    @CsvColumn(position = 3)
    private String string1;
    
    @CsvColumn(position = 4, optional=true, inputDefaultValue="@empty")
    @CsvStringConverter(maxLength=6)
    private String string2;
    
    @CsvColumn(position = 5)
    private Date date1;
    
    @CsvColumn(position = 6, optional=true)
    @CsvDateConverter(pattern="yyyy年MM月dd日")
    private Timestamp date2;
    
    @CsvColumn(position = 7, optional=true, inputDefaultValue="BLUE")
    @CsvEnumConverter(ignoreCase = true)
    private SampleEnum enum1;
    
    @CsvColumn(position = 8, label="列挙型2", optional=true, inputDefaultValue="青")
    @CsvEnumConverter(ignoreCase = true, valueMethodName="aliasName")
    private SampleEnum enum2;
    
    @CsvColumn(position = 9)
    private boolean boolean1;
    
    @CsvColumn(position = 10, optional=true, inputDefaultValue="○", outputDefaultValue="×")
    @CsvBooleanConverter(inputTrueValue = {"○"}, inputFalseValue = {"×"}, outputTrueValue = "○", outputFalseValue="×")
    private Boolean boolean2;
    
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
    
    public Double getNumber2() {
        return number2;
    }
    
    public void setNumber2(Double number2) {
        this.number2 = number2;
    }
    
    public String getString1() {
        return string1;
    }
    
    public void setString1(String string1) {
        this.string1 = string1;
    }
    
    public String getString2() {
        return string2;
    }
    
    public void setString2(String string2) {
        this.string2 = string2;
    }
    
    public Date getDate1() {
        return date1;
    }
    
    public void setDate1(Date date1) {
        this.date1 = date1;
    }
    
    public Timestamp getDate2() {
        return date2;
    }
    
    public void setDate2(Timestamp date2) {
        this.date2 = date2;
    }
    
    public SampleEnum getEnum1() {
        return enum1;
    }
    
    public void setEnum1(SampleEnum enum1) {
        this.enum1 = enum1;
    }
    
    public SampleEnum getEnum2() {
        return enum2;
    }
    
    public void setEnum2(SampleEnum enum2) {
        this.enum2 = enum2;
    }
    
    public boolean getBoolean1() {
        return boolean1;
    }
    
    public void setBoolean1(boolean boolean1) {
        this.boolean1 = boolean1;
    }
    
    public Boolean getBoolean2() {
        return boolean2;
    }
    
    public void setBoolean2(Boolean boolean2) {
        this.boolean2 = boolean2;
    }
    
}

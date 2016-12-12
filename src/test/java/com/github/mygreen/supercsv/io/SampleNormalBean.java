package com.github.mygreen.supercsv.io;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

import com.github.mygreen.supercsv.annotation.CsvBean;
import com.github.mygreen.supercsv.annotation.CsvColumn;
import com.github.mygreen.supercsv.annotation.constraint.CsvLengthMax;
import com.github.mygreen.supercsv.annotation.constraint.CsvNumberMax;
import com.github.mygreen.supercsv.annotation.constraint.CsvRequire;
import com.github.mygreen.supercsv.annotation.conversion.CsvDefaultValue;
import com.github.mygreen.supercsv.annotation.format.CsvBooleanFormat;
import com.github.mygreen.supercsv.annotation.format.CsvDateTimeFormat;
import com.github.mygreen.supercsv.annotation.format.CsvEnumFormat;
import com.github.mygreen.supercsv.annotation.format.CsvNumberFormat;

/**
 * テスト用の基本的な型のBean
 * 
 * @version 2.0
 * @since 1.2
 * @author T.TSUCHIE
 *
 */
@CsvBean(header=true, validateHeader=true)
public class SampleNormalBean implements Serializable {
    
    /** serialVersionUID */
    private static final long serialVersionUID = 1L;
    
    /** 読み込み時のグループ */
    public static interface ReadGroup {}
    
    /** 書き込み時のグループ */
    public static interface WriteGroup {}
    
    @CsvColumn(number=1)
    @CsvRequire
    private int id;
    
    @CsvColumn(number=2, label="数字1")
    @CsvNumberFormat(pattern="###,###,###")
    @CsvRequire
    @CsvNumberMax(value="999,999")
    private int number1;
    
    @CsvColumn(number=3)
    private Double number2;
    
    @CsvColumn(number=4)
    @CsvRequire
    private String string1;
    
    @CsvColumn(number=5)
    @CsvDefaultValue("")
    @CsvLengthMax(value=6)
    private String string2;
    
    @CsvColumn(number=6)
    @CsvRequire
    private Date date1;
    
    @CsvColumn(number=7)
    @CsvDateTimeFormat(pattern="yyyy年MM月dd日")
    private Timestamp date2;
    
    @CsvColumn(number=8)
    @CsvEnumFormat(ignoreCase=true)
    @CsvDefaultValue("BLUE")
    private SampleEnum enum1;
    
    @CsvColumn(number=9, label="列挙型2")
    @CsvEnumFormat(ignoreCase=true, selector="aliasName")
    @CsvDefaultValue(value="青", groups=ReadGroup.class)
    private SampleEnum enum2;
    
    @CsvColumn(number=10)
    @CsvRequire
    private boolean boolean1;
    
    @CsvColumn(number=11)
    @CsvBooleanFormat(readForTrue={"○"}, readForFalse={"×"}, writeAsTrue="○", writeAsFalse="×")
    @CsvDefaultValue(value="○", groups=ReadGroup.class)
    @CsvDefaultValue(value="×", groups=WriteGroup.class)
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
    
    public boolean isBoolean1() {
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

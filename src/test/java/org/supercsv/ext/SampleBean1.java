package org.supercsv.ext;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.supercsv.ext.annotation.CsvBean;
import org.supercsv.ext.annotation.CsvBooleanConverter;
import org.supercsv.ext.annotation.CsvColumn;
import org.supercsv.ext.annotation.CsvDateConverter;
import org.supercsv.ext.annotation.CsvEnumConverter;
import org.supercsv.ext.annotation.CsvNumberConverter;
import org.supercsv.ext.annotation.CsvStringConverter;

@CsvBean(header=true)
public class SampleBean1 implements Serializable {
    
    /** serialVersionUID */
    private static final long serialVersionUID = 1L;
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
    
    @CsvColumn(position = 0, label="数字1")
    private int integer1;
    
    @CsvColumn(position = 1, optional=true)
    @CsvNumberConverter(pattern="###,###,###")
    private Integer integer2;
    
    @CsvColumn(position = 2)
    private String string1;
    
    @CsvColumn(position = 3, optional=true, inputDefaultValue="@empty")
    @CsvStringConverter(maxLength=6/*, contain={"1"}*/)
    private String string2;
    
    @CsvColumn(position = 4)
    private Date date1;
    
    @CsvColumn(position = 5, optional=true)
    @CsvDateConverter(pattern="yyyy年MM月dd日"/*, min="2000年01月30日"*/)
    private Timestamp date2;
    
    @CsvColumn(position = 6, label="列挙型", optional=true/*, inputDefaultValue="青"*/, inputDefaultValue="BLUE")
    @CsvEnumConverter(ignoreCase = true/*, valueMethodName="aliasName"*/)
    private Color enum1;
    
    @CsvColumn(position = 7, optional=false, inputDefaultValue="○", outputDefaultValue="×")
    @CsvBooleanConverter(inputTrueValue = {"○"}, inputFalseValue = {"×"}, outputTrueValue = "○", outputFalseValue="×")
    private Boolean avaialble;
    
    public SampleBean1() {
        
    }
    
    public int getInteger1() {
        return integer1;
    }

    
    public void setInteger1(int integer1) {
        this.integer1 = integer1;
    }

    
    public Integer getInteger2() {
        return integer2;
    }

    
    public void setInteger2(Integer integer2) {
        this.integer2 = integer2;
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

    
    public Color getEnum1() {
        return enum1;
    }

    
    public void setEnum1(Color enum1) {
        this.enum1 = enum1;
    }

    
    public Boolean getAvaialble() {
        return avaialble;
    }

    
    public void setAvaialble(Boolean avaialble) {
        this.avaialble = avaialble;
    }
    
    
}

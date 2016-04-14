package org.supercsv.ext;

import java.io.Serializable;

import org.supercsv.ext.annotation.CsvBean;
import org.supercsv.ext.annotation.CsvColumn;

@CsvBean
public class TrimCsv implements Serializable {
    
    /** serialVersionUID */
    private static final long serialVersionUID = 1L;
    
    @CsvColumn(position=0, trim=false)
    public String strTrim0;
    
    @CsvColumn(position=1, trim=true)
    public String strTrim1;
    
    @CsvColumn(position=2, trim=true, optional=true)
    public String strTrim2;
    
    @CsvColumn(position=3, trim=true, optional=true, unique=true)
    public String strTrim3;
    
    @CsvColumn(position=4, trim=true, optional=true)
    public int intTrim4;
    
    public String getStrTrim0() {
        return strTrim0;
    }
    
    public void setStrTrim0(String strTrim0) {
        this.strTrim0 = strTrim0;
    }
    
    public String getStrTrim1() {
        return strTrim1;
    }
    
    public void setStrTrim1(String strTrim1) {
        this.strTrim1 = strTrim1;
    }
    
    public String getStrTrim2() {
        return strTrim2;
    }
    
    public void setStrTrim2(String strTrim2) {
        this.strTrim2 = strTrim2;
    }
    
    public String getStrTrim3() {
        return strTrim3;
    }
    
    public void setStrTrim3(String strTrim3) {
        this.strTrim3 = strTrim3;
    }
    
    public int getIntTrim4() {
        return intTrim4;
    }
    
    public void setIntTrim4(int intTrim4) {
        this.intTrim4 = intTrim4;
    }
    
}

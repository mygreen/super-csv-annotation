package org.supercsv.ext.builder.spring;

import java.io.Serializable;

import org.supercsv.ext.annotation.CsvBean;
import org.supercsv.ext.annotation.CsvColumn;
import org.supercsv.ext.annotation.CsvStringConverter;


@CsvBean(header=true)
public class UserCsv implements Serializable {
    
    /** serialVersionUID */
    private static final long serialVersionUID = 1L;

    @CsvColumn(position=0, label="ユーザID")
    private Integer id;
    
    @CsvColumn(position=1, label="ユーザ名", builderClass=UserNameCellProcessorBuilder.class)
    @CsvStringConverter(maxLength=10)
    private String name;
    
    @CsvColumn(position=2, label="メールアドレス")
    private String mailAddress;
    
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getMailAddress() {
        return mailAddress;
    }
    
    public void setMailAddress(String mailAddress) {
        this.mailAddress = mailAddress;
    }
}

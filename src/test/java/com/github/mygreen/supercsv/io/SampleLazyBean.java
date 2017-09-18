package com.github.mygreen.supercsv.io;

import java.io.Serializable;
import java.time.LocalDate;

import com.github.mygreen.supercsv.annotation.CsvBean;
import com.github.mygreen.supercsv.annotation.CsvColumn;
import com.github.mygreen.supercsv.annotation.constraint.CsvRequire;
import com.github.mygreen.supercsv.annotation.format.CsvDateTimeFormat;

/**
 * テスト用の名前によるマッピング
 *
 * @since 2.1
 * @author T.TSUCHIE
 *
 */
@CsvBean(header=true, validateHeader=true)
public class SampleLazyBean implements Serializable {

    /** serialVersionUID */
    private static final long serialVersionUID = 1L;
    
    // ラベルがフィールド名
    @CsvColumn
    @CsvRequire
    private int no;
    
    // 列番号を指定
    @CsvColumn(number=2)
    @CsvRequire
    private String name;
    
    // ラベルだけ指定
    @CsvColumn(label="生年月日")
    @CsvDateTimeFormat(pattern="uuuu/MM/dd")
    private LocalDate birthday;
    
    // 番号とラベルの両方を指定
    @CsvColumn(number=4, label="備考")
    private String comment;
    
    public int getNo() {
        return no;
    }
    
    public void setNo(int no) {
        this.no = no;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public LocalDate getBirthday() {
        return birthday;
    }
    
    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }
    
    public String getComment() {
        return comment;
    }
    
    public void setComment(String comment) {
        this.comment = comment;
    }
    
}

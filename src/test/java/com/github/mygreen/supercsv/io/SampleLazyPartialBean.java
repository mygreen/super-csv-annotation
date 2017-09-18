package com.github.mygreen.supercsv.io;

import java.time.LocalDate;

import com.github.mygreen.supercsv.annotation.CsvBean;
import com.github.mygreen.supercsv.annotation.CsvColumn;
import com.github.mygreen.supercsv.annotation.CsvPartial;

/**
 * 部分的にカラムを読み込む。
 * 
 * @since 2.1
 * @author T.TSUCHIE
 *
 */
@CsvBean(header=true, validateHeader=true)
@CsvPartial(columnSize=8, headers={
        @CsvPartial.Header(number=3, label="誕生日"),
        @CsvPartial.Header(number=5, label="住所")
})
public class SampleLazyPartialBean {
    
    @CsvColumn(number=1)
    private int id;
    
    @CsvColumn(label="名前")
    private String name;
    
    @CsvColumn(number=6, label="有効期限")
    private LocalDate expiredDate;
    
    @CsvColumn(label="備考")
    private String comment;
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public LocalDate getExpiredDate() {
        return expiredDate;
    }
    
    public void setExpiredDate(LocalDate expiredDate) {
        this.expiredDate = expiredDate;
    }
    
    public String getComment() {
        return comment;
    }
    
    public void setComment(String comment) {
        this.comment = comment;
    }
}

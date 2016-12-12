package com.github.mygreen.supercsv.io;

import java.time.LocalDate;

import com.github.mygreen.supercsv.annotation.CsvBean;
import com.github.mygreen.supercsv.annotation.CsvColumn;
import com.github.mygreen.supercsv.annotation.constraint.CsvNumberMin;
import com.github.mygreen.supercsv.annotation.constraint.CsvRequire;
import com.github.mygreen.supercsv.annotation.constraint.CsvUnique;
import com.github.mygreen.supercsv.annotation.conversion.CsvDefaultValue;
import com.github.mygreen.supercsv.annotation.conversion.CsvNullConvert;
import com.github.mygreen.supercsv.annotation.format.CsvBooleanFormat;
import com.github.mygreen.supercsv.annotation.format.CsvDateTimeFormat;
import com.github.mygreen.supercsv.annotation.format.CsvNumberFormat;
import com.github.mygreen.supercsv.builder.BuildCase;

/**
 * サンプル用のBean。
 * <p>特に、マニュアルのhowtouseに記載するもの。</p>
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
@CsvBean
public class HowtoUseCsv {
    
    @CsvColumn(number=1, label="ID")
    @CsvRequire                        // 必須チェックを行う
    @CsvUnique(order=1)                // 全レコード内で値がユニークかチェックする(順番指定)
    @CsvNumberMin(value="0", order=2)  // 最小値かどかチェックする(順番指定)
    private Integer id;
    
    @CsvColumn(number=2, label="名前")
    private String name;
    
    @CsvColumn(number=3, label="誕生日")
    @CsvDateTimeFormat(pattern="yyyy年MM月dd日")   // 日時の書式を指定する
    private LocalDate birthday;
    
    @CsvColumn(number=4, label="給料")
    @CsvNumberFormat(pattern="#,###0")                   // 数値の書式を指定する
    @CsvDefaultValue(value="N/A", cases=BuildCase.Write)  // 書き込み時に値がnull(空)の場合、「N/A」として出力します。
    @CsvNullConvert(value="N/A", cases=BuildCase.Read)    // 読み込み時に値が「N/A」のとき、nullとして読み込みます。
    private Integer salary;
    
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
    
    public LocalDate getBirthday() {
        return birthday;
    }
    
    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }
    
    public Integer getSalary() {
        return salary;
    }
    
    public void setSalary(Integer salary) {
        this.salary = salary;
    }
    
}

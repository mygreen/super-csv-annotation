package com.github.mygreen.supercsv.io;

import java.time.LocalDate;

import com.github.mygreen.supercsv.annotation.CsvBean;
import com.github.mygreen.supercsv.annotation.CsvColumn;
import com.github.mygreen.supercsv.annotation.conversion.CsvFixedSize;
import com.github.mygreen.supercsv.annotation.conversion.CsvFullChar;
import com.github.mygreen.supercsv.annotation.format.CsvDateTimeFormat;
import com.github.mygreen.supercsv.builder.FixedSizeHeaderMapper;
import com.github.mygreen.supercsv.cellprocessor.conversion.CharWidthPaddingProcessor;

/**
 * テスト用のBean。
 * 固定長の名前によるマッピングのBean
 *
 * @since 2.1
 * @author T.TSUCHIE
 *
 */
@CsvBean(header=false, headerMapper=FixedSizeHeaderMapper.class) // ヘッダーを固定長に処理するマッパーを指定する。
public class SampleLazyFixedColumnBean {

    // 右詰めする。
    @CsvColumn(number=1)
    @CsvFixedSize(size=5, rightAlign=true)
    private int no;

    // 全角空白で埋める。
    // ただし、全角は長さ=2、半角は長さ=1 として処理する。
    @CsvColumn(label="ユーザ名")
    @CsvFullChar
    @CsvFixedSize(size=20, padChar='　', paddingProcessor=CharWidthPaddingProcessor.class)
    private String userName;

    // 他のオブジェクト型への変換を行う場合
    @CsvColumn(label="誕生日")
    @CsvFixedSize(size=10, padChar='_')
    @CsvDateTimeFormat(pattern="uuuu-MM-dd")
    private LocalDate birthDay;

    // 指定した文字長を超えた場合、切り落とす。
    @CsvColumn(label="コメント")
    @CsvFixedSize(size=20, rightAlign=false, chopped=true, paddingProcessor=CharWidthPaddingProcessor.class)
    private String comment;

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public LocalDate getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(LocalDate birthDay) {
        this.birthDay = birthDay;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

}

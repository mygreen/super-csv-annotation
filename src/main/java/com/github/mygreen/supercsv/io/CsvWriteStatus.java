package com.github.mygreen.supercsv.io;


/**
 * CSVの書き込み処理ステータスを表現する列挙型です。
 *
 * @since 2.3
 * @author T.TSUCHIE
 *
 */
public enum CsvWriteStatus {
    
    /**
     * 書き込み処理に成功したとき。
     */
    SUCCESS,
    /**
     * 書き込み処理に失敗したとき。
     */
    ERROR;
    
}

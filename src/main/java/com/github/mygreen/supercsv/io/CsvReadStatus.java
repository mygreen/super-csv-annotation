package com.github.mygreen.supercsv.io;


/**
 * CSVの読み込み処理ステータスを表現する列挙型です。
 *
 * @since 2.3
 * @author T.TSUCHIE
 *
 */
public enum CsvReadStatus {
    
    /**
     * 読み込み時にCSVファイルを最後まで読み込んだとき。
     */
    EOF,
    /**
     * 読み込み/書き込み処理に成功したとき。
     */
    SUCCESS,
    /**
     * 読み込み/書き込み処理に失敗したとき。
     */
    ERROR;
    
}

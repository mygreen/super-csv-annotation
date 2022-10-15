package com.github.mygreen.supercsv.io;

import org.supercsv.exception.SuperCsvException;

/**
 * CSV処理に失敗したときの戦略。
 *
 * @since 2.3
 * @author T.TSUCHIE
 *
 */
@FunctionalInterface
public interface CsvErrorHandler {
    
    /**
     * 例外発生時に何も行わない実装を取得します。
     * <p>例外を無視して続けて処理したい場合に利用します。
     * @return 空の実装を返します。
     */
    static CsvErrorHandler empty() {
        return new CsvErrorHandler() {

            @Override
            public void onError(SuperCsvException exception) {
                // ignore error
            }
            
        };
    }
    
    /**
     * CSV処理に失敗したときに呼び出される処理です。
     * @param exception 発生したCSVに関する例外
     */
    void onError(SuperCsvException exception);
}

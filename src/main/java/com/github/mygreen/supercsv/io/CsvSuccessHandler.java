package com.github.mygreen.supercsv.io;


/**
 * CSV処理に成功したときの戦略。
 *
 * @since 2.3
 * @author T.TSUCHIE
 * 
 * @param <T> マッピング対象のBeanのクラスタイプ
 */
@FunctionalInterface
public interface CsvSuccessHandler<T> {
    
    /**
     * CSVの行に対する処理が正常に行われたときに呼び出されます。
     * @param record CSVの行をマッピングしたBeanオブジェクト。
     */
    void onSuccess(T record);
}

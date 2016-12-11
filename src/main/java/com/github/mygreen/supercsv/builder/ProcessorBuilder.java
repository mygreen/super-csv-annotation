package com.github.mygreen.supercsv.builder;

import java.util.Optional;

import org.supercsv.cellprocessor.ift.CellProcessor;


/**
 * {@link CellProcessor}を組み立てるためのインタフェース。
 * 
 * @param <T> 処理対象のクラスタイプ。
 * @since 1.1
 * @author T.TSUCHIE
 *
 */
public interface ProcessorBuilder<T> {
    
    /**
     * 読み込み用の{@link CellProcessor}を組み立てる。
     * @param type フィールドのクラスタイプ。
     * @param field フィールド情報。
     * @param config 設定情報
     * @param groups グループ情報
     * @return 組み立てた{@link CellProcessor}。
     */
    Optional<CellProcessor> buildForReading(Class<T> type, FieldAccessor field, Configuration config, Class<?>[] groups);
    
    /**
     * 書き込み用の{@link CellProcessor}を組み立てる。
     * @param type フィールドのクラスタイプ。
     * @param field フィールド情報。
     * @param config 設定情報
     * @param groups グループ情報
     * @return 組み立てた{@link CellProcessor}。
     */
    Optional<CellProcessor> buildForWriting(Class<T> type, FieldAccessor field, Configuration config, Class<?>[] groups);
    
}

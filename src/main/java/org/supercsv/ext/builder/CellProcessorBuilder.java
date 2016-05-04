package org.supercsv.ext.builder;

import java.lang.annotation.Annotation;

import org.supercsv.cellprocessor.ift.CellProcessor;


/**
 * {@link CellProcessor}を組み立てるためのインタフェース。
 * 
 * @param <T> 処理対象のクラスタイプ。
 * @since 1.1
 * @author T.TSUCHIE
 *
 */
public interface CellProcessorBuilder<T> {
    
    /**
     * 書き込み用の{@link CellProcessor}を組み立てる。
     * @param type フィールドのクラスタイプ。
     * @param annos フィールドに付与された全てのアノテーション。
     * @param ignoreValidationProcessor 値がtrueの場合、最大値の検証などの制約チェックをするCellProcessorは無視し、組み込まない。
     * @return 組み立てた{@link CellProcessor}。
     */
    CellProcessor buildOutputCellProcessor(Class<T> type, Annotation[] annos, boolean ignoreValidationProcessor);
    
    /**
     * 読み込み用の{@link CellProcessor}を組み立てる。
     * @param type フィールドのクラスタイプ。
     * @param annos フィールドに付与された全てのアノテーション。
     * @return 組み立てた{@link CellProcessor}。
     */
    CellProcessor buildInputCellProcessor(Class<T> type, Annotation[] annos);
    
}

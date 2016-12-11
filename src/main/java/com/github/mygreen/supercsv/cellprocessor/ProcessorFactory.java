package com.github.mygreen.supercsv.cellprocessor;

import java.util.Optional;

import org.supercsv.cellprocessor.ift.CellProcessor;

import com.github.mygreen.supercsv.builder.BuildCase;
import com.github.mygreen.supercsv.builder.Configuration;
import com.github.mygreen.supercsv.builder.FieldAccessor;
import com.github.mygreen.supercsv.cellprocessor.format.TextFormatter;

/**
 * {@link CellProcessor}のインスタンスを作成するインタフェース。
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
@FunctionalInterface
public interface ProcessorFactory {
    
    /**
     * 値を変換する{@link CellProcessor}を作成する。
     * 
     * @param next Chainで次に実行される{@link CellProcessor}。値がない場合がある。
     * @param field 処理対象のフィールド情報。
     * @param formatter フィールドの書式に沿ったフォーマッタ。
     * @param config システム情報。
     * @param buildCase 組み立てる際の種別。読み込み時と書き込み時と区別する際に利用します。
     * @param groups グループ情報。提供するアノテーションの絞り込みに利用します。
     * @return {@link CellProcessor}の実装クラスのインスタンス。
     *         引数nextをそのまま返すため、値がない場合がある。
     */
    Optional<CellProcessor> create(Optional<CellProcessor> next, FieldAccessor field, 
            TextFormatter<?> formatter, Configuration config, BuildCase buildCase, Class<?>[] groups);
    
}

package com.github.mygreen.supercsv.cellprocessor;

import java.lang.annotation.Annotation;
import java.util.Optional;

import org.supercsv.cellprocessor.ift.CellProcessor;

import com.github.mygreen.supercsv.builder.Configuration;
import com.github.mygreen.supercsv.builder.FieldAccessor;
import com.github.mygreen.supercsv.cellprocessor.format.TextFormatter;

/**
 * 変換のアノテーションを元に値の変換を行う{@link CellProcessor}を作成するためのインタフェース。
 * 
 * @param <A> 対応する変換のアノテーション。
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
@FunctionalInterface
public interface ConversionProcessorFactory<A extends Annotation> {
    
    /**
     * 値を変換する{@link CellProcessor}を作成します。
     * 
     * @param anno ハンドリング対象のアノテーションです。
     * @param next chainで次に実行する{@link CellProcessor}。値がない場合があります。
     * @param field 処理対象のフィールド情報。
     * @param formatter フィールドの書式に沿ったフォーマッタ。
     * @param config システム情報設定。
     * @return {@link CellProcessor}の実装クラスのインスタンス。
     *         引数nextをそのまま返す場合、値がない場合がある。
     */
    Optional<CellProcessor> create(A anno, Optional<CellProcessor> next, FieldAccessor field,
            TextFormatter<?> formatter, Configuration config);
    
}

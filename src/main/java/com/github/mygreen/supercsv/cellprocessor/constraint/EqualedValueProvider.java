package com.github.mygreen.supercsv.cellprocessor.constraint;

import java.util.Collection;

import com.github.mygreen.supercsv.annotation.constraint.CsvEquals;
import com.github.mygreen.supercsv.builder.FieldAccessor;

/**
 * {@link CsvEquals}による等値かどうか比較する際の値の候補を提供するインタフェース。
 * <p>値を別ファイルやDBから取得する時などサービスクラスとして実装します。</p>
 * 
 * 
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
@FunctionalInterface
public interface EqualedValueProvider<T> {
    
    /**
     * 比較対象の値を取得します。
     * @param field フィールド情報
     * @return 比較対処の値のリストを返します。
     */
    Collection<T> getEqualedValues(FieldAccessor field);
    
}

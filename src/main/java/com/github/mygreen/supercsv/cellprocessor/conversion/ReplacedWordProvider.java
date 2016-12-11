package com.github.mygreen.supercsv.cellprocessor.conversion;

import java.util.Collection;

import com.github.mygreen.supercsv.annotation.conversion.CsvWordReplace;
import com.github.mygreen.supercsv.builder.FieldAccessor;

/**
 * {@link CsvWordReplace}による置換語彙を提供するためのインタフェースです。
 * <p>語彙を別ファイルやDBから取得する時などサービスクラスとして実装します。</p>
 * 
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
@FunctionalInterface
public interface ReplacedWordProvider {
    
    /**
     * 語彙の一覧を取得する。
     * @param field フィールド情報
     * @return 語彙を返します。チェック対象の文字がない場合は、空のリストを返します。
     */
    Collection<ReplacedWord> getReplacedWords(FieldAccessor field);
    
}

package com.github.mygreen.supercsv.builder;

import org.supercsv.exception.SuperCsvReflectionException;

/**
 * インスタンスを作成する処理のインタフェース
 * 
 * @param <T> create()メソッドの引数のクラスタイプ。
 * @param <R> create()メソッドの戻り値のタイプ。
 * 
 * @since 2.0
 * @author T.TSUCHIE
 * 
 */
public interface BeanFactory<T, R> {
    
    /**
     * 引数Tのクラスタイプのインスタンスを返す。
     * @param type クラスタイプ。
     * @return 作成したクラスのインスタンス。
     * @throws NullPointerException {@literal type is null.}
     * @throws SuperCsvReflectionException インスタンスの作成に失敗した場合
     */
    R create(T type);
}

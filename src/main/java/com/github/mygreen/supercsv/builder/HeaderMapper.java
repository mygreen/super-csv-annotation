package com.github.mygreen.supercsv.builder;

/**
 * カラムに対するヘッダーを決める処理のインタフェース。
 *
 * @since 2.1
 * @author T.TSUCHIE
 *
 */
public interface HeaderMapper {
    
    /**
     * カラム情報からヘッダーのラベル情報を取得する。
     * @param column カラム情報
     * @param config システム情報
     * @param groups グループ情報
     * @return ヘッダー情報
     */
    String toMap(ColumnMapping column, Configuration config, Class<?>[] groups);
    
}

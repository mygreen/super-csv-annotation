package com.github.mygreen.supercsv.localization;

import java.util.Optional;

/**
 * メッセージを解決する機能を提供するインターフェース。
 * 
 * @author T.TSUCHIE
 *
 */
public interface MessageResolver {
    
    /**
     * コードを指定してメッセージを取得する。
     * @param code メッセージのコード。
     * @return 指定したコードが見つからない場合は、空を返す。
     */
    public Optional<String> getMessage(String code);
    
}

package org.supercsv.ext.util;

import java.util.Locale;


/**
 * 
 * @version 1.2
 * @author T.TSUCHIE
 *
 */
public class Utils {
    
    /**
     * check with empty object.
     * 
     * @since 1.2
     * @param value
     * @return true : {@literal value is null or length == 0}.
     */
    public static boolean isEmpty(final Object[] value) {
        if(value == null || value.length == 0) {
            return true;
        }
        
        return false;
    }
    
    public static boolean isEmpty(final String value) {
        if(value == null || value.isEmpty()) {
            return true;
        }
        
        return false;
    }
    
    /**
     * 文字列形式のロケールをオブジェクトに変換する。
     * <p>アンダーバーで区切った'ja_JP'を分解して、Localeに渡す。
     * @since 1.2
     * @param str
     * @return 引数が空の時はデフォルトロケールを返す。
     */
    public static Locale getLocale(final String str) {
        
        if(isEmpty(str)) {
            return Locale.getDefault();
        }
        
        if(!str.contains("_")) {
            return new Locale(str);
        }
        
        final String[] split = str.split("_");
        if(split.length == 2) {
            return new Locale(split[0], split[1]);
            
        } else {
            return new Locale(split[0], split[1], split[2]);
        }
        
    }
}

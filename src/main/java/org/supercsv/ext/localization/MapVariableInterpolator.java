/*
 * MapVariableInterpolator.java
 * created in 2013/03/10
 *
 * (C) Copyright 2003-2013 GreenDay Project. All rights reserved.
 */
package org.supercsv.ext.localization;

import java.util.Map;
import java.util.Map.Entry;

/**
 * Simple Substitutes variables within a string by values.
 * <p>placeholder '${name}'
 * <p>warning : this class not support placeholder escape, so use should be Common-Lang 'StrSubstitutor'
 * 
 * @see org.apache.commons.lang.text.StrSubstitutor
 * @see net.sf.oval.internal.MessageRenderer
 * @author T.TSUCHIE
 *
 */
public class MapVariableInterpolator {
    
    /** 正規表現のエスケープマップ */
    private static String[][] escapeMapRegex = {
        {"$", "\\$"},
        {"^", "\\^"},
        {".", "\\."},
        {"*", "\\*"},
        {"|", "\\|"},
        {"?", "\\?"},
        {"+", "\\+"},
        {"\\", "\\\\"},
        {"(", "\\("},
        {")", "\\)"},
        {"[", "\\["},
        {"]", "\\]"},
        {"{", "\\{"},
        {"}", "\\}"},
    };
    
    private MapVariableInterpolator() {
        super();
    }
    
    public static String interpolate(final String message, final Map<String, ?> variables) {
        if(message == null) {
            throw new IllegalArgumentException("message must be not null.");
        }
        
        // if there are no place holders in the message simply return it
        if (message.indexOf('{') < 0) {
            return message;
        }
        
        if(variables == null || variables.isEmpty()) {
            return message;
        }
        
        String value = message;
        for(Entry<String, ?> entry : variables.entrySet()) {
            // create placeholder and escape regex.
            final String varName = escapeRegexPattern("${" + entry.getKey() + "}");
            value = value.replaceAll(varName, convertObjectToString(entry.getValue()));
        }
        
        return value;
    }
    
    public static String interpolate(final String message) {
        if(message == null) {
            throw new IllegalArgumentException("message must be not null.");
        }
        return message;
    }
    
    private static String convertObjectToString(final Object value) {
        if(value == null) {
            return "";
            
        } else if(value instanceof String) {
            return (String) value;
        }
        
        return value.toString();
        
    }
    
    private static String escapeRegexPattern(final String value) {
        return escapeStringRecursive(value, escapeMapRegex);
    }
    
    /**
     * マッピングにしたがって，文字列をエスケープする．
     * <p>再帰的に処理する。
     * 
     * @param str 文字列
     * @param escapeMaps エスケープマッピング
     * @return エスケープされた文字列
     */
    private static String escapeStringRecursive(final String str, String[][] escapeMaps) {
        
        if(str == null || str.isEmpty()) {
            return "";
        }
        
        // エスケープマップに沿って，分割統治で変換していく．
        int index = -1;
        for(String[] escapeMap : escapeMaps) {
            
            // 一致する文字がある場合，再帰的に変換していく．
            if((index = str.indexOf(escapeMap[0])) >= 0) {
                
                return escapeStringRecursive(str.substring(0, index), escapeMaps)
                    + escapeMap[1]
                    + escapeStringRecursive(str.substring(index + escapeMap[0].length()), escapeMaps);
            }
        }
        
        return str;
    }
    
}

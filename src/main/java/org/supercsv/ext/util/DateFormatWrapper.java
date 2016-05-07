package org.supercsv.ext.util;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * スレッドセーフな{@link DateFormat}。
 *
 * @since 1.2
 * @author T.TSUCHIE
 *
 */
public class DateFormatWrapper {
    
    private final DateFormat formatter;
    
    /**
     * フォーマッタを指定してインスタンスを作成するコンストラクタ。
     * @param formatter 日時のフォーマッタ。
     * @throws NullPointerException if formatter is null.
     */
    public DateFormatWrapper(final DateFormat formatter) {
        if(formatter == null) {
            throw new NullPointerException("formatter should not be null.");
        }
        
        this.formatter = (DateFormat) formatter.clone();
        
    }
    
    /**
     * 日時のクラス型を指定してインスタンスを作成するコンストラクタ。
     * <p>書式は、指定したクラスタイプによりにより、自動的に決まります。</p>
     * <ul>
     *  <li>{@link Timestamp}型の場合、書式は「yyyy-MM-dd HH:mm:ss.SSS」となります。</li>
     *  <li>{@link Time}型の場合、書式は「HH:mm:ss」となります。</li>
     *  <li>{@link java.sql.Date}型の場合、書式は「yyyy-MM-dd」となります。</li>
     *  <li>{@link Date}型の場合、書式は「yyyy-MM-dd HH:mm:ss」となります。</li>
     * </ul>
     * 
     * @param dateClass 日時型のクラスタイプ。
     * @throws NullPointerException if dateClass is null.
     */
    public DateFormatWrapper(final Class<? extends Date> dateClass) {
        
        if(dateClass == null) {
            throw new NullPointerException("dateClass should not be null.");
        }
        
        final String pattern;
        if(Timestamp.class.isAssignableFrom(dateClass)) {
            pattern = "yyyy-MM-dd HH:mm:ss.SSS";
            
        } else if(Time.class.isAssignableFrom(dateClass)) {
            pattern = "HH:mm:ss";
            
        } else if(java.sql.Date.class.isAssignableFrom(dateClass)) {
            pattern = "yyyy-MM-dd";
            
        } else {
            pattern = "yyyy-MM-dd HH:mm:ss";
        }
        
        this.formatter = new SimpleDateFormat(pattern);
    }
    
    /**
     * 日時オブジェクトを文字列にフォーマットします。
     * @param date 日時オブジェクト。
     * @return フォーマットした文字列。
     */
    public synchronized <T extends Date> String format(final T date) {
        return formatter.format(date);
        
    }
    
    /**
     * 文字列を解析し、日時オブジェクトに変換します。
     * @param str 解析対象の文字列。
     * @return 変換した日時オブジェクト。
     * @throws ParseException 文字列が不正など、解析に失敗した場合。
     */
    public synchronized Date parse(final String str) throws ParseException {
        return formatter.parse(str);
    }
    
    /**
     * 書式を取得します。
     * @return 書式が取得できない場合、nullを返します。
     */
    public String getPattern() {
        
        if(formatter instanceof SimpleDateFormat) {
            SimpleDateFormat sdf = (SimpleDateFormat) formatter;
            return sdf.toPattern();
        }
        
        return null;
        
    }
    
}

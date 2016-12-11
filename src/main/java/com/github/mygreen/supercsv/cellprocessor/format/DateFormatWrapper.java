package com.github.mygreen.supercsv.cellprocessor.format;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * スレッドセーフな{@link DateFormat}。
 *
 * @since 1.2
 * @author T.TSUCHIE
 *
 */
public class DateFormatWrapper<T extends Date> extends AbstractTextFormatter<T> {
    
    private final DateFormat formatter;
    
    private final Class<T> type;
    
    /**
     * フォーマッタを指定してインスタンスを作成するコンストラクタ。
     * @param formatter 日時のフォーマッタ。
     * @param type 対応する日時のクラス。
     * @throws NullPointerException if formatter is null.
     */
    public DateFormatWrapper(final DateFormat formatter, final Class<T> type) {
        Objects.requireNonNull(formatter);
        Objects.requireNonNull(type);
        
        this.formatter = (DateFormat) formatter.clone();
        this.type = type;
        
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
     * @param type 日時型のクラスタイプ。
     * @throws NullPointerException if dateClass is null.
     */
    public DateFormatWrapper(final Class<T> type) {
        Objects.requireNonNull(type);
        this.type = type;
        
        final String pattern;
        if(Timestamp.class.isAssignableFrom(type)) {
            pattern = "yyyy-MM-dd HH:mm:ss.SSS";
            
        } else if(Time.class.isAssignableFrom(type)) {
            pattern = "HH:mm:ss";
            
        } else if(java.sql.Date.class.isAssignableFrom(type)) {
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
    @Override
    public synchronized String print(final Date date) {
        return formatter.format(date);
        
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public synchronized T parse(final String text) throws TextParseException {
        
        final Date date;
        try {
            date = formatter.parse(text);
        } catch (ParseException e) {
            throw new TextParseException(text, Date.class);
        }
        
        if(Timestamp.class.isAssignableFrom(type)) {
            return (T)new Timestamp(date.getTime());
            
        } else if(Time.class.isAssignableFrom(type)) {
            return (T)new Time(date.getTime());
            
        } else if(java.sql.Date.class.isAssignableFrom(type)) {
            return (T)new java.sql.Date(date.getTime());
            
        } else if(Date.class.isAssignableFrom(type)) {
            return (T)date;
            
        } else {
            throw new TextParseException(text, type, "Cannot support type.");
        }
    }
    
    @Override
    public Optional<String> getPattern() {
        
        if(formatter instanceof SimpleDateFormat) {
            SimpleDateFormat sdf = (SimpleDateFormat) formatter;
            return Optional.of(sdf.toPattern());
        }
        
        return Optional.empty();
        
    }
    
    @Override
    public Map<String, Object> getMessageVariables() {
        
        final Map<String, Object> vars = new HashMap<>();
        getPattern().ifPresent(p -> vars.put("pattern", p));
        return vars;
    }
    
}

package com.github.mygreen.supercsv.cellprocessor.format;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.chrono.JapaneseDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import com.github.mygreen.supercsv.util.Utils;

/**
 * Date and Time APIの{@link DateTimeFormatter}をラップしたクラス。
 *
 * @version 2.1
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class TemporalFormatWrapper<T extends TemporalAccessor> extends AbstractTextFormatter<T> {
    
    private final DateTimeFormatter formatter;
    
    private final Class<T> type;
    
    private final Method parseMethod;
    
    private String pattern;
    
    /**
     * 
     * @param formatter ラップする{@link DateTimeFormatter}を指定します。
     * @param type {@link TemporalAccessor}の実装クラスを指定します。
     * @throws NullPointerException {@literal if formatter or type is null.}
     * @throws IllegalArgumentException {@literal type is not support class type.}
     */
    public TemporalFormatWrapper(final DateTimeFormatter formatter, final Class<T> type) {
        Objects.requireNonNull(formatter);
        Objects.requireNonNull(type);
        
        this.formatter = formatter;
        this.type = type;
        
        
        try {
            this.parseMethod = type.getMethod("parse", CharSequence.class, DateTimeFormatter.class);
            
        } catch (NoSuchMethodException | SecurityException e) {
            throw new IllegalArgumentException(String.format("Cannot suuport type : %s.", type.getName()));
        }
        
    }
    
    /**
     * {@inheritDoc}
     * 
     * サポートしていないクラスタイプの場合、例外{@link TextParseException}がスローされます。
     */
    @SuppressWarnings("unchecked")
    @Override
    public T parse(final String text) {
        
        try {
            return (T) parseMethod.invoke(type, text, formatter);
            
        } catch(IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new TextParseException(text, type, "Cannot suuport type.");
            
        } catch(DateTimeParseException e) {
            throw new TextParseException(text, type, e);
        }
        
    }
    
    @Override
    public String print(final T object) {
        
        if(pattern.contains("G") && formatter.getLocale().getLanguage().equals("ja")) {
            if(LocalDate.class.isAssignableFrom(type)) {
                JapaneseDate date = JapaneseDate.from((LocalDate)object);
                return formatter.format(date);
            }
        }
        
        
        return formatter.format(object);
    }
    
    @Override
    public Optional<String> getPattern() {
        if(Utils.isEmpty(pattern)) {
            return Optional.empty();
        } else {
            return Optional.of(pattern);
        }
    }
    
    /**
     * パースする際のエラーメッセージ中に利用する書式を指定します。
     * @param pattern コンストラクタで指定した{@link DateTimeFormatter}に対応する書式を指定します。
     */
    public void setPattern(String pattern) {
        this.pattern = pattern;
    }
    
    @Override
    public Map<String, Object> getMessageVariables() {
        final Map<String, Object> vars = new HashMap<>();
        getPattern().ifPresent(p -> vars.put("pattern", p));
        
        return vars;
    }
    
}

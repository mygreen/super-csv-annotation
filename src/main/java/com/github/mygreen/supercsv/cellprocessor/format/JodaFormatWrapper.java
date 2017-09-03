package com.github.mygreen.supercsv.cellprocessor.format;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.joda.time.ReadableInstant;
import org.joda.time.ReadablePartial;
import org.joda.time.format.DateTimeFormatter;

import com.github.mygreen.supercsv.util.Utils;

/**
 * Joda-Timeのフォーマッタをラップしたクラス。
 *
 * @version 2.1
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class JodaFormatWrapper<T> extends AbstractTextFormatter<T> {
    
    private final DateTimeFormatter formatter;
    
    private final Class<T> type;
    
    private final Method parseMethod;
    
    private String pattern;
    
    /**
     * 
     * @param formatter ラップする{@link DateTimeFormatter}を指定します。
     * @param type {@link ReadablePartial}の実装クラスを指定します。
     * @throws NullPointerException {@literal if formatter or type is null.}
     * @throws IllegalArgumentException {@literal type is not support class type.}
     * 
     */
    public JodaFormatWrapper(final DateTimeFormatter formatter, final Class<T> type) {
        Objects.requireNonNull(formatter);
        Objects.requireNonNull(type);
        
        this.formatter = formatter;
        this.type = type;
        
        try {
            this.parseMethod = type.getMethod("parse", String.class, DateTimeFormatter.class);
            
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
            
        } catch(IllegalAccessException | InvocationTargetException e) {
            throw new TextParseException(text, type, "Cannot suuport type.");
            
        } catch(IllegalArgumentException e) {
            throw new TextParseException(text, type, e);
        }
        
    }
    
    @Override
    public String print(final T object) {
        if(object instanceof ReadablePartial) {
            return formatter.print((ReadablePartial)object);
        }
        
        if(object instanceof ReadableInstant) {
            return formatter.print((ReadableInstant)object);
        }
        
        throw new TextPrintException(object, String.format("Cannot suuport type [%s].", object.getClass().getName()));
    }
    
    @Override
    public Optional<String> getPattern() {
        if(Utils.isEmpty(pattern)) {
            return Optional.empty();
        } else {
            return Optional.of(pattern);
        }
    }
    
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

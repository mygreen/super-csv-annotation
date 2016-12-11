package com.github.mygreen.supercsv.cellprocessor.format;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.joda.time.ReadableInstant;
import org.joda.time.ReadablePartial;
import org.joda.time.format.DateTimeFormatter;

import com.github.mygreen.supercsv.util.Utils;

/**
 * Joda-Timeのフォーマッタをラップしたクラス。
 * <p>{@link LocalDateTime}/{@link LocalDate}/{@link LocalTime}/{@link DateTime}に対応している。</p>
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class JodaFormatWrapper<T> extends AbstractTextFormatter<T> {
    
    private final DateTimeFormatter formatter;
    
    private final Class<T> type;
    
    private String pattern;
    
    /**
     * 
     * @param formatter ラップする{@link DateTimeFormatter}を指定します。
     * @param type {@link ReadablePartial}の実装クラスである、
     *        {@link LocalDateTime}/{@link LocalDate}/{@link LocalTime}の何れかを指定します。
     * @throws NullPointerException {@literal if formatter or type is null.}
     */
    public JodaFormatWrapper(final DateTimeFormatter formatter, final Class<T> type) {
        Objects.requireNonNull(formatter);
        Objects.requireNonNull(type);
        
        this.formatter = formatter;
        this.type = type;
    }
    
    /**
     * {@inheritDoc}
     * 
     * {@link LocalDateTime}/{@link LocalDate}/{@link LocalTime}以外のクラスタイプの場合、
     * 例外{@link TextParseException}がスローされます。
     */
    @SuppressWarnings("unchecked")
    @Override
    public T parse(final String text) {
        
        try {
            if(LocalDateTime.class.isAssignableFrom(type)) {
                return (T) LocalDateTime.parse(text, formatter);
                
            } else if(LocalDate.class.isAssignableFrom(type)) {
                return (T) LocalDate.parse(text, formatter);
                
            } else if(LocalTime.class.isAssignableFrom(type)) {
                return (T) LocalTime.parse(text, formatter);
                
            } else if(DateTime.class.isAssignableFrom(type)) {
                return (T) DateTime.parse(text, formatter);
            }
            
        } catch(IllegalArgumentException e) {
            throw new TextParseException(text, type, e);
        }
        
        throw new TextParseException(text, type, "Cannot suuport type.");
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

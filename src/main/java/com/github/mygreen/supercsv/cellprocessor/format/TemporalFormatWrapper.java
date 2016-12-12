package com.github.mygreen.supercsv.cellprocessor.format;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
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
 * <p>{@link LocalDateTime}/{@link LocalDate}/{@link LocalTime}/{@link ZonedDateTime}に対応している。</p>
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class TemporalFormatWrapper<T extends TemporalAccessor> extends AbstractTextFormatter<T> {
    
    private final DateTimeFormatter formatter;
    
    private final Class<T> type;
    
    private String pattern;
    
    /**
     * 
     * @param formatter ラップする{@link DateTimeFormatter}を指定します。
     * @param type {@link TemporalAccessor}の実装クラスである、
     *        {@link LocalDateTime}/{@link LocalDate}/{@link LocalTime}/{@link ZonedDateTime}の何れかを指定します。
     * @throws NullPointerException {@literal if formatter or type is null.}
     */
    public TemporalFormatWrapper(final DateTimeFormatter formatter, final Class<T> type) {
        Objects.requireNonNull(formatter);
        Objects.requireNonNull(type);
        
        this.formatter = formatter;
        this.type = type;
    }
    
    /**
     * {@inheritDoc}
     * 
     * {@link LocalDateTime}/{@link LocalDate}/{@link LocalTime}/{@link ZonedDateTime}以外のクラスタイプの場合、
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
                
            } else if(ZonedDateTime.class.isAssignableFrom(type)) {
                return (T) ZonedDateTime.parse(text, formatter);
                
            }
        } catch(DateTimeParseException e) {
            throw new TextParseException(text, type, e);
        }
        
        throw new TextParseException(text, type, "Cannot suuport type.");
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

package com.github.mygreen.supercsv.cellprocessor.format;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * 書式を指定した数値のフォーマッタ。
 * 
 * @since 1.2
 * @author T.TSUCHIE
 *
 */
public class NumberFormatWrapper<T extends Number> extends AbstractTextFormatter<T> {
    
    private final NumberFormat formatter;
    
    private final Class<T> type;
    
    private final boolean lenient;
    
    public NumberFormatWrapper(final NumberFormat formatter, final Class<T> type) {
        this(formatter, type, false);
    }
    
    public NumberFormatWrapper(final NumberFormat formatter, final Class<T> type, final boolean lenient) {
        Objects.requireNonNull(formatter);
        Objects.requireNonNull(type);
        
        this.formatter = (NumberFormat) formatter.clone();
        this.type = type;
        this.lenient = lenient;
        
    }
    
    @Override
    public synchronized String print(final Number number) {
        return formatter.format(number);
    }
    
    @Override
    public T parse(final String text) {
        return parse(type, text);
    }
    
    /**
     * 指定した数値のクラスに文字列をパースする。
     * <p>Java標準のクラスタイプをサポートします。</p>
     * 
     * @param type 変換する数値のクラス。
     * @param text パース対象の文字列。
     * @return パースした数値のオブジェクト。
     * @throws IllegalArgumentException サポートしていないクラスタイプが指定された場合。
     * @throws TextParseException fail convert Number or BigDecimal.
     */
    @SuppressWarnings("unchecked")
    synchronized <N extends Number> N parse(final Class<N> type, final String text) {
        
        final Number result;
        if(lenient) {
            try {
                result = formatter.parse(text);
            } catch(ParseException e) {
                throw new TextParseException(text, type, e);
            }
        } else {
            ParsePosition position = new ParsePosition(0);
            result = formatter.parse(text, position);
            
            if(position.getIndex() != text.length()) {
                throw new TextParseException(text, type, String.format("Cannot parse '%s' using fromat %s", text, getPattern()));
            }
        }
        
        try {
            if(result instanceof BigDecimal) {
                // if set DecimalFormat#setParseBigDecimal(true)
                return (N) convertWithBigDecimal(type, (BigDecimal) result, text);
                
            } else {
                return (N) convertWithNumber(type, result, text);
            }
        } catch(NumberFormatException | ArithmeticException e) {
            throw new TextParseException(text, type, e);
        }
        
    }
    
    private Number convertWithNumber(final Class<? extends Number> type, final Number number, final String str) {
        
        if(Byte.class.isAssignableFrom(type) || byte.class.isAssignableFrom(type)) {
            return number.byteValue();
            
        } else if(Short.class.isAssignableFrom(type) || short.class.isAssignableFrom(type)) {
            return number.shortValue() ;
            
        } else if(Integer.class.isAssignableFrom(type) || int.class.isAssignableFrom(type)) {
            return number.intValue();
            
        } else if(Long.class.isAssignableFrom(type) || long.class.isAssignableFrom(type)) {
            return number.longValue();
            
        } else if(Float.class.isAssignableFrom(type) || float.class.isAssignableFrom(type)) {
            return number.floatValue();
            
        } else if(Double.class.isAssignableFrom(type) || double.class.isAssignableFrom(type)) {
            return number.doubleValue();
            
        } else if(type.isAssignableFrom(BigInteger.class)) {
            return new BigInteger(str);
            
        } else if(type.isAssignableFrom(BigDecimal.class)) {
            return new BigDecimal(str);
            
        }
        
        throw new IllegalArgumentException(String.format("not support class type : %s", type.getCanonicalName()));
    }
    
    private Number convertWithBigDecimal(final Class<? extends Number> type, final BigDecimal number, final String str) {
        
        if(Byte.class.isAssignableFrom(type) || byte.class.isAssignableFrom(type)) {
            return lenient ? number.byteValue() : number.byteValueExact();
            
        } else if(Short.class.isAssignableFrom(type) || short.class.isAssignableFrom(type)) {
            return lenient ? number.shortValue() : number.shortValueExact();
            
        } else if(Integer.class.isAssignableFrom(type) || int.class.isAssignableFrom(type)) {
            return lenient ? number.intValue() : number.intValueExact();
            
        } else if(Long.class.isAssignableFrom(type) || long.class.isAssignableFrom(type)) {
            return lenient ? number.longValue() : number.longValueExact();
            
        } else if(Float.class.isAssignableFrom(type) || float.class.isAssignableFrom(type)) {
            return number.floatValue();
            
        } else if(Double.class.isAssignableFrom(type) || double.class.isAssignableFrom(type)) {
            return number.doubleValue();
            
        } else if(type.isAssignableFrom(BigInteger.class)) {
            return lenient ? number.toBigInteger() : number.toBigIntegerExact();
            
        } else if(type.isAssignableFrom(BigDecimal.class)) {
            return number;
            
        }
        
        throw new IllegalArgumentException(String.format("not support class type : %s", type.getCanonicalName()));
        
    }
    
    @Override
    public Optional<String> getPattern() {
        
        if(formatter instanceof DecimalFormat) {
            DecimalFormat df = (DecimalFormat) formatter;
            return Optional.of(df.toPattern());
        }
        
        return Optional.empty();
        
    }
    
    /**
     * パースする際に、数値に変換可能な部分のみ変換するかどうか。
     * <p>例えば、trueのときは、{@literal 123abc} をパースする際に{@literal 123}を数値としてパースします。
     *   <br>falseの場合は、例外{@link TextParseException}をスローします。
     * </p>
     * @return trueの場合、曖昧にパースします。
     */
    public boolean isLenient() {
        return lenient;
    }
    
    @Override
    public Map<String, Object> getMessageVariables() {
        
        final Map<String, Object> vars = new HashMap<>();
        getPattern().ifPresent(p -> vars.put("pattern", p));
        
        return vars;
    }
    
}

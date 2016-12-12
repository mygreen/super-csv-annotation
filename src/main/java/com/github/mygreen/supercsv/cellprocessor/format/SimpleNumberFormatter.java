package com.github.mygreen.supercsv.cellprocessor.format;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 書式がない数値のフォーマッタ。
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class SimpleNumberFormatter<T extends Number> extends AbstractTextFormatter<T> {
    
    private final Class<T> type;
    
    private final boolean lenient;
    
    private final MathContext mathContext;
    
    /**
     * デフォルトコンストラクタ
     * @param type 数値のクラスタイプ
     * @param lenient 曖昧にパースするかどうか。
     * @throws NullPointerException {@literal type is null.}
     * 
     */
    public SimpleNumberFormatter(final Class<T> type, final boolean lenient) {
        this(type, lenient, null);
        
    }
    
    /**
     * デフォルトコンストラクタ
     * @param type 数値のクラスタイプ
     * @param lenient 曖昧にパースするかどうか。
     * @param mathContext 丸めの方法を指定します。nullを渡すと省略できます。
     * @throws NullPointerException {@literal type is null.}
     * 
     */
    public SimpleNumberFormatter(final Class<T> type, final boolean lenient, final MathContext mathContext) {
        Objects.requireNonNull(type);
        
        this.type = type;
        this.lenient = lenient;
        this.mathContext = mathContext;
        
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public T parse(final String text) {
        
        try {
            final BigDecimal number = mathContext == null ? new BigDecimal(text) : new BigDecimal(text, mathContext);
            return (T) parseFromBigDecimal(type, number);
            
        } catch(NumberFormatException | ArithmeticException e) {
            throw new TextParseException(text, type, e);
        }
    }
    
    private Number parseFromBigDecimal(final Class<? extends Number> type, final BigDecimal number) {
        
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
        
        throw new IllegalArgumentException(String.format("Not support class type : %s", type.getCanonicalName()));
        
    }
    
    @Override
    public String print(final T object) {
        
        if(mathContext != null) {
            return printNumber(object);
        }
        
        return object.toString();
    }
    
    private String printNumber(final Object object) {
        
        if(Byte.class.isAssignableFrom(type) || byte.class.isAssignableFrom(type)) {
            return new BigDecimal((byte)object, mathContext).toPlainString();
            
        } else if(Short.class.isAssignableFrom(type) || short.class.isAssignableFrom(type)) {
            return new BigDecimal((short)object, mathContext).toPlainString();
            
        } else if(Integer.class.isAssignableFrom(type) || int.class.isAssignableFrom(type)) {
            return new BigDecimal((int)object, mathContext).toPlainString();
            
        } else if(Long.class.isAssignableFrom(type) || long.class.isAssignableFrom(type)) {
            return new BigDecimal((long)object, mathContext).toPlainString();
            
        } else if(Float.class.isAssignableFrom(type) || float.class.isAssignableFrom(type)) {
            return new BigDecimal((float)object, mathContext).toPlainString();
            
        } else if(Double.class.isAssignableFrom(type) || double.class.isAssignableFrom(type)) {
            return new BigDecimal((double)object, mathContext).toPlainString();
            
        } else if(BigInteger.class.isAssignableFrom(type)) {
            return new BigDecimal((BigInteger)object, mathContext).toPlainString();
            
        } else {
            return object.toString();
        }
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
    
    /**
     * パースする際の数値の丸め方法の指定情報を取得します。
     * @return 数値の丸め方法
     */
    public MathContext getMathContext() {
        return mathContext;
    }
    
    @Override
    public Map<String, Object> getMessageVariables() {
        
        final Map<String, Object> vars = new HashMap<>();
        
        if(mathContext != null) {
            vars.put("precision", mathContext.getPrecision());
        }
        
        return vars;
    }
    
}

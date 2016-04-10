package org.supercsv.ext.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.ParsePosition;

/**
 * Thread safe number formatter.
 * 
 * @since 1.2
 * @author T.TSUCHIE
 *
 */
public class NumberFormatWrapper {
    
    private final NumberFormat formatter;
    
    private final boolean lenient;
    
    public NumberFormatWrapper(final NumberFormat formatter) {
        this(formatter, false);
    }
    
    public NumberFormatWrapper(final NumberFormat formatter, final boolean lenient) {
        
        if(formatter == null) {
            throw new NullPointerException("formatter should not be null.");
        }
        
        this.formatter = (NumberFormat) formatter.clone();
        this.lenient = lenient;
        
    }
    
    public synchronized <N extends Number> String format(final N number) {
        return formatter.format(number);
    }
    
    /**
     * 
     * @param type
     * @param value
     * @return
     * @throws ParseException fail parse string to Number.
     * @throws ConversionException fail convert Number or BigDecimal.
     */
    @SuppressWarnings("unchecked")
    public synchronized <N extends Number> N parse(final Class<N> type, final String value) throws ParseException, ConversionException {
        
        final Number result;
        if(lenient) {
           result = formatter.parse(value);
        } else {
            ParsePosition position = new ParsePosition(0);
            result = formatter.parse(value, position);
            
            if(position.getIndex() != value.length()) {
                throw new ParseException(
                        String.format("Cannot parse '%s' using fromat %s", value, getPattern()), position.getIndex());
            }
        }
        
        try {
            if(result instanceof BigDecimal) {
                // if set DecimalFormat#setParseBigDecimal(true)
                return (N) convertWithBigDecimal(type, (BigDecimal) result, value);
                
            } else {
                return (N) convertWithNumber(type, result, value);
            }
        } catch(NumberFormatException | ArithmeticException e) {
            throw new ConversionException(value, type, e);
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
    
    public String getPattern() {
        
        if(formatter instanceof DecimalFormat) {
            DecimalFormat df = (DecimalFormat) formatter;
            return df.toPattern();
        }
        
        return null;
        
    }
    
    public boolean isLenient() {
        return lenient;
    }
    
}

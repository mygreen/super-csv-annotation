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
    
    private final ThreadLocal<NumberFormat> formatter;
    
    private final boolean lenient;
    
    public NumberFormatWrapper(final NumberFormat formatter) {
        this(formatter, false);
    }
    
    public NumberFormatWrapper(final NumberFormat formatter, final boolean lenient) {
        
        if(formatter == null) {
            throw new NullPointerException("formatter should not be null.");
        }
        
        this.formatter = new ThreadLocal<NumberFormat>() {
            
            @Override
            protected NumberFormat initialValue() {
                return formatter;
            }
            
        };
        
        this.lenient = lenient;
        
    }
    
    public String format(final Number number) {
        return formatter.get().format(number);
    }
    
    public <N extends Number> Number parse(final Class<N> type, final String value) throws ParseException {
        
        
        final Number result;
        if(lenient) {
           result = formatter.get().parse(value);
        } else {
            ParsePosition position = new ParsePosition(0);
            result = formatter.get().parse(value, position);
            
            if(position.getIndex() != value.length()) {
                throw new ParseException(
                        String.format("Cannot parse '%s' using fromat %s", value, getPattern()), position.getIndex());
            }
        }
        
        if(Byte.class.isAssignableFrom(type) || byte.class.isAssignableFrom(type)) {
            return result.byteValue();
            
        } else if(Short.class.isAssignableFrom(type) || short.class.isAssignableFrom(type)) {
            return result.shortValue() ;
            
        } else if(Integer.class.isAssignableFrom(type) || int.class.isAssignableFrom(type)) {
            return result.intValue();
            
        } else if(Long.class.isAssignableFrom(type) || long.class.isAssignableFrom(type)) {
            return result.longValue();
            
        } else if(Float.class.isAssignableFrom(type) || float.class.isAssignableFrom(type)) {
            return result.floatValue();
            
        } else if(Double.class.isAssignableFrom(type) || double.class.isAssignableFrom(type)) {
            return result.doubleValue();
            
        } else if(type.isAssignableFrom(BigInteger.class)) {
            return new BigInteger(result.toString());
            
        } else if(type.isAssignableFrom(BigDecimal.class)) {
            return new BigDecimal(result.toString());
            
        }
        
        throw new IllegalArgumentException(String.format("not support class type : %s", type.getCanonicalName()));
        
    }
    
    public String getPattern() {
        
        NumberFormat nf = formatter.get();
        if(nf instanceof DecimalFormat) {
            DecimalFormat df = (DecimalFormat) nf;
            return df.toPattern();
        }
        
        return null;
        
    }
    
}

package org.supercsv.ext.util;

/**
 *
 * @since 1.2
 * @author T.TSUCHIE
 *
 */
public class ConversionException extends Exception {
    
    /** serialVersionUID */
    private static final long serialVersionUID = 7389770363413465673L;
    
    private final Object fromValue;
    
    private final Class<?> toType;
    
    public ConversionException(final Object fromValue, final Class<?> toType, final Throwable e) {
        
        super(String.format("fail convert from '%s' to type '%s'", fromValue.toString(), toType.getCanonicalName()), e);
        this.fromValue = fromValue;
        this.toType = toType;
    }
    
    public Object getFromValue() {
        return fromValue;
    }
    
    public Class<?> getToType() {
        return toType;
    }
    
}

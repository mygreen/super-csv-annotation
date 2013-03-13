/*
 * ParseEnum.java
 * created in 2013/03/06
 *
 * (C) Copyright 2003-2013 GreenDay Project. All rights reserved.
 */
package org.supercsv.ext.cellprocessor;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;
import org.supercsv.exception.SuperCsvCellProcessorException;
import org.supercsv.util.CsvContext;


/**
 *
 *
 * @author T.TSUCHIE
 *
 */
@SuppressWarnings("rawtypes")
public class ParseEnum extends CellProcessorAdaptor implements StringCellProcessor {
    
    final Class type;
    
    final boolean lenient;
    
    protected final Map<String, Enum> enumValueMap;
    
    public ParseEnum(final Class type) {
        this(type, false);
    }
    
    public ParseEnum(final Class type, final CellProcessor next) {
        this(type, false, next);
    }
    
    public ParseEnum(final Class type, final boolean lenient) {
        super();
        checkPreconditions(type);
        this.type = type;
        this.lenient = lenient;
        this.enumValueMap = createEnumMap(type, lenient);
    }
    
    public ParseEnum(final Class type, final boolean lenient, final CellProcessor next) {
        super(next);
        checkPreconditions(type);
        this.type = type;
        this.lenient = lenient;
        this.enumValueMap = createEnumMap(type, lenient);
    }
    
    private static void checkPreconditions(final Class type) {
        
        if(type == null) {
            throw new NullPointerException("type should be not null");
        }
        
        if(!Enum.class.isAssignableFrom(type)) {
            throw new IllegalArgumentException(String.format("type should be Enum class : %s", type.getCanonicalName()));
        }
    }
    
    @SuppressWarnings("unchecked")
    protected Map<String, Enum> createEnumMap(final Class enumClass, final boolean lenient) {
        
        Map<String, Enum> map = new LinkedHashMap<String, Enum>();
        EnumSet set = EnumSet.allOf(enumClass);
        for(Iterator<Enum> it = set.iterator(); it.hasNext(); ) {
            Enum e = it.next();
            
            final String key = (lenient ? e.name().toLowerCase() : e.name());
            map.put(key, e);            
        }
        
        return Collections.unmodifiableMap(map);
    }
    
    @Override
    public Object execute(final Object value, final CsvContext context) {
        
        validateInputNotNull(value, context);
        
        final Enum result;
        if(value instanceof Enum && value.getClass().isAssignableFrom(type)) {
            result = (Enum) value;
            
        } else if(value instanceof String) {
            final String stringValue = (lenient ? ((String) value).toLowerCase() : (String) value);
            result = enumValueMap.get(stringValue);
            if(result == null) {
                throw new SuperCsvCellProcessorException(
                        String.format("'%s' could not be parsed as an Enum", value), context, this);
            }
            
        } else {
            final String actualClassName = value.getClass().getName();
            throw new SuperCsvCellProcessorException(String.format(
                "the input value should be of type Enum or String but is of type %s", actualClassName), context, this);
        }
        
        return next.execute(result, context);
    }
    
    public Class<?> getType() {
        return type;
    }
    
    public boolean isLenient() {
        return lenient;
    }
}

/*
 * ParseEnum.java
 * created in 2013/03/06
 *
 * (C) Copyright 2003-2013 GreenDay Project. All rights reserved.
 */
package org.supercsv.ext.cellprocessor;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;
import org.supercsv.exception.SuperCsvCellProcessorException;
import org.supercsv.ext.Utils;
import org.supercsv.ext.cellprocessor.ift.ValidationCellProcessor;
import org.supercsv.util.CsvContext;


/**
 *
 *
 * @author T.TSUCHIE
 *
 */
@SuppressWarnings("rawtypes")
public class ParseEnum extends CellProcessorAdaptor 
        implements StringCellProcessor, ValidationCellProcessor {
    
    protected final Class type;
    
    protected final boolean lenient;
    
    protected final Map<String, Enum> enumValueMap;
    
    protected final Method valueMethod;
    
    public ParseEnum(final Class type) {
        this(type, false);
    }
    
    public ParseEnum(final Class type, final CellProcessor next) {
        this(type, false, next);
    }
    
    public ParseEnum(final Class type, final String valueMethodName) {
        this(type, false, valueMethodName);
    }
    
    public ParseEnum(final Class type, final String valueMethodName, final CellProcessor next) {
        this(type, false, valueMethodName, next);
    }
    
    public ParseEnum(final Class type, final boolean lenient) {
        super();
        checkPreconditions(type);
        this.type = type;
        this.lenient = lenient;
        this.enumValueMap = createEnumMap(type, lenient);
        this.valueMethod = null;
    }
    
    public ParseEnum(final Class type, final boolean lenient, final CellProcessor next) {
        super(next);
        checkPreconditions(type);
        this.type = type;
        this.lenient = lenient;
        this.enumValueMap = createEnumMap(type, lenient);
        this.valueMethod = null;
    }
    
    public ParseEnum(final Class type, final boolean lenient, final String valueMethodName) {
        super();
        checkPreconditions(type);
        this.type = type;
        this.lenient = lenient;
        this.enumValueMap = createEnumMap(type, lenient, valueMethodName);
        this.valueMethod = createEnumValueMethod(type, valueMethodName);
    }
    
    public ParseEnum(final Class type, final boolean lenient, final String valueMethodName, final CellProcessor next) {
        super(next);
        checkPreconditions(type);
        this.type = type;
        this.lenient = lenient;
        this.enumValueMap = createEnumMap(type, lenient, valueMethodName);
        this.valueMethod = createEnumValueMethod(type, valueMethodName);
    }
    
    protected static void checkPreconditions(final Class type) {
        
        if(type == null) {
            throw new NullPointerException("type should be not null");
        }
        
        if(!Enum.class.isAssignableFrom(type)) {
            throw new IllegalArgumentException(String.format("type should be Enum class : %s", type.getCanonicalName()));
        }
    }
    
    @SuppressWarnings("unchecked")
    protected Method createEnumValueMethod(final Class enumClass, final String valueMethodName) {
        try {
            return enumClass.getMethod(valueMethodName);
        } catch (SecurityException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(String.format("not found method '%s'", valueMethodName), e);
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
    
    @SuppressWarnings("unchecked")
    protected Map<String, Enum> createEnumMap(final Class enumClass, final boolean lenient, final String methodName) {
        
        Map<String, Enum> map = new LinkedHashMap<String, Enum>();
        try {
            final Method method = createEnumValueMethod(enumClass, methodName);
            
            EnumSet set = EnumSet.allOf(enumClass);
            for(Iterator<Enum> it = set.iterator(); it.hasNext(); ) {
                Enum e = it.next();
                
                Object returnValue = method.invoke(e);
                final String key = (lenient ? returnValue.toString().toLowerCase() : returnValue.toString());
                
                map.put(key, e);            
            }
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
        
        return Collections.unmodifiableMap(map);
    }
    
    @Override
    public Object execute(final Object value, final CsvContext context) {
        
        validateInputNotNull(value, context);
        
        final Enum result;
        if(value instanceof String) {
            final String stringValue = (lenient ? ((String) value).toLowerCase() : (String) value);
            result = enumValueMap.get(stringValue);
            if(result == null) {
                throw new SuperCsvCellProcessorException(
                        String.format("'%s' could not be parsed as an Enum", value), context, this);
            }
            
        } else if(value instanceof Enum && value.getClass().isAssignableFrom(type)) {
            result = (Enum) value;
            
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
    
    public Map<String, Enum> getEnumValueMap() {
        return enumValueMap;
    }
    
    public Method getValueMethod() {
        return valueMethod;
    }

    @Override
    public String getMessageCode() {
        return ParseEnum.class.getCanonicalName() + ".violated";
    }

    @Override
    public Map<String, ?> getMessageVariable() {
        final Map<String, Object> vars = new HashMap<String, Object>();
        vars.put("type", getType().getCanonicalName());
        vars.put("valueMethod", getValueMethod() == null ? "" : getValueMethod().getName());
        
        final List<String> enumValues = new ArrayList<String>();
        for(Map.Entry<String, Enum> entry : getEnumValueMap().entrySet()) {
            enumValues.add(entry.getValue().name());
        }
        vars.put("enumValues", enumValues);
        vars.put("enumsStr", Utils.join(enumValues, ", "));
        return vars;
    }

    @Override
    public String formatValue(final Object value) {
        if(value == null) {
            return "";
        }
        return value.toString();
    }
}

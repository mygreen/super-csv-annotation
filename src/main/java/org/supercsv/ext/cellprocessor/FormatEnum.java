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
import org.supercsv.cellprocessor.ift.StringCellProcessor;
import org.supercsv.exception.SuperCsvCellProcessorException;
import org.supercsv.ext.cellprocessor.ift.ValidationCellProcessor;
import org.supercsv.util.CsvContext;


/**
 *
 *
 * @author T.TSUCHIE
 *
 */
@SuppressWarnings("rawtypes")
public class FormatEnum extends CellProcessorAdaptor implements ValidationCellProcessor {
    
    protected final Class type;
    
    protected final Map<Enum, String> enumValueMap;
    
    protected final Method valueMethod;
    
    public FormatEnum(final Class type) {
        super();
        checkPreconditions(type);
        this.type = type;
        this.enumValueMap = createEnumMap(type);
        this.valueMethod = null;
    }
    
    public FormatEnum(final Class type, final StringCellProcessor next) {
        super(next);
        checkPreconditions(type);
        this.type = type;
        this.enumValueMap = createEnumMap(type);
        this.valueMethod = null;
    }
    
    
    public FormatEnum(final Class type, final String valueMethodName) {
        super();
        checkPreconditions(type);
        this.type = type;
        this.enumValueMap = createEnumMap(type, valueMethodName);
        this.valueMethod = createEnumValueMethod(type, valueMethodName);
    }
    
    public FormatEnum(final Class type, final String valueMethodName, final StringCellProcessor next) {
        super(next);
        checkPreconditions(type);
        this.type = type;
        this.enumValueMap = createEnumMap(type, valueMethodName);
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
    protected Map<Enum, String> createEnumMap(final Class enumClass) {
        
        Map<Enum, String> map = new LinkedHashMap<Enum, String>();
        EnumSet set = EnumSet.allOf(enumClass);
        for(Iterator<Enum> it = set.iterator(); it.hasNext(); ) {
            Enum e = it.next();
            
            map.put(e, e.name());            
        }
        
        return Collections.unmodifiableMap(map);
    }
    
    @SuppressWarnings("unchecked")
    protected Map<Enum, String> createEnumMap(final Class enumClass, final String methodName) {
        
        Map<Enum, String> map = new LinkedHashMap<Enum, String>();
        try {
            final Method method = createEnumValueMethod(enumClass, methodName);
            
            EnumSet set = EnumSet.allOf(enumClass);
            for(Iterator<Enum> it = set.iterator(); it.hasNext(); ) {
                Enum e = it.next();
                
                Object returnValue = method.invoke(e);
                map.put(e, returnValue.toString());            
            }
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
        
        return Collections.unmodifiableMap(map);
    }
    
    @Override
    public Object execute(final Object value, final CsvContext context) {
        
        validateInputNotNull(value, context);
        
        final String result;
        if(value instanceof Enum && value.getClass().isAssignableFrom(type)) {
            
            result = getEnumValueMap().get((Enum) value);
            if(result == null) {
                throw new SuperCsvCellProcessorException(
                        String.format("'%s' could not be format as an Enum", value), context, this);
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
    
    public Map<Enum, String> getEnumValueMap() {
        return enumValueMap;
    }
    
    public Method getValueMethod() {
        return valueMethod;
    }

    @Override
    public String getMessageCode() {
        return FormatEnum.class.getCanonicalName() + ".violated";
    }

    @Override
    public Map<String, ?> getMessageVariable() {
        Map<String, Object> vars = new HashMap<String, Object>();
        vars.put("type", getType().getCanonicalName());
        vars.put("valueMethod", getValueMethod().getName());
        
        List<String> enumValues = new ArrayList<String>();
        for(Map.Entry<Enum, String> entry : getEnumValueMap().entrySet()) {
            enumValues.add(entry.getKey().name());
        }
        vars.put("enumValues", enumValues);
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

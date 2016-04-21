package org.supercsv.ext.cellprocessor;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;
import org.supercsv.exception.SuperCsvCellProcessorException;
import org.supercsv.ext.cellprocessor.ift.ValidationCellProcessor;
import org.supercsv.util.CsvContext;


/**
 *
 * @version 1.2
 * @author T.TSUCHIE
 *
 */
public class ParseEnum extends CellProcessorAdaptor 
        implements StringCellProcessor, ValidationCellProcessor {
    
    protected final Class<? extends Enum<?>> type;
    
    protected final boolean ignoreCase;
    
    protected final Map<String, Enum<?>> enumValueMap;
    
    protected final Method valueMethod;
    
    public <T extends Enum<T>> ParseEnum(final Class<T> type) {
        this(type, false);
    }
    
    public <T extends Enum<T>> ParseEnum(final Class<T> type, final CellProcessor next) {
        this(type, false, next);
    }
    
    public <T extends Enum<T>> ParseEnum(final Class<T> type, final String valueMethodName) {
        this(type, false, valueMethodName);
    }
    
    public <T extends Enum<T>> ParseEnum(final Class<T> type, final String valueMethodName, final CellProcessor next) {
        this(type, false, valueMethodName, next);
    }
    
    public <T extends Enum<T>> ParseEnum(final Class<T> type, final boolean ignoreCase) {
        super();
        checkPreconditions(type);
        this.type = type;
        this.ignoreCase = ignoreCase;
        this.enumValueMap = createEnumMap(type, ignoreCase);
        this.valueMethod = null;
    }
    
    public <T extends Enum<T>> ParseEnum(final Class<T> type, final boolean ignoreCase, final CellProcessor next) {
        super(next);
        checkPreconditions(type);
        this.type = type;
        this.ignoreCase = ignoreCase;
        this.enumValueMap = createEnumMap(type, ignoreCase);
        this.valueMethod = null;
    }
    
    public <T extends Enum<T>> ParseEnum(final Class<T> type, final boolean ignoreCase, final String valueMethodName) {
        super();
        checkPreconditions(type);
        this.type = type;
        this.ignoreCase = ignoreCase;
        this.enumValueMap = createEnumMap(type, ignoreCase, valueMethodName);
        this.valueMethod = getEnumValueMethod(type, valueMethodName);
    }
    
    public <T extends Enum<T>> ParseEnum(final Class<T> type, final boolean ignoreCase, final String valueMethodName, final CellProcessor next) {
        super(next);
        checkPreconditions(type);
        this.type = type;
        this.ignoreCase = ignoreCase;
        this.enumValueMap = createEnumMap(type, ignoreCase, valueMethodName);
        this.valueMethod = getEnumValueMethod(type, valueMethodName);
    }
    
    protected static void checkPreconditions(final Class<?> type) {
        
        if(type == null) {
            throw new NullPointerException("type should be not null");
        }
    }
    
    protected <T extends Enum<T>> Method getEnumValueMethod(final Class<T> enumClass, final String valueMethodName) {
        try {
            final Method method = enumClass.getMethod(valueMethodName);
            method.setAccessible(true);
            return method;
            
        } catch (ReflectiveOperationException e) {
            throw new IllegalArgumentException(String.format("not found method '%s'", valueMethodName), e);
        }
        
    }
    
    protected <T extends Enum<T>> Map<String, Enum<?>> createEnumMap(final Class<T> enumClass, final boolean ignoreCase) {
        
        final EnumSet<T> set = EnumSet.allOf(enumClass);
        
        final Map<String, Enum<?>> map = new LinkedHashMap<>();
        for(T e : set) {
            final String key = (ignoreCase ? e.name().toLowerCase() : e.name());
            map.put(key, e);
            
        }
        
        return Collections.unmodifiableMap(map);
    }
    
    protected <T extends Enum<T>> Map<String, Enum<?>> createEnumMap(final Class<T> enumClass, final boolean ignoreCase,
            final String methodName) {
        
        final Method method = getEnumValueMethod(enumClass, methodName);
        
        final Map<String, Enum<?>> map = new LinkedHashMap<>();
        try {
            
            EnumSet<T> set = EnumSet.allOf(enumClass);
            for(T e : set) {
                Object returnValue = method.invoke(e);
                final String key = (ignoreCase ? returnValue.toString().toLowerCase() : returnValue.toString());
                
                map.put(key, e);  
            }
            
        } catch(ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
        
        return Collections.unmodifiableMap(map);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public Object execute(final Object value, final CsvContext context) {
        
        validateInputNotNull(value, context);
        
        if( !(value instanceof String) ) {
            throw new SuperCsvCellProcessorException(String.class, value, context, this);
        }
        
        final String stringValue = (ignoreCase ? ((String)value).toLowerCase() : ((String)value));
        final Enum<?> result = enumValueMap.get(stringValue);
        
        if(result == null) {
            throw new SuperCsvCellProcessorException(
                    String.format("'%s' could not be parsed as an Enum", value), context, this);
        }
        
        return next.execute(result, context);
        
    }
    
    public Class<?> getType() {
        return type;
    }
    
    public boolean isIgnoreCase() {
        return ignoreCase;
    }
    
    public Map<String, Enum<?>> getEnumValueMap() {
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
        vars.put("ignoreCase", isIgnoreCase());
        
        final List<Enum<?>> enumValues = getEnumValueMap().entrySet().stream()
                .map(e -> e.getValue())
                .collect(Collectors.toList());
        
        final String enumsStr = getEnumValueMap().entrySet().stream()
                .map(e -> e.getKey())
                .collect(Collectors.joining(", "));
        
        vars.put("enumValues", enumValues);
        vars.put("enumsStr", enumsStr);
        
        return vars;
    }

    @Override
    public String formatValue(final Object value) {
        if(value == null) {
            return "";
        }
        
        if(value.getClass().isAssignableFrom(type)) {
            final Enum<?> enumValue = (Enum<?>) value;
            for(Map.Entry<String, Enum<?>> entry : getEnumValueMap().entrySet()) {
                if(entry.getValue().equals(enumValue)) {
                    return entry.getKey();
                }
            }
        }
        
        return value.toString();
    }
}

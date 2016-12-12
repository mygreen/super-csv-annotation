package com.github.mygreen.supercsv.cellprocessor.format;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Boolean型に対するフォーマッタ。
 * 
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class BooleanFormatter extends AbstractTextFormatter<Boolean> {
    
    private static final String[] DEFAULT_READ_TRUE_VALUES = new String[] {"true", "1", "yes", "on", "y", "t"};
    private static final String[] DEFAULT_READ_FALSE_VALUES = new String[] {"false", "0", "no", "off", "f", "n"};
    
    private static final String DEFAULT_WRITE_TRUE_VALUE = "true";
    private static final String DEFAULT_WRITE_FALSE_VALUE = "false";
    
    private final Set<String> readTrueValues;
    
    private final Set<String> readFalseValues;
    
    private final String writeTrueValue;
    
    private final String writeFalseValue;
    
    private boolean ignoreCase;
    
    private boolean failToFalse;
    
    public BooleanFormatter() {
        this(DEFAULT_READ_TRUE_VALUES, DEFAULT_READ_FALSE_VALUES,
                DEFAULT_WRITE_TRUE_VALUE, DEFAULT_WRITE_FALSE_VALUE,
                false, false);
    }
    
    public BooleanFormatter(final String[] readTrueValues, final String[] readFalseValues,
            final String writeTrueValue, final String writeFalseValue,
            final boolean ignoreCase, boolean failToFalse) {
        
        Objects.requireNonNull(readTrueValues);
        Objects.requireNonNull(readFalseValues);
        
        this.readTrueValues = toSet(readTrueValues);
        this.readFalseValues = toSet(readFalseValues);
        this.writeTrueValue = writeTrueValue;
        this.writeFalseValue = writeFalseValue;
        this.ignoreCase = ignoreCase;
        this.failToFalse = failToFalse;
    }
    
    private static Set<String> toSet(final String[] values) {
        
        Set<String> set = new LinkedHashSet<>();
        Collections.addAll(set, values);
        return Collections.unmodifiableSet(set);
        
    }
    
    @Override
    public Boolean parse(final String text) {
        
        if(contains(readTrueValues, text, ignoreCase) ) {
            return Boolean.TRUE;
            
        } else if(contains(readFalseValues, text, ignoreCase) ) {
            return Boolean.FALSE;
            
        } else {
            if(failToFalse) {
                return Boolean.FALSE;
            } else {
                throw new TextParseException(text, Boolean.class,
                        String.format("'%s' could not be parsed as a Boolean", text));
            }
        }
    }
    
    private static boolean contains(final Set<String> set, final String value, final boolean ignoreCase) {
        
        if(ignoreCase) {
            for(String element : set) {
                if(element.equalsIgnoreCase(value)) {
                    return true;
                }
            }
            
            return false;
        } else {
            return set.contains(value);
        }
        
    }
    
    @Override
    public String print(final Boolean object) {
        
        return object ? writeTrueValue : writeFalseValue;
        
    }
    
    @Override
    public Map<String, Object> getMessageVariables() {
        
        final Map<String, Object> vars = new HashMap<>();
        vars.put("trueValues", readTrueValues);
        vars.put("falseValues", readFalseValues);
        
        vars.put("ignoreCase", ignoreCase);
        vars.put("failToFalse", failToFalse);
        return vars;
        
    }
    
}

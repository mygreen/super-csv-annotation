package com.github.mygreen.supercsv.builder;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

import org.supercsv.exception.SuperCsvReflectionException;
import org.supercsv.util.CsvContext;

import com.github.mygreen.supercsv.validation.CsvBindingErrors;
import com.github.mygreen.supercsv.validation.ValidationContext;

/**
 * 
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class CallbackMethod implements Comparable<CallbackMethod> {
    
    protected final Method method;
    
    public CallbackMethod(final Method method) {
        Objects.requireNonNull(method);
        
        method.setAccessible(true);
        this.method = method;
        
    }
    
    /**
     * メソッドの実行
     * @param record Beanのオブジェクト
     * @param csvContext 現在のCSVのレコード情報
     * @param bindingErrors エラー情報。
     * @param beanMapping マッピング情報
     * @throws SuperCsvReflectionException メソッドの実行に失敗した場合。
     */
    public void invoke(final Object record, final CsvContext csvContext, final CsvBindingErrors bindingErrors, 
            final BeanMapping<?> beanMapping) {
        
        // 引数の組み立て
        final Class<?>[] paramTypes = method.getParameterTypes();
        final Object[] paramValues =  new Object[paramTypes.length];
        
        for(int i=0; i < paramTypes.length; i++) {
            
            if(CsvContext.class.isAssignableFrom(paramTypes[i])) {
                paramValues[i] = csvContext;
                
            } else if(CsvBindingErrors.class.isAssignableFrom(paramTypes[i])) {
                paramValues[i] = bindingErrors;
                
            } else if(paramTypes[i].isArray() && Class.class.isAssignableFrom(paramTypes[i].getComponentType())) {
                paramValues[i] = beanMapping.getGroups();
                
            } else if(ValidationContext.class.isAssignableFrom(paramTypes[i])) {
                paramValues[i] = new ValidationContext<>(csvContext, beanMapping);
                
            } else if(beanMapping.getType().isAssignableFrom(paramTypes[i])) {
                paramValues[i] = record;
                
            } else {
                paramValues[i] = null;
            }
            
        }
        
        execute(record, paramValues);
        
    }
    
    protected void execute(final Object record, final Object[] paramValues) {
        try {
            method.invoke(record, paramValues);
            
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            Throwable t = e.getCause() == null ? e : e.getCause();
            throw new SuperCsvReflectionException(
                    String.format("Fail execute method '%s#%s'.", record.getClass().getName(), method.getName()),
                    t);
        }
        
        
    }
    
    /**
     * メソッド名の昇順
     */
    @Override
    public int compareTo(final CallbackMethod o) {
        
        final String name1 = method.getDeclaringClass().getName() + "#" + method.getName();
        final String name2 = o.method.getDeclaringClass().getName() + "#" + o.method.getName();
        
        return name1.compareTo(name2);
        
    }
    
}

package com.github.mygreen.supercsv.builder;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

import org.supercsv.exception.SuperCsvReflectionException;

/**
 * Listenerクラス用のコールバック用メソッドの実行
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class ListenerCallbackMethod extends CallbackMethod {
    
    private final Object listener;
    
    public ListenerCallbackMethod(final Object listener, final Method method) {
        super(method);
        Objects.requireNonNull(listener);
        this.listener = listener;
    }
    
    @Override
    protected void execute(final Object record, final Object[] paramValues) {
        try {
            method.invoke(listener, paramValues);
            
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            Throwable t = e.getCause() == null ? e : e.getCause();
            throw new SuperCsvReflectionException(
                    String.format("Fail execute method '%s#%s'.", listener.getClass().getName(), method.getName()),
                    t);
        }
        
        
    }
    
}

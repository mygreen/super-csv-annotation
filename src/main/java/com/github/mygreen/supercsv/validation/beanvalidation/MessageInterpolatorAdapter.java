package com.github.mygreen.supercsv.validation.beanvalidation;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import javax.validation.metadata.ConstraintDescriptor;

import org.hibernate.validator.internal.engine.MessageInterpolatorContext;

import com.github.mygreen.supercsv.localization.MessageInterpolator;
import com.github.mygreen.supercsv.localization.MessageResolver;

/**
 * SuperCsvAnnotationの{@link MessageInterpolator}とBeanValidationの{@link javax.validation.MessageInterpolator}をブリッジする。
 * <p>BeanValidationのメッセージ処理をカスタマイズするために利用する。</p>
 *
 * @version 2.2
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class MessageInterpolatorAdapter implements javax.validation.MessageInterpolator {
    
    private final MessageResolver messageResolver;
    
    private final MessageInterpolator csvMessageInterpolator;
    
    /**
     * 
     * @param messageResolver
     * @param csvMessageInterpolator
     * @throws NullPointerException {@literal if messageResolver or csvMessageInterpolator is null.}
     */
    public MessageInterpolatorAdapter(final MessageResolver messageResolver, final MessageInterpolator csvMessageInterpolator) {
        Objects.requireNonNull(messageResolver);
        Objects.requireNonNull(csvMessageInterpolator);
        
        this.messageResolver = messageResolver;
        this.csvMessageInterpolator = csvMessageInterpolator;
    }
    
    @Override
    public String interpolate(final String messageTemplate, final Context context) {
        return csvMessageInterpolator.interpolate(messageTemplate, createMessageVariables(context), true, messageResolver);
    }
    
    @Override
    public String interpolate(final String messageTemplate, final Context context, final Locale locale) {
        return csvMessageInterpolator.interpolate(messageTemplate, createMessageVariables(context), true, messageResolver);
    }
    
    /**
     * メッセージ中で利用可能な変数を作成する
     * @param context コンテキスト
     * @return メッセージ変数のマップ
     */
    private Map<String, Object> createMessageVariables(final Context context) {
        
        final Map<String, Object> vars = new HashMap<>();
        
        if(context instanceof MessageInterpolatorContext) {
            MessageInterpolatorContext mic = (MessageInterpolatorContext)context;
            vars.putAll(mic.getMessageParameters());
        }
        
        final ConstraintDescriptor<?> descriptor = context.getConstraintDescriptor();
        for(Map.Entry<String, Object> entry : descriptor.getAttributes().entrySet()) {
            final String attrName = entry.getKey();
            final Object attrValue = entry.getValue();
            
            vars.put(attrName, attrValue);
        }
        
        // 検証対象の値
        vars.computeIfAbsent("validatedValue", key -> context.getValidatedValue());
        
        // デフォルトのメッセージ
        final String defaultCode = String.format("%s.message", descriptor.getAnnotation().annotationType().getCanonicalName());
        final Optional<String> defaultMessage = messageResolver.getMessage(defaultCode);
        
        vars.put(defaultCode, 
                defaultMessage.orElseThrow(() -> new RuntimeException(String.format("not found message code '%s'", defaultCode))));
        
        
        return vars;
        
    }
    
    public MessageResolver getMessageResolver() {
        return messageResolver;
    }
    
    public MessageInterpolator getCsvMessageInterpolator() {
        return csvMessageInterpolator;
    }
    
}

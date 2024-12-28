package com.github.mygreen.supercsv.validation.beanvalidation;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import com.github.mygreen.supercsv.localization.MessageInterpolator;
import com.github.mygreen.supercsv.localization.MessageResolver;

import jakarta.validation.metadata.ConstraintDescriptor;

/**
 * SuperCsvAnnotationの{@link MessageInterpolator}とBeanValidationの{@link jakarta.validation.MessageInterpolator}をブリッジする。
 * <p>BeanValidationのメッセージ処理をカスタマイズするために利用する。</p>
 *
 * @since 2.4
 * @author T.TSUCHIE
 *
 */
public class JakartaMessageInterpolatorAdapter implements jakarta.validation.MessageInterpolator {
    
    private final MessageResolver messageResolver;
    
    private final MessageInterpolator csvMessageInterpolator;
    
    /**
     * SuperCsvAnnotation用のメッセージソースとInterpolatorを指定してBeanValidationのMessageInterpolatorを作成します。
     * 
     * @param messageResolver SuperCsvAnnotation用のメッセージソース。
     * @param csvMessageInterpolator SuperCsvAnnotation用のMessageInterpolator。
     * @throws NullPointerException {@literal if messageResolver or csvMessageInterpolator is null.}
     */
    public JakartaMessageInterpolatorAdapter(final MessageResolver messageResolver, final MessageInterpolator csvMessageInterpolator) {
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
        
        final ConstraintDescriptor<?> descriptor = context.getConstraintDescriptor();
        for(Map.Entry<String, Object> entry : descriptor.getAttributes().entrySet()) {
            final String attrName = entry.getKey();
            final Object attrValue = entry.getValue();
            
            vars.put(attrName, attrValue);
        }
        
        // 検証対象の値
        vars.computeIfAbsent("validatedValue", key -> context.getValidatedValue());
        
        return vars;
        
    }
    
    public MessageResolver getMessageResolver() {
        return messageResolver;
    }
    
    public MessageInterpolator getCsvMessageInterpolator() {
        return csvMessageInterpolator;
    }
    
}

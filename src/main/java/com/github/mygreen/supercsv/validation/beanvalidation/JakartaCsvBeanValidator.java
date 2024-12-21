package com.github.mygreen.supercsv.validation.beanvalidation;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mygreen.supercsv.builder.ColumnMapping;
import com.github.mygreen.supercsv.localization.MessageInterpolator;
import com.github.mygreen.supercsv.localization.ResourceBundleMessageResolver;
import com.github.mygreen.supercsv.validation.CsvBindingErrors;
import com.github.mygreen.supercsv.validation.CsvFieldError;
import com.github.mygreen.supercsv.validation.CsvValidator;
import com.github.mygreen.supercsv.validation.ValidationContext;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.metadata.ConstraintDescriptor;

/**
 * Jakarata Bean Validaion 3.0/3.1 を利用したValidatorにブリッジする{@link CsvValidator}。
 * 
 * @since 2.4
 * @author T.TSUCHIE
 *
 */
public class JakartaCsvBeanValidator implements CsvValidator<Object> {
    
    private static final Logger logger = LoggerFactory.getLogger(JakartaCsvBeanValidator.class);
    
    /**
     * BeanValidationのアノテーションの属性で、メッセージ中の変数から除外するもの。
     * <p>メッセージの再構築を行う際に必要
     */
    private static final Set<String> EXCLUDE_MESSAGE_ANNOTATION_ATTRIBUTES;
    static {
        Set<String> set = new HashSet<String>(3);
        set.add("message");
        set.add("groups");
        set.add("payload");
        
        EXCLUDE_MESSAGE_ANNOTATION_ATTRIBUTES = Collections.unmodifiableSet(set);
    }
    
    private final Validator targetValidator;
    
    public JakartaCsvBeanValidator(final Validator targetValidator) {
        Objects.requireNonNull(targetValidator);
        this.targetValidator = targetValidator;
    }
    
    public JakartaCsvBeanValidator() {
        this.targetValidator = createDefaultValidator();
    }
    
    /**
     * Bean Validatonのデフォルトのインスタンスを取得する。
     * @return 
     */
    private Validator createDefaultValidator() {
        final ValidatorFactory validatorFactory = Validation.byDefaultProvider().configure()
                .messageInterpolator(new JakartaMessageInterpolatorAdapter(new ResourceBundleMessageResolver(), new MessageInterpolator()))
                .buildValidatorFactory();
        
        final Validator validator = validatorFactory.usingContext()
                .getValidator();
        
        return validator;
    }
    
    /**
     * BeanValidationのValidatorを取得する。
     * @return
     */
    public Validator getTargetValidator() {
        return targetValidator;
    }
    
    @Override
    public void validate(final Object record, final CsvBindingErrors bindingErrors, 
            final ValidationContext<Object> validationContext) {
        validate(record, bindingErrors, validationContext, validationContext.getBeanMapping().getGroups());
        
    }
    
    /**
     * グループを指定して検証を実行する。
     * @param record 検証対象のオブジェクト。
     * @param bindingErrors エラーオブジェクト
     * @param validationContext 入力値検証のためのコンテキスト情報
     * @param groups BeanValiationのグループのクラス
     */
    public void validate(final Object record, final CsvBindingErrors bindingErrors, 
            final ValidationContext<Object> validationContext, final Class<?>... groups) {
        Objects.requireNonNull(record);
        Objects.requireNonNull(bindingErrors);
        Objects.requireNonNull(validationContext);
        
        processConstraintViolation(getTargetValidator().validate(record, groups), bindingErrors, validationContext);
    }
    
    /**
     * BeanValidationの検証結果をSheet用のエラーに変換する
     * @param violations BeanValidationの検証結果
     * @param bindingErrors エラー情報
     * @param validationContext 入力値検証のためのコンテキスト情報
     */
    private void processConstraintViolation(final Set<ConstraintViolation<Object>> violations,
            final CsvBindingErrors bindingErrors, final ValidationContext<Object> validationContext) {
        
        for(ConstraintViolation<Object> violation : violations) {
            
            final String field = violation.getPropertyPath().toString();
            final ConstraintDescriptor<?> cd = violation.getConstraintDescriptor();
            
            final String[] errorCodes = determineErrorCode(cd);
            
            final Map<String, Object> errorVars = createVariableForConstraint(cd);
            
            if(isCsvField(field, validationContext)) {
                // フィールドエラーの場合
                
                final CsvFieldError fieldError = bindingErrors.getFirstFieldError(field);
                if(fieldError != null && fieldError.isProcessingFailure()) {
                    // CellProcessorで発生したエラーが既ににある場合は、処理をスキップする。
                    continue;
                }
                
                final ColumnMapping columnMapping = validationContext.getBeanMapping().getColumnMapping(field).get();
                
                errorVars.put("lineNumber", validationContext.getCsvContext().getLineNumber());
                errorVars.put("rowNumber", validationContext.getCsvContext().getRowNumber());
                errorVars.put("columnNumber", columnMapping.getNumber());
                errorVars.put("label", columnMapping.getLabel());
                errorVars.computeIfAbsent("printer", key -> columnMapping.getFormatter());
                
                // 実際の値を取得する
                final Object fieldValue = violation.getInvalidValue();
                errorVars.computeIfAbsent("validatedValue", key -> fieldValue);
                
                bindingErrors.rejectValue(field, columnMapping.getField().getType(), 
                        errorCodes, errorVars, violation.getMessageTemplate());
                
            } else {
                // オブジェクトエラーの場合
                bindingErrors.reject(errorCodes, errorVars, violation.getMessageTemplate());
                
            }
            
        }
        
        
    }
    
    /**
     * エラーコードを決定する。
     * <p>※ユーザ指定メッセージの場合はエラーコードは空。</p>
     * 
     * @since 2.4
     * @param descriptor フィールド情報
     * @return エラーコード
     */
    protected String[] determineErrorCode(final ConstraintDescriptor<?> descriptor) {
        
        // バリデーション用アノテーションから属性「message」のでデフォルト値を取得し、変更されているかどう比較する。
        String defaultMessage = null;
        try {
            Method messageMethod = descriptor.getAnnotation().annotationType().getMethod("message");
            messageMethod.setAccessible(true);
            defaultMessage = Objects.toString(messageMethod.getDefaultValue(), null);
        } catch (NoSuchMethodException | SecurityException e) {
            logger.warn("Fail getting annotation's attribute 'message' for " + descriptor.getAnnotation().annotationType().getSimpleName() , e);
        }
        
        if(!descriptor.getMessageTemplate().equals(defaultMessage)) {
            /*
             * アノテーション属性「message」の値がデフォルト値から変更されている場合は、
             * ユーザー指定メッセージとして判断し、エラーコードは空にしてユーザー指定メッセージを優先させる。
             */
            return new String[]{};
            
        } else {
            // アノテーションのクラス名をもとに生成する。
            return new String[]{
                    descriptor.getAnnotation().annotationType().getSimpleName(),
                    descriptor.getAnnotation().annotationType().getCanonicalName(),
                    descriptor.getAnnotation().annotationType().getCanonicalName() + ".message"
            };
        }
    }
    
    /**
     * CSVのカラムのフィールドか判定する。
     * @param field フィールド名
     * @param validationContext CSVの検証情報
     * @return trueのとき、CSVのカラムフィールド。
     */
    private boolean isCsvField(final String field, final ValidationContext<Object> validationContext) {
        return validationContext.getBeanMapping().getColumnMapping(field).isPresent();
    }
    
    /**
     * BeanValidationのアノテーションの値を元に、メッセージ変数を作成する。
     * @param descriptor
     * @return メッセージ変数
     */
    private Map<String, Object> createVariableForConstraint(final ConstraintDescriptor<?> descriptor) {
        
        final Map<String, Object> vars = new HashMap<>();
        
        for(Map.Entry<String, Object> entry : descriptor.getAttributes().entrySet()) {
            final String attrName = entry.getKey();
            final Object attrValue = entry.getValue();
            
            // メッセージ変数で必要ないものを除外する
            if(EXCLUDE_MESSAGE_ANNOTATION_ATTRIBUTES.contains(attrName)) {
                continue;
            }
            
            vars.put(attrName, attrValue);
        }
        
        return vars;
        
    }
    
}

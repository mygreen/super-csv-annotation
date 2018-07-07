package com.github.mygreen.supercsv.validation.beanvalidation;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.MessageInterpolator;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.metadata.ConstraintDescriptor;

import org.hibernate.validator.internal.engine.MessageInterpolatorContext;
import org.hibernate.validator.internal.engine.ValidatorImpl;

import com.github.mygreen.supercsv.builder.ColumnMapping;
import com.github.mygreen.supercsv.validation.CsvBindingErrors;
import com.github.mygreen.supercsv.validation.CsvFieldError;
import com.github.mygreen.supercsv.validation.CsvValidator;
import com.github.mygreen.supercsv.validation.ValidationContext;

/**
 * BeanValidaion JSR-303(ver.1.0)/JSR-349(ver.1.1)にブリッジする{@link CsvValidator}。
 * 
 * @version 2.2
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class CsvBeanValidator implements CsvValidator<Object> {
    
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
    
    private final MessageInterpolator messageInterpolator;
    
    public CsvBeanValidator(final Validator targetValidator) {
        Objects.requireNonNull(targetValidator);
        this.targetValidator = targetValidator;
        this.messageInterpolator = getMessageInterpolatorFromValidator(targetValidator);
    }
    
    public CsvBeanValidator() {
        this.targetValidator = createDefaultValidator();
        this.messageInterpolator = getMessageInterpolatorFromValidator(targetValidator);
    }
    
    /**
     * Bean Validatonのデフォルトのインスタンスを取得する。
     * @return
     */
    private Validator createDefaultValidator() {
        final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        final Validator validator = validatorFactory.usingContext()
                .getValidator();
        
        return validator;
    }
    
    /**
     * ValidatorからMessageInterpolartorを取得する。
     * @param validator
     * @return {@link ValidatorImpl}出ない場合は、nullを返す。
     * @throws IllegalStateException 取得に失敗した場合。
     */
    private static MessageInterpolator getMessageInterpolatorFromValidator(Validator validator) {
        
        if(!(validator instanceof ValidatorImpl)) {
            return null;
        }
        
        try {
            Field field = ValidatorImpl.class.getDeclaredField("messageInterpolator");
            field.setAccessible(true);
            
            MessageInterpolator interpolator = (MessageInterpolator)field.get(validator);
            return interpolator;
            
        } catch (IllegalAccessException | NoSuchFieldException | SecurityException e) {
            throw new IllegalStateException("fail reflect MessageInterpolrator from ValidatorImpl.", e);
        }
        
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
                
                String defaultMessage = determineDefaltMessage(errorVars, violation);
                
                bindingErrors.rejectValue(field, columnMapping.getField().getType(), 
                        errorCodes, errorVars, defaultMessage);
                
            } else {
                // オブジェクトエラーの場合
                bindingErrors.reject(errorCodes, errorVars, violation.getMessage());
                
            }
            
        }
        
        
    }
    
    /**
     * エラーコードの決定する
     * @param descriptor フィールド情報
     * @return エラーコード
     */
    protected String[] determineErrorCode(ConstraintDescriptor<?> descriptor) {
        return new String[] {
                descriptor.getAnnotation().annotationType().getSimpleName()/*,
                descriptor.getAnnotation().annotationType().getCanonicalName(),
                descriptor.getAnnotation().annotationType().getCanonicalName() + ".message"
                */
        };
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
    
    /**
     * CSVの標準メッセージを取得する。
     * <p>CSVメッセージ変数で、再度フォーマットを試みる。</p>
     * 
     * @param errorVars エラー時の変数
     * @param violation エラー情報
     * @return デフォルトメッセージ
     */
    protected String determineDefaltMessage(final Map<String, Object> errorVars, ConstraintViolation<Object> violation) {
        
        String message = violation.getMessage();
        if(messageInterpolator == null) {
            return message;
            
        } else if(!(message.contains("{") && message.contains("}"))) {
            // 変数形式が含まれていない場合は、そのまま返す。
            return message;
        }
        
        MessageInterpolatorContext context = new MessageInterpolatorContext(
                violation.getConstraintDescriptor(),
                violation.getInvalidValue(),
                violation.getRootBeanClass(),
                errorVars,
                Collections.emptyMap());
        
        return messageInterpolator.interpolate(violation.getMessageTemplate(), context);
        
    }
    
}

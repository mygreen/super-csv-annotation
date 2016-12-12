package com.github.mygreen.supercsv.validation;

import java.util.HashMap;
import java.util.Map;

import org.supercsv.cellprocessor.ift.CellProcessor;

/**
 * フィールドに対するエラー情報。
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class CsvFieldError extends CsvError {
    
    /** serialVersionUID */
    private static final long serialVersionUID = -7775352881004606689L;
    
    private final boolean processingFailure;
    
    private final String field;
    
    public CsvFieldError(final String objectName, final String field, final boolean processingFailure,
            final String[] codes, final Map<String, Object> variables, final String defaultMessage) {
        super(objectName, codes, variables, defaultMessage);
        
        this.processingFailure = processingFailure;
        this.field = field;
    }
    
    /**
     * {@link CellProcessor}内で発生したエラーかどうか。
     * <p>{@link CellProcessor}で例外がスローされると、型変換されず、検証対象のBeanに値が設定されないないため、
     *    後から{@link CsvValidator}で値を検証する際に検証をスキップする判定に利用する。
     * </p>
     * @return trueの場合、{@link CellProcessor}内で発生したエラー。
     */
    public boolean isProcessingFailure() {
        return processingFailure;
    }    
    
    /**
     * フィールド名を取得する。
     * @return Beanにされたフィールドの名称を返す。
     */
    public String getField() {
        return field;
    }
    
    public static final class Builder {
        
        private final String objectName;
        
        private final String field;
        
        private final String[] codes;
        
        private boolean processingFailure;
        
        private Map<String, Object> variables = new HashMap<>();
        
        private String defaultMessage;
        
        public Builder(final String objectName, final String field, final String[] codes) {
            this.objectName = objectName;
            this.field = field;
            this.codes = codes;
        }
        
        public Builder processingFailure(final boolean processingFailure) {
            this.processingFailure = processingFailure;
            return this;
        }
        
        public Builder variables(final Map<String, Object> variables) {
            this.variables.putAll(variables);
            return this;
        }
        
        public Builder variables(final String key, final Object value) {
            this.variables.put(key, value);
            return this;
        }
        
        public Builder defaultMessage(final String defaultMessage) {
            this.defaultMessage = defaultMessage;
            return this;
        }
        
        public CsvFieldError build() {
            
            return new CsvFieldError(objectName, field, processingFailure, codes, variables, defaultMessage);
        }
        
    }
    
}

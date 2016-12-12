package com.github.mygreen.supercsv.validation;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.github.mygreen.supercsv.localization.MessageInterpolator;
import com.github.mygreen.supercsv.localization.MessageResolver;

/**
 * オブジェクトに対するエラーメッセージ。
 * 
 * @version 2.0
 * @author T.TSUCHIE
 *
 */
public class CsvError implements Serializable {
    
    /** serialVersionUID */
    private static final long serialVersionUID = -1561852232153053944L;
    
    /**
     * オブジェクト名
     */
    private final String objectName;
    
    /**
     * メッセージコード
     * <p>複数指定可能で、フォーマットする際に一致するメッセージのリソースが見つかるまで</p>
     */
    private final String[] codes;
    
    /**
     * メッセージ中の変数。
     */
    private final Map<String, Object> variables;
    
    /**
     * デフォルトメッセージ。
     * <p>指定したコードに対するメッセージが見つからない場合に、適用されるメッセージ。
     */
    private final String defaultMessage;
    
    public CsvError(final String objectName, final String[] codes, final Map<String, Object> variables,
            final String defaultMessaage) {
        this.objectName = objectName;
        this.codes = codes;
        this.variables = variables;
        this.defaultMessage = defaultMessaage;
    }
    
    /**
     * メッセージにフォーマットする。
     * @param messageResolver 
     * @param messageInterpolator
     * @return フォーマットされたメッセージ。
     */
    public String format(final MessageResolver messageResolver, final MessageInterpolator messageInterpolator) {
        
        for(String code : getCodes()) {
            Optional<String> message = messageResolver.getMessage(code);
            if(message.isPresent()) {
                return messageInterpolator.interpolate(message.get(), getVariables(), true, messageResolver);
            }
            
        }
        
        // デフォルトメッセージはBeanValidationのとき変数を追加している場合があるため、再度フォーマットする。
        return messageInterpolator.interpolate(getDefaultMessage(), getVariables(), true, messageResolver);
    }
    
    /**
     * オブジェクト名の取得。
     * @return Beanクラス名のパッケージ名を除いた値。
     */
    public String getObjectName() {
        return objectName;
    }
    
    /**
     * メッセージコードの候補を取得する。
     * @return メッセージコードの候補。
     */
    public String[] getCodes() {
        return codes;
    }
    
    /**
     * メッセージ変数を取得する。
     * @return メッセージをフォーマットする際に、その中で利用可能な変数。
     */
    public Map<String, Object> getVariables() {
        return variables;
    }
    
    /**
     * デフォルトメッセージを取得する。
     * @return メッセージコードで指定したメッセージリソースが見つからない場合に適用されるメッセージ。
     */
    public String getDefaultMessage() {
        return defaultMessage;
    }
    
    public static final class Builder {
        
        private final String objectName;
        
        private final String[] codes;
        
        private Map<String, Object> variables = new HashMap<>();
        
        private String defaultMessage;
        
        public Builder(final String objectName, final String[] codes) {
            this.objectName = objectName;
            this.codes = codes;
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
        
        public CsvError build() {
            
            return new CsvError(objectName, codes, variables, defaultMessage);
        }
        
    }
    
}

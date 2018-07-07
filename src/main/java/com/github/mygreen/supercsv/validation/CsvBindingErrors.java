package com.github.mygreen.supercsv.validation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.github.mygreen.supercsv.util.ArgUtils;
import com.github.mygreen.supercsv.util.Utils;

/**
 * CSVのエラー情報を管理するためのクラス。
 * <p>SpringFrameworkのBindingResultを参考。</p>
 * <p>現状、ネストしたフィールドはサポートしていないため、パスの機能を省略して実装する。</p>
 *
 * @version 2.2
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class CsvBindingErrors implements Serializable {
    
    /** serialVersionUID */
    private static final long serialVersionUID = -9014484172903397334L;
    
    /** パスの区切り文字 */
    public static final String PATH_SEPARATOR = ".";
    
    /** オブジェクト名 */
    private final String objectName;
    
    /** エラーオブジェクト */
    private final List<CsvError> errors = new ArrayList<>();
    
    /** エラーコードの候補を生成するクラス */
    private MessageCodeGenerator messageCodeGenerator = new MessageCodeGenerator();
    
    /**
     * オブジェクト名を指定するコンストラクタ。
     * @param objectName メッセージオブジェクトを構成する際に、自動的に付与する名称。
     * @throws NullPointerException {@link objectName is null.}
     */
    public CsvBindingErrors(final String objectName) {
        Objects.requireNonNull(objectName);
        this.objectName = objectName;
    }
    
    /**
     * クラス名を元にオブジェクト名を設定するコンストラクタ。
     * @param clazz 検証対象のBeanのクラスタイプ。
     */
    public CsvBindingErrors(final Class<?> clazz) {
        this(clazz.getSimpleName());
    }
    
    /**
     * オブジェクト名の取得
     * @return コンストラクタで設定したオブジェクト名を取得する。
     */
    public String getObjectName() {
        return objectName;
    }
    
    /**
     * 全てのエラー情報を取得する。
     * @return 現在のエラー情報
     */
    public List<CsvError> getAllErrors() {
        return new ArrayList<>(errors);
    }
    
    /**
     * エラーを追加する
     * @param error
     */
    public void addError(final CsvError error) {
        this.errors.add(error);
    }
    
    /**
     * 全てのエラー情報を追加する。
     * @param errors 追加するエラー情報。
     */
    public void addAllErrors(Collection<CsvError> errors) {
        this.errors.addAll(errors);
    }
    
    /**
     * 全てのエラー情報を削除する。
     */
    public void clearAllErrors() {
        this.errors.clear();
    }
    
    /**
     * エラー情報が存在するかどうか確かめる。
     * @return trueの場合、エラー情報が存在する。
     */
    public boolean hasErrors() {
        return !errors.isEmpty();
    }
    
    /**
     * グローバルエラーを取得する
     * @return エラーがない場合は空のリストを返す
     */
    public List<CsvError> getGlobalErrors() {
        final List<CsvError> list = new ArrayList<CsvError>();
        for(CsvError item : this.errors) {
            if(!(item instanceof CsvFieldError)) {
                list.add(item);
            }
        }
        
        return list;
    }
    
    /**
     * 先頭のグローバルエラーを取得する。
     * @return 存在しない場合は、nullを返す。
     */
    public CsvError getFirstGlobalError() {
        for(CsvError item : this.errors) {
            if(!(item instanceof CsvFieldError)) {
                return item;
            }
        }
        
        return null;
    }
    
    /**
     * グローバルエラーがあるか確かめる。
     * @return
     */
    public boolean hasGlobalErrors() {
        return !getGlobalErrors().isEmpty();
    }
    
    /**
     * グローバルエラーの件数を取得する
     * @return
     */
    public int getGlobalErrorCount() {
        return getGlobalErrors().size();
    }
    
    /**
     * フィールドエラーを取得する
     * @return エラーがない場合は空のリストを返す
     */
    public List<CsvFieldError> getFieldErrors() {
        final List<CsvFieldError> list = new ArrayList<CsvFieldError>();
        for(CsvError item : this.errors) {
            if(item instanceof CsvFieldError) {
                list.add((CsvFieldError) item);
            }
        }
        
        return list;
    }
    
    /**
     * 先頭のフィールドエラーを取得する
     * @return エラーがない場合は空のリストを返す
     */
    public CsvFieldError getFirstFieldError() {
        for(CsvError item : this.errors) {
            if(item instanceof CsvFieldError) {
                return (CsvFieldError) item;
            }
        }
        
        return null;
    }
    
    /**
     * フィールドエラーが存在するか確かめる。
     * @return true:フィールドエラーを持つ。
     */
    public boolean hasFieldErrors() {
        return !getFieldErrors().isEmpty();
    }
    
    /**
     * フィールドエラーの件数を取得する。
     * @return
     */
    public int getFieldErrorCount() {
        return getFieldErrors().size();
    }
    
    /**
     * パスを指定してフィールドエラーを取得する
     * @param path 最後に'*'を付けるとワイルドカードが指定可能。
     * @return
     * @throws IllegalArgumentException {@literal if path is empty.}
     */
    public List<CsvFieldError> getFieldErrors(final String path) {
        ArgUtils.notEmpty(path, "path");
        
        final List<CsvFieldError> list = new ArrayList<CsvFieldError>();
        for(CsvError item : this.errors) {
            if(item instanceof CsvFieldError && isMatchingFieldError(path, (CsvFieldError) item)) {
                list.add((CsvFieldError) item);
            }
        }
        
        return list;
    }
    
    /**
     * パスを指定して先頭のフィールドエラーを取得する
     * @param path 最後に'*'を付けるとワイルドカードが指定可能。
     * @return エラーがない場合は空のリストを返す
     */
    public CsvFieldError getFirstFieldError(final String path) {
        ArgUtils.notEmpty(path, "path");
        
        for(CsvError item : this.errors) {
            if(item instanceof CsvFieldError && isMatchingFieldError(path, (CsvFieldError) item)) {
                return (CsvFieldError) item;
            }
        }
        
        return null;
    }
    
    /**
     * 指定したパスのフィィールドエラーが存在するか確かめる。
     * @param path 最後に'*'を付けるとワイルドカードが指定可能。
     * @return true:エラーがある場合。
     */
    public boolean hasFieldErrors(final String path) {
        return !getFieldErrors(path).isEmpty();
    }
    
    /**
     * 指定したパスのフィィールドエラーの件数を取得する。
     * @param path 最後に'*'を付けるとワイルドカードが指定可能。
     * @return
     */
    public int getFieldErrorCount(final String path) {
        return getFieldErrors(path).size();
    }
    
    /**
     * 指定したパスがフィールドエラーのパスと一致するかチェックするかどうか。
     * @param path 
     * @param fieldError
     * @return true: 一致する場合。
     */
    private boolean isMatchingFieldError(final String path, final CsvFieldError fieldError) {
        
        if (fieldError.getField().equals(path)) {
            return true;
        }
        
        if(path.endsWith("*")) {
            String subPath = path.substring(0, path.length()-1);
            return fieldError.getField().startsWith(subPath);
        }
        
        return false;
    }
    
    /**
     * グローバルエラーを登録する。
     * @param errorCode エラーコード
     */
    public void reject(final String errorCode) {
        reject(errorCode, Collections.emptyMap(), null);
    }
    
    /**
     * グローバルエラーを登録する。
     * @param errorCode エラーコード
     * @param defaultMessage 指定したエラーコードに対するメッセージが見つからないときに使用するメッセージです。指定しない場合はnullを設定します。
     */
    public void reject(final String errorCode, final String defaultMessage) {
        reject(errorCode, Collections.emptyMap(), defaultMessage);
    }
    
    /**
     * グローバルエラーを登録する。
     * @param errorCode エラーコード
     * @param messageVariables メッセージ中の変数。
     */
    public void reject(final String errorCode, final Map<String, Object> messageVariables) {
        reject(errorCode, messageVariables, null);
    }
    
    /**
     * グローバルエラーを登録する。
     * @param errorCode エラーコード
     * @param messageVariables メッセージ中の変数。
     * @param defaultMessage 指定したエラーコードに対するメッセージが見つからないときに使用するメッセージです。指定しない場合はnullを設定します。
     */
    public void reject(final String errorCode, final Map<String, Object> messageVariables, final String defaultMessage) {
        reject(new String[]{errorCode}, messageVariables, defaultMessage);
    }
    
    /**
     * グローバルエラーを登録する。
     * @since 2.2
     * @param errorCodes エラーコード
     * @param messageVariables メッセージ中の変数。
     * @param defaultMessage 指定したエラーコードに対するメッセージが見つからないときに使用するメッセージです。指定しない場合はnullを設定します。
     */
    public void reject(final String[] errorCodes, final Map<String, Object> messageVariables, final String defaultMessage) {
        
        String[] codes = new String[0];
        for(String errorCode : errorCodes) {
            codes = Utils.concat(codes, messageCodeGenerator.generateCodes(errorCode, getObjectName()));
        }
        addError(new CsvError(getObjectName(), codes, messageVariables, defaultMessage));
    }
    
    /**
     * フィールドエラーを登録します。
     * @param field フィールドパス。
     * @param errorCode エラーコード。
     */
    public void rejectValue(final String field, final String errorCode) {
        rejectValue(field, null, errorCode, Collections.emptyMap(), null);
        
    }
    
    /**
     * フィールドエラーを登録します。
     * @param field フィールドパス。
     * @param errorCode エラーコード。
     * @param defaultMessage 指定したエラーコードに対するメッセージが見つからないときに使用するメッセージです。指定しない場合はnullを設定します。
     */
    public void rejectValue(final String field, final String errorCode, final String defaultMessage) {
        rejectValue(field, null, errorCode, Collections.emptyMap(), defaultMessage);
        
    }
    
    /**
     * フィールドエラーを登録します。
     * @param field フィールドパス。
     * @param errorCode エラーコード。
     * @param messageVariables メッセージ中の変数。
     * @param defaultMessage 指定したエラーコードに対するメッセージが見つからないときに使用するメッセージです。指定しない場合はnullを設定します。
     */
    public void rejectValue(final String field, final String errorCode, 
            final Map<String, Object> messageVariables, final String defaultMessage) {
        
        rejectValue(field, null, errorCode, Collections.emptyMap(), defaultMessage);
        
    }
    
    /**
     * フィールドエラーを登録します。
     * @param field フィールドパス。
     * @param fieldType フィールドのタイプ
     * @param errorCode エラーコード。
     * @param messageVariables メッセージ中の変数。
     */
    public void rejectValue(final String field, final Class<?> fieldType, final String errorCode, 
            final Map<String, Object> messageVariables) {
        
        rejectValue(field, fieldType, errorCode, messageVariables, null);
        
    }
    
    /**
     * フィールドエラーを登録します。
     * @param field フィールドパス。
     * @param fieldType フィールドのタイプ
     * @param errorCode エラーコード。
     * @param messageVariables メッセージ中の変数。
     * @param defaultMessage 指定したエラーコードに対するメッセージが見つからないときに使用するメッセージです。指定しない場合はnullを設定します。
     */
    public void rejectValue(final String field, final Class<?> fieldType, final String errorCode, 
            final Map<String, Object> messageVariables, final String defaultMessage) {
        
        rejectValue(field, fieldType, new String[]{errorCode}, messageVariables, defaultMessage);
    }
    
    /**
     * フィールドエラーを登録します。
     * @since 2.2
     * @param field フィールドパス。
     * @param fieldType フィールドのタイプ
     * @param errorCodes エラーコード。
     * @param messageVariables メッセージ中の変数。
     * @param defaultMessage 指定したエラーコードに対するメッセージが見つからないときに使用するメッセージです。指定しない場合はnullを設定します。
     */
    public void rejectValue(final String field, final Class<?> fieldType, final String[] errorCodes, 
            final Map<String, Object> messageVariables, final String defaultMessage) {
        
        String[] codes = new String[0];
        for(String errorCode : errorCodes) {
            codes = Utils.concat(codes, messageCodeGenerator.generateCodes(errorCode, getObjectName(), field, fieldType));
        }
        
        addError(new CsvFieldError(getObjectName(), field, false, codes, messageVariables, defaultMessage));
        
    }
    
    public MessageCodeGenerator getMessageCodeGenerator() {
        return messageCodeGenerator;
    }
    
    public void setMessageCodeGenerator(MessageCodeGenerator messageCodeGenerator) {
        this.messageCodeGenerator = messageCodeGenerator;
    }
    
    
}

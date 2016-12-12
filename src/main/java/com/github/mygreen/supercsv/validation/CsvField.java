package com.github.mygreen.supercsv.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.github.mygreen.supercsv.annotation.CsvColumn;
import com.github.mygreen.supercsv.builder.ColumnMapping;
import com.github.mygreen.supercsv.util.ArgUtils;

/**
 * 独自にフィールドの値を検証する機能を実装する際のヘルパクラス。
 *
 * @param <T> フィールドのクラスタイプ。
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class CsvField<T> {
    
    private final String name;
    
    private final T value;
    
    private final ColumnMapping columnMapping;
    
    private final ValidationContext<?> validationContext;
    
    private final List<CsvFieldValidator<T>> validators = new ArrayList<>();
    
    /**
     * 
     * @param <R> レコードのクラスタイプ。
     * @param validationContext 入力値検証の情報
     * @param record レコードオブジェクト
     * @param fieldName フィールド名
     * @throws NullPointerException {@literal bindingErrors or validationContext or record is null.}
     * @throws IllegalArgumentException 指定したフィールドがレコードに存在しない場合。
     */
    @SuppressWarnings("unchecked")
    public <R> CsvField(final ValidationContext<R> validationContext, final R record, final String fieldName) {
        Objects.requireNonNull(validationContext);
        Objects.requireNonNull(record);
        ArgUtils.notEmpty(fieldName, "fieldName");
        
        this.columnMapping = validationContext.getBeanMapping().getColumnMapping(fieldName)
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("not found field '%s' in record '%s'", fieldName, record.getClass().getName())));
        
        this.validationContext = validationContext;
        this.name = fieldName;
        this.value = (T)columnMapping.getField().getValue(record);
        
    }
    
    /**
     * フィールドの名称を取得します。
     * @return コンストラクタで指定したフィールドの名称。
     */
    public String getName() {
        return name;
    }
    
    /**
     * フィールドのークラスタイプを取得します。
     * @return クラス情報を返す。
     */
    @SuppressWarnings("unchecked")
    public Class<T> getType() {
        return (Class<T>) columnMapping.getField().getType();
    }
    
    /**
     * フィールドのラベルを取得します。
     * @return アノテーションの属性{@link CsvColumn#label()}の値を取得します。
     */
    public String getLabel() {
        return columnMapping.getLabel();
    }
    
    /**
     * フィールドの列番号を取得します。
     * @return アノテーションの属性{@link CsvColumn#number()}の値を取得します。
     */
    public int getColumnNumber() {
        return columnMapping.getNumber();
    }
    
    /**
     * フィールドに対するエラーがあるか検査します。
     * @param bindingErrors エラー情報
     * @return trueの場合、エラーがあると判定します。
     * @throws NullPointerException {@literal bindingErrors is null.}
     */
    public boolean hasError(final CsvBindingErrors bindingErrors) {
        Objects.requireNonNull(bindingErrors);
        return bindingErrors.hasFieldErrors(name);
    }
    
    /**
     * フィールドに対するエラーがないか検査します。
     * @see #hasError(CsvBindingErrors)
     * @param bindingErrors エラー情報
     * @return trueの場合、エラーがないと判定します。
     * @throws NullPointerException {@literal bindingErrors is null.}
     */
    public boolean hasNotError(final CsvBindingErrors bindingErrors) {
        return !hasError(bindingErrors);
    }
    
    /**
     * 値が空かどうか判定します。
     * <p>基本的に、nullかどうかで判定しますが、文字列型の場合は空文字かどうかでも判定します。
     * @return trueの場合、空と判定します。
     */
    public boolean isEmpty() {
        if(value == null) {
            return true;
        }
        
        if(value instanceof String) {
            return ((String)value).isEmpty();
        }
        
        return false;
    }
    
    /**
     * 値が空でないかどうか判定します。
     * @since {@link #isEmpty()}
     * @return trueの場合、非空と判定します。
     */
    public boolean isNotEmpty() {
        return !isEmpty();
        
    }
    
    /**
     * フィールドのバリデータを追加する。
     * @param validator バリデータ。
     * @return 自身のインスタンス。
     * @throws NullPointerException {@literal validator is null.}
     */
    public CsvField<T> add(final CsvFieldValidator<T> validator) {
        Objects.requireNonNull(validator);
        
        validators.add(validator);
        return this;
    }
    
    /**
     * フィールドの値の検証を行う。
     * <p>既にエラーがある場合や検証後エラーとなる場合は、その時点で検証を中止する。</p>
     * @param bindingErrors エラー情報。
     * @return 自身のインスタンス。
     * @throws NullPointerException {@literal bindingErrors is null.}
     */
    public CsvField<T> validate(final CsvBindingErrors bindingErrors) {
        Objects.requireNonNull(bindingErrors);
        
        for(CsvFieldValidator<T> validator : getValidators()) {
            if(hasError(bindingErrors)) {
                return this;
            }
            
            validator.validate(bindingErrors, this);
        }
        
        return this;
        
    }
    
    /**
     * フィールドのエラー情報を取得する。
     * @param bindingErrors エラー情報
     * @return エラーがない場合は空のリストを返します。
     * @throws NullPointerException {@literal bindingErrors is null.}
     */
    public List<CsvFieldError> getFieldErrors(final CsvBindingErrors bindingErrors) {
        Objects.requireNonNull(bindingErrors);
        return bindingErrors.getFieldErrors(getName());
    }
    
    /**
     * フィールドの値を取得します。
     * @return CellProcessorでエラーが発生した場合、値はなくnullを返します。
     */
    public T getValue() {
        return value;
    }
    
    /**
     * カラムのマッピング情報
     * @return CsvBeanに定義したカラムのマッピング情報
     */
    public ColumnMapping getColumnMapping() {
        return columnMapping;
    }
    
    /**
     * 入力値検証の情報
     * @return コンストラクタで渡した
     */
    public ValidationContext<?> getValidationContext() {
        return validationContext;
    }
    
    /**
     * フィールドのValidatorの一覧を取得する。
     * @return 設定されている{@link CsvFieldValidator}のリストを取得する。
     */
    public List<CsvFieldValidator<T>> getValidators() {
        return validators;
    }
    
}

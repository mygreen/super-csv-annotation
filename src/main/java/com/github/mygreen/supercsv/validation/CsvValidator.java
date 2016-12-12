package com.github.mygreen.supercsv.validation;

/**
 * CSVのレコード（Beanクラス）に対する入力値検証のインタフェース。
 * 
 * @param <R> Beanのクラスタイプ
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public interface CsvValidator<R> {
    
    /**
     * レコードの値を検証する。
     * @param record 検証対象のレコードオブジェクト
     * @param bindingErrors エラー情報
     * @param validationContext マッピング情報。
     */
    void validate(R record, CsvBindingErrors bindingErrors, ValidationContext<R> validationContext);
    
}

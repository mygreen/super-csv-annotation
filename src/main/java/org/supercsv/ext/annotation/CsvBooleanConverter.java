package org.supercsv.ext.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.supercsv.cellprocessor.FmtBool;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.ext.cellprocessor.ParseBoolean;


/**
 * boolean/Boolean型の変換規則を定義するアノテーション。
 *
 * @author T.TSUCHIE
 *
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CsvBooleanConverter {
    
    /**
     * 読み込み時に{@literal true} とする候補を指定します。
     * <p>{@link #inputFalseValue()} と重複する値がある場合は、{@link #inputTrueValue()} の値が優先されます。</p>
     * <p>{@link CellProcessor}の{@link ParseBoolean}が設定されます。</p>
     * 
     * @return trueとして読み込む候補。
     */
    String[] inputTrueValue() default {"true", "1", "yes", "on", "y", "t"};
    
    /**
     * 読み込み時に{@literal false} とする候補を指定します。
     * <p>{@link #inputTrueValue()} と重複する値がある場合は、{@link #inputTrueValue()} の値が優先されます。</p>
     * <p>{@link CellProcessor}の{@link ParseBoolean}が設定されます。</p>
     * 
     * @return falseとして読み込む候補。
     */
    String[] inputFalseValue() default {"false", "0", "no", "off", "f", "n"};
    
    /**
     * 書き込み時に{@link true}の値を表現する値を指定します。
     * <p>`true`以外の`○`など他の値として出力したい場合に指定します。</p>
     * <p>{@link CellProcessor}の{@link FmtBool}が設定されます。</p>
     * 
     * @return trueの値の代替として出力される値。
     */
    String outputTrueValue() default "true";
    
    /**
     * 書き込み時に{@link false}の値を表現する値を指定します。
     * <p>`false`以外の`×`など他の値として出力したい場合に指定します。</p>
     * <p>{@link CellProcessor}の{@link FmtBool}が設定されます。</p>
     * 
     * @return falseの値の代替として出力される値。
     */
    String outputFalseValue() default "false";
    
    /**
     * 読み込み時に、大文字・小文字を区別なく候補の値と比較して処理するか指定します。
     * @return trueの場合、大文字・小文字の区別は行いません。
     */
    boolean ignoreCase() default false;
    
    /**
     * 読み込み時に {@link #inputTrueValue()}、{@link #inputFalseValue()} で指定した候補の値と一致しない場合、値をfalseとして読み込み込むか指定します。
     * @return trueの場合、読み込み用の候補の値と一致しない場合、falseとして読み込みます。
     */
    boolean failToFalse() default false;
}

package org.supercsv.ext.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.regex.Pattern;

import org.supercsv.cellprocessor.constraint.ForbidSubStr;
import org.supercsv.cellprocessor.constraint.RequireSubStr;
import org.supercsv.cellprocessor.constraint.StrNotNullOrEmpty;
import org.supercsv.cellprocessor.constraint.StrRegEx;
import org.supercsv.cellprocessor.constraint.Strlen;
import org.supercsv.ext.cellprocessor.constraint.Length;
import org.supercsv.ext.cellprocessor.constraint.MaxLength;
import org.supercsv.ext.cellprocessor.constraint.MinLength;


/**
 * 文字列型の変換規則を定義するアノテーション。
 *
 * @author T.TSUCHIE
 *
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CsvStringConverter {
    
    /**
     * カラムの値の文字長さが指定した長さ以上（最小長）かどうかチェックします。
     * <p>{@link CellProcessor}の{@link MinLength}が設定されます。</p>
     * <p>{@link #minLength()}, {@link #maxLength()}の両方の値を指定すると、{@link CellProcessor}の{@link Length}が設定されます。
     * @return 0以上の値を指定すると有効になります。
     */
    int minLength() default -1;
    
    /**
     * カラムの値の文字長さが指定した長さ以下（最大長）かどうかチェックします。
     * <p>{@link CellProcessor}の{@link MaxLength}が設定されます。</p>
     * <p>{@link #minLength()}, {@link #maxLength()}の両方の値を指定すると、{@link CellProcessor}の{@link Length}が設定されます。
     * @return 0以上の値を指定すると有効になります。
     */
    int maxLength() default -1;
    
    /**
     * カラムの値の文字長が指定した長さかどうかチェックします。
     * <p>{@link CellProcessor}の{@link Strlen}が指定されます。</p>
     * @return 複数の文字長を指定可能です。
     */
    int[] exactLength() default {};
    
    /**
     * カラムの値が指定した正規表現に一致するかどうかチェックします。
     * <p>{@link CellProcessor}の{@link StrRegEx}が設定されます。</p>
     * @return {@link Pattern}で解釈可能な書式を指定します。
     */
    String regex() default "";
    
    /**
     * カラムの値が指定した禁止語彙を含まないかどうかチェックします。
     * <p>{@link CellProcessor}の{@link ForbidSubStr}が設定されます。</p>
     * @return 複数の文字長を指定可能です。
     */
    String[] forbid() default {};
    
    /**
     * カラムの値が指定した語彙を含んでいるかどうかチェックします。
     * <p>{@link CellProcessor}の{@link RequireSubStr}が設定されます。</p>
     * @return 複数の文字長を指定可能です。
     */
    String[] contain() default {};
    
    /**
     * カラムの値がnullまたは空文字かどうかチェックします。。
     * <p>{@link CellProcessor}の{@link StrNotNullOrEmpty}が設定されます。</p>
     * @return trueの場合、nullまたは空文字かどうかチェックします。
     */
    boolean notEmpty() default false;
    
}

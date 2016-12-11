package com.github.mygreen.supercsv.annotation.format;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.supercsv.cellprocessor.ift.CellProcessor;

import com.github.mygreen.supercsv.cellprocessor.format.TextFormatter;

/**
 * 任意のタイプのフォーマッタを指定するアノテーション。
 * <p>このライブラリに対応していない書式や独自のタイプに対応する際に指定します。</p>
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CsvFormat {
    
    /**
     * フォーマッタのクラスを指定します。
     * @return {@link TextFormatter}の実装クラスを指定します。
     */
    @SuppressWarnings("rawtypes")
    Class<? extends TextFormatter> formatter();
    
    /**
     * エラー時のメッセージを指定します。
     * <p>{@literal {key}}の書式の場合、プロパティファイルから取得した値を指定できます。</p>
     * <p>このメッセージは、文字列をブール型にパースする際に失敗したときに適用されるメッセージに使用します。</p>
     * @return 省略した場合は、適用された{@link CellProcessor}に基づいたメッセージが出力されます。
     */
    String message() default "";
}

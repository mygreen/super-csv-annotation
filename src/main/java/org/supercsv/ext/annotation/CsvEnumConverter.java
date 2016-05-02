package org.supercsv.ext.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * 列挙型の変換規則を指定するアノテーション。
 *
 * @version 1.1
 * @author T.TSUCHIE
 *
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CsvEnumConverter {
    
    /**
     * 読み込み時に、大文字・小文字を区別なく候補の値と比較して処理するか指定します。
     * @return trueの場合、大文字・小文字の区別は行いません。
     */
    boolean ignoreCase() default false;
    
    /**
     * 列挙型のをname()メソッド以外から取得するときに指定します。
     * <p>例). Color.label()のlabel()メソッドを指定するときには、'label'と指定します。
     */
    String valueMethodName() default "";
}

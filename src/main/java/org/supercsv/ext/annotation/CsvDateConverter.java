package org.supercsv.ext.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Locale;


/**
 * Date formmating annotation.
 *
 * @author T.TSUCHIE
 *
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CsvDateConverter {
    
    /**
     * format pattern.
     * <p>指定しない場合は、クラスタイプにより自動的に決まります。
     * @return
     */
    String pattern() default "";
    
    /**
     * parsing date, lenient.
     * <p>default : false
     * @return
     */
    boolean lenient() default true;
    
    /**
     * timezone id
     * <p>LocalDateTime,LocalDate,LocalTimeの時は、指定しても意味がありません。
     * @return
     */
    String timezone() default "";
    
    /**
     * formatting {@link Locale}.
     * ex. 'ja', 'ja_JP'
     * @since 1.2
     * @return
     */
    String locale() default "";
    
    /**
     * start date 
     * <p>set CellProcessor for 'FutureDate'
     * @return
     */
    String min() default "";
    
    /**
     * end date 
     * <p>set CellProcessor for 'PastDate'
     * @return
     */
    String max() default "";
}

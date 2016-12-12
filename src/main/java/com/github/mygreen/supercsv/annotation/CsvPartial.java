package com.github.mygreen.supercsv.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 部分的にカラムをマッピングする際に、ファイルの情報を補完するためのアノテーションです。
 * 
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CsvPartial {
    
    /**
     * 実際のCSVファイルのカラム数を指定します。
     * <p>定義しているカラムの{@link CsvColumn#number()}より大きい値を指定します。</p>
     * @return 1以上の値を指定します。
     */
    int columnSize();
    
    /**
     * ヘッダー情報を定義するためのアノテーションです。
     * @return Beanに定義されていないカラムのヘッダー情報を補足するために使用されます。
     */
    Header[] headers() default {};
    
    /**
     * ヘッダー情報を表現します。
     *
     */
    @interface Header {
        
        /**
         * カラム番号を指定します。
         * @return 1以上の値を指定します。
         */
        int number();
        
        /**
         * ヘッダーの値を指定します。
         * @return ヘッダーの値を指定します。
         */
        String label();
        
    }
}

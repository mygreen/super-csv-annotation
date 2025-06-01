package com.github.mygreen.supercsv.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.github.mygreen.supercsv.annotation.conversion.CsvFixedSize;

/**
 * 部分的にカラムをマッピングする際のカラム情報を補完するためのアノテーションです。
 * 
 * <h3 class="description">基本的な使い方</h3>
 * 
 * <ul>
 *   <li>部分的にマッピングする際には、単純に、マッピングしないカラムの定義を省略します。</li>
 *   <li>ただし、定義している最大のカラム番号が、実ファイルのカラム番号よりも小さい場合は、
 *       属性{@link #columnSize()}で、実際のカラムサイズを指定します。
 *   </li>
 *   <li>見出し行がある場合、定義していない見出しを属性{@link #headers()}で定義します。</li>
 * </ul>
 * 
 * <pre class="highlight"><code class="java">
 * {@literal @CsvBean(header=true, validateHeader=true)}
 * {@literal @CsvPartial}(columnSize=5, headers={
 *     {@literal @CsvPartial.Header(number=3, label="電話番号")},
 *     {@literal @CsvPartial.Header(number=5, label="生年月日")})
 * public class SampleCsv {
 *     
 *     {@literal @CsvColumn(number=1)}
 *     private int id;
 *
 *     {@literal @CsvColumn(number=2, label="氏名")}
 *     private String name;
 *
 *     // カラム番号3は読み込まない場合は、定義を行いません。
 *
 *     {@literal @CsvColumn(number=4, label="メールアドレス")}
 *     private String email;
 *
 *     // カラム番号5はマッピングしない場合は、定義を行いません。
 *     
 *     // getter/setterは省略
 * }
 * </code></pre>
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
        
        /**
         * 固定長を指定します。
         * 複数指定した場合は先頭のアノテーションが採用されます。
         * 
         * @since 2.5
         * @return 固定長カラムの設定。
         */
        CsvFixedSize[] fixedSize() default {};
        
    }
}

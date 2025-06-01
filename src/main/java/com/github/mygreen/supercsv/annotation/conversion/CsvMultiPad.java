package com.github.mygreen.supercsv.annotation.conversion;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.github.mygreen.supercsv.annotation.DefaultGroup;
import com.github.mygreen.supercsv.builder.BuildCase;
import com.github.mygreen.supercsv.cellprocessor.conversion.CharWidthPaddingProcessor;
import com.github.mygreen.supercsv.cellprocessor.conversion.PaddingProcessor;
import com.github.mygreen.supercsv.cellprocessor.conversion.SimplePaddingProcessor;

/**
 * パディングするためのアノテーションです。
 * <p>アノテーション {@link CsvLeftPad}や{@link CsvRightPad}より、柔軟に指定ができます。</p>
 *
 * <h3 class="description">基本的な使い方</h3>
 * 
 * <ul>
 *   <li>属性 {@link #size()}で、パディングするサイズを指定します。
 *     <br>変換対象の文字長が属性{@link #size()}の値を超える場合、パディングは行いません。
 *   </li>
 *   <li>属性{@link #padChar()}でパディング文字を指定することができます。
 *     <br>デフォルトでは、半角空白がパディング文字です。
 *   </li>
 *   <li>文字サイズは、考え型によって変わるため、{@link #paddingProcessor()} で処理方式を指定します。
 *      <br>例えば、{@link CharWidthPaddingProcessor}は、文字の幅（半角の長さ=1、全角の長さ=2）として書き込み時にパディングします。
 *   </li>
 *   <li>アノテーション{@link CsvTrim}と併用する際には、処理結果が互いに変換対象となるため、
 *      属性{@link #cases()}や{@link #groups()}で適用するケースを分けるようにしてください。
 *   </li>
 * </ul>
 * 
 * <pre class="highlight"><code class="java">
 * {@literal @CsvBean}
 * public class SampleCsv {
 *     
 *     // 値が[10]の場合、結果は、[&nbsp;&nbsp;&nbsp;10]となります。
 *     {@literal @CsvColumn(number=1)}
 *     {@literal @CsvMultiPad(size=5)}
 *     private Integer id;
 *     
 *     // パディング文字を変更する場合
 *     {@literal @CsvColumn(number=2)}
 *     {@literal @CsvMultiPad(size=5, padChar='_')}
 *     private Integer number;
 *     
 *     // パディングする際の処理方法を指定する場合
 *     {@literal @CsvColumn(number=2)}
 *     {@literal @CsvMultiPad(size=5, padChar='　', paddingProcessor=CharWidthPaddingProcessor.class)}
 *     private String name;
 *     
 *     // 書き込み時のみ適用する場合
 *     {@literal @CsvColumn(number=4)}
 *     {@literal @CsvTrim(cases=BuildCase.Read)}
 *     {@literal @CsvMultiPad(size=20, cases=BuildCase.Write)}
 *     private String comment;
 *     
 *     // getter/setterは省略
 * }
 * </code></pre>
 *
 * @since 2.1
 * @author T.TSUCHIE
 *
 */
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Repeatable(CsvMultiPad.List.class)
@CsvConversion(value={})
public @interface CsvMultiPad {

    /**
     * パディングするサイズを指定します。
     * <p>値は1以上を指定する必要があります。</p>
     * <p>カラムのサイズは考え型によってバイト数、文字幅など異なるため、
     *    属性{@link #paddingProcessor()}によって、変更することができます。
     * </p>
     * 
     * @return パディングのサイズ
     */
    int size();

    /**
     * パディングする際の文字を指定します。
     * @return パディングの文字。
     */
    char padChar() default ' ';

    /**
     * 右詰めをするかどうか指定します。
     * <br>falseの場合は左詰めです。
     *
     * @return trueのときは右側に詰め、パディング文字は左側に追加されます。
     */
    boolean rightAlign() default false;

    /**
     * 指定したカラムの長さを超えた場合、切り出すかどうか指定します。
     * @return 指定したカラムの長さを超えた場合、切り出すかどうか。
     *
     */
    boolean chopped() default false;

    /**
     * パディングの処理方法を指定します。
     * <p>{@link PaddingProcessor}を実装したクラス指定します。</p>
     *
     * @return パディング処理の実装クラスを指定します。
     */
    Class<? extends PaddingProcessor> paddingProcessor() default SimplePaddingProcessor.class;

    /**
     * 適用するケースを指定します。
     * @return 何も指定しない場合は全てのケースに適用されます。
     */
    BuildCase[] cases() default {};

    /**
     * グループのクラスを指定します。
     * <p>処理ごとに適用するアノテーションを切り替えたい場合に指定します。
     * @return 指定しない場合は、{@link DefaultGroup}が適用され全ての処理に適用されます。
     */
    Class<?>[] groups() default {};

    /**
     * アノテーションの処理順序の定義。
     * @return 値が大きいほど後に実行されます。
     *         値が同じ場合は、アノテーションのクラス名の昇順になります。
     */
    int order() default 0;

    @Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface List {

        CsvMultiPad[] value();
    }
}

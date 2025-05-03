package com.github.mygreen.supercsv.annotation.conversion;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.github.mygreen.supercsv.annotation.CsvComposition;
import com.github.mygreen.supercsv.annotation.CsvOverridesAttribute;
import com.github.mygreen.supercsv.annotation.DefaultGroup;
import com.github.mygreen.supercsv.builder.BuildCase;
import com.github.mygreen.supercsv.cellprocessor.conversion.ByteSizePaddingProcessor;
import com.github.mygreen.supercsv.cellprocessor.conversion.CharWidthPaddingProcessor;
import com.github.mygreen.supercsv.cellprocessor.conversion.PaddingProcessor;
import com.github.mygreen.supercsv.cellprocessor.conversion.SimplePaddingProcessor;

/**
 * 固定長のカラムを表現するためのアノテーションです。
 * <p>このアノテーションは、次の合成のアノテーションによって、構成されています。</p>
 * <ul>
 *  <li>書き込み時は、アノテーション{@link CsvMultiPad}によって、パディングします。</li>
 *  <li>読み込み時は、アノテーション{@link CsvOneSideTrim}によって、トリムします。</li>
 * </ul>
 * 
 * <h3 class="description">基本的な使い方</h3>
 * 
 * <ul>
 *   <li>属性{@link #size()}で文字のサイズを指定します。</li>
 *   <li>文字のサイズは、カウント方法の考え型によって変わるため、{@link #paddingProcessor()} で処理方式を指定します。
 *      <br>例えば、{@link CharWidthPaddingProcessor}は、文字の幅（半角文字のサイズ=1、全角文字のサイズ=2）として書き込み時にパディングします。
 *   </li>
 *   <li>属性{@link #rightAlign()}によって、右詰めか左詰めか指定します。
 *    <br>trueのとき右詰め、falseのとき左詰めで、デフォルトは左詰めです。
 *   </li>
 * </ul>
 * 
 * <pre class="highlight"><code class="java">
 * {@literal @CsvBean}
 * public class SampleCsv {
 *     
 *     // 右詰めする
 *     {@literal @CsvColumn(number=1)}
 *     {@literal @CsvFixedSize(size=10, rightAlign=true)}
 *     private Integer value;
 *     
 *     // 全角空白で埋める。
 *     // ただし、文字の幅（半角のサイズ=1、全角のサイズ=2）として処理する。
 *     {@literal @CsvColumn(number=2)}
 *     {@literal @CsvFullChar}
 *     {@literal @CsvFixedSize(size=20, padChar='　', paddingProcessor=CharWidthPaddingProcessor.class)}
 *     private String userName;
 *     
 *     // 他のオブジェクト型への変換を行う
 *     {@literal @CsvColumn(number=3)}
 *     {@literal @CsvFixedSize(size=10, padChar='_')}
 *     {@literal @CsvDateTimeFormat(pattern="uuuu-MM-dd")}
 *     private LocalDate birthday;
 *     
 *     // 指定した文字長を超えた場合、切り落とす。
 *     {@literal @CsvColumn(number=4, label="コメント")}
 *     {@literal @CsvFixedSize(size=20, chopped=true)}
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
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Repeatable(CsvFixedSize.List.class)
@CsvComposition
@CsvMultiPad(size=0, cases=BuildCase.Write, order=1000)
@CsvOneSideTrim(cases=BuildCase.Read)
public @interface CsvFixedSize {
    
    /**
     * カラムのサイズを指定します。
     * <p>値は1以上を指定する必要があります。</p>
     * <p>サイズは考え型によってバイト数、文字幅など異なるため、
     *    属性{@link #paddingProcessor()}によって、変更することができます。
     * </p>
     * 
     * @return カラムのサイズ。
     */
    @CsvOverridesAttribute(annotation=CsvMultiPad.class, name="size")
    int size();
    
    /**
     * パディングする際の文字を指定します。
     * <br>読み込み時には、トリム対象の文字となります。
     *
     * @return パディング文字。
     */
    @CsvOverridesAttribute(annotation=CsvMultiPad.class, name="padChar")
    @CsvOverridesAttribute(annotation=CsvOneSideTrim.class, name="trimChar")
    char padChar() default ' ';
    
    /**
     * パディング時に指定したカラムの長さを超えた場合、切り出すかどうか指定します。
     * @return trueの場合、指定したカラムの長さを超えた場合切り出します。
     *
     */
    @CsvOverridesAttribute(annotation=CsvMultiPad.class, name="chopped")
    boolean chopped() default false;
    
    /**
     * パディングの処理方法を指定します。
     * <p>{@link PaddingProcessor}を実装したクラス指定します。
     *  <br>標準では次の実装が提供されています。
     * </p>
     * <ul>
     *   <li>{@link SimplePaddingProcessor}：文字の種別にかかわらず１文字としてカウントしてパディングします。</li>
     *   <li>{@link CharWidthPaddingProcessor}：文字の幅（半角は1文字、全角は2文字）によってカウントしてパディングします。</li>
     *   <li>{@link ByteSizePaddingProcessor}：バイト数によってカウントしてパディングします。</li>
     * </ul>
     *
     * @return パディング処理の実装クラス。
     */
    @CsvOverridesAttribute(annotation=CsvMultiPad.class, name="paddingProcessor")
    Class<? extends PaddingProcessor> paddingProcessor() default CharWidthPaddingProcessor.class;
    
    /**
     * 右詰めするかどうか指定します。
     * <br>読み込み時のトリム時は、逆の意味になるので注意。
     * <br>falseの場合は、左詰めです。
     *
     * @return パディング文字が右側にあるかどうか。
     */
    @CsvOverridesAttribute(annotation=CsvMultiPad.class, name="rightAlign")
    @CsvOverridesAttribute(annotation=CsvOneSideTrim.class, name="leftAlign")
    boolean rightAlign() default false;
    
    /**
     * グループのクラスを指定します。
     * <p>処理ごとに適用するアノテーションを切り替えたい場合に指定します。
     * @return 指定しない場合は、{@link DefaultGroup}が適用され全ての処理に適用されます。
     */
    Class<?>[] groups() default {};
    
    /**
     * アノテーションを複数個指定する際の要素です。
     */
    @Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface List {
        
        CsvFixedSize[] value();
    }

}

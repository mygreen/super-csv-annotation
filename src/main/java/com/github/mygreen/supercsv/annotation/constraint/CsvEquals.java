package com.github.mygreen.supercsv.annotation.constraint;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Collection;

import org.supercsv.cellprocessor.ift.CellProcessor;

import com.github.mygreen.supercsv.annotation.DefaultGroup;
import com.github.mygreen.supercsv.builder.BuildCase;
import com.github.mygreen.supercsv.cellprocessor.constraint.EqualedValueProvider;
import com.github.mygreen.supercsv.cellprocessor.format.TextPrinter;

/**
 * セルの値が指定した値と等しいかどうか検証するためのアノテーションです。
 * <p>全てのクラスタイプに指定可能です。</p>
 * 
 * <h3 class="description">基本的な使い方</h3>
 * <p>属性{@link #value()}で比較対象の値を指定します。</p>
 * <ul>
 *   <li>値は複数指定可能で、その場合、何れかの値に一致すれば問題ありません。</li>
 *   <li>数値や日時など書式を持つ場合は、その書式に沿った値を指定する必要があります。</li>
 * </ul>
 * 
 * <pre class="highlight"><code class="java">
 * {@literal @CsvBean}
 * public class SampleCsv {
 *     
 *     {@literal @CsvColumn(number=1)}
 *     {@literal @CsvEquals({"admin", "normal"})}
 *     private String type;
 *     
 *     // 書式を指定している場合
 *     {@literal @CsvColumn(number=2)}
 *     {@literal @CsvNumberForamt(pattern="#,##0")}
 *     {@literal @CsvEquals({"-1,000", "1,000"})}
 *     private Integer number;
 *     
 *     // getter/setterは省略
 * }
 * </code></pre>
 * 
 * <h3 class="description">DBやファイルなどのリソースから取得する場合</h3>
 * <p>値をDBやファイルなどの別リソースから取得する場合は、属性{@link #provider()}にて、
 *    プロバイダ{@link EqualedValueProvider}の実装クラスを指定します。
 * </p>
 * <p>Spring Frameworkと連携している場合は、プロバイダクラスをSpringBeanとして登録しておくことでインジェクションできます。</p>
 * 
 * <pre class="highlight"><code class="java">
 * {@literal @CsvBean}
 * public class SampleCsv {
 *     
 *     {@literal @CsvColumn(number=1)}
 *     {@literal @CsvEquals(provider=FileEqualedValueProvider.class)}
 *     private Integer number;
 *     
 *     // setter/getterは省略
 * }
 * 
 * // プロバイダクラスの実装（ファイルから値を取得する）
 * public class FileEqualedValueProvider implements {@literal EqualedValueProvider<Integer>} {
 *     
 *     {@literal @Override}
 *     public {@literal Collection<Integer>} getEqualedValues(final FieldAccessor field) {
 *         
 *         String lines;
 *         try {
 *              lines = Files.readAllLines(
 *                      new File("equaled_value.txt").toPath(), Charset.forName("UTF-8"));
 *              
 *         } catch (IOException e) {
 *             throw new RuntimeException("fail reading the equaled value file.", e);
 *         }
 *         
 *         // 読み取った値をInteger型に変換します。
 *         return lines.stream()
 *             .map(l {@literal ->} Integer.valueOf(l))
 *             .collect(Collectors.toList());
 *         
 *     }
 * }
 * </code></pre>
 * 
 * 
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Repeatable(CsvEquals.List.class)
@CsvConstraint(value={})
public @interface CsvEquals {
    
    /**
     * 比較する値を指定します。
     * <p>複数指定した場合は、何れかに一致すれば問題ありません。</p>
     * <p>ブール型、数値や日時型の場合は、アノテーションで指定した書式に沿った値を指定する必要があります。</p>
     * @return 比較対象の値。
     */
    String[] value() default {};
    
    /**
     * 値を取得するプロバイダクラスを指定します。。
     * @return {@link EqualedValueProvider}の実装クラス。
     */
    @SuppressWarnings("rawtypes")
    Class<? extends EqualedValueProvider> [] provider() default {};
    
    
    /**
     * エラー時のメッセージを指定します。
     * <p>{@literal {key}}の書式の場合、プロパティファイルから取得した値を指定できます。</p>
     * 
     * <p>使用可能なメッセージ中の変数は下記の通りです。</p>
     * <ul>
     *   <li>lineNumber : カラムの値に改行が含まれている場合を考慮した実際の行番号です。1から始まります。</li>
     *   <li>rowNumber : CSVの行番号です。1から始まります。</li>
     *   <li>columnNumber : CSVの列番号です。1から始まります。</li>
     *   <li>label : カラムの見出し名です。</li>
     *   <li>validatedValue : 実際のカラムの値です。</li>
     *   <li>equalsValues : アノテーションの属性{@link #value()}をフィールドの型に変換した値です。{@link Collection}の形式です。</li>
     *   <li>formatter : カラムの値に対数するフォーマッタです。{@link TextPrinter#print(Object)}でvalidatedValue, equalsValuesの値を文字列に変換します。</li>
     * </ul>
     * 
     * @return 省略した場合は、適用された{@link CellProcessor}に基づいたメッセージが出力されます。
     */
    String message() default "{com.github.mygreen.supercsv.annotation.constraint.CsvEquals.message}";
    
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
    
    /**
     * アノテーションを複数個指定する際の要素です。
     */
    @Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface List {
        
        CsvEquals[] value();
    }
}

package com.github.mygreen.supercsv.annotation.conversion;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.github.mygreen.supercsv.annotation.DefaultGroup;
import com.github.mygreen.supercsv.builder.BuildCase;
import com.github.mygreen.supercsv.cellprocessor.conversion.ReplacedWordProvider;

/**
 * 語彙に一致した一部の文字列を置換するためのアノテーションです。
 * 
 * <h3 class="description">基本的な使い方</h3>
 * <ul>
 *   <li>属性{@link #words()}で置換前の対象となる語彙を指定します。
 *     <br>置換後の値を{@link #replacements()}で指定します。</li>
 *   <li>置換前と置換後の値は配列で複数指定で、指定のインデックスが同じものが対応する値となります。
 *     <br>属性{@link #words()}と{@link #replacements()}の配列のサイズが異なる場合は、エラーとなります。
 *   </li>
 *   <li>一度置換された値は、再度置換対象となりません。
 *     <br>循環参照のような定義をしていても、置換対象となりません。
 *   </li>
 *   <li>置換対象の文字は、文字長が長いものが優先されます。
 *     <br>文字長が同じ場合は、辞書順となります。
 *   </li>
 * </ul>
 * 
 * <pre class="highlight"><code class="java">
 * {@literal @CsvBean}
 * public class SampleCsv {
 *     
 *     // 下さい {@literal ->} ください
 *     // 御願い {@literal ->} お願い
 *     {@literal @CsvColumn(number=1)}
 *     {@literal @CsvWordReplace(words={"下さい", "御願い"}, replacements={"ください", "お願い"})}
 *     private String comment;
 *     
 *     // getter/setterは省略
 * }
 * </code></pre>
 * 
 * <h3 class="description">DBやファイルなどのリソースから取得する場合</h3>
 * <p>語彙をDBやファイルなどの別リソースから取得する場合は、属性{@link #provider()}にて、
 *    プロバイダ{@link ReplacedWordProvider}の実装クラスを指定します。
 * </p>
 * <p>Spring Frameworkと連携している場合は、プロバイダクラスをSpringBeanとして登録しておくことでインジェクションできます。</p>
 * 
 * <pre class="highlight"><code class="java">
 * {@literal @CsvBean}
 * public class SampleCsv {
 *     
 *     {@literal @CsvColumn(number=1)}
 *     {@literal @CsvWordReplace(provider=FileReplacedWordProvider.class)}
 *     private String comment;
 *     
 *     // setter/getterは省略
 * }
 * 
 * // プロバイダクラスの実装（ファイルから語彙を取得する）
 * public class FileReplacedWordProvider implements ReplacedWordProvider {
 *     
 *     {@literal @Override}
 *     public {@literal Collection<ReplacedWord>} getReplacedWords(final FieldAccessor field) {
 *         
 *         // ファイルから語彙の定義を読み込む
 *         {@literal List<String>} lines;
 *         try {
 *              
 *              lines = Files.readAllLines(
 *                      new File("replaced_word.txt").toPath(), Charset.forName("UTF-8"));
 *              
 *         } catch (IOException e) {
 *             throw new RuntimeException("fail reading the replaced words file.", e);
 *         }
 *         
 *         // 読み込んだ各行の値を分割して、ReplacedWord クラスに変換する。
 *         return lines.stream()
 *             .map(l {@literal ->} l.split(","))
 *             .map(s {@literal ->} new ReplacedWord(s[0], s[1]))
 *             .collect(Collectors.toLit());
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
@Repeatable(CsvWordReplace.List.class)
@CsvConversion(value={})
public @interface CsvWordReplace {
    
    /**
     * 置換対象の語彙の一覧
     * @return 複数指定する場合、{@link #replacements()}と同じ個数を設定します。
     */
    String[] words() default {};
    
    /**
     * 置換後の値の一覧
     * @return 複数指定する場合、{@link #words()}と同じ個数を設定します。
     */
    String[] replacements() default {};
    
    /**
     * 語彙を取得するプロバイダクラスを指定します。
     * @return {@link ReplacedWordProvider}の実装クラスを設定します。
     */
    Class<? extends ReplacedWordProvider>[] provider() default {};
    
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
        
        CsvWordReplace[] value();
    }
    
}

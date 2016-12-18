package com.github.mygreen.supercsv.annotation.format;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Collection;

import org.supercsv.cellprocessor.ift.CellProcessor;


/**
 * 列挙型の書式を定義するためのアノテーションです。
 * 
 * <h3 class="description">基本的な使い方</h3>
 * <p>列挙型の場合、アノテーションを付与しなくてもマッピングできます。
 *   <br>その際は、カラムの値と列挙型の要素の値をマッピングさせます。
 *   <br>要素の値とは、{@link Enum#name()}で取得できる値です。
 * </p>
 * <ul>
 *   <li>属性{@link #ignoreCase()}で、読み込み時に大文字・小文字の区別なく比較するか指定します。</li>
 * </ul>
 * 
 * <pre class="highlight"><code class="java">
 * {@literal @CsvBean}
 * public class SampleCsv {
 *       
 *     {@literal @CsvColumn(number=1, label="権限")}
 *     private RoleType role;
 *     
 *     // 読み込み時に大文字・小文字の区別を行いません。
 *     {@literal @CsvColumn(number=2, label="権限2")}
 *     {@literal @CsvEnumFormat(ignoreCase=true)}
 *     private RoleType role2;
 *     
 *     // setter/getterは省略
 * }
 * 
 * // 列挙型の定義
 * public enum RoleType {
 *     Normal, Admin;
 * }
 * </code></pre>
 * 
 * <h3 class="description">別名でマッピングする場合</h3>
 * <p>別名でマッピングする場合、属性{@link #selector()} で列挙型の要素の別名を取得するメソッド名を指定します。</p>
 * <p>以下の例では、読み込み時に入力値が {@literal 一般権限} の場合、 {@literal RoleType.Normal} にマッピングされます。</p>
 * 
 * <pre class="highlight"><code class="java">
 * {@literal @CsvBean}
 * public class SampleCsv {
 *       
 *     {@literal @CsvColumn(number=1, label="権限")}
 *     {@literal @CsvEnumFormat(selector="localeName")}
 *     private RoleType role;
 *     
 *     // setter/getterは省略
 * }
 * 
 * // 列挙型の定義
 * public enum RoleType {
 *     Normal("一般権限"), Admin("管理者権限");
 *     
 *     // 別名の設定
 *     private String localeName;
 *     
 *     private RoleType(String localeName) {
 *         this.localeName = localeName;
 *     }
 *     
 *     // 別名の取得用メソッド
 *     public String localeName() {
 *         return this.localeName;
 *     }
 * }
 * </code></pre>
 * 
 * @version 2.0
 * @author T.TSUCHIE
 *
 */
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CsvEnumFormat {
    
    /**
     * 読み込み時に、大文字・小文字を区別なく候補の値と比較して判定するか指定します。
     * @return trueの場合、大文字・小文字の区別は行いません。
     */
    boolean ignoreCase() default false;
    
    /**
     * 列挙型を{@link Enum#name()}メソッド以外から取得するときに指定します。
     * <p>例). Color.label()のlabel()メソッドを指定するときには、'label'と指定します。
     * @return 独自に実装した引数なしの文字列型を返すメソッドを指定します。省略した場合は、{@link Enum#name()}から取得できる値が採用されます。
     */
    String selector() default "";
    
    /**
     * エラー時のメッセージを指定します。
     * <p>{@literal {key}}の書式の場合、プロパティファイルから取得した値を指定できます。</p>
     * <p>このメッセージは、文字列を列挙型にパースする際に失敗したときに適用されるメッセージに使用します。</p>
     * 
     * <p>使用可能なメッセージ中の変数は下記の通りです。</p>
     * <ul>
     *   <li>lineNumber : カラムの値に改行が含まれている場合を考慮した実際の行番号です。1から始まります。</li>
     *   <li>rowNumber : CSVの行番号です。1から始まります。</li>
     *   <li>columnNumber : CSVの列番号です。1から始まります。</li>
     *   <li>label : カラムの見出し名です。</li>
     *   <li>validatedValue : 実際のカラムの値です。</li>
     *   <li>ignoreCase : アノテーションの属性{@link #ignoreCase()}の値です。</li>
     *   <li>selector : アノテーションの属性{@link #selector()}の値です。</li>
     *   <li>enums : 列挙型の値を{@link Collection}型に変換した値です。</li>
     * </ul>
     * 
     * @return 省略した場合は、適用された{@link CellProcessor}に基づいたメッセージが出力されます。
     */
    String message() default "{com.github.mygreen.supercsv.annotation.format.CsvEnumFormat.message}";
    
}

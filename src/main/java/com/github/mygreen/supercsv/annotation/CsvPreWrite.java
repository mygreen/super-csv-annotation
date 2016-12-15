package com.github.mygreen.supercsv.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * ライフサイクルイベントをハンドリングするためのアノテーションです。
 * <p>レコードの書き込み前に、このアノテーションを付与した任意のメソッドが実行されます。</p>
 * 
 * <ul>
 *   <li>戻り値なしのpublicメソッドに付与する必要があります。</li>
 *   <li>引数は、次の任意の値が指定可能で、定義順は関係ありません。
 *     <br>引数を取らないことも可能です。
 *   </li>
 * </ul>
 * 
 * <table class="description">
 *  <caption>メソッドに指定可能な引数一覧</caption>
 *  <thead>
 *  <tr>
 *   <th>引数のタイプ</th>
 *   <th>説明</th>
 *  </tr>
 *  </thead>
 *  <tbody>
 *  <tr>
 *   <td>{@link org.supercsv.util.CsvContext}</td>
 *   <td>CSVの1レコード分の情報。</td>
 *  </tr>
 *  <tr>
 *   <td>{@link com.github.mygreen.supercsv.validation.CsvBindingErrors}</td>
 *   <td>CSVの1レコード分のエラー情報。</td>
 *  </tr>
 *  <tr>
 *   <td>{@link Class}[]</td>
 *   <td>グループのクラス情報（配列）。</td>
 *  </tr>
 *  <tr>
 *   <td>処理対象のBeanクラス。</td>
 *   <td>処理対象のBeanオブジェクト。</td>
 *  </tr>
 *  </tbody>
 * </table>
 * 
 * 
 * <p>実装方法として、JavaBeanに直接処理を実装する方法と、リスナークラスを指定して別のクラスで実装する方法の2種類があります。</p>
 * 
 * <h3 class="description">JavaBeanクラスに実装する場合</h3>
 * 
 * <p>任意のメソッドにアノテーションを付与します。</p>
 * 
 * <pre class="highlight"><code class="java">
 * {@literal @CsvBean}
 * public class SampleCsv {
 *     
 *     {@literal @CsvColumn(number=1)}
 *     private String name;
 *     
 *     {@literal @CsvPreWrite}
 *     public void onPreWrite(CsvContext csvContext, CsvBindingErrors bindingErrors) {
 *         //任意の処理の実装
 *         
 *     }
 *     
 *     // getter/setterは省略
 * }
 * </code></pre>
 * 
 * <h3 class="description">リスナークラスに実装する場合</h3>
 * 
 * <p>POJOであるリスナークラスの任意のメソッドにアノテーションを付与します。</p>
 * <p>Spring Frameworkと連携している場合、リスナークラスをSpringBeanとして登録しているとでインジェクションできます。</p>
 * 
 * <pre class="highlight"><code class="java">
 * {@literal @CsvBean(listeners=SampleListener.class)}  // リスナークラスの指定
 * public class SampleCsv {
 *     
 *     {@literal @CsvColumn(number=1)}
 *     private String name;
 *     
 *     // getter/setterは省略
 * }
 * 
 * // SampleCsvに対するリスナー
 * public static class SampleListener {
 * 
 *     {@literal @CsvPreWrite}
 *     public void onPreWrite(SampleCsv record, CsvContext csvContext, CsvBindingErrors bindingErrors) {
 *         //任意の処理の実装
 *         
 *     }
 * }
 * </code></pre>
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CsvPreWrite {
    
}

package com.github.mygreen.supercsv.cellprocessor.constraint;

import java.util.Collection;

import com.github.mygreen.supercsv.annotation.constraint.CsvEquals;
import com.github.mygreen.supercsv.builder.FieldAccessor;

/**
 * {@link CsvEquals}による等値かどうか比較する際の値を提供するインタフェースです。
 * <p>値を別ファイルやDBから取得する時などサービスクラスとして実装します。</p>
 * <p>基本的な使い方は、 {@link CsvEquals}のJavaDocを参照してください。</p>
 * 
 * <h3 class="description">フィールドごとにリソースを切り替えたい場合</h3>
 * <p>フィールドごとにリソースを切り替えたい場合は、メソッドの引数{@link FieldAccessor}で判定を行います。</p>
 * <p>また、独自のパラメータを渡したい時は、独自のアノテーションを作成し、それをフィールドに付与して、
 *    引数{@link FieldAccessor}から取得して判定します。
 * </p>
 * 
 * <pre class="highlight"><code class="java">
 * // 読み込むリソースを定義されているフィールドやクラスで分ける場合
 * public class FileEqualedValueProvider implements {@literal EqualedValueProvider<Integer>} {
 *     
 *     {@literal @Override}
 *     public {@literal Collection<Integer>} getEqualedValues(final FieldAccessor field) {
 *         
 *         final String path;
 *         if(field.getDeclaredClass().equals(AdminCsv.class)) {
 *             path = "forbbidden_word_admin.txt";
 *         } else {
 *             path = "forbbidden_word.txt";
 *         }
 *         
 *         String lines;
 *         try {
 *              lines = Files.readAllLines(new File(path).toPath(), Charset.forName("UTF-8"));
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
 * <h3 class="description">Spring Frameworkと連携する場合</h3>
 * <p>Spring Frameworkと連携している場合は、プロバイダクラスをSpringBeanとして登録しておくことでインジェクションされます。</p>
 * <p>また、メソッド{@link EqualedValueProvider#getEqualedValues(FieldAccessor)}は、定義したフィールド単位に呼ばれるため、
 *   多数のフィールドで定義していると何度も呼ばれ、効率が悪くなる場合があります。
 *   <br>このようなときは、Spring Framework 3.1から追加された Cache Abstraction(キャッシュの抽象化)機能を使うと改善できます。
 * </p>
 * 
 * <pre class="highlight"><code class="java">
 * // SpringBeanとして登録する場合。
 * {@literal @Service}
 * {@literal @Transactional}
 * public class EqualedValueProviderImpl implements {@literal EqualedValueProvider<Integer>} {
 *     
 *     // リポジトリのインジェクション
 *     {@literal @Autowired}
 *     private EqualedValueRepository equaledValueRepository;
 *     
 *     {@literal @Override}
 *     public {@literal Collection<Integer>} getEqualedValues(final FieldAccessor field) {
 *         
 *         final Role role;
 *         if(field.getDeclaredClass().equals(AdminCsv.class)) {
 *             role = Role.admin;
 *         } else {
 *             role = Role.normal;
 *         }
 *         
 *         return loadWords(role).stream()
 *             .map(dto {@literal ->} dto.getValue())
 *             .collect(Collectors.toList());
 *         
 *     }
 *     
 *     // リポジトリから取得した内容をキャッシュする。
 *     // 引数 role をキーにして、区別する。
 *     {@literal @Transactional(readOnly = true)}
 *     {@literal @Cacheable(cacheNames="forbbidenWords", key="#role")}
 *     public {@literal List<EqualedDto>} loadWords(Role role) {
 *          
 *          if(role.euals(Role.admin)) {
 *              return equaledValueRepository.findByRole(role);
 *          } else {
 *              return equaledValueRepository.findAll();
 *          }
 *          
 *     }
 * }
 * </code></pre>
 * 
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
@FunctionalInterface
public interface EqualedValueProvider<T> {
    
    /**
     * 比較対象の値を取得します。
     * @param field フィールド情報
     * @return 比較対処の値のリストを返します。
     */
    Collection<T> getEqualedValues(FieldAccessor field);
    
}

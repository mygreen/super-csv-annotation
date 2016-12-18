package com.github.mygreen.supercsv.cellprocessor.constraint;

import java.util.Collection;

import com.github.mygreen.supercsv.annotation.constraint.CsvWordRequire;
import com.github.mygreen.supercsv.builder.FieldAccessor;

/**
 * {@link CsvWordRequire}による必須語彙のチェックを行う際の語彙を提供するためのインタフェースです。
 * <p>語彙を別ファイルやDBから取得する時などサービスクラスとして実装します。</p>
 * 
 * <p>基本的な使い方は、 {@link CsvWordRequire}のJavaDocを参照してください。</p>
 * 
 * <h3 class="description">フィールドごとにリソースを切り替えたい場合</h3>
 * <p>フィールドごとにリソースを切り替えたい場合は、メソッドの引数{@link FieldAccessor}で判定を行います。</p>
 * <p>また、独自のパラメータを渡したい時は、独自のアノテーションを作成し、それをフィールドに付与して、
 *    引数{@link FieldAccessor}から取得して判定します。
 * </p>
 * 
 * <pre class="highlight"><code class="java">
 * // 読み込むリソースを定義されているフィールドやクラスで分ける場合
 * public class FileRequiredWordProvider implements RequiredWordProvider {
 *     
 *     {@literal @Override}
 *     public {@literal Collection<String>} getRequiredWords(final FieldAccessor field) {
 *         
 *         final String path;
 *         if(field.getDeclaredClass().equals(AdminCsv.class)) {
 *             path = "required_word_admin.txt";
 *         } else {
 *             path = "required_word.txt";
 *         }
 *         
 *         try {
 *              return Files.readAllLines(new File(path).toPath(), Charset.forName("UTF-8"));
 *              
 *         } catch (IOException e) {
 *             throw new RuntimeException("fail reading the required words file.", e);
 *         }
 *         
 *     }
 * }
 * </code></pre>
 * 
 * <h3 class="description">Spring Frameworkと連携する場合</h3>
 * <p>Springと連携している場合は、プロバイダクラスをSpringBeanとして登録しておくことでインジェクションできます。</p>
 * <p>また、メソッド{@link RequiredWordProvider#getRequiredWords(FieldAccessor)}は、定義したフィールド単位に呼ばれるため、多数のフィールドで定義していると何度も呼ばれ、効率が悪くなる場合があります。
 *   <br>このようなときは、Spring Framework3.1から追加された Cache Abstraction(キャッシュの抽象化)機能を使うと改善できます。
 * </p>
 * 
 * <pre class="highlight"><code class="java">
 * // SpringBeanとして登録する場合。
 * {@literal @Service}
 * {@literal @Transactional}
 * public class RequiredWordProviderImpl implements RequiredWordProvider {
 *     
 *     // リポジトリのインジェクション
 *     {@literal @Autowired}
 *     private RequiredWordRepository requiredWordRepository;
 *     
 *     {@literal @Override}
 *     public {@literal Collection<String>} getRequiredWords(final FieldAccessor field) {
 *         
 *         final Role role;
 *         if(field.getDeclaredClass().equals(AdminCsv.class)) {
 *             role = Role.admin;
 *         } else {
 *             role = Role.normal;
 *         }
 *         
 *         return loadWords(role).stream()
 *             .map(dto {@literal ->} dto.getWord())
 *             .collect(Collectors.toList());
 *         
 *     }
 *     
 *     // リポジトリから取得した内容をキャッシュする。
 *     // 引数 role をキーにして、区別する。
 *     {@literal @Transactional(readOnly = true)}
 *     {@literal @Cacheable(cacheNames="requiredWords", key="#role")}
 *     public {@literal List<WordDto>} loadWords(Role role) {
 *          
 *          if(role.euals(Role.admin)) {
 *              return requiredWordRepository.findByRole(role);
 *          } else {
 *              return requiredWordRepository.findAll();
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
public interface RequiredWordProvider {
    
    /**
     * 語彙の一覧を取得する。
     * @param field フィールド情報
     * @return 語彙を返します。チェック対象の文字がない場合は、空のリストを返します。
     */
    Collection<String> getRequiredWords(FieldAccessor field);
    
}

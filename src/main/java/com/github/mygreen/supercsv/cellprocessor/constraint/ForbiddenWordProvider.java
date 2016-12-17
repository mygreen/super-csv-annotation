package com.github.mygreen.supercsv.cellprocessor.constraint;

import java.util.Collection;

import com.github.mygreen.supercsv.annotation.constraint.CsvWordForbid;
import com.github.mygreen.supercsv.builder.FieldAccessor;

/**
 * {@link CsvWordForbid}による禁止語彙のチェックを行う際の語彙を提供するためのインタフェースです。
 * <p>語彙を別ファイルやDBから取得する時などサービスクラスとして実装します。</p>
 * <p>基本的な使い方は、 {@link CsvWordForbid}のJavaDocを参照してください。</p>
 * 
 * <h3 class="description">フィールドごとにリソースを切り替えたい場合</h3>
 * <p>フィールドごとにリソースを切り替えたい場合は、メソッドの引数{@link FieldAccessor}で判定を行います。</p>
 * <p>また、独自のパラメータを渡したい時は、独自のアノテーションを作成し、それをフィールドに付与して、
 *    引数{@link FieldAccessor}から取得して判定します。
 * </p>
 * 
 * <pre class="highlight"><code class="java">
 * // 読み込むリソースを定義されているフィールドやクラスで分ける場合
 * public class FileForbiddenWordProvider implements ForbiddenWordProvider {
 *     
 *     {@literal @Override}
 *     public {@literal Collection<String>} getForbiddenWords(final FieldAccessor field) {
 *         
 *         final String path;
 *         if(field.getDeclaredClass().equals(AdminCsv.class)) {
 *             path = "forbbidden_word_admin.txt";
 *         } else {
 *             path = "forbbidden_word.txt";
 *         }
 *         
 *         try {
 *              return Files.readAllLines(new File(path).toPath(), Charset.forName("UTF-8"));
 *              
 *         } catch (IOException e) {
 *             throw new RuntimeException("fail reading the forbidden words file.", e);
 *         }
 *         
 *     }
 * }
 * </code></pre>
 * 
 * <h3 class="description">Spring Frameworkと連携する場合</h3>
 * <p>Spring Frameworkと連携している場合は、プロバイダクラスをSpringBeanとして登録しておくことでインジェクションされます。</p>
 * <p>また、メソッド{@link ForbiddenWordProvider#getForbiddenWords(FieldAccessor)}は、定義したフィールド単位に呼ばれるため、
 *   多数のフィールドで定義していると何度も呼ばれ、効率が悪くなる場合があります。
 *   <br>このようなときは、Spring Framework 3.1から追加された Cache Abstraction(キャッシュの抽象化)機能を使うと改善できます。
 * </p>
 * 
 * <pre class="highlight"><code class="java">
 * // SpringBeanとして登録する場合。
 * {@literal @Service}
 * {@literal @Transactional}
 * public class ForbiddenWordProviderImpl implements ForbiddenWordProvider {
 *     
 *     // リポジトリのインジェクション
 *     {@literal @Autowired}
 *     private ForbiddenWordRepository forbiddenWordRepository;
 *     
 *     {@literal @Override}
 *     public {@literal Collection<String>} getForbiddenWords(final FieldAccessor field) {
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
 *     {@literal @Cacheable(cacheNames="forbbidenWords", key="#role")}
 *     public {@literal List<WordDto>} loadWords(Role role) {
 *          
 *          if(role.euals(Role.admin)) {
 *              return forbiddenWordRepository.findByRole(role);
 *          } else {
 *              return forbiddenWordRepository.findAll();
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
public interface ForbiddenWordProvider {
    
    /**
     * 語彙の一覧を取得します。
     * @param field フィールド情報。フィールドによって取得するリソースを切り替える際に利用します。
     * @return 語彙を返します。チェック対象の文字がない場合は、空のリストを返します。
     */
    Collection<String> getForbiddenWords(FieldAccessor field);
    
}

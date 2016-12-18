package com.github.mygreen.supercsv.cellprocessor.conversion;

import java.io.Serializable;
import java.util.Collection;

import com.github.mygreen.supercsv.annotation.conversion.CsvWordReplace;
import com.github.mygreen.supercsv.builder.FieldAccessor;
import com.github.mygreen.supercsv.util.ArgUtils;

/**
 * {@link CsvWordReplace}による語彙による置換処理をを個ナウ際の語彙を提供するためのインタフェースです。
 * <p>語彙を別ファイルやDBから取得する時などサービスクラスとして実装します。</p>
 * 
 * <p>基本的な使い方は、 {@link CsvWordReplace}のJavaDocを参照してください。</p>
 * 
 * <h3 class="description">フィールドごとにリソースを切り替えたい場合</h3>
 * <p>フィールドごとにリソースを切り替えたい場合は、メソッドの引数{@link FieldAccessor}で判定を行います。</p>
 * <p>また、独自のパラメータを渡したい時は、独自のアノテーションを作成し、それをフィールドに付与して、
 *    引数{@link FieldAccessor}から取得して判定します。
 * </p>
 * 
 * <pre class="highlight"><code class="java">
 * // 読み込むリソースを定義されているフィールドやクラスで分ける場合
 * public class FileReplacedWordProvider implements ReplacedWordProvider {
 *     
 *     {@literal @Override}
 *     public {@literal Collection<Word>} getReplacedWords(final FieldAccessor field) {
 *         
 *         final String path;
 *         if(field.getDeclaredClass().equals(AdminCsv.class)) {
 *             path = "replaced_word_admin.txt";
 *         } else {
 *             path = "replaced_word.txt";
 *         }
 *         
 *         // ファイルから語彙の定義を読み込む
 *         {@literal List<String>} lines;
 *         try {
 *              
 *              lines = Files.readAllLines(new File(path).toPath(), Charset.forName("UTF-8"));
 *              
 *         } catch (IOException e) {
 *             throw new RuntimeException("fail reading the replaced words file.", e);
 *         }
 *         
 *         // 読み込んだ各行の値を分割して、ReplacedWordProvider.Word クラスに変換する。
 *         return lines.stream()
 *             .map(l {@literal ->} l.split(","))
 *             .map(s {@literal ->} new Word(s[0], s[1]))
 *             .collect(Collectors.toLit());
 *         
 *     }
 * }
 * </code></pre>
 * 
 * <h3 class="description">Spring Frameworkと連携する場合</h3>
 * <p>Springと連携している場合は、プロバイダクラスをSpringBeanとして登録しておくことでインジェクションできます。</p>
 * <p>また、メソッド{@link ReplacedWordProvider#getReplacedWords(FieldAccessor)}は、定義したフィールド単位に呼ばれるため、多数のフィールドで定義していると何度も呼ばれ、効率が悪くなる場合があります。
 *   <br>このようなときは、Spring Framework3.1から追加された Cache Abstraction(キャッシュの抽象化)機能を使うと改善できます。
 * </p>
 * 
 * <pre class="highlight"><code class="java">
 * // SpringBeanとして登録する場合。
 * {@literal @Service}
 * {@literal @Transactional}
 * public class ReplacedWordProviderImpl implements ReplacedWordProvider {
 *     
 *     // リポジトリのインジェクション
 *     {@literal @Autowired}
 *     private ReplacedWordRepository replacedWordRepository;
 *     
 *     {@literal @Override}
 *     public {@literal Collection<Word>} getReplacedWords(final FieldAccessor field) {
 *         
 *         final Role role;
 *         if(field.getDeclaredClass().equals(AdminCsv.class)) {
 *             role = Role.admin;
 *         } else {
 *             role = Role.normal;
 *         }
 *         
 *         return loadWords(role).stream()
 *             .map(dto {@literal ->} new Word(dto.getWord(), dto.getReplacement()))
 *             .collect(Collectors.toList());
 *         
 *     }
 *     
 *     // リポジトリから取得した内容をキャッシュする。
 *     // 引数 role をキーにして、区別する。
 *     {@literal @Transactional(readOnly = true)}
 *     {@literal @Cacheable(cacheNames="replacedWords", key="#role")}
 *     public {@literal List<WordDto>} loadWords(Role role) {
 *          
 *          if(role.euals(Role.admin)) {
 *              return replacedWordRepository.findByRole(role);
 *          } else {
 *              return replacedWordRepository.findAll();
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
public interface ReplacedWordProvider {
    
    /**
     * 語彙の一覧を取得する。
     * @param field フィールド情報
     * @return 語彙を返します。チェック対象の文字がない場合は、空のリストを返します。
     */
    Collection<Word> getReplacedWords(FieldAccessor field);
    
    /**
     * 置換語彙を表現するクラス。
     *
     * @since 2.0.1
     * @author T.TSUCHIE
     *
     */
    public static class Word implements Serializable {
        
        /** serialVersionUID */
        private static final long serialVersionUID = 1L;
        
        private final String word;
        
        private final String replacement;
        
        /**
         * 置換語彙のコンストラクタ。
         * @param word 置換対象の文字。
         * @param replacement 置換後の文字。
         * @throws NullPointerException word or replacement is null.
         * @throws IllegalArgumentException word is empty.
         */
        public Word(final String word, final String replacement) {
            ArgUtils.notEmpty(word, "word");
            ArgUtils.notNull(replacement, "replacement");
            
            this.word = word;
            this.replacement = replacement;
        }
        
        /**
         * 置換対象の語彙を取得する。
         * @return 置換対象の語彙。
         */
        public String getWord() {
            return word;
        }
        
        /**
         * 置換後の文字列を返します。
         * @return 置換語彙の文字列
         */
        public String getReplacement() {
            return replacement;
        }
        
    }
    
}

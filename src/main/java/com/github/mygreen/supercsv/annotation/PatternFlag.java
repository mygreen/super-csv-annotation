package com.github.mygreen.supercsv.annotation;


/**
 * 正規表現をコンパイルする際のフラグを表現するための列挙型。
 * <p>アノテーションの正規表現の属性として指定するために使用する。</p>
 * 
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public enum PatternFlag {
    
    /**
     * Unixライン・モードを有効にします。
     * <p>このモードでは、「{@literal \n}」行末記号以外は、{@literal .、^、および$}の動作で認識されません。</p>
     *
     * @see java.util.regex.Pattern#UNIX_LINES
     */
    UNIX_LINES( java.util.regex.Pattern.UNIX_LINES ),
    
    /**
     * 大文字と小文字を区別しないマッチングを有効にします。
     * <p>デフォルトの大文字と小文字を区別しないマッチングでは、US-ASCII文字セットの文字だけがマッチします。
     *    Unicodeに準拠した大文字と小文字を区別しないマッチングを有効にするには、
     *    {@link #UNIX_LINES}フラグをこのフラグと組み合わせて指定します。
     * </p>
     *
     * @see java.util.regex.Pattern#CASE_INSENSITIVE
     */
    CASE_INSENSITIVE( java.util.regex.Pattern.CASE_INSENSITIVE ),
    
    /**
     * パターン内で空白とコメントを使用できるようにします。
     * <p>このモードでは、空白は無視され、#で始まる埋込みコメントは行末まで無視されます。</p>
     *
     * @see java.util.regex.Pattern#COMMENTS
     */
    COMMENTS( java.util.regex.Pattern.COMMENTS ),
    
    /**
     * 複数行モードを有効にします。
     * <p>複数行モードでは、表現{@literal ^と$}は、それぞれ行末記号または入力シーケンスの末尾の直後または直前にマッチします。
     *    デフォルトでは、これらの表現は入力シーケンス全体の先頭と末尾にだけマッチします。
     * </p>
     * 
     * @see java.util.regex.Pattern#MULTILINE
     */
    MULTILINE( java.util.regex.Pattern.MULTILINE ),
    
    /**
     * DOTALLモードを有効にします。
     * <p>DOTALLモードでは、表現.は行末記号を含む任意の文字にマッチします。
     *    デフォルトでは、この表現は行末記号にマッチしません。
     * </p>
     *
     * @see java.util.regex.Pattern#DOTALL
     */
    DOTALL( java.util.regex.Pattern.DOTALL ),
    
    /**
     * Unicodeに準拠した大文字と小文字を区別しないマッチングを有効にします。
     * <p>このフラグと{@link #CASE_INSENSITIVE}フラグを同時に指定した場合は、
     *    Unicode標準に準拠した大文字と小文字を区別しないマッチングが行われます。
     *    デフォルトの大文字と小文字を区別しないマッチングでは、US-ASCII文字セットの文字だけがマッチします。
     * </p>
     *
     * @see java.util.regex.Pattern#UNICODE_CASE
     */
    UNICODE_CASE( java.util.regex.Pattern.UNICODE_CASE ),
    
    /**
     * 正規等価を有効にします。
     * <p>このフラグを指定したときは、2つの文字の完全な正規分解がマッチした場合に限り、それらの文字がマッチするとみなされます。
     *    たとえば、このフラグを指定すると、表現「{@literal a\u030A}」は文字列「{@literal \u00E5}」にマッチします。
     *    デフォルトのマッチングでは、正規等価が考慮されません。
     * </p>
     *
     * @see java.util.regex.Pattern#CANON_EQ
     */
    CANON_EQ( java.util.regex.Pattern.CANON_EQ );
    
    //JDK flag value
    private final int value;
    
    private PatternFlag(int value) {
        this.value = value;
    }
    
    /**
     * @return {@link java.util.regex.Pattern}に対応するフラグの値を返す。
     */
    public int getValue() {
        return value;
    }
    
}

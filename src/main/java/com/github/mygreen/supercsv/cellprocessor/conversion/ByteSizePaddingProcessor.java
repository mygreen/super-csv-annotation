package com.github.mygreen.supercsv.cellprocessor.conversion;

import java.nio.charset.Charset;

import com.github.mygreen.supercsv.util.ArgUtils;

/**
 * 文字数をバイトサイズでカウントして、パディングする。
 * <p>バイト数は、エンコードによって変わるため、環境によってこのクラスを継承しクラスを使用してください。</p>
 *
 * @since 2.1
 * @author T.TSUCHIE
 *
 */
public abstract class ByteSizePaddingProcessor extends AbstractPaddingOperator {
    
    private final Charset charset;
    
    /**
     * 文字コードを指定するコンストラクタ
     * @param charset 文字コード
     * @throws NullPointerException {@literal charset is null.}
     */
    public ByteSizePaddingProcessor(final Charset charset) {
        ArgUtils.notNull(charset, "charset");
        this.charset = charset;
    }
    
    @Override
    public int count(int codePoint) {
        return count(String.valueOf(Character.toChars(codePoint)));
    }
    
    @Override
    public int count(final String text) {
        ArgUtils.notNull(text, "text");
        
        return text.getBytes(charset).length;
    }
    
    /**
     * UTF-8でエンコードしたバイト数をカウントして、パディングする。
     */
    public static class Utf8 extends ByteSizePaddingProcessor {
        
        public Utf8() {
            super(Charset.forName("UTF-8"));
        }
        
    };
    
    /**
     * Windows-31j(CP932)でエンコードしたバイト数をカウントして、パディングする。
     */
    public static class Windows31j extends ByteSizePaddingProcessor {
        
        public Windows31j() {
            super(Charset.forName("Windows-31j"));
        }
        
    };
    
    /**
     * EUC-JPでエンコードしたバイト数をカウントして、パディングする。
     */
    public static class EucJp extends ByteSizePaddingProcessor {
        
        public EucJp() {
            super(Charset.forName("EUC-JP"));
        }
        
    };

}

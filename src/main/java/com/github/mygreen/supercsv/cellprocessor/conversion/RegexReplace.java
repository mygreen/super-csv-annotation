package com.github.mygreen.supercsv.cellprocessor.conversion;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;
import org.supercsv.util.CsvContext;

/**
 * 文字列を置換する{@link CellProcessor}です。
 * 
 * @version 2.2
 * @since 1.2
 * @author T.TSUCHIE
 *
 */
public class RegexReplace extends CellProcessorAdaptor implements StringCellProcessor {
    
    private final Pattern pattern;
    
    private final String replacement;
    
    private final boolean partialMatched;
    
    /**
     * 正規表現と置換文字を指定してインスタンスを作成するコンストラクタ。
     * 
     * @param pattern コンパイル済みの正規表現。
     * @param replacement 置換文字列
     * @param partialMatched 部分一致で検索するかどうか。
     * @throws NullPointerException if pattern or replacement is null.
     */
    public RegexReplace(final Pattern pattern, final String replacement, final boolean partialMatched) {
        super();
        checkPreconditions(pattern, replacement);
        this.pattern = pattern;
        this.replacement = replacement;
        this.partialMatched = partialMatched;
    }
    
    /**
     * 正規表現と置換文字を指定してインスタンスを作成するコンストラクタ。
     * 
     * @param pattern コンパイル済みの正規表現。
     * @param replacement 置換文字列
     * @param partialMatched 部分一致で検索するかどうか。
     * @param next チェインの中で呼ばれる次の{@link CellProcessor}.
     * @throws NullPointerException if pattern or replacement is null.
     */
    public RegexReplace(final Pattern pattern, final String replacement, final boolean partialMatched,
            final StringCellProcessor next) {
        super(next);
        checkPreconditions(pattern, replacement);
        this.pattern = pattern;
        this.replacement = replacement;
        this.partialMatched = partialMatched;
    }
    
    /**
     * コンスタによるインスタンスを生成する際の前提条件となる引数のチェックを行う。
     * 
     * @param pattern コンパイル済みの正規表現
     * @param replacement 置換する文字。
     * @throws NullPointerException if pattern or replacement is null.
     */
    private static void checkPreconditions(final Pattern pattern, final String replacement) {
        if(pattern == null ) {
            throw new NullPointerException("regex should not be null");
        }
        
        if(replacement == null) {
            throw new NullPointerException("replacement should not be null");
        }
    }
    
    @Override
    public <T> T execute(final Object value, final CsvContext context) {
        
        if(value == null) {
            return next.execute(value, context);
        }
        
        final Matcher matcher = pattern.matcher(value.toString());
        final boolean matched = partialMatched ? matcher.find() : matcher.matches();
        if(matched) {
            final String result = matcher.replaceAll(replacement);
            return next.execute(result, context);
        }
        
        return next.execute(value, context);
    }
    
    /**
     * 
     * @return 設定せれた正規表現
     */
    public String getRegex() {
        return pattern.toString();
    }
    
    /**
     * 
     * @return 正規表現のフラグ
     */
    public int getFlags() {
        return pattern.flags();
    }
    
    /**
     * 
     * @return 置換文字列を取得する
     */
    public String getReplacement() {
        return replacement;
    }

    /**
     * 
     * @return 部分一致で検索するかどうか。
     */
    public boolean isPartialMatched() {
        return partialMatched;
    }
    
    
}

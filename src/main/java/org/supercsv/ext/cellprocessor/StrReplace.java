package org.supercsv.ext.cellprocessor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.cellprocessor.ift.BoolCellProcessor;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.ift.DateCellProcessor;
import org.supercsv.cellprocessor.ift.DoubleCellProcessor;
import org.supercsv.cellprocessor.ift.LongCellProcessor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;
import org.supercsv.util.CsvContext;

/**
 * 文字列が正規表現に一致するかチェックする{@link CellProcessor}です。
 * 
 * @since 1.2
 * @author T.TSUCHIE
 *
 */
public class StrReplace extends CellProcessorAdaptor implements BoolCellProcessor, DateCellProcessor,
        DoubleCellProcessor, LongCellProcessor, StringCellProcessor {
    
    private final Pattern pattern;
    
    private final String replacement;
    
    /**
     * 正規表現と置換文字を指定してインスタンスを作成するコンストラクタ。
     * 
     * @param pattern コンパイル済みの正規表現。
     * @param replacement 置換文字列
     * @throws NullPointerException if pattern or replacement is null.
     */
    public StrReplace(final Pattern pattern, final String replacement) {
        super();
        checkPreconditions(pattern, replacement);
        this.pattern = pattern;
        this.replacement = replacement;
    }
    
    /**
     * 正規表現と置換文字を指定してインスタンスを作成するコンストラクタ。
     * 
     * @param pattern コンパイル済みの正規表現。
     * @param replacement 置換文字列
     * @param next チェインの中で呼ばれる次の{@link CellProcessor}.
     * @throws NullPointerException if pattern or replacement is null.
     */
    public StrReplace(final Pattern pattern, final String replacement, final StringCellProcessor next) {
        super(next);
        checkPreconditions(pattern, replacement);
        this.pattern = pattern;
        this.replacement = replacement;
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
        
        validateInputNotNull(value, context);
        
        final Matcher matcher = pattern.matcher(value.toString());
        if(matcher.matches()) {
            final String result = matcher.replaceAll(replacement);
            return next.execute(result, context);
        }
        
        return next.execute(value, context);
    }
    
}

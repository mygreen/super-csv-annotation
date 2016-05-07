package org.supercsv.ext.cellprocessor.constraint;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;
import org.supercsv.exception.SuperCsvConstraintViolationException;
import org.supercsv.ext.cellprocessor.ift.ValidationCellProcessor;
import org.supercsv.util.CsvContext;

/**
 * 文字列が正規表現に一致するかチェックする{@link CellProcessor}です。
 * 
 * @since 1.2
 * @author T.TSUCHIE
 * 
 *
 */
public class StrPattern extends CellProcessorAdaptor implements StringCellProcessor, ValidationCellProcessor {
    
    private final Pattern pattern;
    
    /**
     * 正規表現を指定してインスタンスを作成するコンストラクタ。
     * 
     * @param pattern コンパイル済みの正規表現
     * @throws NullPointerException pattern is null.
     */
    public StrPattern(final Pattern pattern) {
        super();
        checkPreconditions(pattern);
        this.pattern = pattern;
    }
    
    /**
     * 正規表現を指定してインスタンスを作成するコンストラクタ。
     * 
     * @param pattern コンパイル済みの正規表現
     * @param next チェインの中で呼ばれる次の{@link CellProcessor}.
     * @throws NullPointerException pattern is null.
     */
    public StrPattern(final Pattern pattern, final StringCellProcessor next) {
        super(next);
        checkPreconditions(pattern);
        this.pattern = pattern;
    }
    
    /**
     * コンスタによるインスタンスを生成する際の前提条件となる引数のチェックを行う。
     * 
     * @param pattern コンパイル済みの正規表現
     * @throws NullPointerException if pattern is null
     */
    private static void checkPreconditions(final Pattern pattern) {
        if( pattern == null ) {
            throw new NullPointerException("pattern should not be null");
        }
    }
    
    @Override
    public <T> T execute(final Object value, final CsvContext context) {
        
        validateInputNotNull(value, context);
        
        final boolean matches = pattern.matcher((String) value).matches();
        if(!matches) {
            final String regex = getRegex();
            throw new SuperCsvConstraintViolationException(
                    String.format("'%s' does not match the regular expression '%s'", value, regex), context, this);
        }
        
        return next.execute(value, context);
    }
    
    @Override
    public Map<String, ?> getMessageVariable() {
        Map<String, Object> vars = new HashMap<String, Object>();
        vars.put("regex", getRegex());
        return vars;
    }
    
    /**
     * 設定した正規表現の式を取得する。
     * @return 正規表現の式。
     */
    public String getRegex() {
        return pattern.pattern();
    }
    
}

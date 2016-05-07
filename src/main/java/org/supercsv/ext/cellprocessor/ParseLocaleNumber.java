package org.supercsv.ext.cellprocessor;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;
import org.supercsv.exception.SuperCsvCellProcessorException;
import org.supercsv.ext.cellprocessor.ift.ValidationCellProcessor;
import org.supercsv.ext.util.ConversionException;
import org.supercsv.ext.util.NumberFormatWrapper;
import org.supercsv.util.CsvContext;


/**
 * 文字列を解析し、書式付きの数値に変換する{@link CellProcessor}.
 * <p>解析する処理は、スレッドセーフです。</p>
 * 
 * @version 1.2
 * @author T.TSUCHIE
 *
 */
public class ParseLocaleNumber<N extends Number> extends CellProcessorAdaptor
        implements StringCellProcessor, ValidationCellProcessor {
    
    /**
     * 変換後のクラスタイプ。
     */
    private final Class<N> type;
    
    protected final NumberFormatWrapper formatter;
    
    /**
     * フォーマッタを指定してインスタンスを作成するコンストラクタ。
     * 
     * @param type 変換後の数値のクラスタイプ。
     * @param formatter 数値のフォーマッタ。
     * @param lenient 厳密に解析しないかどうか。
     * @throws NullPointerException if type or formatter is null.
     */
    public ParseLocaleNumber(final Class<N> type, final NumberFormat formatter, final boolean lenient) {
        super();
        checkPreconditions(type, formatter);
        this.type = type;
        this.formatter = new NumberFormatWrapper(formatter, lenient);
    }
    
    /**
     * フォーマッタを指定してインスタンスを作成するコンストラクタ。
     * 
     * @param type 変換後の数値のクラスタイプ。
     * @param formatter 数値のフォーマッタ。
     * @param lenient 厳密に解析しないかどうか。
     * @param next チェインの中で呼ばれる次の{@link CellProcessor}.
     * @throws NullPointerException if type or formatter is null.
     */
    public ParseLocaleNumber(final Class<N> type, final NumberFormat formatter, final boolean lenient, final CellProcessor next) {
        super(next);
        checkPreconditions(type, formatter);
        this.type = type;
        this.formatter = new NumberFormatWrapper(formatter, lenient);
        
    }
    
    /**
     * フォーマッタを指定してインスタンスを作成するコンストラクタ。
     * <p>厳密に解析は行いません。</p>
     * @param type 変換後の数値のクラスタイプ。
     * @param formatter 数値のフォーマッタ。
     */
    public ParseLocaleNumber(final Class<N> type, final NumberFormat formatter) {
        this(type, formatter, false);
    }
    
    /**
     * フォーマッタを指定してインスタンスを作成するコンストラクタ。
     * <p>厳密に解析は行いません。</p>
     * @param type 変換後の数値のクラスタイプ。
     * @param formatter 数値のフォーマッタ。
     * @param next チェインの中で呼ばれる次の{@link CellProcessor}.
     */
    public ParseLocaleNumber(final Class<N> type, final NumberFormat formatter, final CellProcessor next) {
        this(type, formatter, false, next);
        
    }
    
    /**
     * コンスタによるインスタンスを生成する際の前提条件となる引数のチェックを行う。
     * @param type 変換後のクラスタイプ。
     * @param formatter 数値のフォーマッタ。
     * @throws NullPointerException type == null || formatter == null.
     * 
     */
    private static <N extends Number> void checkPreconditions(final Class<N> type, final NumberFormat formatter) {
        
        if(type == null) {
            throw new NullPointerException("type is null.");
        }
        
        if(formatter == null) {
            throw new NullPointerException("formatter is null.");
        }
        
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public Object execute(final Object value, final CsvContext context) {
        validateInputNotNull(value, context);
        
        if(!(value instanceof String)) {
            throw new SuperCsvCellProcessorException(String.class, value, context, this);
        }
        
        try {
            Object result = formatter.parse(type, (String) value);
            return next.execute(result, context);
            
        } catch(ParseException | ConversionException e) {
            throw new SuperCsvCellProcessorException(
                    String.format("'%s' could not be parsed as a BigDecimal", value),
                    context, this, e);
        }
    }
    
    @Override
    public Map<String, ?> getMessageVariable() {
        final Map<String, Object> vars = new HashMap<String, Object>();
        vars.put("type", getType().getCanonicalName());
        vars.put("pattern", getPattern());
        
        return vars;
    }
    
    @Override
    public String formatValue(final Object value) {
        if(value == null) {
            return "";
        }
        
        if(value instanceof Number) {
            final Number number = (Number) value;
            return formatter.format(number);
            
        }
        
        return value.toString();
    }
    
    /**
     * 変換後のクラスタイプを取得します。
     * @return コンストラクタで渡したクラスタイプを返します。
     */
    public Class<N> getType() {
        return type;
    }
    
    /**
     * 書式を取得します。
     * @return 書式がない場合、nullを返します。
     */
    public String getPattern() {
        return formatter.getPattern();
    }
    
}

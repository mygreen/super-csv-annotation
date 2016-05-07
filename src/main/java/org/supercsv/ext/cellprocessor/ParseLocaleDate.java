package org.supercsv.ext.cellprocessor;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.ift.DateCellProcessor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;
import org.supercsv.exception.SuperCsvCellProcessorException;
import org.supercsv.ext.cellprocessor.ift.ValidationCellProcessor;
import org.supercsv.ext.util.DateFormatWrapper;
import org.supercsv.util.CsvContext;


/**
 * 文字列を解析し、{@link Date}型に変換する{@link CellProcessor}。
 * <p>解析する処理は、スレッドセーフです。</p>
 * 
 * @version 1.2
 * @author T.TSUCHIE
 *
 */
public class ParseLocaleDate extends CellProcessorAdaptor
        implements StringCellProcessor, ValidationCellProcessor {
    
    protected final DateFormatWrapper formatter;
    
    /**
     * フォーマッタを指定してインスタンスを作成するコンストラクタ。
     * @param formatter 日時のフォーマッタ。
     * @throws NullPointerException if formatter is null.
     */
    public ParseLocaleDate(final DateFormat formatter) {
        super();
        checkPreconditions(formatter);
        this.formatter = new DateFormatWrapper(formatter);
    }
    
    /**
     * フォーマッタを指定してインスタンスを作成するコンストラクタ。
     * @param formatter 日時のフォーマッタ。
     * @param next チェインの中で呼ばれる次の{@link CellProcessor}.
     * @throws NullPointerException if formatter or next is null.
     */
    public ParseLocaleDate(final DateFormat formatter, final DateCellProcessor next) {
        super(next);
        checkPreconditions(formatter);
        this.formatter = new DateFormatWrapper(formatter);
    }
    
    /**
     * コンスタによるインスタンスを生成する際の前提条件となる引数のチェックを行う。
     * @throws NullPointerException formatter is null.
     * 
     */
    private static void checkPreconditions(final DateFormat formatter) {
        if(formatter == null) {
            throw new NullPointerException("formatter is null.");
        }
    }
    
    /**
     * {@inheritDoc}
     * 
     * @throws SuperCsvCellProcessorException
     *             if value is null, isn't a String, or can't be parsed to a Date.a
     */
    @SuppressWarnings("unchecked")
    @Override
    public Object execute(final Object value, final CsvContext context) {
        validateInputNotNull(value, context);
        
        if( !(value instanceof String) ) {
            throw new SuperCsvCellProcessorException(String.class, value, context, this);
        }
        
        try {
            final Date result = parse((String) value);
            return next.execute(result, context);
            
        } catch(ParseException e) {
            throw new SuperCsvCellProcessorException(
                    String.format("'%s' could not be parsed as a Date", value),
                    context, this, e);
        }
    }
    
    /**
     * 文字列を解析し{@link Date}に変換する。
     * <p>{@link Date}のサブクラスの場合、このメソッドをオーバライドして処理を変更する。</p>
     * 
     * @param value 解析対象の文字列
     * @return 変換した日時オブジェクト。
     * @throws ParseException 解析に失敗した場合にスローする。
     */
    protected Date parse(final String value) throws ParseException {
        return formatter.parse(value);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, ?> getMessageVariable() {
        Map<String, Object> vars = new HashMap<String, Object>();
        vars.put("pattern", formatter.getPattern());
        
        return vars;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String formatValue(final Object value) {
        if(value == null) {
            return "";
        }
        
        if(value instanceof Date) {
            final Date date = (Date) value;
            return formatter.format(date);
            
        }
        
        return value.toString();
    }
    
    /**
     * 書式を取得します。
     * @return 書式がない場合、nullを返します。
     */
    public String getPattern() {
        return formatter.getPattern();
    }

}

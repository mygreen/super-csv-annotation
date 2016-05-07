package org.supercsv.ext.cellprocessor;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.cellprocessor.FmtDate;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.ift.DateCellProcessor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;
import org.supercsv.exception.SuperCsvCellProcessorException;
import org.supercsv.ext.cellprocessor.ift.ValidationCellProcessor;
import org.supercsv.ext.util.DateFormatWrapper;
import org.supercsv.util.CsvContext;


/**
 * {@link Date}型をフォーマットする{@link CellProcessor}。
 * 
 * @version 1.2
 * @see {@link FmtDate}
 * @author T.TSUCHIE
 *
 */
public class FormatLocaleDate extends CellProcessorAdaptor
        implements DateCellProcessor, ValidationCellProcessor {
    
    protected final DateFormatWrapper formatter;
    
    /**
     * フォーマッタを指定してインスタンスを作成するコンストラクタ。
     * @param formatter 日時のフォーマッタ。
     * @throws NullPointerException if formatter is null.
     */
    public FormatLocaleDate(final DateFormat formatter) {
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
    public FormatLocaleDate(final DateFormat formatter, final StringCellProcessor next) {
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
    
    @SuppressWarnings("unchecked")
    @Override
    public Object execute(final Object value, final CsvContext context) {
        
        validateInputNotNull(value, context);
        
        if(!(value instanceof Date)) {
            throw new SuperCsvCellProcessorException(Date.class, value, context, this);
        }
        
        String result = formatter.format((Date) value);
        return next.execute(result, context);
    }
    
    @Override
    public Map<String, ?> getMessageVariable() {
        Map<String, Object> vars = new HashMap<String, Object>();
        
        return vars;
    }
    
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
    
}

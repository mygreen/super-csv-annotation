package org.supercsv.ext.cellprocessor;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.cellprocessor.FmtNumber;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.ift.DoubleCellProcessor;
import org.supercsv.cellprocessor.ift.LongCellProcessor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;
import org.supercsv.exception.SuperCsvCellProcessorException;
import org.supercsv.ext.cellprocessor.ift.ValidationCellProcessor;
import org.supercsv.ext.util.NumberFormatWrapper;
import org.supercsv.util.CsvContext;


/**
 * 数値型をフォーマットするCellProcessor.
 * 
 * @version 1.2
 * @see {@link FmtNumber}
 * @author T.TSUCHIE
 *
 */
public class FormatLocaleNumber extends CellProcessorAdaptor
        implements DoubleCellProcessor, LongCellProcessor, ValidationCellProcessor {
    
    protected final NumberFormatWrapper formatter;
    
    /**
     * フォーマッタを指定してインスタンスを作成するコンストラクタ。
     * @param formatter 数値のフォーマッタ。
     * @throws NullPointerException if formatter is null.
     */
    public FormatLocaleNumber(final NumberFormat formatter) {
        super();
        checkPreconditions(formatter);
        this.formatter = new NumberFormatWrapper(formatter);
    }
    
    /**
     * フォーマッタを指定してインスタンスを作成するコンストラクタ。
     * @param formatter 数値のフォーマッタ。
     * @param next チェインの中で呼ばれる次の{@link CellProcessor}.
     * @throws NullPointerException if formatter is null.
     */
    public FormatLocaleNumber(final NumberFormat formatter, final StringCellProcessor next) {
        super(next);
        checkPreconditions(formatter);
        this.formatter = new NumberFormatWrapper(formatter);
        
    }
    
    /**
     * コンスタによるインスタンスを生成する際の前提条件となる引数のチェックを行う。
     * @throws NullPointerException formatter is null.
     * 
     */
    private static void checkPreconditions(final NumberFormat formatter) {
        if(formatter == null) {
            throw new NullPointerException("formatter is null.");
        }
        
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public Object execute(final Object value, final CsvContext context) {
        
        validateInputNotNull(value, context);
        
        if(!(value instanceof Number)) {
            throw new SuperCsvCellProcessorException(Number.class, value, context, this);
        }
        
        String result = formatter.format((Number) value);
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
        
        if(value instanceof Number) {
            final Number number = (Number) value;
            return formatter.format(number);
            
        }
        
        return value.toString();
        
    }

}

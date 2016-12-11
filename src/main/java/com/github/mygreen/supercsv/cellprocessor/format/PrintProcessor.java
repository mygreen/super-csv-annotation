package com.github.mygreen.supercsv.cellprocessor.format;

import org.supercsv.cellprocessor.ift.BoolCellProcessor;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.ift.DateCellProcessor;
import org.supercsv.cellprocessor.ift.DoubleCellProcessor;
import org.supercsv.cellprocessor.ift.LongCellProcessor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;
import org.supercsv.util.CsvContext;

import com.github.mygreen.supercsv.cellprocessor.ValidationCellProcessor;

/**
 * オブジェクトを文字列に変換するプロセッサ。
 * 
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class PrintProcessor<T> extends ValidationCellProcessor implements BoolCellProcessor, DateCellProcessor, 
        DoubleCellProcessor, LongCellProcessor, StringCellProcessor {
    
    private final TextPrinter<T> printer;
    
    /**
     * プリンタを指定してインスタンスを作成するコンストラクタ。
     * @param printer オブジェクトを文字列に変換するプリンタ。
     * @throws NullPointerException if printer is null.
     */
    public PrintProcessor(final TextPrinter<T> printer) {
        super();
        checkPreconditions(printer);
        this.printer = printer;
    }
    
    /**
     * プリンタを指定してインスタンスを作成するコンストラクタ。
     * @param printer オブジェクトを文字列に変換するプリンタ。
     * @param next チェインの中で呼ばれる次の{@link CellProcessor}.
     * @throws NullPointerException if printer is null.
     */
    public PrintProcessor(final TextPrinter<T> printer, final StringCellProcessor next) {
        super(next);
        checkPreconditions(printer);
        this.printer = printer;
    }
    
    /**
     * コンスタによるインスタンスを生成する際の前提条件となる引数のチェックを行う。
     * @throws NullPointerException printer is null.
     * 
     */
    private static <T> void checkPreconditions(final TextPrinter<T> printer) {
        if(printer == null) {
            throw new NullPointerException("printer is null.");
        }
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public Object execute(final Object value, final CsvContext context) {
        
        if(value == null) {
            return next.execute(value, context);
        }
        
        try {
            final String result = printer.print((T)value);
            return next.execute(result, context);
            
        } catch(TextPrintException e) {
            throw createValidationException(context)
                .messageFormat("'%s' could not print.", value.toString())
                .exception(e)
                .rejectedValue(value)
                .build();
        }
        
    }
    
}

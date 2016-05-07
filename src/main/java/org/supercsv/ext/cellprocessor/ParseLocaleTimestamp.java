package org.supercsv.ext.cellprocessor;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.ift.DateCellProcessor;

/**
 * 文字列を解析し、{@link Timestamp}型に変換する{@link CellProcessor}.
 * 
 * @version 1.2
 * @author T.TSUCHIE
 *
 */
public class ParseLocaleTimestamp extends ParseLocaleDate {
    
    /**
     * フォーマッタを指定してインスタンスを作成するコンストラクタ。
     * @param formatter 日時のフォーマッタ。
     * @throws NullPointerException if formatter is null.
     */
    public ParseLocaleTimestamp(final DateFormat formatter) {
        super(formatter);
    }
    
    /**
     * フォーマッタを指定してインスタンスを作成するコンストラクタ。
     * @param formatter 日時のフォーマッタ。
     * @param next チェインの中で呼ばれる次の{@link CellProcessor}.
     * @throws NullPointerException if formatter or next is null.
     */
    public ParseLocaleTimestamp(final DateFormat formatter, final DateCellProcessor next) {
       super(formatter, next);
    }
    
    @Override
    protected Date parse(final String value) throws ParseException {
        
        final Date result = formatter.parse(value);
        return new Timestamp(result.getTime());
    }
}

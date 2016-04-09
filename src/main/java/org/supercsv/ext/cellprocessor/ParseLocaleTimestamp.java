package org.supercsv.ext.cellprocessor;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

import org.supercsv.cellprocessor.ift.DateCellProcessor;

/**
 * 
 * @version 1.2
 * @author T.TSUCHIE
 *
 */
public class ParseLocaleTimestamp extends ParseLocaleDate {
    
    public ParseLocaleTimestamp(final DateFormat formatter) {
        super(formatter);
    }
    
    public ParseLocaleTimestamp(final DateFormat formatter, final DateCellProcessor next) {
       super(formatter, next);
    }
    
    @Override
    protected Date parse(final String value) throws ParseException {
        
        final Date result = formatter.parse(value);
        return new Timestamp(result.getTime());
    }
}

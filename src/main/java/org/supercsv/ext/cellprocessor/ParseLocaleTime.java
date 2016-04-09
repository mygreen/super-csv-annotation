package org.supercsv.ext.cellprocessor;

import java.sql.Time;
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
public class ParseLocaleTime extends ParseLocaleDate {
    
    public ParseLocaleTime(final DateFormat formatter) {
        super(formatter);
    }
    
    public ParseLocaleTime(final DateFormat formatter, final DateCellProcessor next) {
       super(formatter, next);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected Date parse(final String value) throws ParseException {
        
        final Date result = formatter.parse(value);
        return new Time(result.getTime());
    }
}

package org.supercsv.ext.cellprocessor;

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
public class ParseLocaleSqlDate extends ParseLocaleDate {
    
    public ParseLocaleSqlDate(final DateFormat formatter) {
        super(formatter);
    }
    
    public ParseLocaleSqlDate(final DateFormat formatter, final DateCellProcessor next) {
       super(formatter, next);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected Date parse(final String value) throws ParseException {
        
        final Date result = formatter.parse(value);
        return new java.sql.Date(result.getTime());
    }
}

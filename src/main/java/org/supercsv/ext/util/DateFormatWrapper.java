package org.supercsv.ext.util;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * DateFormatter with thread-safe.
 *
 * @since 1.2
 * @author T.TSUCHIE
 *
 */
public class DateFormatWrapper {
    
    private final DateFormat formatter;
    
    public DateFormatWrapper(final DateFormat formatter) {
        if(formatter == null) {
            throw new NullPointerException("formatter should not be null.");
        }
        
        this.formatter = (DateFormat) formatter.clone();
        
    }
    
    public DateFormatWrapper(final Class<? extends Date> dateClass) {
        
        if(dateClass == null) {
            throw new NullPointerException("dateClass should not be null.");
        }
        
        final String pattern;
        if(Timestamp.class.isAssignableFrom(dateClass)) {
            pattern = "yyyy-MM-dd HH:mm:ss.SSS";
            
        } else if(Time.class.isAssignableFrom(dateClass)) {
            pattern = "HH:mm:ss";
            
        } else if(java.sql.Date.class.isAssignableFrom(dateClass)) {
            pattern = "yyyy-MM-dd";
            
        } else {
            pattern = "yyyy-MM-dd HH:mm:ss";
        }
        
        this.formatter = new SimpleDateFormat(pattern);
    }
    
    /**
     * Format date to string with synchorinzed.
     * @param date date value.
     * @return formatted string.
     */
    public synchronized <T extends Date> String format(final T date) {
        return formatter.format(date);
        
    }
    
    /**
     * Parse string to date with synchronized.
     * @param str string value.
     * @return parsed date.
     * @throws ParseException
     */
    public synchronized Date parse(final String str) throws ParseException {
        return formatter.parse(str);
    }
    
    public String getPattern() {
        
        if(formatter instanceof SimpleDateFormat) {
            SimpleDateFormat sdf = (SimpleDateFormat) formatter;
            return sdf.toPattern();
        }
        
        return null;
        
    }
    
}

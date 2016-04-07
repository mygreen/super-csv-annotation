package org.supercsv.ext.cellprocessor.constraint;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 
 *
 * @since 1.2
 * @author T.TSUCHIE
 *
 */
public class DateFormatterWrapper {
    
    private final ThreadLocal<DateFormat> formatter;
    
    public DateFormatterWrapper(final DateFormat formatter) {
        if(formatter == null) {
            throw new NullPointerException("formatter should not be null.");
        }
        
        this.formatter = new ThreadLocal<DateFormat>() {
            
            @Override
            protected DateFormat initialValue() {
                return formatter;
            }
            
        };
        
    }
    
    public DateFormatterWrapper(final Class<?> dateClass) {
        
        if(dateClass == null) {
            throw new NullPointerException("dateClass should not be null.");
        }
        
        final String pattern;
        if(Timestamp.class.isAssignableFrom(dateClass)) {
            pattern = "yyyy-MM-dd HH:mm:ss.SSS";
            
        } else if(Time.class.isAssignableFrom(dateClass)) {
            pattern = "HH:mm:ss";
            
        } else if(java.sql.Date.class.isAssignableFrom(dateClass)) {
            pattern = "HH:mm:ss";
            
        } else {
            pattern = "yyyy-MM-dd HH:mm:ss";
        }
        
        this.formatter = new ThreadLocal<DateFormat>() {
            
            @Override
            protected DateFormat initialValue() {
                SimpleDateFormat sdf = new SimpleDateFormat(pattern);
                return sdf;
            }
            
        };
    }
    
    public String format(final Date date) {
        return formatter.get().format(date);
        
    }
    
}

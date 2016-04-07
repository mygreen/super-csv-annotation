package org.supercsv.ext;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.supercsv.util.CsvContext;

/**
 * Utility methods and constants for tests.
 * 
 * @since 1.2
 * @author T.TSUCHIE
 *
 */
public class TestUtils {
    
    public static final CsvContext  ANONYMOUS_CSVCONTEXT = new CsvContext(1, 2, 3);
    
    /**
     * create Date instance.
     * @param year
     * @param month Month(start with 1)
     * @param dayOfMonth
     * @return
     */
    public static Date toDate(final int year, final int month, final int dayOfMonth) {
        
        return toDate(year, month, dayOfMonth, 0, 0, 0);
    }
    
    /**
     * create Date instance.
     * @param year
     * @param month Month(start with 1)
     * @param dayOfMonth
     * @param hour
     * @param minute
     * @param second
     * @return
     */
    public static Date toDate(final int year, final int month, final int dayOfMonth,
            final int hour, final int minute, final int second) {
        
        Calendar cal = Calendar.getInstance();
        cal.set(year, month-1, dayOfMonth, hour, minute, second);
        return cal.getTime();
    }
    
    public static Date plusDays(final Date date, final int daysToAdd) {
        
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        
        cal.add(Calendar.DAY_OF_MONTH, daysToAdd);
        
        return cal.getTime();
        
    }
    
    public static Date minusDays(final Date date, final int daysToSubstract) {
        
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        
        cal.add(Calendar.DAY_OF_MONTH, -daysToSubstract);
        
        return cal.getTime();
        
    }
}

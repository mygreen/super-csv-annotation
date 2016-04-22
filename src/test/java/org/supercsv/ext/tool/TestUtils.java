package org.supercsv.ext.tool;

import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.cellprocessor.ift.CellProcessor;
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
     * create Intger from str.
     * @param value
     * @return
     */
    public static Integer toInteger(final String value) {
        return Integer.parseInt(value);
    }
    
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
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }
    
    public static java.sql.Date toSqlDate(final int year, final int month, final int dayOfMonth) {
        return new java.sql.Date(toDate(year, month, dayOfMonth).getTime());
    }
    
    public static Timestamp toTimestamp(final int year, final int month, final int dayOfMonth,
            final int hour, final int minute, final int second, final int millsecond) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month-1, dayOfMonth, hour, minute, second);
        cal.set(Calendar.MILLISECOND, millsecond);
        
        return new Timestamp(cal.getTimeInMillis());
    }
    
    public static Time toTime(final int hour, final int minute, final int second) {
        
        Calendar cal = Calendar.getInstance();
        cal.set(1970, 0, 1, hour, minute, second);
        cal.set(Calendar.MILLISECOND, 0);
        return new Time(cal.getTime().getTime());
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
    
    public static Date plusHours(final Date date, final int hoursToAdd) {
        
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        
        cal.add(Calendar.HOUR_OF_DAY, hoursToAdd);
        
        return cal.getTime();
        
    }
    
    public static Date minusHours(final Date date, final int hoursToSubstract) {
        
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        
        cal.add(Calendar.HOUR_OF_DAY, -hoursToSubstract);
        
        return cal.getTime();
        
    }
    
    public static Date plusSeconds(final Date date, final int secondsToAdd) {
        
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        
        cal.add(Calendar.SECOND, secondsToAdd);
        
        return cal.getTime();
        
    }
    
    public static Date minusSeconds(final Date date, final int secondsToSubstract) {
        
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        
        cal.add(Calendar.SECOND, -secondsToSubstract);
        
        return cal.getTime();
        
    }
    
    public static Date plusMillseconds(final Date date, final int millsecondsToAdd) {
        
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        
        cal.add(Calendar.MILLISECOND, millsecondsToAdd);
        
        return cal.getTime();
        
    }
    
    public static Date minusMillseconds(final Date date, final int millsecondsToSubstract) {
        
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        
        cal.add(Calendar.MILLISECOND, -millsecondsToSubstract);
        
        return cal.getTime();
        
    }
    
    public static Annotation[] getAnnotations(final Class<?> clazz, final String fieldName) {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.getAnnotations();
        } catch(ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static void printCellProcessorChain(final CellProcessor cellProcessor, final String message) {
        
        if(cellProcessor == null) {
            return;
        }
        
        System.out.printf("======= print CellProcessor chain structures. :: %s ========\n", message);
        printCellProcessorChain(cellProcessor, new PrintWriter(System.out));
        System.out.println();
    }
    
    public static void printCellProcessorChain(final CellProcessor cellProcessor, final PrintWriter writer) {
        
        String index = "";
        CellProcessor cp = cellProcessor;
        do {
            if(index.length() == 0) {
                writer.printf("%s%s\n", index, cp.getClass().getName());
                writer.flush();
            } else {
                writer.printf("%s└%s\n", index, cp.getClass().getName());
                writer.flush();
            }
            
            // next processor
            try {
                if(cp instanceof CellProcessorAdaptor) {
                    Field field = CellProcessorAdaptor.class.getDeclaredField("next");
                    field.setAccessible(true);
                    cp = (CellProcessor) field.get(cp);
                } else {
                    break;
                }
                
            } catch(ReflectiveOperationException e) {
                return;
            }
            
            index += "    ";
            
        } while(cp != null);
        
    }
    
    public static final String format(final Date value, final String pattern) {
        
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        return formatter.format(value);
        
    }
}

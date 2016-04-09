package org.supercsv.ext.util;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

/**
 * 
 * @since 1.2
 * @author T.TSUCHIE
 *
 */
public class SimpleDateFormatBuilder {
    
    private String pattern;
    
    private Locale locale;
    
    private TimeZone timeZone;
    
    private boolean lenient;
    
    /**
     * create instance {@link SimpleDateFormatBuilder}.
     * @param pattern format pattern.
     * @return
     */
    public static SimpleDateFormatBuilder create(final String pattern) {
        return new SimpleDateFormatBuilder(pattern);
    }
    
    private SimpleDateFormatBuilder(final String pattern) {
        this.pattern = pattern;
        this.locale = Locale.getDefault();
        this.timeZone = TimeZone.getDefault();
        this.lenient = false;
    }
    
    /**
     * create instance {@link SimpleDateFormat}
     * @return
     */
    public SimpleDateFormat build() {
        
        final SimpleDateFormat formatter;
        if(locale != null) {
            formatter = new SimpleDateFormat(pattern);
        } else {
            formatter = new SimpleDateFormat(pattern, locale);
        }
        
        if(timeZone != null) {
            formatter.setTimeZone(timeZone);
        }
        formatter.setLenient(lenient);
        
        return formatter;
        
    }
    
    public SimpleDateFormatBuilder locale(final Locale locale) {
        this.locale = locale;
        return this;
    }
    
    public SimpleDateFormatBuilder timeZone(final TimeZone timeZone) {
        this.timeZone = timeZone;
        return this;
    }
    
    public SimpleDateFormatBuilder lenient(final boolean lenient) {
        this.lenient = lenient;
        return this;
    }
    
}

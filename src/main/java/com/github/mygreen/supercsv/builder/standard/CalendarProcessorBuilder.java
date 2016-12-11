package com.github.mygreen.supercsv.builder.standard;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.TimeZone;

import com.github.mygreen.supercsv.annotation.constraint.CsvDateTimeMax;
import com.github.mygreen.supercsv.annotation.constraint.CsvDateTimeMin;
import com.github.mygreen.supercsv.annotation.constraint.CsvDateTimeRange;
import com.github.mygreen.supercsv.annotation.format.CsvDateTimeFormat;
import com.github.mygreen.supercsv.builder.AbstractProcessorBuilder;
import com.github.mygreen.supercsv.builder.Configuration;
import com.github.mygreen.supercsv.builder.FieldAccessor;
import com.github.mygreen.supercsv.cellprocessor.constraint.DateTimeMaxFactory;
import com.github.mygreen.supercsv.cellprocessor.constraint.DateTimeMinFactory;
import com.github.mygreen.supercsv.cellprocessor.constraint.DateTimeRangeFactory;
import com.github.mygreen.supercsv.cellprocessor.format.AbstractTextFormatter;
import com.github.mygreen.supercsv.cellprocessor.format.DateFormatWrapper;
import com.github.mygreen.supercsv.cellprocessor.format.SimpleDateFormatBuilder;
import com.github.mygreen.supercsv.cellprocessor.format.TextFormatter;
import com.github.mygreen.supercsv.util.Utils;

/**
 * {@link Calendar}クラスに対するビルダ。
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class CalendarProcessorBuilder extends AbstractProcessorBuilder<Calendar> {
    
    @Override
    protected void init() {
        super.init();
        
        // 制約のアノテーションの追加
        registerForConstraint(CsvDateTimeRange.class, new DateTimeRangeFactory<>());
        registerForConstraint(CsvDateTimeMin.class, new DateTimeMinFactory<>());
        registerForConstraint(CsvDateTimeMax.class, new DateTimeMaxFactory<>());
        
    }
    
    @Override
    protected TextFormatter<Calendar> getDefaultFormatter(final FieldAccessor field, final Configuration config) {
        
        final TextFormatter<Date> dateFormatter = getDateFormatter(field, config);
        
        return new AbstractTextFormatter<Calendar>() {
            
            @Override
            public String print(final Calendar object) {
                
                return dateFormatter.print(object.getTime());
            }
            
            @Override
            public Calendar parse(final String text) {
                
                final Date date = dateFormatter.parse(text);
                final Calendar cal = createDefaultCalendar(field);
                cal.setTime(date);
                
                return cal;
            }
            
            @Override
            public Optional<String> getPattern() {
                return dateFormatter.getPattern();
            }
            
            @Override
            public void setValidationMessage(String validationMessage) {
                dateFormatter.setValidationMessage(validationMessage);
            }
            
            @Override
            public Map<String, Object> getMessageVariables() {
                return dateFormatter.getMessageVariables();
            }
            
            @Override
            public Optional<String> getValidationMessage() {
                return dateFormatter.getValidationMessage();
            }
        };
        
    }
    
    private DateFormatWrapper<Date> getDateFormatter(final FieldAccessor field, final Configuration config) {
        
        final String defaultPattern = "yyyy-MM-dd HH:mm:ss";
        
        final Optional<CsvDateTimeFormat> formatAnno = field.getAnnotation(CsvDateTimeFormat.class);
        if(!formatAnno.isPresent()) {
            return new DateFormatWrapper<>(new SimpleDateFormat(defaultPattern), Date.class);
        }
        
        String pattern = formatAnno.get().pattern();
        if(pattern.isEmpty()) {
            pattern = defaultPattern;
        }
        
        final boolean lenient = formatAnno.get().lenient();
        final Locale locale = Utils.getLocale(formatAnno.get().locale());
        final TimeZone timeZone = formatAnno.get().timezone().isEmpty() ? TimeZone.getDefault()
                : TimeZone.getTimeZone(formatAnno.get().timezone());
        
        final DateFormat formatter = SimpleDateFormatBuilder.create(pattern)
                .lenient(lenient)
                .locale(locale)
                .timeZone(timeZone)
                .build();
        
        final DateFormatWrapper<Date> wrapper = new DateFormatWrapper<>(formatter, Date.class); 
        wrapper.setValidationMessage(formatAnno.get().message());
        
        return wrapper;
        
    }
    
    private Calendar createDefaultCalendar(final FieldAccessor field) {
        
        final Optional<CsvDateTimeFormat> formatAnno = field.getAnnotation(CsvDateTimeFormat.class);
        if(!formatAnno.isPresent()) {
            return Calendar.getInstance();
        }
        
        final Locale locale = Utils.getLocale(formatAnno.get().locale());
        final TimeZone timeZone = formatAnno.get().timezone().isEmpty() ? TimeZone.getDefault()
                : TimeZone.getTimeZone(formatAnno.get().timezone());
        
        return Calendar.getInstance(timeZone, locale);
    }
    
}

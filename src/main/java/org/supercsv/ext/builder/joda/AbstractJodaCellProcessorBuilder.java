package org.supercsv.ext.builder.joda;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;
import java.util.TimeZone;

import org.joda.time.DateTimeZone;
import org.joda.time.ReadablePartial;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.ext.annotation.CsvDateConverter;
import org.supercsv.ext.builder.AbstractCellProcessorBuilder;
import org.supercsv.ext.cellprocessor.joda.FutureJoda;
import org.supercsv.ext.cellprocessor.joda.JodaRange;
import org.supercsv.ext.cellprocessor.joda.PastJoda;
import org.supercsv.ext.util.Utils;

/**
 * Cell processor builder for Joda-Time class (ex. LocalTime, LocalDateTime)
 *
 * @since 1.2
 * @author T.TSUCHIE
 *
 */
public abstract class AbstractJodaCellProcessorBuilder<T extends ReadablePartial> extends AbstractCellProcessorBuilder<T> {
    
    protected Optional<CsvDateConverter> getAnnotation(final Annotation[] annos) {
        
        if(Utils.isEmpty(annos)) {
            return Optional.empty();
        }
        
        return Arrays.stream(annos)
            .filter(a -> a instanceof CsvDateConverter)
            .map(a -> (CsvDateConverter) a)
            .findFirst();
        
    }
    
    protected DateTimeFormatter createDateTimeFormatter(final Optional<CsvDateConverter> converterAnno) {
        
        final String pattern = getPattern(converterAnno);
        final Locale locale = getLocale(converterAnno);
        final DateTimeZone zone = getDateTimeZone(converterAnno);
        
        return createDateTimeFormatter(pattern, locale, zone);
    }
    
    protected DateTimeFormatter createDateTimeFormatter(final String pattern, /*final ResolverStyle style,*/
            final Locale locale, final DateTimeZone zone) {
        //TODO: lenient
        return DateTimeFormat.forPattern(pattern)
                .withLocale(locale)
                .withZone(zone);
    }
    
    protected String getPattern(final Optional<CsvDateConverter> converterAnno) {
        
        return converterAnno.map(a -> a.pattern())
                .map(s -> s.isEmpty() ? getDefaultPattern() : s)
                .orElse(getDefaultPattern());
    }
    

    /**
     * get default pattern.
     * @return
     */
    protected abstract String getDefaultPattern();
    
    protected Locale getLocale(final Optional<CsvDateConverter> converterAnno) {
        
        return converterAnno.map(a -> Utils.getLocale(a.locale()))
                .orElse(Locale.getDefault());
    }
    
    protected DateTimeZone getDateTimeZone(final Optional<CsvDateConverter> converterAnno) {
        
        return converterAnno.map(a -> DateTimeZone.forTimeZone(TimeZone.getTimeZone(a.timezone())))
                .orElse(DateTimeZone.getDefault());
    }
    
    protected Optional<String> getMin(final Optional<CsvDateConverter> converterAnno) {
        return converterAnno.map(a -> a.min())
                .filter(s -> s.length() > 0);
    }
    
    protected Optional<String> getMax(final Optional<CsvDateConverter> converterAnno) {
        return converterAnno.map(a -> a.max())
                .filter(s -> s.length() > 0);
    }
    
    protected CellProcessor prependRangeProcessor(final Optional<T> min, final Optional<T> max,
            final DateTimeFormatter formatter, final CellProcessor processor) {
        
        CellProcessor cp = processor;
        if(min.isPresent() && max.isPresent()) {
            if(cp == null) {
                cp = new JodaRange<T>(min.get(), max.get()).setFormatter(formatter);
            } else {
                cp = new JodaRange<T>(min.get(), max.get(), cp).setFormatter(formatter);
            }
        } else if(min.isPresent()) {
            if(cp == null) {
                cp = new FutureJoda<T>(min.get()).setFormatter(formatter);
            } else {
                cp = new FutureJoda<T>(min.get(), cp).setFormatter(formatter);
            }
        } else if(max.isPresent()) {
            if(cp == null) {
                cp = new PastJoda<T>(max.get()).setFormatter(formatter);
            } else {
                cp = new PastJoda<T>(max.get(), cp).setFormatter(formatter);
            }
        }
        
        return cp;
        
    }
    
}

package org.supercsv.ext.builder.time;

import java.lang.annotation.Annotation;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.time.temporal.TemporalAccessor;
import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;
import java.util.TimeZone;

import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.ext.Utils;
import org.supercsv.ext.annotation.CsvDateConverter;
import org.supercsv.ext.builder.AbstractCellProcessorBuilder;
import org.supercsv.ext.cellprocessor.time.FutureTemporal;
import org.supercsv.ext.cellprocessor.time.PastTemporal;
import org.supercsv.ext.cellprocessor.time.TemporalRange;

/**
 * Cell processor builder for JSR-310 class (ex. LocalTime, LocalDateTime)
 *
 * @since 1.2
 * @author T.TSUCHIE
 *
 */
public abstract class AbstractTemporalAccessorCellProcessorBuilder<T extends TemporalAccessor & Comparable<? super T>>
        extends AbstractCellProcessorBuilder<T> {
    
    protected Optional<CsvDateConverter> getAnnotation(final Annotation[] annos) {
        
        if(Utils.isEmpty(annos)) {
            return Optional.empty();
        }
        
        return Arrays.stream(annos)
            .filter(a -> a instanceof CsvDateConverter)
            .map(a -> (CsvDateConverter) a)
            .findFirst();
        
    }
    
    protected DateTimeFormatter createDateTimeFormatter(final String pattern, final ResolverStyle style,
            final Locale locale, final ZoneId zone) {
        
        return DateTimeFormatter.ofPattern(pattern, locale)
                .withResolverStyle(style)
                .withZone(zone);
        
    }
    
    /**
     * get default pattern.
     * @return
     */
    protected abstract String getDefaultPattern();
    
    protected String getPattern(final Optional<CsvDateConverter> converterAnno) {
        
        return converterAnno.map(a -> a.pattern())
                .orElse(getDefaultPattern());
    }
    
    protected ResolverStyle getResolverStyle(final Optional<CsvDateConverter> converterAnno) {
        
        return converterAnno.map(a -> a.lenient() ? ResolverStyle.LENIENT : ResolverStyle.STRICT)
                .orElse(ResolverStyle.LENIENT);
        
    }
    
    protected Locale getLocale(final Optional<CsvDateConverter> converterAnno) {
        
        return converterAnno.map(a -> Utils.getLocale(a.locale()))
                .orElse(Locale.getDefault());
    }
    
    protected ZoneId getZoneId(final Optional<CsvDateConverter> converterAnno) {
        
        return converterAnno.map(a -> TimeZone.getTimeZone(a.timezone()).toZoneId())
                .orElse(ZoneId.systemDefault());
    }
    
    protected Optional<String> getMin(final Optional<CsvDateConverter> converterAnno) {
        return converterAnno.map(a -> a.min())
                .filter(s -> s.length() > 0);
    }
    
    protected Optional<String> getMax(final Optional<CsvDateConverter> converterAnno) {
        return converterAnno.map(a -> a.max())
                .filter(s -> s.length() > 0);
    }
    
    protected abstract T parseTemporal(final String value, final DateTimeFormatter formatter);
    
    protected CellProcessor prependRangeProcessor(final Optional<T> min, final Optional<T> max, final CellProcessor processor) {
        
        CellProcessor cp = processor;
        if(min.isPresent() && max.isPresent()) {
            if(cp == null) {
                cp = new TemporalRange<T>(min.get(), max.get());
            } else {
                cp = new TemporalRange<T>(min.get(), max.get(), cp);
            }
        } else if(min.isPresent()) {
            if(cp == null) {
                cp = new FutureTemporal<T>(min.get());
            } else {
                cp = new FutureTemporal<T>(min.get(), cp);
            }
        } else if(max.isPresent()) {
            if(cp == null) {
                cp = new PastTemporal<T>(max.get());
            } else {
                cp = new PastTemporal<T>(max.get(), cp);
            }
        }
        
        return cp;
        
    }
    
}

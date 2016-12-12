package com.github.mygreen.supercsv.builder.time;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;
import java.util.Optional;
import java.util.TimeZone;

import com.github.mygreen.supercsv.annotation.constraint.CsvDateTimeRange;
import com.github.mygreen.supercsv.annotation.constraint.CsvDateTimeMin;
import com.github.mygreen.supercsv.annotation.constraint.CsvDateTimeMax;
import com.github.mygreen.supercsv.annotation.format.CsvDateTimeFormat;
import com.github.mygreen.supercsv.builder.AbstractProcessorBuilder;
import com.github.mygreen.supercsv.builder.Configuration;
import com.github.mygreen.supercsv.builder.FieldAccessor;
import com.github.mygreen.supercsv.cellprocessor.constraint.DateTimeRangeFactory;
import com.github.mygreen.supercsv.cellprocessor.constraint.DateTimeMinFactory;
import com.github.mygreen.supercsv.cellprocessor.constraint.DateTimeMaxFactory;
import com.github.mygreen.supercsv.cellprocessor.format.TemporalFormatWrapper;
import com.github.mygreen.supercsv.cellprocessor.format.TextFormatter;
import com.github.mygreen.supercsv.util.Utils;


/**
 * JSR-310 'Date and Time API' の{@link TemporalAccessor}のテンプレートクラス。
 * <p>基本的に、{@link TemporalAccessor}のサブクラスのビルダは、このクラスを継承して作成する。</p>
 * 
 * @version 2.0
 * @since 1.2
 * @author T.TSUCHIE
 *
 */
public abstract class AbstractTemporalProcessorBuilder<T extends TemporalAccessor & Comparable<? super T>>
        extends AbstractProcessorBuilder<T> {
    
    public AbstractTemporalProcessorBuilder() {
        super();
    }
    
    @Override
    protected void init() {
        super.init();
        
        // 制約のアノテーションの追加
        registerForConstraint(CsvDateTimeRange.class, new DateTimeRangeFactory<>());
        registerForConstraint(CsvDateTimeMin.class, new DateTimeMinFactory<>());
        registerForConstraint(CsvDateTimeMax.class, new DateTimeMaxFactory<>());
        
    }
    
    /**
     * 変換規則から、{@link DateTimeFormatter}のインスタンスを作成する。
     * <p>アノテーション{@link CsvDateTimeFormat}が付与されていない場合は、各種タイプごとの標準の書式で作成する。</p>
     * @param field フィールド情報
     * @param config システム設定
     * @return {@link DateTimeFormatter}のインスタンス。
     */
    protected DateTimeFormatter createFormatter(final FieldAccessor field, final Configuration config) {
        
        final Optional<CsvDateTimeFormat> formatAnno = field.getAnnotation(CsvDateTimeFormat.class);
        if(!formatAnno.isPresent()) {
            return DateTimeFormatter.ofPattern(getDefaultPattern());
        }
        
        String pattern = formatAnno.get().pattern();
        if(pattern.isEmpty()) {
            pattern = getDefaultPattern();
        }
        
        final ResolverStyle style = formatAnno.get().lenient() ? ResolverStyle.LENIENT : ResolverStyle.STRICT;
        final Locale locale = Utils.getLocale(formatAnno.get().locale());
        final ZoneId zone = formatAnno.get().timezone().isEmpty() ? ZoneId.systemDefault()
                : TimeZone.getTimeZone(formatAnno.get().timezone()).toZoneId();
        
        return DateTimeFormatter.ofPattern(pattern, locale)
                .withResolverStyle(style)
                .withZone(zone);
        
    }
    
    @SuppressWarnings("unchecked")
    @Override
    protected TextFormatter<T> getDefaultFormatter(final FieldAccessor field, final Configuration config) {
        
        final Optional<CsvDateTimeFormat> formatAnno = field.getAnnotation(CsvDateTimeFormat.class);
        final String pattern = getPattern(field);
        final DateTimeFormatter formatter = createFormatter(field, config);
        
        final TemporalFormatWrapper<T> wrapper = new TemporalFormatWrapper<>(formatter, (Class<T>)field.getType());
        wrapper.setPattern(pattern);
        formatAnno.ifPresent(a -> wrapper.setValidationMessage(a.message()));
        return wrapper;
        
    }
    
    /**
     * アノテーション{@link CsvDateTimeFormat}が付与されている場合は、パターンを取得する。
     * <p>ただし、アノテーションが付与されていない場合は、{@link #getDefaultPattern()}の値を返す。</p>
     * <p>{@link DateTimeFormatter}のインスタンスからは、元となったパターンは直接取得できないため、
     *    別途取得して利用するために使用する。</p>
     * @param field フィールド情報。
     * @return 日時の書式パターン。
     */
    protected String getPattern(final FieldAccessor field) {
        return field.getAnnotation(CsvDateTimeFormat.class)
                .map(a -> a.pattern())
                .filter(p -> !p.isEmpty())
                .orElse(getDefaultPattern());
    }
    
    /**
     * 変換規則用のアノテーションが定義されていないときの標準の書式を取得する。
     * 
     * @return {@link DateTimeFormatter}で解析可能な日時の書式。
     */
    protected abstract String getDefaultPattern();
    
}

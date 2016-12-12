package com.github.mygreen.supercsv.builder.joda;

import java.util.Locale;
import java.util.Optional;
import java.util.TimeZone;

import org.joda.time.Chronology;
import org.joda.time.DateTimeZone;
import org.joda.time.ReadablePartial;
import org.joda.time.chrono.ISOChronology;
import org.joda.time.chrono.LenientChronology;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

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
import com.github.mygreen.supercsv.cellprocessor.format.JodaFormatWrapper;
import com.github.mygreen.supercsv.cellprocessor.format.TextFormatter;
import com.github.mygreen.supercsv.util.Utils;


/**
 * Joda-Time の{@link ReadablePartial}のテンプレートクラス。
 * <p>基本的に、{@link ReadablePartial}のサブクラスのビルダは、このクラスを継承して作成する。</p>
 *
 * @version 2.0
 * @since 1.2
 * @author T.TSUCHIE
 *
 */
public abstract class AbstractJodaProcessorBuilder<T> extends AbstractProcessorBuilder<T> {
    
    public AbstractJodaProcessorBuilder() {
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
            return DateTimeFormat.forPattern(getDefaultPattern());
        }
        
        String pattern = formatAnno.get().pattern();
        if(pattern.isEmpty()) {
            pattern = getDefaultPattern();
        }
        
        final Locale locale = Utils.getLocale(formatAnno.get().locale());
        final DateTimeZone zone = formatAnno.get().timezone().isEmpty() ? DateTimeZone.getDefault()
                : DateTimeZone.forTimeZone(TimeZone.getTimeZone(formatAnno.get().timezone()));
        
        final DateTimeFormatter formatter = DateTimeFormat.forPattern(pattern)
                .withLocale(locale)
                .withZone(zone);
        
        final boolean lenient = formatAnno.get().lenient();
        if(lenient) {
            Chronology chronology = LenientChronology.getInstance(ISOChronology.getInstance());
            return formatter.withChronology(chronology);
            
        } else {
            return formatter;
        }
        
    }
    
    @SuppressWarnings("unchecked")
    @Override
    protected TextFormatter<T> getDefaultFormatter(final FieldAccessor field, final Configuration config) {
        
        final Optional<CsvDateTimeFormat> formatAnno = field.getAnnotation(CsvDateTimeFormat.class);
        final String pattern = getPattern(field);
        final DateTimeFormatter formatter = createFormatter(field, config);
        
        final JodaFormatWrapper<T> wrapper = new JodaFormatWrapper<>(formatter, (Class<T>)field.getType());
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

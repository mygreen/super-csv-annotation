package org.supercsv.ext.builder.joda;

import java.lang.annotation.Annotation;
import java.time.temporal.TemporalAccessor;
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
 * Joda-Time の{@link TemporalAccessor}のテンプレートクラス。
 * <p>基本的に、{@link ReadablePartial}のサブクラスのビルダは、このクラスを継承して作成する。
 *
 * @since 1.2
 * @author T.TSUCHIE
 *
 */
public abstract class AbstractJodaCellProcessorBuilder<T extends ReadablePartial> extends AbstractCellProcessorBuilder<T> {
    
    /**
     * アノテーション{@link CsvDateConverter} を取得する。
     * @param annos アノテーションの一覧
     * @return アノテーションの定義がない場合は空を返す。
     */
    protected Optional<CsvDateConverter> getDateConverterAnnotation(final Annotation[] annos) {
        return getAnnotation(annos, CsvDateConverter.class);
    }
    
    /**
     * 日時の書式を取得する。
     * @param converterAnno 変換規則を定義したアノテーション。
     * @return アノテーションがない場合は、デフォルト値を返す。
     */
    protected String getPattern(final Optional<CsvDateConverter> converterAnno) {
        
        return converterAnno.map(a -> a.pattern())
                .map(s -> s.isEmpty() ? getDefaultPattern() : s)
                .orElse(getDefaultPattern());
    }
    
    /**
     * ロケールを取得する。
     * @param converterAnno 変換規則を定義したアノテーション。
     * @return アノテーションがない場合は、システムのデフォルト値を返す。
     */
    protected Locale getLocale(final Optional<CsvDateConverter> converterAnno) {
        
        return converterAnno.map(a -> Utils.getLocale(a.locale()))
                .orElse(Locale.getDefault());
    }
    
    /**
     * タイムゾーンを取得する。
     * @param converterAnno 変換規則を定義したアノテーション。
     * @return アノテーションがない場合は、システムのデフォルト値を返す。
     */
    protected DateTimeZone getDateTimeZone(final Optional<CsvDateConverter> converterAnno) {
        
        return converterAnno.map(a -> DateTimeZone.forTimeZone(TimeZone.getTimeZone(a.timezone())))
                .orElse(DateTimeZone.getDefault());
    }
    
    /**
     * 指定された値より未来日かどうかをチェックするための最小値を取得する。
     * @param converterAnno 変換規則を定義したアノテーション。
     * @return アノテーションがない場合や、値がない場合は、空を返す。
     */
    protected Optional<String> getMin(final Optional<CsvDateConverter> converterAnno) {
        return converterAnno.map(a -> a.min())
                .filter(s -> s.length() > 0);
    }
    
    /**
     * 指定された値より過去日かどうかをチェックするための最大値を取得する。
     * @param converterAnno 変換規則を定義したアノテーション。
     * @return アノテーションがない場合や、値がない場合は、空を返す。
     */
    protected Optional<String> getMax(final Optional<CsvDateConverter> converterAnno) {
        return converterAnno.map(a -> a.max())
                .filter(s -> s.length() > 0);
    }
    
    /**
     * 組み立て途中の{@link CellProcessor}に最小値/最大値/範囲をチェックするための{@link CellProcessor}を、Chainの前に追加する。
     * 
     * @param type フィールドのクラスタイプ。
     * @param annos フィールドに付与された全てのアノテーション。
     * @param cellProcessor 組み立て途中の{@link CellProcessor}
     * @param min 最小値
     * @param max 最大値
     * @return 最小値、最大値の指定の仕方により、最小値、最大値、範囲のチェックを追加するかどうか変わる。
     */
    protected CellProcessor prependRangeProcessor(final Class<T> type, final Annotation[] annos, final CellProcessor cellProcessor,
            final Optional<T> min, final Optional<T> max) {
        
        final DateTimeFormatter formatter = createDateTimeFormatter(getDateConverterAnnotation(annos));
        
        if(min.isPresent() && max.isPresent()) {
            
            final JodaRange<T> cp;
            if(cellProcessor == null) {
                cp = new JodaRange<T>(min.get(), max.get());
            } else {
                cp = new JodaRange<T>(min.get(), max.get(), cellProcessor);
            }
            
            cp.setFormatter(formatter);
            return cp;
            
        } else if(min.isPresent()) {
            
            final FutureJoda<T> cp;
            if(cellProcessor == null) {
                cp = new FutureJoda<T>(min.get());
            } else {
                cp = new FutureJoda<T>(min.get(), cellProcessor);
            }
            
            cp.setFormatter(formatter);
            return cp;
            
        } else if(max.isPresent()) {
            
            final PastJoda<T> cp;
            if(cellProcessor == null) {
                cp = new PastJoda<T>(max.get());
            } else {
                cp = new PastJoda<T>(max.get(), cellProcessor);
            }
            
            cp.setFormatter(formatter);
            return cp;
        }
        
        return cellProcessor;
        
    }
    /**
     * 変換規則用のアノテーションが定義されていないときの標準の書式を取得する。
     * 
     * @return {@link DateTimeFormatter}で解析可能な日時の書式。
     */
    protected abstract String getDefaultPattern();
    
    /**
     * 変換規則から、{@link DateTimeFormatter}のインスタンスを作成する。
     * @param converterAnno 変換規則を定義したアノテーション。
     * @return 日時のフォーマッタ。
     */
    protected DateTimeFormatter createDateTimeFormatter(final Optional<CsvDateConverter> converterAnno) {
        
        final String pattern = getPattern(converterAnno);
        final Locale locale = getLocale(converterAnno);
        final DateTimeZone zone = getDateTimeZone(converterAnno);
        
        return DateTimeFormat.forPattern(pattern)
                .withLocale(locale)
                .withZone(zone);
    }
    
   
    
}

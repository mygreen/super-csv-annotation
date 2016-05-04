package org.supercsv.ext.builder.time;

import java.lang.annotation.Annotation;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;
import java.util.Optional;
import java.util.TimeZone;

import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.ext.annotation.CsvDateConverter;
import org.supercsv.ext.builder.AbstractCellProcessorBuilder;
import org.supercsv.ext.cellprocessor.time.FutureTemporal;
import org.supercsv.ext.cellprocessor.time.PastTemporal;
import org.supercsv.ext.cellprocessor.time.TemporalRange;
import org.supercsv.ext.util.Utils;

/**
 * JSR-310 'Date and Time API' の{@link TemporalAccessor}のテンプレートクラス。
 * <p>基本的に、{@link TemporalAccessor}のサブクラスのビルダは、このクラスを継承して作成する。
 *
 * @since 1.2
 * @author T.TSUCHIE
 *
 */
public abstract class AbstractTemporalAccessorCellProcessorBuilder<T extends TemporalAccessor & Comparable<? super T>>
        extends AbstractCellProcessorBuilder<T> {
    
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
                .filter(s -> s.length() > 0)
                .orElse(getDefaultPattern());
    }
    
    /**
     * 読み込み時に日時の解析方法の設定を取得する。
     * @param converterAnno 変換規則を定義したアノテーション。
     * @return アノテーションがない場合は、デフォルト値を返す。
     */
    protected ResolverStyle getResolverStyle(final Optional<CsvDateConverter> converterAnno) {
        
        return converterAnno.map(a -> a.lenient() ? ResolverStyle.LENIENT : ResolverStyle.STRICT)
                .orElse(ResolverStyle.LENIENT);
        
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
    protected ZoneId getZoneId(final Optional<CsvDateConverter> converterAnno) {
        
        return converterAnno.map(a -> a.timezone())
                .filter(s -> s.length() > 0)
                .map(s ->TimeZone.getTimeZone(s).toZoneId())
                .orElse(ZoneId.systemDefault());
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
            
            final TemporalRange<T> cp;
            if(cellProcessor == null) {
                cp = new TemporalRange<T>(min.get(), max.get());
            } else {
                cp = new TemporalRange<T>(min.get(), max.get(), cellProcessor);
            }
            
            cp.setFormatter(formatter);
            return cp;
            
        } else if(min.isPresent()) {
            
            final FutureTemporal<T> cp;
            if(cellProcessor == null) {
                cp = new FutureTemporal<T>(min.get());
            } else {
                cp = new FutureTemporal<T>(min.get(), cellProcessor);
            }
            
            cp.setFormatter(formatter);
            return cp;
            
        } else if(max.isPresent()) {
            
            final PastTemporal<T> cp;
            if(cellProcessor == null) {
                cp = new PastTemporal<T>(max.get());
            } else {
                cp = new PastTemporal<T>(max.get(), cellProcessor);
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
        final ResolverStyle style = getResolverStyle(converterAnno);
        final Locale locale = getLocale(converterAnno);
        final ZoneId zone = getZoneId(converterAnno);
        
        return DateTimeFormatter.ofPattern(pattern, locale)
                .withResolverStyle(style)
                .withZone(zone);
        
    }
    
    
    
}

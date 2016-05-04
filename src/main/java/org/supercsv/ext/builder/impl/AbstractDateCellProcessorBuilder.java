package org.supercsv.ext.builder.impl;

import java.lang.annotation.Annotation;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;
import java.util.TimeZone;

import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.ext.annotation.CsvDateConverter;
import org.supercsv.ext.builder.AbstractCellProcessorBuilder;
import org.supercsv.ext.builder.CellProcessorBuilder;
import org.supercsv.ext.cellprocessor.constraint.DateRange;
import org.supercsv.ext.cellprocessor.constraint.FutureDate;
import org.supercsv.ext.cellprocessor.constraint.PastDate;
import org.supercsv.ext.exception.SuperCsvInvalidAnnotationException;
import org.supercsv.ext.util.Utils;

/**
 * {@link Date}型の{@link CellProcessorBuilder}のテンプレートクラス。
 * <p>基本的に、{@link Date}のサブクラスのビルダは、このクラスを継承して作成する。
 * 
 * @since 1.2
 * @author T.TSUCHIE
 *
 */
public abstract class AbstractDateCellProcessorBuilder<T extends Date> extends AbstractCellProcessorBuilder<T> {
    
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
     * 読み込み時に日時の解析を厳密に行うか判定するかの設定を取得する。
     * @param converterAnno 変換規則を定義したアノテーション。
     * @return アノテーションがない場合は、デフォルト値を返す。
     */
    protected boolean getLenient(final Optional<CsvDateConverter> converterAnno) {
        
        return converterAnno.map(a -> a.lenient())
                .orElse(true);
        
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
    protected TimeZone getTimeZone(final Optional<CsvDateConverter> converterAnno) {
        return converterAnno.map(a -> a.timezone())
                .filter(s -> s.length() > 0)
                .map(s -> TimeZone.getTimeZone(s))
                .orElse(TimeZone.getDefault());
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
        
        final DateFormat formatter = createDateFormatter(getDateConverterAnnotation(annos));
        
        if(min.isPresent() && max.isPresent()) {
            
            final DateRange<T> cp;
            if(cellProcessor == null) {
                cp = new DateRange<T>(min.get(), max.get());
            } else {
                cp = new DateRange<T>(min.get(), max.get(), cellProcessor);
            }
            
            cp.setFormatter(formatter);
            return cp;
            
        } else if(min.isPresent()) {
            
            final FutureDate<T> cp;
            if(cellProcessor == null) {
                cp = new FutureDate<T>(min.get());
            } else {
                cp = new FutureDate<T>(min.get(), cellProcessor);
            }
            
            cp.setFormatter(formatter);
            return cp;
            
        } else if(max.isPresent()) {
            
            final PastDate<T> cp;
            if(cellProcessor == null) {
                cp = new PastDate<T>(max.get());
            } else {
                cp = new PastDate<T>(max.get(), cellProcessor);
            }
            
            cp.setFormatter(formatter);
            return cp;
        }
        
        return cellProcessor;
    }
    
    /**
     * 変換規則用のアノテーションが定義されていないときの標準の書式を取得する。
     * 
     * @return {@link SimpleDateFormat}で解析可能な日時の書式。
     */
    protected abstract String getDefaultPattern();
    
    /**
     * 変換規則から、{@link DateFormat}のインスタンスを作成する。
     * @param converterAnno 変換規則を定義したアノテーション。
     * @return 日時のフォーマッタ。
     */
    protected DateFormat createDateFormatter(final Optional<CsvDateConverter> converterAnno) {
        
        final String pattern = getPattern(converterAnno);
        final boolean lenient = getLenient(converterAnno);
        final Locale locale = getLocale(converterAnno);
        final TimeZone timeZone = getTimeZone(converterAnno);
        
        final DateFormat formatter = new SimpleDateFormat(pattern, locale);
        formatter.setLenient(lenient);
        formatter.setTimeZone(timeZone);
        
        return formatter;
        
    }
    
    /**
     * 文字列を変換規則に従い{@link Date}型に変換する。
     * @param annos 変換規則が定義されたアノテーション一覧。
     * @param strValue 変換対象の文字列。
     * @return 変換された値。変換対象の文字列が空の場合は、空を返す。
     */
    protected Optional<Date> parseDate(final Annotation[] annos, final String strValue) {
        
        if(strValue.isEmpty()) {
            return Optional.empty();
        }
        
        final Optional<CsvDateConverter> converterAnno = getDateConverterAnnotation(annos);
        final DateFormat formatter = createDateFormatter(converterAnno);
        final String pattern = getPattern(converterAnno);
        
        try {
            return Optional.of(formatter.parse(strValue));
        } catch (ParseException e) {
            throw new SuperCsvInvalidAnnotationException(
                    String.format(" default '%s' value cannot parse to Date with pattern '%s'", strValue, pattern),
                    e);
        }
    }
    
}

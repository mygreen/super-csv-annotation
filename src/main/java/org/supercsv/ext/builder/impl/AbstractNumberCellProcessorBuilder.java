package org.supercsv.ext.builder.impl;

import java.lang.annotation.Annotation;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;
import java.util.Optional;

import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.ext.annotation.CsvNumberConverter;
import org.supercsv.ext.builder.AbstractCellProcessorBuilder;
import org.supercsv.ext.builder.CellProcessorBuilder;
import org.supercsv.ext.cellprocessor.constraint.Max;
import org.supercsv.ext.cellprocessor.constraint.Min;
import org.supercsv.ext.cellprocessor.constraint.Range;
import org.supercsv.ext.util.Utils;


/**
 * 数値型の{@link CellProcessorBuilder}のテンプレートクラス。
 * <p>基本的に、{@link Number}のサブクラスのビルダは、このクラスを継承して作成する。
 * 
 * @version 1.2
 * @author T.TSUCHIE
 *
 */
public abstract class AbstractNumberCellProcessorBuilder<N extends Number & Comparable<N>> extends AbstractCellProcessorBuilder<N> {
    
    /**
     * アノテーション{@link CsvNumberConverter} を取得する。
     * @param annos アノテーションの一覧
     * @return アノテーションの定義がない場合は空を返す。
     */
    protected Optional<CsvNumberConverter> getNumberConverterAnnotation(final Annotation[] annos) {
        
        return getAnnotation(annos, CsvNumberConverter.class);
        
    }
    
    /**
     * 数値の書式を取得する。
     * @param converterAnno 変換規則を定義したアノテーション。
     * @return アノテーションがない場合は、空を返す。
     */
    protected Optional<String> getPattern(final Optional<CsvNumberConverter> converterAnno) {
        
        return converterAnno.map(a -> a.pattern())
                .filter(s -> s.length() > 0);
    }
    
    /**
     * 読み込み時に数値の解析を厳密に行うか判定するかの設定を取得する。
     * @param converterAnno 変換規則を定義したアノテーション。
     * @return アノテーションがない場合は、デフォルト値を返す。
     */
    protected boolean getLenient(final Optional<CsvNumberConverter> converterAnno) {
        
        return converterAnno.map(a -> a.lenient())
                .orElse(true);
    }
    
    /**
     * ロケールを取得する。
     * @param converterAnno 変換規則を定義したアノテーション。
     * @return アノテーションがない場合は、システムのデフォルト値を返す。
     */
    protected Locale getLocale(final Optional<CsvNumberConverter> converterAnno) {
        
        return converterAnno.map(a -> Utils.getLocale(a.locale()))
                .orElse(Locale.getDefault());
    }
    
    /**
     * 通貨記号を取得する。
     * @param converterAnno 変換規則を定義したアノテーション。
     * @return アノテーションがない場合は、空を返す。
     */
    protected Optional<Currency> getCurrency(final Optional<CsvNumberConverter> converterAnno) {
        
        return converterAnno.map(a -> a.currency())
                .filter(s -> s.length() > 0)
                .map(s -> Currency.getInstance(s));
    }
    
    /**
     * 数値の丸め方法を取得する。
     * @param converterAnno 変換規則を定義したアノテーション。
     * @return アノテーションがない場合は、空を返す。
     */
    protected Optional<RoundingMode> getRounding(final Optional<CsvNumberConverter> converterAnno) {
        
       return converterAnno.map(a -> a.rounding());
    }
    
    /**
     * 指定された値以下かどうかをチェックするための最小値を取得する。
     * @param converterAnno 変換規則を定義したアノテーション。
     * @return アノテーションがない場合や、値がない場合は、空を返す。
     */
    protected Optional<String> getMin(final Optional<CsvNumberConverter> converterAnno) {
        
        return converterAnno.map(a -> a.min())
                .filter(s -> s.length() > 0);
        
    }
    
    /**
     * 指定された値以上かどうかをチェックするための最大値を取得する。
     * @param converterAnno 変換規則を定義したアノテーション。
     * @return アノテーションがない場合や、値がない場合は、空を返す。
     */
    protected Optional<String> getMax(final Optional<CsvNumberConverter> converterAnno) {
        
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
    protected CellProcessor prependRangeProcessor(final Class<N> type, final Annotation[] annos, final CellProcessor cellProcessor,
            final Optional<N> min, final Optional<N> max) {
        
        final Optional<NumberFormat> formatter = createNumberFormatter(getNumberConverterAnnotation(annos));
        
        if(min.isPresent() && max.isPresent()) {
            
            final Range<N> cp;
            if(cellProcessor == null) {
                cp = new Range<N>(min.get(), max.get());
            } else {
                cp = new Range<N>(min.get(), max.get(), cellProcessor);
            }
            
            formatter.ifPresent(f -> cp.setFormatter(f));
            return cp;
            
        } else if(min.isPresent()) {
            
            final Min<N> cp;
            if(cellProcessor == null) {
                cp = new Min<N>(min.get());
            } else {
                cp = new Min<N>(min.get(), cellProcessor);
            }
            
            formatter.ifPresent(f -> cp.setFormatter(f));
            return cp;
            
        } else if(max.isPresent()) {
            
            final Max<N> cp;
            if(cellProcessor == null) {
                cp = new Max<N>(max.get());
            } else {
                cp = new Max<N>(max.get(), cellProcessor);
            }
            
            formatter.ifPresent(f -> cp.setFormatter(f));
            return cp;
        }
        
        return cellProcessor;
    }
    
    /**
     * 変換規則から、{@link NumberFormat}のインスタンスを作成する。
     * @param converterAnno 変換規則を定義したアノテーション。
     * @return 数値のフォーマッタ。アノテーションがなかったり、書式(pattern)が指定されていない場合は、空を返す。
     */
    protected Optional<NumberFormat> createNumberFormatter(final Optional<CsvNumberConverter> converterAnno) {
        
        final Optional<String> pattern = getPattern(converterAnno);
        
        if(!pattern.isPresent()) {
            return Optional.empty();
        }
        
//        final boolean lenient = getLenient(converterAnno);
        final Locale locale = getLocale(converterAnno);
        final Optional<Currency> currency = getCurrency(converterAnno);
        final Optional<RoundingMode> roundingMode = getRounding(converterAnno);
        final DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance(locale);
        
        final DecimalFormat formatter = new DecimalFormat(pattern.get(), symbols);
        formatter.setParseBigDecimal(true);
        
        roundingMode.ifPresent(r -> formatter.setRoundingMode(r));
        currency.ifPresent(c -> formatter.setCurrency(c));
        
        return Optional.of(formatter);
    }
    
    
}

package com.github.mygreen.supercsv.builder.standard;

import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Currency;
import java.util.Locale;
import java.util.Optional;

import org.supercsv.cellprocessor.ift.CellProcessor;

import com.github.mygreen.supercsv.annotation.constraint.CsvNumberMax;
import com.github.mygreen.supercsv.annotation.constraint.CsvNumberMin;
import com.github.mygreen.supercsv.annotation.constraint.CsvNumberRange;
import com.github.mygreen.supercsv.annotation.format.CsvNumberFormat;
import com.github.mygreen.supercsv.builder.AbstractProcessorBuilder;
import com.github.mygreen.supercsv.builder.Configuration;
import com.github.mygreen.supercsv.builder.FieldAccessor;
import com.github.mygreen.supercsv.cellprocessor.constraint.NumberMaxFactory;
import com.github.mygreen.supercsv.cellprocessor.constraint.NumberMinFactory;
import com.github.mygreen.supercsv.cellprocessor.constraint.NumberRangeFactory;
import com.github.mygreen.supercsv.cellprocessor.format.NumberFormatWrapper;
import com.github.mygreen.supercsv.cellprocessor.format.SimpleNumberFormatter;
import com.github.mygreen.supercsv.cellprocessor.format.TextFormatter;
import com.github.mygreen.supercsv.util.Utils;


/**
 * 数値型に対する{@link CellProcessor}を組み立てるクラス。
 * <p>各種タイプごとに実装を行う。</p>
 * 
 * @version 2.0
 * @author T.TSUCHIE
 *
 */
public abstract class AbstractNumberProcessorBuilder<N extends Number & Comparable<N>> extends AbstractProcessorBuilder<N> {
    
    public AbstractNumberProcessorBuilder() {
        super();
        
    }
    
    @Override
    protected void init() {
        super.init();
        
        // 制約のアノテーションの追加
        registerForConstraint(CsvNumberRange.class, new NumberRangeFactory<>());
        registerForConstraint(CsvNumberMin.class, new NumberMinFactory<>());
        registerForConstraint(CsvNumberMax.class, new NumberMaxFactory<>());
        
        
    }
    
    @Override
    protected TextFormatter<N> getDefaultFormatter(final FieldAccessor field, final Configuration config) {
        
        final Optional<NumberFormatWrapper<N>> formatter = createFormatter(field, config);
        if(formatter.isPresent()) {
            return formatter.get();
            
        } else {
            return createSimpleFormatter(field, config);
            
        }
        
    }
    
    /**
     * 数値のフォーマッタを作成する。
     * <p>アノテーション{@link CsvNumberFormat}の値を元に作成します。</p>
     * @param field フィールド情報
     * @param config システム設定
     * @return アノテーション{@link CsvNumberFormat}が付与されていない場合は、空を返す。
     */
    @SuppressWarnings("unchecked")
    protected Optional<NumberFormatWrapper<N>> createFormatter(final FieldAccessor field, final Configuration config) {
        
        final Optional<CsvNumberFormat> formatAnno = field.getAnnotation(CsvNumberFormat.class);
        
        if(!formatAnno.isPresent()) {
            return Optional.empty();
        }
        
        final String pattern = formatAnno.get().pattern();
        if(pattern.isEmpty()) {
            return Optional.empty();
        }
        
        final boolean lenient = formatAnno.get().lenient();
        final Locale locale = Utils.getLocale(formatAnno.get().locale());
        final Optional<Currency> currency = formatAnno.get().currency().isEmpty() ? Optional.empty()
                : Optional.of(Currency.getInstance(formatAnno.get().currency()));
        final RoundingMode roundingMode = formatAnno.get().rounding();
        final DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance(locale);
        
        final DecimalFormat formatter = new DecimalFormat(pattern, symbols);
        formatter.setParseBigDecimal(true);
        formatter.setRoundingMode(roundingMode);
        currency.ifPresent(c -> formatter.setCurrency(c));
        
        final NumberFormatWrapper<N> wrapper = new NumberFormatWrapper<>(formatter, (Class<N>)field.getType(), lenient);
        wrapper.setValidationMessage(formatAnno.get().message());
        
        return Optional.of(wrapper);
        
    }
    
    /**
     * アノテーション{@link CsvNumberFormat}の指定がないときなどの書式のない数値のフォーマッタの作成。
     * <p>{@link #createFormatter(FieldAccessor, Configuration)}で結果が空の時に使用する。</p>
     * 
     * @param field フィールド情報
     * @param config システム設定
     * @return
     */
    @SuppressWarnings("unchecked")
    protected SimpleNumberFormatter<N> createSimpleFormatter(final FieldAccessor field, final Configuration config) {
        
        final Optional<CsvNumberFormat> formatAnno = field.getAnnotation(CsvNumberFormat.class);
        if(!formatAnno.isPresent()) {
            return new SimpleNumberFormatter<>((Class<N>)field.getType(), false);
        }
        
        final boolean lenient = formatAnno.get().lenient();
        final RoundingMode roundingMode = formatAnno.get().rounding();
        final int precision = formatAnno.get().precision();
        
        final SimpleNumberFormatter<N> formatter;
        if(precision >= 0) {
            formatter = new SimpleNumberFormatter<>((Class<N>)field.getType(), lenient, new MathContext(precision, roundingMode));
            
        } else {
            formatter = new SimpleNumberFormatter<>((Class<N>)field.getType(), lenient);
            
        }
        
        formatter.setValidationMessage(formatAnno.get().message());
        
        return formatter;
        
    }
    
}

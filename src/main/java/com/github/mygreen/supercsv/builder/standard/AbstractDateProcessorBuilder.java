package com.github.mygreen.supercsv.builder.standard;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;
import java.util.TimeZone;

import org.supercsv.cellprocessor.ift.CellProcessor;

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
import com.github.mygreen.supercsv.cellprocessor.format.DateFormatWrapper;
import com.github.mygreen.supercsv.cellprocessor.format.SimpleDateFormatBuilder;
import com.github.mygreen.supercsv.util.Utils;

/**
 * 日時型に対する{@link CellProcessor}を組み立てるクラス。
 * <p>各種タイプごとに実装を行う。</p>
 * 
 * @version 2.0
 * @author T.TSUCHIE
 *
 */
public abstract class AbstractDateProcessorBuilder<T extends Date> extends AbstractProcessorBuilder<T> {
    
    public AbstractDateProcessorBuilder() {
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
     * 日時のフォーマッタを作成する。
     * <p>アノテーション{@link CsvDateTimeFormat}が付与されていない場合は、各種タイプごとの標準の書式で作成する。</p>
     * @param field プロパティ情報
     * @param config システム設定
     * @return {@link DateFormatWrapper}のインスタンス。
     */
    @SuppressWarnings("unchecked")
    protected DateFormatWrapper<T> getDefaultFormatter(final FieldAccessor field, final Configuration config) {
        
        final Optional<CsvDateTimeFormat> formatAnno = field.getAnnotation(CsvDateTimeFormat.class);
        if(!formatAnno.isPresent()) {
            return new DateFormatWrapper<>(new SimpleDateFormat(getDefaultPattern()), (Class<T>)field.getType());
        }
        
        String pattern = formatAnno.get().pattern();
        if(pattern.isEmpty()) {
            pattern = getDefaultPattern();
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
        
        final DateFormatWrapper<T> wrapper = new DateFormatWrapper<>(formatter, (Class<T>)field.getType()); 
        wrapper.setValidationMessage(formatAnno.get().message());
        
        return wrapper;
        
    }
    
    /**
     * 変換規則用のアノテーションが定義されていないときの標準の書式を取得する。
     * 
     * @return {@link SimpleDateFormat}で解析可能な日時の書式。
     */
    protected abstract String getDefaultPattern();
    
}

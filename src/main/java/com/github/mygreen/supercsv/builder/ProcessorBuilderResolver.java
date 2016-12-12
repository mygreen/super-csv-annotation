package com.github.mygreen.supercsv.builder;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import com.github.mygreen.supercsv.builder.standard.BigDecimalProcessorBuilder;
import com.github.mygreen.supercsv.builder.standard.BigIntegerProcessorBuilder;
import com.github.mygreen.supercsv.builder.standard.BooleanProcessorBuilder;
import com.github.mygreen.supercsv.builder.standard.ByteProcessorBuilder;
import com.github.mygreen.supercsv.builder.standard.CalendarProcessorBuilder;
import com.github.mygreen.supercsv.builder.standard.CharacterProcessorBuilder;
import com.github.mygreen.supercsv.builder.standard.DateProcessorBuilder;
import com.github.mygreen.supercsv.builder.standard.DoubleProcessorBuilder;
import com.github.mygreen.supercsv.builder.standard.EnumProcessorBuilder;
import com.github.mygreen.supercsv.builder.standard.FloatProcessorBuilder;
import com.github.mygreen.supercsv.builder.standard.IntegerProcessorBuilder;
import com.github.mygreen.supercsv.builder.standard.LongProcessorBuilder;
import com.github.mygreen.supercsv.builder.standard.ShortProcessorBuilder;
import com.github.mygreen.supercsv.builder.standard.SqlDateProcessorBuilder;
import com.github.mygreen.supercsv.builder.standard.StringProcessorBuilder;
import com.github.mygreen.supercsv.builder.standard.TimeProcessorBuilder;
import com.github.mygreen.supercsv.builder.standard.TimestampProcessorBuilder;
import com.github.mygreen.supercsv.builder.time.LocalDateProcessorBuilder;
import com.github.mygreen.supercsv.builder.time.LocalDateTimeProcessorBuilder;
import com.github.mygreen.supercsv.builder.time.LocalTimeProcessorBuilder;
import com.github.mygreen.supercsv.builder.time.ZonedDateTimeProcessorBuilder;
import com.github.mygreen.supercsv.util.Utils;

/**
 * 各タイプに対して、登録された{@link ProcessorBuilder}を解決するクラス。
 * 
 * @version 2.0
 * @author T.TSUCHIE
 *
 */
public class ProcessorBuilderResolver {
    
    private Map<Class<?>, ProcessorBuilder<?>> builderMap = new HashMap<>();
    
    /**
     * デフォルトコンストラクタ。
     * <p>{@link #init()}メソッドが呼ばれる。
     */
    public ProcessorBuilderResolver() {
        init();
    }
    
    /**
     * 標準の{@link ProcessorBuilder}を登録緒する。
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void init() {
        
        register(String.class, new StringProcessorBuilder());
        
        register(Character.class, new CharacterProcessorBuilder());
        register(char.class, new CharacterProcessorBuilder());
        
        register(Boolean.class, new BooleanProcessorBuilder());
        register(boolean.class, new BooleanProcessorBuilder());
        
        register(Byte.class, new ByteProcessorBuilder());
        register(byte.class, new ByteProcessorBuilder());
        register(Short.class, new ShortProcessorBuilder());
        register(short.class, new ShortProcessorBuilder());
        register(Integer.class, new IntegerProcessorBuilder());
        register(int.class, new IntegerProcessorBuilder());
        register(Long.class, new LongProcessorBuilder());
        register(long.class, new LongProcessorBuilder());
        register(Float.class, new FloatProcessorBuilder());
        register(float.class, new FloatProcessorBuilder());
        register(Double.class, new DoubleProcessorBuilder());
        register(double.class, new DoubleProcessorBuilder());
        
        register(BigDecimal.class, new BigDecimalProcessorBuilder());
        register(BigInteger.class, new BigIntegerProcessorBuilder());
        
        register(Calendar.class, new CalendarProcessorBuilder());
        register(java.util.Date.class, new DateProcessorBuilder());
        register(java.sql.Date.class, new SqlDateProcessorBuilder());
        register(Time.class, new TimeProcessorBuilder());
        register(Timestamp.class, new TimestampProcessorBuilder());
        
        register(Enum.class, new EnumProcessorBuilder());
        
        // Java8 Date and Tiem API
        register(LocalDateTime.class, new LocalDateTimeProcessorBuilder());
        register(LocalDate.class, new LocalDateProcessorBuilder());
        register(LocalTime.class, new LocalTimeProcessorBuilder());
        register(ZonedDateTime.class, new ZonedDateTimeProcessorBuilder());
        
        // Joda-Time
        if(Utils.isEnabledJodaTime()) {
            register(org.joda.time.LocalDateTime.class, new com.github.mygreen.supercsv.builder.joda.LocalDateTimeProcessorBuilder());
            register(org.joda.time.LocalDate.class, new com.github.mygreen.supercsv.builder.joda.LocalDateProcessorBuilder());
            register(org.joda.time.LocalTime.class, new com.github.mygreen.supercsv.builder.joda.LocalTimeProcessorBuilder());
            register(org.joda.time.DateTime.class, new com.github.mygreen.supercsv.builder.joda.DateTimeProcessorBuilder());
        }
        
    }
    
    /**
     * 指定したクラスタイプに対する{@link ProcessorBuilder}を取得する。
     * 
     * @param <T> 対象のオブジェクトタイプ
     * @param type クラスタイプ。
     * @return 解決できない場合は、{@literal null}を返す。
     */
    @SuppressWarnings("unchecked")
    public <T> ProcessorBuilder<T> resolve(final Class<T> type) {
        
        ProcessorBuilder<?> builder = builderMap.get(type);
        if(builder == null && Enum.class.isAssignableFrom(type)) {
            // 列挙型の場合は、具象化されたクラスタイプである必要があるため、別途取得する
            builder = builderMap.get(Enum.class);
        }
        
        return (ProcessorBuilder<T>) builder;
    }
    
    /**
     * {@link ProcessorBuilder}を登録する。
     * <p>既に登録済みのものがある場合、新しい値に置き換えられます。
     * 
     * @param <T> 対象のオブジェクトタイプ
     * @param type クラスタイプ。
     * @param builder {@link ProcessorBuilder}の実装。
     * @return 以前に登録されている値を返す。登録済みのものが内場合は{@literal null}を返す。
     */
    @SuppressWarnings("unchecked")
    public <T> ProcessorBuilder<T> register(final Class<T> type, final ProcessorBuilder<T> builder) {
        return (ProcessorBuilder<T>)this.builderMap.put(type, builder);
    }
    
}

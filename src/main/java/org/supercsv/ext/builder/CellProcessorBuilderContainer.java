package org.supercsv.ext.builder;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.supercsv.ext.builder.impl.BigDecimalCellProcessorBuilder;
import org.supercsv.ext.builder.impl.BigIntegerCellProcessorBuilder;
import org.supercsv.ext.builder.impl.BooleanCellProcessorBuilder;
import org.supercsv.ext.builder.impl.ByteCellProcessorBuilder;
import org.supercsv.ext.builder.impl.CharacterCellProcessorBuilder;
import org.supercsv.ext.builder.impl.DateCellProcessorBuilder;
import org.supercsv.ext.builder.impl.DoubleCellProcessorBuilder;
import org.supercsv.ext.builder.impl.EnumCellProcessorBuilder;
import org.supercsv.ext.builder.impl.FloatCellProcessorBuilder;
import org.supercsv.ext.builder.impl.IntegerCellProcessorBuilder;
import org.supercsv.ext.builder.impl.LongCellProcessorBuilder;
import org.supercsv.ext.builder.impl.ShortCellProcessorBuilder;
import org.supercsv.ext.builder.impl.SqlDateCellProcessorBuilder;
import org.supercsv.ext.builder.impl.StringCellProcessorBuilder;
import org.supercsv.ext.builder.impl.TimeCellProcessorBuilder;
import org.supercsv.ext.builder.impl.TimestampCellProcessorBuilder;


/**
 *
 * @version 1.2
 * @author T.TSUCHIE
 *
 */
public class CellProcessorBuilderContainer {
    
    @SuppressWarnings("rawtypes")
    private final Map<Class<?>, CellProcessorBuilder> builderMap;
    
    @SuppressWarnings("rawtypes")
    public CellProcessorBuilderContainer() {
        builderMap = new ConcurrentHashMap<Class<?>, CellProcessorBuilder>();
        init();
    }
    
    public void init() {
        builderMap.clear();
        
        registerBuilder(String.class, new StringCellProcessorBuilder());
        
        registerBuilder(Character.class, new CharacterCellProcessorBuilder());
        registerBuilder(char.class, new CharacterCellProcessorBuilder());
        
        registerBuilder(Boolean.class, new BooleanCellProcessorBuilder());
        registerBuilder(boolean.class, new BooleanCellProcessorBuilder());
        
        registerBuilder(Byte.class, new ByteCellProcessorBuilder());
        registerBuilder(byte.class, new ByteCellProcessorBuilder());
        registerBuilder(Short.class, new ShortCellProcessorBuilder());
        registerBuilder(short.class, new ShortCellProcessorBuilder());
        registerBuilder(Integer.class, new IntegerCellProcessorBuilder());
        registerBuilder(int.class, new IntegerCellProcessorBuilder());
        registerBuilder(Long.class, new LongCellProcessorBuilder());
        registerBuilder(long.class, new LongCellProcessorBuilder());
        registerBuilder(Float.class, new FloatCellProcessorBuilder());
        registerBuilder(float.class, new FloatCellProcessorBuilder());
        registerBuilder(Double.class, new DoubleCellProcessorBuilder());
        registerBuilder(double.class, new DoubleCellProcessorBuilder());
        
        registerBuilder(BigDecimal.class, new BigDecimalCellProcessorBuilder());
        registerBuilder(BigInteger.class, new BigIntegerCellProcessorBuilder());
        
        registerBuilder(Date.class, new DateCellProcessorBuilder());
        registerBuilder(java.sql.Date.class, new SqlDateCellProcessorBuilder());
        registerBuilder(Timestamp.class, new TimestampCellProcessorBuilder());
        registerBuilder(Time.class, new TimeCellProcessorBuilder());
        
        registerBuilder(Enum.class, new EnumCellProcessorBuilder());
        
        try {
            // Availabled Super CSV Java8 Extension
            Class.forName("org.supercsv.cellprocessor.time.FmtLocalDateTime");
            
            registerBuilder(java.time.LocalDate.class, new org.supercsv.ext.builder.time.LocalDateCellProcessorBuilder());
            registerBuilder(java.time.LocalDateTime.class, new org.supercsv.ext.builder.time.LocalDateTimeCellProcessorBuilder());
            registerBuilder(java.time.LocalTime.class, new org.supercsv.ext.builder.time.LocalTimeCellProcessorBuilder());
            
            registerBuilder(java.time.chrono.JapaneseDate.class, new org.supercsv.ext.builder.time.LocalDateCellProcessorBuilder());
            
            registerBuilder(java.time.ZonedDateTime.class, new org.supercsv.ext.builder.time.ZonedDateTimeCellProcessorBuilder());
            
            registerBuilder(java.time.Duration.class, new org.supercsv.ext.builder.time.DurationCellProcessorBuilder());
            registerBuilder(java.time.Period.class, new org.supercsv.ext.builder.time.PeriodCellProcessorBuilder());
            registerBuilder(java.time.ZoneId.class, new org.supercsv.ext.builder.time.ZoneIdCellProcessorBuilder());
            
        } catch(ClassNotFoundException e) {
            
        }
        
        try {
            // Availabled Super CSV Joda-Time Extension
            Class.forName("org.supercsv.cellprocessor.joda.FmtLocalDateTime");
            
            registerBuilder(org.joda.time.LocalDate.class, new org.supercsv.ext.builder.joda.LocalDateCellProcessorBuilder());
            registerBuilder(org.joda.time.LocalDateTime.class, new org.supercsv.ext.builder.joda.LocalDateTimeCellProcessorBuilder());
            registerBuilder(org.joda.time.LocalTime.class, new org.supercsv.ext.builder.joda.LocalTimeCellProcessorBuilder());
            
            registerBuilder(org.joda.time.Duration.class, new org.supercsv.ext.builder.joda.DurationCellProcessorBuilder());
            registerBuilder(org.joda.time.Interval.class, new org.supercsv.ext.builder.joda.IntervalCellProcessorBuilder());
            registerBuilder(org.joda.time.DateTimeZone.class, new org.supercsv.ext.builder.joda.DateTimeZoneCellProcessorBuilder());
            
        } catch(ClassNotFoundException e) {
            
        }
    }
    
    public <T> void registerBuilder(final Class<?> type, final CellProcessorBuilder<T> builder) {
        builderMap.put(type, builder);
        
    }
    
    @SuppressWarnings("unchecked")
    public <T> CellProcessorBuilder<T> getBuilder(final Class<?> type) {
        return builderMap.get(type);
    }
    
}

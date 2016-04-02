/*
 * CellProcessorBuilderContainer.java
 * created in 2013/03/05
 *
 * (C) Copyright 2003-2013 GreenDay Project. All rights reserved.
 */
package org.supercsv.ext.builder;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.chrono.JapaneseDate;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.supercsv.ext.builder.time.DurationCellProcessorBuilder;
import org.supercsv.ext.builder.time.LocalDateCellProcessorBuilder;
import org.supercsv.ext.builder.time.LocalDateTimeCellProcessorBuilder;
import org.supercsv.ext.builder.time.LocalTimeCellProcessorBuilder;
import org.supercsv.ext.builder.time.PeriodCellProcessorBuilder;
import org.supercsv.ext.builder.time.ZoneIdCellProcessorBuilder;
import org.supercsv.ext.builder.time.ZonedDateTimeCellProcessorBuilder;


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
        
        registerBuilder(Byte.class, NumberCellProcessorBuilder.newByte());
        registerBuilder(byte.class, NumberCellProcessorBuilder.newByte());
        registerBuilder(Short.class, NumberCellProcessorBuilder.newShort());
        registerBuilder(short.class, NumberCellProcessorBuilder.newShort());
        registerBuilder(Integer.class, NumberCellProcessorBuilder.newInteger());
        registerBuilder(int.class, NumberCellProcessorBuilder.newInteger());
        registerBuilder(Long.class, NumberCellProcessorBuilder.newLong());
        registerBuilder(long.class, NumberCellProcessorBuilder.newLong());
        registerBuilder(Float.class, NumberCellProcessorBuilder.newFloat());
        registerBuilder(float.class, NumberCellProcessorBuilder.newFloat());
        registerBuilder(Double.class, NumberCellProcessorBuilder.newDouble());
        registerBuilder(double.class, NumberCellProcessorBuilder.newDouble());
        
        registerBuilder(BigDecimal.class, NumberCellProcessorBuilder.newBigDecimal());
        registerBuilder(BigInteger.class, NumberCellProcessorBuilder.newBigInteger());
        
        registerBuilder(Date.class, new DateCellProcessorBuilder());
        registerBuilder(java.sql.Date.class, DateCellProcessorBuilder.newSqlDate());
        registerBuilder(Timestamp.class, DateCellProcessorBuilder.newTimestamp());
        registerBuilder(Time.class, DateCellProcessorBuilder.newTime());
        
        registerBuilder(Enum.class, new EnumCellProcessorBuilder());
        
        try {
            // Availabled Super CSV Java8 Extension
            Class.forName("org.supercsv.cellprocessor.time.FmtLocalDateTime");
            
            registerBuilder(LocalDate.class, new LocalDateCellProcessorBuilder());
            registerBuilder(LocalDateTime.class, new LocalDateTimeCellProcessorBuilder());
            registerBuilder(LocalTime.class, new LocalTimeCellProcessorBuilder());
            
            registerBuilder(JapaneseDate.class, new LocalDateCellProcessorBuilder());
            
            registerBuilder(ZonedDateTime.class, new ZonedDateTimeCellProcessorBuilder());
            
            registerBuilder(Duration.class, new DurationCellProcessorBuilder());
            registerBuilder(Period.class, new PeriodCellProcessorBuilder());
            registerBuilder(ZoneId.class, new ZoneIdCellProcessorBuilder());
            
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

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
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 *
 *
 * @author T.TSUCHIE
 *
 */
public class CellProcessorBuilderContainer {
    
    @SuppressWarnings("rawtypes")
    private final Map<Class<?>, AbstractCellProcessorBuilder> builderMap;
    
    @SuppressWarnings("rawtypes")
    public CellProcessorBuilderContainer() {
        builderMap = new ConcurrentHashMap<Class<?>, AbstractCellProcessorBuilder>();
        init();
    }
    
    public void init() {
        builderMap.clear();
        
        registBuilder(String.class, new StringCellProcessorBuilder());
        
        registBuilder(Boolean.class, new BooleanCellProcessorBuilder());
        registBuilder(boolean.class, new BooleanCellProcessorBuilder());
        
        registBuilder(Byte.class, NumberCellProcessorBuilder.newByte());
        registBuilder(byte.class, NumberCellProcessorBuilder.newByte());
        registBuilder(Short.class, NumberCellProcessorBuilder.newShort());
        registBuilder(short.class, NumberCellProcessorBuilder.newShort());
        registBuilder(Integer.class, NumberCellProcessorBuilder.newInteger());
        registBuilder(int.class, NumberCellProcessorBuilder.newInteger());
        registBuilder(Long.class, NumberCellProcessorBuilder.newLong());
        registBuilder(long.class, NumberCellProcessorBuilder.newLong());
        registBuilder(Float.class, NumberCellProcessorBuilder.newFloat());
        registBuilder(float.class, NumberCellProcessorBuilder.newFloat());
        registBuilder(Double.class, NumberCellProcessorBuilder.newDouble());
        registBuilder(double.class, NumberCellProcessorBuilder.newDouble());
        
        registBuilder(BigDecimal.class, NumberCellProcessorBuilder.newBigDecimal());
        registBuilder(BigInteger.class, NumberCellProcessorBuilder.newBigInteger());
        
        registBuilder(Character.class, new CharacterCellProcessorBuilder());
        registBuilder(char.class, new CharacterCellProcessorBuilder());
        
        registBuilder(Date.class, new DateCellProcessorBuilder());
        registBuilder(java.sql.Date.class, DateCellProcessorBuilder.newSqlDate());
        registBuilder(Timestamp.class, DateCellProcessorBuilder.newTimestamp());
        registBuilder(Time.class, DateCellProcessorBuilder.newTime());
        
        registBuilder(Enum.class, new EnumCellProcessorBuilder());
    }
    
    public <T> void registBuilder(final Class<?> type, final AbstractCellProcessorBuilder<T> builder) {
        builderMap.put(type, builder);
        
    }
    
    @SuppressWarnings("unchecked")
    public <T> AbstractCellProcessorBuilder<T> getBuilder(final Class<?> type) {
        return builderMap.get(type);
    }
    
}

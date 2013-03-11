/*
 * ParseShort.java
 * created in 2013/03/06
 *
 * (C) Copyright 2003-2013 GreenDay Project. All rights reserved.
 */
package org.supercsv.ext.cellprocessor;

import java.util.EnumSet;
import java.util.Iterator;

import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;
import org.supercsv.exception.SuperCsvCellProcessorException;
import org.supercsv.util.CsvContext;


/**
 *
 *
 * @author T.TSUCHIE
 *
 */
@SuppressWarnings("rawtypes")
public class ParseEnum extends CellProcessorAdaptor implements StringCellProcessor {
    
    final Class type;
    
    final boolean lenient;
    
    public ParseEnum(final Class type) {
        this(type, false);
    }
    
    public ParseEnum(final Class type, final CellProcessor next) {
        this(type, false, next);
    }
    
    public ParseEnum(final Class type, final boolean lenient) {
        super();
        checkPreconditions(type);
        this.type = type;
        this.lenient = lenient;
    }
    
    public ParseEnum(final Class type, final boolean lenient, final CellProcessor next) {
        super(next);
        checkPreconditions(type);
        this.type = type;
        this.lenient = lenient;
    }
    
    private static void checkPreconditions(final Class type) {
        
        if(type == null) {
            throw new IllegalArgumentException("type should be not null");
        }
    }
    
    @Override
    public Object execute(final Object value, final CsvContext context) {
        
        validateInputNotNull(value, context);
        
        final Enum result;
        if(value instanceof Enum && value.getClass().isAssignableFrom(type)) {
            result = (Enum) value;
            
        } else if(value instanceof String) {
            result = valueOfEnumByName((String) value);
            if(result == null) {
                throw new SuperCsvCellProcessorException(
                        String.format("'%s' could not be parsed as an Enum", value), context, this);
            }
            
        } else {
            final String actualClassName = value.getClass().getName();
            throw new SuperCsvCellProcessorException(String.format(
                "the input value should be of type Enum or String but is of type %s", actualClassName), context, this);
        }
        
        return next.execute(result, context);
    }
    
    @SuppressWarnings("unchecked")
    protected Enum valueOfEnumByName(final String name) {
        
        EnumSet set = EnumSet.allOf((Class) type);
        for(Iterator<Enum> it = set.iterator(); it.hasNext(); ) {
            Enum e = it.next();
            
            if(name.equals(e.name())) {
                return e;
            }
            
            if(lenient && name.equalsIgnoreCase(e.name())) {
                return e;
            }
            
        }
        
        return null;
    }
    
    public Class<?> getType() {
        return type;
    }
    
    public boolean isLenient() {
        return lenient;
    }
}

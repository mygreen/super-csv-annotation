/*
 * EnumCellProcessorBuilder.java
 * created in 2013/03/05
 *
 * (C) Copyright 2003-2013 GreenDay Project. All rights reserved.
 */
package org.supercsv.ext.builder;

import java.lang.annotation.Annotation;
import java.util.EnumSet;
import java.util.Iterator;

import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.ext.annotation.CsvEnumConverter;
import org.supercsv.ext.cellprocessor.ParseEnum;


/**
 *
 *
 * @author T.TSUCHIE
 *
 */
public class EnumCellProcessorBuilder extends AbstractCellProcessorBuilder<Enum<?>> {
    
    protected CsvEnumConverter getAnnotation(Annotation[] annos) {
        
        if(annos == null || annos.length == 0) {
            return null;
        }
        
        for(Annotation anno : annos) {
            if(anno instanceof CsvEnumConverter) {
                return (CsvEnumConverter) anno;
            }
        }
        
        return null;
        
    }
    
    protected boolean getLenient(final CsvEnumConverter converterAnno) {
        if(converterAnno == null) {
            return false;
        }
        
        return converterAnno.lenient();
    }
    
    @Override
    public CellProcessor buildOutputCellProcessor(final Class<Enum<?>> type, final Annotation[] annos,
            final CellProcessor processor, final boolean ignoreValidableProcessor) {
        
//        CsvEnumConverter converterAnno = getAnnotation(annos);
        
        return processor;
    }
    
    @Override
    public CellProcessor buildInputCellProcessor(final Class<Enum<?>> type, final Annotation[] annos,
            final CellProcessor processor) {
        
        CsvEnumConverter converterAnno = getAnnotation(annos);
        final boolean lenient = getLenient(converterAnno);
        
        CellProcessor cellProcessor = processor;
        cellProcessor = (cellProcessor == null ? 
                new ParseEnum(type, lenient) : new ParseEnum(type, lenient, cellProcessor));
        
        return cellProcessor;
    }
    
    
    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public Enum getParseValue(Class<Enum<?>> type, Annotation[] annos, String defaultValue) {
        CsvEnumConverter converterAnno = getAnnotation(annos);
        final boolean lenient = getLenient(converterAnno);
        
        EnumSet set = EnumSet.allOf((Class) type);
        for(Iterator<Enum> it = set.iterator(); it.hasNext(); ) {
            Enum e = it.next();
            
            if(defaultValue.equals(e.name())) {
                return e;
            }
            
            if(lenient && defaultValue.equalsIgnoreCase(e.name())) {
                return e;
            }
            
        }
        
        throw new IllegalArgumentException(String.format("convert fail enum value %s", defaultValue));
    }
    
}

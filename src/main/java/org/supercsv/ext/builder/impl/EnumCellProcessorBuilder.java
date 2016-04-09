package org.supercsv.ext.builder.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.EnumSet;
import java.util.Iterator;

import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;
import org.supercsv.ext.annotation.CsvEnumConverter;
import org.supercsv.ext.builder.AbstractCellProcessorBuilder;
import org.supercsv.ext.cellprocessor.FormatEnum;
import org.supercsv.ext.cellprocessor.ParseEnum;
import org.supercsv.ext.exception.SuperCsvInvalidAnnotationException;


/**
 *
 * @version 1.2
 * @author T.TSUCHIE
 *
 */
public class EnumCellProcessorBuilder<T extends Enum<T>> extends AbstractCellProcessorBuilder<T> {
    
    protected CsvEnumConverter getAnnotation(final Annotation[] annos) {
        
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
    
    protected boolean getIgnoreCase(final CsvEnumConverter converterAnno) {
        if(converterAnno == null) {
            return false;
        }
        
        return converterAnno.ignoreCase();
    }
    
    protected String getValueMethodName(final CsvEnumConverter converterAnno) {
        if(converterAnno == null) {
            return "";
        }
        
        return converterAnno.valueMethodName();
    }
    
    @Override
    public CellProcessor buildOutputCellProcessor(final Class<T> type, final Annotation[] annos,
            final CellProcessor processor, final boolean ignoreValidationProcessor) {
        
        final CsvEnumConverter converterAnno = getAnnotation(annos);
        final String valueMethodName = getValueMethodName(converterAnno);
        
        CellProcessor cellProcessor = processor;
        if(!valueMethodName.isEmpty()) {
            cellProcessor = (cellProcessor == null ? 
                    new FormatEnum(type, valueMethodName) :
                        new FormatEnum(type, valueMethodName, (StringCellProcessor) cellProcessor));
        }
        
        return cellProcessor;
    }
    
    @Override
    public CellProcessor buildInputCellProcessor(final Class<T> type, final Annotation[] annos,
            final CellProcessor processor) {
        
        final CsvEnumConverter converterAnno = getAnnotation(annos);
        final boolean ignoreCase = getIgnoreCase(converterAnno);
        final String valueMethodName = getValueMethodName(converterAnno);
        
        CellProcessor cellProcessor = processor;
        if(valueMethodName.isEmpty()) {
            cellProcessor = (cellProcessor == null ? 
                    new ParseEnum(type, ignoreCase) : new ParseEnum(type, ignoreCase, cellProcessor));
        } else {
            cellProcessor = (cellProcessor == null ? 
                    new ParseEnum(type, ignoreCase, valueMethodName) :
                        new ParseEnum(type, ignoreCase, valueMethodName, cellProcessor));
        }
        
        return cellProcessor;
    }
    
    
    @Override
    public T getParseValue(final Class<T> type, final Annotation[] annos, final String defaultValue) {
        CsvEnumConverter converterAnno = getAnnotation(annos);
        final boolean ignoreCase = getIgnoreCase(converterAnno);
        final String valueMethodName = getValueMethodName(converterAnno);
        
        final EnumSet<T> set = EnumSet.allOf(type);
        if(valueMethodName.isEmpty()) {
            for(Iterator<T> it = set.iterator(); it.hasNext(); ) {
                T e = it.next();
                
                if(defaultValue.equals(e.name())) {
                    return e;
                }
                
                if(ignoreCase && defaultValue.equalsIgnoreCase(e.name())) {
                    return e;
                }
                
            }
        } else {
            try {
                final Method valueMethod = type.getMethod(valueMethodName);
                for(Iterator<T> it = set.iterator(); it.hasNext(); ) {
                    T e = it.next();
                    
                    final String value = valueMethod.invoke(e).toString();
                    if(defaultValue.equals(value)) {
                        return e;
                    }
                    
                    if(ignoreCase && defaultValue.equalsIgnoreCase(value)) {
                        return e;
                    }
                    
                }
            } catch(Exception e) {
                throw new SuperCsvInvalidAnnotationException(
                        String.format("enum class '%s' has not method '%s'", type.getCanonicalName(), valueMethodName));
            }
        }
        
        throw new SuperCsvInvalidAnnotationException(String.format("convert fail enum value %s", defaultValue));
    }
    
}

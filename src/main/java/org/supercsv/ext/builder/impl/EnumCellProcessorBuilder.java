package org.supercsv.ext.builder.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.EnumSet;

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
        
        CellProcessor cp = processor;
        if(!valueMethodName.isEmpty()) {
            cp = (cp == null ? 
                    new FormatEnum(type, valueMethodName) :
                        new FormatEnum(type, valueMethodName, (StringCellProcessor) cp));
        }
        
        return cp;
    }
    
    @Override
    public CellProcessor buildInputCellProcessor(final Class<T> type, final Annotation[] annos,
            final CellProcessor processor) {
        
        final CsvEnumConverter converterAnno = getAnnotation(annos);
        final boolean ignoreCase = getIgnoreCase(converterAnno);
        final String valueMethodName = getValueMethodName(converterAnno);
        
        CellProcessor cp = processor;
        if(valueMethodName.isEmpty()) {
            cp = (cp == null ? 
                    new ParseEnum(type, ignoreCase) : new ParseEnum(type, ignoreCase, cp));
        } else {
            cp = (cp == null ? 
                    new ParseEnum(type, ignoreCase, valueMethodName) :
                        new ParseEnum(type, ignoreCase, valueMethodName, cp));
        }
        
        return cp;
    }
    
    
    @Override
    public T getParseValue(final Class<T> type, final Annotation[] annos, final String strValue) {
        
        final CsvEnumConverter converterAnno = getAnnotation(annos);
        final boolean ignoreCase = getIgnoreCase(converterAnno);
        final String valueMethodName = getValueMethodName(converterAnno);
        
        final EnumSet<T> set = EnumSet.allOf(type);
        if(valueMethodName.isEmpty()) {
            for(T e : set) {
                if(strValue.equals(e.name())) {
                    return e;
                }
                
                if(ignoreCase && strValue.equalsIgnoreCase(e.name())) {
                    return e;
                }
                
            }
            
        } else {
            try {
                final Method valueMethod = type.getMethod(valueMethodName);
                valueMethod.setAccessible(true);
                
                for(T e: set) {
                    final String value = valueMethod.invoke(e).toString();
                    if(strValue.equals(value)) {
                        return e;
                    }
                    
                    if(ignoreCase && strValue.equalsIgnoreCase(value)) {
                        return e;
                    }
                }
                
            } catch(ReflectiveOperationException e) {
                throw new SuperCsvInvalidAnnotationException(
                        String.format("enum class '%s' has not method '%s'", type.getCanonicalName(), valueMethodName));
            }
        }
        
        throw new SuperCsvInvalidAnnotationException(String.format("parse fail enum value %s", strValue));
    }
    
}

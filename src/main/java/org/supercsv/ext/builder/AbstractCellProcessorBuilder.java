/*
 * AbstractCellProcessorBuilder.java
 * created in 2013/03/05
 *
 * (C) Copyright 2003-2013 GreenDay Project. All rights reserved.
 */
package org.supercsv.ext.builder;

import java.lang.annotation.Annotation;

import org.supercsv.cellprocessor.ConvertNullTo;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.constraint.Equals;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.constraint.Unique;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;
import org.supercsv.ext.annotation.CsvColumn;
import org.supercsv.ext.cellprocessor.Trim;


/**
 *
 * @version 1.1
 * @author T.TSUCHIE
 *
 */
public abstract class AbstractCellProcessorBuilder<T> implements CellProcessorBuilder<T> {
    
    protected CsvColumn getCsvColumnAnnotation(final Annotation[] annos) {
        
        for(Annotation anno : annos) {
            if(anno instanceof CsvColumn) {
                return (CsvColumn) anno;
            }
        }
        
        return null;
        
    }
    
    @Override
    public CellProcessor buildOutputCellProcessor(final Class<T> type, final Annotation[] annos,
            final boolean ignoreValidationProcessor) {
        
        final CsvColumn csvColumnAnno = getCsvColumnAnnotation(annos);
        
        CellProcessor cellProcessor = null;
        
        if(csvColumnAnno.trim()) {
            cellProcessor = prependTrimProcessor(cellProcessor);
        }
        
        cellProcessor = buildOutputCellProcessor(type, annos, cellProcessor, ignoreValidationProcessor);
        
        if(csvColumnAnno.unique() && !ignoreValidationProcessor) {
            cellProcessor = prependUniqueProcessor(cellProcessor);
        }
        
        if(!csvColumnAnno.equalsValue().isEmpty() && !ignoreValidationProcessor) {
            cellProcessor = prependEqualsProcessor(type, cellProcessor,
                    getParseValue(type, annos, csvColumnAnno.equalsValue()));
        }
        
        if(csvColumnAnno.optional() && !type.isPrimitive()) {
            cellProcessor = prependOptionalProcessor(cellProcessor);
        } else {
            cellProcessor = prependNotNullProcessor(cellProcessor);
        }
        
        if(!csvColumnAnno.outputDefaultValue().isEmpty()) {
            cellProcessor = prependConvertNullToProcessor(type, cellProcessor,
//                    getParseValue(type, annos, csvColumnAnno.outputDefaultValue()))
                    csvColumnAnno.outputDefaultValue());
        }
        
        return cellProcessor;
    }
    
    @Override
    public CellProcessor buildInputCellProcessor(final Class<T> type, final Annotation[] annos) {
        
        final CsvColumn csvColumnAnno = getCsvColumnAnnotation(annos);
        
        CellProcessor cellProcessor = null;
        cellProcessor = buildInputCellProcessor(type, annos, cellProcessor);
        
        if(csvColumnAnno.unique()) {
            cellProcessor = prependUniqueProcessor(cellProcessor);
        }
        
        if(!csvColumnAnno.equalsValue().isEmpty()) {
            cellProcessor = prependEqualsProcessor(type, cellProcessor,
                    getParseValue(type, annos, csvColumnAnno.equalsValue()));
        }
        
        if(csvColumnAnno.trim()) {
            cellProcessor = prependTrimProcessor(cellProcessor);
        }
        
        if(csvColumnAnno.optional() && !type.isPrimitive()) {
            cellProcessor = prependOptionalProcessor(cellProcessor);
        } else {
            cellProcessor = prependNotNullProcessor(cellProcessor);
        }
        
        if(!csvColumnAnno.inputDefaultValue().isEmpty()) {
            cellProcessor = prependConvertNullToProcessor(type, cellProcessor,
                    getParseValue(type, annos, csvColumnAnno.inputDefaultValue()));
        }
        
        return cellProcessor;
    }
    
    protected CellProcessor prependConvertNullToProcessor(final Class<T> type, final CellProcessor processor, final Object value) {
        
        return (processor == null ? 
                new ConvertNullTo(value) : new ConvertNullTo(value, processor));
    }
    
    protected CellProcessor prependEqualsProcessor(final Class<T> type, final CellProcessor processor, final Object value) {
        
        return (processor == null ? 
                new Equals(value) : new Equals(value, processor));
    }
    
    protected CellProcessor prependUniqueProcessor(final CellProcessor processor) {
        return (processor == null ? new Unique() : new Unique(processor));
    }
    
    protected CellProcessor prependOptionalProcessor(final CellProcessor processor) {
        return (processor == null ? new Optional() : new Optional(processor));
    }
    
    protected CellProcessor prependNotNullProcessor(final CellProcessor processor) {
        return (processor == null ? new NotNull() : new NotNull(processor));
    }
    
    protected CellProcessor prependTrimProcessor(final CellProcessor processor) {
        return (processor == null ? new Trim() : new Trim((StringCellProcessor) processor));
    }
    
    public abstract CellProcessor buildOutputCellProcessor(Class<T> type, Annotation[] annos, CellProcessor processor, boolean ignoreValidationProcessor);
    
    public abstract CellProcessor buildInputCellProcessor(Class<T> type, Annotation[] annos, CellProcessor processor);
    
    public abstract T getParseValue(Class<T> type, Annotation[] annos, String strValue);
    
}

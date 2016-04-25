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
        
        if(csvColumnAnno.optional()) {
            cellProcessor = prependOptionalProcessor(cellProcessor);
        } else {
            cellProcessor = prependNotNullProcessor(cellProcessor, annos);
        }
        
        cellProcessor = buildOutputCellProcessorWithConvertNullTo(type, annos, ignoreValidationProcessor, cellProcessor, csvColumnAnno);
        
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
                    csvColumnAnno.equalsValue());
        }
        if(csvColumnAnno.trim()) {
            cellProcessor = prependTrimProcessor(cellProcessor);
        }
        
        if(csvColumnAnno.optional()) {
            cellProcessor = prependOptionalProcessor(cellProcessor);
        } else {
            cellProcessor = prependNotNullProcessor(cellProcessor, annos);
        }
        
        cellProcessor = buildInputCellProcessorWithConvertNullTo(type, annos, cellProcessor, csvColumnAnno);
        
        return cellProcessor;
    }
    
    protected CellProcessor buildOutputCellProcessorWithConvertNullTo(final Class<T> type, final Annotation[] annos, final boolean ignoreValidationProcessor,
            final CellProcessor cellProcessor, final CsvColumn csvColumnAnno) {
        
        if(!csvColumnAnno.outputDefaultValue().isEmpty()) {
            final Object defaultValue = csvColumnAnno.outputDefaultValue();
            return prependConvertNullToProcessor(type, cellProcessor, defaultValue);
        }
        
        return cellProcessor;
    }
    
    protected CellProcessor buildInputCellProcessorWithConvertNullTo(final Class<T> type, final Annotation[] annos,
            final CellProcessor cellProcessor, final CsvColumn csvColumnAnno) {
        
        if(!csvColumnAnno.inputDefaultValue().isEmpty()) {
            return prependConvertNullToProcessor(type, cellProcessor,
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
    
    protected CellProcessor prependNotNullProcessor(final CellProcessor processor, final Annotation[] annos) {
        return (processor == null ? new NotNull() : new NotNull(processor));
    }
    
    protected CellProcessor prependTrimProcessor(final CellProcessor processor) {
        return (processor == null ? new Trim() : new Trim((StringCellProcessor) processor));
    }
    
    public abstract CellProcessor buildOutputCellProcessor(Class<T> type, Annotation[] annos, CellProcessor processor, boolean ignoreValidationProcessor);
    
    public abstract CellProcessor buildInputCellProcessor(Class<T> type, Annotation[] annos, CellProcessor processor);
    
    public abstract T getParseValue(Class<T> type, Annotation[] annos, String strValue);
    
}

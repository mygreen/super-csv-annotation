package org.supercsv.ext.builder;

import java.lang.annotation.Annotation;

import org.supercsv.cellprocessor.ift.CellProcessor;


/**
 * cell processor builder for root interface.
 * 
 * @since 1.1
 * @param <T> 
 * @author T.TSUCHIE
 *
 */
public interface CellProcessorBuilder<T> {
    
    /**
     * build cell processors for writing.
     * @param type
     * @param annos
     * @param ignoreValidationProcessor
     * @return
     */
    CellProcessor buildOutputCellProcessor(Class<T> type, Annotation[] annos, boolean ignoreValidationProcessor);
    
    /**
     * build cell processor for reading.
     * @param type
     * @param annos
     * @return
     */
    CellProcessor buildInputCellProcessor(Class<T> type, Annotation[] annos);
    
}

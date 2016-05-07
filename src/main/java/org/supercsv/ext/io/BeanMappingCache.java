package org.supercsv.ext.io;

import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.ext.builder.CsvBeanMapping;


/**
 *
 *
 * @author T.TSUCHIE
 *
 */
public class BeanMappingCache {
    
    private final String[] header;
    
    private final String[] nameMapping;
    
    private final CellProcessor[] inputCellProcessors;
    
    private final CellProcessor[] outputCellProcessors;
    
    @SuppressWarnings("rawtypes")
    public BeanMappingCache(CsvBeanMapping mapping) {
        this.header = mapping.getHeader();
        this.nameMapping = mapping.getNameMapping();
        this.inputCellProcessors = mapping.getInputCellProcessor();
        this.outputCellProcessors = mapping.getOutputCellProcessor();
    }
    
    public String[] getHeader() {
        return header;
    }
    
    public String[] getNameMapping() {
        return nameMapping;
    }
    
    public CellProcessor[] getInputCellProcessors() {
        return inputCellProcessors;
    }
    
    public CellProcessor[] getOutputCellProcessors() {
        return outputCellProcessors;
    }
    
}

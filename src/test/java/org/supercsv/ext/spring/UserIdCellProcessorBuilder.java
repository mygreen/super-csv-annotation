package org.supercsv.ext.spring;

import java.lang.annotation.Annotation;

import org.springframework.beans.factory.annotation.Autowired;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.ext.builder.StringCellProcessorBuilder;


/**
 *
 *
 * @author T.TSUCHIE
 *
 */
public class UserIdCellProcessorBuilder extends StringCellProcessorBuilder {
    
    @Autowired
    private UserService userService;
    
    @Override
    public CellProcessor buildOutputCellProcessor(final Class<String> type, final  Annotation[] annos,
            final CellProcessor processor, final boolean ignoreValidationProcessor) {
        
        CellProcessor cellProcessor = super.buildOutputCellProcessor(type, annos, processor, ignoreValidationProcessor);
        return cellProcessor;
    }
    
    @Override
    public CellProcessor buildInputCellProcessor(final Class<String> type, final Annotation[] annos,
            final CellProcessor processor) {
        
        CellProcessor cellProcessor = processor;
        if(cellProcessor == null) {
            cellProcessor = new UserIdExist(userService);
        } else {
            cellProcessor = new UserIdExist(userService, cellProcessor);
        }
        
        cellProcessor = super.buildInputCellProcessor(type, annos, cellProcessor);
        
        return cellProcessor;
    }
    
}

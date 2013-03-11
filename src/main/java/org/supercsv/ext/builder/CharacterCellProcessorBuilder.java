/*
 * CharacterCellProcessorBuilder.java
 * created in 2013/03/05
 *
 * (C) Copyright 2003-2013 GreenDay Project. All rights reserved.
 */
package org.supercsv.ext.builder;

import java.lang.annotation.Annotation;

import org.supercsv.cellprocessor.ParseChar;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.ift.DoubleCellProcessor;


/**
 *
 *
 * @author T.TSUCHIE
 *
 */
public class CharacterCellProcessorBuilder extends AbstractCellProcessorBuilder<Character> {

    @Override
    public CellProcessor buildOutputCellProcessor(final Class<Character> type, final Annotation[] annos,
            final CellProcessor processor, final boolean ignoreValidationProcessor) {
        return processor;
    }

    @Override
    public CellProcessor buildInputCellProcessor(final Class<Character> type, final Annotation[] annos, 
            final CellProcessor processor) {
        
        CellProcessor cellProcessor = processor;
        cellProcessor = (cellProcessor == null ? new ParseChar() : new ParseChar((DoubleCellProcessor) cellProcessor));
        
        return cellProcessor;
    }
    
    @Override
    public Character getParseValue(final Class<Character> type, final Annotation[] annos, final String defaultValue) {
        return defaultValue.charAt(0);
    }
}

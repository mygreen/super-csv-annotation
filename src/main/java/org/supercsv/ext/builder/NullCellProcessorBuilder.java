/*
 * NullCellProcessorBuilder.java
 * created in 2013/03/08
 *
 * (C) Copyright 2003-2013 GreenDay Project. All rights reserved.
 */
package org.supercsv.ext.builder;

import java.lang.annotation.Annotation;

import org.supercsv.cellprocessor.ift.CellProcessor;


/**
 *
 *
 * @author T.TSUCHIE
 *
 */
public class NullCellProcessorBuilder extends AbstractCellProcessorBuilder<Class<?>>{
    
    @Override
    public CellProcessor buildOutputCellProcessor(final Class<Class<?>> type, final Annotation[] annos,
            CellProcessor processor, final boolean ignoreValidableProcessor) {
        return processor;
    }
    
    @Override
    public CellProcessor buildInputCellProcessor(final Class<Class<?>> type, final Annotation[] annos,
            final CellProcessor processor) {
        return processor;
    }
    
    @Override
    public Class<?> getParseValue(Class<Class<?>> type, Annotation[] annos, String defaultValue) {
        return null;
    }
    
}

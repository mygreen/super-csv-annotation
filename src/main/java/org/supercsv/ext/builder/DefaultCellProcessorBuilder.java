/*
 * DefaultCellProcessorBuilder.java
 * created in 2013/03/08
 *
 * (C) Copyright 2003-2013 GreenDay Project. All rights reserved.
 */
package org.supercsv.ext.builder;

import java.lang.annotation.Annotation;

import org.supercsv.cellprocessor.ift.CellProcessor;


/**
 *
 * @version 1.1
 * @author T.TSUCHIE
 *
 */
public class DefaultCellProcessorBuilder extends AbstractCellProcessorBuilder<Class<?>>{
    
    public static DefaultCellProcessorBuilder INSTANCE = new DefaultCellProcessorBuilder();
    
    @Override
    public CellProcessor buildOutputCellProcessor(final Class<Class<?>> type, final Annotation[] annos,
            CellProcessor processor, final boolean ignoreValidationProcessor) {
        return processor;
    }
    
    @Override
    public CellProcessor buildInputCellProcessor(final Class<Class<?>> type, final Annotation[] annos,
            final CellProcessor processor) {
        return processor;
    }
    
    @Override
    public Class<?> getParseValue(final Class<Class<?>> type, final Annotation[] annos, final String defaultValue) {
        return null;
    }
    
}

/*
 * BooleanCellProcessorBuilder.java
 * created in 2013/03/05
 *
 * (C) Copyright 2003-2013 GreenDay Project. All rights reserved.
 */
package org.supercsv.ext.builder;

import java.lang.annotation.Annotation;

import org.supercsv.cellprocessor.FmtBool;
import org.supercsv.cellprocessor.ift.BoolCellProcessor;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;
import org.supercsv.ext.annotation.CsvBooleanConverter;
import org.supercsv.ext.cellprocessor.ParseBoolean;
import org.supercsv.ext.exception.SuperCsvInvalidAnnotationException;


/**
 *
 * @version 1.2
 * @author T.TSUCHIE
 *
 */
public class BooleanCellProcessorBuilder extends AbstractCellProcessorBuilder<Boolean> {
    
    protected CsvBooleanConverter getAnnotation(final Annotation[] annos) {
        
        if(annos == null || annos.length == 0) {
            return null;
        }
        
        for(Annotation anno : annos) {
            if(anno instanceof CsvBooleanConverter) {
                return (CsvBooleanConverter) anno;
            }
        }
        
        return null;
        
    }
    
    protected String getOutputTrueValue(final CsvBooleanConverter converterAnno) {
        if(converterAnno == null) {
            return "true";
        }
        
        return converterAnno.outputTrueValue();
    }
    
    protected String getOutputFalseValue(final CsvBooleanConverter converterAnno) {
        if(converterAnno == null) {
            return "false";
        }
        
        return converterAnno.outputFalseValue();
    }
    
    protected String[] getInputTrueValue(final CsvBooleanConverter converterAnno) {
        if(converterAnno == null) {
            return new String[]{"true", "1", "yes", "on", "y", "t"};
        }
        
        return converterAnno.inputTrueValue();
    }
    
    protected String[] getInputFalseValue(final CsvBooleanConverter converterAnno) {
        if(converterAnno == null) {
            return new String[]{"false", "0", "no", "off", "f", "n"};
        }
        
        return converterAnno.inputFalseValue();
    }
    
    protected boolean getIgnoreCase(final CsvBooleanConverter converterAnno) {
        if(converterAnno == null) {
            return false;
        }
        
        return converterAnno.ignoreCase();
    }
    
    protected boolean getFailToFalse(final CsvBooleanConverter converterAnno) {
        if(converterAnno == null) {
            return false;
        }
        
        return converterAnno.failToFalse();
    }
    
    @Override
    public CellProcessor buildOutputCellProcessor(final Class<Boolean> type, final Annotation[] annos,
            final CellProcessor processor, final boolean ignoreValidationProcessor) {
        
        final CsvBooleanConverter converterAnno = getAnnotation(annos);
        final String trueValue = getOutputTrueValue(converterAnno);
        final String falseValue = getOutputFalseValue(converterAnno);
        
        CellProcessor cellProcessor = processor;
        cellProcessor = (cellProcessor == null 
                ? new FmtBool(trueValue, falseValue) : new FmtBool(trueValue, falseValue, (StringCellProcessor) cellProcessor));
        return cellProcessor;
        
    }
    
    @Override
    public CellProcessor buildInputCellProcessor(final Class<Boolean> type, final Annotation[] annos,
            final CellProcessor processor) {
        
        final CsvBooleanConverter converterAnno = getAnnotation(annos);
        final String[] trueValue = getInputTrueValue(converterAnno);
        final String[] falseValue = getInputFalseValue(converterAnno);
        final boolean ignoreCase = getIgnoreCase(converterAnno);
        final boolean failToFalse = getFailToFalse(converterAnno);
        
        CellProcessor cellProcessor = processor;
        cellProcessor = (cellProcessor == null
                ? new ParseBoolean(trueValue, falseValue, ignoreCase).setFailToFalse(failToFalse) :
                    new ParseBoolean(trueValue, falseValue, ignoreCase, (BoolCellProcessor) cellProcessor).setFailToFalse(failToFalse));
        
        return cellProcessor;
    }
    
    @Override
    public Boolean getParseValue(final Class<Boolean> type, final Annotation[] annos, final String defaultValue) {
        final CsvBooleanConverter converterAnno = getAnnotation(annos);
        final String[] trueValue = getInputTrueValue(converterAnno);
        final String[] falseValue = getInputFalseValue(converterAnno);
        final boolean ignoreCase = getIgnoreCase(converterAnno);
        final boolean failToFalse = getFailToFalse(converterAnno);
        
        for(String trueStr : trueValue) {
            if(ignoreCase && trueStr.equalsIgnoreCase(defaultValue)) {
                return Boolean.TRUE;
            } else if(!ignoreCase && trueStr.equals(defaultValue)) {
                return Boolean.TRUE;
            }
        }
        
        for(String falseStr : falseValue) {
            if(ignoreCase && falseStr.equalsIgnoreCase(defaultValue)) {
                return Boolean.FALSE;
            } else if(!ignoreCase && falseStr.equals(defaultValue)) {
                return Boolean.FALSE;
            }
        }
        
        if(failToFalse) {
            return Boolean.TRUE;
        }
        
        throw new SuperCsvInvalidAnnotationException(String.format("defaultValue'%s' cannot parse.", defaultValue));
    }
    
    
}

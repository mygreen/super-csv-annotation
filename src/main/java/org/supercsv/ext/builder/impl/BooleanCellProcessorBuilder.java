package org.supercsv.ext.builder.impl;

import java.lang.annotation.Annotation;

import org.supercsv.cellprocessor.FmtBool;
import org.supercsv.cellprocessor.ift.BoolCellProcessor;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;
import org.supercsv.ext.annotation.CsvBooleanConverter;
import org.supercsv.ext.annotation.CsvColumn;
import org.supercsv.ext.builder.AbstractCellProcessorBuilder;
import org.supercsv.ext.cellprocessor.ParseBoolean;
import org.supercsv.ext.exception.SuperCsvInvalidAnnotationException;


/**
 *
 * @version 1.2
 * @author T.TSUCHIE
 *
 */
public class BooleanCellProcessorBuilder extends AbstractCellProcessorBuilder<Boolean> {
    
    @Override
    protected CellProcessor buildInputCellProcessorWithConvertNullTo(final Class<Boolean> type, final Annotation[] annos,
            final CellProcessor cellProcessor, final CsvColumn csvColumnAnno) {
        
        // プリミティブ型の場合、オプションかつ初期値が与えられていない場合、falseに変換する。
        if(type.isPrimitive() && csvColumnAnno.optional() && csvColumnAnno.inputDefaultValue().isEmpty()) {
            return prependConvertNullToProcessor(type, cellProcessor, false);
            
        } else if(!csvColumnAnno.inputDefaultValue().isEmpty()) {
            return prependConvertNullToProcessor(type, cellProcessor,
                    getParseValue(type, annos, csvColumnAnno.inputDefaultValue()));
        }
        
        return cellProcessor;
    }
    
    protected CsvBooleanConverter getAnnotation(final Annotation[] annos) {
        
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
        
        CellProcessor cp = processor;
        cp = (cp == null 
                ? new FmtBool(trueValue, falseValue) : new FmtBool(trueValue, falseValue, (StringCellProcessor) cp));
        return cp;
        
    }
    
    @Override
    public CellProcessor buildInputCellProcessor(final Class<Boolean> type, final Annotation[] annos,
            final CellProcessor processor) {
        
        final CsvBooleanConverter converterAnno = getAnnotation(annos);
        final String[] trueValue = getInputTrueValue(converterAnno);
        final String[] falseValue = getInputFalseValue(converterAnno);
        final boolean ignoreCase = getIgnoreCase(converterAnno);
        final boolean failToFalse = getFailToFalse(converterAnno);
        
        CellProcessor cp = processor;
        cp = (cp == null
                ? new ParseBoolean(trueValue, falseValue, ignoreCase).setFailToFalse(failToFalse) :
                    new ParseBoolean(trueValue, falseValue, ignoreCase, (BoolCellProcessor) cp).setFailToFalse(failToFalse));
        
        return cp;
    }
    
    @Override
    public Boolean getParseValue(final Class<Boolean> type, final Annotation[] annos, final String strValue) {
        
        final CsvBooleanConverter converterAnno = getAnnotation(annos);
        final String[] trueValue = getInputTrueValue(converterAnno);
        final String[] falseValue = getInputFalseValue(converterAnno);
        final boolean ignoreCase = getIgnoreCase(converterAnno);
        final boolean failToFalse = getFailToFalse(converterAnno);
        
        for(String trueStr : trueValue) {
            if(ignoreCase && trueStr.equalsIgnoreCase(strValue)) {
                return Boolean.TRUE;
            } else if(!ignoreCase && trueStr.equals(strValue)) {
                return Boolean.TRUE;
            }
        }
        
        for(String falseStr : falseValue) {
            if(ignoreCase && falseStr.equalsIgnoreCase(strValue)) {
                return Boolean.FALSE;
            } else if(!ignoreCase && falseStr.equals(strValue)) {
                return Boolean.FALSE;
            }
        }
        
        if(failToFalse) {
            return Boolean.FALSE;
        }
        
        throw new SuperCsvInvalidAnnotationException(String.format("defaultValue '%s' cannot parse.", strValue));
    }
    
    
}

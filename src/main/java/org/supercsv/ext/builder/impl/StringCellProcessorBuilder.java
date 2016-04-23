package org.supercsv.ext.builder.impl;

import java.lang.annotation.Annotation;

import org.supercsv.cellprocessor.constraint.ForbidSubStr;
import org.supercsv.cellprocessor.constraint.RequireSubStr;
import org.supercsv.cellprocessor.constraint.StrNotNullOrEmpty;
import org.supercsv.cellprocessor.constraint.StrRegEx;
import org.supercsv.cellprocessor.constraint.Strlen;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;
import org.supercsv.ext.annotation.CsvColumn;
import org.supercsv.ext.annotation.CsvStringConverter;
import org.supercsv.ext.builder.AbstractCellProcessorBuilder;
import org.supercsv.ext.cellprocessor.constraint.Length;
import org.supercsv.ext.cellprocessor.constraint.MaxLength;
import org.supercsv.ext.cellprocessor.constraint.MinLength;


/**
 *
 *
 * @author T.TSUCHIE
 *
 */
public class StringCellProcessorBuilder extends AbstractCellProcessorBuilder<String> {
    
    public static final String CONVERT_NULL_STRING_EMPTY = "@empty";
    
    protected CsvStringConverter getAnnotation(final Annotation[] annos) {
        
        for(Annotation anno : annos) {
            if(anno instanceof CsvStringConverter) {
                return (CsvStringConverter) anno;
            }
        }
        
        return null;
        
    }
    
    protected Integer getMinLength(final CsvStringConverter converterAnno) {
        if(converterAnno == null) {
            return null;
        }
        
        if(converterAnno.minLength() < 0) {
            return null;
        }
        
        return converterAnno.minLength();
    }
    
    protected Integer getMaxLength(final CsvStringConverter converterAnno) {
        if(converterAnno == null) {
            return null;
        }
        
        if(converterAnno.maxLength() < 0) {
            return null;
        }
        
        return converterAnno.maxLength();
    }
    
    protected int[] getExactLength(final CsvStringConverter converterAnno) {
        if(converterAnno == null) {
            return null;
        }
        
        if(converterAnno.exactLength().length == 0) {
            return null;
        }
        
        return converterAnno.exactLength();
    }
    
    protected String getRegex(final CsvStringConverter converterAnno) {
        if(converterAnno == null) {
            return "";
        }
        
        return converterAnno.regex();
    }
    
    protected String[] getForbid(final CsvStringConverter converterAnno) {
        if(converterAnno == null) {
            return new String[]{};
        }
        
        return converterAnno.forbid();
    }
    
    protected String[] getContain(final CsvStringConverter converterAnno) {
        if(converterAnno == null) {
            return new String[]{};
        }
        
        return converterAnno.contain();
    }
    
    protected boolean getNotEmpty(final CsvStringConverter converterAnno) {
        if(converterAnno == null) {
            return false;
        }
        
        return converterAnno.notEmpty();
    }
    
    @Override
    protected CellProcessor buildOutputCellProcessorWithConvertNullTo(final Class<String> type, final Annotation[] annos, final boolean ignoreValidationProcessor,
            final CellProcessor processor, final CsvColumn csvColumnAnno) {
        
        if(!csvColumnAnno.outputDefaultValue().isEmpty()) {
            final String defaultValue = getParseValue(type, annos, csvColumnAnno.outputDefaultValue());
            return prependConvertNullToProcessor(type, processor, defaultValue);
        }
        
        return processor;
    }
    
    @Override
    public CellProcessor buildOutputCellProcessor(final Class<String> type, final  Annotation[] annos,
            final CellProcessor processor, final boolean ignoreValidationProcessor) {
        
        final CsvStringConverter converterAnno = getAnnotation(annos);
        final Integer minLength = getMinLength(converterAnno);
        final Integer maxLength = getMaxLength(converterAnno);
        final int[] exactLength = getExactLength(converterAnno);
        final String regex = getRegex(converterAnno);
        final String[] forbid = getForbid(converterAnno);
        final String[] contain = getContain(converterAnno);
        final boolean notEmpty = getNotEmpty(converterAnno);
        
        CellProcessor cp = processor;
        
        if(!ignoreValidationProcessor) {
            cp = prependForbidProcessor(cp, forbid);
            cp = prependContainProcessor(cp, contain);
            cp = prependRegExProcessor(cp, regex);
            cp = prependLengthProcessor(cp, minLength, maxLength, exactLength);
            cp = prependNotEmptyProcessor(cp, notEmpty);
        }
        
        return cp;
    }
    
    @Override
    public CellProcessor buildInputCellProcessor(final Class<String> type, final Annotation[] annos,
            final CellProcessor processor) {
        
        final CsvStringConverter converterAnno = getAnnotation(annos);
        final Integer minLength = getMinLength(converterAnno);
        final Integer maxLength = getMaxLength(converterAnno);
        final int[] exactLength = getExactLength(converterAnno);
        final String regex = getRegex(converterAnno);
        final String[] forbid = getForbid(converterAnno);
        final String[] contain = getContain(converterAnno);
        final boolean notEmpty = getNotEmpty(converterAnno);
        
        CellProcessor cp = processor;
        cp = prependForbidProcessor(cp, forbid);
        cp = prependContainProcessor(cp, contain);
        cp = prependRegExProcessor(cp, regex);
        cp = prependLengthProcessor(cp, minLength, maxLength, exactLength);
        cp = prependNotEmptyProcessor(cp, notEmpty);
        
        return cp;
    }
    
    protected CellProcessor prependLengthProcessor(final CellProcessor processor, 
            final Integer minLength, final Integer maxLength, final int[] exactLength) {
        
        if(minLength != null && maxLength != null) {
            return (processor == null ? 
                    new Length(minLength, maxLength) : new Length(minLength, maxLength, processor));
            
        } else if(minLength != null) {
            return (processor == null ? 
                    new MinLength(minLength) : new MinLength(minLength, processor));
            
        } else if(maxLength != null) {
            return (processor == null ? 
                    new MaxLength(maxLength) : new MaxLength(maxLength, processor));
            
        } else if(exactLength != null) {
            return (processor == null ? 
                    new Strlen(exactLength) : new Strlen(exactLength, processor));
            
        }
        
        return processor;
    }
    
    protected CellProcessor prependRegExProcessor(final CellProcessor processor, final String regex) {
        
        if(regex.isEmpty()) {
            return processor;
        }
        
        return (processor == null ?
                new StrRegEx(regex) : new StrRegEx(regex, (StringCellProcessor) processor));
    }
    
    protected CellProcessor prependForbidProcessor(final CellProcessor processor, final String[] forbid) {
        
        if(forbid.length == 0) {
            return processor;
        }
        
        return (processor == null ?
                new ForbidSubStr(forbid) : new ForbidSubStr(forbid, (StringCellProcessor) processor));
        
    }
    
    protected CellProcessor prependContainProcessor(final CellProcessor processor, final String[] contain) {
        
        if(contain.length == 0) {
            return processor;
        }
        
        return (processor == null ?
                new RequireSubStr(contain) : new RequireSubStr(contain, (StringCellProcessor) processor));
        
    }
    
    protected CellProcessor prependNotEmptyProcessor(final CellProcessor processor, final boolean notEmpty) {
        
        if(!notEmpty) {
            return processor;
        }
        
        return (processor == null ?
                new StrNotNullOrEmpty() : new StrNotNullOrEmpty((StringCellProcessor) processor));
        
    }
    
    @Override
    public String getParseValue(final Class<String> type, final Annotation[] annos, final String defaultValue) {
        if(defaultValue.equals(CONVERT_NULL_STRING_EMPTY)) {
            return "";
        }
        return defaultValue;
    }
}

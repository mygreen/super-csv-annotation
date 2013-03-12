/*
 * StringCellProcessorBuilder.java
 * created in 2013/03/05
 *
 * (C) Copyright 2003-2013 GreenDay Project. All rights reserved.
 */
package org.supercsv.ext.builder;

import java.lang.annotation.Annotation;

import org.supercsv.cellprocessor.constraint.ForbidSubStr;
import org.supercsv.cellprocessor.constraint.RequireSubStr;
import org.supercsv.cellprocessor.constraint.StrNotNullOrEmpty;
import org.supercsv.cellprocessor.constraint.StrRegEx;
import org.supercsv.cellprocessor.constraint.Strlen;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;
import org.supercsv.ext.annotation.CsvStringConverter;
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
        
        if(annos == null || annos.length == 0) {
            return null;
        }
        
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
    
    protected Integer getExactLength(final CsvStringConverter converterAnno) {
        if(converterAnno == null) {
            return null;
        }
        
        if(converterAnno.exactLength() < 0) {
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
    public CellProcessor buildOutputCellProcessor(final Class<String> type, final  Annotation[] annos,
            final CellProcessor processor, final boolean ignoreValidationProcessor) {
        
        final CsvStringConverter converterAnno = getAnnotation(annos);
        final Integer minLength = getMinLength(converterAnno);
        final Integer maxLength = getMaxLength(converterAnno);
        final Integer exactLength = getExactLength(converterAnno);
        final String regex = getRegex(converterAnno);
        final String[] forbid = getForbid(converterAnno);
        final String[] contain = getContain(converterAnno);
        final boolean notEmpty = getNotEmpty(converterAnno);
        
        CellProcessor cellProcessor = processor;
        
        if(!ignoreValidationProcessor) {
            cellProcessor = prependRegExProcessor(cellProcessor, regex);
            cellProcessor = prependLengthProcessor(cellProcessor, minLength, maxLength, exactLength);
            cellProcessor = prependForbidProcessor(cellProcessor, forbid);
            cellProcessor = prependContainProcessor(cellProcessor, contain);
            cellProcessor = prependNotEmptyProcessor(cellProcessor, notEmpty);
        }
        
        return cellProcessor;
    }
    
    @Override
    public CellProcessor buildInputCellProcessor(final Class<String> type, final Annotation[] annos,
            final CellProcessor processor) {
        
        final CsvStringConverter converterAnno = getAnnotation(annos);
        final Integer minLength = getMinLength(converterAnno);
        final Integer maxLength = getMaxLength(converterAnno);
        final Integer exactLength = getExactLength(converterAnno);
        final String regex = getRegex(converterAnno);
        final String[] forbid = getForbid(converterAnno);
        final String[] contain = getContain(converterAnno);
        final boolean notEmpty = getNotEmpty(converterAnno);
        
        CellProcessor cellProcessor = processor;
        cellProcessor = prependRegExProcessor(cellProcessor, regex);
        cellProcessor = prependLengthProcessor(cellProcessor, minLength, maxLength, exactLength);
        cellProcessor = prependForbidProcessor(cellProcessor, forbid);
        cellProcessor = prependContainProcessor(cellProcessor, contain);
        cellProcessor = prependNotEmptyProcessor(cellProcessor, notEmpty);
        
        return cellProcessor;
    }
    
    protected CellProcessor prependLengthProcessor(final CellProcessor processor, 
            final Integer minLength, final Integer maxLength, final Integer exactLength) {
        
        CellProcessor cellProcessor = processor;
        
        if(minLength != null && maxLength != null) {
            if(cellProcessor == null) {
                cellProcessor = new Length(minLength, maxLength);
            } else {
                cellProcessor = new Length(minLength, maxLength, cellProcessor);
            }
        } else if(minLength != null) {
            if(cellProcessor == null) {
                cellProcessor = new MinLength(minLength);
            } else {
                cellProcessor = new MinLength(minLength, cellProcessor);
            }
        } else if(maxLength != null) {
            if(cellProcessor == null) {
                cellProcessor = new MaxLength(maxLength);
            } else {
                cellProcessor = new MaxLength(maxLength, cellProcessor);
            }
        } else if(exactLength != null) {
            if(cellProcessor == null) {
                cellProcessor = new Strlen(exactLength);
            } else {
                cellProcessor = new Strlen(exactLength, cellProcessor);
            }
            
        }
        
        return cellProcessor;
    }
    
    protected CellProcessor prependRegExProcessor(final CellProcessor processor, final String regex) {
        
        CellProcessor cellProcessor = processor;
        
        if(regex.isEmpty()) {
            return cellProcessor;
        }
        
        if(cellProcessor == null) {
            cellProcessor = new StrRegEx(regex);
        } else {
            cellProcessor = new StrRegEx(regex, (StringCellProcessor) cellProcessor);
        }
        
        return cellProcessor;
    }
    
    protected CellProcessor prependForbidProcessor(final CellProcessor processor, final String[] forbid) {
        
        CellProcessor cellProcessor = processor;
        if(forbid == null || forbid.length == 0) {
            return cellProcessor;
        }
        
        if(cellProcessor == null) {
            cellProcessor = new ForbidSubStr(forbid);
        } else {
            cellProcessor = new ForbidSubStr(forbid, (StringCellProcessor) cellProcessor);
        }
        
        return cellProcessor;
    }
    
    protected CellProcessor prependContainProcessor(final CellProcessor processor, final String[] contain) {
        
        CellProcessor cellProcessor = processor;
        
        if(contain == null || contain.length == 0) {
            return cellProcessor;
        }
        
        if(cellProcessor == null) {
            cellProcessor = new RequireSubStr(contain);
        } else {
            cellProcessor = new RequireSubStr(contain, (StringCellProcessor) cellProcessor);
        }
        
        return cellProcessor;
    }
    
    protected CellProcessor prependNotEmptyProcessor(final CellProcessor processor, final boolean notEmpty) {
        
        CellProcessor cellProcessor = processor;
        
        if(!notEmpty) {
            return cellProcessor;
        }
        
        if(cellProcessor == null) {
            cellProcessor = new StrNotNullOrEmpty();
        } else {
            cellProcessor = new StrNotNullOrEmpty((StringCellProcessor) cellProcessor);
        }
        
        return cellProcessor;
    }
    
    @Override
    public String getParseValue(final Class<String> type, final Annotation[] annos, final String defaultValue) {
        if(defaultValue.equals(CONVERT_NULL_STRING_EMPTY)) {
            return "";
        }
        return defaultValue;
    }
}

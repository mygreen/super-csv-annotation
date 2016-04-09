package org.supercsv.ext.builder.impl;

import java.lang.annotation.Annotation;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.ext.annotation.CsvNumberConverter;
import org.supercsv.ext.builder.AbstractCellProcessorBuilder;
import org.supercsv.ext.cellprocessor.constraint.Max;
import org.supercsv.ext.cellprocessor.constraint.Min;
import org.supercsv.ext.cellprocessor.constraint.Range;
import org.supercsv.ext.util.Utils;


/**
 *
 *
 * @author T.TSUCHIE
 *
 */
public abstract class AbstractNumberCellProcessorBuilder<N extends Number & Comparable<N>> extends AbstractCellProcessorBuilder<N> {
    
    protected CsvNumberConverter getAnnotation(final Annotation[] annos) {
        
        if(annos == null || annos.length == 0) {
            return null;
        }
        
        for(Annotation anno : annos) {
            if(anno instanceof CsvNumberConverter) {
                return (CsvNumberConverter) anno;
            }
        }
        
        return null;
        
    }
    
    protected String getPattern(final CsvNumberConverter converterAnno) {
        if(converterAnno == null) {
            return "";
        }
        
        return converterAnno.pattern();
    }
    
    protected boolean getLenient(final CsvNumberConverter converterAnno) {
        if(converterAnno == null) {
            return false;
        }
        
        return converterAnno.lenient();
    }
    
    protected String getMin(final CsvNumberConverter converterAnno) {
        if(converterAnno == null) {
            return "";
        }
        
        return converterAnno.min();
        
    }
    
    protected String getMax(final CsvNumberConverter converterAnno) {
        if(converterAnno == null) {
            return "";
        }
        
        return converterAnno.max();
        
    }
    
    protected Locale getLocale(final CsvNumberConverter converterAnno) {
        if(converterAnno == null) {
            return Locale.getDefault();
        }
        
        return Utils.getLocale(converterAnno.locale());
    }
    
    protected Currency getCurrency(final CsvNumberConverter converterAnno) {
        if(converterAnno == null) {
            return null;
            
        } else if(converterAnno.currency().isEmpty()) {
            return null;
        }
        
        return Currency.getInstance(converterAnno.currency());
    }
    
    protected CellProcessor prependRangeProcessor(final N min, final N max, final NumberFormat formatter, final CellProcessor processor) {
        
        CellProcessor cellProcessor = processor;
        if(min != null && max != null) {
            if(cellProcessor == null) {
                cellProcessor = new Range<N>(min, max).setFormatter(formatter);
            } else {
                cellProcessor = new Range<N>(min, max, cellProcessor).setFormatter(formatter);
            }
        } else if(min != null) {
            if(cellProcessor == null) {
                cellProcessor = new Min<N>(min).setFormatter(formatter);
            } else {
                cellProcessor = new Min<N>(min, cellProcessor).setFormatter(formatter);
            }
        } else if(max != null) {
            if(cellProcessor == null) {
                cellProcessor = new Max<N>(max).setFormatter(formatter);
            } else {
                cellProcessor = new Max<N>(max, cellProcessor).setFormatter(formatter);
            }
        }
        
        return cellProcessor;
    }
    
    protected NumberFormat createNumberFormat(final String pattern, final boolean lenient,
            final Currency currency, final DecimalFormatSymbols symbols) {
        
        if(pattern.isEmpty()) {
            return null;
        }
        
        DecimalFormat value = null;
        if(symbols != null) {
            value = new DecimalFormat(pattern, symbols);
        } else {
            value = new DecimalFormat(pattern);
        }
        
        value.setParseBigDecimal(true);
        
        if(currency != null) {
            value.setCurrency(currency);
        }
        
        return value;
    }
    
}

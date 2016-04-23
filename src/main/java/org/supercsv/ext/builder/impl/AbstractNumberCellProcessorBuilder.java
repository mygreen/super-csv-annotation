package org.supercsv.ext.builder.impl;

import java.lang.annotation.Annotation;
import java.math.RoundingMode;
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
    
    protected RoundingMode getRoundingMode(final CsvNumberConverter converterAnno) {
        if(converterAnno == null) {
            return null;
            
        }
        
        return converterAnno.roundingMode();
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
    
    protected CellProcessor prependRangeProcessor(final N min, final N max, final NumberFormat formatter, final CellProcessor processor) {
        
        CellProcessor cp = processor;
        if(min != null && max != null) {
            if(cp == null) {
                cp = new Range<N>(min, max).setFormatter(formatter);
            } else {
                cp = new Range<N>(min, max, cp).setFormatter(formatter);
            }
        } else if(min != null) {
            if(cp == null) {
                cp = new Min<N>(min).setFormatter(formatter);
            } else {
                cp = new Min<N>(min, cp).setFormatter(formatter);
            }
        } else if(max != null) {
            if(cp == null) {
                cp = new Max<N>(max).setFormatter(formatter);
            } else {
                cp = new Max<N>(max, cp).setFormatter(formatter);
            }
        }
        
        return cp;
    }
    
    protected NumberFormat createNumberFormatter(final CsvNumberConverter converterAnno) {
        
        final String pattern = getPattern(converterAnno);
        final boolean lenient = getLenient(converterAnno);
        final Locale locale = getLocale(converterAnno);
        final Currency currency = getCurrency(converterAnno);
        final RoundingMode roundingMode = getRoundingMode(converterAnno);
        final DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance(locale);
        
        return createNumberFormatter(pattern, lenient, currency, symbols, roundingMode);
        
    }
    
    protected NumberFormat createNumberFormatter(final String pattern, final boolean lenient,
            final Currency currency, final DecimalFormatSymbols symbols, final RoundingMode roundingMode) {
        
        if(pattern.isEmpty()) {
            return null;
        }
        
        final DecimalFormat formatter = new DecimalFormat(pattern, symbols);
        formatter.setParseBigDecimal(true);
        formatter.setRoundingMode(roundingMode);
        
        if(currency != null) {
            formatter.setCurrency(currency);
        }
        
        return formatter;
    }
    
}

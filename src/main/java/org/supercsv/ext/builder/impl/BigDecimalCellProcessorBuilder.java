package org.supercsv.ext.builder.impl;

import java.lang.annotation.Annotation;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;

import org.supercsv.cellprocessor.ParseBigDecimal;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;
import org.supercsv.ext.annotation.CsvNumberConverter;
import org.supercsv.ext.cellprocessor.FormatLocaleNumber;
import org.supercsv.ext.cellprocessor.ParseLocaleNumber;
import org.supercsv.ext.exception.SuperCsvInvalidAnnotationException;

public class BigDecimalCellProcessorBuilder extends AbstractNumberCellProcessorBuilder<BigDecimal> {
    
    @Override
    public CellProcessor buildOutputCellProcessor(final Class<BigDecimal> type, final Annotation[] annos,
            final CellProcessor processor, final boolean ignoreValidationProcessor) {
        
        final CsvNumberConverter converterAnno = getAnnotation(annos);
        final NumberFormat formatter = createNumberFormatter(converterAnno);
        
        final BigDecimal min = getParseValue(type, annos, getMin(converterAnno));
        final BigDecimal max = getParseValue(type, annos, getMax(converterAnno));
        
        CellProcessor cp = processor;
        if(formatter != null) {
            cp = (cp == null ?
                    new FormatLocaleNumber(formatter) : new FormatLocaleNumber(formatter, (StringCellProcessor) cp));
        }
        
        if(!ignoreValidationProcessor) {
            cp = prependRangeProcessor(min, max, formatter, cp);
        }
        
        return cp;
    }
    
    @Override
    public CellProcessor buildInputCellProcessor(final Class<BigDecimal> type, final Annotation[] annos,
            final CellProcessor processor) {
        
        final CsvNumberConverter converterAnno = getAnnotation(annos);
        final NumberFormat formatter = createNumberFormatter(converterAnno);
        final boolean lenient = getLenient(converterAnno);
        
        final BigDecimal min = getParseValue(type, annos, getMin(converterAnno));
        final BigDecimal max = getParseValue(type, annos, getMax(converterAnno));
        
        CellProcessor cp = processor;
        cp = prependRangeProcessor(min, max, formatter, cp);
        
        if(formatter != null) {
            cp = (cp == null ?
                    new ParseLocaleNumber<BigDecimal>(type, formatter, lenient) :
                        new ParseLocaleNumber<BigDecimal>(type, formatter, lenient, cp));                
        } else {
            cp = (cp == null ? 
                    new ParseBigDecimal() : new ParseBigDecimal(cp));
        }
        
        return cp;
        
    }
    
    @Override
    public BigDecimal getParseValue(final Class<BigDecimal> type, final Annotation[] annos, final String strValue) {
        
        if(strValue.isEmpty()) {
            return null;
        }
        
        final CsvNumberConverter converterAnno = getAnnotation(annos);
        final NumberFormat formatter = createNumberFormatter(converterAnno);
        final String pattern = getPattern(converterAnno);
        
        if(formatter != null) {
            try {
                return (BigDecimal) formatter.parse(strValue);
            } catch(ParseException e) {
                throw new SuperCsvInvalidAnnotationException(
                        String.format(" value '%s' cannot parse to Number with pattern '%s'", strValue, pattern),
                        e);
            }
        } else {
            return new BigDecimal(strValue);
        }
    }
    
}

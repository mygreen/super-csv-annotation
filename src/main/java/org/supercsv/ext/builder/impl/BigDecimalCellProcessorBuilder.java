package org.supercsv.ext.builder.impl;

import java.lang.annotation.Annotation;
import java.math.BigDecimal;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Currency;
import java.util.Locale;

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
        final String pattern = getPattern(converterAnno);
        final boolean lenient = getLenient(converterAnno);
        final Locale locale = getLocale(converterAnno);
        final Currency currency = getCurrency(converterAnno);
        final DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance(locale);
        
        final NumberFormat formatter = createNumberFormat(pattern, lenient, currency, symbols);
        
        final BigDecimal min = parseNumber(getMin(converterAnno), formatter);
        final BigDecimal max = parseNumber(getMax(converterAnno), formatter);
        
        CellProcessor cellProcessor = processor;
        if(formatter != null) {
            cellProcessor = (cellProcessor == null ?
                    new FormatLocaleNumber(formatter) : new FormatLocaleNumber(formatter, (StringCellProcessor) cellProcessor));
        }
        
        if(!ignoreValidationProcessor) {
            cellProcessor = prependRangeProcessor(min, max, formatter, cellProcessor);
        }
        
        return cellProcessor;
    }
    
    @Override
    public CellProcessor buildInputCellProcessor(final Class<BigDecimal> type, final Annotation[] annos,
            final CellProcessor processor) {
        
        final CsvNumberConverter converterAnno = getAnnotation(annos);
        final String pattern = getPattern(converterAnno);
        final boolean lenient = getLenient(converterAnno);
        final Locale locale = getLocale(converterAnno);
        final Currency currency = getCurrency(converterAnno);
        final DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance(locale);
        
        final NumberFormat formatter = createNumberFormat(pattern, lenient, currency, symbols);
        
        final BigDecimal min = parseNumber(getMin(converterAnno), formatter);
        final BigDecimal max = parseNumber(getMax(converterAnno), formatter);
        
        CellProcessor cellProcessor = processor;
        cellProcessor = prependRangeProcessor(min, max, formatter, cellProcessor);
        
        if(formatter != null) {
            cellProcessor = (cellProcessor == null ?
                    new ParseLocaleNumber<BigDecimal>(type, formatter, lenient) :
                        new ParseLocaleNumber<BigDecimal>(type, formatter, lenient, (StringCellProcessor)cellProcessor));                
        } else {
            cellProcessor = (cellProcessor == null ? 
                    new ParseBigDecimal() : new ParseBigDecimal(cellProcessor));
        }
        
        return cellProcessor;
        
    }
    
    protected BigDecimal parseNumber(final String value, final NumberFormat formatter) {
        if(value.isEmpty()) {
            return null;
        }
        
        if(formatter != null) {
            try {
                return (BigDecimal) formatter.parse(value);
            } catch(ParseException e) {
                throw new SuperCsvInvalidAnnotationException(
                        String.format(" value '%s' cannot parse to BigDecimal",
                                value, formatter), e);
            }
        }
        
        return new BigDecimal(value);
    }

    @Override
    public BigDecimal getParseValue(final Class<BigDecimal> type, final Annotation[] annos, final String defaultValue) {
        final CsvNumberConverter converterAnno = getAnnotation(annos);
        final String pattern = getPattern(converterAnno);
        final boolean lenient = getLenient(converterAnno);
        final Locale locale = getLocale(converterAnno);
        final Currency currency = getCurrency(converterAnno);
        final DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance(locale);
        
        final NumberFormat formatter = createNumberFormat(pattern, lenient, currency, symbols);
        
        return parseNumber(defaultValue, formatter);
    }
    
}

package org.supercsv.ext.builder.impl;

import java.lang.annotation.Annotation;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Currency;
import java.util.Locale;

import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.ift.DoubleCellProcessor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;
import org.supercsv.ext.annotation.CsvNumberConverter;
import org.supercsv.ext.cellprocessor.FormatLocaleNumber;
import org.supercsv.ext.cellprocessor.ParseFloat;
import org.supercsv.ext.cellprocessor.ParseLocaleNumber;
import org.supercsv.ext.exception.SuperCsvInvalidAnnotationException;

public class FloatCellProcessorBuilder extends AbstractNumberCellProcessorBuilder<Float> {
    
    @Override
    public CellProcessor buildOutputCellProcessor(final Class<Float> type, final Annotation[] annos,
            final CellProcessor processor, final boolean ignoreValidationProcessor) {
        
        final CsvNumberConverter converterAnno = getAnnotation(annos);
        final String pattern = getPattern(converterAnno);
        final boolean lenient = getLenient(converterAnno);
        final Locale locale = getLocale(converterAnno);
        final Currency currency = getCurrency(converterAnno);
        final DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance(locale);
        
        final NumberFormat formatter = createNumberFormat(pattern, lenient, currency, symbols);
        
        final Float min = parseNumber(getMin(converterAnno), formatter);
        final Float max = parseNumber(getMax(converterAnno), formatter);
        
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
    public CellProcessor buildInputCellProcessor(final Class<Float> type, final Annotation[] annos,
            final CellProcessor processor) {
        
        final CsvNumberConverter converterAnno = getAnnotation(annos);
        final String pattern = getPattern(converterAnno);
        final boolean lenient = getLenient(converterAnno);
        final Locale locale = getLocale(converterAnno);
        final Currency currency = getCurrency(converterAnno);
        final DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance(locale);
        
        final NumberFormat formatter = createNumberFormat(pattern, lenient, currency, symbols);
        
        final Float min = parseNumber(getMin(converterAnno), formatter);
        final Float max = parseNumber(getMax(converterAnno), formatter);
        
        CellProcessor cellProcessor = processor;
        cellProcessor = prependRangeProcessor(min, max, formatter, cellProcessor);
        
        if(formatter != null) {
            cellProcessor = (cellProcessor == null ?
                    new ParseLocaleNumber<Float>(type, formatter, lenient) :
                        new ParseLocaleNumber<Float>(type, formatter, lenient, (StringCellProcessor)cellProcessor));
                
        } else {
            cellProcessor = (cellProcessor == null ?
                    new ParseFloat() : new ParseFloat((DoubleCellProcessor) cellProcessor));
        }
        
        return cellProcessor;
        
    }
    
    protected Float parseNumber(final String value, final NumberFormat formatter) {
        if(value.isEmpty()) {
            return null;
        }
        
        if(formatter != null) {
            try {
                return formatter.parse(value).floatValue();
            } catch(ParseException e) {
                throw new SuperCsvInvalidAnnotationException(
                        String.format(" value '%s' cannot parse to Float",
                                value, formatter), e);
            }
        }
        
        return Float.valueOf(value);
    }

    @Override
    public Float getParseValue(final Class<Float> type, final Annotation[] annos, final String defaultValue) {
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

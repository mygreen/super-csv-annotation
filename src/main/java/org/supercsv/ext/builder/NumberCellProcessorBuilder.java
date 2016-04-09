/*
 * NumberCellProcessorBuilder.java
 * created in 2013/03/05
 *
 * (C) Copyright 2003-2013 GreenDay Project. All rights reserved.
 */
package org.supercsv.ext.builder;

import java.lang.annotation.Annotation;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Currency;
import java.util.Locale;

import org.supercsv.cellprocessor.ParseBigDecimal;
import org.supercsv.cellprocessor.ParseDouble;
import org.supercsv.cellprocessor.ParseInt;
import org.supercsv.cellprocessor.ParseLong;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.ift.DoubleCellProcessor;
import org.supercsv.cellprocessor.ift.LongCellProcessor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;
import org.supercsv.ext.annotation.CsvNumberConverter;
import org.supercsv.ext.cellprocessor.FormatLocaleNumber;
import org.supercsv.ext.cellprocessor.ParseBigInteger;
import org.supercsv.ext.cellprocessor.ParseByte;
import org.supercsv.ext.cellprocessor.ParseFloat;
import org.supercsv.ext.cellprocessor.ParseLocaleNumber;
import org.supercsv.ext.cellprocessor.ParseShort;
import org.supercsv.ext.cellprocessor.constraint.Max;
import org.supercsv.ext.cellprocessor.constraint.Min;
import org.supercsv.ext.cellprocessor.constraint.Range;
import org.supercsv.ext.exception.SuperCsvInvalidAnnotationException;
import org.supercsv.ext.util.Utils;


/**
 *
 *
 * @author T.TSUCHIE
 *
 */
public abstract class NumberCellProcessorBuilder<N extends Number & Comparable<N>> extends AbstractCellProcessorBuilder<N> {
    
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
    
    public static ByteCellProcessorBuilder newByte() {
        return new ByteCellProcessorBuilder();
    }
    
    public static ShortCellProcessorBuilder newShort() {
        return new ShortCellProcessorBuilder();
    }
    
    public static IntegerCellProcessorBuilder newInteger() {
        return new IntegerCellProcessorBuilder();
    }
    
    public static LongCellProcessorBuilder newLong() {
        return new LongCellProcessorBuilder();
    }
    
    public static FloatCellProcessorBuilder newFloat() {
        return new FloatCellProcessorBuilder();
    }
    
    public static DoubleCellProcessorBuilder newDouble() {
        return new DoubleCellProcessorBuilder();
    }
    
    public static BigDecimalCellProcessorBuilder newBigDecimal() {
        return new BigDecimalCellProcessorBuilder();
    }
    
    public static BigIntegerCellProcessorBuilder newBigInteger() {
        return new BigIntegerCellProcessorBuilder();
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
    
    public static class ByteCellProcessorBuilder extends NumberCellProcessorBuilder<Byte> {
        
        @Override
        public CellProcessor buildOutputCellProcessor(final Class<Byte> type, final Annotation[] annos,
                final CellProcessor processor, final boolean ignoreValidationProcessor) {
            
            final CsvNumberConverter converterAnno = getAnnotation(annos);
            final String pattern = getPattern(converterAnno);
            final boolean lenient = getLenient(converterAnno);
            final Locale locale = getLocale(converterAnno);
            final Currency currency = getCurrency(converterAnno);
            final DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance(locale);
            
            final NumberFormat formatter = createNumberFormat(pattern, lenient, currency, symbols);
            
            final Byte min = parseNumber(getMin(converterAnno), formatter);
            final Byte max = parseNumber(getMax(converterAnno), formatter);
            
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
        public CellProcessor buildInputCellProcessor(final Class<Byte> type, final Annotation[] annos,
                final CellProcessor processor) {
            
            final CsvNumberConverter converterAnno = getAnnotation(annos);
            final String pattern = getPattern(converterAnno);
            final boolean lenient = getLenient(converterAnno);
            final Locale locale = getLocale(converterAnno);
            final Currency currency = getCurrency(converterAnno);
            final DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance(locale);
            
            final NumberFormat formatter = createNumberFormat(pattern, lenient, currency, symbols);
            
            final Byte min = parseNumber(getMin(converterAnno), formatter);
            final Byte max = parseNumber(getMax(converterAnno), formatter);
            
            CellProcessor cellProcessor = processor;
            cellProcessor = prependRangeProcessor(min, max, formatter, cellProcessor);
            
            if(formatter != null) {
                cellProcessor = (cellProcessor == null ?
                        new ParseLocaleNumber<Byte>(type, formatter, lenient) :
                            new ParseLocaleNumber<Byte>(type, formatter, lenient, (StringCellProcessor)cellProcessor));
            } else {
                cellProcessor = (cellProcessor == null ?
                        new ParseByte() : new ParseByte((LongCellProcessor) cellProcessor));
            }
            
            return cellProcessor;
            
        }
        
        protected Byte parseNumber(final String value, final NumberFormat formatter) {
            if(value.isEmpty()) {
                return null;
            }
            
            if(formatter != null) {
                try {
                    return formatter.parse(value).byteValue();
                } catch(ParseException e) {
                    throw new SuperCsvInvalidAnnotationException(
                            String.format(" value '%s' cannot parse to Byte",
                                    value, formatter), e);
                }
            }
            
            return Byte.valueOf(value);
        }
        
        @Override
        public Byte getParseValue(final Class<Byte> type, final Annotation[] annos, final String defaultValue) {
            
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
    
    public static class ShortCellProcessorBuilder extends NumberCellProcessorBuilder<Short> {
        
        @Override
        public CellProcessor buildOutputCellProcessor(final Class<Short> type, final Annotation[] annos,
                final CellProcessor processor, final boolean ignoreValidationProcessor) {
            
            final CsvNumberConverter converterAnno = getAnnotation(annos);
            final String pattern = getPattern(converterAnno);
            final boolean lenient = getLenient(converterAnno);
            final Locale locale = getLocale(converterAnno);
            final Currency currency = getCurrency(converterAnno);
            final DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance(locale);
            
            final NumberFormat formatter = createNumberFormat(pattern, lenient, currency, symbols);
            
            final Short min = parseNumber(getMin(converterAnno), formatter);
            final Short max = parseNumber(getMax(converterAnno), formatter);
            
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
        public CellProcessor buildInputCellProcessor(final Class<Short> type, final Annotation[] annos,
                final CellProcessor processor) {
            
            final CsvNumberConverter converterAnno = getAnnotation(annos);
            final String pattern = getPattern(converterAnno);
            final boolean lenient = getLenient(converterAnno);
            final Locale locale = getLocale(converterAnno);
            final Currency currency = getCurrency(converterAnno);
            final DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance(locale);
            
            final NumberFormat formatter = createNumberFormat(pattern, lenient, currency, symbols);
            
            final Short min = parseNumber(getMin(converterAnno), formatter);
            final Short max = parseNumber(getMax(converterAnno), formatter);
            
            CellProcessor cellProcessor = processor;
            cellProcessor = prependRangeProcessor(min, max, formatter, cellProcessor);
            
            if(formatter != null) {
                cellProcessor = (cellProcessor == null ?
                        new ParseLocaleNumber<Short>(type, formatter, lenient) :
                            new ParseLocaleNumber<Short>(type, formatter, lenient, (StringCellProcessor)cellProcessor));
            } else {
                cellProcessor = (cellProcessor == null ?
                        new ParseShort() : new ParseShort((LongCellProcessor) cellProcessor));
            }
            
            return cellProcessor;
            
        }
        
        protected Short parseNumber(final String value, final NumberFormat formatter) {
            if(value.isEmpty()) {
                return null;
            }
            
            if(formatter != null) {
                try {
                    return formatter.parse(value).shortValue();
                } catch(ParseException e) {
                    throw new SuperCsvInvalidAnnotationException(
                            String.format(" value '%s' cannot parse to Short",
                                    value, formatter), e);
                }
            }
            
            return Short.valueOf(value);
        }

        @Override
        public Short getParseValue(final Class<Short> type, final Annotation[] annos, final String defaultValue) {
            
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
    
    public static class IntegerCellProcessorBuilder extends NumberCellProcessorBuilder<Integer> {
        
        @Override
        public CellProcessor buildOutputCellProcessor(final Class<Integer> type, final Annotation[] annos,
                final CellProcessor processor, final boolean ignoreValidationProcessor) {
            
            final CsvNumberConverter converterAnno = getAnnotation(annos);
            final String pattern = getPattern(converterAnno);
            final boolean lenient = getLenient(converterAnno);
            final Locale locale = getLocale(converterAnno);
            final Currency currency = getCurrency(converterAnno);
            final DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance(locale);
            
            final NumberFormat formatter = createNumberFormat(pattern, lenient, currency, symbols);
            
            final Integer min = parseNumber(getMin(converterAnno), formatter);
            final Integer max = parseNumber(getMax(converterAnno), formatter);
            
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
        public CellProcessor buildInputCellProcessor(final Class<Integer> type, final Annotation[] annos,
                final CellProcessor processor) {
            
            final CsvNumberConverter converterAnno = getAnnotation(annos);
            final String pattern = getPattern(converterAnno);
            final boolean lenient = getLenient(converterAnno);
            final Locale locale = getLocale(converterAnno);
            final Currency currency = getCurrency(converterAnno);
            final DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance(locale);
            
            final NumberFormat formatter = createNumberFormat(pattern, lenient, currency, symbols);
            
            final Integer min = parseNumber(getMin(converterAnno), formatter);
            final Integer max = parseNumber(getMax(converterAnno), formatter);
            
            CellProcessor cellProcessor = processor;
            cellProcessor = prependRangeProcessor(min, max, formatter, cellProcessor);
            
            if(formatter != null) {
                cellProcessor = (cellProcessor == null ?
                        new ParseLocaleNumber<Integer>(type, formatter, lenient) :
                            new ParseLocaleNumber<Integer>(type, formatter, lenient, (StringCellProcessor)cellProcessor));
            } else {
                cellProcessor = (cellProcessor == null ?
                        new ParseInt() : new ParseInt((LongCellProcessor) cellProcessor));
            }
            
            return cellProcessor;
        }
        
        protected Integer parseNumber(final String value, final NumberFormat formatter) {
            if(value.isEmpty()) {
                return null;
            }
            
            if(formatter != null) {
                try {
                    return formatter.parse(value).intValue();
                } catch(ParseException e) {
                    throw new SuperCsvInvalidAnnotationException(
                            String.format(" value '%s' cannot parse to Integer",
                                    value, formatter), e);
                }
            }
            
            return Integer.valueOf(value);
        }
        
        @Override
        public Integer getParseValue(final Class<Integer> type, final Annotation[] annos, final String defaultValue) {
            
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
    
    public static class LongCellProcessorBuilder extends NumberCellProcessorBuilder<Long> {
        
        @Override
        public CellProcessor buildOutputCellProcessor(final Class<Long> type, final Annotation[] annos,
                final CellProcessor processor, final boolean ignoreValidationProcessor) {
            
            final CsvNumberConverter converterAnno = getAnnotation(annos);
            final String pattern = getPattern(converterAnno);
            final boolean lenient = getLenient(converterAnno);
            final Locale locale = getLocale(converterAnno);
            final Currency currency = getCurrency(converterAnno);
            final DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance(locale);
            
            final NumberFormat formatter = createNumberFormat(pattern, lenient, currency, symbols);
            
            final Long min = parseNumber(getMin(converterAnno), formatter);
            final Long max = parseNumber(getMax(converterAnno), formatter);
            
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
        public CellProcessor buildInputCellProcessor(final Class<Long> type,final  Annotation[] annos,
                final CellProcessor processor) {
            
            final CsvNumberConverter converterAnno = getAnnotation(annos);
            final String pattern = getPattern(converterAnno);
            final boolean lenient = getLenient(converterAnno);
            final Locale locale = getLocale(converterAnno);
            final Currency currency = getCurrency(converterAnno);
            final DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance(locale);
            
            final NumberFormat formatter = createNumberFormat(pattern, lenient, currency, symbols);
            
            final Long min = parseNumber(getMin(converterAnno), formatter);
            final Long max = parseNumber(getMax(converterAnno), formatter);
            
            CellProcessor cellProcessor = processor;
            cellProcessor = prependRangeProcessor(min, max, formatter, cellProcessor);
            
            if(formatter != null) {
                cellProcessor = (cellProcessor == null ?
                        new ParseLocaleNumber<Long>(type, formatter, lenient) :
                            new ParseLocaleNumber<Long>(type, formatter, lenient, (StringCellProcessor)cellProcessor));
            } else {
                cellProcessor = (cellProcessor == null ?
                        new ParseLong() : new ParseLong((LongCellProcessor) cellProcessor));
            }
            
            return cellProcessor;
            
        }
        
        protected Long parseNumber(final String value, final NumberFormat formatter) {
            if(value.isEmpty()) {
                return null;
            }
            
            if(formatter != null) {
                try {
                    return formatter.parse(value).longValue();
                } catch(ParseException e) {
                    throw new SuperCsvInvalidAnnotationException(
                            String.format(" value '%s' cannot parse to Long",
                                    value, formatter), e);
                }
            }
            
            return Long.valueOf(value);
        }

        @Override
        public Long getParseValue(final Class<Long> type, final Annotation[] annos, final String defaultValue) {
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
    
    public static class FloatCellProcessorBuilder extends NumberCellProcessorBuilder<Float> {
        
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
    
    public static class DoubleCellProcessorBuilder extends NumberCellProcessorBuilder<Double> {
        
        @Override
        public CellProcessor buildOutputCellProcessor(final Class<Double> type, final Annotation[] annos, 
                final CellProcessor processor, final boolean ignoreValidationProcessor) {
            
            final CsvNumberConverter converterAnno = getAnnotation(annos);
            final String pattern = getPattern(converterAnno);
            final boolean lenient = getLenient(converterAnno);
            final Locale locale = getLocale(converterAnno);
            final Currency currency = getCurrency(converterAnno);
            final DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance(locale);
            
            final NumberFormat formatter = createNumberFormat(pattern, lenient, currency, symbols);
            
            final Double min = parseNumber(getMin(converterAnno), formatter);
            final Double max = parseNumber(getMax(converterAnno), formatter);
            
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
        public CellProcessor buildInputCellProcessor(final Class<Double> type, final Annotation[] annos,
                final CellProcessor processor) {
            
            final CsvNumberConverter converterAnno = getAnnotation(annos);
            final String pattern = getPattern(converterAnno);
            final boolean lenient = getLenient(converterAnno);
            final Locale locale = getLocale(converterAnno);
            final Currency currency = getCurrency(converterAnno);
            final DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance(locale);
            
            final NumberFormat formatter = createNumberFormat(pattern, lenient, currency, symbols);
            
            final Double min = parseNumber(getMin(converterAnno), formatter);
            final Double max = parseNumber(getMax(converterAnno), formatter);
            
            CellProcessor cellProcessor = processor;
            cellProcessor = prependRangeProcessor(min, max, formatter, cellProcessor);
            
            if(formatter != null) {
                cellProcessor = (cellProcessor == null ?
                        new ParseLocaleNumber<Double>(type, formatter, lenient) :
                            new ParseLocaleNumber<Double>(type, formatter, lenient, (StringCellProcessor)cellProcessor));
            } else {
                cellProcessor = (cellProcessor == null ?
                        new ParseDouble() : new ParseDouble((DoubleCellProcessor) cellProcessor));
            }
            
            return cellProcessor;
            
        }
        
        protected Double parseNumber(final String value, final NumberFormat formatter) {
            if(value.isEmpty()) {
                return null;
            }
            
            if(formatter != null) {
                try {
                    return formatter.parse(value).doubleValue();
                } catch(ParseException e) {
                    throw new SuperCsvInvalidAnnotationException(
                            String.format(" value '%s' cannot parse to Double",
                                    value, formatter), e);
                }
            }
            
            return Double.valueOf(value);
        }

        @Override
        public Double getParseValue(final Class<Double> type, final Annotation[] annos, final String defaultValue) {
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
    
    public static class BigDecimalCellProcessorBuilder extends NumberCellProcessorBuilder<BigDecimal> {
        
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
    
    public static class BigIntegerCellProcessorBuilder extends NumberCellProcessorBuilder<BigInteger> {
        
        @Override
        public CellProcessor buildOutputCellProcessor(final Class<BigInteger> type, final Annotation[] annos,
                final CellProcessor processor, final boolean ignoreValidationProcessor) {
            
            final CsvNumberConverter converterAnno = getAnnotation(annos);
            final String pattern = getPattern(converterAnno);
            final boolean lenient = getLenient(converterAnno);
            final Locale locale = getLocale(converterAnno);
            final Currency currency = getCurrency(converterAnno);
            final DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance(locale);
            
            final NumberFormat formatter = createNumberFormat(pattern, lenient, currency, symbols);
            
            final BigInteger min = parseNumber(getMin(converterAnno), formatter);
            final BigInteger max = parseNumber(getMax(converterAnno), formatter);
            
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
        public CellProcessor buildInputCellProcessor(final Class<BigInteger> type, final Annotation[] annos,
                final CellProcessor processor) {
            
            final CsvNumberConverter converterAnno = getAnnotation(annos);
            final String pattern = getPattern(converterAnno);
            final boolean lenient = getLenient(converterAnno);
            final Locale locale = getLocale(converterAnno);
            final Currency currency = getCurrency(converterAnno);
            final DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance(locale);
            
            final NumberFormat formatter = createNumberFormat(pattern, lenient, currency, symbols);
            
            final BigInteger min = parseNumber(getMin(converterAnno), formatter);
            final BigInteger max = parseNumber(getMax(converterAnno), formatter);
            
            CellProcessor cellProcessor = processor;
            cellProcessor = prependRangeProcessor(min, max, formatter, cellProcessor);
            
            if(formatter != null) {
                cellProcessor = (cellProcessor == null ?
                        new ParseBigInteger() : new ParseBigInteger(cellProcessor));
            } else {
                cellProcessor = (cellProcessor == null ?
                        new ParseLocaleNumber<BigInteger>(type, formatter, lenient) :
                            new ParseLocaleNumber<BigInteger>(type, formatter, lenient, (StringCellProcessor)cellProcessor));                
            }
            
            return cellProcessor;
        }
        
        protected BigInteger parseNumber(final String value, final NumberFormat formatter) {
            if(value.isEmpty()) {
                return null;
            }
            
            if(formatter != null) {
                try {
                    return ((BigDecimal) formatter.parse(value)).toBigIntegerExact();
                } catch(ParseException e) {
                    throw new SuperCsvInvalidAnnotationException(
                            String.format(" value '%s' cannot parse to BigInteger",
                                    value, formatter), e);
                }
            }
            
            return new BigInteger(value);
        }

        @Override
        public BigInteger getParseValue(final Class<BigInteger> type, final Annotation[] annos, final String defaultValue) {
            
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
    
}

package org.supercsv.ext.builder.impl;

import java.lang.annotation.Annotation;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Optional;

import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;
import org.supercsv.ext.annotation.CsvNumberConverter;
import org.supercsv.ext.cellprocessor.FormatLocaleNumber;
import org.supercsv.ext.cellprocessor.ParseBigInteger;
import org.supercsv.ext.cellprocessor.ParseLocaleNumber;
import org.supercsv.ext.exception.SuperCsvInvalidAnnotationException;

/**
 * {@link BigInteger}型の{@link CellProcessor}を組み立てるクラス。
 *
 * @version 1.2
 * @author T.TSUCHIE
 *
 */
public class BigIntegerCellProcessorBuilder extends AbstractNumberCellProcessorBuilder<BigInteger> {
    
    @Override
    public CellProcessor buildOutputCellProcessor(final Class<BigInteger> type, final Annotation[] annos,
            final CellProcessor processor, final boolean ignoreValidationProcessor) {
        
        final Optional<CsvNumberConverter> converterAnno = getNumberConverterAnnotation(annos);
        final Optional<NumberFormat> formatter = createNumberFormatter(converterAnno);
        
        final Optional<BigInteger> min = getMin(converterAnno).map(n -> parseValue(type, annos, n).get());
        final Optional<BigInteger> max = getMax(converterAnno).map(n -> parseValue(type, annos, n).get());
        
        CellProcessor cp = processor;
        if(formatter.isPresent()) {
            cp = (cp == null ?
                    new FormatLocaleNumber(formatter.get()) : new FormatLocaleNumber(formatter.get(), (StringCellProcessor) cp));
        }
        
        if(!ignoreValidationProcessor) {
            cp = prependRangeProcessor(type, annos, cp, min, max);
        }
        
        return cp;
        
    }
    
    @Override
    public CellProcessor buildInputCellProcessor(final Class<BigInteger> type, final Annotation[] annos,
            final CellProcessor processor) {
        
        final Optional<CsvNumberConverter> converterAnno = getNumberConverterAnnotation(annos);
        final Optional<NumberFormat> formatter = createNumberFormatter(converterAnno);
        final boolean lenient = getLenient(converterAnno);
        
        final Optional<BigInteger> min = getMin(converterAnno).map(n -> parseValue(type, annos, n).get());
        final Optional<BigInteger> max = getMax(converterAnno).map(n -> parseValue(type, annos, n).get());
        
        CellProcessor cp = processor;
        cp = prependRangeProcessor(type, annos, cp, min, max);
        
        if(formatter.isPresent()) {
            cp = (cp == null ?
                    new ParseLocaleNumber<BigInteger>(type, formatter.get(), lenient) :
                        new ParseLocaleNumber<BigInteger>(type, formatter.get(), lenient, cp));                
        } else {
            cp = (cp == null ?
                    new ParseBigInteger() : new ParseBigInteger(cp));
        }
        
        return cp;
    }
    
    @Override
    public Optional<BigInteger> parseValue(final Class<BigInteger> type, final Annotation[] annos, final String strValue) {
        
        if(strValue.isEmpty()) {
            return Optional.empty();
        }
        
        final Optional<CsvNumberConverter> converterAnno = getNumberConverterAnnotation(annos);
        final Optional<NumberFormat> formatter = createNumberFormatter(converterAnno);
        final Optional<String> pattern = getPattern(converterAnno);
        
        if(formatter.isPresent()) {
            try {
                BigInteger value = ((BigDecimal) formatter.get().parse(strValue)).toBigInteger();
                return Optional.of(value);
                
            } catch(ParseException e) {
                throw new SuperCsvInvalidAnnotationException(
                        String.format(" value '%s' cannot parse to Number with pattern '%s'", strValue, pattern),
                        e);
            }
        } else {
            return Optional.of(new BigInteger(strValue));
        }
    }
    
}

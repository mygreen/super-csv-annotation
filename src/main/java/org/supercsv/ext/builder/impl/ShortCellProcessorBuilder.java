package org.supercsv.ext.builder.impl;

import java.lang.annotation.Annotation;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Optional;

import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.ift.LongCellProcessor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;
import org.supercsv.ext.annotation.CsvColumn;
import org.supercsv.ext.annotation.CsvNumberConverter;
import org.supercsv.ext.cellprocessor.FormatLocaleNumber;
import org.supercsv.ext.cellprocessor.ParseLocaleNumber;
import org.supercsv.ext.cellprocessor.ParseShort;
import org.supercsv.ext.exception.SuperCsvInvalidAnnotationException;
import org.supercsv.ext.util.Utils;

/**
 * short/Short型の{@link CellProcessor}を組み立てるクラス。
 *
 * @version 1.2
 * @author T.TSUCHIE
 *
 */
public class ShortCellProcessorBuilder extends AbstractNumberCellProcessorBuilder<Short> {
    
    @Override
    protected CellProcessor buildInputCellProcessorWithConvertNullTo(final Class<Short> type, final Annotation[] annos,
            final CellProcessor cellProcessor, final CsvColumn csvColumnAnno) {
        
        // プリミティブ型の場合、オプションかつ初期値が与えられていない場合、0に変換する。
        if(type.isPrimitive() && csvColumnAnno.optional() && csvColumnAnno.inputDefaultValue().isEmpty()) {
            return prependConvertNullToProcessor(type, annos, cellProcessor, (short)0);
            
        } else if(!csvColumnAnno.inputDefaultValue().isEmpty()) {
            Optional<Short> value = parseValue(type, annos, csvColumnAnno.inputDefaultValue());
            return prependConvertNullToProcessor(type, annos, cellProcessor, value.get());
        }
        
        return cellProcessor;
    }
    
    @Override
    public CellProcessor buildOutputCellProcessor(final Class<Short> type, final Annotation[] annos,
            final CellProcessor processor, final boolean ignoreValidationProcessor) {
        
        final Optional<CsvNumberConverter> converterAnno = getNumberConverterAnnotation(annos);
        final Optional<NumberFormat> formatter = createNumberFormatter(converterAnno);
        
        final Optional<Short> min = getMin(converterAnno).map(s -> parseValue(type, annos, s).get());
        final Optional<Short> max = getMax(converterAnno).map(s -> parseValue(type, annos, s).get());
        
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
    public CellProcessor buildInputCellProcessor(final Class<Short> type, final Annotation[] annos,
            final CellProcessor processor) {
        
        final Optional<CsvNumberConverter> converterAnno = getNumberConverterAnnotation(annos);
        final Optional<NumberFormat> formatter = createNumberFormatter(converterAnno);
        final boolean lenient = getLenient(converterAnno);
        
        final Optional<Short> min = getMin(converterAnno).map(s -> parseValue(type, annos, s).get());
        final Optional<Short> max = getMax(converterAnno).map(s -> parseValue(type, annos, s).get());
        
        CellProcessor cp = processor;
        cp = prependRangeProcessor(type, annos, cp, min, max);
        
        if(formatter.isPresent()) {
            cp = (cp == null ?
                    new ParseLocaleNumber<Short>(type, formatter.get(), lenient) :
                        new ParseLocaleNumber<Short>(type, formatter.get(), lenient, cp));
        } else {
            cp = (cp == null ?
                    new ParseShort() : new ParseShort((LongCellProcessor)cp));
        }
        
        return cp;
    }
    
    @Override
    public Optional<Short> parseValue(final Class<Short> type, final Annotation[] annos, final String strValue) {
        
        if(Utils.isEmpty(strValue)) {
            return Optional.empty();
        }
        
        final Optional<CsvNumberConverter> converterAnno = getNumberConverterAnnotation(annos);
        final Optional<NumberFormat> formatter = createNumberFormatter(converterAnno);
        final Optional<String> pattern = getPattern(converterAnno);
        
        if(formatter.isPresent()) {
            try {
                return Optional.of(formatter.get().parse(strValue).shortValue());
            } catch(ParseException e) {
                throw new SuperCsvInvalidAnnotationException(
                        String.format(" value '%s' cannot parse to Number with pattern '%s'", strValue, pattern.get()),
                        e);
            }
        } else {
            return Optional.of(Short.valueOf(strValue));
        }
    }
    
}


package org.supercsv.ext.builder.impl;

import java.lang.annotation.Annotation;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Optional;

import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.ift.DoubleCellProcessor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;
import org.supercsv.ext.annotation.CsvColumn;
import org.supercsv.ext.annotation.CsvNumberConverter;
import org.supercsv.ext.cellprocessor.FormatLocaleNumber;
import org.supercsv.ext.cellprocessor.ParseFloat;
import org.supercsv.ext.cellprocessor.ParseLocaleNumber;
import org.supercsv.ext.exception.SuperCsvInvalidAnnotationException;

/**
 * float/Float型の{@link CellProcessor}を組み立てるクラス。
 *
 * @version 1.2
 * @author T.TSUCHIE
 *
 */
public class FloatCellProcessorBuilder extends AbstractNumberCellProcessorBuilder<Float> {
    
    @Override
    protected CellProcessor buildInputCellProcessorWithConvertNullTo(final Class<Float> type, final Annotation[] annos,
            final CellProcessor cellProcessor, final CsvColumn csvColumnAnno) {
        
        // プリミティブ型の場合、オプションかつ初期値が与えられていない場合、0に変換する。
        if(type.isPrimitive() && csvColumnAnno.optional() && csvColumnAnno.inputDefaultValue().isEmpty()) {
            return prependConvertNullToProcessor(type, annos, cellProcessor, 0.0f);
            
        } else if(!csvColumnAnno.inputDefaultValue().isEmpty()) {
            Optional<Float> value = parseValue(type, annos, csvColumnAnno.inputDefaultValue());
            return prependConvertNullToProcessor(type, annos, cellProcessor, value.get());
        }
        
        return cellProcessor;
    }
    
    @Override
    public CellProcessor buildOutputCellProcessor(final Class<Float> type, final Annotation[] annos,
            final CellProcessor processor, final boolean ignoreValidationProcessor) {
        
        final Optional<CsvNumberConverter> converterAnno = getNumberConverterAnnotation(annos);
        final Optional<NumberFormat> formatter = createNumberFormatter(converterAnno);
        
        final Optional<Float> min = getMin(converterAnno).map(n -> parseValue(type, annos, n).get());
        final Optional<Float> max = getMax(converterAnno).map(n -> parseValue(type, annos, n).get());
        
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
    public CellProcessor buildInputCellProcessor(final Class<Float> type, final Annotation[] annos,
            final CellProcessor processor) {
        
        final Optional<CsvNumberConverter> converterAnno = getNumberConverterAnnotation(annos);
        final Optional<NumberFormat> formatter = createNumberFormatter(converterAnno);
        final boolean lenient = getLenient(converterAnno);
        
        final Optional<Float> min = getMin(converterAnno).map(n -> parseValue(type, annos, n).get());
        final Optional<Float> max = getMax(converterAnno).map(n -> parseValue(type, annos, n).get());
        
        CellProcessor cp = processor;
        cp = prependRangeProcessor(type, annos, cp, min, max);
        
        if(formatter.isPresent()) {
            cp = (cp == null ?
                    new ParseLocaleNumber<Float>(type, formatter.get(), lenient) :
                        new ParseLocaleNumber<Float>(type, formatter.get(), lenient, cp));
                
        } else {
            cp = (cp == null ?
                    new ParseFloat() : new ParseFloat((DoubleCellProcessor) cp));
        }
        
        return cp;
        
    }
    
    @Override
    public Optional<Float> parseValue(final Class<Float> type, final Annotation[] annos, final String strValue) {
        
        if(strValue.isEmpty()) {
            return Optional.empty();
        }
        
        final Optional<CsvNumberConverter> converterAnno = getNumberConverterAnnotation(annos);
        final Optional<NumberFormat> formatter = createNumberFormatter(converterAnno);
        final Optional<String> pattern = getPattern(converterAnno);
        
        if(formatter.isPresent()) {
            try {
                return Optional.of(formatter.get().parse(strValue).floatValue());
            } catch(ParseException e) {
                throw new SuperCsvInvalidAnnotationException(
                        String.format(" value '%s' cannot parse to Number with pattern '%s'", strValue, pattern),
                        e);
            }
        } else {
            return Optional.of(Float.valueOf(strValue));
        }
    }
    
}

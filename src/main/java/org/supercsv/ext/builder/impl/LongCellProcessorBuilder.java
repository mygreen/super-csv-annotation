package org.supercsv.ext.builder.impl;

import java.lang.annotation.Annotation;
import java.text.NumberFormat;
import java.text.ParseException;

import org.supercsv.cellprocessor.ParseLong;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.ift.LongCellProcessor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;
import org.supercsv.ext.annotation.CsvColumn;
import org.supercsv.ext.annotation.CsvNumberConverter;
import org.supercsv.ext.cellprocessor.FormatLocaleNumber;
import org.supercsv.ext.cellprocessor.ParseLocaleNumber;
import org.supercsv.ext.exception.SuperCsvInvalidAnnotationException;

public class LongCellProcessorBuilder extends AbstractNumberCellProcessorBuilder<Long> {
    
    @Override
    protected CellProcessor buildInputCellProcessorWithConvertNullTo(final Class<Long> type, final Annotation[] annos,
            final CellProcessor cellProcessor, final CsvColumn csvColumnAnno) {
        
        // プリミティブ型の場合、オプションかつ初期値が与えられていない場合、0に変換する。
        if(type.isPrimitive() && csvColumnAnno.optional() && csvColumnAnno.inputDefaultValue().isEmpty()) {
            return prependConvertNullToProcessor(type, cellProcessor, 0l);
            
        } else if(!csvColumnAnno.inputDefaultValue().isEmpty()) {
            return prependConvertNullToProcessor(type, cellProcessor,
                    getParseValue(type, annos, csvColumnAnno.inputDefaultValue()));
        }
        
        return cellProcessor;
    }
    
    @Override
    public CellProcessor buildOutputCellProcessor(final Class<Long> type, final Annotation[] annos,
            final CellProcessor processor, final boolean ignoreValidationProcessor) {
        
        final CsvNumberConverter converterAnno = getAnnotation(annos);
        final NumberFormat formatter = createNumberFormatter(converterAnno);
        
        final Long min = getParseValue(type, annos, getMin(converterAnno));
        final Long max = getParseValue(type, annos, getMax(converterAnno));
        
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
        final NumberFormat formatter = createNumberFormatter(converterAnno);
        final boolean lenient = getLenient(converterAnno);
        
        final Long min = getParseValue(type, annos, getMin(converterAnno));
        final Long max = getParseValue(type, annos, getMax(converterAnno));
        
        CellProcessor cellProcessor = processor;
        cellProcessor = prependRangeProcessor(min, max, formatter, cellProcessor);
        
        if(formatter != null) {
            cellProcessor = (cellProcessor == null ?
                    new ParseLocaleNumber<Long>(type, formatter, lenient) :
                        new ParseLocaleNumber<Long>(type, formatter, lenient, cellProcessor));
        } else {
            cellProcessor = (cellProcessor == null ?
                    new ParseLong() : new ParseLong((LongCellProcessor) cellProcessor));
        }
        
        return cellProcessor;
        
    }
    
    @Override
    public Long getParseValue(final Class<Long> type, final Annotation[] annos, final String strValue) {
        
        if(strValue.isEmpty()) {
            return null;
        }
        
        final CsvNumberConverter converterAnno = getAnnotation(annos);
        final NumberFormat formatter = createNumberFormatter(converterAnno);
        final String pattern = getPattern(converterAnno);
        
        if(formatter != null) {
            try {
                return formatter.parse(strValue).longValue();
            } catch(ParseException e) {
                throw new SuperCsvInvalidAnnotationException(
                        String.format(" value '%s' cannot parse to Number with pattern '%s'", strValue, pattern),
                        e);
            }
        } else {
            return Long.valueOf(strValue);
        }
    }
    
}

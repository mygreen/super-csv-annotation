package org.supercsv.ext.builder.impl;

import java.lang.annotation.Annotation;
import java.sql.Time;
import java.text.DateFormat;

import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.ift.DateCellProcessor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;
import org.supercsv.ext.annotation.CsvDateConverter;
import org.supercsv.ext.cellprocessor.FormatLocaleDate;
import org.supercsv.ext.cellprocessor.ParseLocaleTime;

public class TimeCellProcessorBuilder extends AbstractDateCellProcessorBuilder<Time> {
    
    @Override
    public String getDefaultPattern() {
        return "HH:mm:ss";
    }
    
    @Override
    public CellProcessor buildOutputCellProcessor(final Class<Time> type, final Annotation[] annos,
            final CellProcessor processor, final boolean ignoreValidationProcessor) {
        
        final CsvDateConverter converterAnno = getAnnotation(annos);
        final DateFormat formatter = createDateFormatter(converterAnno);
        
        final Time min = getParseValue(type, annos, getMin(converterAnno));
        final Time max = getParseValue(type, annos, getMax(converterAnno));
        
        CellProcessor cp = processor;
        cp = (cp == null ? 
                new FormatLocaleDate(formatter) : 
                    new FormatLocaleDate(formatter, (StringCellProcessor) cp));
        
        if(!ignoreValidationProcessor) {
            cp = prependRangeProcessor(min, max, formatter, cp);
        }
        return cp;
    }
    
    @Override
    public CellProcessor buildInputCellProcessor(final Class<Time> type, final Annotation[] annos,
            final CellProcessor processor) {
        
        final CsvDateConverter converterAnno = getAnnotation(annos);
        final DateFormat formatter = createDateFormatter(converterAnno);
        
        final Time min = getParseValue(type, annos, getMin(converterAnno));
        final Time max = getParseValue(type, annos, getMax(converterAnno));
        
        CellProcessor cp = processor;
        cp = prependRangeProcessor(min, max, formatter, cp);
        cp = (cp == null ?
                new ParseLocaleTime(formatter) :
                    new ParseLocaleTime(formatter, (DateCellProcessor)cp));
        
        return cp;
        
    }
    
    @Override
    public Time getParseValue(final Class<Time> type, final Annotation[] annos, final String strValue) {
        
        return parseDate(annos, strValue)
                .map(d -> new Time(d.getTime()))
                .orElse(null);
    }
}

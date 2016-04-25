package org.supercsv.ext.builder.impl;

import java.lang.annotation.Annotation;
import java.sql.Timestamp;
import java.text.DateFormat;

import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.ift.DateCellProcessor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;
import org.supercsv.ext.annotation.CsvDateConverter;
import org.supercsv.ext.cellprocessor.FormatLocaleDate;
import org.supercsv.ext.cellprocessor.ParseLocaleTimestamp;

public class TimestampCellProcessorBuilder extends AbstractDateCellProcessorBuilder<Timestamp> {
    
    @Override
    public String getDefaultPattern() {
        return "yyyy-MM-dd HH:mm:ss.SSS";
    }
    
    @Override
    public CellProcessor buildOutputCellProcessor(final Class<Timestamp> type, final Annotation[] annos,
            final CellProcessor processor, final boolean ignoreValidationProcessor) {
        
        final CsvDateConverter converterAnno = getAnnotation(annos);
        final DateFormat formatter = createDateFormatter(converterAnno);
        
        final Timestamp min = getParseValue(type, annos, getMin(converterAnno));
        final Timestamp max = getParseValue(type, annos, getMax(converterAnno));
        
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
    public CellProcessor buildInputCellProcessor(final Class<Timestamp> type, final Annotation[] annos,
            final CellProcessor processor) {
        
        final CsvDateConverter converterAnno = getAnnotation(annos);
        final DateFormat formatter = createDateFormatter(converterAnno);
        
        final Timestamp min = getParseValue(type, annos, getMin(converterAnno));
        final Timestamp max = getParseValue(type, annos, getMax(converterAnno));
        
        CellProcessor cp = processor;
        cp = prependRangeProcessor(min, max, formatter, cp);
        
        cp = (cp == null ?
                new ParseLocaleTimestamp(formatter) :
                    new ParseLocaleTimestamp(formatter, (DateCellProcessor)cp));
        
        return cp;
        
    }
    
    @Override
    public Timestamp getParseValue(final Class<Timestamp> type, final Annotation[] annos, final String strValue) {
        
        return parseDate(annos, strValue)
                .map(d -> new Timestamp(d.getTime()))
                .orElse(null);
    }
    
}

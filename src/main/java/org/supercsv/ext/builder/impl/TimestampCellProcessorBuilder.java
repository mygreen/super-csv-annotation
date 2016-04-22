package org.supercsv.ext.builder.impl;

import java.lang.annotation.Annotation;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.util.Date;

import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.ift.DateCellProcessor;
import org.supercsv.ext.annotation.CsvDateConverter;
import org.supercsv.ext.cellprocessor.ParseLocaleTimestamp;

public class TimestampCellProcessorBuilder extends DateCellProcessorBuilder {
    
    @Override
    protected String getPattern(final CsvDateConverter converterAnno) {
        if(converterAnno == null || converterAnno.pattern().isEmpty()) {
            return "yyyy-MM-dd HH:mm:ss.SSS";
        }
        
        return converterAnno.pattern();
    }
    
    @Override
    public CellProcessor buildInputCellProcessor(final Class<Date> type, final Annotation[] annos,
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
    public Timestamp getParseValue(final Class<Date> type, final Annotation[] annos, final String strValue) {
        Date date = super.getParseValue(type, annos, strValue);
        return date == null ? null : new Timestamp(date.getTime());
    }
    
}

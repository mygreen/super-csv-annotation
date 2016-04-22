package org.supercsv.ext.builder.impl;

import java.lang.annotation.Annotation;
import java.sql.Time;
import java.text.DateFormat;
import java.util.Date;

import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.ift.DateCellProcessor;
import org.supercsv.ext.annotation.CsvDateConverter;
import org.supercsv.ext.cellprocessor.ParseLocaleTime;

public class TimeCellProcessorBuilder extends DateCellProcessorBuilder {
    
    @Override
    protected String getPattern(final CsvDateConverter converterAnno) {
        if(converterAnno == null || converterAnno.pattern().isEmpty()) {
            return "HH:mm";
        }
        
        return converterAnno.pattern();
    }
    
    @Override
    public CellProcessor buildInputCellProcessor(final Class<Date> type, final Annotation[] annos,
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
    public Time getParseValue(final Class<Date> type, final Annotation[] annos, final String strValue) {
        Date date = super.getParseValue(type, annos, strValue);
        return date == null ? null : new Time(date.getTime());
    }
}

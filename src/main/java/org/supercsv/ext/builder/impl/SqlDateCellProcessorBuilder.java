package org.supercsv.ext.builder.impl;

import java.lang.annotation.Annotation;
import java.sql.Date;
import java.text.DateFormat;

import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.ift.DateCellProcessor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;
import org.supercsv.ext.annotation.CsvDateConverter;
import org.supercsv.ext.cellprocessor.FormatLocaleDate;
import org.supercsv.ext.cellprocessor.ParseLocaleSqlDate;

public class SqlDateCellProcessorBuilder extends AbstractDateCellProcessorBuilder<Date> {
    
    @Override
    public String getDefaultPattern() {
        return "yyyy-MM-dd";
    }
    
    @Override
    public CellProcessor buildOutputCellProcessor(final Class<Date> type, final Annotation[] annos,
            final CellProcessor processor, final boolean ignoreValidationProcessor) {
        
        final CsvDateConverter converterAnno = getAnnotation(annos);
        final DateFormat formatter = createDateFormatter(converterAnno);
        
        final Date min = getParseValue(type, annos, getMin(converterAnno));
        final Date max = getParseValue(type, annos, getMax(converterAnno));
        
        CellProcessor cp = processor;
        cp = (cp == null ? new FormatLocaleDate(formatter) : new FormatLocaleDate(formatter, (StringCellProcessor) cp));
        
        if(!ignoreValidationProcessor) {
            cp = prependRangeProcessor(min, max, formatter, cp);
        }
        return cp;
    }
    
    @Override
    public CellProcessor buildInputCellProcessor(final Class<Date> type, final Annotation[] annos, 
            final CellProcessor processor) {
        
        final CsvDateConverter converterAnno = getAnnotation(annos);
        final DateFormat formatter = createDateFormatter(converterAnno);
        
        final Date min = getParseValue(type, annos, getMin(converterAnno));
        final Date max = getParseValue(type, annos, getMax(converterAnno));
        
        CellProcessor cp = processor;
        cp = prependRangeProcessor(min, max, formatter, cp);
        
        cp = (cp == null ? new ParseLocaleSqlDate(formatter) : new ParseLocaleSqlDate(formatter, (DateCellProcessor)cp));
        
        return cp;
        
    }
    
    @Override
    public Date getParseValue(final Class<Date> type, final Annotation[] annos, final String strValue) {
        
        return parseDate(annos, strValue)
                .map(d -> new Date(d.getTime()))
                .orElse(null);
    }
    
}

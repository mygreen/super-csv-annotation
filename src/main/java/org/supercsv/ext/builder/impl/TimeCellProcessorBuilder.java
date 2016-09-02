package org.supercsv.ext.builder.impl;

import java.lang.annotation.Annotation;
import java.sql.Time;
import java.text.DateFormat;
import java.util.Optional;

import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.ift.DateCellProcessor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;
import org.supercsv.ext.annotation.CsvDateConverter;
import org.supercsv.ext.builder.CellProcessorBuilder;
import org.supercsv.ext.cellprocessor.FormatLocaleDate;
import org.supercsv.ext.cellprocessor.ParseLocaleTime;

/**
 * {@link Time}型の{@link CellProcessorBuilder}クラス。
 * 
 * @version 1.2
 * @author T.TSUCHIE
 *
 */
public class TimeCellProcessorBuilder extends AbstractDateCellProcessorBuilder<Time> {
    
    @Override
    public String getDefaultPattern() {
        return "HH:mm:ss";
    }
    
    @Override
    public CellProcessor buildOutputCellProcessor(final Class<Time> type, final Annotation[] annos,
            final CellProcessor processor, final boolean ignoreValidationProcessor) {
        
        final Optional<CsvDateConverter> converterAnno = getDateConverterAnnotation(annos);
        final DateFormat formatter = createDateFormatter(converterAnno);
        
        final Optional<Time> min = getMin(converterAnno).map(s -> parseValue(type, annos, s).get());
        final Optional<Time> max = getMax(converterAnno).map(s -> parseValue(type, annos, s).get());
        
        CellProcessor cp = processor;
        cp = (cp == null ? new FormatLocaleDate(formatter) : new FormatLocaleDate(formatter, (StringCellProcessor) cp));
        
        if(!ignoreValidationProcessor) {
            cp = prependRangeProcessor(type, annos, cp, min, max);;
        }
        return cp;
    }
    
    @Override
    public CellProcessor buildInputCellProcessor(final Class<Time> type, final Annotation[] annos,
            final CellProcessor processor) {
        
        final Optional<CsvDateConverter> converterAnno = getDateConverterAnnotation(annos);
        final DateFormat formatter = createDateFormatter(converterAnno);
        
        final Optional<Time> min = getMin(converterAnno).map(s -> parseValue(type, annos, s).get());
        final Optional<Time> max = getMax(converterAnno).map(s -> parseValue(type, annos, s).get());
        
        CellProcessor cp = processor;
        cp = prependRangeProcessor(type, annos, cp, min, max);;
        cp = (cp == null ? new ParseLocaleTime(formatter) : new ParseLocaleTime(formatter, (DateCellProcessor)cp));
        
        return cp;
        
    }
    
    @Override
    public Optional<Time> parseValue(final Class<Time> type, final Annotation[] annos, final String strValue) {
        
        return parseDate(annos, strValue).map(d -> new Time(d.getTime()));
    }
}

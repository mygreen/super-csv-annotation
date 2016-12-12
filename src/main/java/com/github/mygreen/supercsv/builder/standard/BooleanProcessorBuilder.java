package com.github.mygreen.supercsv.builder.standard;

import java.util.Optional;

import com.github.mygreen.supercsv.annotation.format.CsvBooleanFormat;
import com.github.mygreen.supercsv.builder.AbstractProcessorBuilder;
import com.github.mygreen.supercsv.builder.Configuration;
import com.github.mygreen.supercsv.builder.FieldAccessor;
import com.github.mygreen.supercsv.cellprocessor.format.BooleanFormatter;
import com.github.mygreen.supercsv.cellprocessor.format.TextFormatter;


/**
 * boolean/Boolean型のビルダクラス。
 * 
 * @version 2.0
 * @author T.TSUCHIE
 *
 */
public class BooleanProcessorBuilder extends AbstractProcessorBuilder<Boolean> {
    
    @Override
    protected TextFormatter<Boolean> getDefaultFormatter(final FieldAccessor field, final Configuration config) {
        
        final Optional<CsvBooleanFormat> formatAnno = field.getAnnotation(CsvBooleanFormat.class);
        
        final BooleanFormatter formatter = formatAnno.map(anno -> new BooleanFormatter(anno.readForTrue(), anno.readForFalse(),
                anno.writeAsTrue(), anno.writeAsFalse(), anno.ignoreCase(), anno.failToFalse()))
                .orElseGet(() -> new BooleanFormatter());
        
        formatAnno.ifPresent(a -> formatter.setValidationMessage(a.message()));
        
        return formatter;
    }
    
}

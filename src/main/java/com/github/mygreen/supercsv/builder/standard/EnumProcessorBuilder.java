package com.github.mygreen.supercsv.builder.standard;

import java.util.Optional;

import com.github.mygreen.supercsv.annotation.format.CsvEnumFormat;
import com.github.mygreen.supercsv.builder.AbstractProcessorBuilder;
import com.github.mygreen.supercsv.builder.Configuration;
import com.github.mygreen.supercsv.builder.FieldAccessor;
import com.github.mygreen.supercsv.cellprocessor.format.EnumFormatter;


/**
 * 列挙型に対するビルダ
 * 
 * @version 2.0
 * @author T.TSUCHIE
 *
 */
public class EnumProcessorBuilder<T extends Enum<T>> extends AbstractProcessorBuilder<T> {
    
    public EnumProcessorBuilder() {
        super();
        
    }
    
    @Override
    protected void init() {
        super.init();
        
        // 制約のアノテーションの追加
    }
    
    @SuppressWarnings("unchecked")
    @Override
    protected EnumFormatter<T> getDefaultFormatter(final FieldAccessor field, final Configuration config) {
        
        final Optional<CsvEnumFormat> formatAnno = field.getAnnotation(CsvEnumFormat.class);
        final Optional<String> selector = formatAnno.map(a -> a.selector().isEmpty() ? null : a.selector());
        final boolean ignoreCase = formatAnno.map(a -> a.ignoreCase()).orElse(false);
        
        final EnumFormatter<T> formatter;
        if(selector.isPresent()) {
            formatter = new EnumFormatter<T>((Class<T>)field.getType(), ignoreCase, selector.get());
        } else {
            formatter = new EnumFormatter<T>((Class<T>)field.getType(), ignoreCase);
        }
        
        formatAnno.ifPresent(a -> formatter.setValidationMessage(a.message()));
        
        return formatter;
    }
    
    
}

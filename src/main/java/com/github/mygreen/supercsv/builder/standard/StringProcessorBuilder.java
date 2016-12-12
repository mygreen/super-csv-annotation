package com.github.mygreen.supercsv.builder.standard;

import com.github.mygreen.supercsv.annotation.constraint.CsvLengthExact;
import com.github.mygreen.supercsv.annotation.constraint.CsvWordForbid;
import com.github.mygreen.supercsv.annotation.constraint.CsvLengthBetween;
import com.github.mygreen.supercsv.annotation.constraint.CsvLengthMax;
import com.github.mygreen.supercsv.annotation.constraint.CsvLengthMin;
import com.github.mygreen.supercsv.annotation.constraint.CsvPattern;
import com.github.mygreen.supercsv.annotation.constraint.CsvWordRequire;
import com.github.mygreen.supercsv.builder.AbstractProcessorBuilder;
import com.github.mygreen.supercsv.builder.Configuration;
import com.github.mygreen.supercsv.builder.FieldAccessor;
import com.github.mygreen.supercsv.cellprocessor.constraint.LengthExactFactory;
import com.github.mygreen.supercsv.cellprocessor.constraint.WordForbidFactory;
import com.github.mygreen.supercsv.cellprocessor.constraint.LengthBetweenFactory;
import com.github.mygreen.supercsv.cellprocessor.constraint.LengthMaxFactory;
import com.github.mygreen.supercsv.cellprocessor.constraint.LengthMinFactory;
import com.github.mygreen.supercsv.cellprocessor.constraint.PatternFactory;
import com.github.mygreen.supercsv.cellprocessor.constraint.WordRequireFactory;
import com.github.mygreen.supercsv.cellprocessor.format.TextFormatter;


/**
 * 文字列型に対するCellProcessorを組み立てるクラス。
 * 
 * @version 2.0
 * @author T.TSUCHIE
 *
 */
public class StringProcessorBuilder extends AbstractProcessorBuilder<String> {
    
    public StringProcessorBuilder() {
        super();
        
    }
    
    @Override
    protected void init() {
        super.init();
        
        // 制約のアノテーションの追加
        registerForConstraint(CsvLengthBetween.class, new LengthBetweenFactory());
        registerForConstraint(CsvLengthMin.class, new LengthMinFactory());
        registerForConstraint(CsvLengthMax.class, new LengthMaxFactory());
        registerForConstraint(CsvLengthExact.class, new LengthExactFactory());
        registerForConstraint(CsvPattern.class, new PatternFactory());
        registerForConstraint(CsvWordRequire.class, new WordRequireFactory());
        registerForConstraint(CsvWordForbid.class, new WordForbidFactory());
        
    }
    
    @Override
    protected TextFormatter<String> getDefaultFormatter(final FieldAccessor field, final Configuration config) {
        
        return new TextFormatter<String>() {
            
            @Override
            public String parse(final String text) {
                return text;
            }
            
            @Override
            public String print(final String object) {
                return object;
            }
            
            @Override
            public void setValidationMessage(String validationMessage) {
                // not support
            }
            
        };
    }
    
}

package com.github.mygreen.supercsv.builder.spring;

import java.net.URL;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.mygreen.supercsv.builder.AbstractProcessorBuilder;
import com.github.mygreen.supercsv.builder.Configuration;
import com.github.mygreen.supercsv.builder.FieldAccessor;
import com.github.mygreen.supercsv.cellprocessor.format.TextFormatter;

@Component
public class UrlProcessorBuilder extends AbstractProcessorBuilder<URL> {
    
    @Autowired
    private UrlFormatter formatter;
    
    @Autowired
    private UserNameExistFactory userNameExistFactory;
    
    @Override
    protected void init() {
        super.init();
        
        registerForConstraint(CsvUserNameExist.class, userNameExistFactory);
    }
    
    @Override
    protected TextFormatter<URL> getDefaultFormatter(final FieldAccessor field, final Configuration config) {
        return formatter;
    }
    
}

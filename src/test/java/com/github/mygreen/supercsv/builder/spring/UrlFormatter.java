package com.github.mygreen.supercsv.builder.spring;

import java.net.MalformedURLException;
import java.net.URL;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.github.mygreen.supercsv.cellprocessor.format.AbstractTextFormatter;
import com.github.mygreen.supercsv.cellprocessor.format.TextParseException;


/**
 * {@link URL}のフォーマッタ。
 * <p>Springで管理する。ただし、prototypeスコープ。</p>
 * 
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
@Scope("prototype")
@Component
public class UrlFormatter extends AbstractTextFormatter<URL> {
    
    @Override
    public URL parse(final String text) {
        
        try {
            return new URL(text);
        } catch(MalformedURLException e) {
            throw new TextParseException(text, URL.class, e);
        }
        
    }
    
    @Override
    public String print(final URL object) {
        return object.toExternalForm();
    }
    
}

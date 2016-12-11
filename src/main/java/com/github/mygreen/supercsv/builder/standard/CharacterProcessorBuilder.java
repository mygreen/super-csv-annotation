package com.github.mygreen.supercsv.builder.standard;

import com.github.mygreen.supercsv.builder.AbstractProcessorBuilder;
import com.github.mygreen.supercsv.builder.Configuration;
import com.github.mygreen.supercsv.builder.FieldAccessor;
import com.github.mygreen.supercsv.cellprocessor.format.TextFormatter;
import com.github.mygreen.supercsv.cellprocessor.format.TextParseException;


/**
 * char/Character型に対するビルダ
 * 
 * @version 2.0
 * @author T.TSUCHIE
 *
 */
public class CharacterProcessorBuilder extends AbstractProcessorBuilder<Character> {
    
    @Override
    protected TextFormatter<Character> getDefaultFormatter(final FieldAccessor field, final Configuration config) {
        
        return new TextFormatter<Character>() {
            
            @Override
            public Character parse(final String text) {
                
                if(text.length() >= 1) {
                    return text.charAt(0);
                } else {
                    throw new TextParseException(text, field.getDeclaredClass(),
                            "Cannot be parsed as a char as it is a String longer than 1 character");
                }
            }
            
            @Override
            public String print(final Character object) {
                return object.toString();
            }
            
            @Override
            public void setValidationMessage(String validationMessage) {
                // not support
                
            }
            
        };
    }
    
}

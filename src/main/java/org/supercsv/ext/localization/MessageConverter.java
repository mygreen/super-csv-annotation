/*
 * MessageConverter.java
 * created in 2013/03/09
 *
 * (C) Copyright 2003-2013 GreenDay Project. All rights reserved.
 */
package org.supercsv.ext.localization;

import java.util.ArrayList;
import java.util.List;

/**
 *
 *
 * @author T.TSUCHIE
 *
 */
public class MessageConverter {
    
    private MessageResolver messageResolver;
    
    public MessageConverter() {
        messageResolver = new ResourceBundleMessageResolver();
        
    }
    
    public String convertMessage(final CsvMessage error) {
        
        final String msg = messageResolver.getMessage(error.getCode());
        return MapVariableInterpolator.interpolate(msg, error.getVariables());
        
    }
    
    public List<String> convertMessage(final List<CsvMessage> errors) {
        
        List<String> messages = new ArrayList<String>();
        for(CsvMessage error : errors) {
            messages.add(convertMessage(error));
        }
        return messages;
        
    }
    
    public MessageResolver getMessageResolver() {
        return messageResolver;
    }
    
    public void setMessageResolver(MessageResolver messageResolver) {
        this.messageResolver = messageResolver;
    }
    
}

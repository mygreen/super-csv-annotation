/*
 * PropertiesMessageResolver.java
 * created in 2013/03/09
 *
 * (C) Copyright 2003-2013 GreenDay Project. All rights reserved.
 */
package org.supercsv.ext.localization;

import java.util.Properties;


/**
 * The resolves messages based on the registered {@link Properties}.
 *
 * @author T.TSUCHIE
 *
 */
public class PropertiesMessageResolver implements MessageResolver {
    
    protected Properties properties = new Properties();
    
    public PropertiesMessageResolver() {
        this.properties = new Properties();
    }
    
    public PropertiesMessageResolver(final Properties properties) {
        
        if(properties == null) {
            throw new NullPointerException("properties should not be null.");
        }
        
        this.properties = properties;
    }
    
    /**
     * {@inheritDoc}
     */
    public String getMessage(final String code) {
        return properties.getProperty(code);
    }

    
    public Properties getProperties() {
        return properties;
    }
    
    public void setProperties(Properties properties) {
        this.properties = properties;
    }
    
}

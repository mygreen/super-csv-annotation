/*
 * ResourceBundleMessageResolver.java
 * created in 2013/03/09
 *
 * (C) Copyright 2003-2013 GreenDay Project. All rights reserved.
 */
package org.supercsv.ext.localization;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Default implementation that resolves messages based on the registered resource bundles.
 * 
 * @see net.sf.oval.localization.message.ResourceBundleMessageResolver
 * @version 1.1
 * @author T.TSUCHIE
 *
 */
public class ResourceBundleMessageResolver implements MessageResolver {
    
    public static final String USER_MESSAGE = "SuperCsvMessages";
    
    public static final String DEFAULT_MESSAGE = String.format("%s.%s", ResourceBundleMessageResolver.class.getPackage().getName(), USER_MESSAGE);
    
    private final Map<ResourceBundle, List<String>> messageBundleKeys = new HashMap<ResourceBundle, List<String>>(8);
    
    private final LinkedList<ResourceBundle> messageBundles = new LinkedList<ResourceBundle>();
    
    public static final ResourceBundleMessageResolver INSTANCE = new ResourceBundleMessageResolver();
    
    public ResourceBundleMessageResolver() {
        // add the message bundle for the pre-built constraints in the default locale
        addMessageBundle(ResourceBundle.getBundle(DEFAULT_MESSAGE));
        
        try {
            // load locale messages.
            addMessageBundle(ResourceBundle.getBundle(USER_MESSAGE));
        } catch(Throwable e) { }
    }
    
    /**
     * {@inheritDoc}
     */
    public String getMessage(final String code) {
        for (final ResourceBundle bundle : messageBundles) {
            final List<String> keys = messageBundleKeys.get(bundle);
            if (keys.contains(code)) return bundle.getString(code);
        }
        return null;
    }
    
    /**
     * Adds a message bundle
     * 
     * @param messageBundle
     * @return true if the bundle was registered and false if it was already registered
     */
    public final boolean addMessageBundle(final ResourceBundle messageBundle) {
        if (messageBundles.contains(messageBundle)) return false;
        
        messageBundles.addFirst(messageBundle);
        final List<String> keys = new ArrayList<String>();
        
        for (final Enumeration<String> keysEnum = messageBundle.getKeys(); keysEnum.hasMoreElements();) {
            keys.add(keysEnum.nextElement());
        }
        
        messageBundleKeys.put(messageBundle, keys);
        
        return true;
    }
    
    /**
     * Removes the message bundle
     * 
     * @param messageBundle
     * @return true if the bundle was registered and false if it wasn't registered
     */
    public boolean removeMessageBundle(final ResourceBundle messageBundle) {
        if (!messageBundles.contains(messageBundle)) return false;
        
        messageBundles.remove(messageBundle);
        return true;
    }
}

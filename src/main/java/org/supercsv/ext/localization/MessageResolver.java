/*
 * MessageResolver.java
 * created in 2013/03/09
 *
 * (C) Copyright 2003-2013 GreenDay Project. All rights reserved.
 */
package org.supercsv.ext.localization;


/**
 * Strategy interface for resolving messages.
 * 
 * @see 'OVal' net.sf.oval.localization.
 * @author T.TSUCHIE
 *
 */
public interface MessageResolver {
    
    /**
     * try to resolve message.
     * @param code
     * @return the resolved message. if the message wasn't found, return null.
     */
    public String getMessage(String code);
    
}

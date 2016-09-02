package org.supercsv.ext.localization;


/**
 * Strategy interface for resolving messages.
 * 
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

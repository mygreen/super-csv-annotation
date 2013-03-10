/*
 * CsvMessage.java
 * created in 2013/03/09
 *
 * (C) Copyright 2003-2013 GreenDay Project. All rights reserved.
 */
package org.supercsv.ext.localization;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


/**
 *
 *
 * @author T.TSUCHIE
 *
 */
public class CsvMessage implements Serializable {

    /** serialVersionUID */
    private static final long serialVersionUID = 1L;
    
    private final String code;
    
    private final Map<String, Object> variables = new HashMap<String, Object>();
    
    public CsvMessage(final String code) {
        this.code = code;
    }
    
    public CsvMessage(final String code, final Map<String, ?> vars) {
        this.code = code;
        variables.putAll(vars);
    }
    
    public CsvMessage add(final String varName, final Object varValue) {
        variables.put(varName, varValue);
        return this;
    }
    
    public CsvMessage addAll(final Map<String, ?> vars) {
        variables.putAll(vars);
        return this;
    }
    
    public String getCode() {
        return code;
    }
    
    public Map<String, ?> getVariables() {
        return variables;
    }
}

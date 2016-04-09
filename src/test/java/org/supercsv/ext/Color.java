/*
 * Color.java
 * created in 2013/03/08
 *
 * (C) Copyright 2003-2013 GreenDay Project. All rights reserved.
 */
package org.supercsv.ext;


/**
 *
 * 
 * @author T.TSUCHIE
 *
 */
public enum Color {
    
    RED("赤"), BLUE("青"), YELLOW("黄");
    
    final String aliasName;
    
    private Color(String aliasName) {
        this.aliasName = aliasName;
    }
    
    public String aliasName() {
        return aliasName;
    }
    
    
}

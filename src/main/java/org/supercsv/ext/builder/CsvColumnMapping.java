/*
 * CsvColumnMapping.java
 * created in 2013/03/05
 *
 * (C) Copyright 2003-2013 GreenDay Project. All rights reserved.
 */
package org.supercsv.ext.builder;

import org.supercsv.cellprocessor.ift.CellProcessor;


/**
 *
 *
 * @author T.TSUCHIE
 *
 */
public class CsvColumnMapping {
    
    private Class<?> columnType;
    
    private String columnName;
    
    private int position;
    
    private String label;
    
    private CellProcessor outputCellProcessor;
    
    private CellProcessor inputCellProcessor;
    
    
    public Class<?> getColumnType() {
        return columnType;
    }
    
    public void setColumnType(Class<?> columnType) {
        this.columnType = columnType;
    }
    
    public String getColumnName() {
        return columnName;
    }
    
    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }
    
    public int getPosition() {
        return position;
    }
    
    public void setPosition(int position) {
        this.position = position;
    }
    
    public String getLabel() {
        return label;
    }
    
    public void setLabel(String label) {
        this.label = label;
    }
    
    public CellProcessor getOutputCellProcessor() {
        return outputCellProcessor;
    }
    
    public void setOutputCellProcessor(CellProcessor outputCellProcessor) {
        this.outputCellProcessor = outputCellProcessor;
    }
    
    public CellProcessor getInputCellProcessor() {
        return inputCellProcessor;
    }
    
    public void setInputCellProcessor(CellProcessor inputCellProcessor) {
        this.inputCellProcessor = inputCellProcessor;
    }
    
}

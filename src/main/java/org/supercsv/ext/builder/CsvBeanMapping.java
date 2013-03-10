/*
 * CsvBeanMapping.java
 * created in 2013/03/05
 *
 * (C) Copyright 2003-2013 GreenDay Project. All rights reserved.
 */
package org.supercsv.ext.builder;

import java.util.ArrayList;
import java.util.List;

import org.supercsv.cellprocessor.ift.CellProcessor;


/**
 *
 *
 * @author T.TSUCHIE
 *
 */
public class CsvBeanMapping<T> {
    
    private final Class<T> type;
    
    public CsvBeanMapping(Class<T> type) {
        this.type = type;
    }
    
    private boolean header;
    
    private List<CsvColumnMapping> columns;
    
    public String[] getHeader() {
        
        List<String> list = new ArrayList<String>();
        for(CsvColumnMapping column : columns) {
            list.add(column.getLabel());
        }
        return list.toArray(new String[0]);
    }
    
    public String[] getNameMapping() {
        
        List<String> list = new ArrayList<String>();
        for(CsvColumnMapping column : columns) {
            list.add(column.getColumnName());
        }
        return list.toArray(new String[0]);
    }
    
    public CellProcessor[] getInputCellProcessor() {
        
        List<CellProcessor> list = new ArrayList<CellProcessor>();
        for(CsvColumnMapping column : columns) {
            list.add(column.getInputCellProcessor());
        }
        return list.toArray(new CellProcessor[0]);
    }
    
    public CellProcessor[] getOutputCellProcessor() {
        
        List<CellProcessor> list = new ArrayList<CellProcessor>();
        for(CsvColumnMapping column : columns) {
            list.add(column.getOutputCellProcessor());
        }
        return list.toArray(new CellProcessor[0]);
    }
    
    public Class<T> getType() {
        return type;
    }
    
    public boolean isHeader() {
        return header;
    }
    
    public void setHeader(boolean header) {
        this.header = header;
    }
    
    public List<CsvColumnMapping> getColumns() {
        return columns;
    }
    
    public void setColumns(List<CsvColumnMapping> columns) {
        this.columns = columns;
    }
    
}

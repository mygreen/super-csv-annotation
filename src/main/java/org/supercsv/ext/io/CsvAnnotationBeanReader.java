/*
 * CsvAnnotationBeanReader.java
 * created in 2013/03/06
 *
 * (C) Copyright 2003-2013 GreenDay Project. All rights reserved.
 */
package org.supercsv.ext.io;

import java.io.IOException;
import java.io.Reader;

import org.supercsv.ext.builder.CsvAnnotationBeanParser;
import org.supercsv.ext.builder.CsvBeanMapping;
import org.supercsv.prefs.CsvPreference;


/**
 *
 *
 * @author T.TSUCHIE
 *
 */
public class CsvAnnotationBeanReader<T> extends ValidatableCsvBeanReader {
    
    protected final CsvBeanMapping<T> beanMapping;
    
    protected final BeanMappingCache mappingCache;
    
    public CsvAnnotationBeanReader(final Class<T> clazz, final Reader reader, final CsvPreference preferences) {
        super(reader, preferences);
        this.beanMapping = createBeanMapping(clazz);
        this.mappingCache = new BeanMappingCache(beanMapping);
    }
    
    public CsvAnnotationBeanReader(final CsvBeanMapping<T> beanMapping, final Reader reader, final CsvPreference preferences) {
        super(reader, preferences);
        this.beanMapping = beanMapping;
        this.mappingCache = new BeanMappingCache(this.beanMapping);
        
    }
    
    protected CsvBeanMapping<T> createBeanMapping(Class<T> clazz) {
        return new CsvAnnotationBeanParser().parse(clazz);
    }
    
    public boolean hasHeader() {
        return getBeanMapping().isHeader();
    }
    
    //TODO: アノテーションで設定されている値を等しいかどうかチェックする。
    
    public String[] getHeader() throws IOException {
        return super.getHeader(hasHeader());
    }
    
    public T read() throws IOException {
        return read(getBeanMapping().getType(), getMappingCache().getNameMapping(),
                getMappingCache().getInputCellProcessors());
    }
    
    public CsvBeanMapping<T> getBeanMapping() {
        return beanMapping;
    }
    
    public BeanMappingCache getMappingCache() {
        return mappingCache;
    }
    
}

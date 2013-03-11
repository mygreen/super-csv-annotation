/*
 * CsvAnnotationBeanWriter.java
 * created in 2013/03/06
 *
 * (C) Copyright 2003-2013 GreenDay Project. All rights reserved.
 */
package org.supercsv.ext.io;

import java.io.IOException;
import java.io.Writer;

import org.supercsv.ext.builder.CsvAnnotationBeanParser;
import org.supercsv.ext.builder.CsvBeanMapping;
import org.supercsv.prefs.CsvPreference;


/**
 *
 *
 * @author T.TSUCHIE
 *
 */
public class CsvAnnotationBeanWriter<T> extends ValidatableCsvBeanWriter {
    
    protected final CsvBeanMapping<T> beanMapping;
    
    protected final BeanMappingCache mappingCache;
    
    public CsvAnnotationBeanWriter(final Class<T> clazz, Writer writer, final CsvPreference preferences) {
        this(clazz, writer, preferences, false);
    }
    
    public CsvAnnotationBeanWriter(final Class<T> clazz, Writer writer, final CsvPreference preferences, final boolean ignoreValidationProcessor) { 
        super(writer, preferences);
        this.beanMapping = createBeanMapping(clazz, ignoreValidationProcessor);
        this.mappingCache = new BeanMappingCache(beanMapping);
    }
    
    public CsvAnnotationBeanWriter(final CsvBeanMapping<T> beanMapping, final Writer writer, final CsvPreference preferences) {
        super(writer, preferences);
        this.beanMapping = beanMapping;
        this.mappingCache = new BeanMappingCache(this.beanMapping);
    }
    
    protected CsvBeanMapping<T> createBeanMapping(final Class<T> clazz, final boolean ignoreValidationProcessor) {
        return new CsvAnnotationBeanParser().parse(clazz, ignoreValidationProcessor);
    }
    
    public boolean hasHeader() {
        return getBeanMapping().isHeader();
    }
    
    public void writeHeader() throws IOException {
        writeHeader(getMappingCache().getHeader());
    }
    
    public void write(final Object source) throws IOException {
        write(source, getMappingCache().getNameMapping(), getMappingCache().getOutputCellProcessors());
    }
    
    public CsvBeanMapping<T> getBeanMapping() {
        return beanMapping;
    }
    
    public BeanMappingCache getMappingCache() {
        return mappingCache;
    }
    
}

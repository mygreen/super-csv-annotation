/*
 * CsvAnnotationBeanReader.java
 * created in 2013/03/06
 *
 * (C) Copyright 2003-2013 GreenDay Project. All rights reserved.
 */
package org.supercsv.ext.io;

import java.io.IOException;
import java.io.Reader;

import org.supercsv.exception.SuperCsvException;
import org.supercsv.ext.builder.CsvAnnotationBeanParser;
import org.supercsv.ext.builder.CsvBeanMapping;
import org.supercsv.ext.exception.SuperCsvNoMatchColumnSizeException;
import org.supercsv.ext.exception.SuperCsvNoMatchHeaderException;
import org.supercsv.prefs.CsvPreference;
import org.supercsv.util.CsvContext;


/**
 *
 *
 * @author T.TSUCHIE
 *
 */
public class CsvAnnotationBeanReader<T> extends ValidatableCsvBeanReader {
    
    protected CsvAnnotationBeanParser beanParser = new CsvAnnotationBeanParser();
    
    protected final CsvBeanMapping<T> beanMapping;
    
    protected final BeanMappingCache mappingCache;
    
    public CsvAnnotationBeanReader(final Class<T> clazz, final Reader reader, final CsvPreference preferences) {
        super(reader, preferences);
        this.beanMapping = beanParser.parse(clazz);
        this.mappingCache = new BeanMappingCache(beanMapping);
    }
    
    public CsvAnnotationBeanReader(final Class<T> clazz, final Reader reader, final CsvPreference preferences,
            final CsvAnnotationBeanParser beanParser) {
        super(reader, preferences);
        this.beanParser = beanParser;
        this.beanMapping = beanParser.parse(clazz);
        this.mappingCache = new BeanMappingCache(beanMapping);
    }
    
    public CsvAnnotationBeanReader(final CsvBeanMapping<T> beanMapping, final Reader reader, final CsvPreference preferences) {
        super(reader, preferences);
        this.beanMapping = beanMapping;
        this.mappingCache = new BeanMappingCache(this.beanMapping);
        
    }
    
    public boolean hasHeader() {
        return getBeanMapping().isHeader();
    }
    
    @Override
    public String[] getDefinedHeader() {
        return hasHeader() ? mappingCache.getHeader() : null;
    }
    
    public String[] getHeader() throws IOException {
        return super.getHeader(hasHeader());
    }
    
    /**
     * read header
     * @param checkedHeader 
     * @return
     * @throws IOException
     */
    public String[] readHeader(boolean checkedHeader) throws IOException {
        
        if(!getBeanMapping().isHeader()) {
            return null;
        }
        
        final String[] originalHeader = super.getHeader(hasHeader());
        if(checkedHeader) {
            try {
                validateHeader(originalHeader, getDefinedHeader());
            } catch(SuperCsvException e) {
                getCsvErrors().addAll(exceptionConverter.convertCsvError(e, getDefinedHeader()));
                throw e;
            }
            
        }
        
        return originalHeader;
    }
    
    protected void validateHeader(final String[] sourceHeader, final String[] definedHeader) {
        
        // check column size.
        if(sourceHeader.length != definedHeader.length) {
             final CsvContext context = new CsvContext(1, 1, 1);
             throw new SuperCsvNoMatchColumnSizeException(sourceHeader.length, definedHeader.length, context);
        }
        
        // check header value
        for(int i=0; i < sourceHeader.length; i++) {
            if(!sourceHeader[i].equals(definedHeader[i])) {
                final CsvContext context = new CsvContext(1, 1, i+1);
                throw new SuperCsvNoMatchHeaderException(sourceHeader, definedHeader, context);
            }
        }
        
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

package org.supercsv.ext.io;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;

import org.supercsv.ext.builder.CsvAnnotationBeanParser;
import org.supercsv.ext.builder.CsvBeanMapping;
import org.supercsv.prefs.CsvPreference;


/**
 *
 * @version 1.2
 * @author T.TSUCHIE
 *
 */
public class CsvAnnotationBeanWriter<T> extends ValidatableCsvBeanWriter {
    
    protected CsvAnnotationBeanParser beanParser = new CsvAnnotationBeanParser();
    
    protected final CsvBeanMapping<T> beanMapping;
    
    protected final BeanMappingCache mappingCache;
    
    public CsvAnnotationBeanWriter(final Class<T> clazz, final Writer writer, final CsvPreference preferences) {
        this(clazz, writer, preferences, false);
    }
    
    public CsvAnnotationBeanWriter(final Class<T> clazz, final Writer writer, final CsvPreference preferences,
            final boolean ignoreValidationProcessor, final CsvAnnotationBeanParser beanParser) { 
        super(writer, preferences);
        this.beanParser = beanParser;
        this.beanMapping = beanParser.parse(clazz, ignoreValidationProcessor);
        this.mappingCache = new BeanMappingCache(beanMapping);
    }
    
    public CsvAnnotationBeanWriter(final Class<T> clazz, Writer writer, final CsvPreference preferences, final boolean ignoreValidationProcessor) { 
        super(writer, preferences);
        this.beanMapping = beanParser.parse(clazz, ignoreValidationProcessor);
        this.mappingCache = new BeanMappingCache(beanMapping);
    }
    
    public CsvAnnotationBeanWriter(final CsvBeanMapping<T> beanMapping, final Writer writer, final CsvPreference preferences) {
        super(writer, preferences);
        this.beanMapping = beanMapping;
        this.mappingCache = new BeanMappingCache(this.beanMapping);
    }
    
    public boolean hasHeader() {
        return getBeanMapping().isHeader();
    }
    
    public void writeHeader() throws IOException {
        writeHeader(getMappingCache().getHeader());
    }
    
    public void write(final T source) throws IOException {
        write(source, getMappingCache().getNameMapping(), getMappingCache().getOutputCellProcessors());
    }
    
    public void writeAll(final Collection<T> collection) throws IOException {
        for(T item : collection) {
            write(item, getMappingCache().getNameMapping(), getMappingCache().getOutputCellProcessors());
        }
    }
    
    public CsvBeanMapping<T> getBeanMapping() {
        return beanMapping;
    }
    
    public BeanMappingCache getMappingCache() {
        return mappingCache;
    }
    
    @Override
    public String[] getDefinedHeader() {
        return hasHeader() ? mappingCache.getHeader() : null;
    }
    
}

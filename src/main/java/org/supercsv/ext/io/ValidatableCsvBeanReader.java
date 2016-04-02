/*
 * ValidatableBeanReader.java
 * created in 2013/03/09
 *
 * (C) Copyright 2003-2013 GreenDay Project. All rights reserved.
 */
package org.supercsv.ext.io;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.exception.SuperCsvCellProcessorException;
import org.supercsv.exception.SuperCsvException;
import org.supercsv.exception.SuperCsvReflectionException;
import org.supercsv.ext.exception.SuperCsvNoMatchColumnSizeException;
import org.supercsv.ext.exception.SuperCsvRowException;
import org.supercsv.ext.localization.CsvExceptionConveter;
import org.supercsv.ext.localization.CsvMessage;
import org.supercsv.io.AbstractCsvReader;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.io.ITokenizer;
import org.supercsv.prefs.CsvPreference;
import org.supercsv.util.BeanInterfaceProxy;
import org.supercsv.util.CsvContext;
import org.supercsv.util.MethodCache;
import org.supercsv.util.Util;


/**
 *
 *
 * @author T.TSUCHIE
 *
 */
public class ValidatableCsvBeanReader extends AbstractCsvReader implements ICsvBeanReader {
    
    /** temporary storage of processed columns to be mapped to the bean */
    private final List<Object> processedColumns = new ArrayList<Object>();
    
    /** cache of methods for mapping from columns to fields */
    private final MethodCache cache = new MethodCache();
    
    /** exception converter */
    protected final CsvExceptionConveter exceptionConverter = createExceptionConverter();
    
    /** columns errors */
    private final List<CsvMessage> errors = new ArrayList<CsvMessage>();
    
    protected CsvExceptionConveter createExceptionConverter() {
        return new CsvExceptionConveter();
    }
    
    public boolean hasError() {
        return !errors.isEmpty();
    }
    
    public boolean hasNotError() {
        return errors.isEmpty();
    }
    
    public List<CsvMessage> getCsvErrors() {
        return errors;
    }
    
    /**
     * {@inheritDoc}
     */
    public ValidatableCsvBeanReader(final Reader reader, final CsvPreference preferences) {
        super(reader, preferences);
    }
    
    /**
     * {@inheritDoc}
     */
    public ValidatableCsvBeanReader(final ITokenizer tokenizer, final CsvPreference preferences) {
        super(tokenizer, preferences);
    }
    
    /**
     * Instantiates the bean (or creates a proxy if it's an interface).
     * 
     * @param clazz
     *            the bean class to instantiate (a proxy will be created if an interface is supplied), using the default
     *            (no argument) constructor
     * @return the instantiated bean
     * @throws SuperCsvReflectionException
     *             if there was a reflection exception when instantiating the bean
     */
    protected static <T> T instantiateBean(final Class<T> clazz) {
        final T bean;
        if( clazz.isInterface() ) {
            bean = BeanInterfaceProxy.createProxy(clazz);
        } else {
            try {
                bean = clazz.newInstance();
            }
            catch(InstantiationException e) {
                throw new SuperCsvReflectionException(String.format(
                    "error instantiating bean, check that %s has a default no-args constructor", clazz.getName()), e);
            }
            catch(IllegalAccessException e) {
                throw new SuperCsvReflectionException("error instantiating bean", e);
            }
        }
        
        return bean;
    }
    
    /**
     * Invokes the setter on the bean with the supplied value.
     * 
     * @param bean
     *            the bean
     * @param setMethod
     *            the setter method for the field
     * @param fieldValue
     *            the field value to set
     * @throws SuperCsvException
     *             if there was an exception invoking the setter
     */
    private static void invokeSetter(final Object bean, final Method setMethod, final Object fieldValue) {
        try {
            setMethod.invoke(bean, fieldValue);
        }
        catch(final Exception e) {
            throw new SuperCsvReflectionException(String.format("error invoking method %s()", setMethod.getName()), e);
        }
    }
    
    /**
     * Instantiates the bean (or creates a proxy if it's an interface), and maps the processed columns to the fields of
     * the bean.
     * 
     * @param clazz
     *            the bean class to instantiate (a proxy will be created if an interface is supplied), using the default
     *            (no argument) constructor
     * @param nameMapping
     *            the name mappings
     * @return the populated bean
     * @throws SuperCsvReflectionException
     *             if there was a reflection exception while populating the bean
     */
    protected <T> T populateBean(final Class<T> clazz, final String[] nameMapping) {
        
        // instantiate the bean or proxy
        final T resultBean = instantiateBean(clazz);
        
        // map each column to its associated field on the bean
        for( int i = 0; i < nameMapping.length; i++ ) {
            
            final Object fieldValue = processedColumns.get(i);
            
            // don't call a set-method in the bean if there is no name mapping for the column or no result to store
            if( nameMapping[i] == null || fieldValue == null ) {
                continue;
            }
            
            // invoke the setter on the bean
            Method setMethod = cache.getSetMethod(resultBean, nameMapping[i], fieldValue.getClass());
            invokeSetter(resultBean, setMethod, fieldValue);
            
        }
        
        return resultBean;
    }
    
    @Override
    public <T> T read(T arg0, String... arg1) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T> T read(T arg0, String[] arg1, CellProcessor... arg2) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }
    
    /**
     * {@inheritDoc}
     */
    public <T> T read(final Class<T> clazz, final String... nameMapping) throws IOException {
        
        if( clazz == null ) {
            throw new NullPointerException("clazz should not be null");
        } else if( nameMapping == null ) {
            throw new NullPointerException("nameMapping should not be null");
        }
        
        if( readRow() ) {
            if( nameMapping.length != length() ) {
                throw new IllegalArgumentException(String.format("the nameMapping array and the number of columns read "
                    + "should be the same size (nameMapping length = %d, columns = %d)", nameMapping.length, length()));
            }
            processedColumns.clear();
            processedColumns.addAll(getColumns());
            return populateBean(clazz, nameMapping);
        }
        
        return null; // EOF
    }
    
    /**
     * {@inheritDoc}
     */
    public <T> T read(final Class<T> clazz, final String[] nameMapping, final CellProcessor... processors)
        throws IOException {
        
        if( clazz == null ) {
            throw new NullPointerException("clazz should not be null");
        } else if( nameMapping == null ) {
            throw new NullPointerException("nameMapping should not be null");
        } else if( processors == null ) {
            throw new NullPointerException("processors should not be null");
        }
        
        if( readRow() ) {
            try {
                // execute the processors then populate the bean
                executeCellProcessors(processedColumns, getColumns(), processors, getLineNumber(), getRowNumber());
                return populateBean(clazz, nameMapping);
                
            } catch(SuperCsvRowException e) {
//                errors.addAll(exceptionConverter.convertCsvError(e, getDefinedHeader()));
                throw e;
            } catch(SuperCsvException e) {
                errors.addAll(exceptionConverter.convertCsvError(e, getDefinedHeader()));
                throw e;
            }
        }
        
        return null; // EOF
    }
    
    /**
     * 
     * @see Util#executeCellProcessors(List, List, CellProcessor[], int, int)
     */
    protected void executeCellProcessors(final List<Object> destination, final List<?> source,
            final CellProcessor[] processors, final int lineNo, final int rowNo) {
        
//        Util.executeCellProcessors(processedColumns, getColumns(), processors, getLineNumber(), getRowNumber());
        
        if( destination == null ) {
            throw new NullPointerException("destination should not be null");
        } else if( source == null ) {
            throw new NullPointerException("source should not be null");
        } else if( processors == null ) {
            throw new NullPointerException("processors should not be null");
        }
        
        // the context used when cell processors report exceptions
        final CsvContext context = new CsvContext(lineNo, rowNo, 1);
        context.setRowSource(new ArrayList<Object>(source));
        
        if( source.size() != processors.length ) {
            throw new SuperCsvNoMatchColumnSizeException(source.size(), processors.length, context);
        }
        
        destination.clear();
        
        SuperCsvRowException columnError = new SuperCsvRowException(
                String.format("row (%d) has errors column", rowNo), context);
        for( int i = 0; i < source.size(); i++ ) {
            
            try {
                context.setColumnNumber(i + 1); // update context (columns start at 1)
                
                if( processors[i] == null ) {
                    destination.add(source.get(i)); // no processing required
                } else {
                    destination.add(processors[i].execute(source.get(i), context)); // execute the processor chain
                }
            } catch(SuperCsvCellProcessorException e) {
                columnError.addError(e);
                errors.addAll(exceptionConverter.convertCsvError(e, getDefinedHeader()));
            } catch(SuperCsvException e) {
                columnError.addError(e);
                errors.addAll(exceptionConverter.convertCsvError(e, getDefinedHeader()));
            }
        }
        
        if(columnError.isNotEmptyColumnErrors()) {
            throw columnError;
        }
        
    }
    
    /**
     * Get CSV Headers. if has not header, return null.
     * @return
     */
    public String[] getDefinedHeader() {
        return null;
    }
    
}

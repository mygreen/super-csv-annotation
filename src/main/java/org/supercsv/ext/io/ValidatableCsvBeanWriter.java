package org.supercsv.ext.io;

import java.io.IOException;
import java.io.Writer;
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
import org.supercsv.io.AbstractCsvWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;
import org.supercsv.util.CsvContext;
import org.supercsv.util.MethodCache;
import org.supercsv.util.Util;


/**
 *
 * @version 1.2
 * @author T.TSUCHIE
 *
 */
public class ValidatableCsvBeanWriter extends AbstractCsvWriter implements ICsvBeanWriter {
    
    /** temporary storage of bean values */
    protected final List<Object> beanValues = new ArrayList<Object>();
    
    /** temporary storage of processed columns to be written */
    protected final List<Object> processedColumns = new ArrayList<Object>();
    
    /** cache of methods for mapping from fields to columns */
    protected final MethodCache cache = new MethodCache();
    
    /** super csv exception converter */
    protected CsvExceptionConveter exceptionConverter = new CsvExceptionConveter();
    
    /** column errors */
    private final List<CsvMessage> errors = new ArrayList<CsvMessage>();
    
    public CsvExceptionConveter getExceptionConverter() {
        return exceptionConverter;
    }
    
    public void setExceptionConverter(CsvExceptionConveter exceptionConverter) {
        this.exceptionConverter = exceptionConverter;
    }
    
    public boolean hasError() {
        return !errors.isEmpty();
    }
    
    public boolean hasNotError() {
        return !hasError();
    }
    
    public List<CsvMessage> getCsvErrors() {
        return errors;
    }
    
    public ValidatableCsvBeanWriter(Writer writer, CsvPreference preference) {
        super(writer, preference);
    }
    
    /**
     * Extracts the bean values, using the supplied name mapping array.
     * 
     * @param source
     *            the bean
     * @param nameMapping
     *            the name mapping
     * @throws NullPointerException
     *             if source or nameMapping are null
     * @throws SuperCsvReflectionException
     *             if there was a reflection exception extracting the bean value
     */
    protected void extractBeanValues(final Object source, final String[] nameMapping) throws SuperCsvReflectionException {
        
        if( source == null ) {
            throw new NullPointerException("the bean to write should not be null");
        } else if( nameMapping == null ) {
            throw new NullPointerException(
                "the nameMapping array can't be null as it's used to map from fields to columns");
        }
        
        beanValues.clear();
        
        for( int i = 0; i < nameMapping.length; i++ ) {
            
            final String fieldName = nameMapping[i];
            
            if( fieldName == null ) {
                beanValues.add(null); // assume they always want a blank column
                
            } else {
                Method getMethod = cache.getGetMethod(source, fieldName);
                try {
                    beanValues.add(getMethod.invoke(source));
                }
                catch(final Exception e) {
                    throw new SuperCsvReflectionException(String.format("error extracting bean value for field %s",
                        fieldName), e);
                }
            }
            
        }
        
    }
    
    /**
     * {@inheritDoc}
     */
    public void write(final Object source, final String... nameMapping) throws IOException {
        
        // update the current row/line numbers
        super.incrementRowAndLineNo();
        
        // extract the bean values
        extractBeanValues(source, nameMapping);
        
        // write the list
        super.writeRow(beanValues);
    }
    
    /**
     * {@inheritDoc}
     */
    public void write(final Object source, final String[] nameMapping, final CellProcessor[] processors)
        throws IOException {
        
        // update the current row/line numbers
        super.incrementRowAndLineNo();
        
        // extract the bean values
        extractBeanValues(source, nameMapping);
        
        // execute the processors for each column
        try {
            executeCellProcessors(processedColumns, beanValues, processors, getLineNumber(), getRowNumber());
        } catch(SuperCsvRowException e) {
            throw e;
        } catch(SuperCsvException e) {
            errors.addAll(exceptionConverter.convertCsvError(e, getDefinedHeader()));
            throw e;
        }
        
        // write the list
        super.writeRow(processedColumns);
    }
    
    /**
     * 
     * @see Util#executeCellProcessors(List, List, CellProcessor[], int, int)
     */
    protected void executeCellProcessors(final List<Object> destination, final List<?> source,
            final CellProcessor[] processors, final int lineNo, final int rowNo) {
        
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

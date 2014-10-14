/*
 * CsvExceptionConveter.java
 * created in 2013/03/09
 *
 * (C) Copyright 2003-2013 GreenDay Project. All rights reserved.
 */
package org.supercsv.ext.localization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.exception.SuperCsvCellProcessorException;
import org.supercsv.exception.SuperCsvConstraintViolationException;
import org.supercsv.exception.SuperCsvException;
import org.supercsv.ext.cellprocessor.ift.ValidationCellProcessor;
import org.supercsv.ext.exception.SuperCsvNoMatchColumnSizeException;
import org.supercsv.ext.exception.SuperCsvNoMatchHeaderException;
import org.supercsv.ext.exception.SuperCsvRowException;
import org.supercsv.util.CsvContext;


/**
 * {@link SuperCsvException} convert to Message Object.
 * <p>Message object build from  {@link CsvContext} and {@link CellProcessor}.
 * 
 * 
 *
 * @author T.TSUCHIE
 *
 */
public class CsvExceptionConveter {
    
    public List<CsvMessage> convertCsvError(final SuperCsvException exception) {
        return convertCsvError(exception, null);
    }
    
    public List<CsvMessage> convertCsvError(final SuperCsvException exception, final String[] headers) {
        
        if(exception == null) {
            throw new NullPointerException("exception should not be null."); 
        }
        
        final List<CsvMessage> errors = new ArrayList<CsvMessage>();
        
        if(exception instanceof SuperCsvNoMatchColumnSizeException) {
            errors.addAll(convertCsvError((SuperCsvNoMatchColumnSizeException) exception, headers));
            
        } else if(exception instanceof SuperCsvNoMatchHeaderException) {
            errors.addAll(convertCsvError((SuperCsvNoMatchHeaderException) exception, headers));
            
        } else if(exception instanceof SuperCsvRowException) {
            errors.addAll(convertCsvError((SuperCsvRowException) exception, headers));
        } else if(exception instanceof SuperCsvCellProcessorException) {
            errors.addAll(convertCsvError((SuperCsvCellProcessorException) exception, headers));
            
        } else {
            CsvMessage error = new CsvMessage("csvError");
            error.addAll(createCsvContextVariable(exception.getCsvContext(), headers));
            errors.add(error);
        }
        
        return errors;
    }
    
    public List<CsvMessage> convertCsvError(final SuperCsvRowException exception, final String[] headers) {
        
        if(exception == null) {
            throw new NullPointerException("exception should not be null."); 
        }
        
        List<CsvMessage> messages = new ArrayList<CsvMessage>();
        for(SuperCsvException e : exception.getColumnErrors()) {
            messages.addAll(convertCsvError(e, headers));
        }
        return messages;
    }
    
    public List<CsvMessage> convertCsvError(final SuperCsvCellProcessorException exception, final String[] headers) {
        
        if(exception == null) {
            throw new NullPointerException("exception should not be null."); 
        }
        
        CellProcessor cellProcessor = exception.getProcessor();
        
        final CsvMessage message;
        if(cellProcessor instanceof ValidationCellProcessor) {
            ValidationCellProcessor p = (ValidationCellProcessor) cellProcessor;
            message = new CsvMessage(p.getMessageCode());
            message.addAll(p.getMessageVariable());
            
            final Object source = exception.getCsvContext().getRowSource().get(
                    exception.getCsvContext().getColumnNumber()-1);
            message.add("value", p.formatValue(source));
            
        } else {
            message = new CsvMessage(cellProcessor.getClass().getCanonicalName() + ".violated");
            
            final Object source = exception.getCsvContext().getRowSource().get(
                    exception.getCsvContext().getColumnNumber()-1);
            if(source != null) {
                message.add("value", source.toString());
            } else {
                message.add("value", "");
            }
        }
        
        message.addAll(createCsvContextVariable(exception.getCsvContext(), headers));
        
        List<CsvMessage> errors = new ArrayList<CsvMessage>();
        errors.add(message);
        return errors;
    }
    
    public List<CsvMessage> convertCsvError(final SuperCsvConstraintViolationException exception, final String[] headers) {
        
        if(exception == null) {
            throw new NullPointerException("exception should not be null."); 
        }
        
        return convertCsvError((SuperCsvCellProcessorException) exception, headers);
    }
    
    public List<CsvMessage> convertCsvError(final SuperCsvNoMatchColumnSizeException exception, final String[] headers) {
        
        if(exception == null) {
            throw new NullPointerException("exception should not be null."); 
        }
        
        final CsvMessage message = new CsvMessage("csvError.noMatchColumnSize");
        message.add("lineNumber", String.valueOf(exception.getCsvContext().getLineNumber()));
        message.add("rowNumber", String.valueOf(exception.getCsvContext().getRowNumber()));
        message.add("columnNumber", String.valueOf(exception.getCsvContext().getColumnNumber()));
        message.add("value", String.valueOf(exception.getActualColumnSize()))
            .add("expectedSize", String.valueOf(exception.getEpxpectedColumnSize()));
        
        final int colIndex = exception.getCsvContext().getColumnNumber();
        if(headers != null && headers.length >= colIndex) {
            message.add("columnLabel", headers[colIndex-1]);
        }
        
        final List<CsvMessage> errors = new ArrayList<CsvMessage>();
        errors.add(message);
        return errors;
    }
    
    public List<CsvMessage> convertCsvError(final SuperCsvNoMatchHeaderException exception, final String[] headers) {
        
        if(exception == null) {
            throw new NullPointerException("exception should not be null."); 
        }
        
        final CsvMessage message = new CsvMessage("csvError.notMatchHeader");
        message.add("lineNumber", String.valueOf(exception.getCsvContext().getLineNumber()));
        message.add("rowNumber", String.valueOf(exception.getCsvContext().getRowNumber()));
        message.add("columnNumber", String.valueOf(exception.getCsvContext().getColumnNumber()));
        message.add("value", exception.getActualHeadersWithJoin(","))
            .add("expectedValue", exception.getExpectedHeadersWithJoin(","));
        
        final int colIndex = exception.getCsvContext().getColumnNumber();
        if(headers != null && headers.length >= colIndex) {
            message.add("columnLabel", headers[colIndex-1]);
        }
        
        final List<CsvMessage> errors = new ArrayList<CsvMessage>();
        errors.add(message);
        return errors;
    }
    
    public Map<String, ?> createCsvContextVariable(final CsvContext context, final String[] headers) {
        
        if(context == null) {
            throw new NullPointerException("context should not be null."); 
        }
        
        final Map<String, Object> vars = new HashMap<String, Object>();
        
        vars.put("lineNumber", context.getLineNumber());
        vars.put("rowNumber", context.getRowNumber());
        vars.put("columnNumber", context.getColumnNumber());
        
        final int colIndex = context.getColumnNumber();
        if(headers != null && headers.length >= colIndex) {
            vars.put("columnLabel", headers[colIndex-1]);
        }
        
//        Object source = context.getRowSource().get(context.getColumnNumber()-1);
//        if(source != null) {
//            vars.put("value", source.toString());
//        } else {
//            vars.put("value", "");
//        }
        
        return vars;
    }
    
}

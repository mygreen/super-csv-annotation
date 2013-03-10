/*
 * ExceptionConveter.java
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
        List<CsvMessage> errrors = new ArrayList<CsvMessage>();
        
        if(exception instanceof SuperCsvNoMatchColumnSizeException) {
            errrors.addAll(convertCsvError((SuperCsvNoMatchColumnSizeException) exception));
            
        } else if(exception instanceof SuperCsvRowException) {
            errrors.addAll(convertCsvError((SuperCsvRowException) exception));
        } else if(exception instanceof SuperCsvCellProcessorException) {
            errrors.addAll(convertCsvError((SuperCsvCellProcessorException) exception));
            
        } else {
            CsvMessage error = new CsvMessage("csvError");
            error.addAll(createCsvContextVariable(exception.getCsvContext()));
            errrors.add(error);
        }
        
        return errrors;
    }
    
    public List<CsvMessage> convertCsvError(final SuperCsvRowException exception) {
        List<CsvMessage> messages = new ArrayList<CsvMessage>();
        for(SuperCsvException e : exception.getColumnErrors()) {
            messages.addAll(convertCsvError(e));
        }
        return messages;
    }
    
    public List<CsvMessage> convertCsvError(final SuperCsvCellProcessorException exception) {
        CellProcessor cellProcessor = exception.getProcessor();
        
        final CsvMessage message;
        if(cellProcessor instanceof ValidationCellProcessor) {
            ValidationCellProcessor p = (ValidationCellProcessor) cellProcessor;
            message = new CsvMessage(p.getMessageCode());
            message.addAll(p.getMessageVariable());
            
            final Object source = exception.getCsvContext().getRowSource().get(
                    exception.getCsvContext().getColumnNumber()-1);
            message.add("value", p.formateValue(source));
            
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
        
        message.addAll(createCsvContextVariable(exception.getCsvContext()));
        
        List<CsvMessage> errors = new ArrayList<CsvMessage>();
        errors.add(message);
        return errors;
    }
    
    public List<CsvMessage> convertCsvError(final SuperCsvConstraintViolationException exception) {
        return convertCsvError((SuperCsvCellProcessorException) exception);
    }
    
    public List<CsvMessage> convertCsvError(final SuperCsvNoMatchColumnSizeException exception) {
        CsvMessage message = new CsvMessage("csvError.noMatchColumnSize");
        message.add("lineNumber", String.valueOf(exception.getCsvContext().getLineNumber()));
        message.add("rowNumber", String.valueOf(exception.getCsvContext().getRowNumber()));
        message.add("columnNumber", String.valueOf(exception.getCsvContext().getColumnNumber()));
        message.add("value", String.valueOf(exception.getActualColumnSize()))
            .add("expectedSize", String.valueOf(exception.getEpxpectedColumnSize()));
        
        List<CsvMessage> errors = new ArrayList<CsvMessage>();
        errors.add(message);
        return errors;
    }
    
    public Map<String, ?> createCsvContextVariable(final CsvContext context) {
        
        Map<String, Object> vars = new HashMap<String, Object>();
        
        vars.put("lineNumber", context.getLineNumber());
        vars.put("rowNumber", context.getRowNumber());
        vars.put("columnNumber", context.getColumnNumber());
        
//        Object source = context.getRowSource().get(context.getColumnNumber()-1);
//        if(source != null) {
//            vars.put("value", source.toString());
//        } else {
//            vars.put("value", "");
//        }
        
        return vars;
    }
    
}

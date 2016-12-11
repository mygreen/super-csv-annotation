package com.github.mygreen.supercsv.validation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.supercsv.exception.SuperCsvCellProcessorException;
import org.supercsv.exception.SuperCsvException;
import org.supercsv.util.CsvContext;

import com.github.mygreen.supercsv.builder.BeanMapping;
import com.github.mygreen.supercsv.builder.ColumnMapping;
import com.github.mygreen.supercsv.exception.SuperCsvBindingException;
import com.github.mygreen.supercsv.exception.SuperCsvNoMatchColumnSizeException;
import com.github.mygreen.supercsv.exception.SuperCsvNoMatchHeaderException;
import com.github.mygreen.supercsv.exception.SuperCsvRowException;
import com.github.mygreen.supercsv.exception.SuperCsvValidationException;
import com.github.mygreen.supercsv.localization.MessageInterpolator;
import com.github.mygreen.supercsv.localization.MessageResolver;
import com.github.mygreen.supercsv.localization.ResourceBundleMessageResolver;
import com.github.mygreen.supercsv.util.Utils;

/**
 * {@link SuperCsvException}をメッセージに変換するクラス。
 * 
 * @version 2.0
 * @author T.TSUCHIE
 *
 */
public class CsvExceptionConverter {
    
    private MessageResolver messageResolver = new ResourceBundleMessageResolver();
    
    private MessageInterpolator messageInterpolator = new MessageInterpolator();
    
    private MessageCodeGenerator codeGenerator = new MessageCodeGenerator();
    
    public CsvExceptionConverter() {
        
    }
    
    /**
     * 例外をエラーオブジェクトに変換し、さらに、エラーオブジェジェクトをメッセージにフォーマットする。
     * @param exception 変換するSuperCsvの例外。
     * @param beanMapping Beanの定義情報
     * @return フォーマットされたメッセージ。
     * @throws NullPointerException {@literal exception or beanMapping is null.}
     */
    public List<String> convertAndFormat(final SuperCsvException exception, final BeanMapping<?> beanMapping) {
        
        return convert(exception, beanMapping).stream()
                .map(error -> error.format(messageResolver, messageInterpolator))
                .collect(Collectors.toList());
    }
    
    /**
     * 例外をエラーオブジェクトに変換する。
     * @param exception 変換するSuperCsvの例外。
     * @param beanMapping Beanの定義情報
     * @return 変換されたエラーオブジェクト。
     * @throws NullPointerException {@literal exception or beanMapping is null.}
     */
    public List<CsvError> convert(final SuperCsvException exception, final BeanMapping<?> beanMapping) {
        
        Objects.requireNonNull(beanMapping, "beanMapping should not be null.");
        Objects.requireNonNull(exception, "exception should not be null.");
        
        final List<CsvError> errors = new ArrayList<>();
        
        if(exception instanceof SuperCsvBindingException) {
            errors.addAll(convert((SuperCsvBindingException) exception, beanMapping));
            
        } if(exception instanceof SuperCsvRowException) {
            errors.addAll(convert((SuperCsvRowException) exception, beanMapping));
            
        } else if(exception instanceof SuperCsvValidationException) {
            errors.addAll(convert((SuperCsvValidationException)exception, beanMapping));
            
        } else if(exception instanceof SuperCsvCellProcessorException) {
            errors.addAll(convert((SuperCsvCellProcessorException)exception, beanMapping));
            
        } else if(exception instanceof SuperCsvNoMatchColumnSizeException) {
            errors.addAll(convert((SuperCsvNoMatchColumnSizeException)exception, beanMapping));
            
        } else if(exception instanceof SuperCsvNoMatchHeaderException) {
            errors.addAll(convert((SuperCsvNoMatchHeaderException)exception, beanMapping));
            
        } else {
            errors.addAll(convertDefault(exception, beanMapping));
            
        }
        
        return errors;
    }
    
    private List<CsvError> convert(final SuperCsvBindingException exception, final BeanMapping<?> beanMapping) {
        
        return exception.getBindingErrors().getAllErrors();
    }
    
    private List<CsvError> convert(final SuperCsvRowException exception, final BeanMapping<?> beanMapping) {
        
        return exception.getColumnErrors().stream()
            .flatMap(e -> convert(e, beanMapping).stream())
            .collect(Collectors.toList());
        
    }
    
    private List<CsvFieldError> convert(final SuperCsvValidationException exception, final BeanMapping<?> beanMapping) {
        
        final CsvContext context = exception.getCsvContext();
        final int columnNumber = context.getColumnNumber();
        
        final ColumnMapping columnMapping = beanMapping.getColumnMapping(columnNumber)
                .orElseThrow(() ->  new IllegalStateException(
                        String.format("not found column definition with umber=%d.", columnNumber)));
                
        final Map<String, Object> variables = new HashMap<>();
        variables.put("lineNumber", context.getLineNumber());
        variables.put("rowNumber", context.getRowNumber());
        variables.put("columnNumber", context.getColumnNumber());
        variables.put("label", columnMapping.getLabel());
        variables.put("validatedValue", exception.getRejectedValue());
        variables.putAll(exception.getMessageVariables());
        
        final String defaultMessage = exception.getValidationMessage();
        final String errorCode = exception.getProcessor().getClass().getSimpleName();
        final String objectName = beanMapping.getType().getSimpleName();
        final String fieldName = columnMapping.getName();
        
        // 型変換エラーのコードを生成する
        String[] typeMismatchCodes = {};
        if(exception.isParedError()) {
            // パース時の型変換エラーの場合
            typeMismatchCodes = codeGenerator.generateTypeMismatchCodes(
                    objectName, columnMapping.getName(), columnMapping.getField().getType());
            
        }
        
        // Bean名でエラーコードを生成する
        String[] errorCodes = codeGenerator.generateCodes(
                errorCode, objectName, columnMapping.getName(), columnMapping.getField().getType());
        
        errorCodes = Utils.concat(typeMismatchCodes, errorCodes);
        
        final CsvFieldError fieldError = new CsvFieldError.Builder(objectName, fieldName, errorCodes)
                .processingFailure(true)
                .variables(variables)
                .defaultMessage(defaultMessage)
                .build();
        
        return Arrays.asList(fieldError);
    }
    
    private List<CsvFieldError> convert(final SuperCsvCellProcessorException exception, final BeanMapping<?> beanMapping) {
        
        final CsvContext context = exception.getCsvContext();
        final int columnNumber = context.getColumnNumber();
        final Object rejectedValue = context.getRowSource().get(columnNumber-1);
        
        final ColumnMapping columnMapping = beanMapping.getColumnMapping(columnNumber)
                .orElseThrow(() ->  new IllegalStateException(
                        String.format("not found column definition with number=%d.", columnNumber)));
                
        final Map<String, Object> variables = new HashMap<>();
        variables.put("lineNumber", context.getLineNumber());
        variables.put("rowNumber", context.getRowNumber());
        variables.put("columnNumber", context.getColumnNumber());
        variables.put("label", columnMapping.getLabel());
        variables.put("validatedValue", rejectedValue);
        
        final String defaultMessage = exception.getMessage();
        
        final String errorCode = exception.getProcessor().getClass().getSimpleName();
        final String objectName = beanMapping.getType().getSimpleName();
        final String fieldName = columnMapping.getName();
        
        String[] errorCodes = codeGenerator.generateCodes(
                errorCode, objectName, columnMapping.getName(), columnMapping.getField().getType());
        
        final CsvFieldError fieldError = new CsvFieldError.Builder(objectName, fieldName, errorCodes)
                .processingFailure(true)
                .variables(variables)
                .defaultMessage(defaultMessage)
                .build();
        
        return Arrays.asList(fieldError);
        
    
    }
    
    private List<CsvError> convert(final SuperCsvNoMatchColumnSizeException exception, final BeanMapping<?> beanMapping) {
        
        final CsvContext context = exception.getCsvContext();
        
        final Map<String, Object> variables = new HashMap<>();
        variables.put("lineNumber", context.getLineNumber());
        variables.put("rowNumber", context.getRowNumber());
        variables.put("expectedSize", exception.getEpxpectedColumnSize());
        variables.put("actualSize", exception.getActualColumnSize());
        
        final String defaultMessage = exception.getMessage();
        
        final String errorCode = "csvError.noMatchColumnSize";
        final String objectName = beanMapping.getType().getSimpleName();
        final String[] errorCodes = codeGenerator.generateCodes(errorCode, objectName);
        
        final CsvError error = new CsvError.Builder(objectName, errorCodes)
                .variables(variables)
                .defaultMessage(defaultMessage)
                .build();
        
        return Arrays.asList(error);
        
    }
    
    private List<CsvError> convert(final SuperCsvNoMatchHeaderException exception, final BeanMapping<?> beanMapping) {
        
        final CsvContext context = exception.getCsvContext();
        
        final Map<String, Object> variables = new HashMap<>();
        variables.put("lineNumber", context.getLineNumber());
        variables.put("rowNumber", context.getRowNumber());
        variables.put("expectedHeaders", exception.getExpectedHeaders());
        variables.put("actualHeaders", exception.getActualHeaders());
        variables.put("joinedExpectedHeaders", String.join(", ", exception.getExpectedHeaders()));
        variables.put("joinedActualHeaders", String.join(", ", exception.getActualHeaders()));
        
        final String defaultMessage = exception.getMessage();
        
        final String errorCode = "csvError.noMatchHeader";
        final String objectName = beanMapping.getType().getSimpleName();
        final String[] errorCodes = codeGenerator.generateCodes(errorCode, objectName);
        
        final CsvError error = new CsvError.Builder(objectName, errorCodes)
                .variables(variables)
                .defaultMessage(defaultMessage)
                .build();
        
        return Arrays.asList(error);
        
    }
    
    private List<CsvError> convertDefault(final SuperCsvException exception, final BeanMapping<?> beanMapping) {
        
        final CsvContext context = exception.getCsvContext();
        
        final Map<String, Object> variables = new HashMap<>();
        variables.put("lineNumber", context.getLineNumber());
        variables.put("rowNumber", context.getRowNumber());
        variables.put("columnNumber", context.getColumnNumber());
        
        final String defaultMessage = exception.getMessage();
        
        final String errorCode = "csvError";
        final String objectName = beanMapping.getType().getSimpleName();
        final String[] errorCodes = codeGenerator.generateCodes(errorCode, objectName);
        
        final CsvError error = new CsvError.Builder(objectName, errorCodes)
                .variables(variables)
                .defaultMessage(defaultMessage)
                .build();
        
        return Arrays.asList(error);
        
    }
    
    public MessageResolver getMessageResolver() {
        return messageResolver;
    }
    
    public void setMessageResolver(MessageResolver messageResolver) {
        this.messageResolver = messageResolver;
    }
    
    public MessageInterpolator getMessageInterpolator() {
        return messageInterpolator;
    }
    
    public void setMessageInterpolator(MessageInterpolator messageInterpolator) {
        this.messageInterpolator = messageInterpolator;
    }
    
    public MessageCodeGenerator getCodeGenerator() {
        return codeGenerator;
    }
    
    public void setCodeGenerator(MessageCodeGenerator codeGenerator) {
        this.codeGenerator = codeGenerator;
    }
    
}

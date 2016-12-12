package com.github.mygreen.supercsv.cellprocessor.constraint;

import org.supercsv.cellprocessor.ift.StringCellProcessor;
import org.supercsv.util.CsvContext;

import com.github.mygreen.supercsv.cellprocessor.ValidationCellProcessor;


/**
 * 文字列に対して、正規表現に一致するか検証するCellProcessor.
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class Pattern extends ValidationCellProcessor implements StringCellProcessor {
    
    private final java.util.regex.Pattern pattern;
    
    private final String description;
    
    public Pattern(final java.util.regex.Pattern regexPattern, final String regexDescriptoin) {
        super();
        checkPreconditions(regexPattern);
        this.pattern = regexPattern;
        this.description = regexDescriptoin;
    }
    
    public Pattern(final java.util.regex.Pattern regexPattern, final String regexDescriptoin, final StringCellProcessor next) {
        super(next);
        checkPreconditions(regexPattern);
        this.pattern = regexPattern;
        this.description = regexDescriptoin;
    }
    
    private static void checkPreconditions(final java.util.regex.Pattern regexPattern) {
        if(regexPattern == null) {
            throw new NullPointerException("regexPattern should not be null");
        }
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public Object execute(final Object value, final CsvContext context) {
        
        if(value == null) {
            return next.execute(value, context);
        }
        
        final boolean matches = pattern.matcher((String) value).matches();
        if(!matches) {
            throw createValidationException(context)
                .messageFormat("'%s' does not match the regular expression '%s'", value, getRegex())
                .rejectedValue(value)
                .messageVariables("regex", getRegex())
                .messageVariables("flags", getFlags())
                .messageVariables("description", getDescription())
                .build();
        }
        
        return next.execute(value, context);
    }
    
    /**
     * 
     * @return 設定せれた正規表現
     */
    public String getRegex() {
        return pattern.pattern();
    }
    
    /**
     * 
     * @return 正規表現のフラグ
     */
    public int getFlags() {
        return pattern.flags();
    }
    
    /**
     * 
     * @return 正規表現の説明
     */
    public String getDescription() {
        return description;
    }
    
}

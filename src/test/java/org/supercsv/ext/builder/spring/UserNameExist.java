package org.supercsv.ext.builder.spring;

import java.util.HashMap;
import java.util.Map;

import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;
import org.supercsv.exception.SuperCsvConstraintViolationException;
import org.supercsv.ext.cellprocessor.ift.ValidationCellProcessor;
import org.supercsv.util.CsvContext;


/**
 *
 *
 * @author T.TSUCHIE
 *
 */
public class UserNameExist extends CellProcessorAdaptor 
        implements StringCellProcessor, ValidationCellProcessor {
    
    private UserService userService;
    
    public UserNameExist(UserService userService) {
        this.userService = userService;
    }
    
    public UserNameExist(UserService userService, final CellProcessor next) {
        super(next);
        this.userService = userService;
    }
    
    @Override
    public Map<String, ?> getMessageVariable() {
        Map<String, ?> vars = new HashMap<String, Object>();
        return vars;
    }
    
    @Override
    public String formatValue(final Object value) {
        if(value == null) {
            return "";
        }
        return value.toString();
    }
    
    @Override
    public Object execute(final Object value, final CsvContext context) {
        
        validateInputNotNull(value, context);
        
        final String stringValue = value.toString();
        if( !userService.existByUserName(stringValue) ) {
            throw new SuperCsvConstraintViolationException(String.format("not exist userName '%s'", stringValue),
                    context, this);
        }
        
        return next.execute(stringValue, context);
    }
}

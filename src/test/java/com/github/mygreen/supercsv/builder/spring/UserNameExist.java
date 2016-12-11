package com.github.mygreen.supercsv.builder.spring;

import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;
import org.supercsv.util.CsvContext;

import com.github.mygreen.supercsv.cellprocessor.ValidationCellProcessor;

/**
 * ユーザ名の存在チェックを行う制約のCellProcessor
 * 
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class UserNameExist extends ValidationCellProcessor implements StringCellProcessor {
    
    private final UserService userService;
    
    public UserNameExist(final UserService userService) {
        checkPreconditions(userService);
        this.userService = userService;
    }
    
    public UserNameExist(final UserService userService, final CellProcessor next) {
        super(next);
        checkPreconditions(userService);
        this.userService = userService;
    }
    
    private static void checkPreconditions(final UserService userService) {
        if(userService == null) {
            throw new NullPointerException("userService should not be null");
        }
        
    }
    
    @Override
    public <T> T execute(final Object value, final CsvContext context) {
        
        if(value == null) {
            return next.execute(value, context);
        }
        
        final String result = value.toString();
        if(!userService.existByUserName(result)) {
            throw createValidationException(context)
                .messageFormat("%s dose not found user name.", result)
                .rejectedValue(result)
                .build();
        }
        
        return next.execute(value, context);
    }
    
}

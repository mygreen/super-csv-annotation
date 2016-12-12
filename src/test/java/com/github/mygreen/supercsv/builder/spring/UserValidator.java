package com.github.mygreen.supercsv.builder.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.mygreen.supercsv.validation.CsvBindingErrors;
import com.github.mygreen.supercsv.validation.CsvValidator;
import com.github.mygreen.supercsv.validation.ValidationContext;

/**
 * {@link UserCsv}に対するValidator
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
@Component
public class UserValidator implements CsvValidator<UserCsv> {
    
    @Autowired
    private UserService userService;
    
    @Override
    public void validate(final UserCsv record, final CsvBindingErrors bindingErrors,
            final ValidationContext<UserCsv> validationContext) {
        
        // TODO: 実装する
        
    }
    
}

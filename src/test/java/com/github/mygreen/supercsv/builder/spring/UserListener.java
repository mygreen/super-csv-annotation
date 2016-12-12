package com.github.mygreen.supercsv.builder.spring;

import java.net.URL;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.mygreen.supercsv.annotation.CsvPostRead;
import com.github.mygreen.supercsv.annotation.CsvPreWrite;
import com.github.mygreen.supercsv.validation.CsvBindingErrors;
import com.github.mygreen.supercsv.validation.CsvField;
import com.github.mygreen.supercsv.validation.CsvFieldValidator;
import com.github.mygreen.supercsv.validation.ValidationContext;

/**
 * {@link UserCsv}に対するリスナクラス
 * 
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
@Component
public class UserListener {
    
    @Autowired
    private UserService userSerivce;
    
    @CsvPreWrite
    @CsvPostRead
    public void validate(final UserCsv record, final ValidationContext<UserCsv> validationContext, final CsvBindingErrors bindingErrors) {
        
        final CsvField<URL> homepageField = new CsvField<>(validationContext, record, "homepage");
        homepageField.add(new CsvFieldValidator<URL>() {
            
            @Override
            public void validate(final CsvBindingErrors bindingErrors, final CsvField<URL> field) {
                if(field.isEmpty()) {
                    return;
                }
                
                if(!userSerivce.isValidProtocol(field.getValue())) {
                    
                    Map<String, Object> vars = createMessageVariables(field);
                    vars.put("protocol", field.getValue().getProtocol());
                    
                    bindingErrors.rejectValue(field.getName(), field.getType(), "fieldError.homepage.supportedProtocol", vars);
                }
            }
        })
        .validate(bindingErrors);
        
    }
    
}

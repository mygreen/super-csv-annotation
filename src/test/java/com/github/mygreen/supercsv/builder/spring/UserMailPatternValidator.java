package com.github.mygreen.supercsv.builder.spring;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;


/**
 * Springで管理する
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class UserMailPatternValidator implements ConstraintValidator<UserMailPattern, String> {
    
    // SpringBeanをインジェクションします。
    @Autowired
    private UserService userService;
    
    @Override
    public void initialize(final UserMailPattern constraintAnnotation) {
        
    }
    
    @Override
    public boolean isValid(final String value, final ConstraintValidatorContext context) {
        
        // nullの場合は対象外
        if(value == null) {
            return true;
        }
        
        return userService.isMailPattern(value);
    }
    
}

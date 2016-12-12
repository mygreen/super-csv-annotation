package com.github.mygreen.supercsv.builder;

import javax.validation.Validator;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import com.github.mygreen.supercsv.builder.SpringBeanFactory;
import com.github.mygreen.supercsv.localization.MessageInterpolator;
import com.github.mygreen.supercsv.localization.SpringMessageResolver;
import com.github.mygreen.supercsv.validation.beanvalidation.CsvBeanValidator;
import com.github.mygreen.supercsv.validation.beanvalidation.MessageInterpolatorAdapter;

/**
 * Springのテスト用のコンフィグ
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
@Configuration
@ComponentScan(basePackages="com.github.mygreen.supercsv.builder.spring")
public class SpringTestConfig {
    
    @Bean
    @Description("Springのコンテナを経由するCSV用のBeanFactoryの定義")
    public SpringBeanFactory springBeanFactory() {
        return new SpringBeanFactory();
    }
    
    @Bean
    @Description("Spring標準のメッセージソースの定義")
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.addBasenames("com.github.mygreen.supercsv.localization.SuperCsvMessages", "TestMessages");
        return messageSource;
    }
    
    @Bean
    @Description("本ライブラリのSpring用のMessgeResolverの定義")
    public SpringMessageResolver springMessageResolver() {
        return new SpringMessageResolver(messageSource());
    }
    
    @Bean
    @Description("Spring用のBeanValidatorのValidatorの定義")
    public Validator csvLocalValidatorFactoryBean() {
        
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        
        // メッセージなどをカスタマイズ
        validator.setMessageInterpolator(new MessageInterpolatorAdapter(
                springMessageResolver(), new MessageInterpolator()));
        return validator;
    }
    
    @Bean
    @Description("CSV用のCsvValidaotrの定義")
    public CsvBeanValidator csvBeanValidator() {
        
        // ValidarorのインスタンスをSpring経由で作成したものを利用する
        CsvBeanValidator csvBeanValidator = new CsvBeanValidator(csvLocalValidatorFactoryBean());
        return csvBeanValidator;
    }
    
}

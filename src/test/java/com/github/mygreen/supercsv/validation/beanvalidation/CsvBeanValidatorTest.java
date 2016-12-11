package com.github.mygreen.supercsv.validation.beanvalidation;

import static org.junit.Assert.*;
import static org.assertj.core.api.Assertions.*;
import static com.github.mygreen.supercsv.tool.TestUtils.*;

import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.Range;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import com.github.mygreen.supercsv.annotation.CsvBean;
import com.github.mygreen.supercsv.annotation.CsvColumn;
import com.github.mygreen.supercsv.builder.BeanMapping;
import com.github.mygreen.supercsv.builder.BeanMappingFactory;
import com.github.mygreen.supercsv.localization.MessageInterpolator;
import com.github.mygreen.supercsv.localization.MessageResolver;
import com.github.mygreen.supercsv.localization.ResourceBundleMessageResolver;
import com.github.mygreen.supercsv.validation.CsvBindingErrors;
import com.github.mygreen.supercsv.validation.CsvExceptionConverter;
import com.github.mygreen.supercsv.validation.ValidationContext;

/**
 * {@link CsvBeanValidator}のテスタ
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class CsvBeanValidatorTest {
    
    @Rule
    public TestName name = new TestName();
    
    private CsvBeanValidator csvValidator;
    private CsvBeanValidator csvValidatorCustomMessage;
    
    private BeanMappingFactory beanMappingFactory;
    private CsvExceptionConverter exceptionConverter;
    
    private final Class<?>[] groupEmpty = new Class[]{};
    
    private MessageResolver testMessageResolver;
    private MessageInterpolator messageInterpolator;
    
    @Before
    public void setUp() throws Exception {
        this.beanMappingFactory = new BeanMappingFactory();
        this.exceptionConverter = new CsvExceptionConverter();
        
        this.testMessageResolver = new ResourceBundleMessageResolver(ResourceBundle.getBundle("TestMessages"));
        this.messageInterpolator = new MessageInterpolator();
        
        this.csvValidator = new CsvBeanValidator();
        
        final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        final Validator beanValidator = validatorFactory.usingContext()
                .messageInterpolator(new MessageInterpolatorAdapter(testMessageResolver, messageInterpolator))
                .getValidator();
        this.csvValidatorCustomMessage = new CsvBeanValidator(beanValidator);
        
    }
    
    // テスト用のグループ
    private interface Group1 { }
    private interface Group2 { }
    private interface Group3 { }
    private interface Group4 { }
    
    @CsvBean
    private static class TestCsv {
        
        @CsvColumn(number=1)
        @NotEmpty
        private String id;
        
        @CsvColumn(number=2)
        @Length(max=10)
        @Pattern(regexp="[\\p{Alnum}]+", message="半角英数字で設定してください。", groups=Group1.class)
        private String name;
        
        @CsvColumn(number=3)
        @Range(min=0, max=100, groups=Group2.class)
        private Integer age;
        
        @CsvColumn(number=4)
        boolean used;
        
        @AssertTrue(message="{name}が設定されている際には、{age}は必須です。", groups=Group2.class)
        boolean isValidAgeRequired() {
            if(name != null && !name.isEmpty()) {
                return age != null;
            }
            
            return false;
        }
        
        @DecimalMax(value="20", groups=Group3.class)
        Integer getAge() {
            return age;
        }
        
        @AssertTrue(groups=Group4.class)
        boolean isUsed() {
            return used;
        }
    }
    
    /**
     * 標準のValidatorの場合
     */
    @Test
    public void testValidate_default() {
        
        Class<?>[] groups = groupEmpty;
        CsvBindingErrors bindingErrors = new CsvBindingErrors(TestCsv.class);
        
        BeanMapping<TestCsv> beanMapping = beanMappingFactory.create(TestCsv.class, groupEmpty);
        ValidationContext<TestCsv> validationContext = new ValidationContext<>(ANONYMOUS_CSVCONTEXT, beanMapping);
        
        TestCsv record = new TestCsv();
        
        csvValidator.validate(record, bindingErrors, (ValidationContext)validationContext);
        
        List<String> messages = bindingErrors.getAllErrors().stream()
                .map(error -> error.format(testMessageResolver, messageInterpolator))
                .collect(Collectors.toList());
        
        assertThat(messages).hasSize(1)
            .contains("値が未設定です。");
        
    }
    
    /**
     * グループ指定の場合
     */
    @Test
    public void testValidate_groups() {
        
        Class<?>[] groups = new Class[]{Group1.class};
        CsvBindingErrors bindingErrors = new CsvBindingErrors(TestCsv.class);
        
        BeanMapping<TestCsv> beanMapping = beanMappingFactory.create(TestCsv.class, groupEmpty);
        ValidationContext<TestCsv> validationContext = new ValidationContext<>(ANONYMOUS_CSVCONTEXT, beanMapping);
        
        TestCsv record = new TestCsv();
        record.id = "a01";
        record.name = "あいう";
        
        csvValidator.validate(record, bindingErrors, (ValidationContext)validationContext, groups);
        
        List<String> messages = bindingErrors.getAllErrors().stream()
                .map(error -> error.format(testMessageResolver, messageInterpolator))
                .collect(Collectors.toList());
        
        assertThat(messages).hasSize(1)
            .contains("半角英数字で設定してください。");
        
    }
    
    /**
     * 相関チェックの場合
     */
    @Test
    public void testValidate_relation() {
        
        Class<?>[] groups = new Class[]{Group2.class};
        CsvBindingErrors bindingErrors = new CsvBindingErrors(TestCsv.class);
        
        BeanMapping<TestCsv> beanMapping = beanMappingFactory.create(TestCsv.class, groupEmpty);
        ValidationContext<TestCsv> validationContext = new ValidationContext<>(ANONYMOUS_CSVCONTEXT, beanMapping);
        
        TestCsv record = new TestCsv();
        record.id = "a01";
        record.name = "test";
        
        csvValidator.validate(record, bindingErrors, (ValidationContext)validationContext, groups);
        
        List<String> messages = bindingErrors.getAllErrors().stream()
                .map(error -> error.format(testMessageResolver, messageInterpolator))
                .collect(Collectors.toList());
        
        assertThat(messages).hasSize(1)
            .contains("名前が設定されている際には、年齢は必須です。");
        
    }
    
    /**
     * getterメソッドの場合
     */
    @Test
    public void testValidate_getter() {
        
        Class<?>[] groups = new Class[]{Group3.class};
        CsvBindingErrors bindingErrors = new CsvBindingErrors(TestCsv.class);
        
        BeanMapping<TestCsv> beanMapping = beanMappingFactory.create(TestCsv.class, groupEmpty);
        ValidationContext<TestCsv> validationContext = new ValidationContext<>(ANONYMOUS_CSVCONTEXT, beanMapping);
        
        TestCsv record = new TestCsv();
        record.id = "a01";
        record.name = "test";
        record.age = 40;
        
        csvValidator.validate(record, bindingErrors, (ValidationContext)validationContext, groups);
        
        List<String> messages = bindingErrors.getAllErrors().stream()
                .map(error -> error.format(testMessageResolver, messageInterpolator))
                .collect(Collectors.toList());
        
        assertThat(messages).hasSize(1)
            .contains("20以下の値を設定してください。");
        
    }
    
    /**
     * getterメソッドの場合 - boolean
     */
    @Test
    public void testValidate_getter_boolean() {
        
        Class<?>[] groups = new Class[]{Group4.class};
        CsvBindingErrors bindingErrors = new CsvBindingErrors(TestCsv.class);
        
        BeanMapping<TestCsv> beanMapping = beanMappingFactory.create(TestCsv.class, groupEmpty);
        ValidationContext<TestCsv> validationContext = new ValidationContext<>(ANONYMOUS_CSVCONTEXT, beanMapping);
        
        TestCsv record = new TestCsv();
        record.id = "a01";
        record.used = false;
        
        csvValidator.validate(record, bindingErrors, (ValidationContext)validationContext, groups);
        
        List<String> messages = bindingErrors.getAllErrors().stream()
                .map(error -> error.format(testMessageResolver, messageInterpolator))
                .collect(Collectors.toList());
        
        assertThat(messages).hasSize(1)
            .contains("trueを設定してください。");
        
    }
    
    /**
     * カスタマイズしたValidatorの場合
     */
    @Test
    public void testValidate_custom() {
        
        Class<?>[] groups = groupEmpty;
        CsvBindingErrors bindingErrors = new CsvBindingErrors(TestCsv.class);
        
        BeanMapping<TestCsv> beanMapping = beanMappingFactory.create(TestCsv.class, groupEmpty);
        ValidationContext<TestCsv> validationContext = new ValidationContext<>(ANONYMOUS_CSVCONTEXT, beanMapping);
        
        TestCsv record = new TestCsv();
        
        csvValidatorCustomMessage.validate(record, bindingErrors, (ValidationContext)validationContext);
        
        List<String> messages = bindingErrors.getAllErrors().stream()
                .map(error -> error.format(testMessageResolver, messageInterpolator))
                .collect(Collectors.toList());
        
        assertThat(messages).hasSize(1)
            .contains("[2行, 1列] : 項目「id」の値は必須です。");
        
    }
    
    
}

package com.github.mygreen.supercsv.validation;

import static org.junit.Assert.*;
import static org.assertj.core.api.Assertions.*;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.junit.Before;
import org.junit.Test;
import org.supercsv.prefs.CsvPreference;

import com.github.mygreen.supercsv.annotation.CsvBean;
import com.github.mygreen.supercsv.annotation.CsvColumn;
import com.github.mygreen.supercsv.annotation.constraint.CsvNumberMin;
import com.github.mygreen.supercsv.annotation.constraint.CsvRequire;
import com.github.mygreen.supercsv.annotation.conversion.CsvTrim;
import com.github.mygreen.supercsv.annotation.format.CsvNumberFormat;
import com.github.mygreen.supercsv.builder.BeanMapping;
import com.github.mygreen.supercsv.builder.BeanMappingFactory;
import com.github.mygreen.supercsv.io.CsvAnnotationBeanReader;
import com.github.mygreen.supercsv.io.CsvAnnotationBeanWriter;
import com.github.mygreen.supercsv.localization.MessageResolver;
import com.github.mygreen.supercsv.localization.ResourceBundleMessageResolver;

/**
 * {@link CsvValidator}のテスタ
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class CsvValidatorTest  {
    
    private BeanMappingFactory beanMappingFactory;
    private CsvExceptionConverter exceptionConverter;
    private MessageResolver testMessageResolver = new ResourceBundleMessageResolver(ResourceBundle.getBundle("TestMessages"));
    
    @Before
    public void setUp() throws Exception {
        this.beanMappingFactory = new BeanMappingFactory();
        this.exceptionConverter = new CsvExceptionConverter();
        
    }
    
    @CsvBean(header=true, validators=TestValidator.class)
    public static class TestCsv {
        
        
        @CsvColumn(number=1, label="名前")
        @CsvRequire(considerBlank=true)
        @CsvTrim
        private String name;
        
        @CsvColumn(number=2, label="年齢")
        @CsvTrim
        private Integer age;
        
        @CsvColumn(number=3, label="給料")
        @CsvTrim
        @CsvNumberFormat(pattern="###,##0")
        @CsvNumberMin("0")
        private Integer salary;
        
        public TestCsv() {
            
        }
        
        public TestCsv(final String name, final Integer age, final Integer salary) {
            this.name = name;
            this.age = age;
            this.salary = salary;
        }
        
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public Integer getAge() {
            return age;
        }
        
        public void setAge(Integer age) {
            this.age = age;
        }
        
        public Integer getSalary() {
            return salary;
        }
        
        public void setSalary(Integer salary) {
            this.salary = salary;
        }
        
    }
    
    public static class TestValidator implements CsvValidator<TestCsv> {
        
        @Override
        public void validate(final TestCsv record, final CsvBindingErrors bindingErrors,
                final ValidationContext<TestCsv> validationContext) {
            
            final CsvField<Integer> ageField = new CsvField<>(validationContext, record, "age");
            
            final CsvField<Integer> salaryField = new CsvField<>(validationContext, record, "salary");
            salaryField
                .add(new CsvFieldValidator<Integer>() {
                    
                    @Override
                    public void validate(final CsvBindingErrors bindingErrors, final CsvField<Integer> field) {
                        if(ageField.isEmpty()) {
                            return;
                        }
                        
                        // カラム「age(年齢)」が20以上の場合、カラム「給料(salary)」が設定されているかチェックする。
                        if(ageField.isNotEmpty() && ageField.getValue() >= 20 && field.isEmpty()) {
                            
                            // メッセージ中の変数の作成
                            final Map<String, Object> vars = createMessageVariables(field);
                            vars.put("maxAge", 20);
                            
                            // ageに関するフィールドエラーの追加
                            bindingErrors.rejectValue(field.getName(), field.getType(), "age.required", vars);
                        }
                    }
                })
                .add(new MaxValidator(10_000_000))
                .validate(bindingErrors);
            
            
        }
        
    }
    
    public static class MaxValidator implements CsvFieldValidator<Integer> {
        
        private final int max;
        
        public MaxValidator(final int max) {
            this.max = max;
        }
        
        @Override
        public void validate(CsvBindingErrors bindingErrors, CsvField<Integer> field) {
            if(field.isEmpty()) {
                return;
            }
            
            if(field.getValue() > max) {
                // メッセージ変数の組み立て
                Map<String, Object> vars = createMessageVariables(field);
                vars.put("max", max);
                
                bindingErrors.rejectValue(field.getName(), field.getType(), "fieldError.max", vars);
            }
        }
        
    }
    
    @Test
    public void testValidate_read() throws Exception {
        
        String csv = "名前,年齢,給料"
                + "\r\n阿部真一, 10, "
                + "\r\n山田太郎, 20, \"200,000\""
                + "\r\n鈴木次郎, 30, "  // 給料がnull
                + "\r\n林花子, 40, \"20,000,000\"" // 給料が上限値超え
                + "\r\n本田一郎, 50, abc" // 給料が数値
                ;
        
        CsvAnnotationBeanReader<TestCsv> csvReader = new CsvAnnotationBeanReader<>(TestCsv.class, new StringReader(csv), CsvPreference.STANDARD_PREFERENCE);
        exceptionConverter.setMessageResolver(testMessageResolver);
        csvReader.setExceptionConverter(exceptionConverter);
        
        List<TestCsv> list = csvReader.readAll(true);
        csvReader.close();
        
        assertThat(list).hasSize(2);
        assertThat(list.get(0)).hasFieldOrPropertyWithValue("name", "阿部真一");
        assertThat(list.get(1)).hasFieldOrPropertyWithValue("name", "山田太郎");
        
        List<String> errorMessages = csvReader.getErrorMessages();
        csvReader.getErrorMessages().stream().forEach(System.out::println);
        
        assertThat(errorMessages).hasSize(3).containsExactly(
                "[4行, 3列] : 項目「給料」は、年齢が20歳以上の場合には必須です。",
                "[5行, 3列] : 項目「給料」の値（20,000,000）は、10,000,000以内で入力してください。",
                "[6行, 3列] : 項目「給料」の値（abc）は、整数の書式「#,##0」として不正です。"
                );
        
    }
    
    /**
     * 書き込み時の入力値検証 - 検証のスキップ
     */
    @Test
    public void testValidate_write_skipValidate() throws Exception {
        
        StringWriter writer = new StringWriter();
        
        beanMappingFactory.getConfiguration().setSkipValidationOnWrite(true);
        BeanMapping<TestCsv> beanMapping = beanMappingFactory.create(TestCsv.class);
        
        CsvAnnotationBeanWriter<TestCsv> csvWriter = new CsvAnnotationBeanWriter<>(beanMapping, writer, CsvPreference.STANDARD_PREFERENCE);
        exceptionConverter.setMessageResolver(testMessageResolver);
        csvWriter.setExceptionConverter(exceptionConverter);
        
        List<TestCsv> list = new ArrayList<>();
        list.add(new TestCsv("阿部真一", 10, null));
        list.add(new TestCsv("山田太郎", 20, 200_000));
        list.add(new TestCsv("鈴木次郎", 30, null));  // 給料がnull
        list.add(new TestCsv("林花子", 40, 20_000_000));
        list.add(new TestCsv("橋本誠", 60, 400_000)); // 給料が上限値超え
        
        csvWriter.writeAll(list, true);
        csvWriter.flush();
        csvWriter.close();
        
        
        List<String> errorMessages = csvWriter.getErrorMessages();
        assertThat(errorMessages).isEmpty();
        
        String csv = writer.toString();
        System.out.println(csv);
        
        String expected = "名前,年齢,給料"
                + "\r\n阿部真一,10,"
                + "\r\n山田太郎,20,\"200,000\""
                + "\r\n鈴木次郎,30,"  // 給料がnull
                + "\r\n林花子,40,\"20,000,000\"" // 給料が上限値超え
                + "\r\n橋本誠,60,\"400,000\""
                + "\r\n"
                ;
        
        assertThat(csv).isEqualTo(expected);
        
        
    }
    
    /**
     * 書き込み時の入力値検証 - 検証の実行
     */
    @Test
    public void testValidate_write_validate() throws Exception {
        
        StringWriter writer = new StringWriter();
        
        CsvAnnotationBeanWriter<TestCsv> csvWriter = new CsvAnnotationBeanWriter<>(TestCsv.class, writer, CsvPreference.STANDARD_PREFERENCE);
        exceptionConverter.setMessageResolver(testMessageResolver);
        csvWriter.setExceptionConverter(exceptionConverter);
        
        List<TestCsv> list = new ArrayList<>();
        list.add(new TestCsv("阿部真一", 10, null));
        list.add(new TestCsv("山田太郎", 20, 200_000));
        list.add(new TestCsv("鈴木次郎", 30, null));  // 給料がnull
        list.add(new TestCsv("林花子", 40, 20_000_000));
        list.add(new TestCsv("橋本誠", 60, 400_000)); // 給料が上限値超え
        
        csvWriter.writeAll(list, true);
        csvWriter.flush();
        csvWriter.close();
        
        List<String> errorMessages = csvWriter.getErrorMessages();
        assertThat(errorMessages).hasSize(2).containsExactly(
                "[4行, 3列] : 項目「給料」は、年齢が20歳以上の場合には必須です。",
                "[5行, 3列] : 項目「給料」の値（20,000,000）は、10,000,000以内で入力してください。"
                );
        
        String csv = writer.toString();
        System.out.println(csv);
        
        String expected = "名前,年齢,給料"
                + "\r\n阿部真一,10,"
                + "\r\n山田太郎,20,\"200,000\""
                + "\r\n橋本誠,60,\"400,000\""
                + "\r\n"
                ;
        
        assertThat(csv).isEqualTo(expected);
        
        
    }
    
}

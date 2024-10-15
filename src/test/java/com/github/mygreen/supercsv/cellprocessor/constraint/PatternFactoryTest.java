package com.github.mygreen.supercsv.cellprocessor.constraint;

import static com.github.mygreen.supercsv.tool.HasCellProcessorAssert.assertThat;
import static com.github.mygreen.supercsv.tool.TestUtils.*;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import java.lang.annotation.Annotation;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.supercsv.cellprocessor.ift.CellProcessor;

import com.github.mygreen.supercsv.annotation.CsvBean;
import com.github.mygreen.supercsv.annotation.CsvColumn;
import com.github.mygreen.supercsv.annotation.PatternFlag;
import com.github.mygreen.supercsv.annotation.constraint.CsvPattern;
import com.github.mygreen.supercsv.builder.BeanMapping;
import com.github.mygreen.supercsv.builder.BeanMappingFactory;
import com.github.mygreen.supercsv.builder.ColumnMapping;
import com.github.mygreen.supercsv.builder.Configuration;
import com.github.mygreen.supercsv.builder.FieldAccessor;
import com.github.mygreen.supercsv.builder.ProcessorBuilderResolver;
import com.github.mygreen.supercsv.builder.standard.StringProcessorBuilder;
import com.github.mygreen.supercsv.cellprocessor.format.TextFormatter;
import com.github.mygreen.supercsv.exception.SuperCsvInvalidAnnotationException;
import com.github.mygreen.supercsv.exception.SuperCsvValidationException;
import com.github.mygreen.supercsv.validation.CsvExceptionConverter;

/**
 * {@link PatternFactory}のテスタ
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class PatternFactoryTest {
    
    @Rule
    public TestName name = new TestName();
    
    private PatternFactory factory;
    
    private Configuration config;
    private Comparator<Annotation> comparator;
    private ProcessorBuilderResolver builderResolver;
    
    private BeanMappingFactory beanMappingFactory;
    private CsvExceptionConverter exceptionConverter;
        
    private final Class<?>[] groupEmpty = new Class[]{};
    
    @Before
    public void setUp() throws Exception {
        this.factory = new PatternFactory();
        
        this.config = new Configuration();
        this.beanMappingFactory = new BeanMappingFactory();
        beanMappingFactory.setConfiguration(config);
        
        this.exceptionConverter = new CsvExceptionConverter();
        
        this.comparator = config.getAnnoationComparator();
        this.builderResolver = config.getBuilderResolver();
    }
    
    private static final String TEST_VALUE_VALID_INPUT = "2000-01-02";
    private static final String TEST_VALUE_WRONG_INPUT = "abcdef";
    
    private static final String REGEX = "\\d{4}-\\d{2}-\\d{2}";
    private static final String DESCRIPTION = "説明";
    
    @CsvBean
    private static class TestCsv {
        
        @CsvColumn(number=1, label="カラム1")
        @CsvPattern(regex=REGEX, description=DESCRIPTION, flags=PatternFlag.CASE_INSENSITIVE)
        private String col_default;
        
        @CsvColumn(number=2)
        @CsvPattern(regex=REGEX)
        private String col_description_empty;
        
        @CsvColumn(number=10)
        @CsvPattern(regex=REGEX, message="テストメッセージ")
        private String col_message;
        
        @CsvColumn(number=11)
        @CsvPattern(regex=REGEX, description=DESCRIPTION, flags=PatternFlag.CASE_INSENSITIVE, message="")
        private String col_message_empty;
        
        @CsvColumn(number=12)
        @CsvPattern(regex=REGEX, description=DESCRIPTION, flags=PatternFlag.CASE_INSENSITIVE,
                message="lineNumber={lineNumber}, rowNumber={rowNumber}, columnNumber={columnNumber}, label={label}, validatedValue={validatedValue}, regex={regex}, flags={flags}, description={description}")
        private String col_message_variables;
        
    }
    
    /**
     * アノテーションの設定が不正な場合
     *
     */
    @CsvBean
    private static class ErrorCsv {
        
        @CsvColumn(number=1)
        @CsvPattern(regex="aaa)abc")
        private String col_regex_invalid;
        
    }
    
    @Test
    public void testCreate_default() {
        
        FieldAccessor field = getFieldAccessor(TestCsv.class, "col_default", comparator);
        StringProcessorBuilder builder = (StringProcessorBuilder) builderResolver.resolve(String.class);
        TextFormatter<String> formatter = builder.getFormatter(field, config);
        
        CsvPattern anno = field.getAnnotationsByGroup(CsvPattern.class, groupEmpty).get(0);
        
        {
            //next null
            Optional<CellProcessor> processor = factory.create(anno, Optional.empty(), field, formatter, config);
            printCellProcessorChain(processor.get(), name.getMethodName());
            
            assertThat(processor.get()).isInstanceOf(Pattern.class);
            
            Pattern actual = (Pattern)processor.get();
            assertThat(actual.getRegex()).isEqualTo(REGEX);
            assertThat(actual.getFlags()).isEqualTo(java.util.regex.Pattern.CASE_INSENSITIVE);
            assertThat(actual.getDescription()).isEqualTo(DESCRIPTION);
            
            {
                // valid input
                String input = TEST_VALUE_VALID_INPUT;
                assertThat((Object)actual.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(input);
            }
            
            {
                // wrong input
                String input = TEST_VALUE_WRONG_INPUT;
                assertThatThrownBy(() -> actual.execute(input, ANONYMOUS_CSVCONTEXT)).isInstanceOf(SuperCsvValidationException.class);
            }
            
            assertThat(actual.getValidationMessage()).isEqualTo("{com.github.mygreen.supercsv.annotation.constraint.CsvPattern.message}");
        }
        
        {
            //next exist
            Optional<CellProcessor> processor = factory.create(anno, Optional.of(new NextCellProcessor()), field, formatter, config);
            printCellProcessorChain(processor.get(), name.getMethodName());
            
            assertThat(processor.get()).isInstanceOf(Pattern.class);
            
            Pattern actual = (Pattern)processor.get();
            assertThat(actual.getRegex()).isEqualTo(REGEX);
            assertThat(actual.getFlags()).isEqualTo(java.util.regex.Pattern.CASE_INSENSITIVE);
            assertThat(actual.getDescription()).isEqualTo(DESCRIPTION);
            
            {
                // valid input
                String input = TEST_VALUE_VALID_INPUT;
                assertThat((Object)actual.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(input);
            }
            
            {
                // wrong input
                String input = TEST_VALUE_WRONG_INPUT;
                assertThatThrownBy(() -> actual.execute(input, ANONYMOUS_CSVCONTEXT)).isInstanceOf(SuperCsvValidationException.class);
            }
            
            assertThat(actual.getValidationMessage()).isEqualTo("{com.github.mygreen.supercsv.annotation.constraint.CsvPattern.message}");
        }
        
    }
    
    /**
     * 属性 description を指定しない場合
     */
    @Test
    public void testCreate_attrDescription_empty() {
        
        FieldAccessor field = getFieldAccessor(TestCsv.class, "col_description_empty", comparator);
        StringProcessorBuilder builder = (StringProcessorBuilder) builderResolver.resolve(String.class);
        TextFormatter<String> formatter = builder.getFormatter(field, config);
        
        CsvPattern anno = field.getAnnotationsByGroup(CsvPattern.class, groupEmpty).get(0);
        
        //next null
        Optional<CellProcessor> processor = factory.create(anno, Optional.empty(), field, formatter, config);
        printCellProcessorChain(processor.get(), name.getMethodName());
        
        assertThat(processor.get()).isInstanceOf(Pattern.class);
        
        Pattern actual = (Pattern)processor.get();
        assertThat(actual.getRegex()).isEqualTo(REGEX);
        assertThat(actual.getFlags()).isEqualTo(0);
        assertThat(actual.getDescription()).isEmpty();
        
    }
    
    /**
     * 属性 regex の値が不正な場合
     */
    @Test
    public void testCreate_attrRegex_invalid() {
        
        FieldAccessor field = getFieldAccessor(ErrorCsv.class, "col_regex_invalid", comparator);
        StringProcessorBuilder builder = (StringProcessorBuilder) builderResolver.resolve(String.class);
        TextFormatter<String> formatter = builder.getFormatter(field, config);
        
        CsvPattern anno = field.getAnnotationsByGroup(CsvPattern.class, groupEmpty).get(0);
        
        //next null
        try {
            factory.create(anno, Optional.empty(), field, formatter, config);
            
        } catch(Exception e) {
            
            assertThat(e).isInstanceOf(SuperCsvInvalidAnnotationException.class)
                .hasMessage("'%s' において、アノテーション @CsvPattern の属性 'regex' の値（aaa)abc）は、正規表現として不正です。",
                        field.getNameWithClass());
//            e.printStackTrace();
        }
        
        
    }
    
    /**
     * エラーメッセージのテスト - 標準
     */
    @Test
    public void testErrorMessage_default() {
        
        BeanMapping<TestCsv> beanMapping = beanMappingFactory.create(TestCsv.class, groupEmpty);
        
        ColumnMapping columnMapping = beanMapping.getColumnMapping("col_default").get();
        
        CellProcessor processor = columnMapping.getCellProcessorForReading();
        printCellProcessorChain(processor, name.getMethodName());
        assertThat(processor).hasCellProcessor(Pattern.class);
        
        String input = TEST_VALUE_WRONG_INPUT;
        try {
            processor.execute(input, testCsvContext(columnMapping, input));
            fail();
            
        } catch(Exception e) {
            
            assertThat(e).isInstanceOf(SuperCsvValidationException.class);
            
            List<String> messages = exceptionConverter.convertAndFormat((SuperCsvValidationException)e, beanMapping);
            assertThat(messages).hasSize(1)
                    .contains("[2行, 1列] : 項目「カラム1」の値（abcdef）は、説明に一致しません。");
        }
        
    }
    
    /**
     * エラーメッセージのテスト - 属性descriptionが空
     */
    @Test
    public void testErrorMessage_description_empty() {
        
        BeanMapping<TestCsv> beanMapping = beanMappingFactory.create(TestCsv.class, groupEmpty);
        
        ColumnMapping columnMapping = beanMapping.getColumnMapping("col_description_empty").get();
        
        CellProcessor processor = columnMapping.getCellProcessorForReading();
        printCellProcessorChain(processor, name.getMethodName());
        assertThat(processor).hasCellProcessor(Pattern.class);
        
        String input = TEST_VALUE_WRONG_INPUT;
        try {
            processor.execute(input, testCsvContext(columnMapping, input));
            fail();
            
        } catch(Exception e) {
            
            assertThat(e).isInstanceOf(SuperCsvValidationException.class);
            
            List<String> messages = exceptionConverter.convertAndFormat((SuperCsvValidationException)e, beanMapping);
            assertThat(messages).hasSize(1)
                    .contains("[2行, 2列] : 項目「col_description_empty」の値（abcdef）は、正規表現「\\d{4}-\\d{2}-\\d{2}」に一致しません。");
        }
        
    }
    
    /**
     * エラーメッセージのテスト - アノテーションの属性「message」の指定
     */
    @Test
    public void testErrorMessage_message() {
        
        BeanMapping<TestCsv> beanMapping = beanMappingFactory.create(TestCsv.class, groupEmpty);
        
        ColumnMapping columnMapping = beanMapping.getColumnMapping("col_message").get();
        
        CellProcessor processor = columnMapping.getCellProcessorForReading();
        printCellProcessorChain(processor, name.getMethodName());
        assertThat(processor).hasCellProcessor(Pattern.class);
        
        String input = TEST_VALUE_WRONG_INPUT;
        try {
            processor.execute(input, testCsvContext(columnMapping, input));
            fail();
            
        } catch(Exception e) {
            
            assertThat(e).isInstanceOf(SuperCsvValidationException.class);
            
            List<String> messages = exceptionConverter.convertAndFormat((SuperCsvValidationException)e, beanMapping);
            assertThat(messages).hasSize(1)
                    .contains("テストメッセージ");
        }
        
    }
    
    /**
     * エラーメッセージのテスト - アノテーションの属性「message」が空文字の場合
     */
    @Test
    public void testErrorMessage_empty() {
        
        BeanMapping<TestCsv> beanMapping = beanMappingFactory.create(TestCsv.class, groupEmpty);
        
        ColumnMapping columnMapping = beanMapping.getColumnMapping("col_message_empty").get();
        
        CellProcessor processor = columnMapping.getCellProcessorForReading();
        printCellProcessorChain(processor, name.getMethodName());
        assertThat(processor).hasCellProcessor(Pattern.class);
        
        String input = TEST_VALUE_WRONG_INPUT;
        try {
            processor.execute(input, testCsvContext(columnMapping, input));
            fail();
            
        } catch(Exception e) {
            
            assertThat(e).isInstanceOf(SuperCsvValidationException.class);
            
            List<String> messages = exceptionConverter.convertAndFormat((SuperCsvValidationException)e, beanMapping);
            assertThat(messages).hasSize(1)
                    .contains("[2行, 11列] : 項目「col_message_empty」の値（abcdef）は、説明に一致しません。");
        }
        
    }
    
    /**
     * エラーメッセージのテスト - メッセージ変数の確認
     */
    @Test
    public void testErrorMessage_variables() {
        
        BeanMapping<TestCsv> beanMapping = beanMappingFactory.create(TestCsv.class, groupEmpty);
        
        ColumnMapping columnMapping = beanMapping.getColumnMapping("col_message_variables").get();
        
        CellProcessor processor = columnMapping.getCellProcessorForReading();
        printCellProcessorChain(processor, name.getMethodName());
        assertThat(processor).hasCellProcessor(Pattern.class);
        
        String input = TEST_VALUE_WRONG_INPUT;
        try {
            processor.execute(input, testCsvContext(columnMapping, input));
            fail();
            
        } catch(Exception e) {
            
            assertThat(e).isInstanceOf(SuperCsvValidationException.class);
            
            List<String> messages = exceptionConverter.convertAndFormat((SuperCsvValidationException)e, beanMapping);
            assertThat(messages).hasSize(1)
                    .contains("lineNumber=1, rowNumber=2, columnNumber=12, label=col_message_variables, validatedValue=abcdef, regex=\\d{4}-\\d{2}-\\d{2}, flags=2, description=説明");
        }
        
    }
    
    
}

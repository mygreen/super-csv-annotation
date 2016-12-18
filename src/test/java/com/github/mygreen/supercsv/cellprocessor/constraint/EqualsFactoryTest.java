package com.github.mygreen.supercsv.cellprocessor.constraint;

import static org.junit.Assert.*;
import static org.assertj.core.api.Assertions.*;
import static com.github.mygreen.supercsv.tool.TestUtils.*;
import static com.github.mygreen.supercsv.tool.HasCellProcessorAssert.*;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.supercsv.cellprocessor.ift.CellProcessor;

import com.github.mygreen.supercsv.annotation.CsvBean;
import com.github.mygreen.supercsv.annotation.CsvColumn;
import com.github.mygreen.supercsv.annotation.constraint.CsvEquals;
import com.github.mygreen.supercsv.annotation.format.CsvNumberFormat;
import com.github.mygreen.supercsv.builder.ProcessorBuilderResolver;
import com.github.mygreen.supercsv.builder.BeanMapping;
import com.github.mygreen.supercsv.builder.BeanMappingFactory;
import com.github.mygreen.supercsv.builder.BuildCase;
import com.github.mygreen.supercsv.builder.ColumnMapping;
import com.github.mygreen.supercsv.builder.Configuration;
import com.github.mygreen.supercsv.builder.FieldAccessor;
import com.github.mygreen.supercsv.builder.standard.IntegerProcessorBuilder;
import com.github.mygreen.supercsv.cellprocessor.format.TextFormatter;
import com.github.mygreen.supercsv.exception.SuperCsvInvalidAnnotationException;
import com.github.mygreen.supercsv.exception.SuperCsvValidationException;
import com.github.mygreen.supercsv.validation.CsvExceptionConverter;

/**
 * {@link EqualsFactory}のテスタ
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class EqualsFactoryTest {
    
    @Rule
    public TestName name = new TestName();
    
    private EqualsFactory<Integer> factory;
    
    private Configuration config;
    private Comparator<Annotation> comparator;
    private ProcessorBuilderResolver builderResolver;
    
    private BeanMappingFactory beanMappingFactory;
    private CsvExceptionConverter exceptionConverter;
    
    private final Class<?>[] groupEmpty = new Class[]{};
    
    @Before
    public void setUp() throws Exception {
        this.factory = new EqualsFactory<Integer>();
        
        this.config = new Configuration();
        this.beanMappingFactory = new BeanMappingFactory();
        beanMappingFactory.setConfiguration(config);
        
        this.exceptionConverter = new CsvExceptionConverter();
        
        this.comparator = config.getAnnoationComparator();
        this.builderResolver = config.getBuilderResolver();
    }
    
    private static final String TEST_FORMATTED_PATTERN = "#,###";
    
    private static final Integer TEST_VALUE_VALID_OBJ = 1000;
    private static final String TEST_VALUE_VALID_STR_FORMATTED = "1,000";
    
    private static final Integer TEST_VALUE_WRONG_OBJ = 1001;
    private static final String TEST_VALUE_WRONG_STR_FORMATTED = "1,001";
    
    private static Integer[] VALUES = new Integer[]{-1000, 1000};
    
    @CsvBean
    private static class TestCsv {
        
        @CsvColumn(number=1, label="カラム1")
        @CsvNumberFormat(pattern=TEST_FORMATTED_PATTERN)
        @CsvEquals(value={"-1,000", "1,000"})
        private Integer col_default;
        
        @CsvColumn(number=2)
        @CsvNumberFormat(pattern=TEST_FORMATTED_PATTERN)
        @CsvEquals(value={ "1,000"})
        private Integer col_value_1;
        
        @CsvColumn(number=3)
        @CsvNumberFormat(pattern=TEST_FORMATTED_PATTERN)
        @CsvEquals(provider=FileEqualedValueProvider.class)
        private Integer col_privider;
        
        @CsvColumn(number=10)
        @CsvNumberFormat(pattern=TEST_FORMATTED_PATTERN)
        @CsvEquals(value={"-1,000", "1,000"}, message="テストメッセージ")
        private Integer col_message;
        
        @CsvColumn(number=11)
        @CsvNumberFormat(pattern=TEST_FORMATTED_PATTERN)
        @CsvEquals(value={"-1,000", "1,000"}, message="")
        private Integer col_message_empty;
        
        @CsvColumn(number=12)
        @CsvNumberFormat(pattern=TEST_FORMATTED_PATTERN)
        @CsvEquals(value={"-1,000", "1,000"},
                message="lineNumber={lineNumber}, rowNumber={rowNumber}, columnNumber={columnNumber}, label={label}, validatedValue=${printer.print(validatedValue)}, equalsValues=${f:join(equalsValues, ', ', printer)}")
        private Integer col_message_variables;
        
    }
    
    /**
     * 属性が不正なCSV
     *
     */
    @CsvBean
    private static class ErrorCsv {
        
        @CsvColumn(number=2)
        @CsvNumberFormat(pattern="#,###")
        @CsvEquals(value={"1", "abc"})
        private Integer col_value_wrong;
        
        @CsvColumn(number=2)
        @CsvNumberFormat(pattern="#,###")
        @CsvEquals
        private Integer col_no_value_provider;
        
    }
    
    // ファイルから読み込む場合
    private static class FileEqualedValueProvider implements EqualedValueProvider<Integer> {
        
        @Override
        public Collection<Integer> getEqualedValues(final FieldAccessor field) {
            
            final List<String> lines;
            try {
                lines = Files.readAllLines(
                        new File("src/test/data/data_equaled_value.txt").toPath(), Charset.forName("UTF-8"));
                
            } catch (IOException e) {
                throw new RuntimeException("fail reading the equaled value file.", e);
            }
            
            return lines.stream()
                    .map(l -> Integer.valueOf(l))
                    .collect(Collectors.toList());
            
        }
    }
    
    @Test
    public void testCreate_default() {
        
        FieldAccessor field = getFieldAccessor(TestCsv.class, "col_default", comparator);
        IntegerProcessorBuilder builder = (IntegerProcessorBuilder) builderResolver.resolve(Integer.class);
        TextFormatter<Integer> formatter = builder.getFormatter(field, config);
        
        CsvEquals anno = field.getAnnotationsByGroup(CsvEquals.class, groupEmpty).get(0);
        
        {
            //next null
            Optional<CellProcessor> processor = factory.create(anno, Optional.empty(), field, formatter, config);
            printCellProcessorChain(processor.get(), name.getMethodName());
            
            assertThat(processor.get()).isInstanceOf(Equals.class);
            
            Equals<Integer> actual = (Equals<Integer>)processor.get();
            assertThat(actual.getEqualedValues()).containsExactly(VALUES);
            assertThat(actual.getPrinter()).isEqualTo(formatter);
            
            {
                // valid input
                Integer input = TEST_VALUE_VALID_OBJ;
                assertThat((Object)actual.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(input);
            }
            
            {
                // wrong input
                Integer input = TEST_VALUE_WRONG_OBJ;
                assertThatThrownBy(() -> actual.execute(input, ANONYMOUS_CSVCONTEXT)).isInstanceOf(SuperCsvValidationException.class);
            }
            
            assertThat(actual.getValidationMessage()).isEqualTo("{com.github.mygreen.supercsv.annotation.constraint.CsvEquals.message}");
        }
        
        {
            //next exist
            Optional<CellProcessor> processor = factory.create(anno, Optional.of(new NextCellProcessor()), field, formatter, config);
            printCellProcessorChain(processor.get(), name.getMethodName());
            
            assertThat(processor.get()).isInstanceOf(Equals.class);
            
            Equals<Integer> actual = (Equals<Integer>)processor.get();
            assertThat(actual.getEqualedValues()).containsExactly(VALUES);
            assertThat(actual.getPrinter()).isEqualTo(formatter);
            
            {
                // valid input
                Integer input = TEST_VALUE_VALID_OBJ;
                assertThat((Object)actual.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(input);
            }
            
            {
                // wrong input
                Integer input = TEST_VALUE_WRONG_OBJ;
                assertThatThrownBy(() -> actual.execute(input, ANONYMOUS_CSVCONTEXT)).isInstanceOf(SuperCsvValidationException.class);
            }
            
            assertThat(actual.getValidationMessage()).isEqualTo("{com.github.mygreen.supercsv.annotation.constraint.CsvEquals.message}");
        }
        
    }
    
    /**
     * 属性providerを指定
     */
    @Test
    public void testCreate_attrProvider() {
        
        FieldAccessor field = getFieldAccessor(TestCsv.class, "col_privider", comparator);
        IntegerProcessorBuilder builder = (IntegerProcessorBuilder) builderResolver.resolve(Integer.class);
        TextFormatter<Integer> formatter = builder.getFormatter(field, config);
        
        CsvEquals anno = field.getAnnotationsByGroup(CsvEquals.class, groupEmpty).get(0);
        
        Optional<CellProcessor> processor = factory.create(anno, Optional.empty(), field, formatter, config);
        printCellProcessorChain(processor.get(), name.getMethodName());
        
        assertThat(processor.get()).isInstanceOf(Equals.class);
        
        Equals<Integer> actual = (Equals<Integer>)processor.get();
        assertThat(actual.getEqualedValues()).containsExactly(VALUES);
        assertThat(actual.getPrinter()).isEqualTo(formatter);
        
        {
            // valid input
            Integer input = TEST_VALUE_VALID_OBJ;
            assertThat((Object)actual.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(input);
        }
        
        {
            // wrong input
            Integer input = TEST_VALUE_WRONG_OBJ;
            assertThatThrownBy(() -> actual.execute(input, ANONYMOUS_CSVCONTEXT)).isInstanceOf(SuperCsvValidationException.class);
        }
        
    }
    
    /**
     * 属性valueの値が不正
     */
    @Test
    public void testCreate_attrValue_wrong() {
        
        FieldAccessor field = getFieldAccessor(ErrorCsv.class, "col_value_wrong", comparator);
        IntegerProcessorBuilder builder = (IntegerProcessorBuilder) builderResolver.resolve(Integer.class);
        TextFormatter<Integer> formatter = builder.getFormatter(field, config);
        
        CsvEquals anno = field.getAnnotationsByGroup(CsvEquals.class, groupEmpty).get(0);
        
        try {
            factory.create(anno, Optional.empty(), field, formatter, config);
            fail();
            
        } catch(Exception e) {
            assertThat(e).isInstanceOf(SuperCsvInvalidAnnotationException.class)
                .hasMessage("'%s' において、アノテーション @CsvEquals の属性 'value' の値（abc）は、java.lang.Integer (#,###)として不正です。", 
                        field.getNameWithClass());
            
            SuperCsvInvalidAnnotationException exception = (SuperCsvInvalidAnnotationException)e;
            assertThat(exception.getTargetAnnotation()).isInstanceOf(CsvEquals.class);
            
            
        }
        
    }
    
    /**
     * 属性value, providerの指定がない
     */
    @Test
    public void testCreate_attrNoValueProvider() {
        
        FieldAccessor field = getFieldAccessor(ErrorCsv.class, "col_no_value_provider", comparator);
        IntegerProcessorBuilder builder = (IntegerProcessorBuilder) builderResolver.resolve(Integer.class);
        TextFormatter<Integer> formatter = builder.getFormatter(field, config);
        
        CsvEquals anno = field.getAnnotationsByGroup(CsvEquals.class, groupEmpty).get(0);
        
        try {
            factory.create(anno, Optional.empty(), field, formatter, config);
            fail();
            
        } catch(Exception e) {
            assertThat(e).isInstanceOf(SuperCsvInvalidAnnotationException.class)
                .hasMessage("'%s' において、アノテーション @CsvEquals の属性 'value or provider' の指定は必須です。", 
                        field.getNameWithClass());
            
            SuperCsvInvalidAnnotationException exception = (SuperCsvInvalidAnnotationException)e;
            assertThat(exception.getTargetAnnotation()).isInstanceOf(CsvEquals.class);
            
            
        }
        
    }
    
    /**
     * 属性messageのテスト
     */
    @Test
    public void testCreate_attrMessage() {
        
        FieldAccessor field = getFieldAccessor(TestCsv.class, "col_message", comparator);
        IntegerProcessorBuilder builder = (IntegerProcessorBuilder) builderResolver.resolve(Integer.class);
        TextFormatter<Integer> formatter = builder.getFormatter(field, config);
        
        CsvEquals anno = field.getAnnotationsByGroup(CsvEquals.class, groupEmpty).get(0);
        
        Optional<CellProcessor> processor = factory.create(anno, Optional.empty(), field, formatter, config);
        printCellProcessorChain(processor.get(), name.getMethodName());
        
        assertThat(processor.get()).isInstanceOf(Equals.class);
        
        Equals<Integer> actual = (Equals<Integer>)processor.get();
        
        assertThat(actual.getValidationMessage()).isEqualTo("テストメッセージ");
        
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
        assertThat(processor).hasCellProcessor(Equals.class);
        
        String input = TEST_VALUE_WRONG_STR_FORMATTED;
        try {
            processor.execute(input, testCsvContext(columnMapping, input));
            fail();
            
        } catch(Exception e) {
            
            assertThat(e).isInstanceOf(SuperCsvValidationException.class);
            
            List<String> messages = exceptionConverter.convertAndFormat((SuperCsvValidationException)e, beanMapping);
            assertThat(messages).hasSize(1)
                    .contains("[2行, 1列] : 項目「カラム1」の値（1,001）は、何れかの値「-1,000, 1,000」と一致する必要があります。");
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
        assertThat(processor).hasCellProcessor(Equals.class);
        
        String input = TEST_VALUE_WRONG_STR_FORMATTED;
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
        assertThat(processor).hasCellProcessor(Equals.class);
        
        String input = TEST_VALUE_WRONG_STR_FORMATTED;
        try {
            processor.execute(input, testCsvContext(columnMapping, input));
            fail();
            
        } catch(Exception e) {
            
            assertThat(e).isInstanceOf(SuperCsvValidationException.class);
            
            List<String> messages = exceptionConverter.convertAndFormat((SuperCsvValidationException)e, beanMapping);
            assertThat(messages).hasSize(1)
                    .contains("[2行, 11列] : 項目「col_message_empty」の値（1,001）は、何れかの値「-1,000, 1,000」と一致する必要があります。");
        }
        
    }
    
    /**
     * エラーメッセージのテスト - 値が1つの場合
     */
    @Test
    public void testErrorMessage_value1() {
        
        BeanMapping<TestCsv> beanMapping = beanMappingFactory.create(TestCsv.class, groupEmpty);
        
        ColumnMapping columnMapping = beanMapping.getColumnMapping("col_value_1").get();
        
        CellProcessor processor = columnMapping.getCellProcessorForReading();
        printCellProcessorChain(processor, name.getMethodName());
        assertThat(processor).hasCellProcessor(Equals.class);
        
        String input = TEST_VALUE_WRONG_STR_FORMATTED;
        try {
            processor.execute(input, testCsvContext(columnMapping, input));
            fail();
            
        } catch(Exception e) {
            
            assertThat(e).isInstanceOf(SuperCsvValidationException.class);
            
            List<String> messages = exceptionConverter.convertAndFormat((SuperCsvValidationException)e, beanMapping);
            assertThat(messages).hasSize(1)
                    .contains("[2行, 2列] : 項目「col_value_1」の値（1,001）は、値「1,000」と一致する必要があります。");
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
        assertThat(processor).hasCellProcessor(Equals.class);
        
        String input = TEST_VALUE_WRONG_STR_FORMATTED;
        try {
            processor.execute(input, testCsvContext(columnMapping, input));
            fail();
            
        } catch(Exception e) {
            
            assertThat(e).isInstanceOf(SuperCsvValidationException.class);
            
            List<String> messages = exceptionConverter.convertAndFormat((SuperCsvValidationException)e, beanMapping);
            assertThat(messages).hasSize(1)
                    .contains("lineNumber=1, rowNumber=2, columnNumber=12, label=col_message_variables, validatedValue=1,001, equalsValues=-1,000, 1,000");
        }
        
    }
    
    
}

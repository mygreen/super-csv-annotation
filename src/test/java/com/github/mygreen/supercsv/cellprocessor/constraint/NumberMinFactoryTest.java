package com.github.mygreen.supercsv.cellprocessor.constraint;

import static org.junit.Assert.*;
import static org.assertj.core.api.Assertions.*;
import static com.github.mygreen.supercsv.tool.TestUtils.*;
import static com.github.mygreen.supercsv.tool.HasCellProcessorAssert.*;

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
import com.github.mygreen.supercsv.annotation.constraint.CsvNumberMin;
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
 * {@link NumberMinFactory}のテスタ。
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class NumberMinFactoryTest {
    
    @Rule
    public TestName name = new TestName();
    
    private NumberMinFactory<Integer> factory;
    
    private Configuration config;
    private Comparator<Annotation> comparator;
    private ProcessorBuilderResolver builderResolver;
    
    private BeanMappingFactory beanMappingFactory;
    private CsvExceptionConverter exceptionConverter;
        
    private final Class<?>[] groupEmpty = new Class[]{};
    
    @Before
    public void setUp() throws Exception {
        this.factory = new NumberMinFactory<Integer>();
        
        this.config = new Configuration();
        this.beanMappingFactory = new BeanMappingFactory();
        beanMappingFactory.setConfiguration(config);
        
        this.exceptionConverter = new CsvExceptionConverter();
        
        this.comparator = config.getAnnoationComparator();
        this.builderResolver = config.getBuilderResolver();
    }
    
    private static final String TEST_FORMATTED_PATTERN = "#,###";
    
    private static final Integer TEST_VALUE_MIN_OBJ = 1000;
    private static final String TEST_VALUE_MIN_FORMATTED = "1,000";
    
    private static final Integer TEST_VALUE_MINUS1_OBJ_1 = 999;
    private static final String TEST_VALUE_MINUS1_STR_FORMATTED_1 = "999";
    
    private static final Integer TEST_VALUE_PLUS1_OBJ_1 = 1001;
    private static final String TEST_VALUE_PLUS1_STR_FORMATTED_2 = "1,001";
    
    @CsvBean
    private static class TestCsv {
        
        @CsvColumn(number=1, label="カラム1")
        @CsvNumberFormat(pattern=TEST_FORMATTED_PATTERN)
        @CsvNumberMin(value=TEST_VALUE_MIN_FORMATTED)
        private Integer col_default;
        
        @CsvColumn(number=2)
        @CsvNumberFormat(pattern=TEST_FORMATTED_PATTERN)
        @CsvNumberMin(value=TEST_VALUE_MIN_FORMATTED, inclusive=false)
        private Integer col_inclusive_false;
        
        @CsvColumn(number=10)
        @CsvNumberFormat(pattern=TEST_FORMATTED_PATTERN)
        @CsvNumberMin(value=TEST_VALUE_MIN_FORMATTED, message="テストメッセージ")
        private Integer col_message;
        
        @CsvColumn(number=11)
        @CsvNumberFormat(pattern=TEST_FORMATTED_PATTERN)
        @CsvNumberMin(value=TEST_VALUE_MIN_FORMATTED, message="")
        private Integer col_message_empty;
        
        @CsvColumn(number=12)
        @CsvNumberFormat(pattern=TEST_FORMATTED_PATTERN)
        @CsvNumberMin(value=TEST_VALUE_MIN_FORMATTED,
                message="lineNumber={lineNumber}, rowNumber={rowNumber}, columnNumber={columnNumber}, label={label}, validatedValue=${printer.print(validatedValue)}, min=${printer.print(min)}, inclusive={inclusive}")
        private Integer col_message_variables;
        
    }
    
    @CsvBean
    private static class ErrorCsv {
        
        @CsvColumn(number=1, label="カラム1")
        @CsvNumberFormat(pattern=TEST_FORMATTED_PATTERN)
        @CsvNumberMin(value="aaaa")
        private Integer col_value_wrong;
        
    }
    
    @Test
    public void testCreate_default() {
        
        FieldAccessor field = getFieldAccessor(TestCsv.class, "col_default", comparator);
        IntegerProcessorBuilder builder = (IntegerProcessorBuilder) builderResolver.resolve(Integer.class);
        TextFormatter<Integer> formatter = builder.getFormatter(field, config);
        
        CsvNumberMin anno = field.getAnnotationsByGroup(CsvNumberMin.class, groupEmpty).get(0);
        
        {
            //next null
            Optional<CellProcessor> processor = factory.create(anno, Optional.empty(), field, formatter, config);
            printCellProcessorChain(processor.get(), name.getMethodName());
            
            assertThat(processor.get()).isInstanceOf(NumberMin.class);
            
            NumberMin<Integer> actual = (NumberMin<Integer>)processor.get();
            assertThat(actual.getMin()).isEqualTo(TEST_VALUE_MIN_OBJ);
            assertThat(actual.isInclusive()).isEqualTo(true);
            assertThat(actual.getPrinter()).isEqualTo(formatter);
            
            {
                // valid input
                Integer input = TEST_VALUE_MIN_OBJ;
                assertThat((Object)actual.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(input);
            }
            
            {
                // wrong input
                Integer input = TEST_VALUE_MIN_OBJ - 1;
                assertThatThrownBy(() -> actual.execute(input, ANONYMOUS_CSVCONTEXT)).isInstanceOf(SuperCsvValidationException.class);
            }
            
            assertThat(actual.getValidationMessage()).isEqualTo("{com.github.mygreen.supercsv.annotation.constraint.CsvNumberMin.message}");
        }
        
        {
            //next exist
            Optional<CellProcessor> processor = factory.create(anno, Optional.of(new NextCellProcessor()), field, formatter, config);
            printCellProcessorChain(processor.get(), name.getMethodName());
            
            assertThat(processor.get()).isInstanceOf(NumberMin.class);
            
            NumberMin<Integer> actual = (NumberMin<Integer>)processor.get();
            assertThat(actual.getMin()).isEqualTo(TEST_VALUE_MIN_OBJ);
            assertThat(actual.isInclusive()).isEqualTo(true);
            assertThat(actual.getPrinter()).isEqualTo(formatter);
            
            {
                // valid input
                Integer input = TEST_VALUE_MIN_OBJ;
                assertThat((Object)actual.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(input);
            }
            
            {
                // wrong input
                Integer input = TEST_VALUE_MIN_OBJ - 1;
                assertThatThrownBy(() -> actual.execute(input, ANONYMOUS_CSVCONTEXT)).isInstanceOf(SuperCsvValidationException.class);
            }
            
            assertThat(actual.getValidationMessage()).isEqualTo("{com.github.mygreen.supercsv.annotation.constraint.CsvNumberMin.message}");
        }
        
    }
    
    /**
     * 属性 inclusive = false の場合
     */
    @Test
    public void testCreate_attrInclusive_false() {
        
        FieldAccessor field = getFieldAccessor(TestCsv.class, "col_inclusive_false", comparator);
        IntegerProcessorBuilder builder = (IntegerProcessorBuilder) builderResolver.resolve(Integer.class);
        TextFormatter<Integer> formatter = builder.getFormatter(field, config);
        
        CsvNumberMin anno = field.getAnnotationsByGroup(CsvNumberMin.class, groupEmpty).get(0);
        
        //next null
        Optional<CellProcessor> processor = factory.create(anno, Optional.empty(), field, formatter, config);
        printCellProcessorChain(processor.get(), name.getMethodName());
        
        assertThat(processor.get()).isInstanceOf(NumberMin.class);
        
        NumberMin<Integer> actual = (NumberMin<Integer>)processor.get();
        assertThat(actual.getMin()).isEqualTo(TEST_VALUE_MIN_OBJ);
        assertThat(actual.isInclusive()).isEqualTo(false);
        assertThat(actual.getPrinter()).isEqualTo(formatter);
        
        {
            // valid input
            Integer input = TEST_VALUE_MIN_OBJ - 1;
            assertThatThrownBy(() -> actual.execute(input, ANONYMOUS_CSVCONTEXT)).isInstanceOf(SuperCsvValidationException.class);
        }
        
        {
            // wrong input
            Integer input = TEST_VALUE_MIN_OBJ;
            assertThatThrownBy(() -> actual.execute(input, ANONYMOUS_CSVCONTEXT)).isInstanceOf(SuperCsvValidationException.class);
        }
        
        {
            // wrong input
            Integer input = TEST_VALUE_MIN_OBJ + 1;
            assertThat((Object)actual.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(input);
        }
        
    }
    
    /**
     * 属性valueの書式が不正な場合
     */
    @Test
    public void testCreate_attrValue_wrong() {
        
        FieldAccessor field = getFieldAccessor(ErrorCsv.class, "col_value_wrong", comparator);
        IntegerProcessorBuilder builder = (IntegerProcessorBuilder) builderResolver.resolve(Integer.class);
        TextFormatter<Integer> formatter = builder.getFormatter(field, config);
        
        CsvNumberMin anno = field.getAnnotationsByGroup(CsvNumberMin.class, groupEmpty).get(0);
        
        try {
            factory.create(anno, Optional.empty(), field, formatter, config);
            fail();
            
        } catch(Exception e) {
            assertThat(e).isInstanceOf(SuperCsvInvalidAnnotationException.class)
            .hasMessage("'%s' において、アノテーション @CsvNumberMin の属性 'value' の値（aaaa）は、java.lang.Integer (#,###)として不正です。", 
                    field.getNameWithClass());
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
        
        CsvNumberMin anno = field.getAnnotationsByGroup(CsvNumberMin.class, groupEmpty).get(0);
        
        Optional<CellProcessor> processor = factory.create(anno, Optional.empty(), field, formatter, config);
        printCellProcessorChain(processor.get(), name.getMethodName());
        
        assertThat(processor.get()).isInstanceOf(NumberMin.class);
        
        NumberMin<Integer> actual = (NumberMin<Integer>)processor.get();
        
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
        assertThat(processor).hasCellProcessor(NumberMin.class);
        
        String input = TEST_VALUE_MINUS1_STR_FORMATTED_1;
        try {
            processor.execute(input, testCsvContext(columnMapping, input));
            fail();
            
        } catch(Exception e) {
            
            assertThat(e).isInstanceOf(SuperCsvValidationException.class);
            
            List<String> messages = exceptionConverter.convertAndFormat((SuperCsvValidationException)e, beanMapping);
            assertThat(messages).hasSize(1)
                    .contains("[2行, 1列] : 項目「カラム1」の値（999）は、1,000以上の値でなければなりません。");
        }
        
    }
    
    /**
     * エラーメッセージのテスト - 属性inclusive=falseの場合
     */
    @Test
    public void testErrorMessage_attrInclusive_false() {
        
        BeanMapping<TestCsv> beanMapping = beanMappingFactory.create(TestCsv.class, groupEmpty);
        
        ColumnMapping columnMapping = beanMapping.getColumnMapping("col_inclusive_false").get();
        
        CellProcessor processor = columnMapping.getCellProcessorForReading();
        printCellProcessorChain(processor, name.getMethodName());
        assertThat(processor).hasCellProcessor(NumberMin.class);
        
        String input = TEST_VALUE_MINUS1_STR_FORMATTED_1;
        try {
            processor.execute(input, testCsvContext(columnMapping, input));
            fail();
            
        } catch(Exception e) {
            
            assertThat(e).isInstanceOf(SuperCsvValidationException.class);
            
            List<String> messages = exceptionConverter.convertAndFormat((SuperCsvValidationException)e, beanMapping);
            assertThat(messages).hasSize(1)
                    .contains("[2行, 2列] : 項目「col_inclusive_false」の値（999）は、1,000より大きい値でなければなりません。");
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
        assertThat(processor).hasCellProcessor(NumberMin.class);
        
        String input = TEST_VALUE_MINUS1_STR_FORMATTED_1;
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
        assertThat(processor).hasCellProcessor(NumberMin.class);
        
        String input = TEST_VALUE_MINUS1_STR_FORMATTED_1;
        try {
            processor.execute(input, testCsvContext(columnMapping, input));
            fail();
            
        } catch(Exception e) {
            
            assertThat(e).isInstanceOf(SuperCsvValidationException.class);
            
            List<String> messages = exceptionConverter.convertAndFormat((SuperCsvValidationException)e, beanMapping);
            assertThat(messages).hasSize(1)
                    .contains("[2行, 11列] : 項目「col_message_empty」の値（999）は、1,000以上の値でなければなりません。");
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
        assertThat(processor).hasCellProcessor(NumberMin.class);
        
        String input = TEST_VALUE_MINUS1_STR_FORMATTED_1;
        try {
            processor.execute(input, testCsvContext(columnMapping, input));
            fail();
            
        } catch(Exception e) {
            
            assertThat(e).isInstanceOf(SuperCsvValidationException.class);
            
            List<String> messages = exceptionConverter.convertAndFormat((SuperCsvValidationException)e, beanMapping);
            assertThat(messages).hasSize(1)
                    .contains("lineNumber=1, rowNumber=2, columnNumber=12, label=col_message_variables, validatedValue=999, min=1,000, inclusive=true");
        }
        
    }
    
    
}

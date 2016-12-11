package com.github.mygreen.supercsv.cellprocessor.constraint;

import static org.junit.Assert.*;
import static org.assertj.core.api.Assertions.*;
import static com.github.mygreen.supercsv.tool.TestUtils.*;
import static com.github.mygreen.supercsv.tool.HasCellProcessorAssert.*;

import java.lang.annotation.Annotation;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.supercsv.cellprocessor.ift.CellProcessor;

import com.github.mygreen.supercsv.annotation.CsvBean;
import com.github.mygreen.supercsv.annotation.CsvColumn;
import com.github.mygreen.supercsv.annotation.constraint.CsvDateTimeMax;
import com.github.mygreen.supercsv.annotation.format.CsvDateTimeFormat;
import com.github.mygreen.supercsv.builder.ProcessorBuilderResolver;
import com.github.mygreen.supercsv.builder.BeanMapping;
import com.github.mygreen.supercsv.builder.BeanMappingFactory;
import com.github.mygreen.supercsv.builder.BuildCase;
import com.github.mygreen.supercsv.builder.ColumnMapping;
import com.github.mygreen.supercsv.builder.Configuration;
import com.github.mygreen.supercsv.builder.FieldAccessor;
import com.github.mygreen.supercsv.builder.standard.DateProcessorBuilder;
import com.github.mygreen.supercsv.cellprocessor.format.TextFormatter;
import com.github.mygreen.supercsv.exception.SuperCsvInvalidAnnotationException;
import com.github.mygreen.supercsv.exception.SuperCsvValidationException;
import com.github.mygreen.supercsv.validation.CsvExceptionConverter;


/**
 * {@link DateTimeMaxFactory}のテスタ
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class DateTimeMaxFactoryTest {
    
    @Rule
    public TestName name = new TestName();
    
    private DateTimeMaxFactory<Date> factory;
    
    private Configuration config;
    private Comparator<Annotation> comparator;
    private ProcessorBuilderResolver builderResolver;
    
    private BeanMappingFactory beanMappingFactory;
    private CsvExceptionConverter exceptionConverter;
    
    private final Class<?>[] groupEmpty = new Class[]{};
    
    private static final Date TEST_VALUE_MAX_OBJ = toDate(2000, 1, 1);
    private static final String TEST_VALUE_MAX_FORMATTED = "2000/01/01";
    
    @CsvBean
    private static class TestCsv {
        
        @CsvColumn(number=1, label="カラム1")
        @CsvDateTimeFormat(pattern="yyyy/MM/dd")
        @CsvDateTimeMax(value=TEST_VALUE_MAX_FORMATTED)
        private Date col_default;
        
        @CsvColumn(number=2)
        @CsvDateTimeFormat(pattern="yyyy/MM/dd")
        @CsvDateTimeMax(value=TEST_VALUE_MAX_FORMATTED, inclusive=false)
        private Date col_inclusive_false;
        
        @CsvColumn(number=10)
        @CsvDateTimeFormat(pattern="yyyy/MM/dd")
        @CsvDateTimeMax(value=TEST_VALUE_MAX_FORMATTED, message="テストメッセージ")
        private Date col_message;
        
        @CsvColumn(number=11)
        @CsvDateTimeFormat(pattern="yyyy/MM/dd")
        @CsvDateTimeMax(value=TEST_VALUE_MAX_FORMATTED, message="")
        private Date col_message_empty;
        
        @CsvColumn(number=12)
        @CsvDateTimeFormat(pattern="yyyy/MM/dd")
        @CsvDateTimeMax(value=TEST_VALUE_MAX_FORMATTED,
                message="lineNumber={lineNumber}, rowNumber={rowNumber}, columnNumber={columnNumber}, label={label}, validatedValue=${printer.print(validatedValue)}, max=${printer.print(max)}, inclusive={inclusive}")
        private Date col_message_variables;
        
    }
    
    @CsvBean
    private static class ErrorCsv {
        
        @CsvColumn(number=1, label="カラム1")
        @CsvDateTimeFormat(pattern="yyyy/MM/dd")
        @CsvDateTimeMax(value="aaaa")
        private Date col_value_wrong;
        
    }
    
    @Before
    public void setUp() throws Exception {
        this.factory = new DateTimeMaxFactory<Date>();
        
        this.config = new Configuration();
        this.beanMappingFactory = new BeanMappingFactory();
        beanMappingFactory.setConfiguration(config);
        
        this.exceptionConverter = new CsvExceptionConverter();
        
        this.comparator = config.getAnnoationComparator();
        this.builderResolver = config.getBuilderResolver();
    }
    
    @Test
    public void testCreate_default() {
        
        FieldAccessor field = getFieldAccessor(TestCsv.class, "col_default", comparator);
        DateProcessorBuilder builder = (DateProcessorBuilder) builderResolver.resolve(Date.class);
        TextFormatter<Date> formatter = builder.getFormatter(field, config);
        
        CsvDateTimeMax anno = field.getAnnotationsByGroup(CsvDateTimeMax.class, groupEmpty).get(0);
        
        {
            //next null
            Optional<CellProcessor> processor = factory.create(anno, Optional.empty(), field, formatter, config);
            printCellProcessorChain(processor.get(), name.getMethodName());
            
            assertThat(processor.get()).isInstanceOf(DateTimeMax.class);
            
            DateTimeMax<Date> actual = (DateTimeMax<Date>)processor.get();
            assertThat(actual.getMax()).isEqualTo(TEST_VALUE_MAX_OBJ);
            assertThat(actual.isInclusive()).isEqualTo(true);
            assertThat(actual.getPrinter()).isEqualTo(formatter);
            
            {
                // valid input
                Date input = TEST_VALUE_MAX_OBJ;
                assertThat((Object)actual.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(input);
            }
            
            {
                // wrong input
                Date input = plusDays(TEST_VALUE_MAX_OBJ, 1);
                assertThatThrownBy(() -> actual.execute(input, ANONYMOUS_CSVCONTEXT)).isInstanceOf(SuperCsvValidationException.class);
            }
            
            assertThat(actual.getValidationMessage()).isEqualTo("{com.github.mygreen.supercsv.annotation.constraint.CsvDateTimeMax.message}");
        }
        
        {
            //next exist
            Optional<CellProcessor> processor = factory.create(anno, Optional.of(new NextCellProcessor()), field, formatter, config);
            printCellProcessorChain(processor.get(), name.getMethodName());
            
            assertThat(processor.get()).isInstanceOf(DateTimeMax.class);
            
            DateTimeMax<Date> actual = (DateTimeMax<Date>)processor.get();
            assertThat(actual.getMax()).isEqualTo(TEST_VALUE_MAX_OBJ);
            assertThat(actual.isInclusive()).isEqualTo(true);
            assertThat(actual.getPrinter()).isEqualTo(formatter);
            
            {
                // valid input
                Date input = TEST_VALUE_MAX_OBJ;
                assertThat((Object)actual.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(input);
            }
            
            {
                // wrong input
                Date input = plusDays(TEST_VALUE_MAX_OBJ, 1);
                assertThatThrownBy(() -> actual.execute(input, ANONYMOUS_CSVCONTEXT)).isInstanceOf(SuperCsvValidationException.class);
            }
            
            assertThat(actual.getValidationMessage()).isEqualTo("{com.github.mygreen.supercsv.annotation.constraint.CsvDateTimeMax.message}");
        }
        
    }
    
    /**
     * 属性 inclusive = false の場合
     */
    @Test
    public void testCreate_attrInclusive_false() {
        
        FieldAccessor field = getFieldAccessor(TestCsv.class, "col_inclusive_false", comparator);
        DateProcessorBuilder builder = (DateProcessorBuilder) builderResolver.resolve(Date.class);
        TextFormatter<Date> formatter = builder.getFormatter(field, config);
        
        CsvDateTimeMax anno = field.getAnnotationsByGroup(CsvDateTimeMax.class, groupEmpty).get(0);
        
        //next null
        Optional<CellProcessor> processor = factory.create(anno, Optional.empty(), field, formatter, config);
        printCellProcessorChain(processor.get(), name.getMethodName());
        
        assertThat(processor.get()).isInstanceOf(DateTimeMax.class);
        
        DateTimeMax<Date> actual = (DateTimeMax<Date>)processor.get();
        assertThat(actual.getMax()).isEqualTo(TEST_VALUE_MAX_OBJ);
        assertThat(actual.isInclusive()).isEqualTo(false);
        assertThat(actual.getPrinter()).isEqualTo(formatter);
        
        {
            // valid input
            Date input = plusDays(TEST_VALUE_MAX_OBJ, 1);
            assertThatThrownBy(() -> actual.execute(input, ANONYMOUS_CSVCONTEXT)).isInstanceOf(SuperCsvValidationException.class);
        }
        
        {
            // wrong input
            Date input = TEST_VALUE_MAX_OBJ;
            assertThatThrownBy(() -> actual.execute(input, ANONYMOUS_CSVCONTEXT)).isInstanceOf(SuperCsvValidationException.class);
        }
        
        {
            // wrong input
            Date input = minusDays(TEST_VALUE_MAX_OBJ, 1);
            assertThat((Object)actual.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(input);
        }
        
    }
    
    /**
     * 属性valueの書式が不正な場合
     */
    @Test
    public void testCreate_attrValue_wrong() {
        
        FieldAccessor field = getFieldAccessor(ErrorCsv.class, "col_value_wrong", comparator);
        DateProcessorBuilder builder = (DateProcessorBuilder) builderResolver.resolve(Date.class);
        TextFormatter<Date> formatter = builder.getFormatter(field, config);
        
        CsvDateTimeMax anno = field.getAnnotationsByGroup(CsvDateTimeMax.class, groupEmpty).get(0);
        
        try {
            factory.create(anno, Optional.empty(), field, formatter, config);
            fail();
            
        } catch(Exception e) {
            assertThat(e).isInstanceOf(SuperCsvInvalidAnnotationException.class)
            .hasMessage("'%s' において、アノテーション @CsvDateTimeMax の属性 'value' の値（aaaa）は、java.util.Date (yyyy/MM/dd)として不正です。", 
                    field.getNameWithClass());
        }
        
    }
    
    /**
     * 属性messageのテスト
     */
    @Test
    public void testCreate_attrMessage() {
        
        FieldAccessor field = getFieldAccessor(TestCsv.class, "col_message", comparator);
        DateProcessorBuilder builder = (DateProcessorBuilder) builderResolver.resolve(Date.class);
        TextFormatter<Date> formatter = builder.getFormatter(field, config);
        
        CsvDateTimeMax anno = field.getAnnotationsByGroup(CsvDateTimeMax.class, groupEmpty).get(0);
        
        Optional<CellProcessor> processor = factory.create(anno, Optional.empty(), field, formatter, config);
        printCellProcessorChain(processor.get(), name.getMethodName());
        
        assertThat(processor.get()).isInstanceOf(DateTimeMax.class);
        
        DateTimeMax<Date> actual = (DateTimeMax<Date>)processor.get();
        
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
        assertThat(processor).hasCellProcessor(DateTimeMax.class);
        
        String input = "2000/01/02";
        try {
            processor.execute(input, testCsvContext(columnMapping, input));
            fail();
            
        } catch(Exception e) {
            
            assertThat(e).isInstanceOf(SuperCsvValidationException.class);
            
            List<String> messages = exceptionConverter.convertAndFormat((SuperCsvValidationException)e, beanMapping);
            assertThat(messages).hasSize(1)
                    .contains("[2行, 1列] : 項目「カラム1」の値（2000/01/02）は、2000/01/01以前の値でなければなりません。");
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
        assertThat(processor).hasCellProcessor(DateTimeMax.class);
        
        String input = "2000/01/02";
        try {
            processor.execute(input, testCsvContext(columnMapping, input));
            fail();
            
        } catch(Exception e) {
            
            assertThat(e).isInstanceOf(SuperCsvValidationException.class);
            
            List<String> messages = exceptionConverter.convertAndFormat((SuperCsvValidationException)e, beanMapping);
            assertThat(messages).hasSize(1)
                    .contains("[2行, 2列] : 項目「col_inclusive_false」の値（2000/01/02）は、2000/01/01より前の値でなければなりません。");
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
        assertThat(processor).hasCellProcessor(DateTimeMax.class);
        
        String input = "2000/01/02";
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
        assertThat(processor).hasCellProcessor(DateTimeMax.class);
        
        String input = "2000/01/02";
        try {
            processor.execute(input, testCsvContext(columnMapping, input));
            fail();
            
        } catch(Exception e) {
            
            assertThat(e).isInstanceOf(SuperCsvValidationException.class);
            
            List<String> messages = exceptionConverter.convertAndFormat((SuperCsvValidationException)e, beanMapping);
            assertThat(messages).hasSize(1)
                    .contains("[2行, 11列] : 項目「col_message_empty」の値（2000/01/02）は、2000/01/01以前の値でなければなりません。");
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
        assertThat(processor).hasCellProcessor(DateTimeMax.class);
        
        String input = "2000/01/02";
        try {
            processor.execute(input, testCsvContext(columnMapping, input));
            fail();
            
        } catch(Exception e) {
            
            assertThat(e).isInstanceOf(SuperCsvValidationException.class);
            
            List<String> messages = exceptionConverter.convertAndFormat((SuperCsvValidationException)e, beanMapping);
            assertThat(messages).hasSize(1)
                    .contains("lineNumber=1, rowNumber=2, columnNumber=12, label=col_message_variables, validatedValue=2000/01/02, max=2000/01/01, inclusive=true");
        }
        
    }
    
}

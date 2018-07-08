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
import com.github.mygreen.supercsv.annotation.constraint.CsvDateTimeRange;
import com.github.mygreen.supercsv.annotation.format.CsvDateTimeFormat;
import com.github.mygreen.supercsv.builder.ProcessorBuilderResolver;
import com.github.mygreen.supercsv.builder.BeanMapping;
import com.github.mygreen.supercsv.builder.BeanMappingFactory;
import com.github.mygreen.supercsv.builder.ColumnMapping;
import com.github.mygreen.supercsv.builder.Configuration;
import com.github.mygreen.supercsv.builder.FieldAccessor;
import com.github.mygreen.supercsv.builder.standard.DateProcessorBuilder;
import com.github.mygreen.supercsv.cellprocessor.format.TextFormatter;
import com.github.mygreen.supercsv.exception.SuperCsvInvalidAnnotationException;
import com.github.mygreen.supercsv.exception.SuperCsvValidationException;
import com.github.mygreen.supercsv.validation.CsvExceptionConverter;


/**
 * {@link DateTimeRangeFactory}のテスタ
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class DateTimeRangeFactoryTest {
    
    @Rule
    public TestName name = new TestName();
    
    private DateTimeRangeFactory<Date> factory;
    
    private Configuration config;
    private Comparator<Annotation> comparator;
    private ProcessorBuilderResolver builderResolver;
    
    private BeanMappingFactory beanMappingFactory;
    private CsvExceptionConverter exceptionConverter;
    
    private final Class<?>[] groupEmpty = new Class[]{};
    
    @Before
    public void setUp() throws Exception {
        this.factory = new DateTimeRangeFactory<Date>();
        
        this.config = new Configuration();
        this.beanMappingFactory = new BeanMappingFactory();
        beanMappingFactory.setConfiguration(config);
        
        this.exceptionConverter = new CsvExceptionConverter();
        
        this.comparator = config.getAnnoationComparator();
        this.builderResolver = config.getBuilderResolver();
    }
    
    private static final Date TEST_VALUE_MIN_OBJ = toDate(2000, 1, 1);
    private static final String TEST_VALUE_MIN_FORMATTED = "2000/01/01";
    
    private static final Date TEST_VALUE_MAX_OBJ = toDate(2000, 1, 5);
    private static final String TEST_VALUE_MAX_FORMATTED = "2000/01/05";
    
    @CsvBean
    private static class TestCsv {
        
        @CsvColumn(number=1, label="カラム1")
        @CsvDateTimeFormat(pattern="yyyy/MM/dd")
        @CsvDateTimeRange(min=TEST_VALUE_MIN_FORMATTED, max=TEST_VALUE_MAX_FORMATTED)
        private Date col_default;
        
        @CsvColumn(number=2)
        @CsvDateTimeFormat(pattern="yyyy/MM/dd")
        @CsvDateTimeRange(min=TEST_VALUE_MIN_FORMATTED, max=TEST_VALUE_MAX_FORMATTED, inclusive=false)
        private Date col_inclusive_false;
        
        @CsvColumn(number=10)
        @CsvDateTimeFormat(pattern="yyyy/MM/dd")
        @CsvDateTimeRange(min=TEST_VALUE_MIN_FORMATTED, max=TEST_VALUE_MAX_FORMATTED, message="テストメッセージ")
        private Date col_message;
        
        @CsvColumn(number=11)
        @CsvDateTimeFormat(pattern="yyyy/MM/dd")
        @CsvDateTimeRange(min=TEST_VALUE_MIN_FORMATTED, max=TEST_VALUE_MAX_FORMATTED, message="")
        private Date col_message_empty;
        
        @CsvColumn(number=12)
        @CsvDateTimeFormat(pattern="yyyy/MM/dd")
        @CsvDateTimeRange(min=TEST_VALUE_MIN_FORMATTED, max=TEST_VALUE_MAX_FORMATTED,
                message="lineNumber={lineNumber}, rowNumber={rowNumber}, columnNumber={columnNumber}, label={label}, validatedValue=${printer.print(validatedValue)}, min=${printer.print(min)}, max=${printer.print(max)}, inclusive={inclusive}")
        private Date col_message_variables;
        
    }
    
    @CsvBean
    private static class ErrorCsv {
        
        @CsvColumn(number=1, label="カラム1")
        @CsvDateTimeFormat(pattern="yyyy/MM/dd")
        @CsvDateTimeRange(min="aaaa", max=TEST_VALUE_MAX_FORMATTED)
        private Date col_min_wrong;
        
        @CsvColumn(number=2, label="カラム2")
        @CsvDateTimeFormat(pattern="yyyy/MM/dd")
        @CsvDateTimeRange(min=TEST_VALUE_MIN_FORMATTED, max="aaaa")
        private Date col_max_wrong;
        
        @CsvColumn(number=3, label="カラム3")
        @CsvDateTimeFormat(pattern="yyyy/MM/dd")
        @CsvDateTimeRange(min=TEST_VALUE_MAX_FORMATTED, max=TEST_VALUE_MIN_FORMATTED)
        private Date col_min_max_wrong;
        
    }
    
    @Test
    public void testCreate_default() {
        
        FieldAccessor field = getFieldAccessor(TestCsv.class, "col_default", comparator);
        DateProcessorBuilder builder = (DateProcessorBuilder) builderResolver.resolve(Date.class);
        TextFormatter<Date> formatter = builder.getFormatter(field, config);
        
        CsvDateTimeRange anno = field.getAnnotationsByGroup(CsvDateTimeRange.class, groupEmpty).get(0);
        
        {
            //next null
            Optional<CellProcessor> processor = factory.create(anno, Optional.empty(), field, formatter, config);
            printCellProcessorChain(processor.get(), name.getMethodName());
            
            assertThat(processor.get()).isInstanceOf(DateTimeRange.class);
            
            DateTimeRange<Date> actual = (DateTimeRange<Date>)processor.get();
            assertThat(actual.getMin()).isEqualTo(TEST_VALUE_MIN_OBJ);
            assertThat(actual.getMax()).isEqualTo(TEST_VALUE_MAX_OBJ);
            assertThat(actual.isInclusive()).isEqualTo(true);
            assertThat(actual.getPrinter()).isEqualTo(formatter);
            
            {
                // valid input
                Date input = TEST_VALUE_MIN_OBJ;
                assertThat((Object)actual.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(input);
            }
            
            {
                // wrong input
                Date input = minusDays(TEST_VALUE_MIN_OBJ, 1);
                assertThatThrownBy(() -> actual.execute(input, ANONYMOUS_CSVCONTEXT)).isInstanceOf(SuperCsvValidationException.class);
            }
            
            assertThat(actual.getValidationMessage()).isEqualTo("{com.github.mygreen.supercsv.annotation.constraint.CsvDateTimeRange.message}");
        }
        
        {
            //next exist
            Optional<CellProcessor> processor = factory.create(anno, Optional.of(new NextCellProcessor()), field, formatter, config);
            printCellProcessorChain(processor.get(), name.getMethodName());
            
            assertThat(processor.get()).isInstanceOf(DateTimeRange.class);
            
            DateTimeRange<Date> actual = (DateTimeRange<Date>)processor.get();
            assertThat(actual.getMin()).isEqualTo(TEST_VALUE_MIN_OBJ);
            assertThat(actual.getMax()).isEqualTo(TEST_VALUE_MAX_OBJ);
            assertThat(actual.isInclusive()).isEqualTo(true);
            assertThat(actual.getPrinter()).isEqualTo(formatter);
            
            {
                // valid input
                Date input = TEST_VALUE_MIN_OBJ;
                assertThat((Object)actual.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(input);
            }
            
            {
                // wrong input
                Date input = minusDays(TEST_VALUE_MIN_OBJ, 1);
                assertThatThrownBy(() -> actual.execute(input, ANONYMOUS_CSVCONTEXT)).isInstanceOf(SuperCsvValidationException.class);
            }
            
            assertThat(actual.getValidationMessage()).isEqualTo("{com.github.mygreen.supercsv.annotation.constraint.CsvDateTimeRange.message}");
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
        
        CsvDateTimeRange anno = field.getAnnotationsByGroup(CsvDateTimeRange.class, groupEmpty).get(0);
        
        //next null
        Optional<CellProcessor> processor = factory.create(anno, Optional.empty(), field, formatter, config);
        printCellProcessorChain(processor.get(), name.getMethodName());
        
        assertThat(processor.get()).isInstanceOf(DateTimeRange.class);
        
        DateTimeRange<Date> actual = (DateTimeRange<Date>)processor.get();
        assertThat(actual.getMin()).isEqualTo(TEST_VALUE_MIN_OBJ);
        assertThat(actual.getMax()).isEqualTo(TEST_VALUE_MAX_OBJ);
        assertThat(actual.isInclusive()).isEqualTo(false);
        assertThat(actual.getPrinter()).isEqualTo(formatter);
        
        {
            // valid input
            Date input = minusDays(TEST_VALUE_MIN_OBJ, 1);
            assertThatThrownBy(() -> actual.execute(input, ANONYMOUS_CSVCONTEXT)).isInstanceOf(SuperCsvValidationException.class);
        }
        
        {
            // wrong input
            Date input = TEST_VALUE_MIN_OBJ;
            assertThatThrownBy(() -> actual.execute(input, ANONYMOUS_CSVCONTEXT)).isInstanceOf(SuperCsvValidationException.class);
        }
        
        {
            // wrong input
            Date input = plusDays(TEST_VALUE_MIN_OBJ, 1);
            assertThat((Object)actual.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(input);
        }
        
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
     * 属性messageのテスト
     */
    @Test
    public void testCreate_attrMessage() {
        
        FieldAccessor field = getFieldAccessor(TestCsv.class, "col_message", comparator);
        DateProcessorBuilder builder = (DateProcessorBuilder) builderResolver.resolve(Date.class);
        TextFormatter<Date> formatter = builder.getFormatter(field, config);
        
        CsvDateTimeRange anno = field.getAnnotationsByGroup(CsvDateTimeRange.class, groupEmpty).get(0);
        
        Optional<CellProcessor> processor = factory.create(anno, Optional.empty(), field, formatter, config);
        printCellProcessorChain(processor.get(), name.getMethodName());
        
        assertThat(processor.get()).isInstanceOf(DateTimeRange.class);
        
        DateTimeRange<Date> actual = (DateTimeRange<Date>)processor.get();
        
        assertThat(actual.getValidationMessage()).isEqualTo("テストメッセージ");
        
    }
    
    /**
     * 属性minの書式が不正な場合
     */
    @Test
    public void testCreate_attrMin_wrong() {
        
        FieldAccessor field = getFieldAccessor(ErrorCsv.class, "col_min_wrong", comparator);
        DateProcessorBuilder builder = (DateProcessorBuilder) builderResolver.resolve(Date.class);
        TextFormatter<Date> formatter = builder.getFormatter(field, config);
        
        CsvDateTimeRange anno = field.getAnnotationsByGroup(CsvDateTimeRange.class, groupEmpty).get(0);
        
        try {
            factory.create(anno, Optional.empty(), field, formatter, config);
            fail();
            
        } catch(Exception e) {
            assertThat(e).isInstanceOf(SuperCsvInvalidAnnotationException.class)
            .hasMessage("'%s' において、アノテーション @CsvDateTimeRange の属性 'min' の値（aaaa）は、java.util.Date (yyyy/MM/dd)として不正です。", 
                    field.getNameWithClass());
        }
        
    }
    
    /**
     * 属性maxの書式が不正な場合
     */
    @Test
    public void testCreate_attrMax_wrong() {
        
        FieldAccessor field = getFieldAccessor(ErrorCsv.class, "col_max_wrong", comparator);
        DateProcessorBuilder builder = (DateProcessorBuilder) builderResolver.resolve(Date.class);
        TextFormatter<Date> formatter = builder.getFormatter(field, config);
        
        CsvDateTimeRange anno = field.getAnnotationsByGroup(CsvDateTimeRange.class, groupEmpty).get(0);
        
        try {
            factory.create(anno, Optional.empty(), field, formatter, config);
            fail();
            
        } catch(Exception e) {
            assertThat(e).isInstanceOf(SuperCsvInvalidAnnotationException.class)
            .hasMessage("'%s' において、アノテーション @CsvDateTimeRange の属性 'max' の値（aaaa）は、java.util.Date (yyyy/MM/dd)として不正です。", 
                    field.getNameWithClass());
        }
        
    }
    
    /**
     * 属性minの値がmaxよりも大きい場合
     */
    @Test
    public void testCreate_attrMinMax_wrong() {
        
        FieldAccessor field = getFieldAccessor(ErrorCsv.class, "col_min_max_wrong", comparator);
        DateProcessorBuilder builder = (DateProcessorBuilder) builderResolver.resolve(Date.class);
        TextFormatter<Date> formatter = builder.getFormatter(field, config);
        
        CsvDateTimeRange anno = field.getAnnotationsByGroup(CsvDateTimeRange.class, groupEmpty).get(0);
        
        try {
            factory.create(anno, Optional.empty(), field, formatter, config);
            fail();
            
        } catch(Exception e) {
            assertThat(e).isInstanceOf(SuperCsvInvalidAnnotationException.class)
            .hasMessage("'%s' において、アノテーション @CsvDateTimeRange の属性 'min' の値（2000/01/05）は、属性 'max' の値（2000/01/01）より以前の値で設定してください。", 
                    field.getNameWithClass());
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
        assertThat(processor).hasCellProcessor(DateTimeRange.class);
        
        String input = "1999/12/31";
        try {
            processor.execute(input, testCsvContext(columnMapping, input));
            fail();
            
        } catch(Exception e) {
            
            assertThat(e).isInstanceOf(SuperCsvValidationException.class);
            
            List<String> messages = exceptionConverter.convertAndFormat((SuperCsvValidationException)e, beanMapping);
            assertThat(messages).hasSize(1)
                    .contains("[2行, 1列] : 項目「カラム1」の値（1999/12/31）は、2000/01/01～2000/01/05 の期間内でなければなりません。");
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
        assertThat(processor).hasCellProcessor(DateTimeRange.class);
        
        String input = "1999/12/31";
        try {
            processor.execute(input, testCsvContext(columnMapping, input));
            fail();
            
        } catch(Exception e) {
            
            assertThat(e).isInstanceOf(SuperCsvValidationException.class);
            
            List<String> messages = exceptionConverter.convertAndFormat((SuperCsvValidationException)e, beanMapping);
            assertThat(messages).hasSize(1)
                    .contains("[2行, 2列] : 項目「col_inclusive_false」の値（1999/12/31）は、2000/01/01～2000/01/05 の期間内でなければなりません。");
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
        assertThat(processor).hasCellProcessor(DateTimeRange.class);
        
        String input = "1999/12/31";
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
        assertThat(processor).hasCellProcessor(DateTimeRange.class);
        
        String input = "1999/12/31";
        try {
            processor.execute(input, testCsvContext(columnMapping, input));
            fail();
            
        } catch(Exception e) {
            
            assertThat(e).isInstanceOf(SuperCsvValidationException.class);
            
            List<String> messages = exceptionConverter.convertAndFormat((SuperCsvValidationException)e, beanMapping);
            assertThat(messages).hasSize(1)
                    .contains("[2行, 11列] : 項目「col_message_empty」の値（1999/12/31）は、2000/01/01～2000/01/05 の期間内でなければなりません。");
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
        assertThat(processor).hasCellProcessor(DateTimeRange.class);
        
        String input = "1999/12/31";
        try {
            processor.execute(input, testCsvContext(columnMapping, input));
            fail();
            
        } catch(Exception e) {
            
            assertThat(e).isInstanceOf(SuperCsvValidationException.class);
            
            List<String> messages = exceptionConverter.convertAndFormat((SuperCsvValidationException)e, beanMapping);
            assertThat(messages).hasSize(1)
                    .contains("lineNumber=1, rowNumber=2, columnNumber=12, label=col_message_variables, validatedValue=1999/12/31, min=2000/01/01, max=2000/01/05, inclusive=true");
        }
        
    }
    
}

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
import org.supercsv.util.CsvContext;

import com.github.mygreen.supercsv.annotation.CsvBean;
import com.github.mygreen.supercsv.annotation.CsvColumn;
import com.github.mygreen.supercsv.annotation.constraint.CsvUnique;
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
import com.github.mygreen.supercsv.exception.SuperCsvValidationException;
import com.github.mygreen.supercsv.validation.CsvExceptionConverter;


/**
 * {@link UniqueFactory}のテスタ
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class UniqueFactoryTest {
    
    @Rule
    public TestName name = new TestName();
    
    private UniqueFactory<Integer> factory;
    
    private Configuration config;
    private Comparator<Annotation> comparator;
    private ProcessorBuilderResolver builderResolver;
    
    private BeanMappingFactory beanMappingFactory;
    private CsvExceptionConverter exceptionConverter;
        
    private final Class<?>[] groupEmpty = new Class[]{};
    
    private CsvContext ANONYMOUS_CSVCONTEXT2 = new CsvContext(2, 3, 3);
    
    @Before
    public void setUp() throws Exception {
        this.factory = new UniqueFactory<Integer>();
        
        this.config = new Configuration();
        this.beanMappingFactory = new BeanMappingFactory();
        beanMappingFactory.setConfiguration(config);
        
        this.exceptionConverter = new CsvExceptionConverter();
        
        this.comparator = config.getAnnoationComparator();
        this.builderResolver = config.getBuilderResolver();
    }
    
    private static final Integer TEST_VALUE_OBJ = 1000;
    private static final String TEST_VALUE_STR = "1000";
    private static final String TEST_VALUE_STR_FORMATTED = "1,000";
    
    @CsvBean
    private static class TestCsv {
        
        @CsvColumn(number=1, label="カラム1")
        @CsvNumberFormat(pattern="#,###")
        @CsvUnique
        private Integer col_default;
        
        @CsvColumn(number=10)
        @CsvNumberFormat(pattern="#,###")
        @CsvUnique(message="テストメッセージ")
        private Integer col_message;
        
        @CsvColumn(number=11)
        @CsvNumberFormat(pattern="#,###")
        @CsvUnique(message="")
        private Integer col_message_empty;
        
        @CsvColumn(number=12)
        @CsvNumberFormat(pattern="#,###")
        @CsvUnique(message="lineNumber={lineNumber}, rowNumber={rowNumber}, columnNumber={columnNumber}, label={label}, validatedValue=${printer.print(validatedValue)}, duplicatedLineNumber={duplicatedLineNumber}, duplicatedRowNumber={duplicatedRowNumber}")
        private Integer col_message_variables;
        
    }
    
    @Test
    public void testCreate_default() {
        
        FieldAccessor field = getFieldAccessor(TestCsv.class, "col_default", comparator);
        IntegerProcessorBuilder builder = (IntegerProcessorBuilder) builderResolver.resolve(Integer.class);
        TextFormatter<Integer> formatter = builder.getFormatter(field, config);
        
        CsvUnique anno = field.getAnnotationsByGroup(CsvUnique.class, groupEmpty).get(0);
        
        {
            //next null
            Optional<CellProcessor> processor = factory.create(anno, Optional.empty(), field, formatter, config);
            printCellProcessorChain(processor.get(), name.getMethodName());
            
            assertThat(processor.get()).isInstanceOf(Unique.class);
            
            Unique<Integer> actual = (Unique<Integer>)processor.get();
            assertThat(actual.getPrinter()).isEqualTo(formatter);
            
            {
                // valid input
                Integer input = TEST_VALUE_OBJ;
                assertThat((Object)actual.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(input);
            }
            
            {
                // wrong input
                Integer input = TEST_VALUE_OBJ;
                assertThatThrownBy(() -> actual.execute(input, ANONYMOUS_CSVCONTEXT2)).isInstanceOf(SuperCsvValidationException.class);
            }
            
            assertThat(actual.getValidationMessage()).isEqualTo("{com.github.mygreen.supercsv.annotation.constraint.CsvUnique.message}");
        }
        
        {
            //next exist
            Optional<CellProcessor> processor = factory.create(anno, Optional.of(new NextCellProcessor()), field, formatter, config);
            printCellProcessorChain(processor.get(), name.getMethodName());
            
            assertThat(processor.get()).isInstanceOf(Unique.class);
            
            Unique<Integer> actual = (Unique<Integer>)processor.get();
            assertThat(actual.getPrinter()).isEqualTo(formatter);
            
            {
                // valid input
                Integer input = TEST_VALUE_OBJ;
                assertThat((Object)actual.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(input);
            }
            
            {
                // wrong input
                Integer input = TEST_VALUE_OBJ;
                assertThatThrownBy(() -> actual.execute(input, ANONYMOUS_CSVCONTEXT2)).isInstanceOf(SuperCsvValidationException.class);
            }
            
            assertThat(actual.getValidationMessage()).isEqualTo("{com.github.mygreen.supercsv.annotation.constraint.CsvUnique.message}");
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
        
        CsvUnique anno = field.getAnnotationsByGroup(CsvUnique.class, groupEmpty).get(0);
        
        Optional<CellProcessor> processor = factory.create(anno, Optional.empty(), field, formatter, config);
        printCellProcessorChain(processor.get(), name.getMethodName());
        
        assertThat(processor.get()).isInstanceOf(Unique.class);
        
        Unique<Integer> actual = (Unique<Integer>)processor.get();
        
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
        assertThat(processor).hasCellProcessor(Unique.class);
        
        String input = TEST_VALUE_STR_FORMATTED;
        
        processor.execute(input, testCsvContext(columnMapping, input));
        
        try {
            processor.execute(input, testCsvContext(columnMapping, input, 2, 3));
            fail();
            
        } catch(Exception e) {
            
            assertThat(e).isInstanceOf(SuperCsvValidationException.class);
            
            List<String> messages = exceptionConverter.convertAndFormat((SuperCsvValidationException)e, beanMapping);
            assertThat(messages).hasSize(1)
                    .contains("[3行, 1列] : 項目「カラム1」の値（1,000）は、2行目の値と重複しています。");
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
        assertThat(processor).hasCellProcessor(Unique.class);
        
        String input = TEST_VALUE_STR_FORMATTED;
        processor.execute(input, testCsvContext(columnMapping, input));
        
        try {
            processor.execute(input, testCsvContext(columnMapping, input, 2, 3));
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
        assertThat(processor).hasCellProcessor(Unique.class);
        
        String input = TEST_VALUE_STR_FORMATTED;
        processor.execute(input, testCsvContext(columnMapping, input));
        
        try {
            processor.execute(input, testCsvContext(columnMapping, input, 2, 3));
            fail();
            
        } catch(Exception e) {
            
            assertThat(e).isInstanceOf(SuperCsvValidationException.class);
            
            List<String> messages = exceptionConverter.convertAndFormat((SuperCsvValidationException)e, beanMapping);
            assertThat(messages).hasSize(1)
                    .contains("[3行, 11列] : 項目「col_message_empty」の値（1,000）は、2行目の値と重複しています。");
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
        assertThat(processor).hasCellProcessor(Unique.class);
        
        String input = TEST_VALUE_STR_FORMATTED;
        processor.execute(input, testCsvContext(columnMapping, input));
        
        try {
            processor.execute(input, testCsvContext(columnMapping, input, 2, 3));
            fail();
            
        } catch(Exception e) {
            
            assertThat(e).isInstanceOf(SuperCsvValidationException.class);
            
            List<String> messages = exceptionConverter.convertAndFormat((SuperCsvValidationException)e, beanMapping);
            assertThat(messages).hasSize(1)
                    .contains("lineNumber=2, rowNumber=3, columnNumber=12, label=col_message_variables, validatedValue=1,000, duplicatedLineNumber=1, duplicatedRowNumber=2");
        }
        
    }
    
    
}

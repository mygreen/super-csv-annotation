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
import com.github.mygreen.supercsv.annotation.constraint.CsvRequire;
import com.github.mygreen.supercsv.builder.ProcessorBuilderResolver;
import com.github.mygreen.supercsv.builder.BeanMapping;
import com.github.mygreen.supercsv.builder.BeanMappingFactory;
import com.github.mygreen.supercsv.builder.BuildCase;
import com.github.mygreen.supercsv.builder.ColumnMapping;
import com.github.mygreen.supercsv.builder.Configuration;
import com.github.mygreen.supercsv.builder.FieldAccessor;
import com.github.mygreen.supercsv.builder.standard.StringProcessorBuilder;
import com.github.mygreen.supercsv.cellprocessor.format.TextFormatter;
import com.github.mygreen.supercsv.exception.SuperCsvValidationException;
import com.github.mygreen.supercsv.validation.CsvExceptionConverter;

/**
 * {@link RequireFactory}のテスタ
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class RequireFactoryTest {
    
    @Rule
    public TestName name = new TestName();
    
    private RequireFactory factory;
    
    private Configuration config;
    private Comparator<Annotation> comparator;
    private ProcessorBuilderResolver builderResolver;
    
    private BeanMappingFactory beanMappingFactory;
    
    private final Class<?>[] groupEmpty = new Class[]{};
    
    private final CsvExceptionConverter exceptionConverter = new CsvExceptionConverter();
    
    @Before
    public void setUp() throws Exception {
        this.factory = new RequireFactory();
        
        this.config = new Configuration();
        this.beanMappingFactory = new BeanMappingFactory();
        beanMappingFactory.setConfiguration(config);
        
        this.comparator = config.getAnnoationComparator();
        this.builderResolver = config.getBuilderResolver();
    }
    
    @CsvBean
    private static class TestCsv {
        
        @CsvColumn(number=1, label="カラム1")
        @CsvRequire
        private String col_default;
        
        @CsvColumn(number=2)
        @CsvRequire(considerEmpty=true)
        private String col_empty;
        
        @CsvColumn(number=3)
        @CsvRequire(considerBlank=true)
        private String col_blank;
        
        @CsvColumn(number=10)
        @CsvRequire(message="テストメッセージ")
        private String col_message;
        
        @CsvColumn(number=11)
        @CsvRequire(message="")
        private String col_message_empty;
        
        @CsvColumn(number=12)
        @CsvRequire(considerEmpty=false, considerBlank=true,
                message="lineNumber={lineNumber}, rowNumber={rowNumber}, columnNumber={columnNumber}, label={label}, validatedValue={validatedValue}, considerEmpty={considerEmpty}, considerBlank={considerBlank}")
        private String col_message_variables;
        
    }
    
    /**
     * 標準設定
     */
    @Test
    public void testCreate_default() {
        
        FieldAccessor field = getFieldAccessor(TestCsv.class, "col_default", comparator);
        StringProcessorBuilder builder = (StringProcessorBuilder) builderResolver.resolve(String.class);
        TextFormatter<String> formatter = builder.getFormatter(field, config);
        
        CsvRequire anno = field.getAnnotationsByGroup(CsvRequire.class, groupEmpty).get(0);
        {
            //next null
            Optional<CellProcessor> processor = factory.create(anno, Optional.empty(), field, formatter, config);
            printCellProcessorChain(processor.get(), name.getMethodName());
            
            assertThat(processor.get()).isInstanceOf(Require.class);
            
            Require actual = (Require)processor.get();
            assertThat(actual.isConsiderEmpty()).isFalse();
            assertThat(actual.isConsiderBlank()).isFalse();
            
            {
                // input not empty 
                String input = "abcde";
                assertThat((Object)actual.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(input);
                
            }
            
            {
                // input null 
                String input = null;
                assertThatThrownBy(() -> actual.execute(input, ANONYMOUS_CSVCONTEXT)).isInstanceOf(SuperCsvValidationException.class);
                
            }
            
            {
                // input empty 
                String input = "";
                assertThat((Object)actual.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(input);
                
            }
            
            {
                // input blank 
                String input = "  ";
                assertThat((Object)actual.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(input);
                
            }
            
            assertThat(actual.getValidationMessage()).isEqualTo("{com.github.mygreen.supercsv.annotation.constraint.CsvRequire.message}");
        
        }
        
        {
            //next exist
            Optional<CellProcessor> processor = factory.create(anno, Optional.of(new NextCellProcessor()), field, formatter, config);
            printCellProcessorChain(processor.get(), name.getMethodName());
            
            assertThat(processor.get()).isInstanceOf(Require.class);
            
            Require actual = (Require)processor.get();
            assertThat(actual.isConsiderEmpty()).isFalse();
            assertThat(actual.isConsiderBlank()).isFalse();
            
            {
                // input not empty 
                String input = "abcde";
                assertThat((Object)actual.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(input);
                
            }
            
            {
                // input null 
                String input = null;
                assertThatThrownBy(() -> actual.execute(input, ANONYMOUS_CSVCONTEXT)).isInstanceOf(SuperCsvValidationException.class);
                
            }
            
            {
                // input empty 
                String input = "";
                assertThat((Object)actual.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(input);
                
            }
            
            {
                // input blank 
                String input = "  ";
                assertThat((Object)actual.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(input);
                
            }
            
            assertThat(actual.getValidationMessage()).isEqualTo("{com.github.mygreen.supercsv.annotation.constraint.CsvRequire.message}");
        
        }
        
    }
    
    /**
     * 属性considerEmptyのテスト
     */
    @Test
    public void testCreat_attrConsiderEmpty() {
        
        FieldAccessor field = getFieldAccessor(TestCsv.class, "col_empty", comparator);
        StringProcessorBuilder builder = (StringProcessorBuilder) builderResolver.resolve(String.class);
        TextFormatter<String> formatter = builder.getFormatter(field, config);
        
        CsvRequire anno = field.getAnnotationsByGroup(CsvRequire.class, groupEmpty).get(0);
        
        Optional<CellProcessor> processor = factory.create(anno, Optional.empty(), field, formatter, config);
        printCellProcessorChain(processor.get(), name.getMethodName());
        
        assertThat(processor.get()).isInstanceOf(Require.class);
        
        Require actual = (Require)processor.get();
        assertThat(actual.isConsiderEmpty()).isTrue();
        assertThat(actual.isConsiderBlank()).isFalse();
        
        {
            // input not empty 
            String input = "abcde";
            assertThat((Object)actual.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(input);
            
        }
        
        {
            // input null 
            String input = null;
            assertThatThrownBy(() -> actual.execute(input, ANONYMOUS_CSVCONTEXT)).isInstanceOf(SuperCsvValidationException.class);
            
        }
        
        {
            // input empty 
            String input = "";
            assertThatThrownBy(() -> actual.execute(input, ANONYMOUS_CSVCONTEXT)).isInstanceOf(SuperCsvValidationException.class);
            
        }
        
        {
            // input blank 
            String input = "  ";
            assertThat((Object)actual.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(input);
            
        }
        
    }
    
    /**
     * 属性considerBlankのテスト
     */
    @Test
    public void testCreat_attrConsiderBlank() {
        
        FieldAccessor field = getFieldAccessor(TestCsv.class, "col_blank", comparator);
        StringProcessorBuilder builder = (StringProcessorBuilder) builderResolver.resolve(String.class);
        TextFormatter<String> formatter = builder.getFormatter(field, config);
        
        CsvRequire anno = field.getAnnotationsByGroup(CsvRequire.class, groupEmpty).get(0);
        
        Optional<CellProcessor> processor = factory.create(anno, Optional.empty(), field, formatter, config);
        printCellProcessorChain(processor.get(), name.getMethodName());
        
        assertThat(processor.get()).isInstanceOf(Require.class);
        
        Require actual = (Require)processor.get();
        assertThat(actual.isConsiderEmpty()).isFalse();
        assertThat(actual.isConsiderBlank()).isTrue();
        
        {
            // input not empty 
            String input = "abcde";
            assertThat((Object)actual.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(input);
            
        }
        
        {
            // input null 
            String input = null;
            assertThatThrownBy(() -> actual.execute(input, ANONYMOUS_CSVCONTEXT)).isInstanceOf(SuperCsvValidationException.class);
            
        }
        
        {
            // input empty 
            String input = "";
            assertThatThrownBy(() -> actual.execute(input, ANONYMOUS_CSVCONTEXT)).isInstanceOf(SuperCsvValidationException.class);
            
        }
        
        {
            // input blank 
            String input = "  ";
            assertThatThrownBy(() -> actual.execute(input, ANONYMOUS_CSVCONTEXT)).isInstanceOf(SuperCsvValidationException.class);
            
        }
        
    }
    
    /**
     * 属性messageのテスト
     */
    @Test
    public void testCreate_attrMessage() {
        
        FieldAccessor field = getFieldAccessor(TestCsv.class, "col_message", comparator);
        StringProcessorBuilder builder = (StringProcessorBuilder) builderResolver.resolve(String.class);
        TextFormatter<String> formatter = builder.getFormatter(field, config);
        
        CsvRequire anno = field.getAnnotationsByGroup(CsvRequire.class, groupEmpty).get(0);
        
        Optional<CellProcessor> processor = factory.create(anno, Optional.empty(), field, formatter, config);
        printCellProcessorChain(processor.get(), name.getMethodName());
        
        assertThat(processor.get()).isInstanceOf(Require.class);
        
        Require actual = (Require)processor.get();
        
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
        assertThat(processor).hasCellProcessor(Require.class);
        
        String input = null;
        try {
            processor.execute(input, testCsvContext(columnMapping, input));
            fail();
            
        } catch(Exception e) {
            
            assertThat(e).isInstanceOf(SuperCsvValidationException.class);
            
            List<String> messages = exceptionConverter.convertAndFormat((SuperCsvValidationException)e, beanMapping);
            assertThat(messages).hasSize(1).contains("[2行, 1列] : 項目「カラム1」の値は必須です。");
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
        assertThat(processor).hasCellProcessor(Require.class);
        
        String input = null;
        try {
            processor.execute(input, testCsvContext(columnMapping, input));
            fail();
            
        } catch(Exception e) {
            
            assertThat(e).isInstanceOf(SuperCsvValidationException.class);
            
            List<String> messages = exceptionConverter.convertAndFormat((SuperCsvValidationException)e, beanMapping);
            assertThat(messages).hasSize(1).contains("[2行, 11列] : 項目「col_message_empty」の値は必須です。");
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
        assertThat(processor).hasCellProcessor(Require.class);
        
        String input = null;
        try {
            processor.execute(input, testCsvContext(columnMapping, input));
            fail();
            
        } catch(Exception e) {
            
            assertThat(e).isInstanceOf(SuperCsvValidationException.class);
            
            List<String> messages = exceptionConverter.convertAndFormat((SuperCsvValidationException)e, beanMapping);
            assertThat(messages).hasSize(1).contains("lineNumber=1, rowNumber=2, columnNumber=12, label=col_message_variables, validatedValue=, considerEmpty=false, considerBlank=true");
        }
        
    }
    
    
}

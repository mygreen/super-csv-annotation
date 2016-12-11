package com.github.mygreen.supercsv.cellprocessor.constraint;

import static org.junit.Assert.*;
import static org.assertj.core.api.Assertions.*;
import static com.github.mygreen.supercsv.tool.TestUtils.*;
import static com.github.mygreen.supercsv.tool.HasCellProcessorAssert.*;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collection;
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
import com.github.mygreen.supercsv.annotation.constraint.CsvWordRequire;
import com.github.mygreen.supercsv.builder.ProcessorBuilderResolver;
import com.github.mygreen.supercsv.builder.BeanMapping;
import com.github.mygreen.supercsv.builder.BeanMappingFactory;
import com.github.mygreen.supercsv.builder.BuildCase;
import com.github.mygreen.supercsv.builder.ColumnMapping;
import com.github.mygreen.supercsv.builder.Configuration;
import com.github.mygreen.supercsv.builder.FieldAccessor;
import com.github.mygreen.supercsv.builder.standard.StringProcessorBuilder;
import com.github.mygreen.supercsv.cellprocessor.format.TextFormatter;
import com.github.mygreen.supercsv.exception.SuperCsvInvalidAnnotationException;
import com.github.mygreen.supercsv.exception.SuperCsvValidationException;
import com.github.mygreen.supercsv.validation.CsvExceptionConverter;


/**
 * {@link WordRequireFactory}のテスタ
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class WordRequireFactoryTest {
    
    @Rule
    public TestName name = new TestName();
    
    private WordRequireFactory factory;
    
    private Configuration config;
    private Comparator<Annotation> comparator;
    private ProcessorBuilderResolver builderResolver;
    
    private BeanMappingFactory beanMappingFactory;
    private CsvExceptionConverter exceptionConverter;
        
    private final Class<?>[] groupEmpty = new Class[]{};
    
    @Before
    public void setUp() throws Exception {
        this.factory = new WordRequireFactory();
        
        this.config = new Configuration();
        this.beanMappingFactory = new BeanMappingFactory();
        beanMappingFactory.setConfiguration(config);
        
        this.exceptionConverter = new CsvExceptionConverter();
        
        this.comparator = config.getAnnoationComparator();
        this.builderResolver = config.getBuilderResolver();
    }
    
    private static final String TEST_VALUE_VALID_INPUT = "今日はいい天気です。よろしくお願いいたします。";
    private static final String TEST_VALUE_WRONG_INPUT = "明日は晴れるといいね。よろしくお願いいたします。";
    
    @CsvBean
    private static class TestCsv {
        
        @CsvColumn(number=1, label="カラム1")
        @CsvWordRequire(value={"今日", "天気", "よろしく"})
        private String col_default;
        
        @CsvColumn(number=2)
        @CsvWordRequire(provider=MyRequireWordProvider.class)
        private String col_provider;
        
        @CsvColumn(number=10)
        @CsvWordRequire(value={"今日", "天気", "よろしく"}, message="テストメッセージ")
        private String col_message;
        
        @CsvColumn(number=11)
        @CsvWordRequire(value={"今日", "天気", "よろしく"}, message="")
        private String col_message_empty;
        
        @CsvColumn(number=12)
        @CsvWordRequire(value={"今日", "天気", "よろしく"},
                message="lineNumber={lineNumber}, rowNumber={rowNumber}, columnNumber={columnNumber}, label={label}, validatedValue={validatedValue}, words=${f:join(words, ', ')}")
        private String col_message_variables;
        
    }
    
    /**
     * 属性が不正なCSV
     *
     */
    @CsvBean
    private static class ErrorCsv {
        
        @CsvColumn(number=1)
        @CsvWordRequire
        private String col_no_value_provider;
        
    }
    
    private static class MyRequireWordProvider implements RequiredWordProvider {
        
        @Override
        public Collection<String> getRequiredWords(final FieldAccessor field) {
            return Arrays.asList("今日", "天気", "よろしく");
        }
        
    }
    
    @Test
    public void testCreate_default() {
        
        FieldAccessor field = getFieldAccessor(TestCsv.class, "col_default", comparator);
        StringProcessorBuilder builder = (StringProcessorBuilder) builderResolver.resolve(String.class);
        TextFormatter<String> formatter = builder.getFormatter(field, config);
        
        CsvWordRequire anno = field.getAnnotationsByGroup(CsvWordRequire.class, groupEmpty).get(0);
        
        {
            //next null
            Optional<CellProcessor> processor = factory.create(anno, Optional.empty(), field, formatter, config);
            printCellProcessorChain(processor.get(), name.getMethodName());
            
            assertThat(processor.get()).isInstanceOf(WordRequire.class);
            
            WordRequire actual = (WordRequire)processor.get();
            assertThat(actual.getWords()).containsExactly("今日", "天気", "よろしく");
            
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
            
            assertThat(actual.getValidationMessage()).isEqualTo("{com.github.mygreen.supercsv.annotation.constraint.CsvWordRequire.message}");
        }
        
        {
            //next exist
            Optional<CellProcessor> processor = factory.create(anno, Optional.of(new NextCellProcessor()), field, formatter, config);
            printCellProcessorChain(processor.get(), name.getMethodName());
            
            assertThat(processor.get()).isInstanceOf(WordRequire.class);
            
            WordRequire actual = (WordRequire)processor.get();
            assertThat(actual.getWords()).containsExactly("今日", "天気", "よろしく");
            
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
            
            assertThat(actual.getValidationMessage()).isEqualTo("{com.github.mygreen.supercsv.annotation.constraint.CsvWordRequire.message}");
        }
        
    }
    
    /**
     * 属性providerの指定
     */
    @Test
    public void testCreate_attrProvider() {
        
        FieldAccessor field = getFieldAccessor(TestCsv.class, "col_provider", comparator);
        StringProcessorBuilder builder = (StringProcessorBuilder) builderResolver.resolve(String.class);
        TextFormatter<String> formatter = builder.getFormatter(field, config);
        
        CsvWordRequire anno = field.getAnnotationsByGroup(CsvWordRequire.class, groupEmpty).get(0);
        
        Optional<CellProcessor> processor = factory.create(anno, Optional.empty(), field, formatter, config);
        printCellProcessorChain(processor.get(), name.getMethodName());
        
        assertThat(processor.get()).isInstanceOf(WordRequire.class);
        
        WordRequire actual = (WordRequire)processor.get();
        assertThat(actual.getWords()).containsExactly("今日", "天気", "よろしく");
        
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
        
        assertThat(actual.getValidationMessage()).isEqualTo("{com.github.mygreen.supercsv.annotation.constraint.CsvWordRequire.message}");
        
    }
    
    /**
     * 属性value, providerの指定がない
     */
    @Test
    public void testCreate_attrNoValueProvider() {
        
        FieldAccessor field = getFieldAccessor(ErrorCsv.class, "col_no_value_provider", comparator);
        StringProcessorBuilder builder = (StringProcessorBuilder) builderResolver.resolve(String.class);
        TextFormatter<String> formatter = builder.getFormatter(field, config);
        
        CsvWordRequire anno = field.getAnnotationsByGroup(CsvWordRequire.class, groupEmpty).get(0);
        
        try {
            factory.create(anno, Optional.empty(), field, formatter, config);
            fail();
            
        } catch(Exception e) {
            assertThat(e).isInstanceOf(SuperCsvInvalidAnnotationException.class)
                .hasMessage("'%s' において、アノテーション @CsvWordRequire の属性 'value or provider' の指定は必須です。", 
                    field.getNameWithClass());
            
            SuperCsvInvalidAnnotationException exception = (SuperCsvInvalidAnnotationException)e;
            assertThat(exception.getTargetAnnotation()).isInstanceOf(CsvWordRequire.class);
            
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
        assertThat(processor).hasCellProcessor(WordRequire.class);
        
        String input = TEST_VALUE_WRONG_INPUT;
        try {
            processor.execute(input, testCsvContext(columnMapping, input));
            fail();
            
        } catch(Exception e) {
            
            assertThat(e).isInstanceOf(SuperCsvValidationException.class);
            
            List<String> messages = exceptionConverter.convertAndFormat((SuperCsvValidationException)e, beanMapping);
            assertThat(messages).hasSize(1)
                    .contains("[2行, 1列] : 項目「カラム1」の値（明日は晴れるといいね。よろしくお願いいたします。）には、必須語彙 「今日, 天気」の何れかが含まれていません。");
        }
        
    }
    
    /**
     * エラーメッセージのテスト - 語彙が1つの場合
     */
    @Test
    public void testErrorMessage_word1() {
        
        BeanMapping<TestCsv> beanMapping = beanMappingFactory.create(TestCsv.class, groupEmpty);
        
        ColumnMapping columnMapping = beanMapping.getColumnMapping("col_default").get();
        
        CellProcessor processor = columnMapping.getCellProcessorForReading();
        printCellProcessorChain(processor, name.getMethodName());
        assertThat(processor).hasCellProcessor(WordRequire.class);
        
        String input = "今日は、よろしくお願いします。";
        try {
            processor.execute(input, testCsvContext(columnMapping, input));
            fail();
            
        } catch(Exception e) {
            
            assertThat(e).isInstanceOf(SuperCsvValidationException.class);
            
            List<String> messages = exceptionConverter.convertAndFormat((SuperCsvValidationException)e, beanMapping);
            assertThat(messages).hasSize(1)
                    .contains("[2行, 1列] : 項目「カラム1」の値（今日は、よろしくお願いします。）には、必須語彙 「天気」が含まれていません。");
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
        assertThat(processor).hasCellProcessor(WordRequire.class);
        
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
        assertThat(processor).hasCellProcessor(WordRequire.class);
        
        String input = TEST_VALUE_WRONG_INPUT;
        try {
            processor.execute(input, testCsvContext(columnMapping, input));
            fail();
            
        } catch(Exception e) {
            
            assertThat(e).isInstanceOf(SuperCsvValidationException.class);
            
            List<String> messages = exceptionConverter.convertAndFormat((SuperCsvValidationException)e, beanMapping);
            assertThat(messages).hasSize(1)
                    .contains("[2行, 11列] : 項目「col_message_empty」の値（明日は晴れるといいね。よろしくお願いいたします。）には、必須語彙 「今日, 天気」の何れかが含まれていません。");
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
        assertThat(processor).hasCellProcessor(WordRequire.class);
        
        String input = TEST_VALUE_WRONG_INPUT;
        try {
            processor.execute(input, testCsvContext(columnMapping, input));
            fail();
            
        } catch(Exception e) {
            
            assertThat(e).isInstanceOf(SuperCsvValidationException.class);
            
            List<String> messages = exceptionConverter.convertAndFormat((SuperCsvValidationException)e, beanMapping);
            assertThat(messages).hasSize(1)
                    .contains("lineNumber=1, rowNumber=2, columnNumber=12, label=col_message_variables, validatedValue=明日は晴れるといいね。よろしくお願いいたします。, words=今日, 天気");
        }
        
    }
    
    
}

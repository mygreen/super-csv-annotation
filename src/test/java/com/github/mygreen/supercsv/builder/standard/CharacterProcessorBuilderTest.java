package com.github.mygreen.supercsv.builder.standard;

import static org.junit.Assert.*;
import static org.assertj.core.api.Assertions.*;
import static com.github.mygreen.supercsv.tool.TestUtils.*;

import java.lang.annotation.Annotation;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.supercsv.cellprocessor.ift.CellProcessor;

import com.github.mygreen.supercsv.annotation.CsvBean;
import com.github.mygreen.supercsv.annotation.CsvColumn;
import com.github.mygreen.supercsv.builder.ProcessorBuilderResolver;
import com.github.mygreen.supercsv.builder.BeanMapping;
import com.github.mygreen.supercsv.builder.BeanMappingFactory;
import com.github.mygreen.supercsv.builder.ColumnMapping;
import com.github.mygreen.supercsv.builder.Configuration;
import com.github.mygreen.supercsv.builder.FieldAccessor;
import com.github.mygreen.supercsv.cellprocessor.format.TextFormatter;
import com.github.mygreen.supercsv.cellprocessor.format.TextParseException;
import com.github.mygreen.supercsv.exception.SuperCsvValidationException;
import com.github.mygreen.supercsv.localization.MessageResolver;
import com.github.mygreen.supercsv.localization.ResourceBundleMessageResolver;
import com.github.mygreen.supercsv.validation.CsvExceptionConverter;

/**
 * Test the {@link CharacterProcessorBuilder}
 *
 * @version 2.0
 * @since 1.2
 * @author T.TSUCHIE
 *
 */
@RunWith(Enclosed.class)
public class CharacterProcessorBuilderTest {
    
    /**
     * フォーマッタのテスト
     *
     */
    public static class FormatterTest {
        
        @Rule
        public TestName name = new TestName();
        
        private Configuration config;
        private ProcessorBuilderResolver builderResolver;
        private Comparator<Annotation> comparator;
        
        @Before
        public void setUp() {
            this.config = new Configuration();
            this.builderResolver = config.getBuilderResolver();
            this.comparator = config.getAnnoationComparator();
            
        }
        
        @CsvBean
        private static class TestCsv {
            
            @CsvColumn(number=1)
            char col_primitive;
            
            @CsvColumn(number=2)
            Character col_wrapper;
            
        }
        
        @Test
        public void testParsePrint_primitive() {
            
            FieldAccessor field = getFieldAccessor(TestCsv.class, "col_primitive", comparator);
            CharacterProcessorBuilder builder = (CharacterProcessorBuilder) builderResolver.resolve(char.class);
            TextFormatter<Character> formatter = builder.getFormatter(field, config);
            
            {
                // parse
                assertThat(formatter.parse("a")).isEqualTo('a');
                assertThat(formatter.parse("abc")).isEqualTo('a');
                assertThatThrownBy(() -> formatter.parse("")).isInstanceOf(TextParseException.class);
            }
            
            {
                //print
                assertThat(formatter.print('a')).isEqualTo("a");
            }
            
        }
        
        @Test
        public void testParsePrint_wrapper() {
            
            FieldAccessor field = getFieldAccessor(TestCsv.class, "col_wrapper", comparator);
            CharacterProcessorBuilder builder = (CharacterProcessorBuilder) builderResolver.resolve(Character.class);
            TextFormatter<Character> formatter = builder.getFormatter(field, config);
            
            {
                // parse
                assertThat(formatter.parse("a")).isEqualTo(toCharacter("a"));
                assertThat(formatter.parse("abc")).isEqualTo(toCharacter("a"));
                assertThatThrownBy(() -> formatter.parse("")).isInstanceOf(TextParseException.class);
            }
            
            {
                //print
                assertThat(formatter.print('a')).isEqualTo("a");
                assertThatThrownBy(() -> formatter.parse(null)).isInstanceOf(NullPointerException.class);
            }
        }
        
        @Test
        public void testMessageAndVariables() {
            
            FieldAccessor field = getFieldAccessor(TestCsv.class, "col_primitive", comparator);
            CharacterProcessorBuilder builder = (CharacterProcessorBuilder) builderResolver.resolve(char.class);
            TextFormatter<Character> formatter = builder.getFormatter(field, config);
            
            formatter.setValidationMessage("テストメッセージ");
            
            assertThat(formatter.getValidationMessage()).isEmpty();
            assertThat(formatter.getMessageVariables()).isEmpty();
            
        }
        
        @Test
        public void testPattern() {
            
            FieldAccessor field = getFieldAccessor(TestCsv.class, "col_primitive", comparator);
            CharacterProcessorBuilder builder = (CharacterProcessorBuilder) builderResolver.resolve(char.class);
            TextFormatter<Character> formatter = builder.getFormatter(field, config);
            
            assertThat(formatter.getPattern()).isEmpty();
        }
        
        
    }
    
    /**
     * Tests for wrapper Character.
     *
     */
    public static class WrappterTest {
        
        @Rule
        public TestName name = new TestName();
        
        private BeanMappingFactory beanMappingFactory;
        private CsvExceptionConverter exceptionConverter;
        
        private final Class<?>[] groupEmpty = new Class[]{};
        
        private MessageResolver testMessageResolver;
        
        @Before
        public void setUp() {
            this.beanMappingFactory = new BeanMappingFactory();
            this.exceptionConverter = new CsvExceptionConverter();
            
            this.testMessageResolver = new ResourceBundleMessageResolver(ResourceBundle.getBundle("TestMessages"));
        }
        
        private static final Character TEST_VALUE_1_OBJ = toCharacter("a");
        private static final String TEST_VALUE_1_STR_NORMAL = "a";
        
        @CsvBean
        private static class TestCsv {
            
            @CsvColumn(number=1)
            Character col_default;
            
        }
        
        @Test
        public void testBuildForReading_default() {
            
            BeanMapping<TestCsv> beanMapping = beanMappingFactory.create(TestCsv.class, groupEmpty);
            ColumnMapping columnMapping = beanMapping.getColumnMapping("col_default").get();
            
            CellProcessor processor = columnMapping.getCellProcessorForReading();
            printCellProcessorChain(processor, name.getMethodName());
            
            {
                // null input
                String input = null;
                Character expected = null;
                assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
            }
            
            {
                // empty input
                String input = "";
                Character expected = null;
                assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
            }
            
            {
                // valid input
                String input = TEST_VALUE_1_STR_NORMAL;
                Character expected = TEST_VALUE_1_OBJ;
                assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
            }
            
            {
                // valid input - over size
                String input = "abc";
                Character expected = TEST_VALUE_1_OBJ;
                assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
            }
            
        }
        
        @Test
        public void testBuildForWriging_default() {
            
            BeanMapping<TestCsv> beanMapping = beanMappingFactory.create(TestCsv.class, groupEmpty);
            ColumnMapping columnMapping = beanMapping.getColumnMapping("col_default").get();
            
            CellProcessor processor = columnMapping.getCellProcessorForWriting();
            printCellProcessorChain(processor, name.getMethodName());
            
            {
                // null input
                String input = null;
                String expected = null;
                assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
            }
            
            {
                // valid input
                Character input = TEST_VALUE_1_OBJ;
                String expected = TEST_VALUE_1_STR_NORMAL;
                assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
            }
            
        }
        
    }
    
    /**
     * Tests for primitive
     *
     */
    public static class PrimitiveTest {
        
        @Rule
        public TestName name = new TestName();
        
        private BeanMappingFactory beanMappingFactory;
        private CsvExceptionConverter exceptionConverter;
        
        private final Class<?>[] groupEmpty = new Class[]{};
        
        private MessageResolver testMessageResolver;
        
        @Before
        public void setUp() {
            this.beanMappingFactory = new BeanMappingFactory();
            this.exceptionConverter = new CsvExceptionConverter();
            
            this.testMessageResolver = new ResourceBundleMessageResolver(ResourceBundle.getBundle("TestMessages"));
        }
        
        private static final char TEST_VALUE_PRIMITIVE_INIT_OBJ = '\u0000';
        
        private static final char TEST_VALUE_1_OBJ = 'a';
        private static final String TEST_VALUE_1_STR_NORMAL = "a";
        
        @CsvBean
        private static class TestCsv {
            
            @CsvColumn(number=1)
            char col_default;
            
        }
        
        @Test
        public void testBuildForReading_default() {
            
            BeanMapping<TestCsv> beanMapping = beanMappingFactory.create(TestCsv.class, groupEmpty);
            ColumnMapping columnMapping = beanMapping.getColumnMapping("col_default").get();
            
            CellProcessor processor = columnMapping.getCellProcessorForReading();
            printCellProcessorChain(processor, name.getMethodName());
            
            {
                // null input
                String input = null;
                char expected = TEST_VALUE_PRIMITIVE_INIT_OBJ;
                assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
            }
            
            {
                // empty input
                String input = "";
                char expected = TEST_VALUE_PRIMITIVE_INIT_OBJ;
                assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
            }
            
            {
                // valid input
                String input = TEST_VALUE_1_STR_NORMAL;
                char expected = TEST_VALUE_1_OBJ;
                assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
            }
            
            {
                // valid input - size over
                String input = "abc";
                char expected = TEST_VALUE_1_OBJ;
                assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
            }
            
        }
        
        @Test
        public void testBuildForWriging_default() {
            
            BeanMapping<TestCsv> beanMapping = beanMappingFactory.create(TestCsv.class, groupEmpty);
            ColumnMapping columnMapping = beanMapping.getColumnMapping("col_default").get();
            
            CellProcessor processor = columnMapping.getCellProcessorForWriting();
            printCellProcessorChain(processor, name.getMethodName());
            
            {
                // null input
                String input = null;
                String expected = null;
                assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
            }
            
            {
                // valid input
                char input = TEST_VALUE_1_OBJ;
                String expected = TEST_VALUE_1_STR_NORMAL;
                assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
            }
            
        }
        
    }
    
}

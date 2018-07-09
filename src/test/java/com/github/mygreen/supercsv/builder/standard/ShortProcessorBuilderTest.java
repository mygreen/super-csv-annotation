package com.github.mygreen.supercsv.builder.standard;

import static org.junit.Assert.*;
import static org.assertj.core.api.Assertions.*;
import static com.github.mygreen.supercsv.tool.TestUtils.*;
import static com.github.mygreen.supercsv.tool.HasCellProcessorAssert.*;

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
import com.github.mygreen.supercsv.annotation.format.CsvNumberFormat;
import com.github.mygreen.supercsv.builder.BeanMapping;
import com.github.mygreen.supercsv.builder.BeanMappingFactory;
import com.github.mygreen.supercsv.builder.ColumnMapping;
import com.github.mygreen.supercsv.exception.SuperCsvValidationException;
import com.github.mygreen.supercsv.localization.EncodingControl;
import com.github.mygreen.supercsv.localization.MessageResolver;
import com.github.mygreen.supercsv.localization.ResourceBundleMessageResolver;
import com.github.mygreen.supercsv.validation.CsvExceptionConverter;

/**
 * Test the {@link ShortProcessorBuilder}
 *
 * @version 2.0
 * @since 1.2
 * @author T.TSUCHIE
 *
 */
@RunWith(Enclosed.class)
public class ShortProcessorBuilderTest {
    
    /**
     * Tests for wrapper Short.
     *
     */
    public static class WrapperTest {
        
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
            
            this.testMessageResolver = new ResourceBundleMessageResolver(ResourceBundle.getBundle("TestMessages", new EncodingControl("UTF-8")));
        }
        
        private static final String TEST_FORMATTED_PATTERN = "#,###";
        
        private static final Short TEST_VALUE_1_OBJ = toShort("12345");
        private static final String TEST_VALUE_1_STR_NORMAL = "12345";
        private static final String TEST_VALUE_1_STR_FORMATTED = "12,345";
        
        @CsvBean
        private static class TestCsv {
            
            @CsvColumn(number=1)
            Short col_default;
            
            @CsvColumn(number=2)
            @CsvNumberFormat(pattern=TEST_FORMATTED_PATTERN)
            Short col_format;
            
            @CsvColumn(number=10)
            @CsvNumberFormat(message="テストメッセージ")
            Short col_message;
            
            @CsvColumn(number=11)
            @CsvNumberFormat(message="")
            Short col_message_empty;
            
            @CsvColumn(number=12)
            @CsvNumberFormat(pattern=TEST_FORMATTED_PATTERN,
                    message="lineNumber={lineNumber}, rowNumber={rowNumber}, columnNumber={columnNumber}, label={label}, validatedValue={validatedValue}, pattern={pattern}")
            Short col_message_variables;
            
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
                Short expected = null;
                assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
            }
            
            {
                // empty input
                String input = "";
                Short expected = null;
                assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
            }
            
            {
                // valid input
                String input = TEST_VALUE_1_STR_NORMAL;
                Short expected = TEST_VALUE_1_OBJ;
                assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
            }
            
            {
                // wrong input
                String input = "abc";
                try {
                    processor.execute(input, ANONYMOUS_CSVCONTEXT);
                    fail();
                    
                } catch(Exception e) {
                    assertThat(e).isInstanceOf(SuperCsvValidationException.class);
                    
                    SuperCsvValidationException validationException = (SuperCsvValidationException)e;
                    assertThat(validationException.isParedError()).isTrue();
                    assertThat(validationException.getRejectedValue()).isEqualTo(input);
                    assertThat(validationException.getValidationMessage()).isEqualTo("{com.github.mygreen.supercsv.cellprocessor.format.ParseProcessor.violated}");
                    assertThat(validationException.getMessageVariables()).isEmpty();
                    
                }
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
                Short input = null;
                String expected = null;
                assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
            }
            
            {
                // valid input
                Short input = TEST_VALUE_1_OBJ;
                String expected = TEST_VALUE_1_STR_NORMAL;
                assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
            }
            
        }
        
        @Test
        public void testBuildForReading_format() {
            
            BeanMapping<TestCsv> beanMapping = beanMappingFactory.create(TestCsv.class, groupEmpty);
            ColumnMapping columnMapping = beanMapping.getColumnMapping("col_format").get();
            
            CellProcessor processor = columnMapping.getCellProcessorForReading();
            printCellProcessorChain(processor, name.getMethodName());
            
            {
                // null input
                String input = null;
                Short expected = null;
                assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
            }
            
            {
                // empty input
                String input = "";
                Short expected = null;
                assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
            }
            
            {
                // valid input
                String input = TEST_VALUE_1_STR_FORMATTED;
                Short expected = TEST_VALUE_1_OBJ;
                assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
            }
            
            {
                // wrong input
                String input = "abc";
                try {
                    processor.execute(input, ANONYMOUS_CSVCONTEXT);
                    fail();
                    
                } catch(Exception e) {
                    assertThat(e).isInstanceOf(SuperCsvValidationException.class);
                    
                    SuperCsvValidationException validationException = (SuperCsvValidationException)e;
                    assertThat(validationException.isParedError()).isTrue();
                    assertThat(validationException.getRejectedValue()).isEqualTo(input);
                    assertThat(validationException.getValidationMessage()).isEqualTo("{com.github.mygreen.supercsv.annotation.format.CsvNumberFormat.message}");
                    assertThat(validationException.getMessageVariables()).containsEntry("pattern", TEST_FORMATTED_PATTERN);
                    
                }
            }
        }
        
        @Test
        public void testBuildForWriting_format() {
            
            BeanMapping<TestCsv> beanMapping = beanMappingFactory.create(TestCsv.class, groupEmpty);
            ColumnMapping columnMapping = beanMapping.getColumnMapping("col_format").get();
            
            CellProcessor processor = columnMapping.getCellProcessorForWriting();
            printCellProcessorChain(processor, name.getMethodName());
            
            {
                // null input
                Short input = null;
                String expected = null;
                assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
            }
            
            {
                // valid input
                Short input = TEST_VALUE_1_OBJ;
                String expected = TEST_VALUE_1_STR_FORMATTED;
                assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
            }
            
        }
        
        @Test
        public void testErrorMessage_default() {
            
            BeanMapping<TestCsv> beanMapping = beanMappingFactory.create(TestCsv.class, groupEmpty);
            ColumnMapping columnMapping = beanMapping.getColumnMapping("col_default").get();
            
            CellProcessor processor = columnMapping.getCellProcessorForReading();
            printCellProcessorChain(processor, name.getMethodName());
            
            String input = "abc";
            try {
                processor.execute(input, testCsvContext(columnMapping, input));
                fail();
                
            } catch(Exception e) {
                assertThat(e).isInstanceOf(SuperCsvValidationException.class);
                
                List<String> messages = exceptionConverter.convertAndFormat((SuperCsvValidationException)e, beanMapping);
                assertThat(messages).hasSize(1)
                        .contains("[2行, 1列] : 項目「col_default」の値（abc）の書式は不正です。");                
            }
            
        }
        
        /**
         * アノテーションの属性formatを指定した場合
         */
        @Test
        public void testErrorMessage_format() {
            
            BeanMapping<TestCsv> beanMapping = beanMappingFactory.create(TestCsv.class, groupEmpty);
            ColumnMapping columnMapping = beanMapping.getColumnMapping("col_format").get();
            
            CellProcessor processor = columnMapping.getCellProcessorForReading();
            printCellProcessorChain(processor, name.getMethodName());
            
            String input = "abc";
            try {
                processor.execute(input, testCsvContext(columnMapping, input));
                fail();
                
            } catch(Exception e) {
                assertThat(e).isInstanceOf(SuperCsvValidationException.class);
                
                List<String> messages = exceptionConverter.convertAndFormat((SuperCsvValidationException)e, beanMapping);
                assertThat(messages).hasSize(1)
                        .contains("[2行, 2列] : 項目「col_format」の値（abc）は、数値の書式「#,###」として不正です。");                
            }
            
        }
        
        /**
         * アノテーションの属性messageの指定
         */
        @Test
        public void testErrorMessage_message() {
            
            BeanMapping<TestCsv> beanMapping = beanMappingFactory.create(TestCsv.class, groupEmpty);
            ColumnMapping columnMapping = beanMapping.getColumnMapping("col_message").get();
            
            CellProcessor processor = columnMapping.getCellProcessorForReading();
            printCellProcessorChain(processor, name.getMethodName());
            
            String input = "abc";
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
         * アノテーションの属性messageの値が空文字の場合
         */
        @Test
        public void testErrorMessage_empty() {
            
            BeanMapping<TestCsv> beanMapping = beanMappingFactory.create(TestCsv.class, groupEmpty);
            ColumnMapping columnMapping = beanMapping.getColumnMapping("col_message_empty").get();
            
            CellProcessor processor = columnMapping.getCellProcessorForReading();
            printCellProcessorChain(processor, name.getMethodName());
            
            String input = "abc";
            try {
                processor.execute(input, testCsvContext(columnMapping, input));
                fail();
                
            } catch(Exception e) {
                assertThat(e).isInstanceOf(SuperCsvValidationException.class);
                
                SuperCsvValidationException validationException = (SuperCsvValidationException)e;
                assertThat(validationException.getValidationMessage()).isEqualTo("{com.github.mygreen.supercsv.cellprocessor.format.ParseProcessor.violated}");
                
                List<String> messages = exceptionConverter.convertAndFormat((SuperCsvValidationException)e, beanMapping);
                assertThat(messages).hasSize(1)
                        .contains("[2行, 11列] : 項目「col_message_empty」の値（abc）の書式は不正です。");                
            }
            
        }
        
        /**
         * アノテーションの属性messageの値が空文字の場合
         */
        @Test
        public void testErrorMessage_variables() {
            
            BeanMapping<TestCsv> beanMapping = beanMappingFactory.create(TestCsv.class, groupEmpty);
            ColumnMapping columnMapping = beanMapping.getColumnMapping("col_message_variables").get();
            
            CellProcessor processor = columnMapping.getCellProcessorForReading();
            printCellProcessorChain(processor, name.getMethodName());
            
            String input = "abc";
            try {
                processor.execute(input, testCsvContext(columnMapping, input));
                fail();
                
            } catch(Exception e) {
                assertThat(e).isInstanceOf(SuperCsvValidationException.class);
                
                List<String> messages = exceptionConverter.convertAndFormat((SuperCsvValidationException)e, beanMapping);
                assertThat(messages).hasSize(1)
                        .contains("lineNumber=1, rowNumber=2, columnNumber=12, label=col_message_variables, validatedValue=abc, pattern=#,###");                
            }
            
        }
        
        /**
         * プロパティファイルのtypeMismatchからメッセージを取得する場合
         */
        @Test
        public void testErrorMessage_typeMismatch() {
            
            // 独自のメッセージに入れ替え
            exceptionConverter.setMessageResolver(testMessageResolver);
            
            BeanMapping<TestCsv> beanMapping = beanMappingFactory.create(TestCsv.class, groupEmpty);
            ColumnMapping columnMapping = beanMapping.getColumnMapping("col_default").get();
            
            CellProcessor processor = columnMapping.getCellProcessorForReading();
            printCellProcessorChain(processor, name.getMethodName());
            
            String input = "abc";
            try {
                processor.execute(input, testCsvContext(columnMapping, input));
                fail();
                
            } catch(Exception e) {
                assertThat(e).isInstanceOf(SuperCsvValidationException.class);
                
                List<String> messages = exceptionConverter.convertAndFormat((SuperCsvValidationException)e, beanMapping);
                assertThat(messages).hasSize(1)
                        .contains("[2行, 1列] : 項目「col_default」の値（abc）は、整数の書式として不正です。");                
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
            
            this.testMessageResolver = new ResourceBundleMessageResolver(ResourceBundle.getBundle("TestMessages", new EncodingControl("UTF-8")));
        }
        
        private static final short TEST_VALUE_PRIMITIVE_INIT_OBJ = (short)0;
        
        private static final String TEST_FORMATTED_PATTERN = "#,###";
        
        private static final Short TEST_VALUE_1_OBJ = toShort("12345");
        private static final String TEST_VALUE_1_STR_NORMAL = "12345";
        private static final String TEST_VALUE_1_STR_FORMATTED = "12,345";
        
        @CsvBean
        private static class TestCsv {
            
            @CsvColumn(number=1)
            short col_default;
            
            @CsvColumn(number=2)
            @CsvNumberFormat(pattern=TEST_FORMATTED_PATTERN)
            short col_format;
            
            @CsvColumn(number=10)
            @CsvNumberFormat(message="テストメッセージ")
            short col_message;
            
            @CsvColumn(number=11)
            @CsvNumberFormat(message="")
            short col_message_empty;
            
            @CsvColumn(number=12)
            @CsvNumberFormat(pattern=TEST_FORMATTED_PATTERN,
                    message="lineNumber={lineNumber}, rowNumber={rowNumber}, columnNumber={columnNumber}, label={label}, validatedValue={validatedValue}, pattern={pattern}")
            short col_message_variables;
            
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
                short expected = TEST_VALUE_PRIMITIVE_INIT_OBJ;
                assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
            }
            
            {
                // empty input
                String input = "";
                short expected = TEST_VALUE_PRIMITIVE_INIT_OBJ;
                assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
            }
            
            {
                // valid input
                String input = TEST_VALUE_1_STR_NORMAL;
                short expected = TEST_VALUE_1_OBJ;
                assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
            }
            
            {
                // wrong input
                String input = "abc";
                try {
                    processor.execute(input, ANONYMOUS_CSVCONTEXT);
                    fail();
                    
                } catch(Exception e) {
                    assertThat(e).isInstanceOf(SuperCsvValidationException.class);
                    
                    SuperCsvValidationException validationException = (SuperCsvValidationException)e;
                    assertThat(validationException.isParedError()).isTrue();
                    assertThat(validationException.getRejectedValue()).isEqualTo(input);
                    assertThat(validationException.getValidationMessage()).isEqualTo("{com.github.mygreen.supercsv.cellprocessor.format.ParseProcessor.violated}");
                    assertThat(validationException.getMessageVariables()).isEmpty();
                    
                }
            }
        }
        
        @Test
        public void testBuildForWriging_default() {
            
            BeanMapping<TestCsv> beanMapping = beanMappingFactory.create(TestCsv.class, groupEmpty);
            ColumnMapping columnMapping = beanMapping.getColumnMapping("col_default").get();
            
            CellProcessor processor = columnMapping.getCellProcessorForWriting();
            printCellProcessorChain(processor, name.getMethodName());
            
            {
                // valid input
                short input = TEST_VALUE_1_OBJ;
                String expected = TEST_VALUE_1_STR_NORMAL;
                assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
            }
            
        }
        
        @Test
        public void testBuildForReading_format() {
            
            BeanMapping<TestCsv> beanMapping = beanMappingFactory.create(TestCsv.class, groupEmpty);
            ColumnMapping columnMapping = beanMapping.getColumnMapping("col_format").get();
            
            CellProcessor processor = columnMapping.getCellProcessorForReading();
            printCellProcessorChain(processor, name.getMethodName());
            
            {
                // null input
                String input = null;
                short expected = TEST_VALUE_PRIMITIVE_INIT_OBJ;
                assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
            }
            
            {
                // empty input
                String input = "";
                short expected = TEST_VALUE_PRIMITIVE_INIT_OBJ;
                assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
            }
            
            {
                // valid input
                String input = TEST_VALUE_1_STR_FORMATTED;
                short expected = TEST_VALUE_1_OBJ;
                assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
            }
            
            {
                // wrong input
                String input = "abc";
                try {
                    processor.execute(input, ANONYMOUS_CSVCONTEXT);
                    fail();
                    
                } catch(Exception e) {
                    assertThat(e).isInstanceOf(SuperCsvValidationException.class);
                    
                    SuperCsvValidationException validationException = (SuperCsvValidationException)e;
                    assertThat(validationException.isParedError()).isTrue();
                    assertThat(validationException.getRejectedValue()).isEqualTo(input);
                    assertThat(validationException.getValidationMessage()).isEqualTo("{com.github.mygreen.supercsv.annotation.format.CsvNumberFormat.message}");
                    assertThat(validationException.getMessageVariables()).containsEntry("pattern", TEST_FORMATTED_PATTERN);
                    
                }
            }
        }
        
        @Test
        public void testBuildForWriting_format() {
            
            BeanMapping<TestCsv> beanMapping = beanMappingFactory.create(TestCsv.class, groupEmpty);
            ColumnMapping columnMapping = beanMapping.getColumnMapping("col_format").get();
            
            CellProcessor processor = columnMapping.getCellProcessorForWriting();
            printCellProcessorChain(processor, name.getMethodName());
            
            {
                // valid input
                short input = TEST_VALUE_1_OBJ;
                String expected = TEST_VALUE_1_STR_FORMATTED;
                assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
            }
            
        }
        
        @Test
        public void testErrorMessage_default() {
            
            BeanMapping<TestCsv> beanMapping = beanMappingFactory.create(TestCsv.class, groupEmpty);
            ColumnMapping columnMapping = beanMapping.getColumnMapping("col_default").get();
            
            CellProcessor processor = columnMapping.getCellProcessorForReading();
            printCellProcessorChain(processor, name.getMethodName());
            
            String input = "abc";
            try {
                processor.execute(input, testCsvContext(columnMapping, input));
                fail();
                
            } catch(Exception e) {
                assertThat(e).isInstanceOf(SuperCsvValidationException.class);
                
                List<String> messages = exceptionConverter.convertAndFormat((SuperCsvValidationException)e, beanMapping);
                assertThat(messages).hasSize(1)
                        .contains("[2行, 1列] : 項目「col_default」の値（abc）の書式は不正です。");                
            }
            
        }
        
        /**
         * アノテーションの属性formatを指定した場合
         */
        @Test
        public void testErrorMessage_format() {
            
            BeanMapping<TestCsv> beanMapping = beanMappingFactory.create(TestCsv.class, groupEmpty);
            ColumnMapping columnMapping = beanMapping.getColumnMapping("col_format").get();
            
            CellProcessor processor = columnMapping.getCellProcessorForReading();
            printCellProcessorChain(processor, name.getMethodName());
            
            String input = "abc";
            try {
                processor.execute(input, testCsvContext(columnMapping, input));
                fail();
                
            } catch(Exception e) {
                assertThat(e).isInstanceOf(SuperCsvValidationException.class);
                
                List<String> messages = exceptionConverter.convertAndFormat((SuperCsvValidationException)e, beanMapping);
                assertThat(messages).hasSize(1)
                        .contains("[2行, 2列] : 項目「col_format」の値（abc）は、数値の書式「#,###」として不正です。");                
            }
            
        }
        
        /**
         * アノテーションの属性messageの指定
         */
        @Test
        public void testErrorMessage_message() {
            
            BeanMapping<TestCsv> beanMapping = beanMappingFactory.create(TestCsv.class, groupEmpty);
            ColumnMapping columnMapping = beanMapping.getColumnMapping("col_message").get();
            
            CellProcessor processor = columnMapping.getCellProcessorForReading();
            printCellProcessorChain(processor, name.getMethodName());
            
            String input = "abc";
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
         * アノテーションの属性messageの値が空文字の場合
         */
        @Test
        public void testErrorMessage_empty() {
            
            BeanMapping<TestCsv> beanMapping = beanMappingFactory.create(TestCsv.class, groupEmpty);
            ColumnMapping columnMapping = beanMapping.getColumnMapping("col_message_empty").get();
            
            CellProcessor processor = columnMapping.getCellProcessorForReading();
            printCellProcessorChain(processor, name.getMethodName());
            
            String input = "abc";
            try {
                processor.execute(input, testCsvContext(columnMapping, input));
                fail();
                
            } catch(Exception e) {
                assertThat(e).isInstanceOf(SuperCsvValidationException.class);
                
                SuperCsvValidationException validationException = (SuperCsvValidationException)e;
                assertThat(validationException.getValidationMessage()).isEqualTo("{com.github.mygreen.supercsv.cellprocessor.format.ParseProcessor.violated}");
                
                List<String> messages = exceptionConverter.convertAndFormat((SuperCsvValidationException)e, beanMapping);
                assertThat(messages).hasSize(1)
                        .contains("[2行, 11列] : 項目「col_message_empty」の値（abc）の書式は不正です。");                
            }
            
        }
        
        /**
         * アノテーションの属性messageの値が空文字の場合
         */
        @Test
        public void testErrorMessage_variables() {
            
            BeanMapping<TestCsv> beanMapping = beanMappingFactory.create(TestCsv.class, groupEmpty);
            ColumnMapping columnMapping = beanMapping.getColumnMapping("col_message_variables").get();
            
            CellProcessor processor = columnMapping.getCellProcessorForReading();
            printCellProcessorChain(processor, name.getMethodName());
            
            String input = "abc";
            try {
                processor.execute(input, testCsvContext(columnMapping, input));
                fail();
                
            } catch(Exception e) {
                assertThat(e).isInstanceOf(SuperCsvValidationException.class);
                
                List<String> messages = exceptionConverter.convertAndFormat((SuperCsvValidationException)e, beanMapping);
                assertThat(messages).hasSize(1)
                        .contains("lineNumber=1, rowNumber=2, columnNumber=12, label=col_message_variables, validatedValue=abc, pattern=#,###");                
            }
            
        }
        
        /**
         * プロパティファイルのtypeMismatchからメッセージを取得する場合
         */
        @Test
        public void testErrorMessage_typeMismatch() {
            
            // 独自のメッセージに入れ替え
            exceptionConverter.setMessageResolver(testMessageResolver);
            
            BeanMapping<TestCsv> beanMapping = beanMappingFactory.create(TestCsv.class, groupEmpty);
            ColumnMapping columnMapping = beanMapping.getColumnMapping("col_default").get();
            
            CellProcessor processor = columnMapping.getCellProcessorForReading();
            printCellProcessorChain(processor, name.getMethodName());
            
            String input = "abc";
            try {
                processor.execute(input, testCsvContext(columnMapping, input));
                fail();
                
            } catch(Exception e) {
                assertThat(e).isInstanceOf(SuperCsvValidationException.class);
                
                List<String> messages = exceptionConverter.convertAndFormat((SuperCsvValidationException)e, beanMapping);
                assertThat(messages).hasSize(1)
                        .contains("[2行, 1列] : 項目「col_default」の値（abc）は、整数の書式として不正です。");                
            }
            
        }
        
    }
    
}

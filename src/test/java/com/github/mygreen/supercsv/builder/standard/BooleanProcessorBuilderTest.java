package com.github.mygreen.supercsv.builder.standard;

import static org.junit.Assert.*;
import static org.assertj.core.api.Assertions.*;
import static com.github.mygreen.supercsv.tool.TestUtils.*;
import static com.github.mygreen.supercsv.tool.HasCellProcessorAssert.*;

import java.util.Collection;
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
import com.github.mygreen.supercsv.annotation.format.CsvBooleanFormat;
import com.github.mygreen.supercsv.builder.BeanMapping;
import com.github.mygreen.supercsv.builder.BeanMappingFactory;
import com.github.mygreen.supercsv.builder.ColumnMapping;
import com.github.mygreen.supercsv.exception.SuperCsvValidationException;
import com.github.mygreen.supercsv.localization.EncodingControl;
import com.github.mygreen.supercsv.localization.MessageResolver;
import com.github.mygreen.supercsv.localization.ResourceBundleMessageResolver;
import com.github.mygreen.supercsv.validation.CsvExceptionConverter;

/**
 * Test the {@link BooleanProcessorBuilder}
 *
 * @version 2.0
 * @since 1.2
 * @author T.TSUCHIE
 *
 */
@RunWith(Enclosed.class)
public class BooleanProcessorBuilderTest {
    
    /**
     * Tests for wrapper Boolean.
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
            
            this.testMessageResolver = new ResourceBundleMessageResolver(ResourceBundle.getBundle("TestMessages", new EncodingControl("UTF-8")));
        }
        
        private static final Boolean TEST_VALUE_TRUE_OBJ = Boolean.TRUE;
        private static final String TEST_VALUE_TRUE_STR_NORMAL = "true";
        private static final String TEST_VALUE_TRUE_STR_FORMATTED = "○";
        
        private static final Boolean TEST_VALUE_FALSE_OBJ = Boolean.FALSE;
        private static final String TEST_VALUE_FALSE_STR_NORMAL = "false";
        private static final String TEST_VALUE_FALSE_STR_FORMATTED = "×";
        
        @CsvBean
        private static class TestCsv {
            
            @CsvColumn(number=1)
            Boolean col_default;
            
            @CsvColumn(number=2)
            @CsvBooleanFormat(ignoreCase=true)
            Boolean col_ignoreCase;
            
            @CsvColumn(number=3)
            @CsvBooleanFormat(failToFalse=true)
            Boolean col_failToFalse;
            
            @CsvColumn(number=4)
            @CsvBooleanFormat(readForTrue={"○", "レ"}, readForFalse={"×", "ー"}, writeAsTrue="○", writeAsFalse="×")
            Boolean col_customValue;
            
            @CsvColumn(number=10)
            @CsvBooleanFormat(message="テストメッセージ")
            Boolean col_message;
            
            @CsvColumn(number=11)
            @CsvBooleanFormat(message="")
            Boolean col_message_empty;
            
            @CsvColumn(number=12)
            @CsvBooleanFormat(ignoreCase=true, failToFalse=false,
                    message="lineNumber={lineNumber}, rowNumber={rowNumber}, columnNumber={columnNumber}, label={label}, validatedValue={validatedValue}, trueValues=${f:join(trueValues, ', ')}, falseValues=${f:join(falseValues, ', ')}, ignoreCase={ignoreCase}, failToFalse={failToFalse}")
            Boolean col_message_variables;
            
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
                Boolean expected = null;
                assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
            }
            
            {
                // empty input
                String input = "";
                Boolean expected = null;
                assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
            }
            
            {
                // valid input - true
                String input = TEST_VALUE_TRUE_STR_NORMAL;
                Boolean expected = TEST_VALUE_TRUE_OBJ;
                assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
            }
            
            {
                // valid input - false
                String input = TEST_VALUE_FALSE_STR_NORMAL;
                Boolean expected = TEST_VALUE_FALSE_OBJ;
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
                    assertThat(validationException.getMessageVariables())
                            .containsKey("trueValues")
                            .containsKey("falseValues")
                            .containsEntry("ignoreCase", false)
                            .containsEntry("failToFalse", false);
                    
                    assertThat((Collection<String>)validationException.getMessageVariables().get("trueValues"))
                            .containsExactly("true", "1", "yes", "on", "y", "t");
                    
                    assertThat((Collection<String>)validationException.getMessageVariables().get("falseValues"))
                            .containsExactly("false", "0", "no", "off", "f", "n");
                    
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
                Boolean input = null;
                String expected = null;
                assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
            }
            
            {
                // valid input - true
                Boolean input = TEST_VALUE_TRUE_OBJ;
                String expected = TEST_VALUE_TRUE_STR_NORMAL;
                assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
            }
            
            {
                // valid input - false
                Boolean input = TEST_VALUE_FALSE_OBJ;
                String expected = TEST_VALUE_FALSE_STR_NORMAL;
                assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
            }
            
        }
        
        @Test
        public void testBuildForReading_ignoreCase() {
            
            BeanMapping<TestCsv> beanMapping = beanMappingFactory.create(TestCsv.class, groupEmpty);
            ColumnMapping columnMapping = beanMapping.getColumnMapping("col_ignoreCase").get();
            
            CellProcessor processor = columnMapping.getCellProcessorForReading();
            printCellProcessorChain(processor, name.getMethodName());
            
            {
                // null input
                String input = null;
                Boolean expected = null;
                assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
            }
            
            {
                // empty input
                String input = "";
                Boolean expected = null;
                assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
            }
            
            {
                // valid input - true
                String input = TEST_VALUE_TRUE_STR_NORMAL;
                Boolean expected = TEST_VALUE_TRUE_OBJ;
                assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
            }
            
            {
                // valid input - false
                String input = TEST_VALUE_FALSE_STR_NORMAL;
                Boolean expected = TEST_VALUE_FALSE_OBJ;
                assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
            }
            
            {
                // valid input - true upper case
                String input = TEST_VALUE_TRUE_STR_NORMAL.toUpperCase();
                Boolean expected = TEST_VALUE_TRUE_OBJ;
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
                    assertThat(validationException.getValidationMessage()).isEqualTo("{com.github.mygreen.supercsv.annotation.format.CsvBooleanFormat.message}");
                    assertThat(validationException.getMessageVariables())
                            .containsKey("trueValues")
                            .containsKey("falseValues")
                            .containsEntry("ignoreCase", true)
                            .containsEntry("failToFalse", false);
                    
                    assertThat((Collection<String>)validationException.getMessageVariables().get("trueValues"))
                            .containsExactly("true", "1", "yes", "on", "y", "t");
                    
                    assertThat((Collection<String>)validationException.getMessageVariables().get("falseValues"))
                            .containsExactly("false", "0", "no", "off", "f", "n");
                    
                }
            }
        }
        
        @Test
        public void testBuildForWriging_ignoreCase() {
            
            BeanMapping<TestCsv> beanMapping = beanMappingFactory.create(TestCsv.class, groupEmpty);
            ColumnMapping columnMapping = beanMapping.getColumnMapping("col_ignoreCase").get();
            
            CellProcessor processor = columnMapping.getCellProcessorForWriting();
            printCellProcessorChain(processor, name.getMethodName());
            
            {
                // null input
                Boolean input = null;
                String expected = null;
                assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
            }
            
            {
                // valid input - true
                Boolean input = TEST_VALUE_TRUE_OBJ;
                String expected = TEST_VALUE_TRUE_STR_NORMAL;
                assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
            }
            
            {
                // valid input - false
                Boolean input = TEST_VALUE_FALSE_OBJ;
                String expected = TEST_VALUE_FALSE_STR_NORMAL;
                assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
            }
            
        }
        
        @Test
        public void testBuildForReading_failToFalse() {
            
            BeanMapping<TestCsv> beanMapping = beanMappingFactory.create(TestCsv.class, groupEmpty);
            ColumnMapping columnMapping = beanMapping.getColumnMapping("col_failToFalse").get();
            
            CellProcessor processor = columnMapping.getCellProcessorForReading();
            printCellProcessorChain(processor, name.getMethodName());
            
            {
                // null input
                String input = null;
                Boolean expected = null;
                assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
            }
            
            {
                // empty input
                String input = "";
                Boolean expected = null;
                assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
            }
            
            {
                // valid input - true
                String input = TEST_VALUE_TRUE_STR_NORMAL;
                Boolean expected = TEST_VALUE_TRUE_OBJ;
                assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
            }
            
            {
                // valid input - false
                String input = TEST_VALUE_FALSE_STR_NORMAL;
                Boolean expected = TEST_VALUE_FALSE_OBJ;
                assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
            }
            
            {
                // wrong input
                String input = "abc";
                Boolean expected = TEST_VALUE_FALSE_OBJ;
                assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
            }
            
        }
        
        @Test
        public void testBuildForWriging_failToFalse() {
            
            BeanMapping<TestCsv> beanMapping = beanMappingFactory.create(TestCsv.class, groupEmpty);
            ColumnMapping columnMapping = beanMapping.getColumnMapping("col_failToFalse").get();
            
            CellProcessor processor = columnMapping.getCellProcessorForWriting();
            printCellProcessorChain(processor, name.getMethodName());
            
            {
                // null input
                Boolean input = null;
                String expected = null;
                assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
            }
            
            {
                // valid input - true
                Boolean input = TEST_VALUE_TRUE_OBJ;
                String expected = TEST_VALUE_TRUE_STR_NORMAL;
                assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
            }
            
            {
                // valid input - false
                Boolean input = TEST_VALUE_FALSE_OBJ;
                String expected = TEST_VALUE_FALSE_STR_NORMAL;
                assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
            }
            
        }
        
        @Test
        public void testBuildForReading_customValue() {
            
            BeanMapping<TestCsv> beanMapping = beanMappingFactory.create(TestCsv.class, groupEmpty);
            ColumnMapping columnMapping = beanMapping.getColumnMapping("col_customValue").get();
            
            CellProcessor processor = columnMapping.getCellProcessorForReading();
            printCellProcessorChain(processor, name.getMethodName());
            
            {
                // null input
                String input = null;
                String expected = null;
                assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
            }
            
            {
                // empty input
                String input = "";
                Boolean expected = null;
                assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
            }
            
            {
                // valid input - true
                String input = TEST_VALUE_TRUE_STR_FORMATTED;
                Boolean expected = TEST_VALUE_TRUE_OBJ;
                assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
            }
            
            {
                // valid input - false
                String input = TEST_VALUE_FALSE_STR_FORMATTED;
                Boolean expected = TEST_VALUE_FALSE_OBJ;
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
                    assertThat(validationException.getValidationMessage()).isEqualTo("{com.github.mygreen.supercsv.annotation.format.CsvBooleanFormat.message}");
                    assertThat(validationException.getMessageVariables())
                            .containsKey("trueValues")
                            .containsKey("falseValues")
                            .containsEntry("ignoreCase", false)
                            .containsEntry("failToFalse", false);
                    
                    assertThat((Collection<String>)validationException.getMessageVariables().get("trueValues"))
                            .containsExactly("○", "レ");
                    
                    assertThat((Collection<String>)validationException.getMessageVariables().get("falseValues"))
                            .containsExactly("×", "ー");
                    
                }
            }
        }
        
        @Test
        public void testBuildForWriting_customValue() {
            
            BeanMapping<TestCsv> beanMapping = beanMappingFactory.create(TestCsv.class, groupEmpty);
            ColumnMapping columnMapping = beanMapping.getColumnMapping("col_customValue").get();
            
            CellProcessor processor = columnMapping.getCellProcessorForWriting();
            printCellProcessorChain(processor, name.getMethodName());
            
            {
                // null input
                Boolean input = null;
                String expected = null;
                assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
            }
            
            {
                // valid input - true
                Boolean input = TEST_VALUE_TRUE_OBJ;
                String expected = TEST_VALUE_TRUE_STR_FORMATTED;
                assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
            }
            
            {
                // valid input - false
                Boolean input = TEST_VALUE_FALSE_OBJ;
                String expected = TEST_VALUE_FALSE_STR_FORMATTED;
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
         * アノテーションの属性readForTrue/readForFalseを指定した場合
         */
        @Test
        public void testErrorMessage_customValue() {
            
            BeanMapping<TestCsv> beanMapping = beanMappingFactory.create(TestCsv.class, groupEmpty);
            ColumnMapping columnMapping = beanMapping.getColumnMapping("col_customValue").get();
            
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
                        .contains("[2行, 4列] : 項目「col_customValue」の値（abc）は、trueの値「○, レ」、またはfalseの値「×, ー」の何れかの値で設定してください。");                
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
                        .contains("lineNumber=1, rowNumber=2, columnNumber=12, label=col_message_variables, validatedValue=abc, trueValues=true, 1, yes, on, y, t, falseValues=false, 0, no, off, f, n, ignoreCase=true, failToFalse=false");                
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
                        .contains("[2行, 1列] : 項目「col_default」の値（abc）は、trueの値「true, 1, yes, on, y, t」、またはfalseの値「false, 0, no, off, f, n」の何れかの値で設定してください。");                
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
        
        private static final boolean TEST_VALUE_PRIMITIVE_INIT_OBJ = false;
        
        private static final boolean TEST_VALUE_TRUE_OBJ = Boolean.TRUE;
        private static final String TEST_VALUE_TRUE_STR_NORMAL = "true";
        private static final String TEST_VALUE_TRUE_STR_FORMATTED = "○";
        
        private static final boolean TEST_VALUE_FALSE_OBJ = Boolean.FALSE;
        private static final String TEST_VALUE_FALSE_STR_NORMAL = "false";
        private static final String TEST_VALUE_FALSE_STR_FORMATTED = "×";
        
        @CsvBean
        private static class TestCsv {
            
            @CsvColumn(number=1)
            boolean col_default;
            
            @CsvColumn(number=2)
            @CsvBooleanFormat(ignoreCase=true)
            boolean col_ignoreCase;
            
            @CsvColumn(number=3)
            @CsvBooleanFormat(failToFalse=true)
            boolean col_failToFalse;
            
            @CsvColumn(number=4)
            @CsvBooleanFormat(readForTrue={"○", "レ"}, readForFalse={"×", "ー"}, writeAsTrue="○", writeAsFalse="×")
            boolean col_customValue;
            
            @CsvColumn(number=10)
            @CsvBooleanFormat(message="テストメッセージ")
            boolean col_message;
            
            @CsvColumn(number=11)
            @CsvBooleanFormat(message="")
            boolean col_message_empty;
            
            @CsvColumn(number=12)
            @CsvBooleanFormat(ignoreCase=true, failToFalse=false,
                    message="lineNumber={lineNumber}, rowNumber={rowNumber}, columnNumber={columnNumber}, label={label}, validatedValue={validatedValue}, trueValues=${f:join(trueValues, ', ')}, falseValues=${f:join(falseValues, ', ')}, ignoreCase={ignoreCase}, failToFalse={failToFalse}")
            boolean col_message_variables;
            
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
                boolean expected = TEST_VALUE_PRIMITIVE_INIT_OBJ;
                assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
            }
            
            {
                // empty input
                String input = "";
                boolean expected = TEST_VALUE_PRIMITIVE_INIT_OBJ;
                assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
            }
            
            {
                // valid input - true
                String input = TEST_VALUE_TRUE_STR_NORMAL;
                boolean expected = TEST_VALUE_TRUE_OBJ;
                assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
            }
            
            {
                // valid input - false
                String input = TEST_VALUE_FALSE_STR_NORMAL;
                boolean expected = TEST_VALUE_FALSE_OBJ;
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
                    assertThat(validationException.getMessageVariables())
                            .containsKey("trueValues")
                            .containsKey("falseValues")
                            .containsEntry("ignoreCase", false)
                            .containsEntry("failToFalse", false);
                    
                    assertThat((Collection<String>)validationException.getMessageVariables().get("trueValues"))
                            .containsExactly("true", "1", "yes", "on", "y", "t");
                    
                    assertThat((Collection<String>)validationException.getMessageVariables().get("falseValues"))
                            .containsExactly("false", "0", "no", "off", "f", "n");
                    
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
                // valid input - true
                boolean input = TEST_VALUE_TRUE_OBJ;
                String expected = TEST_VALUE_TRUE_STR_NORMAL;
                assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
            }
            
            {
                // valid input - false
                boolean input = TEST_VALUE_FALSE_OBJ;
                String expected = TEST_VALUE_FALSE_STR_NORMAL;
                assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
            }
            
        }
        
        @Test
        public void testBuildForReading_ignoreCase() {
            
            BeanMapping<TestCsv> beanMapping = beanMappingFactory.create(TestCsv.class, groupEmpty);
            ColumnMapping columnMapping = beanMapping.getColumnMapping("col_ignoreCase").get();
            
            CellProcessor processor = columnMapping.getCellProcessorForReading();
            printCellProcessorChain(processor, name.getMethodName());
            
            {
                // null input
                String input = null;
                boolean expected = TEST_VALUE_PRIMITIVE_INIT_OBJ;
                assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
            }
            
            {
                // empty input
                String input = "";
                boolean expected = TEST_VALUE_PRIMITIVE_INIT_OBJ;
                assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
            }
            
            {
                // valid input - true
                String input = TEST_VALUE_TRUE_STR_NORMAL;
                boolean expected = TEST_VALUE_TRUE_OBJ;
                assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
            }
            
            {
                // valid input - false
                String input = TEST_VALUE_FALSE_STR_NORMAL;
                boolean expected = TEST_VALUE_FALSE_OBJ;
                assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
            }
            
            {
                // valid input - true upper case
                String input = TEST_VALUE_TRUE_STR_NORMAL.toUpperCase();
                boolean expected = TEST_VALUE_TRUE_OBJ;
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
                    assertThat(validationException.getValidationMessage()).isEqualTo("{com.github.mygreen.supercsv.annotation.format.CsvBooleanFormat.message}");
                    assertThat(validationException.getMessageVariables())
                            .containsKey("trueValues")
                            .containsKey("falseValues")
                            .containsEntry("ignoreCase", true)
                            .containsEntry("failToFalse", false);
                    
                    assertThat((Collection<String>)validationException.getMessageVariables().get("trueValues"))
                            .containsExactly("true", "1", "yes", "on", "y", "t");
                    
                    assertThat((Collection<String>)validationException.getMessageVariables().get("falseValues"))
                            .containsExactly("false", "0", "no", "off", "f", "n");
                    
                }
            }
        }
        
        @Test
        public void testBuildForWriging_ignoreCase() {
            
            BeanMapping<TestCsv> beanMapping = beanMappingFactory.create(TestCsv.class, groupEmpty);
            ColumnMapping columnMapping = beanMapping.getColumnMapping("col_ignoreCase").get();
            
            CellProcessor processor = columnMapping.getCellProcessorForWriting();
            printCellProcessorChain(processor, name.getMethodName());
            
            {
                // valid input - true
                boolean input = TEST_VALUE_TRUE_OBJ;
                String expected = TEST_VALUE_TRUE_STR_NORMAL;
                assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
            }
            
            {
                // valid input - false
                boolean input = TEST_VALUE_FALSE_OBJ;
                String expected = TEST_VALUE_FALSE_STR_NORMAL;
                assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
            }
            
        }
        
        @Test
        public void testBuildForReading_failToFalse() {
            
            BeanMapping<TestCsv> beanMapping = beanMappingFactory.create(TestCsv.class, groupEmpty);
            ColumnMapping columnMapping = beanMapping.getColumnMapping("col_failToFalse").get();
            
            CellProcessor processor = columnMapping.getCellProcessorForReading();
            printCellProcessorChain(processor, name.getMethodName());
            
            {
                // null input
                String input = null;
                boolean expected = TEST_VALUE_PRIMITIVE_INIT_OBJ;
                assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
            }
            
            {
                // empty input
                String input = "";
                boolean expected = TEST_VALUE_PRIMITIVE_INIT_OBJ;
                assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
            }
            
            {
                // valid input - true
                String input = TEST_VALUE_TRUE_STR_NORMAL;
                boolean expected = TEST_VALUE_TRUE_OBJ;
                assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
            }
            
            {
                // valid input - false
                String input = TEST_VALUE_FALSE_STR_NORMAL;
                boolean expected = TEST_VALUE_FALSE_OBJ;
                assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
            }
            
            {
                // wrong input
                String input = "abc";
                boolean expected = TEST_VALUE_FALSE_OBJ;
                assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
            }
            
        }
        
        @Test
        public void testBuildForWriging_failToFalse() {
            
            BeanMapping<TestCsv> beanMapping = beanMappingFactory.create(TestCsv.class, groupEmpty);
            ColumnMapping columnMapping = beanMapping.getColumnMapping("col_failToFalse").get();
            
            CellProcessor processor = columnMapping.getCellProcessorForWriting();
            printCellProcessorChain(processor, name.getMethodName());
            
            
            {
                // valid input - true
                boolean input = TEST_VALUE_TRUE_OBJ;
                String expected = TEST_VALUE_TRUE_STR_NORMAL;
                assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
            }
            
            {
                // valid input - false
                boolean input = TEST_VALUE_FALSE_OBJ;
                String expected = TEST_VALUE_FALSE_STR_NORMAL;
                assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
            }
            
        }
        
        @Test
        public void testBuildForReading_customValue() {
            
            BeanMapping<TestCsv> beanMapping = beanMappingFactory.create(TestCsv.class, groupEmpty);
            ColumnMapping columnMapping = beanMapping.getColumnMapping("col_customValue").get();
            
            CellProcessor processor = columnMapping.getCellProcessorForReading();
            printCellProcessorChain(processor, name.getMethodName());
            
            {
                // null input
                String input = null;
                boolean expected = TEST_VALUE_PRIMITIVE_INIT_OBJ;
                assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
            }
            
            {
                // empty input
                String input = "";
                boolean expected = TEST_VALUE_PRIMITIVE_INIT_OBJ;
                assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
            }
            
            {
                // valid input - true
                String input = TEST_VALUE_TRUE_STR_FORMATTED;
                boolean expected = TEST_VALUE_TRUE_OBJ;
                assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
            }
            
            {
                // valid input - false
                String input = TEST_VALUE_FALSE_STR_FORMATTED;
                boolean expected = TEST_VALUE_FALSE_OBJ;
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
                    assertThat(validationException.getValidationMessage()).isEqualTo("{com.github.mygreen.supercsv.annotation.format.CsvBooleanFormat.message}");
                    assertThat(validationException.getMessageVariables())
                            .containsKey("trueValues")
                            .containsKey("falseValues")
                            .containsEntry("ignoreCase", false)
                            .containsEntry("failToFalse", false);
                    
                    assertThat((Collection<String>)validationException.getMessageVariables().get("trueValues"))
                            .containsExactly("○", "レ");
                    
                    assertThat((Collection<String>)validationException.getMessageVariables().get("falseValues"))
                            .containsExactly("×", "ー");
                    
                }
            }
        }
        
        @Test
        public void testBuildForWriting_customValue() {
            
            BeanMapping<TestCsv> beanMapping = beanMappingFactory.create(TestCsv.class, groupEmpty);
            ColumnMapping columnMapping = beanMapping.getColumnMapping("col_customValue").get();
            
            CellProcessor processor = columnMapping.getCellProcessorForWriting();
            printCellProcessorChain(processor, name.getMethodName());
            
            {
                // valid input - true
                boolean input = TEST_VALUE_TRUE_OBJ;
                String expected = TEST_VALUE_TRUE_STR_FORMATTED;
                assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
            }
            
            {
                // valid input - false
                boolean input = TEST_VALUE_FALSE_OBJ;
                String expected = TEST_VALUE_FALSE_STR_FORMATTED;
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
         * アノテーションの属性readForTrue/readForFalseを指定した場合
         */
        @Test
        public void testErrorMessage_customValue() {
            
            BeanMapping<TestCsv> beanMapping = beanMappingFactory.create(TestCsv.class, groupEmpty);
            ColumnMapping columnMapping = beanMapping.getColumnMapping("col_customValue").get();
            
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
                        .contains("[2行, 4列] : 項目「col_customValue」の値（abc）は、trueの値「○, レ」、またはfalseの値「×, ー」の何れかの値で設定してください。");                
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
                        .contains("lineNumber=1, rowNumber=2, columnNumber=12, label=col_message_variables, validatedValue=abc, trueValues=true, 1, yes, on, y, t, falseValues=false, 0, no, off, f, n, ignoreCase=true, failToFalse=false");                
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
                        .contains("[2行, 1列] : 項目「col_default」の値（abc）は、trueの値「true, 1, yes, on, y, t」、またはfalseの値「false, 0, no, off, f, n」の何れかの値で設定してください。");                
            }
            
        }
        
    }
    
}

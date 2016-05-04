package org.supercsv.ext.builder.impl;

import static org.junit.Assert.*;
import static org.supercsv.ext.tool.TestUtils.*;
import static org.hamcrest.Matchers.*;
import static org.supercsv.ext.tool.HasCellProcessor.*;

import java.lang.annotation.Annotation;
import java.math.RoundingMode;
import java.util.Objects;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.supercsv.cellprocessor.ConvertNullTo;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.constraint.Equals;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.constraint.Unique;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.exception.SuperCsvCellProcessorException;
import org.supercsv.exception.SuperCsvConstraintViolationException;
import org.supercsv.ext.annotation.CsvBean;
import org.supercsv.ext.annotation.CsvColumn;
import org.supercsv.ext.annotation.CsvNumberConverter;
import org.supercsv.ext.cellprocessor.FormatLocaleNumber;
import org.supercsv.ext.cellprocessor.ParseLocaleNumber;
import org.supercsv.ext.cellprocessor.ParseByte;
import org.supercsv.ext.cellprocessor.Trim;
import org.supercsv.ext.cellprocessor.constraint.Max;
import org.supercsv.ext.cellprocessor.constraint.Min;
import org.supercsv.ext.cellprocessor.constraint.Range;
import org.supercsv.ext.exception.SuperCsvInvalidAnnotationException;

/**
 * Test the {@link ByteCellProcessorBuilder} CellProcessor.
 * 
 * @since 1.2
 * @author T.TSUCHIE
 *
 */
@RunWith(Enclosed.class)
public class ByteCellProcessorBuilderTest {
    
    /**
     * Tests for wrapper byte.
     *
     */
    public static class ByteTest {
    
        @Rule
        public TestName name = new TestName();
        
        private ByteCellProcessorBuilder builder;
        
        /**
         * Sets up the processor for the test using Combinations
         */
        @Before
        public void setUp() {
            builder = new ByteCellProcessorBuilder();
        }
        
        /** for min/max diff value */
        private static final Byte TEST_VALUE_DIFF = toByte("1");
        
        private static final String TEST_FORMATTED_PATTERN = "#,###";
        
        private static final Byte TEST_VALUE_1_OBJ = toByte("12");
        private static final String TEST_VALUE_1_STR_NORMAL = "12";
        private static final String TEST_VALUE_1_STR_FORMATTED = "12";
        
        private static final Byte TEST_VALUE_2_OBJ = toByte("-23");
        private static final String TEST_VALUE_2_STR_NORMAL = "-23";
        private static final String TEST_VALUE_2_STR_FORMATTED = "-23";
        
        private static final Byte TEST_VALUE_INPUT_DEFAULT_OBJ = toByte("11");
        private static final String TEST_VALUE_INPUT_DEFAULT_STR_NORMAL = "11";
        private static final String TEST_VALUE_INPUT_DEFAULT_STR_FORMATTED = "11";
        
        private static final Byte TEST_VALUE_OUTPUT_DEFAULT_OBJ = toByte("-22");
        private static final String TEST_VALUE_OUTPUT_DEFAULT_STR_NORMAL = "-22";
        private static final String TEST_VALUE_OUTPUT_DEFAULT_STR_FORMATTED = "-22";
        
        private static final Byte TEST_VALUE_MIN_OBJ = toByte("-43");
        private static final String TEST_VALUE_MIN_STR_NORMAL = "-43";
        private static final String TEST_VALUE_MIN_STR_FORMATTED = "-43";
        
        private static final Byte TEST_VALUE_MAX_OBJ = toByte("87");
        private static final String TEST_VALUE_MAX_STR_NORMAL = "87";
        private static final String TEST_VALUE_MAX_STR_FORMATTED = "87";
        
        @CsvBean
        private static class TestCsv {
            
            @CsvColumn(position=0)
            Byte byte_default;
            
            @CsvColumn(position=1, optional=true)
            Byte byte_optional;
            
            @CsvColumn(position=2, trim=true)
            Byte byte_trim;
            
            @CsvColumn(position=3, inputDefaultValue=TEST_VALUE_INPUT_DEFAULT_STR_NORMAL, outputDefaultValue=TEST_VALUE_OUTPUT_DEFAULT_STR_NORMAL)
            Byte byte_defaultValue;
            
            @CsvColumn(position=4, inputDefaultValue=TEST_VALUE_INPUT_DEFAULT_STR_FORMATTED, outputDefaultValue=TEST_VALUE_OUTPUT_DEFAULT_STR_FORMATTED)
            @CsvNumberConverter(pattern=TEST_FORMATTED_PATTERN)
            Byte byte_defaultValue_format;
            
            @CsvColumn(position=4, inputDefaultValue="abc12,345")
            @CsvNumberConverter(pattern=TEST_FORMATTED_PATTERN)
            Byte byte_defaultValue_format_invalid;
            
            @CsvColumn(position=5, equalsValue=TEST_VALUE_1_STR_NORMAL)
            Byte byte_equalsValue;
            
            @CsvColumn(position=6, equalsValue=TEST_VALUE_1_STR_FORMATTED)
            @CsvNumberConverter(pattern=TEST_FORMATTED_PATTERN)
            Byte byte_equalsValue_format;
            
            @CsvColumn(position=7, unique=true)
            Byte byte_unique;
            
            @CsvColumn(position=8, unique=true)
            @CsvNumberConverter(pattern=TEST_FORMATTED_PATTERN)
            Byte byte_unique_format;
            
            @CsvColumn(position=9, optional=true, trim=true, inputDefaultValue=TEST_VALUE_INPUT_DEFAULT_STR_NORMAL, outputDefaultValue=TEST_VALUE_OUTPUT_DEFAULT_STR_NORMAL,
                    equalsValue=TEST_VALUE_1_STR_NORMAL, unique=true)
            Byte byte_combine1;
            
            @CsvColumn(position=10, optional=true, trim=true, inputDefaultValue=TEST_VALUE_INPUT_DEFAULT_STR_FORMATTED, outputDefaultValue=TEST_VALUE_OUTPUT_DEFAULT_STR_FORMATTED,
                    equalsValue=TEST_VALUE_1_STR_FORMATTED, unique=true)
            @CsvNumberConverter(pattern=TEST_FORMATTED_PATTERN)
            Byte byte_combine_format1;
            
            @CsvColumn(position=11)
            @CsvNumberConverter(min=TEST_VALUE_MIN_STR_NORMAL)
            Byte byte_min;
            
            @CsvColumn(position=12)
            @CsvNumberConverter(min=TEST_VALUE_MIN_STR_FORMATTED, pattern=TEST_FORMATTED_PATTERN)
            Byte byte_min_format;
            
            @CsvColumn(position=13)
            @CsvNumberConverter(max=TEST_VALUE_MAX_STR_NORMAL)
            Byte byte_max;
            
            @CsvColumn(position=14)
            @CsvNumberConverter(max=TEST_VALUE_MAX_STR_FORMATTED, pattern=TEST_FORMATTED_PATTERN)
            Byte byte_max_format;
            
            @CsvColumn(position=15)
            @CsvNumberConverter(min=TEST_VALUE_MIN_STR_NORMAL, max=TEST_VALUE_MAX_STR_NORMAL)
            Byte byte_range;
            
            @CsvColumn(position=16)
            @CsvNumberConverter(min=TEST_VALUE_MIN_STR_FORMATTED, max=TEST_VALUE_MAX_STR_FORMATTED, pattern=TEST_FORMATTED_PATTERN)
            Byte byte_range_format;
            
            @CsvColumn(position=17)
            @CsvNumberConverter(pattern="#,##0.0##", lenient=true)
            Byte byte_format_lenient;
            
            @CsvColumn(position=18)
            @CsvNumberConverter(pattern="\u00A4 #,##0.0000", currency="USD")
            Byte byte_format_currency;
            
            @CsvColumn(position=19)
            @CsvNumberConverter(pattern="#,##0", rounding=RoundingMode.HALF_UP)
            Byte byte_format_roundingMode;
            
        }
        
        /**
         * Tests with default. (not grant convert annotation.)
         */
        @Test
        public void testBuildInput_default() {
            
            Annotation[] annos = getAnnotations(TestCsv.class, "byte_default");
            CellProcessor cellProcessor = builder.buildInputCellProcessor(Byte.class, annos);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, hasCellProcessor(NotNull.class));
            assertThat(cellProcessor, hasCellProcessor(ParseByte.class));
            
            assertThat(cellProcessor.execute(TEST_VALUE_1_STR_NORMAL, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_1_OBJ));
            
            // null input
            try {
                cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(NotNull.class)));
            }
            
            // wrong pattern
            try {
                cellProcessor.execute("abc", ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvCellProcessorException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(ParseByte.class)));
            }
            
        }
        
        /**
         * Tests with default. (not grant convert annotation.)
         */
        @Test
        public void testBuildOutput_default() {
            
            Annotation[] annos = getAnnotations(TestCsv.class, "byte_default");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(Byte.class, annos, false);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, hasCellProcessor(NotNull.class));
            
            assertThat(cellProcessor.execute(TEST_VALUE_1_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_1_OBJ));
            
            // null input
            try {
                cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(NotNull.class)));
            }
            
        }
        
        /**
         * Tests with default. (not grant convert annotation.)
         */
        @Test
        public void testBuildOutput_default_ignoreValidation() {
            
            Annotation[] annos = getAnnotations(TestCsv.class, "byte_default");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(Byte.class, annos, true);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, hasCellProcessor(NotNull.class));
            
            assertThat(cellProcessor.execute(TEST_VALUE_1_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_1_OBJ));
            
            // null input
            try {
                cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(NotNull.class)));
            }
        }
        
        /**
         * Tests with optional. (not grant convert annotation.)
         */
        @Test
        public void testBuildInput_optional() {
            
            Annotation[] annos = getAnnotations(TestCsv.class, "byte_optional");
            CellProcessor cellProcessor = builder.buildInputCellProcessor(Byte.class, annos);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, hasCellProcessor(Optional.class));
            
            assertThat(cellProcessor.execute(TEST_VALUE_1_STR_NORMAL, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_1_OBJ));
            
            // null input
            assertThat(cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT), is(nullValue()));
        }
        
        /**
         * Tests with optional. (not grant convert annotation.)
         */
        @Test
        public void testBuildOutput_optional() {
            
            Annotation[] annos = getAnnotations(TestCsv.class, "byte_optional");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(Byte.class, annos, false);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, hasCellProcessor(Optional.class));
            
            assertThat(cellProcessor.execute(TEST_VALUE_1_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_1_OBJ));
            
            // null input
            assertThat(cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT), is(nullValue()));
        }
        
        /**
         * Tests with optional. (not grant convert annotation.)
         */
        @Test
        public void testBuildOutput_optional_ignoreValidation() {
            
            Annotation[] annos = getAnnotations(TestCsv.class, "byte_optional");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(Byte.class, annos, true);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, hasCellProcessor(Optional.class));
            
            assertThat(cellProcessor.execute(TEST_VALUE_1_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_1_OBJ));
            
            // null input
            assertThat(cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT), is(nullValue()));
        }
        
        @Test
        public void testBuildInput_trim() {
            Annotation[] annos = getAnnotations(TestCsv.class, "byte_trim");
            CellProcessor cellProcessor = builder.buildInputCellProcessor(Byte.class, annos);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, hasCellProcessor(Trim.class));
            
            assertThat(cellProcessor.execute("  " + TEST_VALUE_1_STR_NORMAL + " ", ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_1_OBJ));
        }
        
        @Test
        public void testBuildOutput_trim() {
            Annotation[] annos = getAnnotations(TestCsv.class, "byte_trim");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(Byte.class, annos, false);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, hasCellProcessor(Trim.class));
            
            assertThat(cellProcessor.execute(TEST_VALUE_1_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_1_STR_NORMAL));
        }
        
        @Test
        public void testBuildOutput_trim_ignoreValidation() {
            Annotation[] annos = getAnnotations(TestCsv.class, "byte_trim");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(Byte.class, annos, true);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, hasCellProcessor(Trim.class));
            
            assertThat(cellProcessor.execute(TEST_VALUE_1_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_1_STR_NORMAL));
        }
        
        @Test
        public void testBuildInput_defaultValue() {
            Annotation[] annos = getAnnotations(TestCsv.class, "byte_defaultValue");
            CellProcessor cellProcessor = builder.buildInputCellProcessor(Byte.class, annos);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, hasCellProcessor(ConvertNullTo.class));
            
            assertThat(cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_INPUT_DEFAULT_OBJ));
        }
        
        @Test
        public void testBuildOutput_defaultValue() {
            Annotation[] annos = getAnnotations(TestCsv.class, "byte_defaultValue");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(Byte.class, annos, false);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, hasCellProcessor(ConvertNullTo.class));
            
            assertThat(cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_OUTPUT_DEFAULT_STR_NORMAL));
        }
        
        @Test
        public void testBuildOutput_defaultValue_ignoreValidation() {
            Annotation[] annos = getAnnotations(TestCsv.class, "byte_defaultValue");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(Byte.class, annos, true);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, hasCellProcessor(ConvertNullTo.class));
            
            assertThat(cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_OUTPUT_DEFAULT_STR_NORMAL));
        }
        
        @Test
        public void testBuildInput_defaultValue_format() {
            Annotation[] annos = getAnnotations(TestCsv.class, "byte_defaultValue_format");
            CellProcessor cellProcessor = builder.buildInputCellProcessor(Byte.class, annos);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, hasCellProcessor(ConvertNullTo.class));
            
            assertThat(cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_INPUT_DEFAULT_OBJ));
        }
        
        @Test
        public void testBuildOutput_defaultValue_format() {
            Annotation[] annos = getAnnotations(TestCsv.class, "byte_defaultValue_format");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(Byte.class, annos, false);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, hasCellProcessor(ConvertNullTo.class));
            
            assertThat(cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_OUTPUT_DEFAULT_STR_FORMATTED));
        }
        
        @Test
        public void testBuildOutput_defaultValue_format_ignoreValidation() {
            Annotation[] annos = getAnnotations(TestCsv.class, "byte_defaultValue_format");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(Byte.class, annos, true);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, hasCellProcessor(ConvertNullTo.class));
            
            assertThat(cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_OUTPUT_DEFAULT_STR_FORMATTED));
        }
        
        /**
         * Tests with default. (not grant convert annotation.)
         */
        @Test(expected=SuperCsvInvalidAnnotationException.class)
        public void testBuildInput_default_format_invalidAnnotation() {
            
            Annotation[] annos = getAnnotations(TestCsv.class, "byte_defaultValue_format_invalid");
            CellProcessor cellProcessor = builder.buildInputCellProcessor(Byte.class, annos);
            
            cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT);
            fail();
            
        }
        
        @Test
        public void testBuildInput_equalsValue() {
            Annotation[] annos = getAnnotations(TestCsv.class, "byte_equalsValue");
            CellProcessor cellProcessor = builder.buildInputCellProcessor(Byte.class, annos);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, hasCellProcessor(Equals.class));
            
            assertThat(cellProcessor.execute(TEST_VALUE_1_STR_NORMAL, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_1_OBJ));
            
            // not quals input
            try {
                cellProcessor.execute(TEST_VALUE_2_STR_NORMAL, ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Equals.class)));
            }
        }
        
        @Test
        public void testBuildOutput_equalsValue() {
            Annotation[] annos = getAnnotations(TestCsv.class, "byte_equalsValue");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(Byte.class, annos, false);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, hasCellProcessor(Equals.class));
            
            assertThat(cellProcessor.execute(TEST_VALUE_1_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_1_OBJ));
            
            // not quals input
            try {
                cellProcessor.execute(TEST_VALUE_2_OBJ, ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Equals.class)));
            }
        }
        
        @Test
        public void testBuildOutput_equalsValue_ignoreValidation() {
            Annotation[] annos = getAnnotations(TestCsv.class, "byte_equalsValue");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(Byte.class, annos, true);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, not(hasCellProcessor(Equals.class)));
            
            assertThat(cellProcessor.execute(TEST_VALUE_1_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_1_OBJ));
            
            // not quals input
            assertThat(cellProcessor.execute(TEST_VALUE_2_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_2_OBJ));
            
        }
        
        @Test
        public void testBuildInput_equalsValue_format() {
            Annotation[] annos = getAnnotations(TestCsv.class, "byte_equalsValue_format");
            CellProcessor cellProcessor = builder.buildInputCellProcessor(Byte.class, annos);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, hasCellProcessor(Equals.class));
            
            assertThat(cellProcessor.execute(TEST_VALUE_1_STR_FORMATTED, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_1_OBJ));
            
            // not quals input
            try {
                cellProcessor.execute(TEST_VALUE_2_STR_FORMATTED, ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Equals.class)));
            }
        }
        
        @Test
        public void testBuildOutput_equalsValue_format() {
            Annotation[] annos = getAnnotations(TestCsv.class, "byte_equalsValue_format");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(Byte.class, annos, false);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, hasCellProcessor(Equals.class));
            
            assertThat(cellProcessor.execute(TEST_VALUE_1_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_1_STR_FORMATTED));
            
            // not quals input
            try {
                cellProcessor.execute(TEST_VALUE_2_OBJ, ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Equals.class)));
            }
        }
        
        @Test
        public void testBuildOutput_equalsValue_format_ignoreValidation() {
            Annotation[] annos = getAnnotations(TestCsv.class, "byte_equalsValue_format");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(Byte.class, annos, true);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, not(hasCellProcessor(Equals.class)));
            
            assertThat(cellProcessor.execute(TEST_VALUE_1_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_1_STR_FORMATTED));
            
            // not quals input
            assertThat(cellProcessor.execute(TEST_VALUE_2_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_2_STR_FORMATTED));
            
        }
        
        @Test
        public void testBuildInput_unique() {
            Annotation[] annos = getAnnotations(TestCsv.class, "byte_unique");
            CellProcessor cellProcessor = builder.buildInputCellProcessor(Byte.class, annos);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, hasCellProcessor(Unique.class));
            
            assertThat(cellProcessor.execute(TEST_VALUE_1_STR_NORMAL, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_1_OBJ));
            assertThat(cellProcessor.execute(TEST_VALUE_2_STR_NORMAL, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_2_OBJ));
            
            // not unique input
            try {
                cellProcessor.execute(TEST_VALUE_1_STR_NORMAL, ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Unique.class)));
            }
        }
        
        @Test
        public void testBuildOutput_unique() {
            Annotation[] annos = getAnnotations(TestCsv.class, "byte_unique");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(Byte.class, annos, false);
            printCellProcessorChain(cellProcessor, name.getMethodName());
           
            assertThat(cellProcessor, hasCellProcessor(Unique.class));
            
            assertThat(cellProcessor.execute(TEST_VALUE_1_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_1_OBJ));
            assertThat(cellProcessor.execute(TEST_VALUE_2_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_2_OBJ));
            
            // not unique input
            try {
                cellProcessor.execute(TEST_VALUE_1_OBJ, ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Unique.class)));
            }
        }
        
        @Test
        public void testBuildOutput_unique_ignoreValidation() {
            Annotation[] annos = getAnnotations(TestCsv.class, "byte_unique");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(Byte.class, annos, true);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, not(hasCellProcessor(Unique.class)));
            
            assertThat(cellProcessor.execute(TEST_VALUE_1_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_1_OBJ));
            assertThat(cellProcessor.execute(TEST_VALUE_2_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_2_OBJ));
            assertThat(cellProcessor.execute(TEST_VALUE_1_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_1_OBJ));
            
        }
        
        @Test
        public void testBuildInput_unique_format() {
            Annotation[] annos = getAnnotations(TestCsv.class, "byte_unique_format");
            CellProcessor cellProcessor = builder.buildInputCellProcessor(Byte.class, annos);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, hasCellProcessor(Unique.class));
            
            assertThat(cellProcessor.execute(TEST_VALUE_1_STR_FORMATTED, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_1_OBJ));
            assertThat(cellProcessor.execute(TEST_VALUE_2_STR_FORMATTED, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_2_OBJ));
            
            // not unique input
            try {
                cellProcessor.execute(TEST_VALUE_1_STR_FORMATTED, ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Unique.class)));
            }
        }
        
        @Test
        public void testBuildOutput_unique_format() {
            Annotation[] annos = getAnnotations(TestCsv.class, "byte_unique_format");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(Byte.class, annos, false);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, hasCellProcessor(Unique.class));
            
            assertThat(cellProcessor.execute(TEST_VALUE_1_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_1_STR_FORMATTED));
            assertThat(cellProcessor.execute(TEST_VALUE_2_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_2_STR_FORMATTED));
            
            // not unique input
            try {
                cellProcessor.execute(TEST_VALUE_1_OBJ, ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Unique.class)));
            }
        }
        
        @Test
        public void testBuildOutput_unique_format_ignoreValidation() {
            Annotation[] annos = getAnnotations(TestCsv.class, "byte_unique_format");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(Byte.class, annos, true);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, not(hasCellProcessor(Unique.class)));
            
            assertThat(cellProcessor.execute(TEST_VALUE_1_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_1_STR_FORMATTED));
            assertThat(cellProcessor.execute(TEST_VALUE_2_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_2_STR_FORMATTED));
            assertThat(cellProcessor.execute(TEST_VALUE_1_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_1_STR_FORMATTED));
            
        }
        
        @Test
        public void testBuildInput_combine1() {
            Annotation[] annos = getAnnotations(TestCsv.class, "byte_combine1");
            CellProcessor cellProcessor = builder.buildInputCellProcessor(Byte.class, annos);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_INPUT_DEFAULT_OBJ));
            assertThat(cellProcessor.execute("  " + TEST_VALUE_1_STR_NORMAL + "  ", ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_1_OBJ));
            
            // not equals input
            try {
                cellProcessor.execute(TEST_VALUE_2_STR_NORMAL, ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Equals.class)));
            }
            
            // not unique input
            try {
                cellProcessor.execute(TEST_VALUE_1_STR_NORMAL, ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Unique.class)));
            }
        }
        
        @Test
        public void testBuildOutput_combine1() {
            Annotation[] annos = getAnnotations(TestCsv.class, "byte_combine1");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(Byte.class, annos, false);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_OUTPUT_DEFAULT_STR_NORMAL));
            assertThat(cellProcessor.execute(TEST_VALUE_1_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_1_STR_NORMAL));
            
            // not equals input
            try {
                cellProcessor.execute(TEST_VALUE_2_OBJ, ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Equals.class)));
            }
            
            // not unique input
            try {
                cellProcessor.execute(TEST_VALUE_1_OBJ, ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Unique.class)));
            }
        }
        
        @Test
        public void testBuildOutput_combine1_ignoreValidation() {
            Annotation[] annos = getAnnotations(TestCsv.class, "byte_combine1");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(Byte.class, annos, true);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_OUTPUT_DEFAULT_STR_NORMAL));
            assertThat(cellProcessor.execute(TEST_VALUE_1_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_1_STR_NORMAL));
            
            // not equals input
            assertThat(cellProcessor.execute(TEST_VALUE_2_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_2_STR_NORMAL));
            
            // not unique input
            assertThat(cellProcessor.execute(TEST_VALUE_1_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_1_STR_NORMAL));
            
        }
        
        @Test
        public void testBuildInput_combine_format1() {
            Annotation[] annos = getAnnotations(TestCsv.class, "byte_combine_format1");
            CellProcessor cellProcessor = builder.buildInputCellProcessor(Byte.class, annos);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_INPUT_DEFAULT_OBJ));
            assertThat(cellProcessor.execute("  " + TEST_VALUE_1_STR_FORMATTED + "  ", ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_1_OBJ));
            
            // not equals input
            try {
                cellProcessor.execute(TEST_VALUE_2_STR_FORMATTED, ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Equals.class)));
            }
            
            // not unique input
            try {
                cellProcessor.execute(TEST_VALUE_1_STR_FORMATTED, ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Unique.class)));
            }
        }
        
        @Test
        public void testBuildOutput_combine_format1() {
            Annotation[] annos = getAnnotations(TestCsv.class, "byte_combine_format1");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(Byte.class, annos, false);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_OUTPUT_DEFAULT_STR_FORMATTED));
            assertThat(cellProcessor.execute(TEST_VALUE_1_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_1_STR_FORMATTED));
            
            // not equals input
            try {
                cellProcessor.execute(TEST_VALUE_2_OBJ, ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Equals.class)));
            }
            
            // not unique input
            try {
                cellProcessor.execute(TEST_VALUE_1_OBJ, ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Unique.class)));
            }
        }
        
        @Test
        public void testBuildOutput_combine_format1_ignoreValidation() {
            Annotation[] annos = getAnnotations(TestCsv.class, "byte_combine_format1");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(Byte.class, annos, true);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_OUTPUT_DEFAULT_STR_FORMATTED));
            assertThat(cellProcessor.execute(TEST_VALUE_1_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_1_STR_FORMATTED));
            
            // not equals input
            assertThat(cellProcessor.execute(TEST_VALUE_2_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_2_STR_FORMATTED));
            
            // not unique input
            assertThat(cellProcessor.execute(TEST_VALUE_1_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_1_STR_FORMATTED));
            
        }
        
        @Test
        public void testBuildInput_min() {
            Annotation[] annos = getAnnotations(TestCsv.class, "byte_min");
            CellProcessor cellProcessor = builder.buildInputCellProcessor(Byte.class, annos);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, hasCellProcessor(Min.class));
            
            assertThat(cellProcessor.execute(TEST_VALUE_MIN_STR_NORMAL, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_MIN_OBJ));
            
            // greater than min value
            {
                Byte obj = (byte) (TEST_VALUE_MIN_OBJ + TEST_VALUE_DIFF);
                String str = obj.toString();
                
                assertThat(cellProcessor.execute(str, ANONYMOUS_CSVCONTEXT), is(obj));
            
            }
            
            // less than min value
            try {
                Byte obj = (byte) (TEST_VALUE_MIN_OBJ - TEST_VALUE_DIFF);
                String str = obj.toString();
                
                cellProcessor.execute(str, ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Min.class)));
            }
            
        }
        
        @Test
        public void testBuildOutput_min() {
            Annotation[] annos = getAnnotations(TestCsv.class, "byte_min");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(Byte.class, annos, false);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, hasCellProcessor(Min.class));
            
            assertThat(cellProcessor.execute(TEST_VALUE_MIN_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_MIN_OBJ));
            
            // greater than min value
            {
                Byte obj = (byte) (TEST_VALUE_MIN_OBJ + TEST_VALUE_DIFF);
                String str = obj.toString();
                assertThat(cellProcessor.execute(obj, ANONYMOUS_CSVCONTEXT), is(obj));
            }
            
            // less than min value
            try {
                Byte obj = (byte) (TEST_VALUE_MIN_OBJ - TEST_VALUE_DIFF);
                String str = obj.toString();
                
                cellProcessor.execute(obj, ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Min.class)));
            }
            
        }
        
        @Test
        public void testBuildOutput_min_ignoreValidation() {
            Annotation[] annos = getAnnotations(TestCsv.class, "byte_min");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(Byte.class, annos, true);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, not(hasCellProcessor(Min.class)));
            
            // less than min value
            {
                Byte obj = (byte) (TEST_VALUE_MIN_OBJ - TEST_VALUE_DIFF);
                String str = obj.toString();
                
                assertThat(cellProcessor.execute(obj, ANONYMOUS_CSVCONTEXT), is(obj));
            }
            
        }
        
        @Test
        public void testBuildInput_min_format() {
            Annotation[] annos = getAnnotations(TestCsv.class, "byte_min_format");
            CellProcessor cellProcessor = builder.buildInputCellProcessor(Byte.class, annos);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, hasCellProcessor(Min.class));
            
            assertThat(cellProcessor.execute(TEST_VALUE_MIN_STR_FORMATTED, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_MIN_OBJ));
            
            // greater than min value
            {
                Byte obj = (byte) (TEST_VALUE_MIN_OBJ + TEST_VALUE_DIFF);
                String str = format(obj, TEST_FORMATTED_PATTERN);
                
                assertThat(cellProcessor.execute(str, ANONYMOUS_CSVCONTEXT), is(obj));
            }
            
            // less than min value
            try {
                Byte obj = (byte) (TEST_VALUE_MIN_OBJ - TEST_VALUE_DIFF);
                String str = format(obj, TEST_FORMATTED_PATTERN);
                
                cellProcessor.execute(str, ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Min.class)));
            }
            
        }
        
        @Test
        public void testBuildOutput_min_format() {
            Annotation[] annos = getAnnotations(TestCsv.class, "byte_min_format");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(Byte.class, annos, false);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, hasCellProcessor(Min.class));
            
            assertThat(cellProcessor.execute(TEST_VALUE_MIN_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_MIN_STR_FORMATTED));
            
            // greater than min value
            {
                Byte obj = (byte) (TEST_VALUE_MIN_OBJ + TEST_VALUE_DIFF);
                String str = format(obj, TEST_FORMATTED_PATTERN);
                
                assertThat(cellProcessor.execute(obj, ANONYMOUS_CSVCONTEXT), is(str));
            }
            
            // less than min value
            try {
                Byte obj = (byte) (TEST_VALUE_MIN_OBJ - TEST_VALUE_DIFF);
                String str = format(obj, TEST_FORMATTED_PATTERN);
                
                cellProcessor.execute(obj, ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Min.class)));
            }
            
        }
        
        @Test
        public void testBuildOutput_min_format_ignoreValidation() {
            Annotation[] annos = getAnnotations(TestCsv.class, "byte_min_format");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(Byte.class, annos, true);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, not(hasCellProcessor(Min.class)));
            
            // less than min value
            {
                Byte obj = (byte) (TEST_VALUE_MIN_OBJ + TEST_VALUE_DIFF);
                String str = format(obj, TEST_FORMATTED_PATTERN);
                
                assertThat(cellProcessor.execute(obj, ANONYMOUS_CSVCONTEXT), is(str));
            }
            
        }
        
        @Test
        public void testBuildInput_max() {
            Annotation[] annos = getAnnotations(TestCsv.class, "byte_max");
            CellProcessor cellProcessor = builder.buildInputCellProcessor(Byte.class, annos);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, hasCellProcessor(Max.class));
            
            assertThat(cellProcessor.execute(TEST_VALUE_MAX_STR_NORMAL, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_MAX_OBJ));
            
            // less than max value
            {
                Byte obj = (byte) (TEST_VALUE_MAX_OBJ - TEST_VALUE_DIFF);
                String str = obj.toString();
                
                assertThat(cellProcessor.execute(str, ANONYMOUS_CSVCONTEXT), is(obj));
            }
            
            // greater than max value
            try {
                Byte obj = (byte) (TEST_VALUE_MAX_OBJ + TEST_VALUE_DIFF);
                String str = obj.toString();
                
                cellProcessor.execute(str, ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Max.class)));
            }
            
        }
        
        @Test
        public void testBuildOutput_max() {
            Annotation[] annos = getAnnotations(TestCsv.class, "byte_max");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(Byte.class, annos, false);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, hasCellProcessor(Max.class));
            
            assertThat(cellProcessor.execute(TEST_VALUE_MAX_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_MAX_OBJ));
            
            // less than max value
            {
                Byte obj = (byte) (TEST_VALUE_MAX_OBJ - TEST_VALUE_DIFF);
                String str = obj.toString();
                
                assertThat(cellProcessor.execute(obj, ANONYMOUS_CSVCONTEXT), is(obj));
            }
            
            // greater than max value
            try {
                Byte obj = (byte) (TEST_VALUE_MAX_OBJ + TEST_VALUE_DIFF);
                String str = obj.toString();
                
                cellProcessor.execute(obj, ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Max.class)));
            }
            
        }
        
        @Test
        public void testBuildOutput_max_ignoreValidation() {
            Annotation[] annos = getAnnotations(TestCsv.class, "byte_max");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(Byte.class, annos, true);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, not(hasCellProcessor(Max.class)));
            
            // greater than max value
            {
                Byte obj = (byte) (TEST_VALUE_MAX_OBJ + TEST_VALUE_DIFF);
                String str = obj.toString();
                
                assertThat(cellProcessor.execute(obj, ANONYMOUS_CSVCONTEXT), is(obj));
            }
            
        }
        
        @Test
        public void testBuildInput_max_format() {
            Annotation[] annos = getAnnotations(TestCsv.class, "byte_max_format");
            CellProcessor cellProcessor = builder.buildInputCellProcessor(Byte.class, annos);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, hasCellProcessor(Max.class));
            
            assertThat(cellProcessor.execute(TEST_VALUE_MAX_STR_FORMATTED, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_MAX_OBJ));
            
            // less than max value
            {
                Byte obj = (byte) (TEST_VALUE_MAX_OBJ - TEST_VALUE_DIFF);
                String str = format(obj, TEST_FORMATTED_PATTERN);
                
                assertThat(cellProcessor.execute(str, ANONYMOUS_CSVCONTEXT), is(obj));
            }
            
            // greater than max value
            try {
                Byte obj = (byte) (TEST_VALUE_MAX_OBJ + TEST_VALUE_DIFF);
                String str = format(obj, TEST_FORMATTED_PATTERN);
                
                cellProcessor.execute(str, ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Max.class)));
            }
            
        }
        
        @Test
        public void testBuildOutput_max_format() {
            Annotation[] annos = getAnnotations(TestCsv.class, "byte_max_format");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(Byte.class, annos, false);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, hasCellProcessor(Max.class));
            
            assertThat(cellProcessor.execute(TEST_VALUE_MAX_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_MAX_STR_FORMATTED));
            
            // less than max value
            {
                Byte obj = (byte) (TEST_VALUE_MAX_OBJ - TEST_VALUE_DIFF);
                String str = format(obj, TEST_FORMATTED_PATTERN);
                
                assertThat(cellProcessor.execute(obj, ANONYMOUS_CSVCONTEXT), is(str));
            }
            
            // greater than max value
            try {
                Byte obj = (byte) (TEST_VALUE_MAX_OBJ + TEST_VALUE_DIFF);
                String str = format(obj, TEST_FORMATTED_PATTERN);
                
                cellProcessor.execute(obj, ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Max.class)));
            }
            
        }
        
        @Test
        public void testBuildOutput_max_format_ignoreValidation() {
            Annotation[] annos = getAnnotations(TestCsv.class, "byte_max_format");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(Byte.class, annos, true);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, not(hasCellProcessor(Max.class)));
            
            // greater than max value
            {
                Byte obj = (byte) (TEST_VALUE_MAX_OBJ + TEST_VALUE_DIFF);
                String str = format(obj, TEST_FORMATTED_PATTERN);
                
                assertThat(cellProcessor.execute(obj, ANONYMOUS_CSVCONTEXT), is(str));
            
            }
            
        }
        
        @Test
        public void testBuildInput_range() {
            Annotation[] annos = getAnnotations(TestCsv.class, "byte_range");
            CellProcessor cellProcessor = builder.buildInputCellProcessor(Byte.class, annos);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, hasCellProcessor(Range.class));
            
            assertThat(cellProcessor.execute(TEST_VALUE_MIN_STR_NORMAL, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_MIN_OBJ));
            
            // greater than min value
            {
                Byte obj = (byte) (TEST_VALUE_MIN_OBJ + TEST_VALUE_DIFF);
                String str = obj.toString();
                assertThat(cellProcessor.execute(str, ANONYMOUS_CSVCONTEXT), is(obj));
            
            }
            
            // less than min value
            try {
                Byte obj = (byte) (TEST_VALUE_MIN_OBJ - TEST_VALUE_DIFF);
                String str = obj.toString();
                
                cellProcessor.execute(str, ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Range.class)));
            }
            
            
            assertThat(cellProcessor.execute(TEST_VALUE_MAX_STR_NORMAL, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_MAX_OBJ));
            
            // less than max value
            {
                Byte obj = (byte) (TEST_VALUE_MAX_OBJ - TEST_VALUE_DIFF);
                String str = obj.toString();
                
                assertThat(cellProcessor.execute(str, ANONYMOUS_CSVCONTEXT), is(obj));
            }
            
            // greater than max value
            try {
                Byte obj = (byte) (TEST_VALUE_MAX_OBJ + TEST_VALUE_DIFF);
                String str = obj.toString();
                
                cellProcessor.execute(str, ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Range.class)));
            }
            
        }
        
        @Test
        public void testBuildOutput_range() {
            Annotation[] annos = getAnnotations(TestCsv.class, "byte_range");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(Byte.class, annos, false);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, hasCellProcessor(Range.class));
            
            assertThat(cellProcessor.execute(TEST_VALUE_MIN_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_MIN_OBJ));
            
            // greater than min value
            {
                Byte obj = (byte) (TEST_VALUE_MIN_OBJ + TEST_VALUE_DIFF);
                String str = obj.toString();
                assertThat(cellProcessor.execute(obj, ANONYMOUS_CSVCONTEXT), is(obj));
            }
            
            // less than min value
            try {
                Byte obj = (byte) (TEST_VALUE_MIN_OBJ - TEST_VALUE_DIFF);
                String str = obj.toString();
                
                cellProcessor.execute(obj, ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Range.class)));
            }
            
            assertThat(cellProcessor.execute(TEST_VALUE_MAX_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_MAX_OBJ));
            
            // less than max value
            {
                Byte obj = (byte) (TEST_VALUE_MAX_OBJ - TEST_VALUE_DIFF);
                String str = obj.toString();
                
                assertThat(cellProcessor.execute(obj, ANONYMOUS_CSVCONTEXT), is(obj));
            }
            
            // greater than max value
            try {
                Byte obj = (byte) (TEST_VALUE_MAX_OBJ + TEST_VALUE_DIFF);
                String str = obj.toString();
                
                cellProcessor.execute(obj, ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Range.class)));
            }

            
        }
        
        @Test
        public void testBuildOutput_range_ignoreValidation() {
            Annotation[] annos = getAnnotations(TestCsv.class, "byte_range");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(Byte.class, annos, true);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, not(hasCellProcessor(Range.class)));
            
            // less than min value
            {
                Byte obj = (byte) (TEST_VALUE_MIN_OBJ - TEST_VALUE_DIFF);
                String str = obj.toString();
                
                assertThat(cellProcessor.execute(obj, ANONYMOUS_CSVCONTEXT), is(obj));
            }
            
            // greater than max value
            {
                Byte obj = (byte) (TEST_VALUE_MAX_OBJ + TEST_VALUE_DIFF);
                String str = obj.toString();
                
                assertThat(cellProcessor.execute(obj, ANONYMOUS_CSVCONTEXT), is(obj));
            }
        }
        
        @Test
        public void testBuildInput_range_format() {
            Annotation[] annos = getAnnotations(TestCsv.class, "byte_range_format");
            CellProcessor cellProcessor = builder.buildInputCellProcessor(Byte.class, annos);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor.execute(TEST_VALUE_MIN_STR_FORMATTED, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_MIN_OBJ));
            
            // greater than min value
            {
                Byte obj = (byte) (TEST_VALUE_MIN_OBJ + TEST_VALUE_DIFF);
                String str = format(obj, TEST_FORMATTED_PATTERN);
                
                assertThat(cellProcessor.execute(str, ANONYMOUS_CSVCONTEXT), is(obj));
            }
            
            // less than min value
            try {
                Byte obj = (byte) (TEST_VALUE_MIN_OBJ - TEST_VALUE_DIFF);
                String str = format(obj, TEST_FORMATTED_PATTERN);
                
                cellProcessor.execute(str, ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Range.class)));
            }
            
            assertThat(cellProcessor.execute(TEST_VALUE_MAX_STR_FORMATTED, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_MAX_OBJ));
            
            // less than max value
            {
                Byte obj = (byte) (TEST_VALUE_MAX_OBJ - TEST_VALUE_DIFF);
                String str = format(obj, TEST_FORMATTED_PATTERN);
                
                assertThat(cellProcessor.execute(str, ANONYMOUS_CSVCONTEXT), is(obj));
            }
            
            // greater than max value
            try {
                Byte obj = (byte) (TEST_VALUE_MAX_OBJ + TEST_VALUE_DIFF);
                String str = format(obj, TEST_FORMATTED_PATTERN);
                
                cellProcessor.execute(str, ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Range.class)));
            }
            
        }
        
        @Test
        public void testBuildOutput_range_format() {
            Annotation[] annos = getAnnotations(TestCsv.class, "byte_range_format");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(Byte.class, annos, false);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, hasCellProcessor(Range.class));
            
            assertThat(cellProcessor.execute(TEST_VALUE_MIN_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_MIN_STR_FORMATTED));
            
            // greater than min value
            {
                Byte obj = (byte) (TEST_VALUE_MIN_OBJ + TEST_VALUE_DIFF);
                String str = format(obj, TEST_FORMATTED_PATTERN);
                
                assertThat(cellProcessor.execute(obj, ANONYMOUS_CSVCONTEXT), is(str));
            }
            
            // less than min value
            try {
                Byte obj = (byte) (TEST_VALUE_MIN_OBJ - TEST_VALUE_DIFF);
                String str = format(obj, TEST_FORMATTED_PATTERN);
                
                cellProcessor.execute(obj, ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Range.class)));
            }
            
            assertThat(cellProcessor.execute(TEST_VALUE_MAX_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_MAX_STR_FORMATTED));
            
            // less than max value
            {
                Byte obj = (byte) (TEST_VALUE_MAX_OBJ - TEST_VALUE_DIFF);
                String str = format(obj, TEST_FORMATTED_PATTERN);
                
                assertThat(cellProcessor.execute(obj, ANONYMOUS_CSVCONTEXT), is(str));
            }
            
            // greater than max value
            try {
                Byte obj = (byte) (TEST_VALUE_MAX_OBJ + TEST_VALUE_DIFF);
                String str = format(obj, TEST_FORMATTED_PATTERN);
                
                cellProcessor.execute(obj, ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Range.class)));
            }
            
        }
        
        @Test
        public void testBuildOutput_range_format_ignoreValidation() {
            Annotation[] annos = getAnnotations(TestCsv.class, "byte_range_format");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(Byte.class, annos, true);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, not(hasCellProcessor(Range.class)));
            
            // less than min value
            {
                Byte obj = (byte) (TEST_VALUE_MIN_OBJ + TEST_VALUE_DIFF);
                String str = format(obj, TEST_FORMATTED_PATTERN);
                
                assertThat(cellProcessor.execute(obj, ANONYMOUS_CSVCONTEXT), is(str));
            }
            
            // greater than max value
            {
                Byte obj = (byte) (TEST_VALUE_MAX_OBJ + TEST_VALUE_DIFF);
                String str = format(obj, TEST_FORMATTED_PATTERN);
                
                assertThat(cellProcessor.execute(obj, ANONYMOUS_CSVCONTEXT), is(str));
            
            }
            
        }
        
        @Test
        public void testBuildInput_format_lenient() {
            Annotation[] annos = getAnnotations(TestCsv.class, "byte_format_lenient");
            CellProcessor cellProcessor = builder.buildInputCellProcessor(Byte.class, annos);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, hasCellProcessor(ParseLocaleNumber.class));
            
            assertThat(cellProcessor.execute("123.456", ANONYMOUS_CSVCONTEXT), is(toByte("123")));
            assertThat(cellProcessor.execute("12", ANONYMOUS_CSVCONTEXT), is(toByte("12")));
            
        }
        
        @Test
        public void testBuildOutput_format_lenient() {
            Annotation[] annos = getAnnotations(TestCsv.class, "byte_format_lenient");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(Byte.class, annos, true);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, hasCellProcessor(FormatLocaleNumber.class));
            
            assertThat(cellProcessor.execute(toByte("123"), ANONYMOUS_CSVCONTEXT), is("123.0"));
            
        }
        
        @Test
        public void testBuildInput_format_currency() {
            Annotation[] annos = getAnnotations(TestCsv.class, "byte_format_currency");
            CellProcessor cellProcessor = builder.buildInputCellProcessor(Byte.class, annos);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, hasCellProcessor(ParseLocaleNumber.class));
            
            assertThat(cellProcessor.execute("USD 123.0000", ANONYMOUS_CSVCONTEXT), is(toByte("123")));
            
        }
        
        @Test
        public void testBuildOutput_format_currency() {
            Annotation[] annos = getAnnotations(TestCsv.class, "byte_format_currency");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(Byte.class, annos, true);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, hasCellProcessor(FormatLocaleNumber.class));
            
            assertThat(cellProcessor.execute(toByte("123"), ANONYMOUS_CSVCONTEXT), is("USD 123.0000"));
            
        }
        
        @Test
        public void testBuildInput_format_roundingMode() {
            Annotation[] annos = getAnnotations(TestCsv.class, "byte_format_roundingMode");
            CellProcessor cellProcessor = builder.buildInputCellProcessor(Byte.class, annos);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, hasCellProcessor(ParseLocaleNumber.class));
            
            assertThat(cellProcessor.execute("123", ANONYMOUS_CSVCONTEXT), is(toByte("123")));
            
        }
        
        @Test
        public void testBuildOutput_format_roundingMode() {
            Annotation[] annos = getAnnotations(TestCsv.class, "byte_format_roundingMode");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(Byte.class, annos, true);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, hasCellProcessor(FormatLocaleNumber.class));
            
            assertThat(cellProcessor.execute(toByte("123"), ANONYMOUS_CSVCONTEXT), is("123"));
            
        }
    
    }
    
    /**
     * Tests for primitive byte.
     *
     */
    public static class PrivimiveByteTest {
        
        @Rule
        public TestName name = new TestName();
        
        private ByteCellProcessorBuilder builder;
        
        /**
         * Sets up the processor for the test using Combinations
         */
        @Before
        public void setUp() {
            builder = new ByteCellProcessorBuilder();
        }
        
        /** for min/max diff value */
        private static final byte TEST_VALUE_DIFF = 1;
        
        private static final String TEST_FORMATTED_PATTERN = "#,###";
        
        private static final byte TEST_VALUE_PRIMITIVE_INIT_OBJ = 0;
        
        private static final byte TEST_VALUE_1_OBJ = 12;
        private static final String TEST_VALUE_1_STR_NORMAL = "12";
        private static final String TEST_VALUE_1_STR_FORMATTED = "12";
        
        private static final byte TEST_VALUE_2_OBJ = -23;
        private static final String TEST_VALUE_2_STR_NORMAL = "-23";
        private static final String TEST_VALUE_2_STR_FORMATTED = "-23";
        
        private static final byte TEST_VALUE_INPUT_DEFAULT_OBJ = 11;
        private static final String TEST_VALUE_INPUT_DEFAULT_STR_NORMAL = "11";
        private static final String TEST_VALUE_INPUT_DEFAULT_STR_FORMATTED = "11";
        
        private static final byte TEST_VALUE_OUTPUT_DEFAULT_OBJ = -22;
        private static final String TEST_VALUE_OUTPUT_DEFAULT_STR_NORMAL = "-22";
        private static final String TEST_VALUE_OUTPUT_DEFAULT_STR_FORMATTED = "-22";
        
        private static final byte TEST_VALUE_MIN_OBJ = -43;
        private static final String TEST_VALUE_MIN_STR_NORMAL = "-43";
        private static final String TEST_VALUE_MIN_STR_FORMATTED = "-43";
        
        private static final byte TEST_VALUE_MAX_OBJ = 87;
        private static final String TEST_VALUE_MAX_STR_NORMAL = "87";
        private static final String TEST_VALUE_MAX_STR_FORMATTED = "87";
        
        @CsvBean
        private static class TestCsv {
            
            @CsvColumn(position=0)
            byte byte_default;
            
            @CsvColumn(position=1, optional=true)
            byte byte_optional;
            
            @CsvColumn(position=2, trim=true)
            byte byte_trim;
            
            @CsvColumn(position=3, inputDefaultValue=TEST_VALUE_INPUT_DEFAULT_STR_NORMAL, outputDefaultValue=TEST_VALUE_OUTPUT_DEFAULT_STR_NORMAL)
            byte byte_defaultValue;
            
            @CsvColumn(position=4, inputDefaultValue=TEST_VALUE_INPUT_DEFAULT_STR_FORMATTED, outputDefaultValue=TEST_VALUE_OUTPUT_DEFAULT_STR_FORMATTED)
            @CsvNumberConverter(pattern=TEST_FORMATTED_PATTERN)
            byte byte_defaultValue_format;
            
            @CsvColumn(position=4, inputDefaultValue="abc12,345")
            @CsvNumberConverter(pattern=TEST_FORMATTED_PATTERN)
            byte byte_defaultValue_format_invalid;
            
            @CsvColumn(position=5, equalsValue=TEST_VALUE_1_STR_NORMAL)
            byte byte_equalsValue;
            
            @CsvColumn(position=6, equalsValue=TEST_VALUE_1_STR_FORMATTED)
            @CsvNumberConverter(pattern=TEST_FORMATTED_PATTERN)
            byte byte_equalsValue_format;
            
            @CsvColumn(position=7, unique=true)
            byte byte_unique;
            
            @CsvColumn(position=8, unique=true)
            @CsvNumberConverter(pattern=TEST_FORMATTED_PATTERN)
            byte byte_unique_format;
            
            @CsvColumn(position=9, optional=true, trim=true, inputDefaultValue=TEST_VALUE_INPUT_DEFAULT_STR_NORMAL, outputDefaultValue=TEST_VALUE_OUTPUT_DEFAULT_STR_NORMAL,
                    equalsValue=TEST_VALUE_1_STR_NORMAL, unique=true)
            byte byte_combine1;
            
            @CsvColumn(position=10, optional=true, trim=true, inputDefaultValue=TEST_VALUE_INPUT_DEFAULT_STR_FORMATTED, outputDefaultValue=TEST_VALUE_OUTPUT_DEFAULT_STR_FORMATTED,
                    equalsValue=TEST_VALUE_1_STR_FORMATTED, unique=true)
            @CsvNumberConverter(pattern=TEST_FORMATTED_PATTERN)
            byte byte_combine_format1;
            
            @CsvColumn(position=11)
            @CsvNumberConverter(min=TEST_VALUE_MIN_STR_NORMAL)
            byte byte_min;
            
            @CsvColumn(position=12)
            @CsvNumberConverter(min=TEST_VALUE_MIN_STR_FORMATTED, pattern=TEST_FORMATTED_PATTERN)
            byte byte_min_format;
            
            @CsvColumn(position=13)
            @CsvNumberConverter(max=TEST_VALUE_MAX_STR_NORMAL)
            byte byte_max;
            
            @CsvColumn(position=14)
            @CsvNumberConverter(max=TEST_VALUE_MAX_STR_FORMATTED, pattern=TEST_FORMATTED_PATTERN)
            byte byte_max_format;
            
            @CsvColumn(position=15)
            @CsvNumberConverter(min=TEST_VALUE_MIN_STR_NORMAL, max=TEST_VALUE_MAX_STR_NORMAL)
            byte byte_range;
            
            @CsvColumn(position=16)
            @CsvNumberConverter(min=TEST_VALUE_MIN_STR_FORMATTED, max=TEST_VALUE_MAX_STR_FORMATTED, pattern=TEST_FORMATTED_PATTERN)
            byte byte_range_format;
            
            @CsvColumn(position=17)
            @CsvNumberConverter(pattern="#,##0.0##", lenient=true)
            byte byte_format_lenient;
            
            @CsvColumn(position=18)
            @CsvNumberConverter(pattern="\u00A4 #,##0.0000", currency="USD")
            byte byte_format_currency;
            
            @CsvColumn(position=19)
            @CsvNumberConverter(pattern="#,##0", rounding=RoundingMode.HALF_UP)
            byte byte_format_roundingMode;
            
        }
        
        /**
         * Tests with default. (not grant convert annotation.)
         */
        @Test
        public void testBuildInput_default() {
            
            Annotation[] annos = getAnnotations(TestCsv.class, "byte_default");
            CellProcessor cellProcessor = builder.buildInputCellProcessor(byte.class, annos);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, hasCellProcessor(NotNull.class));
            assertThat(cellProcessor, hasCellProcessor(ParseByte.class));
            
            assertThat(cellProcessor.execute(TEST_VALUE_1_STR_NORMAL, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_1_OBJ));
            
            // null input
            try {
                cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(NotNull.class)));
            }
            
            // wrong pattern
            try {
                cellProcessor.execute("abc", ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvCellProcessorException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(ParseByte.class)));
            }
            
        }
        
        /**
         * Tests with default. (not grant convert annotation.)
         */
        @Test
        public void testBuildOutput_default() {
            
            Annotation[] annos = getAnnotations(TestCsv.class, "byte_default");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(byte.class, annos, false);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, hasCellProcessor(NotNull.class));
            
            assertThat(cellProcessor.execute(TEST_VALUE_1_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_1_OBJ));
            
            // null input
            try {
                cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(NotNull.class)));
            }
            
        }
        
        /**
         * Tests with default. (not grant convert annotation.)
         */
        @Test
        public void testBuildOutput_default_ignoreValidation() {
            
            Annotation[] annos = getAnnotations(TestCsv.class, "byte_default");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(byte.class, annos, true);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, hasCellProcessor(NotNull.class));
            
            assertThat(cellProcessor.execute(TEST_VALUE_1_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_1_OBJ));
            
            // null input
            try {
                cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(NotNull.class)));
            }
        }
        
        /**
         * Tests with optional. (not grant convert annotation.)
         */
        @Test
        public void testBuildInput_optional() {
            
            Annotation[] annos = getAnnotations(TestCsv.class, "byte_optional");
            CellProcessor cellProcessor = builder.buildInputCellProcessor(byte.class, annos);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, hasCellProcessor(ConvertNullTo.class));
            
            assertThat(cellProcessor.execute(TEST_VALUE_1_STR_NORMAL, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_1_OBJ));
            
            // null input (if primitive, return 0)
            assertThat(cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_PRIMITIVE_INIT_OBJ));
            
        }
        
        /**
         * Tests with optional. (not grant convert annotation.)
         */
        @Test
        public void testBuildOutput_optional() {
            
            Annotation[] annos = getAnnotations(TestCsv.class, "byte_optional");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(byte.class, annos, false);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, hasCellProcessor(Optional.class));
            
            assertThat(cellProcessor.execute(TEST_VALUE_1_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_1_OBJ));
            
            assertThat(cellProcessor.execute((byte)0, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_PRIMITIVE_INIT_OBJ));
            
        }
        
        /**
         * Tests with optional. (not grant convert annotation.)
         */
        @Test
        public void testBuildOutput_optional_ignoreValidation() {
            
            Annotation[] annos = getAnnotations(TestCsv.class, "byte_optional");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(byte.class, annos, true);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, hasCellProcessor(Optional.class));
            
            assertThat(cellProcessor.execute(TEST_VALUE_1_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_1_OBJ));
                        
            assertThat(cellProcessor.execute(TEST_VALUE_PRIMITIVE_INIT_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_PRIMITIVE_INIT_OBJ));
        }
        
        @Test
        public void testBuildInput_trim() {
            Annotation[] annos = getAnnotations(TestCsv.class, "byte_trim");
            CellProcessor cellProcessor = builder.buildInputCellProcessor(byte.class, annos);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, hasCellProcessor(Trim.class));
            
            assertThat(cellProcessor.execute("  " + TEST_VALUE_1_STR_NORMAL + " ", ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_1_OBJ));
            
        }
        
        @Test
        public void testBuildOutput_trim() {
            Annotation[] annos = getAnnotations(TestCsv.class, "byte_trim");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(byte.class, annos, false);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, hasCellProcessor(Trim.class));
            
            assertThat(cellProcessor.execute(TEST_VALUE_1_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_1_STR_NORMAL));
            
        }
        
        @Test
        public void testBuildOutput_trim_ignoreValidation() {
            Annotation[] annos = getAnnotations(TestCsv.class, "byte_trim");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(byte.class, annos, true);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, hasCellProcessor(Trim.class));
            
            assertThat(cellProcessor.execute(TEST_VALUE_1_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_1_STR_NORMAL));
        }
        
        @Test
        public void testBuildInput_defaultValue() {
            Annotation[] annos = getAnnotations(TestCsv.class, "byte_defaultValue");
            CellProcessor cellProcessor = builder.buildInputCellProcessor(byte.class, annos);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, hasCellProcessor(ConvertNullTo.class));
            
            assertThat(cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_INPUT_DEFAULT_OBJ));
        }
        
        @Test
        public void testBuildOutput_defaultValue() {
            Annotation[] annos = getAnnotations(TestCsv.class, "byte_defaultValue");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(byte.class, annos, false);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, hasCellProcessor(ConvertNullTo.class));
            
            assertThat(cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_OUTPUT_DEFAULT_STR_NORMAL));
        }
        
        @Test
        public void testBuildOutput_defaultValue_ignoreValidation() {
            Annotation[] annos = getAnnotations(TestCsv.class, "byte_defaultValue");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(byte.class, annos, true);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, hasCellProcessor(ConvertNullTo.class));
            
            assertThat(cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_OUTPUT_DEFAULT_STR_NORMAL));
        }
        
        @Test
        public void testBuildInput_defaultValue_format() {
            Annotation[] annos = getAnnotations(TestCsv.class, "byte_defaultValue_format");
            CellProcessor cellProcessor = builder.buildInputCellProcessor(byte.class, annos);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, hasCellProcessor(ConvertNullTo.class));
            
            assertThat(cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_INPUT_DEFAULT_OBJ));
        }
        
        @Test
        public void testBuildOutput_defaultValue_format() {
            Annotation[] annos = getAnnotations(TestCsv.class, "byte_defaultValue_format");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(byte.class, annos, false);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            assertThat(cellProcessor, hasCellProcessor(ConvertNullTo.class));
            
            assertThat(cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_OUTPUT_DEFAULT_STR_FORMATTED));
        }
        
        @Test
        public void testBuildOutput_defaultValue_format_ignoreValidation() {
            Annotation[] annos = getAnnotations(TestCsv.class, "byte_defaultValue_format");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(byte.class, annos, true);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, hasCellProcessor(ConvertNullTo.class));
            
            assertThat(cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_OUTPUT_DEFAULT_STR_FORMATTED));
        }
        
        /**
         * Tests with default. (not grant convert annotation.)
         */
        @Test(expected=SuperCsvInvalidAnnotationException.class)
        public void testBuildInput_default_format_invalidAnnotation() {
            
            Annotation[] annos = getAnnotations(TestCsv.class, "byte_defaultValue_format_invalid");
            CellProcessor cellProcessor = builder.buildInputCellProcessor(byte.class, annos);
            
            cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT);
            fail();
            
        }
        
        @Test
        public void testBuildInput_equalsValue() {
            Annotation[] annos = getAnnotations(TestCsv.class, "byte_equalsValue");
            CellProcessor cellProcessor = builder.buildInputCellProcessor(byte.class, annos);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, hasCellProcessor(Equals.class));
            
            assertThat(cellProcessor.execute(TEST_VALUE_1_STR_NORMAL, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_1_OBJ));
            
            // not quals input
            try {
                cellProcessor.execute(TEST_VALUE_2_STR_NORMAL, ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Equals.class)));
            }
        }
        
        @Test
        public void testBuildOutput_equalsValue() {
            Annotation[] annos = getAnnotations(TestCsv.class, "byte_equalsValue");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(byte.class, annos, false);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, hasCellProcessor(Equals.class));
            
            assertThat(cellProcessor.execute(TEST_VALUE_1_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_1_OBJ));
            
            // not quals input
            try {
                cellProcessor.execute(TEST_VALUE_2_OBJ, ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Equals.class)));
            }
        }
        
        @Test
        public void testBuildOutput_equalsValue_ignoreValidation() {
            Annotation[] annos = getAnnotations(TestCsv.class, "byte_equalsValue");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(byte.class, annos, true);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, not(hasCellProcessor(Equals.class)));
            
            assertThat(cellProcessor.execute(TEST_VALUE_1_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_1_OBJ));
            
            // not quals input
            assertThat(cellProcessor.execute(TEST_VALUE_2_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_2_OBJ));
            
            
        }
        
        @Test
        public void testBuildInput_equalsValue_format() {
            Annotation[] annos = getAnnotations(TestCsv.class, "byte_equalsValue_format");
            CellProcessor cellProcessor = builder.buildInputCellProcessor(byte.class, annos);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, hasCellProcessor(Equals.class));
            
            assertThat(cellProcessor.execute(TEST_VALUE_1_STR_FORMATTED, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_1_OBJ));
            
            // not quals input
            try {
                cellProcessor.execute(TEST_VALUE_2_STR_FORMATTED, ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Equals.class)));
            }
        }
        
        @Test
        public void testBuildOutput_equalsValue_format() {
            Annotation[] annos = getAnnotations(TestCsv.class, "byte_equalsValue_format");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(byte.class, annos, false);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, hasCellProcessor(Equals.class));
            
            assertThat(cellProcessor.execute(TEST_VALUE_1_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_1_STR_FORMATTED));
            
            // not quals input
            try {
                cellProcessor.execute(TEST_VALUE_2_OBJ, ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Equals.class)));
            }

        }
        
        @Test
        public void testBuildOutput_equalsValue_format_ignoreValidation() {
            Annotation[] annos = getAnnotations(TestCsv.class, "byte_equalsValue_format");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(byte.class, annos, true);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, not(hasCellProcessor(Equals.class)));
            
            assertThat(cellProcessor.execute(TEST_VALUE_1_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_1_STR_FORMATTED));
            
            // not quals input
            assertThat(cellProcessor.execute(TEST_VALUE_2_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_2_STR_FORMATTED));
            
            
        }
        
        @Test
        public void testBuildInput_unique() {
            Annotation[] annos = getAnnotations(TestCsv.class, "byte_unique");
            CellProcessor cellProcessor = builder.buildInputCellProcessor(byte.class, annos);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, hasCellProcessor(Unique.class));
            
            assertThat(cellProcessor.execute(TEST_VALUE_1_STR_NORMAL, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_1_OBJ));
            assertThat(cellProcessor.execute(TEST_VALUE_2_STR_NORMAL, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_2_OBJ));
            
            // not unique input
            try {
                cellProcessor.execute(TEST_VALUE_1_STR_NORMAL, ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Unique.class)));
            }
        }
        
        @Test
        public void testBuildOutput_unique() {
            Annotation[] annos = getAnnotations(TestCsv.class, "byte_unique");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(byte.class, annos, false);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, hasCellProcessor(Unique.class));
            
            assertThat(cellProcessor.execute(TEST_VALUE_1_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_1_OBJ));
            assertThat(cellProcessor.execute(TEST_VALUE_2_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_2_OBJ));
            
            // not unique input
            try {
                cellProcessor.execute(TEST_VALUE_1_OBJ, ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Unique.class)));
            }
        }
        
        @Test
        public void testBuildOutput_unique_ignoreValidation() {
            Annotation[] annos = getAnnotations(TestCsv.class, "byte_unique");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(byte.class, annos, true);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, not(hasCellProcessor(Unique.class)));
            
            assertThat(cellProcessor.execute(TEST_VALUE_1_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_1_OBJ));
            assertThat(cellProcessor.execute(TEST_VALUE_2_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_2_OBJ));
            assertThat(cellProcessor.execute(TEST_VALUE_1_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_1_OBJ));
                        
        }
        
        @Test
        public void testBuildInput_unique_format() {
            Annotation[] annos = getAnnotations(TestCsv.class, "byte_unique_format");
            CellProcessor cellProcessor = builder.buildInputCellProcessor(byte.class, annos);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, hasCellProcessor(Unique.class));
            
            assertThat(cellProcessor.execute(TEST_VALUE_1_STR_FORMATTED, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_1_OBJ));
            assertThat(cellProcessor.execute(TEST_VALUE_2_STR_FORMATTED, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_2_OBJ));
            
            // not unique input
            try {
                cellProcessor.execute(TEST_VALUE_1_STR_FORMATTED, ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Unique.class)));
            }
        }
        
        @Test
        public void testBuildOutput_unique_format() {
            Annotation[] annos = getAnnotations(TestCsv.class, "byte_unique_format");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(byte.class, annos, false);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, hasCellProcessor(Unique.class));
            
            assertThat(cellProcessor.execute(TEST_VALUE_1_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_1_STR_FORMATTED));
            assertThat(cellProcessor.execute(TEST_VALUE_2_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_2_STR_FORMATTED));
            
            // not unique input
            try {
                cellProcessor.execute(TEST_VALUE_1_OBJ, ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Unique.class)));
            }
        }
        
        @Test
        public void testBuildOutput_unique_format_ignoreValidation() {
            Annotation[] annos = getAnnotations(TestCsv.class, "byte_unique_format");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(byte.class, annos, true);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, not(hasCellProcessor(Unique.class)));
            
            assertThat(cellProcessor.execute(TEST_VALUE_1_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_1_STR_FORMATTED));
            assertThat(cellProcessor.execute(TEST_VALUE_2_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_2_STR_FORMATTED));
            assertThat(cellProcessor.execute(TEST_VALUE_1_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_1_STR_FORMATTED));
            
        }
        
        @Test
        public void testBuildInput_combine1() {
            Annotation[] annos = getAnnotations(TestCsv.class, "byte_combine1");
            CellProcessor cellProcessor = builder.buildInputCellProcessor(byte.class, annos);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_INPUT_DEFAULT_OBJ));
            assertThat(cellProcessor.execute("  " + TEST_VALUE_1_STR_NORMAL + "  ", ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_1_OBJ));
            
            // not equals input
            try {
                cellProcessor.execute(TEST_VALUE_2_STR_NORMAL, ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Equals.class)));
            }
            
            // not unique input
            try {
                cellProcessor.execute(TEST_VALUE_1_STR_NORMAL, ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Unique.class)));
            }
            
        }
        
        @Test
        public void testBuildOutput_combine1() {
            Annotation[] annos = getAnnotations(TestCsv.class, "byte_combine1");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(byte.class, annos, false);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_OUTPUT_DEFAULT_STR_NORMAL));
            assertThat(cellProcessor.execute(TEST_VALUE_1_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_1_STR_NORMAL));
            
            // not equals input
            try {
                cellProcessor.execute(TEST_VALUE_2_OBJ, ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Equals.class)));
            }
            
            // not unique input
            try {
                cellProcessor.execute(TEST_VALUE_1_OBJ, ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Unique.class)));
            }
        }
        
        @Test
        public void testBuildOutput_combine1_ignoreValidation() {
            Annotation[] annos = getAnnotations(TestCsv.class, "byte_combine1");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(byte.class, annos, true);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_OUTPUT_DEFAULT_STR_NORMAL));
            assertThat(cellProcessor.execute(TEST_VALUE_1_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_1_STR_NORMAL));
            
            // not equals input
            assertThat(cellProcessor.execute(TEST_VALUE_2_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_2_STR_NORMAL));
            
            // not unique input
            assertThat(cellProcessor.execute(TEST_VALUE_1_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_1_STR_NORMAL));
            
            
        }
        
        @Test
        public void testBuildInput_combine_format1() {
            Annotation[] annos = getAnnotations(TestCsv.class, "byte_combine_format1");
            CellProcessor cellProcessor = builder.buildInputCellProcessor(byte.class, annos);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_INPUT_DEFAULT_OBJ));
            assertThat(cellProcessor.execute("  " + TEST_VALUE_1_STR_FORMATTED + "  ", ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_1_OBJ));
            
            // not equals input
            try {
                cellProcessor.execute(TEST_VALUE_2_STR_FORMATTED, ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Equals.class)));
            }
            
            // not unique input
            try {
                cellProcessor.execute(TEST_VALUE_1_STR_FORMATTED, ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Unique.class)));
            }
        }
        
        @Test
        public void testBuildOutput_combine_format1() {
            Annotation[] annos = getAnnotations(TestCsv.class, "byte_combine_format1");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(byte.class, annos, false);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_OUTPUT_DEFAULT_STR_FORMATTED));
            assertThat(cellProcessor.execute(TEST_VALUE_1_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_1_STR_FORMATTED));
            
            // not equals input
            try {
                cellProcessor.execute(TEST_VALUE_2_OBJ, ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Equals.class)));
            }
            
            // not unique input
            try {
                cellProcessor.execute(TEST_VALUE_1_OBJ, ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Unique.class)));
            }
        }
        
        @Test
        public void testBuildOutput_combine_format1_ignoreValidation() {
            Annotation[] annos = getAnnotations(TestCsv.class, "byte_combine_format1");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(byte.class, annos, true);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_OUTPUT_DEFAULT_STR_FORMATTED));
            assertThat(cellProcessor.execute(TEST_VALUE_1_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_1_STR_FORMATTED));
            
            // not equals input
            assertThat(cellProcessor.execute(TEST_VALUE_2_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_2_STR_FORMATTED));
            
            // not unique input
            assertThat(cellProcessor.execute(TEST_VALUE_1_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_1_STR_FORMATTED));
            
        }
        
        @Test
        public void testBuildInput_min() {
            Annotation[] annos = getAnnotations(TestCsv.class, "byte_min");
            CellProcessor cellProcessor = builder.buildInputCellProcessor(byte.class, annos);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, hasCellProcessor(Min.class));
            
            assertThat(cellProcessor.execute(TEST_VALUE_MIN_STR_NORMAL, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_MIN_OBJ));
            
            // greater than min value
            {
                byte obj = TEST_VALUE_MIN_OBJ + TEST_VALUE_DIFF;
                String str = Objects.toString(obj);
                assertThat(cellProcessor.execute(str, ANONYMOUS_CSVCONTEXT), is(obj));
            
            }
            
            // less than min value
            try {
                byte obj = TEST_VALUE_MIN_OBJ - TEST_VALUE_DIFF;
                String str = Objects.toString(obj);
                
                cellProcessor.execute(str, ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Min.class)));
            }            
        }
        
        @Test
        public void testBuildOutput_min() {
            Annotation[] annos = getAnnotations(TestCsv.class, "byte_min");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(byte.class, annos, false);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, hasCellProcessor(Min.class));
            
            assertThat(cellProcessor.execute(TEST_VALUE_MIN_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_MIN_OBJ));
            
            // greater than min value
            {
                byte obj = TEST_VALUE_MIN_OBJ + TEST_VALUE_DIFF;
                String str = Objects.toString(obj);
                assertThat(cellProcessor.execute(obj, ANONYMOUS_CSVCONTEXT), is(obj));
            }
            
            // less than min value
            try {
                byte obj = TEST_VALUE_MIN_OBJ - TEST_VALUE_DIFF;
                String str = Objects.toString(obj);
                
                cellProcessor.execute(obj, ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Min.class)));
            }
            
        }
        
        @Test
        public void testBuildOutput_min_ignoreValidation() {
            Annotation[] annos = getAnnotations(TestCsv.class, "byte_min");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(byte.class, annos, true);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, not(hasCellProcessor(Min.class)));
            
            // less than min value
            {
                byte obj = TEST_VALUE_MIN_OBJ - TEST_VALUE_DIFF;
                String str = Objects.toString(obj);
                
                assertThat(cellProcessor.execute(obj, ANONYMOUS_CSVCONTEXT), is(obj));
            }            
        }
        
        @Test
        public void testBuildInput_min_format() {
            Annotation[] annos = getAnnotations(TestCsv.class, "byte_min_format");
            CellProcessor cellProcessor = builder.buildInputCellProcessor(byte.class, annos);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, hasCellProcessor(Min.class));
            
            assertThat(cellProcessor.execute(TEST_VALUE_MIN_STR_FORMATTED, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_MIN_OBJ));
            
            // greater than min value
            {
                byte obj = TEST_VALUE_MIN_OBJ + TEST_VALUE_DIFF;
                String str = format(obj, TEST_FORMATTED_PATTERN);
                
                assertThat(cellProcessor.execute(str, ANONYMOUS_CSVCONTEXT), is(obj));
            }
            
            // less than min value
            try {
                byte obj = TEST_VALUE_MIN_OBJ - TEST_VALUE_DIFF;
                String str = format(obj, TEST_FORMATTED_PATTERN);
                
                cellProcessor.execute(str, ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Min.class)));
            }            
        }
        
        @Test
        public void testBuildOutput_min_format() {
            Annotation[] annos = getAnnotations(TestCsv.class, "byte_min_format");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(byte.class, annos, false);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, hasCellProcessor(Min.class));
            
            assertThat(cellProcessor.execute(TEST_VALUE_MIN_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_MIN_STR_FORMATTED));
            
            // greater than min value
            {
                byte obj = TEST_VALUE_MIN_OBJ + TEST_VALUE_DIFF;
                String str = format(obj, TEST_FORMATTED_PATTERN);
                
                assertThat(cellProcessor.execute(obj, ANONYMOUS_CSVCONTEXT), is(str));
            }
            
            // less than min value
            try {
                byte obj = TEST_VALUE_MIN_OBJ - TEST_VALUE_DIFF;
                String str = format(obj, TEST_FORMATTED_PATTERN);
                
                cellProcessor.execute(obj, ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Min.class)));
            }            
        }
        
        @Test
        public void testBuildOutput_min_format_ignoreValidation() {
            Annotation[] annos = getAnnotations(TestCsv.class, "byte_min_format");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(byte.class, annos, true);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, not(hasCellProcessor(Min.class)));
            
            // less than min value
            {
                byte obj = TEST_VALUE_MIN_OBJ + TEST_VALUE_DIFF;
                String str = format(obj, TEST_FORMATTED_PATTERN);
                
                assertThat(cellProcessor.execute(obj, ANONYMOUS_CSVCONTEXT), is(str));
            }            
        }
        
        @Test
        public void testBuildInput_max() {
            Annotation[] annos = getAnnotations(TestCsv.class, "byte_max");
            CellProcessor cellProcessor = builder.buildInputCellProcessor(byte.class, annos);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, hasCellProcessor(Max.class));
            
            assertThat(cellProcessor.execute(TEST_VALUE_MAX_STR_NORMAL, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_MAX_OBJ));
            
            // less than max value
            {
                byte obj = TEST_VALUE_MAX_OBJ - TEST_VALUE_DIFF;
                String str = Objects.toString(obj);
                
                assertThat(cellProcessor.execute(str, ANONYMOUS_CSVCONTEXT), is(obj));
            }
            
            // greater than max value
            try {
                byte obj = TEST_VALUE_MAX_OBJ + TEST_VALUE_DIFF;
                String str = Objects.toString(obj);
                
                cellProcessor.execute(str, ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Max.class)));
            }
        }
        
        @Test
        public void testBuildOutput_max() {
            Annotation[] annos = getAnnotations(TestCsv.class, "byte_max");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(byte.class, annos, false);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, hasCellProcessor(Max.class));
            
            assertThat(cellProcessor.execute(TEST_VALUE_MAX_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_MAX_OBJ));
            
            // less than max value
            {
                byte obj = TEST_VALUE_MAX_OBJ - TEST_VALUE_DIFF;
                String str = Objects.toString(obj);
                
                assertThat(cellProcessor.execute(obj, ANONYMOUS_CSVCONTEXT), is(obj));
            }
            
            // greater than max value
            try {
                byte obj = TEST_VALUE_MAX_OBJ + TEST_VALUE_DIFF;
                String str = Objects.toString(obj);
                
                cellProcessor.execute(obj, ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Max.class)));
            }
            
        }
        
        @Test
        public void testBuildOutput_max_ignoreValidation() {
            Annotation[] annos = getAnnotations(TestCsv.class, "byte_max");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(byte.class, annos, true);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, not(hasCellProcessor(Max.class)));
            
            // greater than max value
            {
                byte obj = TEST_VALUE_MAX_OBJ + TEST_VALUE_DIFF;
                String str = Objects.toString(obj);
                
                assertThat(cellProcessor.execute(obj, ANONYMOUS_CSVCONTEXT), is(obj));
            }
            
        }
        
        @Test
        public void testBuildInput_max_format() {
            Annotation[] annos = getAnnotations(TestCsv.class, "byte_max_format");
            CellProcessor cellProcessor = builder.buildInputCellProcessor(byte.class, annos);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, hasCellProcessor(Max.class));
            
            assertThat(cellProcessor.execute(TEST_VALUE_MAX_STR_FORMATTED, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_MAX_OBJ));
            
            // less than max value
            {
                byte obj = TEST_VALUE_MAX_OBJ - TEST_VALUE_DIFF;
                String str = format(obj, TEST_FORMATTED_PATTERN);
                
                assertThat(cellProcessor.execute(str, ANONYMOUS_CSVCONTEXT), is(obj));
            }
            
            // greater than max value
            try {
                byte obj = TEST_VALUE_MAX_OBJ + TEST_VALUE_DIFF;
                String str = format(obj, TEST_FORMATTED_PATTERN);
                
                cellProcessor.execute(str, ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Max.class)));
            }
            
        }
        
        @Test
        public void testBuildOutput_max_format() {
            Annotation[] annos = getAnnotations(TestCsv.class, "byte_max_format");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(byte.class, annos, false);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, hasCellProcessor(Max.class));
            
            assertThat(cellProcessor.execute(TEST_VALUE_MAX_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_MAX_STR_FORMATTED));
            
            // less than max value
            {
                byte obj = TEST_VALUE_MAX_OBJ - TEST_VALUE_DIFF;
                String str = format(obj, TEST_FORMATTED_PATTERN);
                
                assertThat(cellProcessor.execute(obj, ANONYMOUS_CSVCONTEXT), is(str));
            }
            
            // greater than max value
            try {
                byte obj = TEST_VALUE_MAX_OBJ + TEST_VALUE_DIFF;
                String str = format(obj, TEST_FORMATTED_PATTERN);
                
                cellProcessor.execute(obj, ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Max.class)));
            }            
        }
        
        @Test
        public void testBuildOutput_max_format_ignoreValidation() {
            Annotation[] annos = getAnnotations(TestCsv.class, "byte_max_format");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(byte.class, annos, true);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, not(hasCellProcessor(Max.class)));
            
            // greater than max value
            {
                byte obj = TEST_VALUE_MAX_OBJ + TEST_VALUE_DIFF;
                String str = format(obj, TEST_FORMATTED_PATTERN);
                
                assertThat(cellProcessor.execute(obj, ANONYMOUS_CSVCONTEXT), is(str));
            
            }            
        }
        
        @Test
        public void testBuildInput_range() {
            Annotation[] annos = getAnnotations(TestCsv.class, "byte_range");
            CellProcessor cellProcessor = builder.buildInputCellProcessor(byte.class, annos);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, hasCellProcessor(Range.class));
            
            assertThat(cellProcessor.execute(TEST_VALUE_MIN_STR_NORMAL, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_MIN_OBJ));
            
            // greater than min value
            {
                byte obj = TEST_VALUE_MIN_OBJ + TEST_VALUE_DIFF;
                String str = Objects.toString(obj);
                
                assertThat(cellProcessor.execute(str, ANONYMOUS_CSVCONTEXT), is(obj));
            
            }
            
            // less than min value
            try {
                byte obj = TEST_VALUE_MIN_OBJ - TEST_VALUE_DIFF;
                String str = Objects.toString(obj);
                
                cellProcessor.execute(str, ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Range.class)));
            }
            
            
            assertThat(cellProcessor.execute(TEST_VALUE_MAX_STR_NORMAL, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_MAX_OBJ));
            
            // less than max value
            {
                byte obj = TEST_VALUE_MAX_OBJ - TEST_VALUE_DIFF;
                String str = Objects.toString(obj);
                
                assertThat(cellProcessor.execute(str, ANONYMOUS_CSVCONTEXT), is(obj));
            }
            
            // greater than max value
            try {
                byte obj = TEST_VALUE_MAX_OBJ + TEST_VALUE_DIFF;
                String str = Objects.toString(obj);
                
                cellProcessor.execute(str, ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Range.class)));
            }
            
        }
        
        @Test
        public void testBuildOutput_range() {
            Annotation[] annos = getAnnotations(TestCsv.class, "byte_range");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(byte.class, annos, false);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, hasCellProcessor(Range.class));
            
            assertThat(cellProcessor.execute(TEST_VALUE_MIN_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_MIN_OBJ));
            
            // greater than min value
            {
                byte obj = TEST_VALUE_MIN_OBJ + TEST_VALUE_DIFF;
                String str = Objects.toString(obj);
                assertThat(cellProcessor.execute(obj, ANONYMOUS_CSVCONTEXT), is(obj));
            }
            
            // less than min value
            try {
                byte obj = TEST_VALUE_MIN_OBJ - TEST_VALUE_DIFF;
                String str = Objects.toString(obj);
                
                cellProcessor.execute(obj, ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Range.class)));
            }
            
            assertThat(cellProcessor.execute(TEST_VALUE_MAX_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_MAX_OBJ));
            
            // less than max value
            {
                byte obj = TEST_VALUE_MAX_OBJ - TEST_VALUE_DIFF;
                String str = Objects.toString(obj);
                
                assertThat(cellProcessor.execute(obj, ANONYMOUS_CSVCONTEXT), is(obj));
            }
            
            // greater than max value
            try {
                byte obj = TEST_VALUE_MAX_OBJ + TEST_VALUE_DIFF;
                String str = Objects.toString(obj);
                
                cellProcessor.execute(obj, ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Range.class)));
            }            
        }
        
        @Test
        public void testBuildOutput_range_ignoreValidation() {
            Annotation[] annos = getAnnotations(TestCsv.class, "byte_range");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(byte.class, annos, true);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, not(hasCellProcessor(Range.class)));
            
            // less than min value
            {
                byte obj = TEST_VALUE_MIN_OBJ - TEST_VALUE_DIFF;
                String str = Objects.toString(obj);
                
                assertThat(cellProcessor.execute(obj, ANONYMOUS_CSVCONTEXT), is(obj));
            }
            
            // greater than max value
            {
                byte obj = TEST_VALUE_MAX_OBJ + TEST_VALUE_DIFF;
                String str = Objects.toString(obj);
                
                assertThat(cellProcessor.execute(obj, ANONYMOUS_CSVCONTEXT), is(obj));
            }        }
        
        @Test
        public void testBuildInput_range_format() {
            Annotation[] annos = getAnnotations(TestCsv.class, "byte_range_format");
            CellProcessor cellProcessor = builder.buildInputCellProcessor(byte.class, annos);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, hasCellProcessor(Range.class));
            
            assertThat(cellProcessor.execute(TEST_VALUE_MIN_STR_FORMATTED, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_MIN_OBJ));
            
            // greater than min value
            {
                byte obj = TEST_VALUE_MIN_OBJ + TEST_VALUE_DIFF;
                String str = format(obj, TEST_FORMATTED_PATTERN);
                
                assertThat(cellProcessor.execute(str, ANONYMOUS_CSVCONTEXT), is(obj));
            }
            
            // less than min value
            try {
                byte obj = TEST_VALUE_MIN_OBJ - TEST_VALUE_DIFF;
                String str = format(obj, TEST_FORMATTED_PATTERN);
                
                cellProcessor.execute(str, ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Range.class)));
            }
            
            assertThat(cellProcessor.execute(TEST_VALUE_MAX_STR_FORMATTED, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_MAX_OBJ));
            
            // less than max value
            {
                byte obj = TEST_VALUE_MAX_OBJ - TEST_VALUE_DIFF;
                String str = format(obj, TEST_FORMATTED_PATTERN);
                
                assertThat(cellProcessor.execute(str, ANONYMOUS_CSVCONTEXT), is(obj));
            }
            
            // greater than max value
            try {
                byte obj = TEST_VALUE_MAX_OBJ + TEST_VALUE_DIFF;
                String str = format(obj, TEST_FORMATTED_PATTERN);
                
                cellProcessor.execute(str, ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Range.class)));
            }
            
        }
        
        @Test
        public void testBuildOutput_range_format() {
            Annotation[] annos = getAnnotations(TestCsv.class, "byte_range_format");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(byte.class, annos, false);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, hasCellProcessor(Range.class));
            
            assertThat(cellProcessor.execute(TEST_VALUE_MIN_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_MIN_STR_FORMATTED));
            
            // greater than min value
            {
                byte obj = TEST_VALUE_MIN_OBJ + TEST_VALUE_DIFF;
                String str = format(obj, TEST_FORMATTED_PATTERN);
                
                assertThat(cellProcessor.execute(obj, ANONYMOUS_CSVCONTEXT), is(str));
            }
            
            // less than min value
            try {
                byte obj = TEST_VALUE_MIN_OBJ - TEST_VALUE_DIFF;
                String str = format(obj, TEST_FORMATTED_PATTERN);
                
                cellProcessor.execute(obj, ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Range.class)));
            }
            
            assertThat(cellProcessor.execute(TEST_VALUE_MAX_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_MAX_STR_FORMATTED));
            
            // less than max value
            {
                byte obj = TEST_VALUE_MAX_OBJ - TEST_VALUE_DIFF;
                String str = format(obj, TEST_FORMATTED_PATTERN);
                
                assertThat(cellProcessor.execute(obj, ANONYMOUS_CSVCONTEXT), is(str));
            }
            
            // greater than max value
            try {
                byte obj = TEST_VALUE_MAX_OBJ + TEST_VALUE_DIFF;
                String str = format(obj, TEST_FORMATTED_PATTERN);
                
                cellProcessor.execute(obj, ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Range.class)));
            }
            
        }
        
        @Test
        public void testBuildOutput_range_format_ignoreValidation() {
            Annotation[] annos = getAnnotations(TestCsv.class, "byte_range_format");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(byte.class, annos, true);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, not(hasCellProcessor(Range.class)));
            
            // less than min value
            {
                byte obj = TEST_VALUE_MIN_OBJ + TEST_VALUE_DIFF;
                String str = format(obj, TEST_FORMATTED_PATTERN);
                
                assertThat(cellProcessor.execute(obj, ANONYMOUS_CSVCONTEXT), is(str));
            }
            
            // greater than max value
            {
                byte obj = TEST_VALUE_MAX_OBJ + TEST_VALUE_DIFF;
                String str = format(obj, TEST_FORMATTED_PATTERN);
                
                assertThat(cellProcessor.execute(obj, ANONYMOUS_CSVCONTEXT), is(str));
            
            }
        }
        
        @Test
        public void testBuildInput_format_lenient() {
            Annotation[] annos = getAnnotations(TestCsv.class, "byte_format_lenient");
            CellProcessor cellProcessor = builder.buildInputCellProcessor(byte.class, annos);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, hasCellProcessor(ParseLocaleNumber.class));
            
            assertThat(cellProcessor.execute("12,345.567", ANONYMOUS_CSVCONTEXT), is((byte)12345));
            assertThat(cellProcessor.execute("12,345", ANONYMOUS_CSVCONTEXT), is((byte)12345));
            
        }
        
        @Test
        public void testBuildOutput_format_lenient() {
            Annotation[] annos = getAnnotations(TestCsv.class, "byte_format_lenient");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(byte.class, annos, true);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, hasCellProcessor(FormatLocaleNumber.class));
            
            assertThat(cellProcessor.execute((byte)123, ANONYMOUS_CSVCONTEXT), is("123.0"));
            
        }
        
        @Test
        public void testBuildInput_format_currency() {
            Annotation[] annos = getAnnotations(TestCsv.class, "byte_format_currency");
            CellProcessor cellProcessor = builder.buildInputCellProcessor(byte.class, annos);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, hasCellProcessor(ParseLocaleNumber.class));
            
            assertThat(cellProcessor.execute("USD 123.0000", ANONYMOUS_CSVCONTEXT), is((byte)123));
            
        }
        
        @Test
        public void testBuildOutput_format_currency() {
            Annotation[] annos = getAnnotations(TestCsv.class, "byte_format_currency");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(byte.class, annos, true);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, hasCellProcessor(FormatLocaleNumber.class));
            
            assertThat(cellProcessor.execute((byte)123, ANONYMOUS_CSVCONTEXT), is("USD 123.0000"));
            
        }
        
        @Test
        public void testBuildInput_format_roundingMode() {
            Annotation[] annos = getAnnotations(TestCsv.class, "byte_format_roundingMode");
            CellProcessor cellProcessor = builder.buildInputCellProcessor(byte.class, annos);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, hasCellProcessor(ParseLocaleNumber.class));
            
            assertThat(cellProcessor.execute("123", ANONYMOUS_CSVCONTEXT), is((byte)123));
            
        }
        
        @Test
        public void testBuildOutput_format_roundingMode() {
            Annotation[] annos = getAnnotations(TestCsv.class, "byte_format_roundingMode");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(byte.class, annos, true);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, hasCellProcessor(FormatLocaleNumber.class));
            
            assertThat(cellProcessor.execute((byte)123, ANONYMOUS_CSVCONTEXT), is("123"));
            
        }
    }
}

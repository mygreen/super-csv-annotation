package org.supercsv.ext.builder.impl;

import static org.junit.Assert.*;
import static org.supercsv.ext.tool.TestUtils.*;
import static org.hamcrest.Matchers.*;
import static org.supercsv.ext.tool.HasCellProcessor.*;

import java.lang.annotation.Annotation;
import java.math.RoundingMode;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.supercsv.cellprocessor.ConvertNullTo;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ParseInt;
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
import org.supercsv.ext.cellprocessor.Trim;
import org.supercsv.ext.cellprocessor.constraint.Max;
import org.supercsv.ext.cellprocessor.constraint.Min;
import org.supercsv.ext.cellprocessor.constraint.Range;
import org.supercsv.ext.exception.SuperCsvInvalidAnnotationException;

/**
 * Test the {@link IntegerCellProcessorBuilder} CellProcessor.
 * 
 * @since 1.2
 * @author T.TSUCHIE
 *
 */
@RunWith(Enclosed.class)
public class IntegerCellProcessorBuilderTest {
    
    /**
     * Tests for wrapper integer.
     *
     */
    public static class IntegerTest {
    
        @Rule
        public TestName name = new TestName();
        
        private IntegerCellProcessorBuilder builder;
        
        /**
         * Sets up the processor for the test using Combinations
         */
        @Before
        public void setUp() {
            builder = new IntegerCellProcessorBuilder();
        }
        
        @CsvBean
        private static class TestCsv {
            
            @CsvColumn(position=0)
            Integer integer_default;
            
            @CsvColumn(position=1, optional=true)
            Integer integer_optional;
            
            @CsvColumn(position=2, trim=true)
            Integer integer_trim;
            
            @CsvColumn(position=3, inputDefaultValue="12345", outputDefaultValue="-67890")
            Integer integer_defaultValue;
            
            @CsvColumn(position=4, inputDefaultValue="12,345", outputDefaultValue="-67,890")
            @CsvNumberConverter(pattern="#,###")
            Integer integer_defaultValue_format;
            
            @CsvColumn(position=4, inputDefaultValue="abc12,345")
            @CsvNumberConverter(pattern="#,###")
            Integer integer_defaultValue_format_invalid;
            
            @CsvColumn(position=5, equalsValue="123")
            Integer integer_equalsValue;
            
            @CsvColumn(position=6, equalsValue="12,345")
            @CsvNumberConverter(pattern="#,###")
            Integer integer_equalsValue_format;
            
            @CsvColumn(position=7, unique=true)
            Integer integer_unique;
            
            @CsvColumn(position=8, unique=true)
            @CsvNumberConverter(pattern="#,###")
            Integer integer_unique_format;
            
            @CsvColumn(position=9, optional=true, trim=true, inputDefaultValue="123", outputDefaultValue="-678", equalsValue="12345", unique=true)
            Integer integer_combine1;
            
            @CsvColumn(position=10, optional=true, trim=true, inputDefaultValue="123", outputDefaultValue="-678", equalsValue="12345", unique=true)
            @CsvNumberConverter(pattern="#,###")
            Integer integer_combine_format1;
            
            @CsvColumn(position=11)
            @CsvNumberConverter(min="5")
            Integer integer_min;
            
            @CsvColumn(position=12)
            @CsvNumberConverter(min="-12,345", pattern="#,###")
            Integer integer_min_format;
            
            @CsvColumn(position=13)
            @CsvNumberConverter(max="10")
            Integer integer_max;
            
            @CsvColumn(position=14)
            @CsvNumberConverter(max="5,678", pattern="#,###")
            Integer integer_max_format;
            
            @CsvColumn(position=15)
            @CsvNumberConverter(min="5", max="10")
            Integer integer_range;
            
            @CsvColumn(position=16)
            @CsvNumberConverter(min="-12,345", max="5,678", pattern="#,###")
            Integer integer_range_format;
            
            @CsvColumn(position=17)
            @CsvNumberConverter(pattern="#,##0.0##", lenient=true)
            Integer integer_format_lenient;
            
            @CsvColumn(position=18)
            @CsvNumberConverter(pattern="\u00A4 #,##0.0000", currency="USD")
            Integer integer_format_currency;
            
            @CsvColumn(position=19)
            @CsvNumberConverter(pattern="#,##0", roundingMode=RoundingMode.HALF_UP)
            Integer integer_format_roundingMode;
            
        }
        
        /**
         * Tests with default. (not grant convert annotation.)
         */
        @Test
        public void testBuildInput_default() {
            
            Annotation[] annos = getAnnotations(TestCsv.class, "integer_default");
            CellProcessor cellProcessor = builder.buildInputCellProcessor(Integer.class, annos);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            assertThat(cellProcessor, hasCellProcessor(NotNull.class));
            assertThat(cellProcessor, hasCellProcessor(ParseInt.class));
            
            assertThat(cellProcessor.execute("123", ANONYMOUS_CSVCONTEXT), is(toInteger("123")));
            
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
                assertThat(errorProcessor, is(instanceOf(ParseInt.class)));
            }
            
        }
        
        /**
         * Tests with default. (not grant convert annotation.)
         */
        @Test
        public void testBuildOutput_default() {
            
            Annotation[] annos = getAnnotations(TestCsv.class, "integer_default");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(Integer.class, annos, false);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            assertThat(cellProcessor, hasCellProcessor(NotNull.class));
            
            assertThat(cellProcessor.execute(toInteger("123"), ANONYMOUS_CSVCONTEXT), is(toInteger("123")));
            
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
            
            Annotation[] annos = getAnnotations(TestCsv.class, "integer_default");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(Integer.class, annos, true);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            assertThat(cellProcessor, hasCellProcessor(NotNull.class));
            
            assertThat(cellProcessor.execute(toInteger("123"), ANONYMOUS_CSVCONTEXT), is(toInteger("123")));
            
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
            
            Annotation[] annos = getAnnotations(TestCsv.class, "integer_optional");
            CellProcessor cellProcessor = builder.buildInputCellProcessor(Integer.class, annos);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            assertThat(cellProcessor, hasCellProcessor(Optional.class));
            
            assertThat(cellProcessor.execute("123", ANONYMOUS_CSVCONTEXT), is(toInteger("123")));
            
            // null input
            assertThat(cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT), is(nullValue()));
        }
        
        /**
         * Tests with optional. (not grant convert annotation.)
         */
        @Test
        public void testBuildOutput_optional() {
            
            Annotation[] annos = getAnnotations(TestCsv.class, "integer_optional");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(Integer.class, annos, false);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            assertThat(cellProcessor, hasCellProcessor(Optional.class));
            
            assertThat(cellProcessor.execute("123", ANONYMOUS_CSVCONTEXT), is(toInteger("123")));
            
            // null input
            assertThat(cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT), is(nullValue()));
        }
        
        /**
         * Tests with optional. (not grant convert annotation.)
         */
        @Test
        public void testBuildOutput_optional_ignoreValidation() {
            
            Annotation[] annos = getAnnotations(TestCsv.class, "integer_optional");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(Integer.class, annos, true);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            assertThat(cellProcessor, hasCellProcessor(Optional.class));
            
            assertThat(cellProcessor.execute(toInteger("123"), ANONYMOUS_CSVCONTEXT), is(toInteger("123")));
            
            // null input
            assertThat(cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT), is(nullValue()));
        }
        
        @Test
        public void testBuildInput_trim() {
            Annotation[] annos = getAnnotations(TestCsv.class, "integer_trim");
            CellProcessor cellProcessor = builder.buildInputCellProcessor(Integer.class, annos);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            assertThat(cellProcessor, hasCellProcessor(Trim.class));
            
            assertThat(cellProcessor.execute("  123 ", ANONYMOUS_CSVCONTEXT), is(toInteger("123")));
        }
        
        @Test
        public void testBuildOutput_trim() {
            Annotation[] annos = getAnnotations(TestCsv.class, "integer_trim");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(Integer.class, annos, false);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            assertThat(cellProcessor, hasCellProcessor(Trim.class));
            
            assertThat(cellProcessor.execute(toInteger("123"), ANONYMOUS_CSVCONTEXT), is("123"));
        }
        
        @Test
        public void testBuildOutput_trim_ignoreValidation() {
            Annotation[] annos = getAnnotations(TestCsv.class, "integer_trim");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(Integer.class, annos, true);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            assertThat(cellProcessor, hasCellProcessor(Trim.class));
            
            assertThat(cellProcessor.execute(toInteger("123"), ANONYMOUS_CSVCONTEXT), is("123"));
        }
        
        @Test
        public void testBuildInput_defaultValue() {
            Annotation[] annos = getAnnotations(TestCsv.class, "integer_defaultValue");
            CellProcessor cellProcessor = builder.buildInputCellProcessor(Integer.class, annos);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            assertThat(cellProcessor, hasCellProcessor(ConvertNullTo.class));
            
            assertThat(cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT), is(toInteger("12345")));
        }
        
        @Test
        public void testBuildOutput_defaultValue() {
            Annotation[] annos = getAnnotations(TestCsv.class, "integer_defaultValue");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(Integer.class, annos, false);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            assertThat(cellProcessor, hasCellProcessor(ConvertNullTo.class));
            
            assertThat(cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT), is("-67890"));
        }
        
        @Test
        public void testBuildOutput_defaultValue_ignoreValidation() {
            Annotation[] annos = getAnnotations(TestCsv.class, "integer_defaultValue");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(Integer.class, annos, true);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            assertThat(cellProcessor, hasCellProcessor(ConvertNullTo.class));
            
            assertThat(cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT), is("-67890"));
        }
        
        @Test
        public void testBuildInput_defaultValue_format() {
            Annotation[] annos = getAnnotations(TestCsv.class, "integer_defaultValue_format");
            CellProcessor cellProcessor = builder.buildInputCellProcessor(Integer.class, annos);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            assertThat(cellProcessor, hasCellProcessor(ConvertNullTo.class));
            
            assertThat(cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT), is(toInteger("12345")));
        }
        
        @Test
        public void testBuildOutput_defaultValue_format() {
            Annotation[] annos = getAnnotations(TestCsv.class, "integer_defaultValue_format");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(Integer.class, annos, false);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            assertThat(cellProcessor, hasCellProcessor(ConvertNullTo.class));
            
            assertThat(cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT), is("-67,890"));
        }
        
        @Test
        public void testBuildOutput_defaultValue_format_ignoreValidation() {
            Annotation[] annos = getAnnotations(TestCsv.class, "integer_defaultValue_format");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(Integer.class, annos, true);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            assertThat(cellProcessor, hasCellProcessor(ConvertNullTo.class));
            
            assertThat(cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT), is("-67,890"));
        }
        
        /**
         * Tests with default. (not grant convert annotation.)
         */
        @Test(expected=SuperCsvInvalidAnnotationException.class)
        public void testBuildInput_default_format_invalidAnnotation() {
            
            Annotation[] annos = getAnnotations(TestCsv.class, "integer_defaultValue_format_invalid");
            CellProcessor cellProcessor = builder.buildInputCellProcessor(Integer.class, annos);
            
            cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT);
            fail();
            
        }
        
        @Test
        public void testBuildInput_equalsValue() {
            Annotation[] annos = getAnnotations(TestCsv.class, "integer_equalsValue");
            CellProcessor cellProcessor = builder.buildInputCellProcessor(Integer.class, annos);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            assertThat(cellProcessor, hasCellProcessor(Equals.class));
            
            assertThat(cellProcessor.execute("123", ANONYMOUS_CSVCONTEXT), is(toInteger("123")));
            
            // not quals input
            try {
                cellProcessor.execute("456", ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Equals.class)));
            }
        }
        
        @Test
        public void testBuildOutput_equalsValue() {
            Annotation[] annos = getAnnotations(TestCsv.class, "integer_equalsValue");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(Integer.class, annos, false);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            assertThat(cellProcessor, hasCellProcessor(Equals.class));
            
            assertThat(cellProcessor.execute(toInteger("123"), ANONYMOUS_CSVCONTEXT), is(toInteger("123")));
            
            // not quals input
            try {
                cellProcessor.execute(toInteger("456"), ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Equals.class)));
            }
        }
        
        @Test
        public void testBuildOutput_equalsValue_ignoreValidation() {
            Annotation[] annos = getAnnotations(TestCsv.class, "integer_equalsValue");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(Integer.class, annos, true);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            assertThat(cellProcessor, not(hasCellProcessor(Equals.class)));
            
            assertThat(cellProcessor.execute(toInteger("123"), ANONYMOUS_CSVCONTEXT), is(toInteger("123")));
            
            // not quals input
            assertThat(cellProcessor.execute(toInteger("456"), ANONYMOUS_CSVCONTEXT), is(toInteger("456")));
            
        }
        
        @Test
        public void testBuildInput_equalsValue_format() {
            Annotation[] annos = getAnnotations(TestCsv.class, "integer_equalsValue_format");
            CellProcessor cellProcessor = builder.buildInputCellProcessor(Integer.class, annos);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            assertThat(cellProcessor, hasCellProcessor(Equals.class));
            
            assertThat(cellProcessor.execute("12,345", ANONYMOUS_CSVCONTEXT), is(toInteger("12345")));
            
            // not quals input
            try {
                cellProcessor.execute("456", ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Equals.class)));
            }
        }
        
        @Test
        public void testBuildOutput_equalsValue_format() {
            Annotation[] annos = getAnnotations(TestCsv.class, "integer_equalsValue_format");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(Integer.class, annos, false);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            assertThat(cellProcessor, hasCellProcessor(Equals.class));
            
            assertThat(cellProcessor.execute(toInteger("12345"), ANONYMOUS_CSVCONTEXT), is("12,345"));
            
            // not quals input
            try {
                cellProcessor.execute(toInteger("456"), ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Equals.class)));
            }
        }
        
        @Test
        public void testBuildOutput_equalsValue_format_ignoreValidation() {
            Annotation[] annos = getAnnotations(TestCsv.class, "integer_equalsValue_format");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(Integer.class, annos, true);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            assertThat(cellProcessor, not(hasCellProcessor(Equals.class)));
            
            assertThat(cellProcessor.execute(toInteger("12345"), ANONYMOUS_CSVCONTEXT), is("12,345"));
            
            // not quals input
            assertThat(cellProcessor.execute(toInteger("456"), ANONYMOUS_CSVCONTEXT), is("456"));
            
        }
        
        @Test
        public void testBuildInput_unique() {
            Annotation[] annos = getAnnotations(TestCsv.class, "integer_unique");
            CellProcessor cellProcessor = builder.buildInputCellProcessor(Integer.class, annos);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            assertThat(cellProcessor, hasCellProcessor(Unique.class));
            
            assertThat(cellProcessor.execute("123", ANONYMOUS_CSVCONTEXT), is(toInteger("123")));
            assertThat(cellProcessor.execute("456", ANONYMOUS_CSVCONTEXT), is(toInteger("456")));
            
            // not unique input
            try {
                cellProcessor.execute("456", ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Unique.class)));
            }
        }
        
        @Test
        public void testBuildOutput_unique() {
            Annotation[] annos = getAnnotations(TestCsv.class, "integer_unique");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(Integer.class, annos, false);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            assertThat(cellProcessor, hasCellProcessor(Unique.class));
            
            assertThat(cellProcessor.execute(toInteger("123"), ANONYMOUS_CSVCONTEXT), is(toInteger("123")));
            assertThat(cellProcessor.execute(toInteger("456"), ANONYMOUS_CSVCONTEXT), is(toInteger("456")));
            
            // not unique input
            try {
                cellProcessor.execute(toInteger("456"), ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Unique.class)));
            }
        }
        
        @Test
        public void testBuildOutput_unique_ignoreValidation() {
            Annotation[] annos = getAnnotations(TestCsv.class, "integer_unique");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(Integer.class, annos, true);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            assertThat(cellProcessor, not(hasCellProcessor(Unique.class)));
            
            assertThat(cellProcessor.execute(toInteger("123"), ANONYMOUS_CSVCONTEXT), is(toInteger("123")));
            assertThat(cellProcessor.execute(toInteger("123"), ANONYMOUS_CSVCONTEXT), is(toInteger("123")));
            
        }
        
        @Test
        public void testBuildInput_unique_format() {
            Annotation[] annos = getAnnotations(TestCsv.class, "integer_unique_format");
            CellProcessor cellProcessor = builder.buildInputCellProcessor(Integer.class, annos);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            assertThat(cellProcessor, hasCellProcessor(Unique.class));
            
            assertThat(cellProcessor.execute("12,345", ANONYMOUS_CSVCONTEXT), is(toInteger("12345")));
            assertThat(cellProcessor.execute("456", ANONYMOUS_CSVCONTEXT), is(toInteger("456")));
            
            // not unique input
            try {
                cellProcessor.execute("456", ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Unique.class)));
            }
        }
        
        @Test
        public void testBuildOutput_unique_format() {
            Annotation[] annos = getAnnotations(TestCsv.class, "integer_unique_format");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(Integer.class, annos, false);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            assertThat(cellProcessor, hasCellProcessor(Unique.class));
            
            assertThat(cellProcessor.execute(toInteger("12345"), ANONYMOUS_CSVCONTEXT), is("12,345"));
            assertThat(cellProcessor.execute(toInteger("456"), ANONYMOUS_CSVCONTEXT), is("456"));
            
            // not unique input
            try {
                cellProcessor.execute(toInteger("456"), ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Unique.class)));
            }
        }
        
        @Test
        public void testBuildOutput_unique_format_ignoreValidation() {
            Annotation[] annos = getAnnotations(TestCsv.class, "integer_unique_format");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(Integer.class, annos, true);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            assertThat(cellProcessor, not(hasCellProcessor(Unique.class)));
            
            assertThat(cellProcessor.execute(toInteger("12345"), ANONYMOUS_CSVCONTEXT), is("12,345"));
            assertThat(cellProcessor.execute(toInteger("12345"), ANONYMOUS_CSVCONTEXT), is("12,345"));
            
        }
        
        @Test
        public void testBuildInput_combine1() {
            Annotation[] annos = getAnnotations(TestCsv.class, "integer_combine1");
            CellProcessor cellProcessor = builder.buildInputCellProcessor(Integer.class, annos);
            
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT), is(toInteger("123")));
            assertThat(cellProcessor.execute("  12345  ", ANONYMOUS_CSVCONTEXT), is(toInteger("12345")));
            
            // not equals input
            try {
                cellProcessor.execute("678", ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Equals.class)));
            }
            
            // not unique input
            try {
                cellProcessor.execute("12345", ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Unique.class)));
            }
        }
        
        @Test
        public void testBuildOutput_combine1() {
            Annotation[] annos = getAnnotations(TestCsv.class, "integer_combine1");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(Integer.class, annos, false);
            
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT), is("-678"));
            assertThat(cellProcessor.execute(toInteger("12345"), ANONYMOUS_CSVCONTEXT), is("12345"));
            
            // not equals input
            try {
                cellProcessor.execute("678", ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Equals.class)));
            }
            
            // not unique input
            try {
                cellProcessor.execute(toInteger("12345"), ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Unique.class)));
            }
        }
        
        @Test
        public void testBuildOutput_combine1_ignoreValidation() {
            Annotation[] annos = getAnnotations(TestCsv.class, "integer_combine1");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(Integer.class, annos, true);
            
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT), is("-678"));
            assertThat(cellProcessor.execute(toInteger("12345"), ANONYMOUS_CSVCONTEXT), is("12345"));
            
            // not equals input
            assertThat(cellProcessor.execute(toInteger("678"), ANONYMOUS_CSVCONTEXT), is("678"));
            
            // not unique input
            assertThat(cellProcessor.execute(toInteger("12345"), ANONYMOUS_CSVCONTEXT), is("12345"));
            
        }
        
        @Test
        public void testBuildInput_combine_format1() {
            Annotation[] annos = getAnnotations(TestCsv.class, "integer_combine_format1");
            CellProcessor cellProcessor = builder.buildInputCellProcessor(Integer.class, annos);
            
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT), is(toInteger("123")));
            assertThat(cellProcessor.execute("  12345  ", ANONYMOUS_CSVCONTEXT), is(toInteger("12345")));
            
            // not equals input
            try {
                cellProcessor.execute("678", ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Equals.class)));
            }
            
            // not unique input
            try {
                cellProcessor.execute("12345", ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Unique.class)));
            }
        }
        
        @Test
        public void testBuildOutput_combine_format1() {
            Annotation[] annos = getAnnotations(TestCsv.class, "integer_combine_format1");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(Integer.class, annos, false);
            
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT), is("-678"));
            assertThat(cellProcessor.execute(toInteger("12345"), ANONYMOUS_CSVCONTEXT), is("12,345"));
            
            // not equals input
            try {
                cellProcessor.execute("678", ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Equals.class)));
            }
            
            // not unique input
            try {
                cellProcessor.execute(toInteger("12345"), ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Unique.class)));
            }
        }
        
        @Test
        public void testBuildOutput_combine_format1_ignoreValidation() {
            Annotation[] annos = getAnnotations(TestCsv.class, "integer_combine_format1");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(Integer.class, annos, true);
            
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT), is("-678"));
            assertThat(cellProcessor.execute(toInteger("12345"), ANONYMOUS_CSVCONTEXT), is("12,345"));
            
            // not equals input
            assertThat(cellProcessor.execute(toInteger("678"), ANONYMOUS_CSVCONTEXT), is("678"));
            
            // not unique input
            assertThat(cellProcessor.execute(toInteger("12345"), ANONYMOUS_CSVCONTEXT), is("12,345"));
            
        }
        
        @Test
        public void testBuildInput_min() {
            Annotation[] annos = getAnnotations(TestCsv.class, "integer_min");
            CellProcessor cellProcessor = builder.buildInputCellProcessor(Integer.class, annos);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            assertThat(cellProcessor, hasCellProcessor(Min.class));
            
            assertThat(cellProcessor.execute("5", ANONYMOUS_CSVCONTEXT), is(toInteger("5")));
            assertThat(cellProcessor.execute("6", ANONYMOUS_CSVCONTEXT), is(toInteger("6")));
            
            // value less than min(5)
            try {
                cellProcessor.execute("4", ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Min.class)));
            }
            
        }
        
        @Test
        public void testBuildOutput_min() {
            Annotation[] annos = getAnnotations(TestCsv.class, "integer_min");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(Integer.class, annos, false);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            assertThat(cellProcessor, hasCellProcessor(Min.class));
            
            assertThat(cellProcessor.execute(toInteger("5"), ANONYMOUS_CSVCONTEXT), is(toInteger("5")));
            assertThat(cellProcessor.execute(toInteger("6"), ANONYMOUS_CSVCONTEXT), is(toInteger("6")));
            
            // value less than min(5)
            try {
                cellProcessor.execute(toInteger("4"), ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Min.class)));
            }
            
        }
        
        @Test
        public void testBuildOutput_min_ignoreValidation() {
            Annotation[] annos = getAnnotations(TestCsv.class, "integer_min");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(Integer.class, annos, true);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            assertThat(cellProcessor, not(hasCellProcessor(Min.class)));
            
            // value less than min(5)
            assertThat(cellProcessor.execute(toInteger("4"), ANONYMOUS_CSVCONTEXT), is(toInteger("4")));
            
        }
        
        @Test
        public void testBuildInput_min_format() {
            Annotation[] annos = getAnnotations(TestCsv.class, "integer_min_format");
            CellProcessor cellProcessor = builder.buildInputCellProcessor(Integer.class, annos);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            assertThat(cellProcessor, hasCellProcessor(Min.class));
            
            assertThat(cellProcessor.execute("-12,345", ANONYMOUS_CSVCONTEXT), is(toInteger("-12345")));
            assertThat(cellProcessor.execute("-12,344", ANONYMOUS_CSVCONTEXT), is(toInteger("-12344")));
            
            // value less than min(-12345)
            try {
                cellProcessor.execute("-12346", ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Min.class)));
            }
            
        }
        
        @Test
        public void testBuildOutput_min_format() {
            Annotation[] annos = getAnnotations(TestCsv.class, "integer_min_format");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(Integer.class, annos, false);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            assertThat(cellProcessor, hasCellProcessor(Min.class));
            
            assertThat(cellProcessor.execute(toInteger("-12345"), ANONYMOUS_CSVCONTEXT), is("-12,345"));
            assertThat(cellProcessor.execute(toInteger("-12344"), ANONYMOUS_CSVCONTEXT), is("-12,344"));
            
            // value less than min(-12345)
            try {
                cellProcessor.execute(toInteger("-12346"), ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Min.class)));
            }
            
        }
        
        @Test
        public void testBuildOutput_min_format_ignoreValidation() {
            Annotation[] annos = getAnnotations(TestCsv.class, "integer_min_format");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(Integer.class, annos, true);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            assertThat(cellProcessor, not(hasCellProcessor(Min.class)));
            
            // value less than min(-12345)
            assertThat(cellProcessor.execute(toInteger("-12346"), ANONYMOUS_CSVCONTEXT), is("-12,346"));
            
        }
        
        @Test
        public void testBuildInput_max() {
            Annotation[] annos = getAnnotations(TestCsv.class, "integer_max");
            CellProcessor cellProcessor = builder.buildInputCellProcessor(Integer.class, annos);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            assertThat(cellProcessor, hasCellProcessor(Max.class));
            
            assertThat(cellProcessor.execute("10", ANONYMOUS_CSVCONTEXT), is(toInteger("10")));
            assertThat(cellProcessor.execute("9", ANONYMOUS_CSVCONTEXT), is(toInteger("9")));
            
            // value greater than max(10)
            try {
                cellProcessor.execute("11", ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Max.class)));
            }
            
        }
        
        @Test
        public void testBuildOutput_max() {
            Annotation[] annos = getAnnotations(TestCsv.class, "integer_max");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(Integer.class, annos, false);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            assertThat(cellProcessor, hasCellProcessor(Max.class));
            
            assertThat(cellProcessor.execute(toInteger("10"), ANONYMOUS_CSVCONTEXT), is(toInteger("10")));
            assertThat(cellProcessor.execute(toInteger("9"), ANONYMOUS_CSVCONTEXT), is(toInteger("9")));
            
            // value greater than max(10)
            try {
                cellProcessor.execute(toInteger("11"), ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Max.class)));
            }
            
        }
        
        @Test
        public void testBuildOutput_max_ignoreValidation() {
            Annotation[] annos = getAnnotations(TestCsv.class, "integer_max");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(Integer.class, annos, true);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            assertThat(cellProcessor, not(hasCellProcessor(Max.class)));
            
            // value greater than max(10)
            assertThat(cellProcessor.execute(toInteger("11"), ANONYMOUS_CSVCONTEXT), is(toInteger("11")));
            
        }
        
        @Test
        public void testBuildInput_max_format() {
            Annotation[] annos = getAnnotations(TestCsv.class, "integer_max_format");
            CellProcessor cellProcessor = builder.buildInputCellProcessor(Integer.class, annos);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            assertThat(cellProcessor, hasCellProcessor(Max.class));
            
            assertThat(cellProcessor.execute("5,678", ANONYMOUS_CSVCONTEXT), is(toInteger("5678")));
            assertThat(cellProcessor.execute("5,677", ANONYMOUS_CSVCONTEXT), is(toInteger("5677")));
            
            // value greater than max(5679)
            try {
                cellProcessor.execute("5,679", ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Max.class)));
            }
            
        }
        
        @Test
        public void testBuildOutput_max_format() {
            Annotation[] annos = getAnnotations(TestCsv.class, "integer_max_format");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(Integer.class, annos, false);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            assertThat(cellProcessor, hasCellProcessor(Max.class));
            
            assertThat(cellProcessor.execute(toInteger("5678"), ANONYMOUS_CSVCONTEXT), is("5,678"));
            assertThat(cellProcessor.execute(toInteger("5677"), ANONYMOUS_CSVCONTEXT), is("5,677"));
            
            // value greater than max(5679)
            try {
                cellProcessor.execute(toInteger("5679"), ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Max.class)));
            }
            
        }
        
        @Test
        public void testBuildOutput_max_format_ignoreValidation() {
            Annotation[] annos = getAnnotations(TestCsv.class, "integer_max_format");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(Integer.class, annos, true);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            assertThat(cellProcessor, not(hasCellProcessor(Max.class)));
            
            // value greater than max(5679)
            assertThat(cellProcessor.execute(toInteger("5679"), ANONYMOUS_CSVCONTEXT), is("5,679"));
            
        }
        
        @Test
        public void testBuildInput_range() {
            Annotation[] annos = getAnnotations(TestCsv.class, "integer_range");
            CellProcessor cellProcessor = builder.buildInputCellProcessor(Integer.class, annos);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            assertThat(cellProcessor, hasCellProcessor(Range.class));
            
            assertThat(cellProcessor.execute("5", ANONYMOUS_CSVCONTEXT), is(toInteger("5")));
            assertThat(cellProcessor.execute("6", ANONYMOUS_CSVCONTEXT), is(toInteger("6")));
            
            assertThat(cellProcessor.execute("10", ANONYMOUS_CSVCONTEXT), is(toInteger("10")));
            assertThat(cellProcessor.execute("9", ANONYMOUS_CSVCONTEXT), is(toInteger("9")));
            
            // value less than range(5, 10)
            try {
                cellProcessor.execute("4", ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Range.class)));
            }
            
            // value greater than range(5, 10)
            try {
                cellProcessor.execute("11", ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Range.class)));
            }
            
        }
        
        @Test
        public void testBuildOutput_range() {
            Annotation[] annos = getAnnotations(TestCsv.class, "integer_range");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(Integer.class, annos, false);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            assertThat(cellProcessor, hasCellProcessor(Range.class));
            
            assertThat(cellProcessor.execute(toInteger("5"), ANONYMOUS_CSVCONTEXT), is(toInteger("5")));
            assertThat(cellProcessor.execute(toInteger("6"), ANONYMOUS_CSVCONTEXT), is(toInteger("6")));
            
            assertThat(cellProcessor.execute(toInteger("10"), ANONYMOUS_CSVCONTEXT), is(toInteger("10")));
            assertThat(cellProcessor.execute(toInteger("9"), ANONYMOUS_CSVCONTEXT), is(toInteger("9")));
            
            // value less than range(5,10)
            try {
                cellProcessor.execute(toInteger("4"), ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Range.class)));
            }
            
            try {
                cellProcessor.execute(toInteger("11"), ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Range.class)));
            }
            
        }
        
        @Test
        public void testBuildOutput_range_ignoreValidation() {
            Annotation[] annos = getAnnotations(TestCsv.class, "integer_range");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(Integer.class, annos, true);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            assertThat(cellProcessor, not(hasCellProcessor(Range.class)));
            
            // value less than range(5,10)
            assertThat(cellProcessor.execute(toInteger("4"), ANONYMOUS_CSVCONTEXT), is(toInteger("4")));
            
            assertThat(cellProcessor.execute(toInteger("11"), ANONYMOUS_CSVCONTEXT), is(toInteger("11")));
        }
        
        @Test
        public void testBuildInput_range_format() {
            Annotation[] annos = getAnnotations(TestCsv.class, "integer_range_format");
            CellProcessor cellProcessor = builder.buildInputCellProcessor(Integer.class, annos);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            assertThat(cellProcessor, hasCellProcessor(Range.class));
            
            assertThat(cellProcessor.execute("-12,345", ANONYMOUS_CSVCONTEXT), is(toInteger("-12345")));
            assertThat(cellProcessor.execute("-12,344", ANONYMOUS_CSVCONTEXT), is(toInteger("-12344")));
            
            assertThat(cellProcessor.execute("5,678", ANONYMOUS_CSVCONTEXT), is(toInteger("5678")));
            assertThat(cellProcessor.execute("5,677", ANONYMOUS_CSVCONTEXT), is(toInteger("5677")));
            
            // value less than range(-12345,5678)
            try {
                cellProcessor.execute("-12346", ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Range.class)));
            }
            
            // value greater than max(-12345,5678)
            try {
                cellProcessor.execute("5,679", ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Range.class)));
            }
            
        }
        
        @Test
        public void testBuildOutput_range_format() {
            Annotation[] annos = getAnnotations(TestCsv.class, "integer_range_format");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(Integer.class, annos, false);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            assertThat(cellProcessor, hasCellProcessor(Range.class));
            
            assertThat(cellProcessor.execute(toInteger("-12345"), ANONYMOUS_CSVCONTEXT), is("-12,345"));
            assertThat(cellProcessor.execute(toInteger("-12344"), ANONYMOUS_CSVCONTEXT), is("-12,344"));
            
            assertThat(cellProcessor.execute(toInteger("5678"), ANONYMOUS_CSVCONTEXT), is("5,678"));
            assertThat(cellProcessor.execute(toInteger("5677"), ANONYMOUS_CSVCONTEXT), is("5,677"));
            
            // value less than range(-12345,5678)
            try {
                cellProcessor.execute(toInteger("-12346"), ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Range.class)));
            }
            
            // value greater than range(-12345,5678)
            try {
                cellProcessor.execute(toInteger("5679"), ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Range.class)));
            }
            
        }
        
        @Test
        public void testBuildOutput_range_format_ignoreValidation() {
            Annotation[] annos = getAnnotations(TestCsv.class, "integer_range_format");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(Integer.class, annos, true);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, not(hasCellProcessor(Range.class)));
            
            // value less than range(-12345,5678)
            assertThat(cellProcessor.execute(toInteger("-12346"), ANONYMOUS_CSVCONTEXT), is("-12,346"));
            
            // value greater than max(-12345,5678)
            assertThat(cellProcessor.execute(toInteger("5679"), ANONYMOUS_CSVCONTEXT), is("5,679"));
            
        }
        
        @Test
        public void testBuildInput_format_lenient() {
            Annotation[] annos = getAnnotations(TestCsv.class, "integer_format_lenient");
            CellProcessor cellProcessor = builder.buildInputCellProcessor(Integer.class, annos);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, hasCellProcessor(ParseLocaleNumber.class));
            
            assertThat(cellProcessor.execute("12,345.567", ANONYMOUS_CSVCONTEXT), is(toInteger("12345")));
            assertThat(cellProcessor.execute("12,345", ANONYMOUS_CSVCONTEXT), is(toInteger("12345")));
            
        }
        
        @Test
        public void testBuildOutput_format_lenient() {
            Annotation[] annos = getAnnotations(TestCsv.class, "integer_format_lenient");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(Integer.class, annos, true);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, hasCellProcessor(FormatLocaleNumber.class));
            
            assertThat(cellProcessor.execute(toInteger("12345"), ANONYMOUS_CSVCONTEXT), is("12,345.0"));
            
        }
        
        @Test
        public void testBuildInput_format_currency() {
            Annotation[] annos = getAnnotations(TestCsv.class, "integer_format_currency");
            CellProcessor cellProcessor = builder.buildInputCellProcessor(Integer.class, annos);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, hasCellProcessor(ParseLocaleNumber.class));
            
            assertThat(cellProcessor.execute("USD 12,345.0000", ANONYMOUS_CSVCONTEXT), is(toInteger("12345")));
            
        }
        
        @Test
        public void testBuildOutput_format_currency() {
            Annotation[] annos = getAnnotations(TestCsv.class, "integer_format_currency");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(Integer.class, annos, true);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, hasCellProcessor(FormatLocaleNumber.class));
            
            assertThat(cellProcessor.execute(toInteger("12345"), ANONYMOUS_CSVCONTEXT), is("USD 12,345.0000"));
            
        }
        
        @Test
        public void testBuildInput_format_roundingMode() {
            Annotation[] annos = getAnnotations(TestCsv.class, "integer_format_roundingMode");
            CellProcessor cellProcessor = builder.buildInputCellProcessor(Integer.class, annos);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, hasCellProcessor(ParseLocaleNumber.class));
            
            assertThat(cellProcessor.execute("12,345", ANONYMOUS_CSVCONTEXT), is(toInteger("12345")));
            
        }
        
        @Test
        public void testBuildOutput_format_roundingMode() {
            Annotation[] annos = getAnnotations(TestCsv.class, "integer_format_roundingMode");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(Integer.class, annos, true);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, hasCellProcessor(FormatLocaleNumber.class));
            
            assertThat(cellProcessor.execute(toInteger("12345"), ANONYMOUS_CSVCONTEXT), is("12,345"));
            
        }
    
    }
    
    /**
     * Tests for primitive int.
     *
     */
    public static class PrivimiveIntTest {
        
        @Rule
        public TestName name = new TestName();
        
        private IntegerCellProcessorBuilder builder;
        
        /**
         * Sets up the processor for the test using Combinations
         */
        @Before
        public void setUp() {
            builder = new IntegerCellProcessorBuilder();
        }
        
        @CsvBean
        private static class TestCsv {
            
            @CsvColumn(position=0)
            int int_default;
            
            @CsvColumn(position=1, optional=true)
            int int_optional;
            
            @CsvColumn(position=2, trim=true)
            int int_trim;
            
            @CsvColumn(position=3, inputDefaultValue="12345", outputDefaultValue="-67890")
            int int_defaultValue;
            
            @CsvColumn(position=4, inputDefaultValue="12,345", outputDefaultValue="-67,890")
            @CsvNumberConverter(pattern="#,###")
            int int_defaultValue_format;
            
            @CsvColumn(position=4, inputDefaultValue="abc12,345")
            @CsvNumberConverter(pattern="#,###")
            int int_defaultValue_format_invalid;
            
            @CsvColumn(position=5, equalsValue="123")
            int int_equalsValue;
            
            @CsvColumn(position=6, equalsValue="12,345")
            @CsvNumberConverter(pattern="#,###")
            int int_equalsValue_format;
            
            @CsvColumn(position=7, unique=true)
            int int_unique;
            
            @CsvColumn(position=8, unique=true)
            @CsvNumberConverter(pattern="#,###")
            int int_unique_format;
            
            @CsvColumn(position=9, optional=true, trim=true, inputDefaultValue="123", outputDefaultValue="-678", equalsValue="12345", unique=true)
            int int_combine1;
            
            @CsvColumn(position=10, optional=true, trim=true, inputDefaultValue="123", outputDefaultValue="-678", equalsValue="12345", unique=true)
            @CsvNumberConverter(pattern="#,###")
            int int_combine_format1;
            
            @CsvColumn(position=11)
            @CsvNumberConverter(min="5")
            int int_min;
            
            @CsvColumn(position=12)
            @CsvNumberConverter(min="-12,345", pattern="#,###")
            int int_min_format;
            
            @CsvColumn(position=13)
            @CsvNumberConverter(max="10")
            int int_max;
            
            @CsvColumn(position=14)
            @CsvNumberConverter(max="5,678", pattern="#,###")
            int int_max_format;
            
            @CsvColumn(position=15)
            @CsvNumberConverter(min="5", max="10")
            int int_range;
            
            @CsvColumn(position=16)
            @CsvNumberConverter(min="-12,345", max="5,678", pattern="#,###")
            int int_range_format;
            
            @CsvColumn(position=17)
            @CsvNumberConverter(pattern="#,##0.0##", lenient=true)
            int int_format_lenient;
            
            @CsvColumn(position=18)
            @CsvNumberConverter(pattern="\u00A4 #,##0.0000", currency="USD")
            int int_format_currency;
            
            @CsvColumn(position=19)
            @CsvNumberConverter(pattern="#,##0", roundingMode=RoundingMode.HALF_UP)
            int int_format_roundingMode;
        }
        
        /**
         * Tests with default. (not grant convert annotation.)
         */
        @Test
        public void testBuildInput_default() {
            
            Annotation[] annos = getAnnotations(TestCsv.class, "int_default");
            CellProcessor cellProcessor = builder.buildInputCellProcessor(int.class, annos);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            assertThat(cellProcessor, hasCellProcessor(NotNull.class));
            assertThat(cellProcessor, hasCellProcessor(ParseInt.class));
            
            assertThat(cellProcessor.execute("123", ANONYMOUS_CSVCONTEXT), is(123));
            
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
                assertThat(errorProcessor, is(instanceOf(ParseInt.class)));
            }
            
        }
        
        /**
         * Tests with default. (not grant convert annotation.)
         */
        @Test
        public void testBuildOutput_default() {
            
            Annotation[] annos = getAnnotations(TestCsv.class, "int_default");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(int.class, annos, false);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            assertThat(cellProcessor, hasCellProcessor(NotNull.class));
            
            assertThat(cellProcessor.execute(123, ANONYMOUS_CSVCONTEXT), is(123));
            
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
            
            Annotation[] annos = getAnnotations(TestCsv.class, "int_default");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(int.class, annos, true);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            assertThat(cellProcessor, hasCellProcessor(NotNull.class));
            
            assertThat(cellProcessor.execute(123, ANONYMOUS_CSVCONTEXT), is(123));
            
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
            
            Annotation[] annos = getAnnotations(TestCsv.class, "int_optional");
            CellProcessor cellProcessor = builder.buildInputCellProcessor(int.class, annos);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, hasCellProcessor(ConvertNullTo.class));
            
            assertThat(cellProcessor.execute("123", ANONYMOUS_CSVCONTEXT), is(123));
            
            // if type is primitive, then convert to zero.
            assertThat(cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT), is(0));
            
        }
        
        /**
         * Tests with optional. (not grant convert annotation.)
         */
        @Test
        public void testBuildOutput_optional() {
            
            Annotation[] annos = getAnnotations(TestCsv.class, "int_optional");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(int.class, annos, false);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            assertThat(cellProcessor, hasCellProcessor(Optional.class));
            
            assertThat(cellProcessor.execute(123, ANONYMOUS_CSVCONTEXT), is(123));
            
            assertThat(cellProcessor.execute(0, ANONYMOUS_CSVCONTEXT), is(0));
            
        }
        
        /**
         * Tests with optional. (not grant convert annotation.)
         */
        @Test
        public void testBuildOutput_optional_ignoreValidation() {
            
            Annotation[] annos = getAnnotations(TestCsv.class, "int_optional");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(int.class, annos, true);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            assertThat(cellProcessor, hasCellProcessor(Optional.class));
            
            assertThat(cellProcessor.execute(123, ANONYMOUS_CSVCONTEXT), is(123));
            
            assertThat(cellProcessor.execute(0, ANONYMOUS_CSVCONTEXT), is(0));
        }
        
        @Test
        public void testBuildInput_trim() {
            Annotation[] annos = getAnnotations(TestCsv.class, "int_trim");
            CellProcessor cellProcessor = builder.buildInputCellProcessor(int.class, annos);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            assertThat(cellProcessor, hasCellProcessor(Trim.class));
            
            assertThat(cellProcessor.execute("  123 ", ANONYMOUS_CSVCONTEXT), is(123));
        }
        
        @Test
        public void testBuildOutput_trim() {
            Annotation[] annos = getAnnotations(TestCsv.class, "int_trim");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(int.class, annos, false);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            assertThat(cellProcessor, hasCellProcessor(Trim.class));
            
            assertThat(cellProcessor.execute(123, ANONYMOUS_CSVCONTEXT), is("123"));
        }
        
        @Test
        public void testBuildOutput_trim_ignoreValidation() {
            Annotation[] annos = getAnnotations(TestCsv.class, "int_trim");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(int.class, annos, true);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            assertThat(cellProcessor, hasCellProcessor(Trim.class));
            
            assertThat(cellProcessor.execute(123, ANONYMOUS_CSVCONTEXT), is("123"));
        }
        
        @Test
        public void testBuildInput_defaultValue() {
            Annotation[] annos = getAnnotations(TestCsv.class, "int_defaultValue");
            CellProcessor cellProcessor = builder.buildInputCellProcessor(int.class, annos);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            assertThat(cellProcessor, hasCellProcessor(ConvertNullTo.class));
            
            assertThat(cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT), is(12345));
        }
        
        @Test
        public void testBuildOutput_defaultValue() {
            Annotation[] annos = getAnnotations(TestCsv.class, "int_defaultValue");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(int.class, annos, false);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            assertThat(cellProcessor, hasCellProcessor(ConvertNullTo.class));
            
            assertThat(cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT), is("-67890"));
        }
        
        @Test
        public void testBuildOutput_defaultValue_ignoreValidation() {
            Annotation[] annos = getAnnotations(TestCsv.class, "int_defaultValue");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(int.class, annos, true);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            assertThat(cellProcessor, hasCellProcessor(ConvertNullTo.class));
            
            assertThat(cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT), is("-67890"));
        }
        
        @Test
        public void testBuildInput_defaultValue_format() {
            Annotation[] annos = getAnnotations(TestCsv.class, "int_defaultValue_format");
            CellProcessor cellProcessor = builder.buildInputCellProcessor(int.class, annos);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            assertThat(cellProcessor, hasCellProcessor(ConvertNullTo.class));
            
            assertThat(cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT), is(12345));
        }
        
        @Test
        public void testBuildOutput_defaultValue_format() {
            Annotation[] annos = getAnnotations(TestCsv.class, "int_defaultValue_format");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(int.class, annos, false);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            assertThat(cellProcessor, hasCellProcessor(ConvertNullTo.class));
            
            assertThat(cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT), is("-67,890"));
        }
        
        @Test
        public void testBuildOutput_defaultValue_format_ignoreValidation() {
            Annotation[] annos = getAnnotations(TestCsv.class, "int_defaultValue_format");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(int.class, annos, true);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            assertThat(cellProcessor, hasCellProcessor(ConvertNullTo.class));
            
            assertThat(cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT), is("-67,890"));
        }
        
        /**
         * Tests with default. (not grant convert annotation.)
         */
        @Test(expected=SuperCsvInvalidAnnotationException.class)
        public void testBuildInput_default_format_invalidAnnotation() {
            
            Annotation[] annos = getAnnotations(TestCsv.class, "int_defaultValue_format_invalid");
            CellProcessor cellProcessor = builder.buildInputCellProcessor(int.class, annos);
            
            cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT);
            fail();
            
        }
        
        @Test
        public void testBuildInput_equalsValue() {
            Annotation[] annos = getAnnotations(TestCsv.class, "int_equalsValue");
            CellProcessor cellProcessor = builder.buildInputCellProcessor(int.class, annos);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            assertThat(cellProcessor, hasCellProcessor(Equals.class));
            
            assertThat(cellProcessor.execute("123", ANONYMOUS_CSVCONTEXT), is(123));
            
            // not quals input
            try {
                cellProcessor.execute("456", ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Equals.class)));
            }
        }
        
        @Test
        public void testBuildOutput_equalsValue() {
            Annotation[] annos = getAnnotations(TestCsv.class, "int_equalsValue");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(int.class, annos, false);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            assertThat(cellProcessor, hasCellProcessor(Equals.class));
            
            assertThat(cellProcessor.execute(123, ANONYMOUS_CSVCONTEXT), is(123));
            
            // not quals input
            try {
                cellProcessor.execute(456, ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Equals.class)));
            }
        }
        
        @Test
        public void testBuildOutput_equalsValue_ignoreValidation() {
            Annotation[] annos = getAnnotations(TestCsv.class, "int_equalsValue");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(int.class, annos, true);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            assertThat(cellProcessor, not(hasCellProcessor(Equals.class)));
            
            assertThat(cellProcessor.execute(123, ANONYMOUS_CSVCONTEXT), is(123));
            
            // not quals input
            assertThat(cellProcessor.execute(456, ANONYMOUS_CSVCONTEXT), is(456));
            
        }
        
        @Test
        public void testBuildInput_equalsValue_format() {
            Annotation[] annos = getAnnotations(TestCsv.class, "int_equalsValue_format");
            CellProcessor cellProcessor = builder.buildInputCellProcessor(int.class, annos);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            assertThat(cellProcessor, hasCellProcessor(Equals.class));
            
            assertThat(cellProcessor.execute("12,345", ANONYMOUS_CSVCONTEXT), is(12345));
            
            // not quals input
            try {
                cellProcessor.execute("456", ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Equals.class)));
            }
        }
        
        @Test
        public void testBuildOutput_equalsValue_format() {
            Annotation[] annos = getAnnotations(TestCsv.class, "int_equalsValue_format");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(int.class, annos, false);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            assertThat(cellProcessor, hasCellProcessor(Equals.class));
            
            assertThat(cellProcessor.execute(12345, ANONYMOUS_CSVCONTEXT), is("12,345"));
            
            // not quals input
            try {
                cellProcessor.execute(456, ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Equals.class)));
            }
        }
        
        @Test
        public void testBuildOutput_equalsValue_format_ignoreValidation() {
            Annotation[] annos = getAnnotations(TestCsv.class, "int_equalsValue_format");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(int.class, annos, true);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            assertThat(cellProcessor, not(hasCellProcessor(Equals.class)));
            
            assertThat(cellProcessor.execute(12345, ANONYMOUS_CSVCONTEXT), is("12,345"));
            
            // not quals input
            assertThat(cellProcessor.execute(456, ANONYMOUS_CSVCONTEXT), is("456"));
            
        }
        
        @Test
        public void testBuildInput_unique() {
            Annotation[] annos = getAnnotations(TestCsv.class, "int_unique");
            CellProcessor cellProcessor = builder.buildInputCellProcessor(int.class, annos);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            assertThat(cellProcessor, hasCellProcessor(Unique.class));
            
            assertThat(cellProcessor.execute("123", ANONYMOUS_CSVCONTEXT), is(123));
            assertThat(cellProcessor.execute("456", ANONYMOUS_CSVCONTEXT), is(456));
            
            // not unique input
            try {
                cellProcessor.execute("456", ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Unique.class)));
            }
        }
        
        @Test
        public void testBuildOutput_unique() {
            Annotation[] annos = getAnnotations(TestCsv.class, "int_unique");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(int.class, annos, false);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            assertThat(cellProcessor, hasCellProcessor(Unique.class));
            
            assertThat(cellProcessor.execute(123, ANONYMOUS_CSVCONTEXT), is(123));
            assertThat(cellProcessor.execute(456, ANONYMOUS_CSVCONTEXT), is(456));
            
            // not unique input
            try {
                cellProcessor.execute(456, ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Unique.class)));
            }
        }
        
        @Test
        public void testBuildOutput_unique_ignoreValidation() {
            Annotation[] annos = getAnnotations(TestCsv.class, "int_unique");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(int.class, annos, true);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            assertThat(cellProcessor, not(hasCellProcessor(Unique.class)));
            
            assertThat(cellProcessor.execute(123, ANONYMOUS_CSVCONTEXT), is(123));
            assertThat(cellProcessor.execute(123, ANONYMOUS_CSVCONTEXT), is(123));
            
        }
        
        @Test
        public void testBuildInput_unique_format() {
            Annotation[] annos = getAnnotations(TestCsv.class, "int_unique_format");
            CellProcessor cellProcessor = builder.buildInputCellProcessor(int.class, annos);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            assertThat(cellProcessor, hasCellProcessor(Unique.class));
            
            assertThat(cellProcessor.execute("12,345", ANONYMOUS_CSVCONTEXT), is(12345));
            assertThat(cellProcessor.execute("456", ANONYMOUS_CSVCONTEXT), is(456));
            
            // not unique input
            try {
                cellProcessor.execute("456", ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Unique.class)));
            }
        }
        
        @Test
        public void testBuildOutput_unique_format() {
            Annotation[] annos = getAnnotations(TestCsv.class, "int_unique_format");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(int.class, annos, false);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            assertThat(cellProcessor, hasCellProcessor(Unique.class));
            
            assertThat(cellProcessor.execute(12345, ANONYMOUS_CSVCONTEXT), is("12,345"));
            assertThat(cellProcessor.execute(456, ANONYMOUS_CSVCONTEXT), is("456"));
            
            // not unique input
            try {
                cellProcessor.execute(456, ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Unique.class)));
            }
        }
        
        @Test
        public void testBuildOutput_unique_format_ignoreValidation() {
            Annotation[] annos = getAnnotations(TestCsv.class, "int_unique_format");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(int.class, annos, true);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            assertThat(cellProcessor, not(hasCellProcessor(Unique.class)));
            
            assertThat(cellProcessor.execute(12345, ANONYMOUS_CSVCONTEXT), is("12,345"));
            assertThat(cellProcessor.execute(12345, ANONYMOUS_CSVCONTEXT), is("12,345"));
            
        }
        
        @Test
        public void testBuildInput_combine1() {
            Annotation[] annos = getAnnotations(TestCsv.class, "int_combine1");
            CellProcessor cellProcessor = builder.buildInputCellProcessor(int.class, annos);
            
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT), is(123));
            assertThat(cellProcessor.execute("  12345  ", ANONYMOUS_CSVCONTEXT), is(12345));
            
            // not equals input
            try {
                cellProcessor.execute("678", ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Equals.class)));
            }
            
            // not unique input
            try {
                cellProcessor.execute("12345", ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Unique.class)));
            }
        }
        
        @Test
        public void testBuildOutput_combine1() {
            Annotation[] annos = getAnnotations(TestCsv.class, "int_combine1");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(int.class, annos, false);
            
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT), is("-678"));
            assertThat(cellProcessor.execute(12345, ANONYMOUS_CSVCONTEXT), is("12345"));
            
            // not equals input
            try {
                cellProcessor.execute("678", ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Equals.class)));
            }
            
            // not unique input
            try {
                cellProcessor.execute(12345, ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Unique.class)));
            }
        }
        
        @Test
        public void testBuildOutput_combine1_ignoreValidation() {
            Annotation[] annos = getAnnotations(TestCsv.class, "int_combine1");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(int.class, annos, true);
            
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT), is("-678"));
            assertThat(cellProcessor.execute(12345, ANONYMOUS_CSVCONTEXT), is("12345"));
            
            // not equals input
            assertThat(cellProcessor.execute(678, ANONYMOUS_CSVCONTEXT), is("678"));
            
            // not unique input
            assertThat(cellProcessor.execute(12345, ANONYMOUS_CSVCONTEXT), is("12345"));
            
        }
        
        @Test
        public void testBuildInput_combine_format1() {
            Annotation[] annos = getAnnotations(TestCsv.class, "int_combine_format1");
            CellProcessor cellProcessor = builder.buildInputCellProcessor(int.class, annos);
            
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT), is(123));
            assertThat(cellProcessor.execute("  12345  ", ANONYMOUS_CSVCONTEXT), is(12345));
            
            // not equals input
            try {
                cellProcessor.execute("678", ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Equals.class)));
            }
            
            // not unique input
            try {
                cellProcessor.execute("12345", ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Unique.class)));
            }
        }
        
        @Test
        public void testBuildOutput_combine_format1() {
            Annotation[] annos = getAnnotations(TestCsv.class, "int_combine_format1");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(int.class, annos, false);
            
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT), is("-678"));
            assertThat(cellProcessor.execute(12345, ANONYMOUS_CSVCONTEXT), is("12,345"));
            
            // not equals input
            try {
                cellProcessor.execute("678", ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Equals.class)));
            }
            
            // not unique input
            try {
                cellProcessor.execute(12345, ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Unique.class)));
            }
        }
        
        @Test
        public void testBuildOutput_combine_format1_ignoreValidation() {
            Annotation[] annos = getAnnotations(TestCsv.class, "int_combine_format1");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(int.class, annos, true);
            
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT), is("-678"));
            assertThat(cellProcessor.execute(12345, ANONYMOUS_CSVCONTEXT), is("12,345"));
            
            // not equals input
            assertThat(cellProcessor.execute(678, ANONYMOUS_CSVCONTEXT), is("678"));
            
            // not unique input
            assertThat(cellProcessor.execute(12345, ANONYMOUS_CSVCONTEXT), is("12,345"));
            
        }
        
        @Test
        public void testBuildInput_min() {
            Annotation[] annos = getAnnotations(TestCsv.class, "int_min");
            CellProcessor cellProcessor = builder.buildInputCellProcessor(int.class, annos);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            assertThat(cellProcessor, hasCellProcessor(Min.class));
            
            assertThat(cellProcessor.execute("5", ANONYMOUS_CSVCONTEXT), is(5));
            assertThat(cellProcessor.execute("6", ANONYMOUS_CSVCONTEXT), is(6));
            
            // value less than min(5)
            try {
                cellProcessor.execute("4", ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Min.class)));
            }
            
        }
        
        @Test
        public void testBuildOutput_min() {
            Annotation[] annos = getAnnotations(TestCsv.class, "int_min");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(int.class, annos, false);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            assertThat(cellProcessor, hasCellProcessor(Min.class));
            
            assertThat(cellProcessor.execute(5, ANONYMOUS_CSVCONTEXT), is(5));
            assertThat(cellProcessor.execute(6, ANONYMOUS_CSVCONTEXT), is(6));
            
            // value less than min(5)
            try {
                cellProcessor.execute(4, ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Min.class)));
            }
            
        }
        
        @Test
        public void testBuildOutput_min_ignoreValidation() {
            Annotation[] annos = getAnnotations(TestCsv.class, "int_min");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(int.class, annos, true);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            assertThat(cellProcessor, not(hasCellProcessor(Min.class)));
            
            // value less than min(5)
            assertThat(cellProcessor.execute(4, ANONYMOUS_CSVCONTEXT), is(4));
            
        }
        
        @Test
        public void testBuildInput_min_format() {
            Annotation[] annos = getAnnotations(TestCsv.class, "int_min_format");
            CellProcessor cellProcessor = builder.buildInputCellProcessor(int.class, annos);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            assertThat(cellProcessor, hasCellProcessor(Min.class));
            
            assertThat(cellProcessor.execute("-12,345", ANONYMOUS_CSVCONTEXT), is(-12345));
            assertThat(cellProcessor.execute("-12,344", ANONYMOUS_CSVCONTEXT), is(-12344));
            
            // value less than min(-12345)
            try {
                cellProcessor.execute("-12346", ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Min.class)));
            }
            
        }
        
        @Test
        public void testBuildOutput_min_format() {
            Annotation[] annos = getAnnotations(TestCsv.class, "int_min_format");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(int.class, annos, false);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            assertThat(cellProcessor, hasCellProcessor(Min.class));
            
            assertThat(cellProcessor.execute(-12345, ANONYMOUS_CSVCONTEXT), is("-12,345"));
            assertThat(cellProcessor.execute(-12344, ANONYMOUS_CSVCONTEXT), is("-12,344"));
            
            // value less than min(-12345)
            try {
                cellProcessor.execute(-12346, ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Min.class)));
            }
            
        }
        
        @Test
        public void testBuildOutput_min_format_ignoreValidation() {
            Annotation[] annos = getAnnotations(TestCsv.class, "int_min_format");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(int.class, annos, true);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            assertThat(cellProcessor, not(hasCellProcessor(Min.class)));
            
            // value less than min(-12345)
            assertThat(cellProcessor.execute(-12346, ANONYMOUS_CSVCONTEXT), is("-12,346"));
            
        }
        
        @Test
        public void testBuildInput_max() {
            Annotation[] annos = getAnnotations(TestCsv.class, "int_max");
            CellProcessor cellProcessor = builder.buildInputCellProcessor(int.class, annos);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            assertThat(cellProcessor, hasCellProcessor(Max.class));
            
            assertThat(cellProcessor.execute("10", ANONYMOUS_CSVCONTEXT), is(10));
            assertThat(cellProcessor.execute("9", ANONYMOUS_CSVCONTEXT), is(9));
            
            // value greater than max(10)
            try {
                cellProcessor.execute("11", ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Max.class)));
            }
            
        }
        
        @Test
        public void testBuildOutput_max() {
            Annotation[] annos = getAnnotations(TestCsv.class, "int_max");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(int.class, annos, false);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            assertThat(cellProcessor, hasCellProcessor(Max.class));
            
            assertThat(cellProcessor.execute(10, ANONYMOUS_CSVCONTEXT), is(10));
            assertThat(cellProcessor.execute(9, ANONYMOUS_CSVCONTEXT), is(9));
            
            // value greater than max(10)
            try {
                cellProcessor.execute(11, ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Max.class)));
            }
            
        }
        
        @Test
        public void testBuildOutput_max_ignoreValidation() {
            Annotation[] annos = getAnnotations(TestCsv.class, "int_max");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(int.class, annos, true);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            assertThat(cellProcessor, not(hasCellProcessor(Max.class)));
            
            // value greater than max(10)
            assertThat(cellProcessor.execute(11, ANONYMOUS_CSVCONTEXT), is(11));
            
        }
        
        @Test
        public void testBuildInput_max_format() {
            Annotation[] annos = getAnnotations(TestCsv.class, "int_max_format");
            CellProcessor cellProcessor = builder.buildInputCellProcessor(int.class, annos);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            assertThat(cellProcessor, hasCellProcessor(Max.class));
            
            assertThat(cellProcessor.execute("5,678", ANONYMOUS_CSVCONTEXT), is(5678));
            assertThat(cellProcessor.execute("5,677", ANONYMOUS_CSVCONTEXT), is(5677));
            
            // value greater than max(5679)
            try {
                cellProcessor.execute("5,679", ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Max.class)));
            }
            
        }
        
        @Test
        public void testBuildOutput_max_format() {
            Annotation[] annos = getAnnotations(TestCsv.class, "int_max_format");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(int.class, annos, false);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            assertThat(cellProcessor, hasCellProcessor(Max.class));
            
            assertThat(cellProcessor.execute(5678, ANONYMOUS_CSVCONTEXT), is("5,678"));
            assertThat(cellProcessor.execute(5677, ANONYMOUS_CSVCONTEXT), is("5,677"));
            
            // value greater than max(5679)
            try {
                cellProcessor.execute(5679, ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Max.class)));
            }
            
        }
        
        @Test
        public void testBuildOutput_max_format_ignoreValidation() {
            Annotation[] annos = getAnnotations(TestCsv.class, "int_max_format");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(int.class, annos, true);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            assertThat(cellProcessor, not(hasCellProcessor(Max.class)));
            
            // value greater than max(5679)
            assertThat(cellProcessor.execute(5679, ANONYMOUS_CSVCONTEXT), is("5,679"));
            
        }
        
        @Test
        public void testBuildInput_range() {
            Annotation[] annos = getAnnotations(TestCsv.class, "int_range");
            CellProcessor cellProcessor = builder.buildInputCellProcessor(int.class, annos);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            assertThat(cellProcessor, hasCellProcessor(Range.class));
            
            assertThat(cellProcessor.execute("5", ANONYMOUS_CSVCONTEXT), is(5));
            assertThat(cellProcessor.execute("6", ANONYMOUS_CSVCONTEXT), is(6));
            
            assertThat(cellProcessor.execute("10", ANONYMOUS_CSVCONTEXT), is(10));
            assertThat(cellProcessor.execute("9", ANONYMOUS_CSVCONTEXT), is(9));
            
            // value less than range(5, 10)
            try {
                cellProcessor.execute("4", ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Range.class)));
            }
            
            // value greater than range(5, 10)
            try {
                cellProcessor.execute("11", ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Range.class)));
            }
            
        }
        
        @Test
        public void testBuildOutput_range() {
            Annotation[] annos = getAnnotations(TestCsv.class, "int_range");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(int.class, annos, false);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            assertThat(cellProcessor, hasCellProcessor(Range.class));
            
            assertThat(cellProcessor.execute(5, ANONYMOUS_CSVCONTEXT), is(5));
            assertThat(cellProcessor.execute(6, ANONYMOUS_CSVCONTEXT), is(6));
            
            assertThat(cellProcessor.execute(10, ANONYMOUS_CSVCONTEXT), is(10));
            assertThat(cellProcessor.execute(9, ANONYMOUS_CSVCONTEXT), is(9));
            
            // value less than range(5,10)
            try {
                cellProcessor.execute(4, ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Range.class)));
            }
            
            try {
                cellProcessor.execute(11, ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Range.class)));
            }
            
        }
        
        @Test
        public void testBuildOutput_range_ignoreValidation() {
            Annotation[] annos = getAnnotations(TestCsv.class, "int_range");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(int.class, annos, true);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            assertThat(cellProcessor, not(hasCellProcessor(Range.class)));
            
            // value less than range(5,10)
            assertThat(cellProcessor.execute(4, ANONYMOUS_CSVCONTEXT), is(4));
            
            assertThat(cellProcessor.execute(11, ANONYMOUS_CSVCONTEXT), is(11));
        }
        
        @Test
        public void testBuildInput_range_format() {
            Annotation[] annos = getAnnotations(TestCsv.class, "int_range_format");
            CellProcessor cellProcessor = builder.buildInputCellProcessor(int.class, annos);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            assertThat(cellProcessor, hasCellProcessor(Range.class));
            
            assertThat(cellProcessor.execute("-12,345", ANONYMOUS_CSVCONTEXT), is(-12345));
            assertThat(cellProcessor.execute("-12,344", ANONYMOUS_CSVCONTEXT), is(-12344));
            
            assertThat(cellProcessor.execute("5,678", ANONYMOUS_CSVCONTEXT), is(5678));
            assertThat(cellProcessor.execute("5,677", ANONYMOUS_CSVCONTEXT), is(5677));
            
            // value less than range(-12345,5678)
            try {
                cellProcessor.execute("-12346", ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Range.class)));
            }
            
            // value greater than max(-12345,5678)
            try {
                cellProcessor.execute("5,679", ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Range.class)));
            }
            
        }
        
        @Test
        public void testBuildOutput_range_format() {
            Annotation[] annos = getAnnotations(TestCsv.class, "int_range_format");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(int.class, annos, false);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            assertThat(cellProcessor, hasCellProcessor(Range.class));
            
            assertThat(cellProcessor.execute(-12345, ANONYMOUS_CSVCONTEXT), is("-12,345"));
            assertThat(cellProcessor.execute(-12344, ANONYMOUS_CSVCONTEXT), is("-12,344"));
            
            assertThat(cellProcessor.execute(5678, ANONYMOUS_CSVCONTEXT), is("5,678"));
            assertThat(cellProcessor.execute(5677, ANONYMOUS_CSVCONTEXT), is("5,677"));
            
            // value less than range(-12345,5678)
            try {
                cellProcessor.execute(-12346, ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Range.class)));
            }
            
            // value greater than range(-12345,5678)
            try {
                cellProcessor.execute(5679, ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Range.class)));
            }
            
        }
        
        @Test
        public void testBuildOutput_range_format_ignoreValidation() {
            Annotation[] annos = getAnnotations(TestCsv.class, "int_range_format");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(int.class, annos, true);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, not(hasCellProcessor(Range.class)));
            
            // value less than range(-12345,5678)
            assertThat(cellProcessor.execute(-12346, ANONYMOUS_CSVCONTEXT), is("-12,346"));
            
            // value greater than max(-12345,5678)
            assertThat(cellProcessor.execute(5679, ANONYMOUS_CSVCONTEXT), is("5,679"));
            
        }
        
        @Test
        public void testBuildInput_format_lenient() {
            Annotation[] annos = getAnnotations(TestCsv.class, "int_format_lenient");
            CellProcessor cellProcessor = builder.buildInputCellProcessor(int.class, annos);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, hasCellProcessor(ParseLocaleNumber.class));
            
            assertThat(cellProcessor.execute("12,345.567", ANONYMOUS_CSVCONTEXT), is(12345));
            assertThat(cellProcessor.execute("12,345", ANONYMOUS_CSVCONTEXT), is(12345));
            
        }
        
        @Test
        public void testBuildOutput_format_lenient() {
            Annotation[] annos = getAnnotations(TestCsv.class, "int_format_lenient");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(int.class, annos, true);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, hasCellProcessor(FormatLocaleNumber.class));
            
            assertThat(cellProcessor.execute(12345, ANONYMOUS_CSVCONTEXT), is("12,345.0"));
            
        }
        
        @Test
        public void testBuildInput_format_currency() {
            Annotation[] annos = getAnnotations(TestCsv.class, "int_format_currency");
            CellProcessor cellProcessor = builder.buildInputCellProcessor(int.class, annos);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, hasCellProcessor(ParseLocaleNumber.class));
            
            assertThat(cellProcessor.execute("USD 12,345.0000", ANONYMOUS_CSVCONTEXT), is(12345));
            
        }
        
        @Test
        public void testBuildOutput_format_currency() {
            Annotation[] annos = getAnnotations(TestCsv.class, "int_format_currency");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(int.class, annos, true);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, hasCellProcessor(FormatLocaleNumber.class));
            
            assertThat(cellProcessor.execute(12345, ANONYMOUS_CSVCONTEXT), is("USD 12,345.0000"));
            
        }
        
        @Test
        public void testBuildInput_format_roundingMode() {
            Annotation[] annos = getAnnotations(TestCsv.class, "int_format_roundingMode");
            CellProcessor cellProcessor = builder.buildInputCellProcessor(int.class, annos);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, hasCellProcessor(ParseLocaleNumber.class));
            
            assertThat(cellProcessor.execute("12,345", ANONYMOUS_CSVCONTEXT), is(12345));
            
        }
        
        @Test
        public void testBuildOutput_format_roundingMode() {
            Annotation[] annos = getAnnotations(TestCsv.class, "int_format_roundingMode");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(int.class, annos, true);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, hasCellProcessor(FormatLocaleNumber.class));
            
            assertThat(cellProcessor.execute(12345, ANONYMOUS_CSVCONTEXT), is("12,345"));
            
        }
    }
    
}

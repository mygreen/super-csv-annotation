package org.supercsv.ext.builder.joda;

import static org.junit.Assert.*;
import static org.supercsv.ext.tool.TestUtils.*;
import static org.hamcrest.Matchers.*;
import static org.supercsv.ext.tool.HasCellProcessor.*;

import java.lang.annotation.Annotation;

import org.joda.time.LocalTime;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.supercsv.cellprocessor.ConvertNullTo;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.constraint.Equals;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.constraint.Unique;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.joda.FmtLocalTime;
import org.supercsv.cellprocessor.joda.ParseLocalTime;
import org.supercsv.exception.SuperCsvCellProcessorException;
import org.supercsv.exception.SuperCsvConstraintViolationException;
import org.supercsv.ext.annotation.CsvBean;
import org.supercsv.ext.annotation.CsvColumn;
import org.supercsv.ext.annotation.CsvDateConverter;
import org.supercsv.ext.cellprocessor.Trim;
import org.supercsv.ext.cellprocessor.joda.FutureJoda;
import org.supercsv.ext.cellprocessor.joda.JodaRange;
import org.supercsv.ext.cellprocessor.joda.PastJoda;
import org.supercsv.ext.exception.SuperCsvInvalidAnnotationException;

/**
 * Test the {@link LocalTimeCellProcessorBuilder} CellProcessor.
 * 
 * @since 1.2
 * @author T.TSUCHIE
 *
 */
public class LocalTimeCellProcessorBuilderTest {
    
    @Rule
    public TestName name = new TestName();
    
    private LocalTimeCellProcessorBuilder builder;
    
    /**
     * Sets up the processor for the test using Combinations
     */
    @Before
    public void setUp() {
        builder = new LocalTimeCellProcessorBuilder();
    }
    
    private static final String TEST_NORMAL_PATTERN = "HH:mm:ss";
    private static final String TEST_FORMATTED_PATTERN = "H:m:s";
    
    private static final LocalTime TEST_VALUE_1_OBJ = new LocalTime(7, 12, 1);
    private static final String TEST_VALUE_1_STR_NORMAL = "07:12:01";
    private static final String TEST_VALUE_1_STR_FORMATTED = "7:12:1";
    
    private static final LocalTime TEST_VALUE_2_OBJ = new LocalTime(10, 8, 35);
    private static final String TEST_VALUE_2_STR_NORMAL = "10:08:35";
    private static final String TEST_VALUE_2_STR_FORMATTED = "10:8:35";
    
    private static final LocalTime TEST_VALUE_INPUT_DEFAULT_OBJ = new LocalTime(8, 2, 3);
    private static final String TEST_VALUE_INPUT_DEFAULT_STR_NORMAL = "08:02:03";
    private static final String TEST_VALUE_INPUT_DEFAULT_STR_FORMATTED = "8:2:3";
    
    private static final LocalTime TEST_VALUE_OUTPUT_DEFAULT_OBJ = new LocalTime(12, 31, 1);
    private static final String TEST_VALUE_OUTPUT_DEFAULT_STR_NORMAL = "12:31:01";
    private static final String TEST_VALUE_OUTPUT_DEFAULT_STR_FORMATTED = "12:31:1";
    
    private static final LocalTime TEST_VALUE_MIN_OBJ = new LocalTime(1, 0, 0);
    private static final String TEST_VALUE_MIN_STR_NORMAL = "01:00:00";
    private static final String TEST_VALUE_MIN_STR_FORMATTED = "1:0:0";
    
    private static final LocalTime TEST_VALUE_MAX_OBJ = new LocalTime(4, 59, 59);
    private static final String TEST_VALUE_MAX_STR_NORMAL = "04:59:59";
    private static final String TEST_VALUE_MAX_STR_FORMATTED = "4:59:59";
    
    @CsvBean
    private static class TestCsv {
        
        @CsvColumn(position=0)
        LocalTime localdatetime_default;
        
        @CsvColumn(position=1, optional=true)
        LocalTime localdatetime_optional;
        
        @CsvColumn(position=2, trim=true)
        LocalTime localdatetime_trim;
        
        @CsvColumn(position=3, inputDefaultValue=TEST_VALUE_INPUT_DEFAULT_STR_NORMAL, outputDefaultValue=TEST_VALUE_OUTPUT_DEFAULT_STR_NORMAL)
        LocalTime localdatetime_defaultValue;
        
        @CsvColumn(position=4, inputDefaultValue=TEST_VALUE_INPUT_DEFAULT_STR_FORMATTED, outputDefaultValue=TEST_VALUE_OUTPUT_DEFAULT_STR_FORMATTED)
        @CsvDateConverter(pattern=TEST_FORMATTED_PATTERN)
        LocalTime localdatetime_defaultValue_format;
        
        @CsvColumn(position=5, inputDefaultValue="2000-01-01 08:02:03")
        @CsvDateConverter(pattern=TEST_FORMATTED_PATTERN)
        LocalTime localdatetime_defaultValue_format_invalid;
        
        @CsvColumn(position=6, equalsValue=TEST_VALUE_1_STR_NORMAL)
        LocalTime localdatetime_equalsValue;
        
        @CsvColumn(position=7, equalsValue=TEST_VALUE_1_STR_FORMATTED)
        @CsvDateConverter(pattern=TEST_FORMATTED_PATTERN)
        LocalTime localdatetime_equalsValue_format;
        
        @CsvColumn(position=8, unique=true)
        LocalTime localdatetime_unique;
        
        @CsvColumn(position=9, unique=true)
        @CsvDateConverter(pattern=TEST_FORMATTED_PATTERN)
        LocalTime localdatetime_unique_format;
        
        @CsvColumn(position=10, optional=true, trim=true,
                inputDefaultValue=TEST_VALUE_INPUT_DEFAULT_STR_NORMAL, outputDefaultValue=TEST_VALUE_OUTPUT_DEFAULT_STR_NORMAL,
                equalsValue=TEST_VALUE_1_STR_NORMAL, unique=true)
        LocalTime localdatetime_combine1;
        
        @CsvColumn(position=11, optional=true, trim=true,
                inputDefaultValue=TEST_VALUE_INPUT_DEFAULT_STR_FORMATTED, outputDefaultValue=TEST_VALUE_OUTPUT_DEFAULT_STR_FORMATTED,
                equalsValue=TEST_VALUE_1_STR_FORMATTED, unique=true)
        @CsvDateConverter(pattern=TEST_FORMATTED_PATTERN)
        LocalTime localdatetime_combine_format1;
        
        @CsvColumn(position=12)
        @CsvDateConverter(min=TEST_VALUE_MIN_STR_NORMAL)
        LocalTime localdatetime_min;
        
        @CsvColumn(position=13)
        @CsvDateConverter(min=TEST_VALUE_MIN_STR_FORMATTED, pattern=TEST_FORMATTED_PATTERN)
        LocalTime localdatetime_min_format;
        
        @CsvColumn(position=14)
        @CsvDateConverter(max=TEST_VALUE_MAX_STR_NORMAL)
        LocalTime localdatetime_max;
        
        @CsvColumn(position=15)
        @CsvDateConverter(max=TEST_VALUE_MAX_STR_FORMATTED, pattern=TEST_FORMATTED_PATTERN)
        LocalTime localdatetime_max_format;
        
        @CsvColumn(position=16)
        @CsvDateConverter(min=TEST_VALUE_MIN_STR_NORMAL, max=TEST_VALUE_MAX_STR_NORMAL)
        LocalTime localdatetime_range;
        
        @CsvColumn(position=17)
        @CsvDateConverter(min=TEST_VALUE_MIN_STR_FORMATTED, max=TEST_VALUE_MAX_STR_FORMATTED, pattern=TEST_FORMATTED_PATTERN)
        LocalTime localdatetime_range_format;
        
        @CsvColumn(position=18)
        @CsvDateConverter(lenient=false)
        LocalTime localdatetime_lenient;
        
        @CsvColumn(position=19)
        @CsvDateConverter(pattern="GGGGyy年M月d日 H時m分s秒", locale="ja_JP_JP")
        LocalTime localdatetime_locale;
        
        @CsvColumn(position=20)
        @CsvDateConverter(timezone="GMT")
        LocalTime localdatetime_timezone;
    }
    
    /**
     * Tests with default. (not grant convert annotation.)
     */
    @Test
    public void testBuildInput_default() {
        
        Annotation[] annos = getAnnotations(TestCsv.class, "localdatetime_default");
        CellProcessor cellProcessor = builder.buildInputCellProcessor(LocalTime.class, annos);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        
        assertThat(cellProcessor, hasCellProcessor(NotNull.class));
        assertThat(cellProcessor, hasCellProcessor(ParseLocalTime.class));
        
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
            assertThat(errorProcessor, is(instanceOf(ParseLocalTime.class)));
        }
        
    }
    
    /**
     * Tests with default. (not grant convert annotation.)
     */
    @Test
    public void testBuildOutput_default() {
        
        Annotation[] annos = getAnnotations(TestCsv.class, "localdatetime_default");
        CellProcessor cellProcessor = builder.buildOutputCellProcessor(LocalTime.class, annos, false);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        
        assertThat(cellProcessor, hasCellProcessor(NotNull.class));
        
        assertThat(cellProcessor.execute(TEST_VALUE_1_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_1_STR_NORMAL));
        
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
        
        Annotation[] annos = getAnnotations(TestCsv.class, "localdatetime_default");
        CellProcessor cellProcessor = builder.buildOutputCellProcessor(LocalTime.class, annos, true);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        
        assertThat(cellProcessor, hasCellProcessor(NotNull.class));
        
        assertThat(cellProcessor.execute(TEST_VALUE_1_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_1_STR_NORMAL));
        
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
        
        Annotation[] annos = getAnnotations(TestCsv.class, "localdatetime_optional");
        CellProcessor cellProcessor = builder.buildInputCellProcessor(LocalTime.class, annos);
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
        
        Annotation[] annos = getAnnotations(TestCsv.class, "localdatetime_optional");
        CellProcessor cellProcessor = builder.buildOutputCellProcessor(LocalTime.class, annos, false);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        
        assertThat(cellProcessor, hasCellProcessor(Optional.class));
        
        assertThat(cellProcessor.execute(TEST_VALUE_1_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_1_STR_NORMAL));
        
        // null input
        assertThat(cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT), is(nullValue()));
    }
    
    /**
     * Tests with optional. (not grant convert annotation.)
     */
    @Test
    public void testBuildOutput_optional_ignoreValidation() {
        
        Annotation[] annos = getAnnotations(TestCsv.class, "localdatetime_optional");
        CellProcessor cellProcessor = builder.buildOutputCellProcessor(LocalTime.class, annos, true);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        
        assertThat(cellProcessor, hasCellProcessor(Optional.class));
        
        assertThat(cellProcessor.execute(TEST_VALUE_1_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_1_STR_NORMAL));
        
        // null input
        assertThat(cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT), is(nullValue()));
    }
    
    @Test
    public void testBuildInput_trim() {
        Annotation[] annos = getAnnotations(TestCsv.class, "localdatetime_trim");
        CellProcessor cellProcessor = builder.buildInputCellProcessor(LocalTime.class, annos);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        
        assertThat(cellProcessor, hasCellProcessor(Trim.class));
        
        assertThat(cellProcessor.execute("  " + TEST_VALUE_1_STR_NORMAL + " ", ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_1_OBJ));
    }
    
    @Test
    public void testBuildOutput_trim() {
        Annotation[] annos = getAnnotations(TestCsv.class, "localdatetime_trim");
        CellProcessor cellProcessor = builder.buildOutputCellProcessor(LocalTime.class, annos, false);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        
        assertThat(cellProcessor, hasCellProcessor(Trim.class));
        
        assertThat(cellProcessor.execute(TEST_VALUE_1_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_1_STR_NORMAL));
    }
    
    @Test
    public void testBuildOutput_trim_ignoreValidation() {
        Annotation[] annos = getAnnotations(TestCsv.class, "localdatetime_trim");
        CellProcessor cellProcessor = builder.buildOutputCellProcessor(LocalTime.class, annos, true);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        
        assertThat(cellProcessor, hasCellProcessor(Trim.class));
        
        assertThat(cellProcessor.execute(TEST_VALUE_1_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_1_STR_NORMAL));
    }
    
    @Test
    public void testBuildInput_defaultValue() {
        Annotation[] annos = getAnnotations(TestCsv.class, "localdatetime_defaultValue");
        CellProcessor cellProcessor = builder.buildInputCellProcessor(LocalTime.class, annos);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        
        assertThat(cellProcessor, hasCellProcessor(ConvertNullTo.class));
        
        assertThat(cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_INPUT_DEFAULT_OBJ));
    }
    
    @Test
    public void testBuildOutput_defaultValue() {
        Annotation[] annos = getAnnotations(TestCsv.class, "localdatetime_defaultValue");
        CellProcessor cellProcessor = builder.buildOutputCellProcessor(LocalTime.class, annos, false);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        
        assertThat(cellProcessor, hasCellProcessor(ConvertNullTo.class));
        
        assertThat(cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_OUTPUT_DEFAULT_STR_NORMAL));
    }
    
    @Test
    public void testBuildOutput_defaultValue_ignoreValidation() {
        Annotation[] annos = getAnnotations(TestCsv.class, "localdatetime_defaultValue");
        CellProcessor cellProcessor = builder.buildOutputCellProcessor(LocalTime.class, annos, true);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        
        assertThat(cellProcessor, hasCellProcessor(ConvertNullTo.class));
        
        assertThat(cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_OUTPUT_DEFAULT_STR_NORMAL));
    }
    
    @Test
    public void testBuildInput_defaultValue_format() {
        Annotation[] annos = getAnnotations(TestCsv.class, "localdatetime_defaultValue_format");
        CellProcessor cellProcessor = builder.buildInputCellProcessor(LocalTime.class, annos);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        
        assertThat(cellProcessor, hasCellProcessor(ConvertNullTo.class));
        
        assertThat(cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_INPUT_DEFAULT_OBJ));
    }
    
    @Test
    public void testBuildOutput_defaultValue_format() {
        Annotation[] annos = getAnnotations(TestCsv.class, "localdatetime_defaultValue_format");
        CellProcessor cellProcessor = builder.buildOutputCellProcessor(LocalTime.class, annos, false);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        
        assertThat(cellProcessor, hasCellProcessor(ConvertNullTo.class));
        
        assertThat(cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_OUTPUT_DEFAULT_STR_FORMATTED));
    }
    
    @Test
    public void testBuildOutput_defaultValue_format_ignoreValidation() {
        Annotation[] annos = getAnnotations(TestCsv.class, "localdatetime_defaultValue_format");
        CellProcessor cellProcessor = builder.buildOutputCellProcessor(LocalTime.class, annos, true);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        
        assertThat(cellProcessor, hasCellProcessor(ConvertNullTo.class));
        
        assertThat(cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_OUTPUT_DEFAULT_STR_FORMATTED));
    }
    
    /**
     * Tests with default. (not grant convert annotation.)
     */
    @Test(expected=SuperCsvInvalidAnnotationException.class)
    public void testBuildInput_default_format_invalidAnnotation() {
        
        Annotation[] annos = getAnnotations(TestCsv.class, "localdatetime_defaultValue_format_invalid");
        CellProcessor cellProcessor = builder.buildInputCellProcessor(LocalTime.class, annos);
        
        cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT);
        fail();
        
    }
    
    @Test
    public void testBuildInput_equalsValue() {
        Annotation[] annos = getAnnotations(TestCsv.class, "localdatetime_equalsValue");
        CellProcessor cellProcessor = builder.buildInputCellProcessor(LocalTime.class, annos);
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
        Annotation[] annos = getAnnotations(TestCsv.class, "localdatetime_equalsValue");
        CellProcessor cellProcessor = builder.buildOutputCellProcessor(LocalTime.class, annos, false);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        assertThat(cellProcessor, hasCellProcessor(Equals.class));
        
        assertThat(cellProcessor.execute(TEST_VALUE_1_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_1_STR_NORMAL));
        
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
        Annotation[] annos = getAnnotations(TestCsv.class, "localdatetime_equalsValue");
        CellProcessor cellProcessor = builder.buildOutputCellProcessor(LocalTime.class, annos, true);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        
        assertThat(cellProcessor, not(hasCellProcessor(Equals.class)));
        
        assertThat(cellProcessor.execute(TEST_VALUE_1_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_1_STR_NORMAL));
        
        // not quals input
        assertThat(cellProcessor.execute(TEST_VALUE_2_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_2_STR_NORMAL));
        
    }
    
    @Test
    public void testBuildInput_equalsValue_format() {
        Annotation[] annos = getAnnotations(TestCsv.class, "localdatetime_equalsValue_format");
        CellProcessor cellProcessor = builder.buildInputCellProcessor(LocalTime.class, annos);
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
        Annotation[] annos = getAnnotations(TestCsv.class, "localdatetime_equalsValue_format");
        CellProcessor cellProcessor = builder.buildOutputCellProcessor(LocalTime.class, annos, false);
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
        Annotation[] annos = getAnnotations(TestCsv.class, "localdatetime_equalsValue_format");
        CellProcessor cellProcessor = builder.buildOutputCellProcessor(LocalTime.class, annos, true);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        
        assertThat(cellProcessor, not(hasCellProcessor(Equals.class)));
        
        assertThat(cellProcessor.execute(TEST_VALUE_1_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_1_STR_FORMATTED));
        
        // not quals input
        assertThat(cellProcessor.execute(TEST_VALUE_2_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_2_STR_FORMATTED));
        
    }
    
    @Test
    public void testBuildInput_unique() {
        Annotation[] annos = getAnnotations(TestCsv.class, "localdatetime_unique");
        CellProcessor cellProcessor = builder.buildInputCellProcessor(LocalTime.class, annos);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        
        assertThat(cellProcessor, hasCellProcessor(Unique.class));
        
        assertThat(cellProcessor.execute(TEST_VALUE_1_STR_NORMAL, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_1_OBJ));
        
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
        Annotation[] annos = getAnnotations(TestCsv.class, "localdatetime_unique");
        CellProcessor cellProcessor = builder.buildOutputCellProcessor(LocalTime.class, annos, false);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        
        assertThat(cellProcessor, hasCellProcessor(Unique.class));
        
        assertThat(cellProcessor.execute(TEST_VALUE_1_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_1_STR_NORMAL));
        
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
        Annotation[] annos = getAnnotations(TestCsv.class, "localdatetime_unique");
        CellProcessor cellProcessor = builder.buildOutputCellProcessor(LocalTime.class, annos, true);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        
        assertThat(cellProcessor, not(hasCellProcessor(Unique.class)));
        
        assertThat(cellProcessor.execute(TEST_VALUE_1_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_1_STR_NORMAL));
        
        // not quals input
        assertThat(cellProcessor.execute(TEST_VALUE_1_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_1_STR_NORMAL));
        
    }
    
    @Test
    public void testBuildInput_unique_format() {
        Annotation[] annos = getAnnotations(TestCsv.class, "localdatetime_unique_format");
        CellProcessor cellProcessor = builder.buildInputCellProcessor(LocalTime.class, annos);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        
        assertThat(cellProcessor, hasCellProcessor(Unique.class));
        
        assertThat(cellProcessor.execute(TEST_VALUE_1_STR_FORMATTED, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_1_OBJ));
        
        // not quals input
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
        Annotation[] annos = getAnnotations(TestCsv.class, "localdatetime_unique_format");
        CellProcessor cellProcessor = builder.buildOutputCellProcessor(LocalTime.class, annos, false);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        
        assertThat(cellProcessor, hasCellProcessor(Unique.class));
        
        assertThat(cellProcessor.execute(TEST_VALUE_1_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_1_STR_FORMATTED));
        
        // not quals input
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
        Annotation[] annos = getAnnotations(TestCsv.class, "localdatetime_unique_format");
        CellProcessor cellProcessor = builder.buildOutputCellProcessor(LocalTime.class, annos, true);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        
        assertThat(cellProcessor, not(hasCellProcessor(Unique.class)));
        
        assertThat(cellProcessor.execute(TEST_VALUE_1_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_1_STR_FORMATTED));
        
        // not quals input
        assertThat(cellProcessor.execute(TEST_VALUE_1_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_1_STR_FORMATTED));
        
    }
    
    @Test
    public void testBuildInput_combine1() {
        Annotation[] annos = getAnnotations(TestCsv.class, "localdatetime_combine1");
        CellProcessor cellProcessor = builder.buildInputCellProcessor(LocalTime.class, annos);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        
        assertThat(cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_INPUT_DEFAULT_OBJ));
        assertThat(cellProcessor.execute("   " + TEST_VALUE_1_STR_NORMAL + "   ", ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_1_OBJ));
        
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
        Annotation[] annos = getAnnotations(TestCsv.class, "localdatetime_combine1");
        CellProcessor cellProcessor = builder.buildOutputCellProcessor(LocalTime.class, annos, false);
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
        Annotation[] annos = getAnnotations(TestCsv.class, "localdatetime_combine1");
        CellProcessor cellProcessor = builder.buildOutputCellProcessor(LocalTime.class, annos, true);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        
        assertThat(cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_OUTPUT_DEFAULT_STR_NORMAL));
        assertThat(cellProcessor.execute(TEST_VALUE_1_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_1_STR_NORMAL));
        
        // not equals input
        assertThat(cellProcessor.execute(TEST_VALUE_2_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_2_STR_NORMAL));
        
        // not unique input
        assertThat(cellProcessor.execute(TEST_VALUE_1_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_1_STR_NORMAL));
        
    }
    
    @Test
    public void testBuildInput_format_combine1() {
        Annotation[] annos = getAnnotations(TestCsv.class, "localdatetime_combine_format1");
        CellProcessor cellProcessor = builder.buildInputCellProcessor(LocalTime.class, annos);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        
        assertThat(cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_INPUT_DEFAULT_OBJ));
        assertThat(cellProcessor.execute("   " + TEST_VALUE_1_STR_FORMATTED + "   ", ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_1_OBJ));
        
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
    public void testBuildOutput_format_combine1() {
        Annotation[] annos = getAnnotations(TestCsv.class, "localdatetime_combine_format1");
        CellProcessor cellProcessor = builder.buildOutputCellProcessor(LocalTime.class, annos, false);
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
        Annotation[] annos = getAnnotations(TestCsv.class, "localdatetime_combine_format1");
        CellProcessor cellProcessor = builder.buildOutputCellProcessor(LocalTime.class, annos, true);
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
        Annotation[] annos = getAnnotations(TestCsv.class, "localdatetime_min");
        CellProcessor cellProcessor = builder.buildInputCellProcessor(LocalTime.class, annos);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        
        assertThat(cellProcessor, hasCellProcessor(FutureJoda.class));
        
        assertThat(cellProcessor.execute(TEST_VALUE_MIN_STR_NORMAL, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_MIN_OBJ));
        
        // greater than min value
        {
            LocalTime obj = TEST_VALUE_MIN_OBJ.plusSeconds(1);
            String str = format(obj, TEST_NORMAL_PATTERN);
            
            assertThat(cellProcessor.execute(str, ANONYMOUS_CSVCONTEXT), is(obj));
        }
        
        // less than min value
        try {
            LocalTime obj = TEST_VALUE_MIN_OBJ.minusSeconds(1);
            String str = format(obj, TEST_NORMAL_PATTERN);
            
            cellProcessor.execute(str, ANONYMOUS_CSVCONTEXT);
            fail();
        } catch(SuperCsvConstraintViolationException e) {
            CellProcessor errorProcessor = e.getProcessor();
            assertThat(errorProcessor, is(instanceOf(FutureJoda.class)));
        }
        
    }
    
    @Test
    public void testBuildOutput_min() {
        Annotation[] annos = getAnnotations(TestCsv.class, "localdatetime_min");
        CellProcessor cellProcessor = builder.buildOutputCellProcessor(LocalTime.class, annos, false);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        
        assertThat(cellProcessor, hasCellProcessor(FutureJoda.class));
        
        assertThat(cellProcessor.execute(TEST_VALUE_MIN_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_MIN_STR_NORMAL));
        
        // greater than min value
        {
            LocalTime obj = TEST_VALUE_MIN_OBJ.plusSeconds(1);
            String str = format(obj, TEST_NORMAL_PATTERN);
            
            assertThat(cellProcessor.execute(obj, ANONYMOUS_CSVCONTEXT), is(str));
        }
        
        // less min value
        try {
            LocalTime obj = TEST_VALUE_MIN_OBJ.minusSeconds(1);
            String str = format(obj, TEST_NORMAL_PATTERN);
            
            cellProcessor.execute(obj, ANONYMOUS_CSVCONTEXT);
            fail();
        } catch(SuperCsvConstraintViolationException e) {
            CellProcessor errorProcessor = e.getProcessor();
            assertThat(errorProcessor, is(instanceOf(FutureJoda.class)));
        }
        
    }
    
    @Test
    public void testBuildOutput_min_ignoreValidation() {
        Annotation[] annos = getAnnotations(TestCsv.class, "localdatetime_min");
        CellProcessor cellProcessor = builder.buildOutputCellProcessor(LocalTime.class, annos, true);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        
        assertThat(cellProcessor, not(hasCellProcessor(FutureJoda.class)));
        
        // less than min value
        {
            LocalTime obj = TEST_VALUE_MIN_OBJ.minusSeconds(1);
            String str = format(obj, TEST_NORMAL_PATTERN);
            
            assertThat(cellProcessor.execute(obj, ANONYMOUS_CSVCONTEXT), is(str));
        }
    }
    
    @Test
    public void testBuildInput_min_format() {
        Annotation[] annos = getAnnotations(TestCsv.class, "localdatetime_min_format");
        CellProcessor cellProcessor = builder.buildInputCellProcessor(LocalTime.class, annos);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        
        assertThat(cellProcessor, hasCellProcessor(FutureJoda.class));
        
        assertThat(cellProcessor.execute(TEST_VALUE_MIN_STR_FORMATTED, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_MIN_OBJ));
        
        // greater than min value
        {
            LocalTime obj = TEST_VALUE_MIN_OBJ.plusSeconds(1);
            String str = format(obj, TEST_FORMATTED_PATTERN);
            
            assertThat(cellProcessor.execute(str, ANONYMOUS_CSVCONTEXT), is(obj));
        }
        
        // less than min value
        try {
            LocalTime obj = TEST_VALUE_MIN_OBJ.minusSeconds(1);
            String str = format(obj, TEST_FORMATTED_PATTERN);
            
            cellProcessor.execute(str, ANONYMOUS_CSVCONTEXT);
            fail();
        } catch(SuperCsvConstraintViolationException e) {
            CellProcessor errorProcessor = e.getProcessor();
            assertThat(errorProcessor, is(instanceOf(FutureJoda.class)));
        }
        
    }
    
    @Test
    public void testBuildOutput_min_format() {
        Annotation[] annos = getAnnotations(TestCsv.class, "localdatetime_min_format");
        CellProcessor cellProcessor = builder.buildOutputCellProcessor(LocalTime.class, annos, false);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        
        assertThat(cellProcessor, hasCellProcessor(FutureJoda.class));
        
        assertThat(cellProcessor.execute(TEST_VALUE_MIN_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_MIN_STR_FORMATTED));
        
        // greater than min value
        {
            LocalTime obj = TEST_VALUE_MIN_OBJ.plusSeconds(1);
            String str = format(obj, TEST_FORMATTED_PATTERN);
            
            assertThat(cellProcessor.execute(obj, ANONYMOUS_CSVCONTEXT), is(str));
        }
        
        // less than min value
        try {
            LocalTime obj = TEST_VALUE_MIN_OBJ.minusSeconds(1);
            String str = format(obj, TEST_FORMATTED_PATTERN);
            
            cellProcessor.execute(obj, ANONYMOUS_CSVCONTEXT);
            fail();
        } catch(SuperCsvConstraintViolationException e) {
            CellProcessor errorProcessor = e.getProcessor();
            assertThat(errorProcessor, is(instanceOf(FutureJoda.class)));
        }
        
    }
    
    @Test
    public void testBuildOutput_min_format_ignoreValidation() {
        Annotation[] annos = getAnnotations(TestCsv.class, "localdatetime_min_format");
        CellProcessor cellProcessor = builder.buildOutputCellProcessor(LocalTime.class, annos, true);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        
        assertThat(cellProcessor, not(hasCellProcessor(FutureJoda.class)));
        
        // less than min value
        {
            LocalTime obj = TEST_VALUE_MIN_OBJ.minusSeconds(1);
            String str = format(obj, TEST_FORMATTED_PATTERN);
            
            assertThat(cellProcessor.execute(obj, ANONYMOUS_CSVCONTEXT), is(str));
        }
    }
    
    @Test
    public void testBuildInput_max() {
        Annotation[] annos = getAnnotations(TestCsv.class, "localdatetime_max");
        CellProcessor cellProcessor = builder.buildInputCellProcessor(LocalTime.class, annos);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        
        assertThat(cellProcessor, hasCellProcessor(PastJoda.class));
        
        assertThat(cellProcessor.execute(TEST_VALUE_MAX_STR_NORMAL, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_MAX_OBJ));
        
        // less than max value
        {
            LocalTime obj = TEST_VALUE_MAX_OBJ.minusSeconds(1);
            String str = format(obj, TEST_NORMAL_PATTERN);
            
            assertThat(cellProcessor.execute(str, ANONYMOUS_CSVCONTEXT), is(obj));
        }
        
        // greater than max value
        try {
            LocalTime obj = TEST_VALUE_MAX_OBJ.plusSeconds(1);
            String str = format(obj, TEST_NORMAL_PATTERN);
            
            cellProcessor.execute(str, ANONYMOUS_CSVCONTEXT);
            fail();
        } catch(SuperCsvConstraintViolationException e) {
            CellProcessor errorProcessor = e.getProcessor();
            assertThat(errorProcessor, is(instanceOf(PastJoda.class)));
        }
        
    }
    
    @Test
    public void testBuildOutput_max() {
        Annotation[] annos = getAnnotations(TestCsv.class, "localdatetime_max");
        CellProcessor cellProcessor = builder.buildOutputCellProcessor(LocalTime.class, annos, false);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        
        assertThat(cellProcessor, hasCellProcessor(PastJoda.class));
        
        assertThat(cellProcessor.execute(TEST_VALUE_MAX_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_MAX_STR_NORMAL));
        
        // less than max value
        {
            LocalTime obj = TEST_VALUE_MAX_OBJ.minusSeconds(1);
            String str = format(obj, TEST_NORMAL_PATTERN);
            
            assertThat(cellProcessor.execute(obj, ANONYMOUS_CSVCONTEXT), is(str));
        }
        
        // greater than max value
        try {
            LocalTime obj = TEST_VALUE_MAX_OBJ.plusSeconds(1);
            String str = format(obj, TEST_NORMAL_PATTERN);
            
            cellProcessor.execute(obj, ANONYMOUS_CSVCONTEXT);
            fail();
        } catch(SuperCsvConstraintViolationException e) {
            CellProcessor errorProcessor = e.getProcessor();
            assertThat(errorProcessor, is(instanceOf(PastJoda.class)));
        }
        
    }
    
    @Test
    public void testBuildOutput_max_ignoreValidation() {
        Annotation[] annos = getAnnotations(TestCsv.class, "localdatetime_max");
        CellProcessor cellProcessor = builder.buildOutputCellProcessor(LocalTime.class, annos, true);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        
        assertThat(cellProcessor, not(hasCellProcessor(PastJoda.class)));
        
        // greater than max value
        {
            LocalTime obj = TEST_VALUE_MAX_OBJ.plusSeconds(1);
            String str = format(obj, TEST_NORMAL_PATTERN);
            
            assertThat(cellProcessor.execute(obj, ANONYMOUS_CSVCONTEXT), is(str));
        }
    }
    
    @Test
    public void testBuildInput_max_format() {
        Annotation[] annos = getAnnotations(TestCsv.class, "localdatetime_max_format");
        CellProcessor cellProcessor = builder.buildInputCellProcessor(LocalTime.class, annos);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        
        assertThat(cellProcessor, hasCellProcessor(PastJoda.class));
        
        assertThat(cellProcessor.execute(TEST_VALUE_MAX_STR_FORMATTED, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_MAX_OBJ));
        
        // less than max value
        {
            LocalTime obj = TEST_VALUE_MAX_OBJ.minusSeconds(1);
            String str = format(obj, TEST_FORMATTED_PATTERN);
            
            assertThat(cellProcessor.execute(str, ANONYMOUS_CSVCONTEXT), is(obj));
        }
        
        // greater than max value
        try {
            LocalTime obj = TEST_VALUE_MAX_OBJ.plusSeconds(1);
            String str = format(obj, TEST_FORMATTED_PATTERN);
            
            cellProcessor.execute(str, ANONYMOUS_CSVCONTEXT);
            fail();
        } catch(SuperCsvConstraintViolationException e) {
            CellProcessor errorProcessor = e.getProcessor();
            assertThat(errorProcessor, is(instanceOf(PastJoda.class)));
        }
        
    }
    
    @Test
    public void testBuildOutput_max_format() {
        Annotation[] annos = getAnnotations(TestCsv.class, "localdatetime_max_format");
        CellProcessor cellProcessor = builder.buildOutputCellProcessor(LocalTime.class, annos, false);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        
        assertThat(cellProcessor, hasCellProcessor(PastJoda.class));
        
        assertThat(cellProcessor.execute(TEST_VALUE_MAX_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_MAX_STR_FORMATTED));
        
        // less than max value
        {
            LocalTime obj = TEST_VALUE_MAX_OBJ.minusSeconds(1);
            String str = format(obj, TEST_FORMATTED_PATTERN);
            
            assertThat(cellProcessor.execute(obj, ANONYMOUS_CSVCONTEXT), is(str));
        }
        
        // greater than max value
        try {
            LocalTime obj = TEST_VALUE_MAX_OBJ.plusSeconds(1);
            String str = format(obj, TEST_FORMATTED_PATTERN);
            
            cellProcessor.execute(obj, ANONYMOUS_CSVCONTEXT);
            fail();
        } catch(SuperCsvConstraintViolationException e) {
            CellProcessor errorProcessor = e.getProcessor();
            assertThat(errorProcessor, is(instanceOf(PastJoda.class)));
        }
        
    }
    
    @Test
    public void testBuildOutput_max_format_ignoreValidation() {
        Annotation[] annos = getAnnotations(TestCsv.class, "localdatetime_max_format");
        CellProcessor cellProcessor = builder.buildOutputCellProcessor(LocalTime.class, annos, true);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        
        assertThat(cellProcessor, not(hasCellProcessor(PastJoda.class)));
        
        // greater than max value
        {
            LocalTime obj = TEST_VALUE_MAX_OBJ.plusSeconds(1);
            String str = format(obj, TEST_FORMATTED_PATTERN);
            
            assertThat(cellProcessor.execute(obj, ANONYMOUS_CSVCONTEXT), is(str));
        }
    }
    
    @Test
    public void testBuildInput_range() {
        Annotation[] annos = getAnnotations(TestCsv.class, "localdatetime_range");
        CellProcessor cellProcessor = builder.buildInputCellProcessor(LocalTime.class, annos);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        
        assertThat(cellProcessor, hasCellProcessor(JodaRange.class));
        
        assertThat(cellProcessor.execute(TEST_VALUE_MIN_STR_NORMAL, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_MIN_OBJ));
        
        // greater than min value
        {
            LocalTime obj = TEST_VALUE_MIN_OBJ.plusSeconds(1);
            String str = format(obj, TEST_NORMAL_PATTERN);
            assertThat(cellProcessor.execute(str, ANONYMOUS_CSVCONTEXT), is(obj));
        }
        
        // less than min value
        try {
            LocalTime obj = TEST_VALUE_MIN_OBJ.minusSeconds(1);
            String str = format(obj, TEST_NORMAL_PATTERN);
            
            cellProcessor.execute(str, ANONYMOUS_CSVCONTEXT);
            fail();
        } catch(SuperCsvConstraintViolationException e) {
            CellProcessor errorProcessor = e.getProcessor();
            assertThat(errorProcessor, is(instanceOf(JodaRange.class)));
        }
        
        assertThat(cellProcessor.execute(TEST_VALUE_MAX_STR_NORMAL, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_MAX_OBJ));
        
        // less than max value
        {
            LocalTime obj = TEST_VALUE_MAX_OBJ.minusSeconds(1);
            String str = format(obj, TEST_NORMAL_PATTERN);
            
            assertThat(cellProcessor.execute(str, ANONYMOUS_CSVCONTEXT), is(obj));
        }
        
        // greater than max value
        try {
            LocalTime obj = TEST_VALUE_MAX_OBJ.plusSeconds(1);
            String str = format(obj, TEST_NORMAL_PATTERN);
            
            cellProcessor.execute(str, ANONYMOUS_CSVCONTEXT);
            fail();
        } catch(SuperCsvConstraintViolationException e) {
            CellProcessor errorProcessor = e.getProcessor();
            assertThat(errorProcessor, is(instanceOf(JodaRange.class)));
        }
        
    }
    
    @Test
    public void testBuildOutput_range() {
        Annotation[] annos = getAnnotations(TestCsv.class, "localdatetime_range");
        CellProcessor cellProcessor = builder.buildOutputCellProcessor(LocalTime.class, annos, false);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        
        assertThat(cellProcessor, hasCellProcessor(JodaRange.class));
        
        assertThat(cellProcessor.execute(TEST_VALUE_MIN_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_MIN_STR_NORMAL));
        
        // greater than min value
        {
            LocalTime obj = TEST_VALUE_MIN_OBJ.plusSeconds(1);
            String str = format(obj, TEST_NORMAL_PATTERN);
            
            assertThat(cellProcessor.execute(obj, ANONYMOUS_CSVCONTEXT), is(str));
        }
        
        // less than min value
        try {
            LocalTime obj = TEST_VALUE_MIN_OBJ.minusSeconds(1);
            String str = format(obj, TEST_NORMAL_PATTERN);
            
            cellProcessor.execute(obj, ANONYMOUS_CSVCONTEXT);
            fail();
        } catch(SuperCsvConstraintViolationException e) {
            CellProcessor errorProcessor = e.getProcessor();
            assertThat(errorProcessor, is(instanceOf(JodaRange.class)));
        }
        
        assertThat(cellProcessor.execute(TEST_VALUE_MAX_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_MAX_STR_NORMAL));
        
        // less than max value
        {
            LocalTime obj = TEST_VALUE_MAX_OBJ.minusSeconds(1);
            String str = format(obj, TEST_NORMAL_PATTERN);
            
            assertThat(cellProcessor.execute(obj, ANONYMOUS_CSVCONTEXT), is(str));
        }
        
        // greater than max value
        try {
            LocalTime obj = TEST_VALUE_MAX_OBJ.plusSeconds(1);
            String str = format(obj, TEST_NORMAL_PATTERN);
            
            cellProcessor.execute(obj, ANONYMOUS_CSVCONTEXT);
            fail();
        } catch(SuperCsvConstraintViolationException e) {
            CellProcessor errorProcessor = e.getProcessor();
            assertThat(errorProcessor, is(instanceOf(JodaRange.class)));
        }
        
    }
    
    @Test
    public void testBuildOutput_range_ignoreValidation() {
        Annotation[] annos = getAnnotations(TestCsv.class, "localdatetime_range");
        CellProcessor cellProcessor = builder.buildOutputCellProcessor(LocalTime.class, annos, true);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        
        assertThat(cellProcessor, not(hasCellProcessor(JodaRange.class)));
        
        // less than min value
        {
            LocalTime obj = TEST_VALUE_MIN_OBJ.minusSeconds(1);
            String str = format(obj, TEST_NORMAL_PATTERN);
            
            assertThat(cellProcessor.execute(obj, ANONYMOUS_CSVCONTEXT), is(str));
        }
        
        // greater than max value
        {
            LocalTime obj = TEST_VALUE_MAX_OBJ.plusSeconds(1);
            String str = format(obj, TEST_NORMAL_PATTERN);
            
            assertThat(cellProcessor.execute(obj, ANONYMOUS_CSVCONTEXT), is(str));
        }
    }
    
    @Test
    public void testBuildInput_range_format() {
        Annotation[] annos = getAnnotations(TestCsv.class, "localdatetime_range_format");
        CellProcessor cellProcessor = builder.buildInputCellProcessor(LocalTime.class, annos);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        
        assertThat(cellProcessor, hasCellProcessor(JodaRange.class));
        
        assertThat(cellProcessor.execute(TEST_VALUE_MIN_STR_FORMATTED, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_MIN_OBJ));
        
        // greater than min value
        {
            LocalTime obj = TEST_VALUE_MIN_OBJ.plusSeconds(1);
            String str = format(obj, TEST_FORMATTED_PATTERN);
            assertThat(cellProcessor.execute(str, ANONYMOUS_CSVCONTEXT), is(obj));
        }
        
        // less than min value
        try {
            LocalTime obj = TEST_VALUE_MIN_OBJ.minusSeconds(1);
            String str = format(obj, TEST_FORMATTED_PATTERN);
            
            cellProcessor.execute(str, ANONYMOUS_CSVCONTEXT);
            fail();
        } catch(SuperCsvConstraintViolationException e) {
            CellProcessor errorProcessor = e.getProcessor();
            assertThat(errorProcessor, is(instanceOf(JodaRange.class)));
        }
        
        assertThat(cellProcessor.execute(TEST_VALUE_MAX_STR_FORMATTED, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_MAX_OBJ));
        
        // less than max value
        {
            LocalTime obj = TEST_VALUE_MAX_OBJ.minusSeconds(1);
            String str = format(obj, TEST_FORMATTED_PATTERN);
            
            assertThat(cellProcessor.execute(str, ANONYMOUS_CSVCONTEXT), is(obj));
        }
        
        // greater than max value
        try {
            LocalTime obj = TEST_VALUE_MAX_OBJ.plusSeconds(1);
            String str = format(obj, TEST_FORMATTED_PATTERN);
            
            cellProcessor.execute(str, ANONYMOUS_CSVCONTEXT);
            fail();
        } catch(SuperCsvConstraintViolationException e) {
            CellProcessor errorProcessor = e.getProcessor();
            assertThat(errorProcessor, is(instanceOf(JodaRange.class)));
        }
        
    }
    
    @Test
    public void testBuildOutput_range_format() {
        Annotation[] annos = getAnnotations(TestCsv.class, "localdatetime_range_format");
        CellProcessor cellProcessor = builder.buildOutputCellProcessor(LocalTime.class, annos, false);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        
        assertThat(cellProcessor, hasCellProcessor(JodaRange.class));
        
        assertThat(cellProcessor.execute(TEST_VALUE_MIN_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_MIN_STR_FORMATTED));
        
        // greater than min value
        {
            LocalTime obj = TEST_VALUE_MIN_OBJ.plusSeconds(1);
            String str = format(obj, TEST_FORMATTED_PATTERN);
            
            assertThat(cellProcessor.execute(obj, ANONYMOUS_CSVCONTEXT), is(str));
        }
        
        // less than min value
        try {
            LocalTime obj = TEST_VALUE_MIN_OBJ.minusSeconds(1);
            String str = format(obj, TEST_FORMATTED_PATTERN);
            
            cellProcessor.execute(obj, ANONYMOUS_CSVCONTEXT);
            fail();
        } catch(SuperCsvConstraintViolationException e) {
            CellProcessor errorProcessor = e.getProcessor();
            assertThat(errorProcessor, is(instanceOf(JodaRange.class)));
        }
        
        assertThat(cellProcessor.execute(TEST_VALUE_MAX_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_MAX_STR_FORMATTED));
        
        // less than max value
        {
            LocalTime obj = TEST_VALUE_MAX_OBJ.minusSeconds(1);
            String str = format(obj, TEST_FORMATTED_PATTERN);
            
            assertThat(cellProcessor.execute(obj, ANONYMOUS_CSVCONTEXT), is(str));
        }
        
        // greater than max value
        try {
            LocalTime obj = TEST_VALUE_MAX_OBJ.plusSeconds(1);
            String str = format(obj, TEST_FORMATTED_PATTERN);
            
            cellProcessor.execute(obj, ANONYMOUS_CSVCONTEXT);
            fail();
        } catch(SuperCsvConstraintViolationException e) {
            CellProcessor errorProcessor = e.getProcessor();
            assertThat(errorProcessor, is(instanceOf(JodaRange.class)));
        }
        
    }
    
    @Test
    public void testBuildOutput_range_formatignoreValidation() {
        Annotation[] annos = getAnnotations(TestCsv.class, "localdatetime_range_format");
        CellProcessor cellProcessor = builder.buildOutputCellProcessor(LocalTime.class, annos, true);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        
        assertThat(cellProcessor, not(hasCellProcessor(JodaRange.class)));
        
        // less than min value
        {
            LocalTime obj = TEST_VALUE_MIN_OBJ.minusSeconds(1);
            String str = format(obj, TEST_FORMATTED_PATTERN);
            
            assertThat(cellProcessor.execute(obj, ANONYMOUS_CSVCONTEXT), is(str));
        }
        
        // greater than max value
        {
            LocalTime obj = TEST_VALUE_MAX_OBJ.plusSeconds(1);
            String str = format(obj, TEST_FORMATTED_PATTERN);
            
            assertThat(cellProcessor.execute(obj, ANONYMOUS_CSVCONTEXT), is(str));
        }
    }
    
    @Test
    public void testBuildInput_lenient() {
        
        Annotation[] annos = getAnnotations(TestCsv.class, "localdatetime_lenient");
        CellProcessor cellProcessor = builder.buildInputCellProcessor(LocalTime.class, annos);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        
        assertThat(cellProcessor, hasCellProcessor(ParseLocalTime.class));
        
        assertThat(cellProcessor.execute(TEST_VALUE_1_STR_NORMAL, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_1_OBJ));
        
        try {
            cellProcessor.execute("2016-02-31 07:12:01", ANONYMOUS_CSVCONTEXT);
            fail();
            
        } catch(SuperCsvCellProcessorException e) {
            CellProcessor errorProcessor = e.getProcessor();
            assertThat(errorProcessor, is(instanceOf(ParseLocalTime.class)));
        }
        
    }
    
    @Test
    public void testBuildOutput_lenient() {
        
        Annotation[] annos = getAnnotations(TestCsv.class, "localdatetime_lenient");
        CellProcessor cellProcessor = builder.buildOutputCellProcessor(LocalTime.class, annos, false);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        
        assertThat(cellProcessor, hasCellProcessor(FmtLocalTime.class));
        
        assertThat(cellProcessor.execute(TEST_VALUE_1_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_1_STR_NORMAL));
        
    }
    
    @Ignore("和暦の場合、時間を含むことができないため無視する。")
    @Test
    public void testBuildInput_locale() {
        
        Annotation[] annos = getAnnotations(TestCsv.class, "localdatetime_locale");
        CellProcessor cellProcessor = builder.buildInputCellProcessor(LocalTime.class, annos);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        
        assertThat(cellProcessor, hasCellProcessor(ParseLocalTime.class));
        
        assertThat(cellProcessor.execute("平成28年2月29日 7時12分1秒", ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_1_OBJ));
        
    }
    
    @Ignore("和暦の場合、時間を含むことができないため無視する。")
    @Test
    public void testBuildOutput_locale() {
        
        Annotation[] annos = getAnnotations(TestCsv.class, "localdatetime_locale");
        CellProcessor cellProcessor = builder.buildOutputCellProcessor(LocalTime.class, annos, false);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        
        assertThat(cellProcessor, hasCellProcessor(FmtLocalTime.class));
        
        assertThat(cellProcessor.execute(TEST_VALUE_1_OBJ, ANONYMOUS_CSVCONTEXT), is("平成28年2月29日 7時12分1秒"));
        
    }
    
    @Test
    public void testBuildInput_timezone() {
        
        Annotation[] annos = getAnnotations(TestCsv.class, "localdatetime_timezone");
        CellProcessor cellProcessor = builder.buildInputCellProcessor(LocalTime.class, annos);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        
        assertThat(cellProcessor, hasCellProcessor(ParseLocalTime.class));
        
        // ignore timzezone
        assertThat(cellProcessor.execute(TEST_VALUE_1_STR_NORMAL, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_1_OBJ));
        
    }
    
    @Test
    public void testBuildOutput_timezone() {
        
        Annotation[] annos = getAnnotations(TestCsv.class, "localdatetime_timezone");
        CellProcessor cellProcessor = builder.buildOutputCellProcessor(LocalTime.class, annos, false);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        
        assertThat(cellProcessor, hasCellProcessor(FmtLocalTime.class));
        
        // ignore timzezone
        assertThat(cellProcessor.execute(TEST_VALUE_1_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_1_STR_NORMAL));
        
    }
}

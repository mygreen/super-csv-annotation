package org.supercsv.ext.builder.impl;

import static org.junit.Assert.*;
import static org.supercsv.ext.tool.TestUtils.*;
import static org.hamcrest.Matchers.*;
import static org.supercsv.ext.tool.HasCellProcessor.*;

import java.lang.annotation.Annotation;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.supercsv.cellprocessor.ConvertNullTo;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.constraint.Equals;
import org.supercsv.cellprocessor.constraint.ForbidSubStr;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.constraint.RequireSubStr;
import org.supercsv.cellprocessor.constraint.StrNotNullOrEmpty;
import org.supercsv.cellprocessor.constraint.StrRegEx;
import org.supercsv.cellprocessor.constraint.Strlen;
import org.supercsv.cellprocessor.constraint.Unique;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.exception.SuperCsvConstraintViolationException;
import org.supercsv.ext.annotation.CsvBean;
import org.supercsv.ext.annotation.CsvColumn;
import org.supercsv.ext.annotation.CsvStringConverter;
import org.supercsv.ext.cellprocessor.Trim;
import org.supercsv.ext.cellprocessor.constraint.Length;
import org.supercsv.ext.cellprocessor.constraint.MaxLength;
import org.supercsv.ext.cellprocessor.constraint.MinLength;

/**
 * Test the {@link StringCellProcessorBuilder} CellProcessor.
 *
 * @since 1.2
 * @author T.TSUCHIE
 *
 */
public class StringCellProcessorBuilderTest {
    
    @Rule
    public TestName name = new TestName();
    
    private StringCellProcessorBuilder builder;
    
    /**
     * Sets up the processor for the test using Combinations
     */
    @Before
    public void setUp() {
        builder = new StringCellProcessorBuilder();
    }
    
    @CsvBean
    private static class TestCsv {
        
        @CsvColumn(position=0)
        String str_default;
        
        @CsvColumn(position=1, optional=true)
        String str_optional;
        
        @CsvColumn(position=2, trim=true)
        String str_trim;
        
        @CsvColumn(position=3, inputDefaultValue="OK[in]", outputDefaultValue="OK[out]")
        String str_defaultValue;
        
        @CsvColumn(position=4, inputDefaultValue="@empty", outputDefaultValue="@empty")
        String str_defaultValue_empty;
        
        @CsvColumn(position=5, equalsValue="abc")
        String str_equalsValue;
        
        @CsvColumn(position=6, unique=true)
        String str_unique;
        
        @CsvColumn(position=7, optional=true, trim=true, inputDefaultValue=" OK[in] ", outputDefaultValue=" OK[out]   ", equalsValue="OK", unique=true)
        String str_combine1;
        
        @CsvColumn(position=8)
        @CsvStringConverter(minLength=5)
        String str_minLength;
        
        @CsvColumn(position=9)
        @CsvStringConverter(maxLength=5)
        String str_maxLength;
        
        @CsvColumn(position=10)
        @CsvStringConverter(minLength=2, maxLength=5)
        String str_minMaxLength;
        
        @CsvColumn(position=11)
        @CsvStringConverter(exactLength={2, 5})
        String str_exactLength;
        
        @CsvColumn(position=12)
        @CsvStringConverter(regex="\\d{4}-\\d{2}-\\d{2}")
        String str_regex;
        
        @CsvColumn(position=13)
        @CsvStringConverter(forbid={"abc", "ABC"})
        String str_forbid;
        
        @CsvColumn(position=14)
        @CsvStringConverter(contain={"world", "ABC"})
        String str_contain;
        
        @CsvColumn(position=15)
        @CsvStringConverter(notEmpty=true)
        String str_notEmpty;
        
        @CsvColumn(position=16, trim=true)
        @CsvStringConverter(minLength=2, regex=".*", forbid={"abc", "ABC"}, contain={"world"}, notEmpty=true)
        String str_combine2;
        
    }
    
    /**
     * Tests with default. (not grant convert annotation.)
     */
    @Test
    public void testBuildInput_default() {
        
        Annotation[] annos = getAnnotations(TestCsv.class, "str_default");
        CellProcessor cellProcessor = builder.buildInputCellProcessor(String.class, annos);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        assertThat(cellProcessor, hasCellProcessor(NotNull.class));
        
        assertThat(cellProcessor.execute("abc", ANONYMOUS_CSVCONTEXT), is("abc"));
        assertThat(cellProcessor.execute("", ANONYMOUS_CSVCONTEXT), is(""));
        
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
    public void testBuildOutput_default() {
        
        Annotation[] annos = getAnnotations(TestCsv.class, "str_default");
        CellProcessor cellProcessor = builder.buildOutputCellProcessor(String.class, annos, false);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        assertThat(cellProcessor, hasCellProcessor(NotNull.class));
        
        assertThat(cellProcessor.execute("abc", ANONYMOUS_CSVCONTEXT), is("abc"));
        assertThat(cellProcessor.execute("", ANONYMOUS_CSVCONTEXT), is(""));
        
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
        
        Annotation[] annos = getAnnotations(TestCsv.class, "str_default");
        CellProcessor cellProcessor = builder.buildOutputCellProcessor(String.class, annos, true);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        assertThat(cellProcessor, hasCellProcessor(NotNull.class));
        
        assertThat(cellProcessor.execute("abc", ANONYMOUS_CSVCONTEXT), is("abc"));
        assertThat(cellProcessor.execute("", ANONYMOUS_CSVCONTEXT), is(""));
        
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
        
        Annotation[] annos = getAnnotations(TestCsv.class, "str_optional");
        CellProcessor cellProcessor = builder.buildInputCellProcessor(String.class, annos);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        assertThat(cellProcessor, hasCellProcessor(Optional.class));
        
        assertThat(cellProcessor.execute("abc", ANONYMOUS_CSVCONTEXT), is("abc"));
        
        // null input
        assertThat(cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT), is(nullValue()));
    }
    
    /**
     * Tests with optional. (not grant convert annotation.)
     */
    @Test
    public void testBuildOutput_optional() {
        
        Annotation[] annos = getAnnotations(TestCsv.class, "str_optional");
        CellProcessor cellProcessor = builder.buildOutputCellProcessor(String.class, annos, false);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        assertThat(cellProcessor, hasCellProcessor(Optional.class));
        
        assertThat(cellProcessor.execute("abc", ANONYMOUS_CSVCONTEXT), is("abc"));
        assertThat(cellProcessor.execute("", ANONYMOUS_CSVCONTEXT), is(""));
        
        // null input
        assertThat(cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT), is(nullValue()));
    }
    
    /**
     * Tests with optional. (not grant convert annotation.)
     */
    @Test
    public void testBuildOutput_optional_ignoreValidation() {
        
        Annotation[] annos = getAnnotations(TestCsv.class, "str_optional");
        CellProcessor cellProcessor = builder.buildOutputCellProcessor(String.class, annos, true);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        assertThat(cellProcessor, hasCellProcessor(Optional.class));
        
        assertThat(cellProcessor.execute("abc", ANONYMOUS_CSVCONTEXT), is("abc"));
        assertThat(cellProcessor.execute("", ANONYMOUS_CSVCONTEXT), is(""));
        
        // null input
        assertThat(cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT), is(nullValue()));
    }
    
    @Test
    public void testBuildInput_trim() {
        Annotation[] annos = getAnnotations(TestCsv.class, "str_trim");
        CellProcessor cellProcessor = builder.buildInputCellProcessor(String.class, annos);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        assertThat(cellProcessor, hasCellProcessor(Trim.class));
        
        assertThat(cellProcessor.execute("  abc ", ANONYMOUS_CSVCONTEXT), is("abc"));
    }
    
    @Test
    public void testBuildOutput_trim() {
        Annotation[] annos = getAnnotations(TestCsv.class, "str_trim");
        CellProcessor cellProcessor = builder.buildOutputCellProcessor(String.class, annos, false);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        assertThat(cellProcessor, hasCellProcessor(Trim.class));
        
        assertThat(cellProcessor.execute("  abc ", ANONYMOUS_CSVCONTEXT), is("abc"));
    }
    
    @Test
    public void testBuildOutput_trim_ignoreValidation() {
        Annotation[] annos = getAnnotations(TestCsv.class, "str_trim");
        CellProcessor cellProcessor = builder.buildOutputCellProcessor(String.class, annos, true);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        assertThat(cellProcessor, hasCellProcessor(Trim.class));
        
        assertThat(cellProcessor.execute("  abc ", ANONYMOUS_CSVCONTEXT), is("abc"));
    }
    
    @Test
    public void testBuildInput_defaultValue() {
        Annotation[] annos = getAnnotations(TestCsv.class, "str_defaultValue");
        CellProcessor cellProcessor = builder.buildInputCellProcessor(String.class, annos);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        assertThat(cellProcessor, hasCellProcessor(ConvertNullTo.class));
        
        assertThat(cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT), is("OK[in]"));
    }
    
    @Test
    public void testBuildOutput_defaultValue() {
        Annotation[] annos = getAnnotations(TestCsv.class, "str_defaultValue");
        CellProcessor cellProcessor = builder.buildOutputCellProcessor(String.class, annos, false);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        assertThat(cellProcessor, hasCellProcessor(ConvertNullTo.class));
        
        assertThat(cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT), is("OK[out]"));
    }
    
    @Test
    public void testBuildOutput_defaultValue_ignoreValidation() {
        Annotation[] annos = getAnnotations(TestCsv.class, "str_defaultValue");
        CellProcessor cellProcessor = builder.buildOutputCellProcessor(String.class, annos, true);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        assertThat(cellProcessor, hasCellProcessor(ConvertNullTo.class));
        
        assertThat(cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT), is("OK[out]"));
    }
    
    @Test
    public void testBuildInput_defaultValue_empty() {
        Annotation[] annos = getAnnotations(TestCsv.class, "str_defaultValue_empty");
        CellProcessor cellProcessor = builder.buildInputCellProcessor(String.class, annos);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        assertThat(cellProcessor, hasCellProcessor(ConvertNullTo.class));
        
        assertThat(cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT), is(""));
    }
    
    @Test
    public void testBuildOutput_defaultValue_empty() {
        Annotation[] annos = getAnnotations(TestCsv.class, "str_defaultValue_empty");
        CellProcessor cellProcessor = builder.buildOutputCellProcessor(String.class, annos, false);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        assertThat(cellProcessor, hasCellProcessor(ConvertNullTo.class));
        
        assertThat(cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT), is(""));
    }
    
    @Test
    public void testBuildOutput_defaultValue_empty_ignoreValidation() {
        Annotation[] annos = getAnnotations(TestCsv.class, "str_defaultValue_empty");
        CellProcessor cellProcessor = builder.buildOutputCellProcessor(String.class, annos, true);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        assertThat(cellProcessor, hasCellProcessor(ConvertNullTo.class));
        
        assertThat(cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT), is(""));
    }
    
    @Test
    public void testBuildInput_equalsValue() {
        Annotation[] annos = getAnnotations(TestCsv.class, "str_equalsValue");
        CellProcessor cellProcessor = builder.buildInputCellProcessor(String.class, annos);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        assertThat(cellProcessor, hasCellProcessor(Equals.class));
        
        assertThat(cellProcessor.execute("abc", ANONYMOUS_CSVCONTEXT), is("abc"));
        
        // not quals input
        try {
            cellProcessor.execute("123", ANONYMOUS_CSVCONTEXT);
            fail();
        } catch(SuperCsvConstraintViolationException e) {
            CellProcessor errorProcessor = e.getProcessor();
            assertThat(errorProcessor, is(instanceOf(Equals.class)));
        }
    }
    
    @Test
    public void testBuildOutput_equalsValue() {
        Annotation[] annos = getAnnotations(TestCsv.class, "str_equalsValue");
        CellProcessor cellProcessor = builder.buildOutputCellProcessor(String.class, annos, false);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        assertThat(cellProcessor, hasCellProcessor(Equals.class));
        
        assertThat(cellProcessor.execute("abc", ANONYMOUS_CSVCONTEXT), is("abc"));
        
        // not quals input
        try {
            cellProcessor.execute("123", ANONYMOUS_CSVCONTEXT);
            fail();
        } catch(SuperCsvConstraintViolationException e) {
            CellProcessor errorProcessor = e.getProcessor();
            assertThat(errorProcessor, is(instanceOf(Equals.class)));
        }
    }
    
    @Test
    public void testBuildOutput_equalsValue_ignoreValidation() {
        Annotation[] annos = getAnnotations(TestCsv.class, "str_equalsValue");
        CellProcessor cellProcessor = builder.buildOutputCellProcessor(String.class, annos, true);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        assertThat(cellProcessor, not(hasCellProcessor(Equals.class)));
        
        assertThat(cellProcessor.execute("abc", ANONYMOUS_CSVCONTEXT), is("abc"));
        
        // not quals input
        assertThat(cellProcessor.execute("123", ANONYMOUS_CSVCONTEXT), is("123"));
        
    }
    
    @Test
    public void testBuildInput_unique() {
        Annotation[] annos = getAnnotations(TestCsv.class, "str_unique");
        CellProcessor cellProcessor = builder.buildInputCellProcessor(String.class, annos);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        assertThat(cellProcessor, hasCellProcessor(Unique.class));
        
        assertThat(cellProcessor.execute("abc", ANONYMOUS_CSVCONTEXT), is("abc"));
        assertThat(cellProcessor.execute("123", ANONYMOUS_CSVCONTEXT), is("123"));
        
        // not unique input
        try {
            cellProcessor.execute("abc", ANONYMOUS_CSVCONTEXT);
            fail();
        } catch(SuperCsvConstraintViolationException e) {
            CellProcessor errorProcessor = e.getProcessor();
            assertThat(errorProcessor, is(instanceOf(Unique.class)));
        }
    }
    
    @Test
    public void testBuildOutput_unique() {
        Annotation[] annos = getAnnotations(TestCsv.class, "str_unique");
        CellProcessor cellProcessor = builder.buildOutputCellProcessor(String.class, annos, false);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        assertThat(cellProcessor, hasCellProcessor(Unique.class));
        
        assertThat(cellProcessor.execute("abc", ANONYMOUS_CSVCONTEXT), is("abc"));
        assertThat(cellProcessor.execute("123", ANONYMOUS_CSVCONTEXT), is("123"));
        
        // not unique input
        try {
            cellProcessor.execute("abc", ANONYMOUS_CSVCONTEXT);
            fail();
        } catch(SuperCsvConstraintViolationException e) {
            CellProcessor errorProcessor = e.getProcessor();
            assertThat(errorProcessor, is(instanceOf(Unique.class)));
        }
    }
    
    @Test
    public void testBuildOutput_unique_ignoreValidation() {
        Annotation[] annos = getAnnotations(TestCsv.class, "str_unique");
        CellProcessor cellProcessor = builder.buildOutputCellProcessor(String.class, annos, true);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        assertThat(cellProcessor, not(hasCellProcessor(Unique.class)));
        
        assertThat(cellProcessor.execute("abc", ANONYMOUS_CSVCONTEXT), is("abc"));
        assertThat(cellProcessor.execute("abc", ANONYMOUS_CSVCONTEXT), is("abc"));
    }
    
    @Test
    public void testBuildInput_combine1() {
        Annotation[] annos = getAnnotations(TestCsv.class, "str_combine1");
        CellProcessor cellProcessor = builder.buildInputCellProcessor(String.class, annos);
        
        printCellProcessorChain(cellProcessor, name.getMethodName());
        
        assertThat(cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT), is(" OK[in] "));
        cellProcessor.execute("OK", ANONYMOUS_CSVCONTEXT);
        
        // not equals input
        try {
            cellProcessor.execute("abc", ANONYMOUS_CSVCONTEXT);
            fail();
        } catch(SuperCsvConstraintViolationException e) {
            CellProcessor errorProcessor = e.getProcessor();
            assertThat(errorProcessor, is(instanceOf(Equals.class)));
        }
        
        // not unique input
        try {
            cellProcessor.execute("OK", ANONYMOUS_CSVCONTEXT);
            fail();
        } catch(SuperCsvConstraintViolationException e) {
            CellProcessor errorProcessor = e.getProcessor();
            assertThat(errorProcessor, is(instanceOf(Unique.class)));
        }
    }
    
    @Test
    public void testBuildOutput_combine1() {
        Annotation[] annos = getAnnotations(TestCsv.class, "str_combine1");
        CellProcessor cellProcessor = builder.buildOutputCellProcessor(String.class, annos, false);
        
        printCellProcessorChain(cellProcessor, name.getMethodName());
        
        assertThat(cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT), is(" OK[out]   "));
        assertThat(cellProcessor.execute("OK", ANONYMOUS_CSVCONTEXT), is("OK"));
        
        // not equals input
        try {
            cellProcessor.execute("abc", ANONYMOUS_CSVCONTEXT);
            fail();
        } catch(SuperCsvConstraintViolationException e) {
            CellProcessor errorProcessor = e.getProcessor();
            assertThat(errorProcessor, is(instanceOf(Equals.class)));
        }
        
        // not unique input
        try {
            cellProcessor.execute("OK", ANONYMOUS_CSVCONTEXT);
            fail();
        } catch(SuperCsvConstraintViolationException e) {
            CellProcessor errorProcessor = e.getProcessor();
            assertThat(errorProcessor, is(instanceOf(Unique.class)));
        }
    }
    
    @Test
    public void testBuildOutput_combine1_ignoreValidation() {
        Annotation[] annos = getAnnotations(TestCsv.class, "str_combine1");
        CellProcessor cellProcessor = builder.buildOutputCellProcessor(String.class, annos, true);
        
        printCellProcessorChain(cellProcessor, name.getMethodName());
        
        assertThat(cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT), is(" OK[out]   "));
        assertThat(cellProcessor.execute("OK", ANONYMOUS_CSVCONTEXT), is("OK"));
        
        // not equals input
        assertThat(cellProcessor.execute("abc", ANONYMOUS_CSVCONTEXT), is("abc"));
        
        // not unique input
        assertThat(cellProcessor.execute("OK", ANONYMOUS_CSVCONTEXT), is("OK"));
        
    }
    
    @Test
    public void testBuildInput_minLength() {
        Annotation[] annos = getAnnotations(TestCsv.class, "str_minLength");
        CellProcessor cellProcessor = builder.buildInputCellProcessor(String.class, annos);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        assertThat(cellProcessor, hasCellProcessor(MinLength.class));
        
        assertThat(cellProcessor.execute("abcde", ANONYMOUS_CSVCONTEXT), is("abcde"));
        assertThat(cellProcessor.execute("abcdef", ANONYMOUS_CSVCONTEXT), is("abcdef"));
        assertThat(cellProcessor.execute(" abcde  ", ANONYMOUS_CSVCONTEXT), is(" abcde  "));
        
        // length less than minLength(5)
        try {
            cellProcessor.execute("abcd", ANONYMOUS_CSVCONTEXT);
            fail();
        } catch(SuperCsvConstraintViolationException e) {
            CellProcessor errorProcessor = e.getProcessor();
            assertThat(errorProcessor, is(instanceOf(MinLength.class)));
        }
        
    }
    
    @Test
    public void testBuildOutput_minLength() {
        Annotation[] annos = getAnnotations(TestCsv.class, "str_minLength");
        CellProcessor cellProcessor = builder.buildOutputCellProcessor(String.class, annos, false);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        assertThat(cellProcessor, hasCellProcessor(MinLength.class));
        
        assertThat(cellProcessor.execute("abcde", ANONYMOUS_CSVCONTEXT), is("abcde"));
        assertThat(cellProcessor.execute("abcdef", ANONYMOUS_CSVCONTEXT), is("abcdef"));
        assertThat(cellProcessor.execute(" abcde  ", ANONYMOUS_CSVCONTEXT), is(" abcde  "));
        
        // length less than minLength(5)
        try {
            cellProcessor.execute("abcd", ANONYMOUS_CSVCONTEXT);
            fail();
        } catch(SuperCsvConstraintViolationException e) {
            CellProcessor errorProcessor = e.getProcessor();
            assertThat(errorProcessor, is(instanceOf(MinLength.class)));
        }
        
    }
    
    @Test
    public void testBuildOutput_minLength_ignoreValidation() {
        Annotation[] annos = getAnnotations(TestCsv.class, "str_minLength");
        CellProcessor cellProcessor = builder.buildOutputCellProcessor(String.class, annos, true);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        assertThat(cellProcessor, not(hasCellProcessor(MinLength.class)));
        
        // length less than minLength(5)
        assertThat(cellProcessor.execute("abcd", ANONYMOUS_CSVCONTEXT), is("abcd"));
        
    }
    
    @Test
    public void testBuildInput_maxLength() {
        Annotation[] annos = getAnnotations(TestCsv.class, "str_maxLength");
        CellProcessor cellProcessor = builder.buildInputCellProcessor(String.class, annos);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        assertThat(cellProcessor, hasCellProcessor(MaxLength.class));
        
        assertThat(cellProcessor.execute("abcde", ANONYMOUS_CSVCONTEXT), is("abcde"));
        assertThat(cellProcessor.execute("abcd", ANONYMOUS_CSVCONTEXT), is("abcd"));
        assertThat(cellProcessor.execute(" abc ", ANONYMOUS_CSVCONTEXT), is(" abc "));
        
        // length less than maxLength(5)
        try {
            cellProcessor.execute("abcdef", ANONYMOUS_CSVCONTEXT);
            fail();
        } catch(SuperCsvConstraintViolationException e) {
            CellProcessor errorProcessor = e.getProcessor();
            assertThat(errorProcessor, is(instanceOf(MaxLength.class)));
        }
        
    }
    
    @Test
    public void testBuildOutput_maxLength() {
        Annotation[] annos = getAnnotations(TestCsv.class, "str_maxLength");
        CellProcessor cellProcessor = builder.buildOutputCellProcessor(String.class, annos, false);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        assertThat(cellProcessor, hasCellProcessor(MaxLength.class));
        
        assertThat(cellProcessor.execute("abcde", ANONYMOUS_CSVCONTEXT), is("abcde"));
        assertThat(cellProcessor.execute("abcd", ANONYMOUS_CSVCONTEXT), is("abcd"));
        assertThat(cellProcessor.execute(" abc ", ANONYMOUS_CSVCONTEXT), is(" abc "));
        
        // length greater than maxLength(5)
        try {
            cellProcessor.execute("abcdef", ANONYMOUS_CSVCONTEXT);
            fail();
        } catch(SuperCsvConstraintViolationException e) {
            CellProcessor errorProcessor = e.getProcessor();
            assertThat(errorProcessor, is(instanceOf(MaxLength.class)));
        }
        
    }
    
    @Test
    public void testBuildOutput_maxLength_ignoreValidation() {
        Annotation[] annos = getAnnotations(TestCsv.class, "str_maxLength");
        CellProcessor cellProcessor = builder.buildOutputCellProcessor(String.class, annos, true);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        assertThat(cellProcessor, not(hasCellProcessor(MaxLength.class)));
        
        // length greater than maxLength(5)
        assertThat(cellProcessor.execute("abcdef", ANONYMOUS_CSVCONTEXT), is("abcdef"));
        
    }
    
    @Test
    public void testBuildInput_length() {
        Annotation[] annos = getAnnotations(TestCsv.class, "str_minMaxLength");
        CellProcessor cellProcessor = builder.buildInputCellProcessor(String.class, annos);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        assertThat(cellProcessor, hasCellProcessor(Length.class));
        
        assertThat(cellProcessor.execute("ab", ANONYMOUS_CSVCONTEXT), is("ab"));
        assertThat(cellProcessor.execute("abc", ANONYMOUS_CSVCONTEXT), is("abc"));
        
        assertThat(cellProcessor.execute("abcde", ANONYMOUS_CSVCONTEXT), is("abcde"));
        assertThat(cellProcessor.execute("abcd", ANONYMOUS_CSVCONTEXT), is("abcd"));
        assertThat(cellProcessor.execute(" abc ", ANONYMOUS_CSVCONTEXT), is(" abc "));
        
        // length less than length(2, 5)
        try {
            cellProcessor.execute("a", ANONYMOUS_CSVCONTEXT);
            fail();
        } catch(SuperCsvConstraintViolationException e) {
            CellProcessor errorProcessor = e.getProcessor();
            assertThat(errorProcessor, is(instanceOf(Length.class)));
        }
        
        // length less than length(2, 5)
        try {
            cellProcessor.execute("abcdef", ANONYMOUS_CSVCONTEXT);
            fail();
        } catch(SuperCsvConstraintViolationException e) {
            CellProcessor errorProcessor = e.getProcessor();
            assertThat(errorProcessor, is(instanceOf(Length.class)));
        }
        
    }
    
    @Test
    public void testBuildOutput_length() {
        Annotation[] annos = getAnnotations(TestCsv.class, "str_minMaxLength");
        CellProcessor cellProcessor = builder.buildOutputCellProcessor(String.class, annos, false);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        assertThat(cellProcessor, hasCellProcessor(Length.class));
        
        assertThat(cellProcessor.execute("ab", ANONYMOUS_CSVCONTEXT), is("ab"));
        assertThat(cellProcessor.execute("abc", ANONYMOUS_CSVCONTEXT), is("abc"));
        
        assertThat(cellProcessor.execute("abcde", ANONYMOUS_CSVCONTEXT), is("abcde"));
        assertThat(cellProcessor.execute("abcd", ANONYMOUS_CSVCONTEXT), is("abcd"));
        assertThat(cellProcessor.execute(" abc ", ANONYMOUS_CSVCONTEXT), is(" abc "));
        
        // length less than length(2, 5)
        try {
            cellProcessor.execute("a", ANONYMOUS_CSVCONTEXT);
            fail();
        } catch(SuperCsvConstraintViolationException e) {
            CellProcessor errorProcessor = e.getProcessor();
            assertThat(errorProcessor, is(instanceOf(Length.class)));
        }
        
        // length greater than length(2, 5)
        try {
            cellProcessor.execute("abcdef", ANONYMOUS_CSVCONTEXT);
            fail();
        } catch(SuperCsvConstraintViolationException e) {
            CellProcessor errorProcessor = e.getProcessor();
            assertThat(errorProcessor, is(instanceOf(Length.class)));
        }
        
    }
    
    @Test
    public void testBuildOutput_length_ignoreValidation() {
        Annotation[] annos = getAnnotations(TestCsv.class, "str_minMaxLength");
        CellProcessor cellProcessor = builder.buildOutputCellProcessor(String.class, annos, true);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        assertThat(cellProcessor, not(hasCellProcessor(Length.class)));
        
        // length less than length(2, 5)
        assertThat(cellProcessor.execute("a", ANONYMOUS_CSVCONTEXT), is("a"));
        
        // length greater than length(2, 5)
        assertThat(cellProcessor.execute("abcdef", ANONYMOUS_CSVCONTEXT), is("abcdef"));
        
    }
    
    @Test
    public void testBuildInput_exactLength() {
        Annotation[] annos = getAnnotations(TestCsv.class, "str_exactLength");
        CellProcessor cellProcessor = builder.buildInputCellProcessor(String.class, annos);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        assertThat(cellProcessor, hasCellProcessor(Strlen.class));
        
        assertThat(cellProcessor.execute("abcde", ANONYMOUS_CSVCONTEXT), is("abcde"));
        assertThat(cellProcessor.execute("ab", ANONYMOUS_CSVCONTEXT), is("ab"));
        
        // length equals exactLength(5)
        try {
            cellProcessor.execute("abcd", ANONYMOUS_CSVCONTEXT);
            fail();
        } catch(SuperCsvConstraintViolationException e) {
            CellProcessor errorProcessor = e.getProcessor();
            assertThat(errorProcessor, is(instanceOf(Strlen.class)));
        }
        
        // length equals exactLength(5)
        try {
            cellProcessor.execute("abcdef", ANONYMOUS_CSVCONTEXT);
            fail();
        } catch(SuperCsvConstraintViolationException e) {
            CellProcessor errorProcessor = e.getProcessor();
            assertThat(errorProcessor, is(instanceOf(Strlen.class)));
        }
        
    }
    
    @Test
    public void testBuildOutput_exactLength() {
        Annotation[] annos = getAnnotations(TestCsv.class, "str_exactLength");
        CellProcessor cellProcessor = builder.buildOutputCellProcessor(String.class, annos, false);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        assertThat(cellProcessor, hasCellProcessor(Strlen.class));
        
        assertThat(cellProcessor.execute("abcde", ANONYMOUS_CSVCONTEXT), is("abcde"));
        assertThat(cellProcessor.execute("ab", ANONYMOUS_CSVCONTEXT), is("ab"));
        
        // length equals exactLength(5)
        try {
            cellProcessor.execute("abcd", ANONYMOUS_CSVCONTEXT);
            fail();
        } catch(SuperCsvConstraintViolationException e) {
            CellProcessor errorProcessor = e.getProcessor();
            assertThat(errorProcessor, is(instanceOf(Strlen.class)));
        }
        
        // length equals exactLength(5)
        try {
            cellProcessor.execute("abcdef", ANONYMOUS_CSVCONTEXT);
            fail();
        } catch(SuperCsvConstraintViolationException e) {
            CellProcessor errorProcessor = e.getProcessor();
            assertThat(errorProcessor, is(instanceOf(Strlen.class)));
        }
        
    }
    
    @Test
    public void testBuildOutput_exactLength_ignoreValidation() {
        Annotation[] annos = getAnnotations(TestCsv.class, "str_exactLength");
        CellProcessor cellProcessor = builder.buildOutputCellProcessor(String.class, annos, true);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        assertThat(cellProcessor, not(hasCellProcessor(Strlen.class)));
        
        // length equals exactLength(5)
        assertThat(cellProcessor.execute("abcd", ANONYMOUS_CSVCONTEXT), is("abcd"));
        assertThat(cellProcessor.execute("abcdef", ANONYMOUS_CSVCONTEXT), is("abcdef"));
        
    }
    
    @Test
    public void testBuildInput_regex() {
        Annotation[] annos = getAnnotations(TestCsv.class, "str_regex");
        CellProcessor cellProcessor = builder.buildInputCellProcessor(String.class, annos);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        assertThat(cellProcessor, hasCellProcessor(StrRegEx.class));
        
        assertThat(cellProcessor.execute("2005-10-12", ANONYMOUS_CSVCONTEXT), is("2005-10-12"));
        
        // wrong pattern
        try {
            cellProcessor.execute("05-10-12", ANONYMOUS_CSVCONTEXT);
            fail();
        } catch(SuperCsvConstraintViolationException e) {
            CellProcessor errorProcessor = e.getProcessor();
            assertThat(errorProcessor, is(instanceOf(StrRegEx.class)));
        }
        
    }
    
    @Test
    public void testBuildOutput_regex() {
        Annotation[] annos = getAnnotations(TestCsv.class, "str_regex");
        CellProcessor cellProcessor = builder.buildOutputCellProcessor(String.class, annos, false);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        assertThat(cellProcessor, hasCellProcessor(StrRegEx.class));
        
        assertThat(cellProcessor.execute("2005-10-12", ANONYMOUS_CSVCONTEXT), is("2005-10-12"));
        
        // wrong pattern
        try {
            cellProcessor.execute("05-10-12", ANONYMOUS_CSVCONTEXT);
            fail();
        } catch(SuperCsvConstraintViolationException e) {
            CellProcessor errorProcessor = e.getProcessor();
            assertThat(errorProcessor, is(instanceOf(StrRegEx.class)));
        }
        
    }
    
    @Test
    public void testBuildOutput_regex_ignoreValidation() {
        Annotation[] annos = getAnnotations(TestCsv.class, "str_regex");
        CellProcessor cellProcessor = builder.buildOutputCellProcessor(String.class, annos, true);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        assertThat(cellProcessor, not(hasCellProcessor(StrRegEx.class)));
        
        assertThat(cellProcessor.execute("2005-10-12", ANONYMOUS_CSVCONTEXT), is("2005-10-12"));
        assertThat(cellProcessor.execute("05-10-12", ANONYMOUS_CSVCONTEXT), is("05-10-12"));
        
    }
    
    @Test
    public void testBuildInput_forbid() {
        Annotation[] annos = getAnnotations(TestCsv.class, "str_forbid");
        CellProcessor cellProcessor = builder.buildInputCellProcessor(String.class, annos);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        assertThat(cellProcessor, hasCellProcessor(ForbidSubStr.class));
        
        assertThat(cellProcessor.execute("Hello!", ANONYMOUS_CSVCONTEXT), is("Hello!"));
        
        // wrong pattern
        try {
            cellProcessor.execute("Hello! abc", ANONYMOUS_CSVCONTEXT);
            fail();
        } catch(SuperCsvConstraintViolationException e) {
            CellProcessor errorProcessor = e.getProcessor();
            assertThat(errorProcessor, is(instanceOf(ForbidSubStr.class)));
        }
        
    }
    
    @Test
    public void testBuildOutput_forbid() {
        Annotation[] annos = getAnnotations(TestCsv.class, "str_forbid");
        CellProcessor cellProcessor = builder.buildOutputCellProcessor(String.class, annos, false);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        assertThat(cellProcessor, hasCellProcessor(ForbidSubStr.class));
        
        assertThat(cellProcessor.execute("Hello!", ANONYMOUS_CSVCONTEXT), is("Hello!"));
        
        // wrong pattern
        try {
            cellProcessor.execute("Hello! abc", ANONYMOUS_CSVCONTEXT);
            fail();
        } catch(SuperCsvConstraintViolationException e) {
            CellProcessor errorProcessor = e.getProcessor();
            assertThat(errorProcessor, is(instanceOf(ForbidSubStr.class)));
        }
        
    }
    
    @Test
    public void testBuildOutput_forbid_ignoreValidation() {
        Annotation[] annos = getAnnotations(TestCsv.class, "str_forbid");
        CellProcessor cellProcessor = builder.buildOutputCellProcessor(String.class, annos, true);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        assertThat(cellProcessor, not(hasCellProcessor(ForbidSubStr.class)));
        
        assertThat(cellProcessor.execute("Hello! abc", ANONYMOUS_CSVCONTEXT), is("Hello! abc"));
        
    }
    
    @Test
    public void testBuildInput_contain() {
        Annotation[] annos = getAnnotations(TestCsv.class, "str_contain");
        CellProcessor cellProcessor = builder.buildInputCellProcessor(String.class, annos);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        assertThat(cellProcessor, hasCellProcessor(RequireSubStr.class));
        
        assertThat(cellProcessor.execute("Hello world!", ANONYMOUS_CSVCONTEXT), is("Hello world!"));
        
        // wrong pattern
        try {
            cellProcessor.execute("Hello!", ANONYMOUS_CSVCONTEXT);
            fail();
        } catch(SuperCsvConstraintViolationException e) {
            CellProcessor errorProcessor = e.getProcessor();
            assertThat(errorProcessor, is(instanceOf(RequireSubStr.class)));
        }
        
    }
    
    @Test
    public void testBuildOutput_contain() {
        Annotation[] annos = getAnnotations(TestCsv.class, "str_contain");
        CellProcessor cellProcessor = builder.buildOutputCellProcessor(String.class, annos, false);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        assertThat(cellProcessor, hasCellProcessor(RequireSubStr.class));
        
        assertThat(cellProcessor.execute("Hello world!", ANONYMOUS_CSVCONTEXT), is("Hello world!"));
        
        // wrong pattern
        try {
            cellProcessor.execute("Hello!", ANONYMOUS_CSVCONTEXT);
            fail();
        } catch(SuperCsvConstraintViolationException e) {
            CellProcessor errorProcessor = e.getProcessor();
            assertThat(errorProcessor, is(instanceOf(RequireSubStr.class)));
        }
        
    }
    
    @Test
    public void testBuildOutput_contain_ignoreValidation() {
        Annotation[] annos = getAnnotations(TestCsv.class, "str_contain");
        CellProcessor cellProcessor = builder.buildOutputCellProcessor(String.class, annos, true);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        assertThat(cellProcessor, not(hasCellProcessor(RequireSubStr.class)));
        
        assertThat(cellProcessor.execute("Hello!", ANONYMOUS_CSVCONTEXT), is("Hello!"));
        
    }
    
    @Test
    public void testBuildInput_notEmpty() {
        Annotation[] annos = getAnnotations(TestCsv.class, "str_notEmpty");
        CellProcessor cellProcessor = builder.buildInputCellProcessor(String.class, annos);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        assertThat(cellProcessor, hasCellProcessor(StrNotNullOrEmpty.class));
        
        assertThat(cellProcessor.execute("abc", ANONYMOUS_CSVCONTEXT), is("abc"));
        
        // wrong pattern
        try {
            cellProcessor.execute("", ANONYMOUS_CSVCONTEXT);
            fail();
        } catch(SuperCsvConstraintViolationException e) {
            CellProcessor errorProcessor = e.getProcessor();
            assertThat(errorProcessor, is(instanceOf(StrNotNullOrEmpty.class)));
        }
        
        // wrong pattern
        try {
            cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT);
            fail();
        } catch(SuperCsvConstraintViolationException e) {
            CellProcessor errorProcessor = e.getProcessor();
            assertThat(errorProcessor, is(instanceOf(NotNull.class)));
        }
        
    }
    
    @Test
    public void testBuildOutput_notEmpty() {
        Annotation[] annos = getAnnotations(TestCsv.class, "str_notEmpty");
        CellProcessor cellProcessor = builder.buildOutputCellProcessor(String.class, annos, false);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        assertThat(cellProcessor, hasCellProcessor(StrNotNullOrEmpty.class));
        
        assertThat(cellProcessor.execute("abc", ANONYMOUS_CSVCONTEXT), is("abc"));
        
        // wrong pattern
        try {
            cellProcessor.execute("", ANONYMOUS_CSVCONTEXT);
            fail();
        } catch(SuperCsvConstraintViolationException e) {
            CellProcessor errorProcessor = e.getProcessor();
            assertThat(errorProcessor, is(instanceOf(StrNotNullOrEmpty.class)));
        }
        
        // wrong pattern
        try {
            cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT);
            fail();
        } catch(SuperCsvConstraintViolationException e) {
            CellProcessor errorProcessor = e.getProcessor();
            assertThat(errorProcessor, is(instanceOf(NotNull.class)));
        }
        
    }
    
    @Test
    public void testBuildOutput_notEmpty_ignoreValidation() {
        Annotation[] annos = getAnnotations(TestCsv.class, "str_notEmpty");
        CellProcessor cellProcessor = builder.buildOutputCellProcessor(String.class, annos, true);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        assertThat(cellProcessor, not(hasCellProcessor(StrNotNullOrEmpty.class)));
        
        assertThat(cellProcessor.execute("abc", ANONYMOUS_CSVCONTEXT), is("abc"));
        assertThat(cellProcessor.execute("", ANONYMOUS_CSVCONTEXT), is(""));
        
    }
    
    @Test
    public void testBuildInput_combine2() {
        Annotation[] annos = getAnnotations(TestCsv.class, "str_combine2");
        CellProcessor cellProcessor = builder.buildInputCellProcessor(String.class, annos);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        
        assertThat(cellProcessor.execute("  Hello! world!  ", ANONYMOUS_CSVCONTEXT), is("Hello! world!"));
        
        // wrong pattern
        try {
            cellProcessor.execute("", ANONYMOUS_CSVCONTEXT);
            fail();
        } catch(SuperCsvConstraintViolationException e) {
            CellProcessor errorProcessor = e.getProcessor();
            assertThat(errorProcessor, is(instanceOf(StrNotNullOrEmpty.class)));
        }
        
        // wrong pattern
        try {
            cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT);
            fail();
        } catch(SuperCsvConstraintViolationException e) {
            CellProcessor errorProcessor = e.getProcessor();
            assertThat(errorProcessor, is(instanceOf(NotNull.class)));
        }
        
    }
    
}

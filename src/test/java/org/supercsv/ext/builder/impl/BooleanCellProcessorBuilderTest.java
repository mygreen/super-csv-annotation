package org.supercsv.ext.builder.impl;

import static org.junit.Assert.*;
import static org.supercsv.ext.tool.TestUtils.*;
import static org.hamcrest.Matchers.*;
import static org.supercsv.ext.tool.HasCellProcessor.*;

import java.lang.annotation.Annotation;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.supercsv.cellprocessor.ConvertNullTo;
import org.supercsv.cellprocessor.FmtBool;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.constraint.Equals;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.constraint.Unique;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.exception.SuperCsvCellProcessorException;
import org.supercsv.exception.SuperCsvConstraintViolationException;
import org.supercsv.ext.annotation.CsvBean;
import org.supercsv.ext.annotation.CsvBooleanConverter;
import org.supercsv.ext.annotation.CsvColumn;
import org.supercsv.ext.cellprocessor.ParseBoolean;
import org.supercsv.ext.cellprocessor.Trim;
import org.supercsv.ext.exception.SuperCsvInvalidAnnotationException;

/**
 * Test the {@link BooleanCellProcessorBuilder} CellProcessor.
 * 
 * @since 1.2
 * @author T.TSUCHIE
 *
 */
@RunWith(Enclosed.class)
public class BooleanCellProcessorBuilderTest {
    
    /**
     * Tests for wrapper integer.
     *
     */
    public static class BooleanTest {
    
        @Rule
        public TestName name = new TestName();
        
        private BooleanCellProcessorBuilder builder;
        
        /**
         * Sets up the processor for the test using Combinations
         */
        @Before
        public void setUp() {
            builder = new BooleanCellProcessorBuilder();
        }
        
        @CsvBean
        private static class TestCsv {
            
            @CsvColumn(position=0)
            Boolean boolean_default;
            
            @CsvColumn(position=1, optional=true)
            Boolean boolean_optional;
            
            @CsvColumn(position=2, trim=true)
            Boolean boolean_trim;
            
            @CsvColumn(position=3, inputDefaultValue="yes", outputDefaultValue="ok")
            Boolean boolean_defaultValue;
            
            @CsvColumn(position=4, inputDefaultValue="abc")
            Boolean boolean_defaultValue_invalid;
            
            @CsvColumn(position=5, inputDefaultValue="no")
            Boolean boolean_defaultValue_false;
            
            @CsvColumn(position=6, inputDefaultValue="NO")
            @CsvBooleanConverter(ignoreCase=true)
            Boolean boolean_defaultValue_false_ignoreCase;
            
            @CsvColumn(position=7, inputDefaultValue="aaa")
            @CsvBooleanConverter(failToFalse=true)
            Boolean boolean_defaultValue_failToFalse;
            
            @CsvColumn(position=8, equalsValue="yes")
            Boolean boolean_equalsValue;
            
            @CsvColumn(position=9, unique=true)
            Boolean boolean_unique;
            
            @CsvColumn(position=10, optional=true, trim=true, inputDefaultValue="yes", outputDefaultValue="ok", equalsValue="yes", unique=true)
            Boolean boolean_combine1;
            
            @CsvColumn(position=11)
            @CsvBooleanConverter(inputTrueValue={"○", "レ"}, inputFalseValue={"×", "ー"})
            Boolean boolean_inputValue;
            
            @CsvColumn(position=12)
            @CsvBooleanConverter(outputTrueValue="○", outputFalseValue="×")
            Boolean boolean_outputValue;
            
            @CsvColumn(position=13)
            @CsvBooleanConverter(ignoreCase=true)
            Boolean boolean_ignoreCase;
            
            @CsvColumn(position=14)
            @CsvBooleanConverter(failToFalse=true)
            Boolean boolean_failToFalse;
            
            @CsvColumn(position=15, optional=true, trim=true, inputDefaultValue="OK", outputDefaultValue="CANCEL")
            @CsvBooleanConverter(inputTrueValue={"ok", "○"}, inputFalseValue={"cancel", "×"}, outputTrueValue="Ok", outputFalseValue="Cancel", failToFalse=true, ignoreCase=true)
            Boolean boolean_combine2;
            
        }
        
        /**
         * Tests with default. (not grant convert annotation.)
         */
        @Test
        public void testBuildInput_default() {
            
            Annotation[] annos = getAnnotations(TestCsv.class, "boolean_default");
            CellProcessor cellProcessor = builder.buildInputCellProcessor(Boolean.class, annos);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, hasCellProcessor(NotNull.class));
            assertThat(cellProcessor, hasCellProcessor(ParseBoolean.class));
            
            assertThat(cellProcessor.execute("true", ANONYMOUS_CSVCONTEXT), is(Boolean.TRUE));
            assertThat(cellProcessor.execute("no", ANONYMOUS_CSVCONTEXT), is(Boolean.FALSE));
            
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
                assertThat(errorProcessor, is(instanceOf(ParseBoolean.class)));
            }
            
        }
        
        /**
         * Tests with default. (not grant convert annotation.)
         */
        @Test
        public void testBuildOutput_default() {
            
            Annotation[] annos = getAnnotations(TestCsv.class, "boolean_default");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(Boolean.class, annos, false);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            assertThat(cellProcessor, hasCellProcessor(NotNull.class));
            
            assertThat(cellProcessor.execute(Boolean.TRUE, ANONYMOUS_CSVCONTEXT), is("true"));
            assertThat(cellProcessor.execute(Boolean.FALSE, ANONYMOUS_CSVCONTEXT), is("false"));
            
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
            
            Annotation[] annos = getAnnotations(TestCsv.class, "boolean_default");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(Boolean.class, annos, true);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, hasCellProcessor(NotNull.class));
            
            assertThat(cellProcessor.execute(Boolean.TRUE, ANONYMOUS_CSVCONTEXT), is("true"));
            assertThat(cellProcessor.execute(Boolean.FALSE, ANONYMOUS_CSVCONTEXT), is("false"));
            
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
            
            Annotation[] annos = getAnnotations(TestCsv.class, "boolean_optional");
            CellProcessor cellProcessor = builder.buildInputCellProcessor(Boolean.class, annos);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            assertThat(cellProcessor, hasCellProcessor(Optional.class));
            
            assertThat(cellProcessor.execute("true", ANONYMOUS_CSVCONTEXT), is(Boolean.TRUE));
            
            // null input
            assertThat(cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT), is(nullValue()));
        }
        
        /**
         * Tests with optional. (not grant convert annotation.)
         */
        @Test
        public void testBuildOutput_optional() {
            
            Annotation[] annos = getAnnotations(TestCsv.class, "boolean_optional");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(Boolean.class, annos, false);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            assertThat(cellProcessor, hasCellProcessor(Optional.class));
            
            assertThat(cellProcessor.execute(Boolean.TRUE, ANONYMOUS_CSVCONTEXT), is("true"));
            
            // null input
            assertThat(cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT), is(nullValue()));
        }
        
        /**
         * Tests with optional. (not grant convert annotation.)
         */
        @Test
        public void testBuildOutput_optional_ignoreValidation() {
            
            Annotation[] annos = getAnnotations(TestCsv.class, "boolean_optional");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(Boolean.class, annos, true);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, hasCellProcessor(Optional.class));
            
            assertThat(cellProcessor.execute(Boolean.TRUE, ANONYMOUS_CSVCONTEXT), is("true"));
            
            // null input
            assertThat(cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT), is(nullValue()));
        }
        
        @Test
        public void testBuildInput_trim() {
            Annotation[] annos = getAnnotations(TestCsv.class, "boolean_trim");
            CellProcessor cellProcessor = builder.buildInputCellProcessor(Boolean.class, annos);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, hasCellProcessor(Trim.class));
            
            assertThat(cellProcessor.execute("  true ", ANONYMOUS_CSVCONTEXT), is(Boolean.TRUE));
        }
        
        @Test
        public void testBuildOutput_trim() {
            Annotation[] annos = getAnnotations(TestCsv.class, "boolean_trim");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(Boolean.class, annos, false);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            assertThat(cellProcessor, hasCellProcessor(Trim.class));
            
            assertThat(cellProcessor.execute(Boolean.TRUE, ANONYMOUS_CSVCONTEXT), is("true"));
        }
        
        @Test
        public void testBuildOutput_trim_ignoreValidation() {
            Annotation[] annos = getAnnotations(TestCsv.class, "boolean_trim");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(Boolean.class, annos, true);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            assertThat(cellProcessor, hasCellProcessor(Trim.class));
            
            assertThat(cellProcessor.execute(Boolean.TRUE, ANONYMOUS_CSVCONTEXT), is("true"));
        }
        
        @Test
        public void testBuildInput_defaultValue() {
            Annotation[] annos = getAnnotations(TestCsv.class, "boolean_defaultValue");
            CellProcessor cellProcessor = builder.buildInputCellProcessor(Boolean.class, annos);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, hasCellProcessor(ConvertNullTo.class));
            
            assertThat(cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT), is(Boolean.TRUE));
        }
        
        @Test
        public void testBuildOutput_defaultValue() {
            Annotation[] annos = getAnnotations(TestCsv.class, "boolean_defaultValue");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(Boolean.class, annos, false);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, hasCellProcessor(ConvertNullTo.class));
            
            assertThat(cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT), is("ok"));
        }
        
        @Test
        public void testBuildOutput_defaultValue_ignoreValidation() {
            Annotation[] annos = getAnnotations(TestCsv.class, "boolean_defaultValue");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(Boolean.class, annos, true);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, hasCellProcessor(ConvertNullTo.class));
            
            assertThat(cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT), is("ok"));
        }
        
        /**
         * Tests with default. (not grant convert annotation.)
         */
        @Test(expected=SuperCsvInvalidAnnotationException.class)
        public void testBuildInput_default_format_invalidAnnotation() {
            
            Annotation[] annos = getAnnotations(TestCsv.class, "boolean_defaultValue_invalid");
            CellProcessor cellProcessor = builder.buildInputCellProcessor(Boolean.class, annos);
            
            cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT);
            fail();
            
        }
        
        @Test
        public void testBuildInput_defaultValue_false() {
            
            Annotation[] annos = getAnnotations(TestCsv.class, "boolean_defaultValue_false");
            CellProcessor cellProcessor = builder.buildInputCellProcessor(Boolean.class, annos);
            
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, hasCellProcessor(ParseBoolean.class));
            
            assertThat(cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT), is(Boolean.FALSE));
            
        }
        
        @Test
        public void testBuildInput_defaultValue_false_ignoreCase() {
            
            Annotation[] annos = getAnnotations(TestCsv.class, "boolean_defaultValue_false_ignoreCase");
            CellProcessor cellProcessor = builder.buildInputCellProcessor(Boolean.class, annos);
            
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, hasCellProcessor(ParseBoolean.class));
            
            assertThat(cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT), is(Boolean.FALSE));
            
        }
        
        @Test
        public void testBuildInput_defaultValue_failToFalse() {
            
            Annotation[] annos = getAnnotations(TestCsv.class, "boolean_defaultValue_failToFalse");
            CellProcessor cellProcessor = builder.buildInputCellProcessor(Boolean.class, annos);
            
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, hasCellProcessor(ParseBoolean.class));
            
            assertThat(cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT), is(Boolean.FALSE));
            
        }
        
        @Test
        public void testBuildInput_equalsValue() {
            Annotation[] annos = getAnnotations(TestCsv.class, "boolean_equalsValue");
            CellProcessor cellProcessor = builder.buildInputCellProcessor(Boolean.class, annos);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, hasCellProcessor(Equals.class));
            
            assertThat(cellProcessor.execute("yes", ANONYMOUS_CSVCONTEXT), is(Boolean.TRUE));
            
            // not quals input
            try {
                cellProcessor.execute("true", ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Equals.class)));
            }
        }
        
        @Test
        public void testBuildOutput_equalsValue() {
            Annotation[] annos = getAnnotations(TestCsv.class, "boolean_equalsValue");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(Boolean.class, annos, false);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, hasCellProcessor(Equals.class));
            
            assertThat(cellProcessor.execute(Boolean.TRUE, ANONYMOUS_CSVCONTEXT), is("true"));
            
            // not quals input
            try {
                cellProcessor.execute(Boolean.FALSE, ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Equals.class)));
            }
        }
        
        @Test
        public void testBuildOutput_equalsValue_ignoreValidation() {
            Annotation[] annos = getAnnotations(TestCsv.class, "boolean_equalsValue");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(Boolean.class, annos, true);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, not(hasCellProcessor(Equals.class)));
            
            assertThat(cellProcessor.execute(Boolean.TRUE, ANONYMOUS_CSVCONTEXT), is("true"));
            
            // not quals input
            assertThat(cellProcessor.execute(Boolean.FALSE, ANONYMOUS_CSVCONTEXT), is("false"));
            
        }
        
        @Test
        public void testBuildInput_unique() {
            Annotation[] annos = getAnnotations(TestCsv.class, "boolean_unique");
            CellProcessor cellProcessor = builder.buildInputCellProcessor(Boolean.class, annos);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, hasCellProcessor(Unique.class));
            
            assertThat(cellProcessor.execute("true", ANONYMOUS_CSVCONTEXT), is(Boolean.TRUE));
            assertThat(cellProcessor.execute("yes", ANONYMOUS_CSVCONTEXT), is(Boolean.TRUE));
            
            // not unique input
            try {
                cellProcessor.execute("true", ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Unique.class)));
            }
        }
        
        @Test
        public void testBuildOutput_unique() {
            Annotation[] annos = getAnnotations(TestCsv.class, "boolean_unique");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(Boolean.class, annos, false);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            assertThat(cellProcessor, hasCellProcessor(Unique.class));
            
            assertThat(cellProcessor.execute(Boolean.TRUE, ANONYMOUS_CSVCONTEXT), is("true"));
            assertThat(cellProcessor.execute(Boolean.FALSE, ANONYMOUS_CSVCONTEXT), is("false"));
            
            // not unique input
            try {
                cellProcessor.execute(Boolean.TRUE, ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Unique.class)));
            }
        }
        
        @Test
        public void testBuildOutput_unique_ignoreValidation() {
            Annotation[] annos = getAnnotations(TestCsv.class, "boolean_unique");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(Boolean.class, annos, true);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, not(hasCellProcessor(Unique.class)));
            
            assertThat(cellProcessor.execute(Boolean.TRUE, ANONYMOUS_CSVCONTEXT), is("true"));
            assertThat(cellProcessor.execute(Boolean.FALSE, ANONYMOUS_CSVCONTEXT), is("false"));
            assertThat(cellProcessor.execute(Boolean.TRUE, ANONYMOUS_CSVCONTEXT), is("true"));
            
        }
        
        @Test
        public void testBuildInput_combine1() {
            Annotation[] annos = getAnnotations(TestCsv.class, "boolean_combine1");
            CellProcessor cellProcessor = builder.buildInputCellProcessor(Boolean.class, annos);
            
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT), is(Boolean.TRUE));
            assertThat(cellProcessor.execute("  yes  ", ANONYMOUS_CSVCONTEXT), is(Boolean.TRUE));
            
            // not equals input
            try {
                cellProcessor.execute("true", ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Equals.class)));
            }
            
            // not unique input
            try {
                cellProcessor.execute("yes", ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Unique.class)));
            }
        }
        
        @Test
        public void testBuildOutput_combine1() {
            Annotation[] annos = getAnnotations(TestCsv.class, "boolean_combine1");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(Boolean.class, annos, false);
            
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT), is("ok"));
            assertThat(cellProcessor.execute(Boolean.TRUE, ANONYMOUS_CSVCONTEXT), is("true"));
            
            // not equals input
            try {
                cellProcessor.execute(Boolean.FALSE, ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Equals.class)));
            }
            
            // not unique input
            try {
                cellProcessor.execute(Boolean.TRUE, ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Unique.class)));
            }
        }
        
        @Test
        public void testBuildOutput_combine1_ignoreValidation() {
            Annotation[] annos = getAnnotations(TestCsv.class, "boolean_combine1");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(Boolean.class, annos, true);
            
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT), is("ok"));
            assertThat(cellProcessor.execute(Boolean.TRUE, ANONYMOUS_CSVCONTEXT), is("true"));
            
            // not equals input
            assertThat(cellProcessor.execute(Boolean.FALSE, ANONYMOUS_CSVCONTEXT), is("false"));
            
            // not unique input
            assertThat(cellProcessor.execute(Boolean.TRUE, ANONYMOUS_CSVCONTEXT), is("true"));
            
        }
        
        @Test
        public void testBuildInput_inputValue() {
            
            Annotation[] annos = getAnnotations(TestCsv.class, "boolean_inputValue");
            CellProcessor cellProcessor = builder.buildInputCellProcessor(Boolean.class, annos);
            
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, hasCellProcessor(ParseBoolean.class));
            
            assertThat(cellProcessor.execute("○", ANONYMOUS_CSVCONTEXT), is(Boolean.TRUE));
            assertThat(cellProcessor.execute("レ", ANONYMOUS_CSVCONTEXT), is(Boolean.TRUE));
            
            assertThat(cellProcessor.execute("×", ANONYMOUS_CSVCONTEXT), is(Boolean.FALSE));
            assertThat(cellProcessor.execute("ー", ANONYMOUS_CSVCONTEXT), is(Boolean.FALSE));
            
            // wrong pattern
            try {
                cellProcessor.execute("true", ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvCellProcessorException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(ParseBoolean.class)));
            }
        }
        
        @Test
        public void testBuildOutput_outputValue() {
            
            Annotation[] annos = getAnnotations(TestCsv.class, "boolean_outputValue");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(Boolean.class, annos, false);
            
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, hasCellProcessor(FmtBool.class));
            
            assertThat(cellProcessor.execute(Boolean.TRUE, ANONYMOUS_CSVCONTEXT), is("○"));
            assertThat(cellProcessor.execute(Boolean.FALSE, ANONYMOUS_CSVCONTEXT), is("×"));
            
        }
        
        @Test
        public void testBuildInput_ignoreCase() {
            
            Annotation[] annos = getAnnotations(TestCsv.class, "boolean_ignoreCase");
            CellProcessor cellProcessor = builder.buildInputCellProcessor(Boolean.class, annos);
            
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, hasCellProcessor(ParseBoolean.class));
            
            assertThat(cellProcessor.execute("TRUE", ANONYMOUS_CSVCONTEXT), is(Boolean.TRUE));
            assertThat(cellProcessor.execute("Yes", ANONYMOUS_CSVCONTEXT), is(Boolean.TRUE));
            
            assertThat(cellProcessor.execute("FALSE", ANONYMOUS_CSVCONTEXT), is(Boolean.FALSE));
            assertThat(cellProcessor.execute("No", ANONYMOUS_CSVCONTEXT), is(Boolean.FALSE));
            
            // wrong pattern
            try {
                cellProcessor.execute("aaaa", ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvCellProcessorException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(ParseBoolean.class)));
            }
        }
        
        @Test
        public void testBuildInput_failToFalse() {
            
            Annotation[] annos = getAnnotations(TestCsv.class, "boolean_failToFalse");
            CellProcessor cellProcessor = builder.buildInputCellProcessor(Boolean.class, annos);
            
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, hasCellProcessor(ParseBoolean.class));
            
            assertThat(cellProcessor.execute("true", ANONYMOUS_CSVCONTEXT), is(Boolean.TRUE));
            
            assertThat(cellProcessor.execute("false", ANONYMOUS_CSVCONTEXT), is(Boolean.FALSE));
            
            assertThat(cellProcessor.execute("aaa", ANONYMOUS_CSVCONTEXT), is(Boolean.FALSE));
        }
        
        @Test
        public void testBuildInput_combine2() {
            
            Annotation[] annos = getAnnotations(TestCsv.class, "boolean_combine2");
            CellProcessor cellProcessor = builder.buildInputCellProcessor(Boolean.class, annos);
            
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, hasCellProcessor(ParseBoolean.class));
            
            assertThat(cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT), is(Boolean.TRUE));
            
            assertThat(cellProcessor.execute("Ok", ANONYMOUS_CSVCONTEXT), is(Boolean.TRUE));
            assertThat(cellProcessor.execute("Cancel", ANONYMOUS_CSVCONTEXT), is(Boolean.FALSE));
            
            assertThat(cellProcessor.execute("  ○  ", ANONYMOUS_CSVCONTEXT), is(Boolean.TRUE));
            assertThat(cellProcessor.execute("  ×  ", ANONYMOUS_CSVCONTEXT), is(Boolean.FALSE));
            
            // wrong pattern
            assertThat(cellProcessor.execute("aaa", ANONYMOUS_CSVCONTEXT), is(Boolean.FALSE));
        }
        
        @Test
        public void testBuildOutput_combine2() {
            
            Annotation[] annos = getAnnotations(TestCsv.class, "boolean_combine2");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(Boolean.class, annos, false);
            
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, hasCellProcessor(FmtBool.class));
            
            assertThat(cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT), is("CANCEL"));
            
            assertThat(cellProcessor.execute(Boolean.TRUE, ANONYMOUS_CSVCONTEXT), is("Ok"));
            assertThat(cellProcessor.execute(Boolean.FALSE, ANONYMOUS_CSVCONTEXT), is("Cancel"));
            
        }
        
        @Test
        public void testBuildOutput_combine2_ignoreValidation() {
            
            Annotation[] annos = getAnnotations(TestCsv.class, "boolean_combine2");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(Boolean.class, annos, true);
            
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, hasCellProcessor(FmtBool.class));
            
            assertThat(cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT), is("CANCEL"));
            
            assertThat(cellProcessor.execute(Boolean.TRUE, ANONYMOUS_CSVCONTEXT), is("Ok"));
            assertThat(cellProcessor.execute(Boolean.FALSE, ANONYMOUS_CSVCONTEXT), is("Cancel"));
            
        }
        
    }
    
    /**
     * Tests for primitive boolean.
     *
     */
    public static class PrimitiveBooleanTest {
        
        @Rule
        public TestName name = new TestName();
        
        private BooleanCellProcessorBuilder builder;
        
        /**
         * Sets up the processor for the test using Combinations
         */
        @Before
        public void setUp() {
            builder = new BooleanCellProcessorBuilder();
        }
        
        @CsvBean
        private static class TestCsv {
            
            @CsvColumn(position=0)
            boolean boolean_default;
            
            @CsvColumn(position=1, optional=true)
            boolean boolean_optional;
            
            @CsvColumn(position=2, trim=true)
            boolean boolean_trim;
            
            @CsvColumn(position=3, inputDefaultValue="yes", outputDefaultValue="ok")
            boolean boolean_defaultValue;
            
            @CsvColumn(position=4, inputDefaultValue="abc")
            boolean boolean_defaultValue_invalid;
            
            @CsvColumn(position=5, inputDefaultValue="false")
            boolean boolean_defaultValue_false;
            
            @CsvColumn(position=6, inputDefaultValue="FALSE")
            @CsvBooleanConverter(ignoreCase=true)
            boolean boolean_defaultValue_false_ignoreCase;
            
            @CsvColumn(position=7, inputDefaultValue="aaa")
            @CsvBooleanConverter(failToFalse=true)
            boolean boolean_defaultValue_failToFalse;
            
            @CsvColumn(position=8, optional=true, trim=true, inputDefaultValue="yes", outputDefaultValue="ok", equalsValue="yes", unique=true)
            boolean boolean_combine1;
            
        }
        
        /**
         * Tests with default. (not grant convert annotation.)
         */
        @Test
        public void testBuildInput_default() {
            
            Annotation[] annos = getAnnotations(TestCsv.class, "boolean_default");
            CellProcessor cellProcessor = builder.buildInputCellProcessor(boolean.class, annos);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, hasCellProcessor(NotNull.class));
            assertThat(cellProcessor, hasCellProcessor(ParseBoolean.class));
            
            assertThat(cellProcessor.execute("true", ANONYMOUS_CSVCONTEXT), is(true));
            assertThat(cellProcessor.execute("no", ANONYMOUS_CSVCONTEXT), is(false));
            
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
                assertThat(errorProcessor, is(instanceOf(ParseBoolean.class)));
            }
            
        }
        
        /**
         * Tests with default. (not grant convert annotation.)
         */
        @Test
        public void testBuildOutput_default() {
            
            Annotation[] annos = getAnnotations(TestCsv.class, "boolean_default");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(boolean.class, annos, false);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            assertThat(cellProcessor, hasCellProcessor(NotNull.class));
            
            assertThat(cellProcessor.execute(true, ANONYMOUS_CSVCONTEXT), is("true"));
            assertThat(cellProcessor.execute(false, ANONYMOUS_CSVCONTEXT), is("false"));
            
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
            
            Annotation[] annos = getAnnotations(TestCsv.class, "boolean_optional");
            CellProcessor cellProcessor = builder.buildInputCellProcessor(boolean.class, annos);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            assertThat(cellProcessor, hasCellProcessor(Optional.class));
            
            assertThat(cellProcessor.execute("true", ANONYMOUS_CSVCONTEXT), is(true));
            assertThat(cellProcessor.execute("false", ANONYMOUS_CSVCONTEXT), is(false));
            
            // null input (primitive input)
            assertThat(cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT), is(false));
        }
        
        /**
         * Tests with optional. (not grant convert annotation.)
         */
        @Test
        public void testBuildOutput_optional() {
            
            Annotation[] annos = getAnnotations(TestCsv.class, "boolean_optional");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(boolean.class, annos, false);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            assertThat(cellProcessor, hasCellProcessor(Optional.class));
            
            assertThat(cellProcessor.execute(true, ANONYMOUS_CSVCONTEXT), is("true"));
            assertThat(cellProcessor.execute(false, ANONYMOUS_CSVCONTEXT), is("false"));
            
        }
        
        @Test
        public void testBuildInput_trim() {
            Annotation[] annos = getAnnotations(TestCsv.class, "boolean_trim");
            CellProcessor cellProcessor = builder.buildInputCellProcessor(boolean.class, annos);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, hasCellProcessor(Trim.class));
            
            assertThat(cellProcessor.execute("  true ", ANONYMOUS_CSVCONTEXT), is(true));
        }
        
        @Test
        public void testBuildOutput_trim() {
            Annotation[] annos = getAnnotations(TestCsv.class, "boolean_trim");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(boolean.class, annos, false);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            assertThat(cellProcessor, hasCellProcessor(Trim.class));
            
            assertThat(cellProcessor.execute(true, ANONYMOUS_CSVCONTEXT), is("true"));
        }
        
        @Test
        public void testBuildInput_defaultValue() {
            Annotation[] annos = getAnnotations(TestCsv.class, "boolean_defaultValue");
            CellProcessor cellProcessor = builder.buildInputCellProcessor(boolean.class, annos);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, hasCellProcessor(ConvertNullTo.class));
            
            assertThat(cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT), is(true));
        }
        
        @Test
        public void testBuildOutput_defaultValue() {
            Annotation[] annos = getAnnotations(TestCsv.class, "boolean_defaultValue");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(boolean.class, annos, false);
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, hasCellProcessor(ConvertNullTo.class));
            
            assertThat(cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT), is("ok"));
        }
        
        /**
         * Tests with default. (not grant convert annotation.)
         */
        @Test(expected=SuperCsvInvalidAnnotationException.class)
        public void testBuildInput_default_format_invalidAnnotation() {
            
            Annotation[] annos = getAnnotations(TestCsv.class, "boolean_defaultValue_invalid");
            CellProcessor cellProcessor = builder.buildInputCellProcessor(boolean.class, annos);
            
            cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT);
            fail();
            
        }
        
        @Test
        public void testBuildInput_defaultValue_false() {
            
            Annotation[] annos = getAnnotations(TestCsv.class, "boolean_defaultValue_false");
            CellProcessor cellProcessor = builder.buildInputCellProcessor(boolean.class, annos);
            
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, hasCellProcessor(ParseBoolean.class));
            
            assertThat(cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT), is(false));
            
        }
        
        @Test
        public void testBuildInput_defaultValue_false_ignoreCase() {
            
            Annotation[] annos = getAnnotations(TestCsv.class, "boolean_defaultValue_false_ignoreCase");
            CellProcessor cellProcessor = builder.buildInputCellProcessor(boolean.class, annos);
            
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, hasCellProcessor(ParseBoolean.class));
            
            assertThat(cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT), is(false));
            
        }
        
        @Test
        public void testBuildInput_defaultValue_failToFalse() {
            
            Annotation[] annos = getAnnotations(TestCsv.class, "boolean_defaultValue_failToFalse");
            CellProcessor cellProcessor = builder.buildInputCellProcessor(boolean.class, annos);
            
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor, hasCellProcessor(ParseBoolean.class));
            
            assertThat(cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT), is(false));
            
        }
        
        @Test
        public void testBuildInput_combine1() {
            Annotation[] annos = getAnnotations(TestCsv.class, "boolean_combine1");
            CellProcessor cellProcessor = builder.buildInputCellProcessor(boolean.class, annos);
            
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT), is(true));
            assertThat(cellProcessor.execute("  yes  ", ANONYMOUS_CSVCONTEXT), is(true));
            
            // not equals input
            try {
                cellProcessor.execute("true", ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Equals.class)));
            }
            
            // not unique input
            try {
                cellProcessor.execute("yes", ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Unique.class)));
            }
        }
        
        @Test
        public void testBuildOutput_combine1() {
            Annotation[] annos = getAnnotations(TestCsv.class, "boolean_combine1");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(boolean.class, annos, false);
            
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT), is("ok"));
            assertThat(cellProcessor.execute(true, ANONYMOUS_CSVCONTEXT), is("true"));
            
            // not equals input
            try {
                cellProcessor.execute(false, ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Equals.class)));
            }
            
            // not unique input
            try {
                cellProcessor.execute(true, ANONYMOUS_CSVCONTEXT);
                fail();
            } catch(SuperCsvConstraintViolationException e) {
                CellProcessor errorProcessor = e.getProcessor();
                assertThat(errorProcessor, is(instanceOf(Unique.class)));
            }
        }
        
        @Test
        public void testBuildOutput_combine1_ignoreValidation() {
            Annotation[] annos = getAnnotations(TestCsv.class, "boolean_combine1");
            CellProcessor cellProcessor = builder.buildOutputCellProcessor(boolean.class, annos, true);
            
            printCellProcessorChain(cellProcessor, name.getMethodName());
            
            assertThat(cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT), is("ok"));
            assertThat(cellProcessor.execute(true, ANONYMOUS_CSVCONTEXT), is("true"));
            
            // not equals input
            assertThat(cellProcessor.execute(false, ANONYMOUS_CSVCONTEXT), is("false"));
            
            // not unique input
            assertThat(cellProcessor.execute(true, ANONYMOUS_CSVCONTEXT), is("true"));
            
        }
        
    }
}

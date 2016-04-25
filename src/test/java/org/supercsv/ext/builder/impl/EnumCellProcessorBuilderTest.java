package org.supercsv.ext.builder.impl;

import static org.junit.Assert.*;
import static org.supercsv.ext.tool.TestUtils.*;

import java.lang.annotation.Annotation;

import static org.hamcrest.Matchers.*;
import static org.supercsv.ext.tool.HasCellProcessor.*;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
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
import org.supercsv.ext.annotation.CsvEnumConverter;
import org.supercsv.ext.cellprocessor.FormatEnum;
import org.supercsv.ext.cellprocessor.ParseEnum;
import org.supercsv.ext.cellprocessor.Trim;
import org.supercsv.ext.exception.SuperCsvInvalidAnnotationException;

/**
 * 
 * Test the {@link EnumCellProcessorBuilder} CellProcessor.
 * 
 * @since 1.2
 * @author T.TSUCHIE
 *
 */
public class EnumCellProcessorBuilderTest {
    
    @Rule
    public TestName name = new TestName();
    
    private EnumCellProcessorBuilder<TestEnum> builder;
    
    /**
     * Sets up the processor for the test using Combinations
     */
    @Before
    public void setUp() {
        builder = new EnumCellProcessorBuilder<>();
    }
    
    public enum TestEnum {
        Red("赤(Red)"), Blue("青(Blue)"), Yellow("黄(Yellow)"), Green("緑(Green)");
        
        final String aliasName;
        
        private TestEnum(String aliasName) {
            this.aliasName = aliasName;
        }
        
        public String aliasName() {
            return aliasName;
        }
    }
    
    private static final String TEST_VALUE_METHOD_NAME = "aliasName";
    
    private static final TestEnum TEST_VALUE_1_OBJ = TestEnum.Red;
    private static final String TEST_VALUE_1_STR_NORMAL = "Red";
    private static final String TEST_VALUE_1_STR_ALIAS = "赤(Red)";
    
    private static final TestEnum TEST_VALUE_2_OBJ = TestEnum.Blue;
    private static final String TEST_VALUE_2_STR_NORMAL = "Blue";
    private static final String TEST_VALUE_2_STR_ALIAS = "青(Blue)";
    
    private static final TestEnum TEST_VALUE_INPUT_DEFAULT_OBJ = TestEnum.Yellow;
    private static final String TEST_VALUE_INPUT_DEFAULT_STR_NORMAL = "Yellow";
    private static final String TEST_VALUE_INPUT_DEFAULT_STR_ALIAS = "黄(Yellow)";
    
    private static final TestEnum TEST_VALUE_OUTPUT_DEFAULT_OBJ = TestEnum.Green;
    private static final String TEST_VALUE_OUTPUT_DEFAULT_STR_NORMAL = "Green";
    private static final String TEST_VALUE_OUTPUT_DEFAULT_STR_ALIAS = "緑(Green)";
    
    @CsvBean
    private static class TestCsv {
        
        @CsvColumn(position=0)
        TestEnum enum_default;
        
        @CsvColumn(position=1, optional=true)
        TestEnum enum_optional;
        
        @CsvColumn(position=2, trim=true)
        TestEnum enum_trim;
        
        @CsvColumn(position=3, inputDefaultValue=TEST_VALUE_INPUT_DEFAULT_STR_NORMAL, outputDefaultValue=TEST_VALUE_OUTPUT_DEFAULT_STR_NORMAL)
        TestEnum enum_defaultValue;
        
        @CsvColumn(position=4, inputDefaultValue="abc")
        TestEnum enum_defaultValue_invalid;
        
        @CsvColumn(position=5, inputDefaultValue=TEST_VALUE_INPUT_DEFAULT_STR_NORMAL)
        @CsvEnumConverter(ignoreCase=true)
        TestEnum enum_default_ignoreCase;
        
        @CsvColumn(position=6, inputDefaultValue=TEST_VALUE_INPUT_DEFAULT_STR_ALIAS)
        @CsvEnumConverter(valueMethodName=TEST_VALUE_METHOD_NAME)
        TestEnum enum_default_valueMethod;
        
        @CsvColumn(position=7, inputDefaultValue=TEST_VALUE_INPUT_DEFAULT_STR_ALIAS)
        @CsvEnumConverter(valueMethodName="sample")
        TestEnum enum_default_valueMethod_wrongName;
        
        @CsvColumn(position=8, inputDefaultValue="黄(yELLOw)")
        @CsvEnumConverter(valueMethodName=TEST_VALUE_METHOD_NAME, ignoreCase=true)
        TestEnum enum_default_valueMethod_ignoreCase;
        
        @CsvColumn(position=9, equalsValue=TEST_VALUE_1_STR_NORMAL)
        TestEnum enum_equalsValue;
        
        @CsvColumn(position=10, equalsValue=TEST_VALUE_1_STR_ALIAS)
        @CsvEnumConverter(valueMethodName=TEST_VALUE_METHOD_NAME)
        TestEnum enum_equalsValue_valueMethod;
        
        @CsvColumn(position=11, unique=true)
        TestEnum enum_unique;
        
        @CsvColumn(position=12, optional=true, trim=true,
                inputDefaultValue=TEST_VALUE_INPUT_DEFAULT_STR_NORMAL, outputDefaultValue=TEST_VALUE_OUTPUT_DEFAULT_STR_NORMAL,
                equalsValue=TEST_VALUE_1_STR_NORMAL, unique=true)
        TestEnum enum_combine1;
        
        @CsvColumn(position=13)
        @CsvEnumConverter(ignoreCase=true)
        TestEnum enum_ignoreCase;
        
        @CsvColumn(position=13)
        @CsvEnumConverter(valueMethodName=TEST_VALUE_METHOD_NAME)
        TestEnum enum_valueMethod;
        
        @CsvColumn(position=13)
        @CsvEnumConverter(valueMethodName=TEST_VALUE_METHOD_NAME, ignoreCase=true)
        TestEnum enum_valueMethod_ignoreCase;
        
        @CsvColumn(position=14, optional=true, trim=true,
                inputDefaultValue=TEST_VALUE_INPUT_DEFAULT_STR_ALIAS, outputDefaultValue=TEST_VALUE_OUTPUT_DEFAULT_STR_ALIAS)
        @CsvEnumConverter(ignoreCase=true, valueMethodName=TEST_VALUE_METHOD_NAME)
        TestEnum enum_combine2;
    }
    
    /**
     * Tests with default. (not grant convert annotation.)
     */
    @Test
    public void testBuildInput_default() {
        
        Annotation[] annos = getAnnotations(TestCsv.class, "enum_default");
        CellProcessor cellProcessor = builder.buildInputCellProcessor(TestEnum.class, annos);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        
        assertThat(cellProcessor, hasCellProcessor(NotNull.class));
        assertThat(cellProcessor, hasCellProcessor(ParseEnum.class));
        
        assertThat(cellProcessor.execute(TEST_VALUE_1_STR_NORMAL, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_1_OBJ));
        assertThat(cellProcessor.execute("Blue", ANONYMOUS_CSVCONTEXT), is(TestEnum.Blue));
        
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
            assertThat(errorProcessor, is(instanceOf(ParseEnum.class)));
        }
        
    }
    
    /**
     * Tests with default. (not grant convert annotation.)
     */
    @Test
    public void testBuildOutput_default() {
        
        Annotation[] annos = getAnnotations(TestCsv.class, "enum_default");
        CellProcessor cellProcessor = builder.buildOutputCellProcessor(TestEnum.class, annos, false);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        
        assertThat(cellProcessor, hasCellProcessor(NotNull.class));
        
        assertThat(cellProcessor.execute(TEST_VALUE_1_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_1_OBJ));
        assertThat(cellProcessor.execute(TEST_VALUE_2_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_2_OBJ));
        
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
        
        Annotation[] annos = getAnnotations(TestCsv.class, "enum_default");
        CellProcessor cellProcessor = builder.buildOutputCellProcessor(TestEnum.class, annos, true);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        
        assertThat(cellProcessor, hasCellProcessor(NotNull.class));
        
        assertThat(cellProcessor.execute(TEST_VALUE_1_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_1_OBJ));
        assertThat(cellProcessor.execute(TEST_VALUE_2_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_2_OBJ));
        
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
        
        Annotation[] annos = getAnnotations(TestCsv.class, "enum_optional");
        CellProcessor cellProcessor = builder.buildInputCellProcessor(TestEnum.class, annos);
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
        
        Annotation[] annos = getAnnotations(TestCsv.class, "enum_optional");
        CellProcessor cellProcessor = builder.buildOutputCellProcessor(TestEnum.class, annos, false);
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
        
        Annotation[] annos = getAnnotations(TestCsv.class, "enum_optional");
        CellProcessor cellProcessor = builder.buildOutputCellProcessor(TestEnum.class, annos, true);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        
        assertThat(cellProcessor, hasCellProcessor(Optional.class));
        
        assertThat(cellProcessor.execute(TEST_VALUE_1_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_1_OBJ));
        
        // null input
        assertThat(cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT), is(nullValue()));
    }
    
    @Test
    public void testBuildInput_trim() {
        Annotation[] annos = getAnnotations(TestCsv.class, "enum_trim");
        CellProcessor cellProcessor = builder.buildInputCellProcessor(TestEnum.class, annos);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        
        assertThat(cellProcessor, hasCellProcessor(Trim.class));
        
        assertThat(cellProcessor.execute("  " + TEST_VALUE_1_STR_NORMAL + " ", ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_1_OBJ));
    }
    
    @Test
    public void testBuildOutput_trim() {
        Annotation[] annos = getAnnotations(TestCsv.class, "enum_trim");
        CellProcessor cellProcessor = builder.buildOutputCellProcessor(TestEnum.class, annos, false);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        
        assertThat(cellProcessor, hasCellProcessor(Trim.class));
        
        assertThat(cellProcessor.execute(TEST_VALUE_1_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_1_STR_NORMAL));
        
    }
    
    @Test
    public void testBuildOutput_trim_ignoreValidation() {
        Annotation[] annos = getAnnotations(TestCsv.class, "enum_trim");
        CellProcessor cellProcessor = builder.buildOutputCellProcessor(TestEnum.class, annos, true);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        
        assertThat(cellProcessor, hasCellProcessor(Trim.class));
        
        assertThat(cellProcessor.execute(TEST_VALUE_1_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_1_STR_NORMAL));
        
    }
    
    @Test
    public void testBuildInput_defaultValue() {
        Annotation[] annos = getAnnotations(TestCsv.class, "enum_defaultValue");
        CellProcessor cellProcessor = builder.buildInputCellProcessor(TestEnum.class, annos);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        
        assertThat(cellProcessor, hasCellProcessor(ConvertNullTo.class));
        
        assertThat(cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_INPUT_DEFAULT_OBJ));
    }
    
    @Test
    public void testBuildOutput_defaultValue() {
        Annotation[] annos = getAnnotations(TestCsv.class, "enum_defaultValue");
        CellProcessor cellProcessor = builder.buildOutputCellProcessor(TestEnum.class, annos, false);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        
        assertThat(cellProcessor, hasCellProcessor(ConvertNullTo.class));
        
        assertThat(cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_OUTPUT_DEFAULT_STR_NORMAL));
    }
    
    @Test
    public void testBuildOutput_defaultValue_ignoreValidation() {
        Annotation[] annos = getAnnotations(TestCsv.class, "enum_defaultValue");
        CellProcessor cellProcessor = builder.buildOutputCellProcessor(TestEnum.class, annos, true);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        
        assertThat(cellProcessor, hasCellProcessor(ConvertNullTo.class));
        
        assertThat(cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_OUTPUT_DEFAULT_STR_NORMAL));
    }
    
    /**
     * Tests with default. (not grant convert annotation.)
     */
    @Test(expected=SuperCsvInvalidAnnotationException.class)
    public void testBuildInput_default_format_invalidAnnotation() {
        
        Annotation[] annos = getAnnotations(TestCsv.class, "enum_defaultValue_invalid");
        CellProcessor cellProcessor = builder.buildInputCellProcessor(TestEnum.class, annos);
        
        cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT);
        fail();
        
    }
    
    @Test
    public void testBuildInput_defaultValue_ignoreCase() {
        Annotation[] annos = getAnnotations(TestCsv.class, "enum_default_ignoreCase");
        CellProcessor cellProcessor = builder.buildInputCellProcessor(TestEnum.class, annos);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        
        assertThat(cellProcessor, hasCellProcessor(ConvertNullTo.class));
        
        assertThat(cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_INPUT_DEFAULT_OBJ));
    }
    
    @Test
    public void testBuildInput_defaultValue_valueMethod() {
        Annotation[] annos = getAnnotations(TestCsv.class, "enum_default_valueMethod");
        CellProcessor cellProcessor = builder.buildInputCellProcessor(TestEnum.class, annos);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        
        assertThat(cellProcessor, hasCellProcessor(ConvertNullTo.class));
        
        assertThat(cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_INPUT_DEFAULT_OBJ));
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testBuildInput_defaultValue_valueMethod_wrongName() {
        Annotation[] annos = getAnnotations(TestCsv.class, "enum_default_valueMethod_wrongName");
        builder.buildInputCellProcessor(TestEnum.class, annos);
        fail();
    }
    
    @Test
    public void testBuildInput_defaultValue_valueMethod_ignoreCase() {
        Annotation[] annos = getAnnotations(TestCsv.class, "enum_default_valueMethod_ignoreCase");
        CellProcessor cellProcessor = builder.buildInputCellProcessor(TestEnum.class, annos);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        
        assertThat(cellProcessor, hasCellProcessor(ConvertNullTo.class));
        
        assertThat(cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_INPUT_DEFAULT_OBJ));
    }
    
    @Test
    public void testBuildInput_equalsValue() {
        Annotation[] annos = getAnnotations(TestCsv.class, "enum_equalsValue");
        CellProcessor cellProcessor = builder.buildInputCellProcessor(TestEnum.class, annos);
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
        Annotation[] annos = getAnnotations(TestCsv.class, "enum_equalsValue");
        CellProcessor cellProcessor = builder.buildOutputCellProcessor(TestEnum.class, annos, false);
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
        Annotation[] annos = getAnnotations(TestCsv.class, "enum_equalsValue");
        CellProcessor cellProcessor = builder.buildOutputCellProcessor(TestEnum.class, annos, true);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        
        assertThat(cellProcessor, not(hasCellProcessor(Equals.class)));
        
        assertThat(cellProcessor.execute(TEST_VALUE_1_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_1_OBJ));
        
        // not quals input
        assertThat(cellProcessor.execute(TEST_VALUE_2_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_2_OBJ));
        
    }
    
    @Test
    public void testBuildInput_equalsValue_valueMethod() {
        Annotation[] annos = getAnnotations(TestCsv.class, "enum_equalsValue_valueMethod");
        CellProcessor cellProcessor = builder.buildInputCellProcessor(TestEnum.class, annos);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        
        assertThat(cellProcessor, hasCellProcessor(Equals.class));
        
        assertThat(cellProcessor.execute(TEST_VALUE_1_STR_ALIAS, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_1_OBJ));
        
        // not quals input
        try {
            cellProcessor.execute(TEST_VALUE_2_STR_ALIAS, ANONYMOUS_CSVCONTEXT);
            fail();
        } catch(SuperCsvConstraintViolationException e) {
            CellProcessor errorProcessor = e.getProcessor();
            assertThat(errorProcessor, is(instanceOf(Equals.class)));
        }
    }
    
    @Test
    public void testBuildOutput_equalsValue_valueMethod() {
        Annotation[] annos = getAnnotations(TestCsv.class, "enum_equalsValue_valueMethod");
        CellProcessor cellProcessor = builder.buildOutputCellProcessor(TestEnum.class, annos, false);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        
        assertThat(cellProcessor, hasCellProcessor(Equals.class));
        
        assertThat(cellProcessor.execute(TEST_VALUE_1_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_1_STR_ALIAS));
        
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
    public void testBuildOutput_equalsValue_valueMethod_ignoreValidation() {
        Annotation[] annos = getAnnotations(TestCsv.class, "enum_equalsValue_valueMethod");
        CellProcessor cellProcessor = builder.buildOutputCellProcessor(TestEnum.class, annos, true);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        
        assertThat(cellProcessor, not(hasCellProcessor(Equals.class)));
        
        assertThat(cellProcessor.execute(TEST_VALUE_1_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_1_STR_ALIAS));
        
        // not quals input
        assertThat(cellProcessor.execute(TEST_VALUE_2_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_2_STR_ALIAS));
        
    }
    
    @Test
    public void testBuildInput_unique() {
        Annotation[] annos = getAnnotations(TestCsv.class, "enum_unique");
        CellProcessor cellProcessor = builder.buildInputCellProcessor(TestEnum.class, annos);
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
        Annotation[] annos = getAnnotations(TestCsv.class, "enum_unique");
        CellProcessor cellProcessor = builder.buildOutputCellProcessor(TestEnum.class, annos, false);
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
        Annotation[] annos = getAnnotations(TestCsv.class, "enum_unique");
        CellProcessor cellProcessor = builder.buildOutputCellProcessor(TestEnum.class, annos, true);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        
        assertThat(cellProcessor, not(hasCellProcessor(Unique.class)));
        
        assertThat(cellProcessor.execute(TEST_VALUE_1_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_1_OBJ));
        assertThat(cellProcessor.execute(TEST_VALUE_2_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_2_OBJ));
        assertThat(cellProcessor.execute(TEST_VALUE_1_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_1_OBJ));
        
    }
    
    @Test
    public void testBuildInput_combine1() {
        Annotation[] annos = getAnnotations(TestCsv.class, "enum_combine1");
        CellProcessor cellProcessor = builder.buildInputCellProcessor(TestEnum.class, annos);
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
        Annotation[] annos = getAnnotations(TestCsv.class, "enum_combine1");
        CellProcessor cellProcessor = builder.buildOutputCellProcessor(TestEnum.class, annos, false);
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
        Annotation[] annos = getAnnotations(TestCsv.class, "enum_combine1");
        CellProcessor cellProcessor = builder.buildOutputCellProcessor(TestEnum.class, annos, true);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        
        assertThat(cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_OUTPUT_DEFAULT_STR_NORMAL));
        assertThat(cellProcessor.execute(TEST_VALUE_1_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_1_STR_NORMAL));
        
        // not equals input
        assertThat(cellProcessor.execute(TEST_VALUE_2_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_2_STR_NORMAL));
        
        // not unique input
        assertThat(cellProcessor.execute(TEST_VALUE_1_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_1_STR_NORMAL));
        
    }
    
    @Test
    public void testBuildInput_ignoreCase() {
        Annotation[] annos = getAnnotations(TestCsv.class, "enum_ignoreCase");
        CellProcessor cellProcessor = builder.buildInputCellProcessor(TestEnum.class, annos);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        
        assertThat(cellProcessor, hasCellProcessor(ParseEnum.class));
        
        {
            String str = toRandomCase(TEST_VALUE_1_STR_NORMAL);
            assertThat(cellProcessor.execute(str, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_1_OBJ));
        }
        
        {
            String str = toRandomCase(TEST_VALUE_2_STR_NORMAL);
            assertThat(cellProcessor.execute(str, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_2_OBJ));
        }
        
    }
    
    @Test
    public void testBuildOutput_ignoreCase() {
        Annotation[] annos = getAnnotations(TestCsv.class, "enum_ignoreCase");
        CellProcessor cellProcessor = builder.buildOutputCellProcessor(TestEnum.class, annos, true);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        
        assertThat(cellProcessor, not(hasCellProcessor(FormatEnum.class)));
        
        assertThat(cellProcessor.execute(TEST_VALUE_1_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_1_OBJ));
        
    }
    
    @Test
    public void testBuildInput_valueMethod() {
        Annotation[] annos = getAnnotations(TestCsv.class, "enum_valueMethod");
        CellProcessor cellProcessor = builder.buildInputCellProcessor(TestEnum.class, annos);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        
        assertThat(cellProcessor, hasCellProcessor(ParseEnum.class));
        
        assertThat(cellProcessor.execute(TEST_VALUE_1_STR_ALIAS, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_1_OBJ));
        assertThat(cellProcessor.execute(TEST_VALUE_2_STR_ALIAS, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_2_OBJ));
        
    }
    
    @Test
    public void testBuildOutput_valueMethod() {
        Annotation[] annos = getAnnotations(TestCsv.class, "enum_valueMethod");
        CellProcessor cellProcessor = builder.buildOutputCellProcessor(TestEnum.class, annos, false);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        
        assertThat(cellProcessor, hasCellProcessor(FormatEnum.class));
        
        assertThat(cellProcessor.execute(TEST_VALUE_1_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_1_STR_ALIAS));
        assertThat(cellProcessor.execute(TEST_VALUE_2_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_2_STR_ALIAS));
        
    }
    
    @Test
    public void testBuildInput_valueMethod_ignoreCase() {
        Annotation[] annos = getAnnotations(TestCsv.class, "enum_valueMethod_ignoreCase");
        CellProcessor cellProcessor = builder.buildInputCellProcessor(TestEnum.class, annos);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        
        assertThat(cellProcessor, hasCellProcessor(ParseEnum.class));
        
        {
            String str = toRandomCase(TEST_VALUE_1_STR_ALIAS);
            assertThat(cellProcessor.execute(str, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_1_OBJ));
        }
        
        {
            String str = toRandomCase(TEST_VALUE_2_STR_ALIAS);
            assertThat(cellProcessor.execute(str, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_2_OBJ));
        }
        
    }
    
    @Test
    public void testBuildOutput_valueMethod_ignoreCase() {
        Annotation[] annos = getAnnotations(TestCsv.class, "enum_valueMethod_ignoreCase");
        CellProcessor cellProcessor = builder.buildOutputCellProcessor(TestEnum.class, annos, false);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        
        assertThat(cellProcessor, hasCellProcessor(FormatEnum.class));
        
        assertThat(cellProcessor.execute(TEST_VALUE_1_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_1_STR_ALIAS));
        assertThat(cellProcessor.execute(TEST_VALUE_2_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_2_STR_ALIAS));
        
    }
    
    
    @Test
    public void testBuildInput_combine2() {
        Annotation[] annos = getAnnotations(TestCsv.class, "enum_combine2");
        CellProcessor cellProcessor = builder.buildInputCellProcessor(TestEnum.class, annos);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        
        assertThat(cellProcessor, hasCellProcessor(ParseEnum.class));
        
        assertThat(cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_INPUT_DEFAULT_OBJ));
        
        {
            String str = toRandomCase(TEST_VALUE_1_STR_ALIAS);
            assertThat(cellProcessor.execute(str, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_1_OBJ));
        }
        
        {
            String str = toRandomCase(TEST_VALUE_2_STR_ALIAS);
            assertThat(cellProcessor.execute(str, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_2_OBJ));
        }
        
    }
    
    @Test
    public void testBuildOutput_combine2() {
        Annotation[] annos = getAnnotations(TestCsv.class, "enum_combine2");
        CellProcessor cellProcessor = builder.buildOutputCellProcessor(TestEnum.class, annos, false);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        
        assertThat(cellProcessor, hasCellProcessor(FormatEnum.class));
        
        assertThat(cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_OUTPUT_DEFAULT_STR_ALIAS));
        assertThat(cellProcessor.execute(TEST_VALUE_1_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_1_STR_ALIAS));
        assertThat(cellProcessor.execute(TEST_VALUE_2_OBJ, ANONYMOUS_CSVCONTEXT), is(TEST_VALUE_2_STR_ALIAS));
        
    }
    

}

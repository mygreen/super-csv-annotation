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
    
    public enum TestEnum {
        Red("赤(RED)"), Blue("青(BLUE)"), Yellow("黄(Yellow)");
        
        final String aliasName;
        
        private TestEnum(String aliasName) {
            this.aliasName = aliasName;
        }
        
        public String aliasName() {
            return aliasName;
        }
    }
    
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
    
    @CsvBean
    private static class TestCsv {
        
        @CsvColumn(position=0)
        TestEnum enum_default;
        
        @CsvColumn(position=1, optional=true)
        TestEnum enum_optional;
        
        @CsvColumn(position=2, trim=true)
        TestEnum enum_trim;
        
        @CsvColumn(position=3, inputDefaultValue="Red", outputDefaultValue="Blue")
        TestEnum enum_defaultValue;
        
        @CsvColumn(position=4, inputDefaultValue="abc")
        TestEnum enum_defaultValue_invalid;
        
        @CsvColumn(position=5, inputDefaultValue="BLUE")
        @CsvEnumConverter(ignoreCase=true)
        TestEnum enum_default_ignoreCase;
        
        @CsvColumn(position=6, inputDefaultValue="青(BLUE)")
        @CsvEnumConverter(valueMethodName="aliasName")
        TestEnum enum_default_valueMethod;
        
        @CsvColumn(position=7, inputDefaultValue="青(BLUE)")
        @CsvEnumConverter(valueMethodName="sample")
        TestEnum enum_default_valueMethod_wrongName;
        
        @CsvColumn(position=8, inputDefaultValue="青(blue)")
        @CsvEnumConverter(valueMethodName="aliasName", ignoreCase=true)
        TestEnum enum_default_valueMethod_ignoreCase;
        
        @CsvColumn(position=9, equalsValue="Blue")
        TestEnum enum_equalsValue;
        
        @CsvColumn(position=10, equalsValue="青(BLUE)")
        @CsvEnumConverter(valueMethodName="aliasName")
        TestEnum enum_equalsValue_valueMethod;
        
        @CsvColumn(position=11, unique=true)
        TestEnum enum_unique;
        
        @CsvColumn(position=12, optional=true, trim=true, inputDefaultValue="Red", outputDefaultValue="Blue", equalsValue="Red", unique=true)
        TestEnum enum_combine1;
        
        @CsvColumn(position=13)
        @CsvEnumConverter(ignoreCase=true)
        TestEnum enum_ignoreCase;
        
        @CsvColumn(position=13)
        @CsvEnumConverter(valueMethodName="aliasName")
        TestEnum enum_valueMethod;
        
        @CsvColumn(position=13)
        @CsvEnumConverter(valueMethodName="aliasName", ignoreCase=true)
        TestEnum enum_valueMethod_ignoreCase;
        
        @CsvColumn(position=14, optional=true, trim=true, inputDefaultValue="赤(RED)", outputDefaultValue="青(BLUE)")
        @CsvEnumConverter(ignoreCase=true, valueMethodName="aliasName")
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
        
        assertThat(cellProcessor.execute("Red", ANONYMOUS_CSVCONTEXT), is(TestEnum.Red));
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
        
        assertThat(cellProcessor.execute(TestEnum.Red, ANONYMOUS_CSVCONTEXT), is(TestEnum.Red));
        assertThat(cellProcessor.execute(TestEnum.Blue, ANONYMOUS_CSVCONTEXT), is(TestEnum.Blue));
        
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
        
        assertThat(cellProcessor.execute(TestEnum.Red, ANONYMOUS_CSVCONTEXT), is(TestEnum.Red));
        assertThat(cellProcessor.execute(TestEnum.Blue, ANONYMOUS_CSVCONTEXT), is(TestEnum.Blue));
        
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
        
        assertThat(cellProcessor.execute("Red", ANONYMOUS_CSVCONTEXT), is(TestEnum.Red));
        
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
        
        assertThat(cellProcessor.execute(TestEnum.Red, ANONYMOUS_CSVCONTEXT), is(TestEnum.Red));
        
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
        
        assertThat(cellProcessor.execute(TestEnum.Red, ANONYMOUS_CSVCONTEXT), is(TestEnum.Red));
        
        // null input
        assertThat(cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT), is(nullValue()));
    }
    
    @Test
    public void testBuildInput_trim() {
        Annotation[] annos = getAnnotations(TestCsv.class, "enum_trim");
        CellProcessor cellProcessor = builder.buildInputCellProcessor(TestEnum.class, annos);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        
        assertThat(cellProcessor, hasCellProcessor(Trim.class));
        
        assertThat(cellProcessor.execute("  Red ", ANONYMOUS_CSVCONTEXT), is(TestEnum.Red));
    }
    
    @Test
    public void testBuildOutput_trim() {
        Annotation[] annos = getAnnotations(TestCsv.class, "enum_trim");
        CellProcessor cellProcessor = builder.buildOutputCellProcessor(TestEnum.class, annos, false);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        
        assertThat(cellProcessor, hasCellProcessor(Trim.class));
        
        assertThat(cellProcessor.execute(TestEnum.Red, ANONYMOUS_CSVCONTEXT), is("Red"));
        
    }
    
    @Test
    public void testBuildOutput_trim_ignoreValidation() {
        Annotation[] annos = getAnnotations(TestCsv.class, "enum_trim");
        CellProcessor cellProcessor = builder.buildOutputCellProcessor(TestEnum.class, annos, true);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        
        assertThat(cellProcessor, hasCellProcessor(Trim.class));
        
        assertThat(cellProcessor.execute(TestEnum.Red, ANONYMOUS_CSVCONTEXT), is("Red"));
        
    }
    
    @Test
    public void testBuildInput_defaultValue() {
        Annotation[] annos = getAnnotations(TestCsv.class, "enum_defaultValue");
        CellProcessor cellProcessor = builder.buildInputCellProcessor(TestEnum.class, annos);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        
        assertThat(cellProcessor, hasCellProcessor(ConvertNullTo.class));
        
        assertThat(cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT), is(TestEnum.Red));
    }
    
    @Test
    public void testBuildOutput_defaultValue() {
        Annotation[] annos = getAnnotations(TestCsv.class, "enum_defaultValue");
        CellProcessor cellProcessor = builder.buildOutputCellProcessor(TestEnum.class, annos, false);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        
        assertThat(cellProcessor, hasCellProcessor(ConvertNullTo.class));
        
        assertThat(cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT), is("Blue"));
    }
    
    @Test
    public void testBuildOutput_defaultValue_ignoreValidation() {
        Annotation[] annos = getAnnotations(TestCsv.class, "enum_defaultValue");
        CellProcessor cellProcessor = builder.buildOutputCellProcessor(TestEnum.class, annos, true);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        
        assertThat(cellProcessor, hasCellProcessor(ConvertNullTo.class));
        
        assertThat(cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT), is("Blue"));
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
        
        assertThat(cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT), is(TestEnum.Blue));
    }
    
    @Test
    public void testBuildInput_defaultValue_valueMethod() {
        Annotation[] annos = getAnnotations(TestCsv.class, "enum_default_valueMethod");
        CellProcessor cellProcessor = builder.buildInputCellProcessor(TestEnum.class, annos);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        
        assertThat(cellProcessor, hasCellProcessor(ConvertNullTo.class));
        
        assertThat(cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT), is(TestEnum.Blue));
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
        
        assertThat(cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT), is(TestEnum.Blue));
    }
    
    @Test
    public void testBuildInput_equalsValue() {
        Annotation[] annos = getAnnotations(TestCsv.class, "enum_equalsValue");
        CellProcessor cellProcessor = builder.buildInputCellProcessor(TestEnum.class, annos);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        
        assertThat(cellProcessor, hasCellProcessor(Equals.class));
        
        assertThat(cellProcessor.execute("Blue", ANONYMOUS_CSVCONTEXT), is(TestEnum.Blue));
        
        // not quals input
        try {
            cellProcessor.execute("Red", ANONYMOUS_CSVCONTEXT);
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
        
        assertThat(cellProcessor.execute(TestEnum.Blue, ANONYMOUS_CSVCONTEXT), is(TestEnum.Blue));
        
        // not quals input
        try {
            cellProcessor.execute(TestEnum.Red, ANONYMOUS_CSVCONTEXT);
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
        
        assertThat(cellProcessor.execute(TestEnum.Blue, ANONYMOUS_CSVCONTEXT), is(TestEnum.Blue));
        
        // not quals input
        assertThat(cellProcessor.execute(TestEnum.Red, ANONYMOUS_CSVCONTEXT), is(TestEnum.Red));
        
    }
    
    @Test
    public void testBuildInput_equalsValue_valueMethod() {
        Annotation[] annos = getAnnotations(TestCsv.class, "enum_equalsValue_valueMethod");
        CellProcessor cellProcessor = builder.buildInputCellProcessor(TestEnum.class, annos);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        
        assertThat(cellProcessor, hasCellProcessor(Equals.class));
        
        assertThat(cellProcessor.execute("青(BLUE)", ANONYMOUS_CSVCONTEXT), is(TestEnum.Blue));
        
        // not quals input
        try {
            cellProcessor.execute("赤(RED)", ANONYMOUS_CSVCONTEXT);
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
        
        assertThat(cellProcessor.execute(TestEnum.Blue, ANONYMOUS_CSVCONTEXT), is("青(BLUE)"));
        
        // not quals input
        try {
            cellProcessor.execute(TestEnum.Red, ANONYMOUS_CSVCONTEXT);
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
        
        assertThat(cellProcessor.execute(TestEnum.Blue, ANONYMOUS_CSVCONTEXT), is("青(BLUE)"));
        
        // not quals input
        assertThat(cellProcessor.execute(TestEnum.Red, ANONYMOUS_CSVCONTEXT), is("赤(RED)"));
        
    }
    
    @Test
    public void testBuildInput_unique() {
        Annotation[] annos = getAnnotations(TestCsv.class, "enum_unique");
        CellProcessor cellProcessor = builder.buildInputCellProcessor(TestEnum.class, annos);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        
        assertThat(cellProcessor, hasCellProcessor(Unique.class));
        
        assertThat(cellProcessor.execute("Red", ANONYMOUS_CSVCONTEXT), is(TestEnum.Red));
        assertThat(cellProcessor.execute("Blue", ANONYMOUS_CSVCONTEXT), is(TestEnum.Blue));
        
        // not unique input
        try {
            cellProcessor.execute("Red", ANONYMOUS_CSVCONTEXT);
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
        
        assertThat(cellProcessor.execute(TestEnum.Red, ANONYMOUS_CSVCONTEXT), is(TestEnum.Red));
        assertThat(cellProcessor.execute(TestEnum.Blue, ANONYMOUS_CSVCONTEXT), is(TestEnum.Blue));
        
        // not unique input
        try {
            cellProcessor.execute(TestEnum.Red, ANONYMOUS_CSVCONTEXT);
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
        
        assertThat(cellProcessor.execute(TestEnum.Red, ANONYMOUS_CSVCONTEXT), is(TestEnum.Red));
        assertThat(cellProcessor.execute(TestEnum.Blue, ANONYMOUS_CSVCONTEXT), is(TestEnum.Blue));
        assertThat(cellProcessor.execute(TestEnum.Red, ANONYMOUS_CSVCONTEXT), is(TestEnum.Red));
        
    }
    
    @Test
    public void testBuildInput_combine1() {
        Annotation[] annos = getAnnotations(TestCsv.class, "enum_combine1");
        CellProcessor cellProcessor = builder.buildInputCellProcessor(TestEnum.class, annos);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        
        assertThat(cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT), is(TestEnum.Red));
        assertThat(cellProcessor.execute("  Red  ", ANONYMOUS_CSVCONTEXT), is(TestEnum.Red));
        
        // not equals input
        try {
            cellProcessor.execute("Blue", ANONYMOUS_CSVCONTEXT);
            fail();
        } catch(SuperCsvConstraintViolationException e) {
            CellProcessor errorProcessor = e.getProcessor();
            assertThat(errorProcessor, is(instanceOf(Equals.class)));
        }
        
        // not unique input
        try {
            cellProcessor.execute("Red", ANONYMOUS_CSVCONTEXT);
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
        
        assertThat(cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT), is("Blue"));
        assertThat(cellProcessor.execute(TestEnum.Red, ANONYMOUS_CSVCONTEXT), is("Red"));
        
        // not equals input
        try {
            cellProcessor.execute(TestEnum.Yellow, ANONYMOUS_CSVCONTEXT);
            fail();
        } catch(SuperCsvConstraintViolationException e) {
            CellProcessor errorProcessor = e.getProcessor();
            assertThat(errorProcessor, is(instanceOf(Equals.class)));
        }
        
        // not unique input
        try {
            cellProcessor.execute(TestEnum.Red, ANONYMOUS_CSVCONTEXT);
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
        
        assertThat(cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT), is("Blue"));
        assertThat(cellProcessor.execute(TestEnum.Red, ANONYMOUS_CSVCONTEXT), is("Red"));
        
        // not equals input
        assertThat(cellProcessor.execute(TestEnum.Yellow, ANONYMOUS_CSVCONTEXT), is("Yellow"));
        
        // not unique input
        assertThat(cellProcessor.execute(TestEnum.Red, ANONYMOUS_CSVCONTEXT), is("Red"));
        
    }
    
    @Test
    public void testBuildInput_ignoreCase() {
        Annotation[] annos = getAnnotations(TestCsv.class, "enum_ignoreCase");
        CellProcessor cellProcessor = builder.buildInputCellProcessor(TestEnum.class, annos);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        
        assertThat(cellProcessor, hasCellProcessor(ParseEnum.class));
        
        assertThat(cellProcessor.execute("Red", ANONYMOUS_CSVCONTEXT), is(TestEnum.Red));
        assertThat(cellProcessor.execute("BLUE", ANONYMOUS_CSVCONTEXT), is(TestEnum.Blue));
        
    }
    
    @Test
    public void testBuildOutput_ignoreCase() {
        Annotation[] annos = getAnnotations(TestCsv.class, "enum_ignoreCase");
        CellProcessor cellProcessor = builder.buildOutputCellProcessor(TestEnum.class, annos, true);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        
        assertThat(cellProcessor, not(hasCellProcessor(FormatEnum.class)));
        
        assertThat(cellProcessor.execute(TestEnum.Red, ANONYMOUS_CSVCONTEXT), is(TestEnum.Red));
        
    }
    
    @Test
    public void testBuildInput_valueMethod() {
        Annotation[] annos = getAnnotations(TestCsv.class, "enum_valueMethod");
        CellProcessor cellProcessor = builder.buildInputCellProcessor(TestEnum.class, annos);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        
        assertThat(cellProcessor, hasCellProcessor(ParseEnum.class));
        
        assertThat(cellProcessor.execute("赤(RED)", ANONYMOUS_CSVCONTEXT), is(TestEnum.Red));
        assertThat(cellProcessor.execute("青(BLUE)", ANONYMOUS_CSVCONTEXT), is(TestEnum.Blue));
        
    }
    
    @Test
    public void testBuildOutput_valueMethod() {
        Annotation[] annos = getAnnotations(TestCsv.class, "enum_valueMethod");
        CellProcessor cellProcessor = builder.buildOutputCellProcessor(TestEnum.class, annos, false);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        
        assertThat(cellProcessor, hasCellProcessor(FormatEnum.class));
        
        assertThat(cellProcessor.execute(TestEnum.Red, ANONYMOUS_CSVCONTEXT), is("赤(RED)"));
        assertThat(cellProcessor.execute(TestEnum.Blue, ANONYMOUS_CSVCONTEXT), is("青(BLUE)"));
        
    }
    
    @Test
    public void testBuildInput_valueMethod_ignoreCase() {
        Annotation[] annos = getAnnotations(TestCsv.class, "enum_valueMethod_ignoreCase");
        CellProcessor cellProcessor = builder.buildInputCellProcessor(TestEnum.class, annos);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        
        assertThat(cellProcessor, hasCellProcessor(ParseEnum.class));
        
        assertThat(cellProcessor.execute("赤(RED)", ANONYMOUS_CSVCONTEXT), is(TestEnum.Red));
        assertThat(cellProcessor.execute("青(BLUE)", ANONYMOUS_CSVCONTEXT), is(TestEnum.Blue));
        
    }
    
    @Test
    public void testBuildOutput_valueMethod_ignoreCase() {
        Annotation[] annos = getAnnotations(TestCsv.class, "enum_valueMethod_ignoreCase");
        CellProcessor cellProcessor = builder.buildOutputCellProcessor(TestEnum.class, annos, false);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        
        assertThat(cellProcessor, hasCellProcessor(FormatEnum.class));
        
        assertThat(cellProcessor.execute(TestEnum.Red, ANONYMOUS_CSVCONTEXT), is("赤(RED)"));
        assertThat(cellProcessor.execute(TestEnum.Blue, ANONYMOUS_CSVCONTEXT), is("青(BLUE)"));
        
    }
    
    
    @Test
    public void testBuildInput_combine2() {
        Annotation[] annos = getAnnotations(TestCsv.class, "enum_combine2");
        CellProcessor cellProcessor = builder.buildInputCellProcessor(TestEnum.class, annos);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        
        assertThat(cellProcessor, hasCellProcessor(ParseEnum.class));
        
        assertThat(cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT), is(TestEnum.Red));
        assertThat(cellProcessor.execute("赤(RED)", ANONYMOUS_CSVCONTEXT), is(TestEnum.Red));
        assertThat(cellProcessor.execute("青(blue)", ANONYMOUS_CSVCONTEXT), is(TestEnum.Blue));
        
    }
    
    @Test
    public void testBuildOutput_combine2() {
        Annotation[] annos = getAnnotations(TestCsv.class, "enum_combine2");
        CellProcessor cellProcessor = builder.buildOutputCellProcessor(TestEnum.class, annos, false);
        printCellProcessorChain(cellProcessor, name.getMethodName());
        
        assertThat(cellProcessor, hasCellProcessor(FormatEnum.class));
        
        assertThat(cellProcessor.execute(null, ANONYMOUS_CSVCONTEXT), is("青(BLUE)"));
        assertThat(cellProcessor.execute(TestEnum.Red, ANONYMOUS_CSVCONTEXT), is("赤(RED)"));
        assertThat(cellProcessor.execute(TestEnum.Blue, ANONYMOUS_CSVCONTEXT), is("青(BLUE)"));
        
    }
    

}

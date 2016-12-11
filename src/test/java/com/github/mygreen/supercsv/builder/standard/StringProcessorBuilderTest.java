package com.github.mygreen.supercsv.builder.standard;

import static org.junit.Assert.*;
import static org.assertj.core.api.Assertions.*;
import static com.github.mygreen.supercsv.tool.TestUtils.*;
import static com.github.mygreen.supercsv.tool.HasCellProcessorAssert.*;

import java.lang.annotation.Annotation;
import java.util.Comparator;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
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

/**
 * Test the {@link StringProcessorBuilder}
 *
 * @version 2.0
 * @since 1.2
 * @author T.TSUCHIE
 *
 */
public class StringProcessorBuilderTest {
    
    @Rule
    public TestName name = new TestName();
    
    private Configuration config;
    private ProcessorBuilderResolver builderResolver;
    private Comparator<Annotation> comparator;
    private BeanMappingFactory beanMappingFactory;
    
    private final Class<?>[] groupEmpty = new Class[]{};
    
    @Before
    public void setUp() {
        this.config = new Configuration();
        this.builderResolver = config.getBuilderResolver();
        this.comparator = config.getAnnoationComparator();
        this.beanMappingFactory = new BeanMappingFactory();
    }
    
    private static final String TEST_VALUE_1_OBJ = "abc";
    private static final String TEST_VALUE_1_STR_NORMAL = "abc";
    
    @CsvBean
    private static class TestCsv {
        
        @CsvColumn(number=1)
        String col_default;
        
    }
    
    @Test
    public void testFormatter_ParsePrint_wrapper() {
        
        FieldAccessor field = getFieldAccessor(TestCsv.class, "col_default", comparator);
        StringProcessorBuilder builder = (StringProcessorBuilder) builderResolver.resolve(String.class);
        TextFormatter<String> formatter = builder.getFormatter(field, config);
        
        {
            // parse
            assertThat(formatter.parse("abc")).isEqualTo("abc");
        }
        
        {
            //print
            assertThat(formatter.print("abc")).isEqualTo("abc");
            assertThat(formatter.print("")).isEqualTo("");
            assertThat(formatter.print(null)).isNull();
        }
    }
    
    @Test
    public void testFormatter_MessageAndVariables() {
        
        FieldAccessor field = getFieldAccessor(TestCsv.class, "col_default", comparator);
        StringProcessorBuilder builder = (StringProcessorBuilder) builderResolver.resolve(String.class);
        TextFormatter<String> formatter = builder.getFormatter(field, config);
        
        formatter.setValidationMessage("テストメッセージ");
        
        assertThat(formatter.getValidationMessage()).isEmpty();
        assertThat(formatter.getMessageVariables()).isEmpty();
        
    }
    
    @Test
    public void testFormatter_Pattern() {
        
        FieldAccessor field = getFieldAccessor(TestCsv.class, "col_default", comparator);
        StringProcessorBuilder builder = (StringProcessorBuilder) builderResolver.resolve(String.class);
        TextFormatter<String> formatter = builder.getFormatter(field, config);
        
        assertThat(formatter.getPattern()).isEmpty();
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
            String expected = null;
            assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
        }
        
        {
            // empty input
            String input = "";
            String expected = "";
            assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
        }
        
        {
            // valid input
            String input = TEST_VALUE_1_STR_NORMAL;
            String expected = TEST_VALUE_1_OBJ;
            assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
        }
        
        {
            // valid input - over size
            String input = "abc";
            String expected = TEST_VALUE_1_OBJ;
            assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
        }
        
        {
            // valid input - empty
            String input = "";
            String expected = "";
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
            String input = TEST_VALUE_1_OBJ;
            String expected = TEST_VALUE_1_STR_NORMAL;
            assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
        }
        
        {
            // valid input - empty
            String input = "";
            String expected = "";
            assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
        }
        
    }
    
    
}

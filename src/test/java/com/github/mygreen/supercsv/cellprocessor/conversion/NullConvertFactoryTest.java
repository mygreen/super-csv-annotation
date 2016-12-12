package com.github.mygreen.supercsv.cellprocessor.conversion;

import static org.junit.Assert.*;
import static org.assertj.core.api.Assertions.*;
import static com.github.mygreen.supercsv.tool.TestUtils.*;

import java.util.Optional;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.supercsv.cellprocessor.ift.CellProcessor;

import com.github.mygreen.supercsv.annotation.CsvBean;
import com.github.mygreen.supercsv.annotation.CsvColumn;
import com.github.mygreen.supercsv.annotation.conversion.CsvNullConvert;
import com.github.mygreen.supercsv.builder.AnnotationComparator;
import com.github.mygreen.supercsv.builder.ProcessorBuilderResolver;
import com.github.mygreen.supercsv.builder.BuildCase;
import com.github.mygreen.supercsv.builder.Configuration;
import com.github.mygreen.supercsv.builder.FieldAccessor;
import com.github.mygreen.supercsv.builder.standard.IntegerProcessorBuilder;
import com.github.mygreen.supercsv.builder.standard.StringProcessorBuilder;
import com.github.mygreen.supercsv.cellprocessor.format.TextFormatter;

/**
 * {@link NullConvertFactory}のテスタ
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class NullConvertFactoryTest {
    
    @Rule
    public TestName name = new TestName();
    
    private NullConvertFactory factory;
    
    private Configuration config;
    
    private final Class<?>[] groupEmpty = new Class[]{};
    private final AnnotationComparator comparator = new AnnotationComparator();
    private final ProcessorBuilderResolver builderResolver = new ProcessorBuilderResolver();
    
    @Before
    public void setUp() throws Exception {
        this.factory = new NullConvertFactory();
        this.config = new Configuration();
        
    }
    
    @CsvBean
    private static class TestCsv {
        
        @CsvColumn(number=1)
        @CsvNullConvert(value={"-", "N/A"})
        private String col_string;
        
        @CsvColumn(number=2)
        @CsvNullConvert(value={"-", "N/A"})
        private int col_int;
        
    }
    
    @Test
    public void testCreate_string() {
        
        FieldAccessor field = getFieldAccessor(TestCsv.class, "col_string", comparator);
        StringProcessorBuilder builder = (StringProcessorBuilder) builderResolver.resolve(String.class);
        TextFormatter<String> formatter = builder.getFormatter(field, config);
        
        CsvNullConvert anno = field.getAnnotationsByGroup(CsvNullConvert.class, groupEmpty).get(0);
        {
            //next null
            Optional<CellProcessor> processor = factory.create(anno, Optional.empty(), field, formatter, config);
            printCellProcessorChain(processor.get(), name.getMethodName());
            
            assertThat(processor.get()).isInstanceOf(NullConvert.class);
            
            NullConvert actual = (NullConvert)processor.get();
            {
                // input not match 
                String input = "abcde";
                assertThat((Object)actual.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(input);
                
            }
            
            {
                // input null 
                String input = null;
                assertThat((Object)actual.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(input);
                
            }
            
            {
                // input match
                String input = "-";
                assertThat((Object)actual.execute(input, ANONYMOUS_CSVCONTEXT)).isNull();
                
            }
            
        }
        
        {
            //next exist
            Optional<CellProcessor> processor = factory.create(anno, Optional.of(new NextCellProcessor()), field, formatter, config);
            printCellProcessorChain(processor.get(), name.getMethodName());
            
            assertThat(processor.get()).isInstanceOf(NullConvert.class);
            
            NullConvert actual = (NullConvert)processor.get();
            {
                // input not match 
                String input = "abcde";
                assertThat((Object)actual.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(input);
                
            }
            
            {
                // input null 
                String input = null;
                assertThat((Object)actual.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(input);
                
            }
            
            {
                // input match
                String input = "-";
                assertThat((Object)actual.execute(input, ANONYMOUS_CSVCONTEXT)).isNull();
                
            }
            
        }
        
    }
    
    @Test
    public void testCerate_int() {
        
        FieldAccessor field = getFieldAccessor(TestCsv.class, "col_int", comparator);
        IntegerProcessorBuilder builder = (IntegerProcessorBuilder) builderResolver.resolve(int.class);
        TextFormatter<Integer> formatter = builder.getFormatter(field, config);
        
        CsvNullConvert anno = field.getAnnotationsByGroup(CsvNullConvert.class, groupEmpty).get(0);
        {
            //next null
            Optional<CellProcessor> processor = factory.create(anno, Optional.empty(), field, formatter, config);
            printCellProcessorChain(processor.get(), name.getMethodName());
            
            assertThat(processor.get()).isInstanceOf(NullConvert.class);
            
            NullConvert actual = (NullConvert)processor.get();
            {
                // input not match 
                String input = "abcde";
                assertThat((Object)actual.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(input);
                
            }
            
            {
                // input null 
                String input = null;
                assertThat((Object)actual.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(input);
                
            }
            
            {
                // input match
                String input = "-";
                assertThat((Object)actual.execute(input, ANONYMOUS_CSVCONTEXT)).isNull();
                
            }
            
        }
        
        {
            //next exist
            Optional<CellProcessor> processor = factory.create(anno, Optional.of(new NextCellProcessor()), field, formatter, config);
            printCellProcessorChain(processor.get(), name.getMethodName());
            
            assertThat(processor.get()).isInstanceOf(NullConvert.class);
            
            NullConvert actual = (NullConvert)processor.get();
            {
                // input not match 
                String input = "abcde";
                assertThat((Object)actual.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(input);
                
            }
            
            {
                // input null 
                String input = null;
                assertThat((Object)actual.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(input);
                
            }
            
            {
                // input match
                String input = "-";
                assertThat((Object)actual.execute(input, ANONYMOUS_CSVCONTEXT)).isNull();
                
            }
            
        }
        
    }
    
}

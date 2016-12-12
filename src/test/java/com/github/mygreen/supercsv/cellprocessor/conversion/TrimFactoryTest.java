package com.github.mygreen.supercsv.cellprocessor.conversion;

import static org.junit.Assert.*;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static com.github.mygreen.supercsv.tool.TestUtils.*;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.supercsv.cellprocessor.ift.CellProcessor;

import com.github.mygreen.supercsv.annotation.CsvBean;
import com.github.mygreen.supercsv.annotation.CsvColumn;
import com.github.mygreen.supercsv.annotation.conversion.CsvTrim;
import com.github.mygreen.supercsv.builder.AnnotationComparator;
import com.github.mygreen.supercsv.builder.ProcessorBuilderResolver;
import com.github.mygreen.supercsv.builder.BuildCase;
import com.github.mygreen.supercsv.builder.Configuration;
import com.github.mygreen.supercsv.builder.FieldAccessor;
import com.github.mygreen.supercsv.builder.standard.StringProcessorBuilder;
import com.github.mygreen.supercsv.cellprocessor.format.TextFormatter;

/**
 * {@link TrimFactory}のテスタ
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class TrimFactoryTest {
    
    @Rule
    public TestName name = new TestName();
    
    private TrimFactory factory;
    
    private Configuration config;
    
    private final Class<?>[] groupEmpty = new Class[]{};
    private final AnnotationComparator comparator = new AnnotationComparator();
    private final ProcessorBuilderResolver builderResolver = new ProcessorBuilderResolver();
    
    @Before
    public void setUp() throws Exception {
        this.factory = new TrimFactory();
        
        this.config = new Configuration();
    }
    
    @CsvBean
    private static class TestCsv {
        
        @CsvColumn(number=1)
        @CsvTrim
        private String col_default;
        
    }
    
    @Test
    public void testCreate_default() {
        
        FieldAccessor field = getFieldAccessor(TestCsv.class, "col_default", comparator);
        StringProcessorBuilder builder = (StringProcessorBuilder) builderResolver.resolve(String.class);
        TextFormatter<String> formatter = builder.getFormatter(field, config);
        
        CsvTrim anno = field.getAnnotationsByGroup(CsvTrim.class, groupEmpty).get(0);
        
        {
            //next null
            Optional<CellProcessor> processor = factory.create(anno, Optional.empty(), field, formatter, config);
            printCellProcessorChain(processor.get(), name.getMethodName());
            
            assertThat(processor.get()).isInstanceOf(Trim.class);
            
            Trim actual = (Trim)processor.get();
            
            {
                String input = "  abcd  ";
                String expected = "abcd";
                assertThat((Object)actual.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
            }
        }
        
        {
            //next exist
            Optional<CellProcessor> processor = factory.create(anno, Optional.of(new NextCellProcessor()), field, formatter, config);
            printCellProcessorChain(processor.get(), name.getMethodName());
            
            assertThat(processor.get()).isInstanceOf(Trim.class);
            
            Trim actual = (Trim)processor.get();
            
            {
                String input = "  abcd  ";
                String expected = "abcd";
                assertThat((Object)actual.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
            }
        }
        
    }
    
}

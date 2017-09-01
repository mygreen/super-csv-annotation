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
import com.github.mygreen.supercsv.annotation.conversion.CsvOneSideTrim;
import com.github.mygreen.supercsv.builder.AnnotationComparator;
import com.github.mygreen.supercsv.builder.Configuration;
import com.github.mygreen.supercsv.builder.FieldAccessor;
import com.github.mygreen.supercsv.builder.ProcessorBuilderResolver;
import com.github.mygreen.supercsv.builder.standard.StringProcessorBuilder;
import com.github.mygreen.supercsv.cellprocessor.format.TextFormatter;

/**
 * {@link OneSideTrimFactory}のテスタ
 *
 * @since 2.1
 * @author T.TSUCHIE
 *
 */
public class OneSideTrimFactoryTest {
    
    @Rule
    public TestName name = new TestName();
    
    private OneSideTrimFactory factory;
    
    private Configuration config;
    
    private final Class<?>[] groupEmpty = new Class[]{};
    private final AnnotationComparator comparator = new AnnotationComparator();
    private final ProcessorBuilderResolver builderResolver = new ProcessorBuilderResolver();
    
    @Before
    public void setUp() throws Exception {
        this.factory = new OneSideTrimFactory();
        
        this.config = new Configuration();
    }
    
    @CsvBean
    private static class TestCsv {
        
        @CsvColumn(number=1)
        @CsvOneSideTrim
        private String col_default;
        
        @CsvColumn(number=2)
        @CsvOneSideTrim(trimChar='-', leftAlign=true)
        private String col_leftTrim;
        
    }
    
    @Test
    public void testCreate_default() {
        
        FieldAccessor field = getFieldAccessor(TestCsv.class, "col_default", comparator);
        StringProcessorBuilder builder = (StringProcessorBuilder) builderResolver.resolve(String.class);
        TextFormatter<String> formatter = builder.getFormatter(field, config);
        
        CsvOneSideTrim anno = field.getAnnotationsByGroup(CsvOneSideTrim.class, groupEmpty).get(0);
        
        {
            //next null
            Optional<CellProcessor> processor = factory.create(anno, Optional.empty(), field, formatter, config);
            printCellProcessorChain(processor.get(), name.getMethodName());
            
            assertThat(processor.get()).isInstanceOf(OneSideTrim.class);
            
            OneSideTrim actual = (OneSideTrim)processor.get();
            assertThat(actual.getTrimChar()).isEqualTo(' ');
            assertThat(actual.isLeftAlign()).isEqualTo(false);
            
            {
                String input = "  abc d e  ";
                String expected = "  abc d e";
                assertThat((Object)actual.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
            }
        }
        
        {
            //next exist
            Optional<CellProcessor> processor = factory.create(anno, Optional.of(new NextCellProcessor()), field, formatter, config);
            printCellProcessorChain(processor.get(), name.getMethodName());
            
            assertThat(processor.get()).isInstanceOf(OneSideTrim.class);
            
            OneSideTrim actual = (OneSideTrim)processor.get();
            assertThat(actual.getTrimChar()).isEqualTo(' ');
            assertThat(actual.isLeftAlign()).isEqualTo(false);
            
            {
                String input = "  abc d e  ";
                String expected = "  abc d e";
                assertThat((Object)actual.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
            }
        }
        
    }
    
    @Test
    public void testCreate_leftTrim() {
        
        FieldAccessor field = getFieldAccessor(TestCsv.class, "col_leftTrim", comparator);
        StringProcessorBuilder builder = (StringProcessorBuilder) builderResolver.resolve(String.class);
        TextFormatter<String> formatter = builder.getFormatter(field, config);
        
        CsvOneSideTrim anno = field.getAnnotationsByGroup(CsvOneSideTrim.class, groupEmpty).get(0);
        
        {
            //next null
            Optional<CellProcessor> processor = factory.create(anno, Optional.empty(), field, formatter, config);
            printCellProcessorChain(processor.get(), name.getMethodName());
            
            assertThat(processor.get()).isInstanceOf(OneSideTrim.class);
            
            OneSideTrim actual = (OneSideTrim)processor.get();
            assertThat(actual.getTrimChar()).isEqualTo('-');
            assertThat(actual.isLeftAlign()).isEqualTo(true);
            
            {
                String input = "--abc d e--";
                String expected = "abc d e--";
                assertThat((Object)actual.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
            }
        }
        
        {
            //next exist
            Optional<CellProcessor> processor = factory.create(anno, Optional.of(new NextCellProcessor()), field, formatter, config);
            printCellProcessorChain(processor.get(), name.getMethodName());
            
            assertThat(processor.get()).isInstanceOf(OneSideTrim.class);
            
            OneSideTrim actual = (OneSideTrim)processor.get();
            assertThat(actual.getTrimChar()).isEqualTo('-');
            assertThat(actual.isLeftAlign()).isEqualTo(true);
            
            {
                String input = "--abc d e--";
                String expected = "abc d e--";
                assertThat((Object)actual.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
            }
        }
        
    }
    
    
    
}

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
import com.github.mygreen.supercsv.annotation.conversion.CsvTruncate;
import com.github.mygreen.supercsv.builder.AnnotationComparator;
import com.github.mygreen.supercsv.builder.ProcessorBuilderResolver;
import com.github.mygreen.supercsv.builder.BuildCase;
import com.github.mygreen.supercsv.builder.Configuration;
import com.github.mygreen.supercsv.builder.FieldAccessor;
import com.github.mygreen.supercsv.builder.standard.StringProcessorBuilder;
import com.github.mygreen.supercsv.cellprocessor.format.TextFormatter;
import com.github.mygreen.supercsv.exception.SuperCsvInvalidAnnotationException;

/**
 * {@link TruncateFactory}のテスタ
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class TruncateFactoryTest {
    
    @Rule
    public TestName name = new TestName();
    
    private TruncateFactory factory;
    
    private Configuration config;
    
    private final Class<?>[] groupEmpty = new Class[]{};
    private final AnnotationComparator comparator = new AnnotationComparator();
    private final ProcessorBuilderResolver builderResolver = new ProcessorBuilderResolver();
    
    @Before
    public void setUp() throws Exception {
        this.factory = new TruncateFactory();
        
        this.config = new Configuration();
    }
    
    @CsvBean
    private static class TestCsv {
        
        @CsvColumn(number=1)
        @CsvTruncate(maxSize=3)
        private String col_default;
        
        @CsvColumn(number=2)
        @CsvTruncate(maxSize=3, suffix="...")
        private String col_suffix;
    }
    
    @CsvBean
    private static class ErrorCsv {
        
        @CsvTruncate(maxSize=0)
        private String col_wrong_maxSize;
        
    }
    
    @Test
    public void testCreate_default() {
        
        FieldAccessor field = getFieldAccessor(TestCsv.class, "col_default", comparator);
        StringProcessorBuilder builder = (StringProcessorBuilder) builderResolver.resolve(String.class);
        TextFormatter<String> formatter = builder.getFormatter(field, config);
        
        CsvTruncate anno = field.getAnnotationsByGroup(CsvTruncate.class, groupEmpty).get(0);
        
        {
            //next null
            Optional<CellProcessor> processor = factory.create(anno, Optional.empty(), field, formatter, config);
            printCellProcessorChain(processor.get(), name.getMethodName());
            
            assertThat(processor.get()).isInstanceOf(Truncate.class);
            
            Truncate actual = (Truncate)processor.get();
            assertThat(actual.getMaxSize()).isEqualTo(3);
            assertThat(actual.getSuffix()).isEqualTo("");
            
            {
                String input = "abc";
                String expected = "abc";
                assertThat((Object)actual.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
            }
            
            {
                String input = "abcd";
                String expected = "abc";
                assertThat((Object)actual.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
            }
        }
        
        {
            //next exist
            Optional<CellProcessor> processor = factory.create(anno, Optional.of(new NextCellProcessor()), field, formatter, config);
            printCellProcessorChain(processor.get(), name.getMethodName());
            
            assertThat(processor.get()).isInstanceOf(Truncate.class);
            
            Truncate actual = (Truncate)processor.get();
            assertThat(actual.getMaxSize()).isEqualTo(3);
            assertThat(actual.getSuffix()).isEqualTo("");
            
            {
                String input = "abc";
                String expected = "abc";
                assertThat((Object)actual.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
            }
            
            {
                String input = "abcd";
                String expected = "abc";
                assertThat((Object)actual.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
            }
        }
        
    }
    
    @Test
    public void testCreate_suffix() {
        
        FieldAccessor field = getFieldAccessor(TestCsv.class, "col_suffix", comparator);
        StringProcessorBuilder builder = (StringProcessorBuilder) builderResolver.resolve(String.class);
        TextFormatter<String> formatter = builder.getFormatter(field, config);
        
        CsvTruncate anno = field.getAnnotationsByGroup(CsvTruncate.class, groupEmpty).get(0);
        
        {
            //next null
            Optional<CellProcessor> processor = factory.create(anno, Optional.empty(), field, formatter, config);
            printCellProcessorChain(processor.get(), name.getMethodName());
            
            assertThat(processor.get()).isInstanceOf(Truncate.class);
            
            Truncate actual = (Truncate)processor.get();
            assertThat(actual.getMaxSize()).isEqualTo(3);
            assertThat(actual.getSuffix()).isEqualTo("...");
            
            {
                String input = "abc";
                String expected = "abc";
                assertThat((Object)actual.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
            }
            
            {
                String input = "abcd";
                String expected = "abc...";
                assertThat((Object)actual.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
            }
        }
        
        {
            //next exist
            Optional<CellProcessor> processor = factory.create(anno, Optional.of(new NextCellProcessor()), field, formatter, config);
            printCellProcessorChain(processor.get(), name.getMethodName());
            
            assertThat(processor.get()).isInstanceOf(Truncate.class);
            
            Truncate actual = (Truncate)processor.get();
            assertThat(actual.getMaxSize()).isEqualTo(3);
            assertThat(actual.getSuffix()).isEqualTo("...");
            
            {
                String input = "abc";
                String expected = "abc";
                assertThat((Object)actual.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
            }
            
            {
                String input = "abcd";
                String expected = "abc...";
                assertThat((Object)actual.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
            }
        }
        
    }
    
    @Test
    public void testCreate_wrong_maxSize() {
        
        FieldAccessor field = getFieldAccessor(ErrorCsv.class, "col_wrong_maxSize", comparator);
        StringProcessorBuilder builder = (StringProcessorBuilder) builderResolver.resolve(String.class);
        TextFormatter<String> formatter = builder.getFormatter(field, config);
        
        CsvTruncate anno = field.getAnnotationsByGroup(CsvTruncate.class, groupEmpty).get(0);
        try {
            factory.create(anno, Optional.empty(), field, formatter, config);
            fail();
            
        } catch(Exception e) {
            assertThat(e).isInstanceOf(SuperCsvInvalidAnnotationException.class)
                .hasMessage("'%s' において、アノテーション @CsvTruncate の属性 'maxSize' の値（0）は、1以上の値を設定してください。",
                        field.getNameWithClass());
                
//            e.printStackTrace();
//            SuperCsvInvalidAnnotationException exception = (SuperCsvInvalidAnnotationException)e;
        }
    }
}

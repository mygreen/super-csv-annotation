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
import com.github.mygreen.supercsv.annotation.conversion.CsvRightPad;
import com.github.mygreen.supercsv.builder.AnnotationComparator;
import com.github.mygreen.supercsv.builder.ProcessorBuilderResolver;
import com.github.mygreen.supercsv.builder.BuildCase;
import com.github.mygreen.supercsv.builder.Configuration;
import com.github.mygreen.supercsv.builder.FieldAccessor;
import com.github.mygreen.supercsv.builder.standard.StringProcessorBuilder;
import com.github.mygreen.supercsv.cellprocessor.format.TextFormatter;
import com.github.mygreen.supercsv.exception.SuperCsvInvalidAnnotationException;



/**
 * {@link RightPadFactory}のテスタ
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class RightPadFactoryTest {
    
    @Rule
    public TestName name = new TestName();
    
    private RightPadFactory factory;
    
    private Configuration config;
    
    private final Class<?>[] groupEmpty = new Class[]{};
    private final AnnotationComparator comparator = new AnnotationComparator();
    private final ProcessorBuilderResolver builderResolver = new ProcessorBuilderResolver();
    
    @Before
    public void setUp() throws Exception {
        this.factory = new RightPadFactory();
        
        this.config = new Configuration();
    }
    
    @CsvBean
    private static class TestCsv {
        
        @CsvColumn(number=1)
        @CsvRightPad(size=5)
        private String col_default;
        
        @CsvColumn(number=1)
        @CsvRightPad(size=5, padChar='_')
        private String col_padChar;
    }
    
    @CsvBean
    private static class ErrorCsv {
        
        @CsvColumn(number=1)
        @CsvRightPad(size=0)
        private String col_wrong_size;
        
    }
    
    @Test
    public void testCreate_default() {
        
        FieldAccessor field = getFieldAccessor(TestCsv.class, "col_default", comparator);
        StringProcessorBuilder builder = (StringProcessorBuilder) builderResolver.resolve(String.class);
        TextFormatter<String> formatter = builder.getFormatter(field, config);
        
        CsvRightPad anno = field.getAnnotationsByGroup(CsvRightPad.class, groupEmpty).get(0);
        
        {
            //next null
            Optional<CellProcessor> processor = factory.create(anno, Optional.empty(), field, formatter, config);
            printCellProcessorChain(processor, name.getMethodName());
            
            assertThat(processor.get()).isInstanceOf(RightPad.class);
            
            RightPad actual = (RightPad)processor.get();
            assertThat(actual.getPadSize()).isEqualTo(5);
            assertThat(actual.getPadChar()).isEqualTo(' ');
            
            {
                String input = "abc";
                String expected = "abc  ";
                assertThat((Object)actual.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
            }
        }
        
        {
            //next exist
            Optional<CellProcessor> processor = factory.create(anno, Optional.of(new NextCellProcessor()), field, formatter, config);
            printCellProcessorChain(processor.get(), name.getMethodName());
            
            assertThat(processor.get()).isInstanceOf(RightPad.class);
            
            RightPad actual = (RightPad)processor.get();
            assertThat(actual.getPadSize()).isEqualTo(5);
            assertThat(actual.getPadChar()).isEqualTo(' ');
            
            {
                String input = "abc";
                String expected = "abc  ";
                assertThat((Object)actual.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
            }
        }
        
    }
    
    @Test
    public void testCreate_padChar() {
        
        FieldAccessor field = getFieldAccessor(TestCsv.class, "col_padChar", comparator);
        StringProcessorBuilder builder = (StringProcessorBuilder) builderResolver.resolve(String.class);
        TextFormatter<String> formatter = builder.getFormatter(field, config);
        
        CsvRightPad anno = field.getAnnotationsByGroup(CsvRightPad.class, groupEmpty).get(0);
        
        {
            //next null
            Optional<CellProcessor> processor = factory.create(anno, Optional.empty(), field, formatter, config);
            printCellProcessorChain(processor, name.getMethodName());
            
            assertThat(processor.get()).isInstanceOf(RightPad.class);
            
            RightPad actual = (RightPad)processor.get();
            assertThat(actual.getPadSize()).isEqualTo(5);
            assertThat(actual.getPadChar()).isEqualTo('_');
            
            {
                String input = "abc";
                String expected = "abc__";
                assertThat((Object)actual.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
            }
        }
        
        {
            //next exist
            Optional<CellProcessor> processor = factory.create(anno, Optional.of(new NextCellProcessor()), field, formatter, config);
            printCellProcessorChain(processor.get(), name.getMethodName());
            
            assertThat(processor.get()).isInstanceOf(RightPad.class);
            
            RightPad actual = (RightPad)processor.get();
            assertThat(actual.getPadSize()).isEqualTo(5);
            assertThat(actual.getPadChar()).isEqualTo('_');
            
            {
                String input = "abc";
                String expected = "abc__";
                assertThat((Object)actual.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
            }
        }
        
    }
    
    @Test
    public void testCreate_wornd_size() {
        
        FieldAccessor field = getFieldAccessor(ErrorCsv.class, "col_wrong_size", comparator);
        StringProcessorBuilder builder = (StringProcessorBuilder) builderResolver.resolve(String.class);
        TextFormatter<String> formatter = builder.getFormatter(field, config);
        
        CsvRightPad anno = field.getAnnotationsByGroup(CsvRightPad.class, groupEmpty).get(0);
        
        try {
            factory.create(anno, Optional.empty(), field, formatter, config);
            fail();
            
        } catch(Exception e) {
            assertThat(e).isInstanceOf(SuperCsvInvalidAnnotationException.class)
                .hasMessage("'%s' において、アノテーション @CsvRightPad の属性 'size' の値（0）は、1以上の値を設定してください。",
                        field.getNameWithClass());;
            
//            e.printStackTrace();
                        
//            SuperCsvInvalidAnnotationException exception = (SuperCsvInvalidAnnotationException)e;
        }
        
    }
    
}

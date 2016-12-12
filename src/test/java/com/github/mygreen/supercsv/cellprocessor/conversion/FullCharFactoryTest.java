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
import com.github.mygreen.supercsv.annotation.conversion.CsvFullChar;
import com.github.mygreen.supercsv.builder.AnnotationComparator;
import com.github.mygreen.supercsv.builder.ProcessorBuilderResolver;
import com.github.mygreen.supercsv.builder.BuildCase;
import com.github.mygreen.supercsv.builder.Configuration;
import com.github.mygreen.supercsv.builder.FieldAccessor;
import com.github.mygreen.supercsv.builder.standard.StringProcessorBuilder;
import com.github.mygreen.supercsv.cellprocessor.format.TextFormatter;

/**
 * {@link FullCharFactory}のテスタ
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class FullCharFactoryTest {
    
    @Rule
    public TestName name = new TestName();
    
    private FullCharFactory factory;
    
    private Configuration config;
    
    private final Class<?>[] groupEmpty = new Class[]{};
    private final AnnotationComparator comparator = new AnnotationComparator();
    private final ProcessorBuilderResolver builderResolver = new ProcessorBuilderResolver();
    
    @Before
    public void setUp() throws Exception {
        this.factory = new FullCharFactory();
        
        this.config = new Configuration();
    }
    
    @CsvBean
    private static class TestCsv {
        
        @CsvColumn(number=1)
        @CsvFullChar
        private String col_default;
        
        @CsvColumn(number=2)
        @CsvFullChar(categories={CharCategory.Number, CharCategory.Alpha})
        private String col_number_alpha;
        
    }
    
    @Test
    public void testCreate_default() {
        
        FieldAccessor field = getFieldAccessor(TestCsv.class, "col_default", comparator);
        StringProcessorBuilder builder = (StringProcessorBuilder) builderResolver.resolve(String.class);
        TextFormatter<String> formatter = builder.getFormatter(field, config);
        
        CsvFullChar anno = field.getAnnotationsByGroup(CsvFullChar.class, groupEmpty).get(0);
        
        {
            //next null
            Optional<CellProcessor> processor = factory.create(anno, Optional.empty(), field, formatter, config);
            printCellProcessorChain(processor.get(), name.getMethodName());
            
            assertThat(processor.get()).isInstanceOf(FullChar.class);
            
            FullChar actual = (FullChar)processor.get();
            assertThat(actual.getCategories()).containsExactly(CharCategory.values());
            
            {
                String input = "abc_ABC_012 !@";
                String expected = "ａｂｃ＿ＡＢＣ＿０１２　！＠";
                assertThat((Object)actual.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
            }
        }
        
        {
            //next exist
            Optional<CellProcessor> processor = factory.create(anno, Optional.of(new NextCellProcessor()), field, formatter, config);
            printCellProcessorChain(processor.get(), name.getMethodName());
            
            assertThat(processor.get()).isInstanceOf(FullChar.class);
            
            FullChar actual = (FullChar)processor.get();
            assertThat(actual.getCategories()).containsExactly(CharCategory.values());
            
            {
                String input = "abc_ABC_012 !@";
                String expected = "ａｂｃ＿ＡＢＣ＿０１２　！＠";
                assertThat((Object)actual.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
            }
        }
        
    }
    
    @Test
    public void testCreate_number_alpha() {
        
        FieldAccessor field = getFieldAccessor(TestCsv.class, "col_number_alpha", comparator);
        StringProcessorBuilder builder = (StringProcessorBuilder) builderResolver.resolve(String.class);
        TextFormatter<String> formatter = builder.getFormatter(field, config);
        
        CsvFullChar anno = field.getAnnotationsByGroup(CsvFullChar.class, groupEmpty).get(0);
        
        {
            //next null
            Optional<CellProcessor> processor = factory.create(anno, Optional.empty(), field, formatter, config);
            printCellProcessorChain(processor.get(), name.getMethodName());
            
            assertThat(processor.get()).isInstanceOf(FullChar.class);
            
            FullChar actual = (FullChar)processor.get();
            assertThat(actual.getCategories()).containsExactly(CharCategory.Number, CharCategory.Alpha);
            
            {
                String input = "abc_ABC_012 !@";
                String expected = "ａｂｃ_ＡＢＣ_０１２ !@";
                assertThat((Object)actual.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
            }
        }
        
        {
            //next exist
            Optional<CellProcessor> processor = factory.create(anno, Optional.of(new NextCellProcessor()), field, formatter, config);
            printCellProcessorChain(processor.get(), name.getMethodName());
            
            assertThat(processor.get()).isInstanceOf(FullChar.class);
            
            FullChar actual = (FullChar)processor.get();
            assertThat(actual.getCategories()).containsExactly(CharCategory.Number, CharCategory.Alpha);
            
            {
                String input = "abc_ABC_012 !@";
                String expected = "ａｂｃ_ＡＢＣ_０１２ !@";
                assertThat((Object)actual.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
            }
        }
        
    }
    
}

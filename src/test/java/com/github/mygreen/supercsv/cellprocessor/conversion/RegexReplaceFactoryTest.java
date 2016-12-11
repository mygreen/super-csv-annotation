package com.github.mygreen.supercsv.cellprocessor.conversion;

import static org.junit.Assert.*;
import static org.assertj.core.api.Assertions.*;
import static com.github.mygreen.supercsv.tool.TestUtils.*;

import java.util.Optional;
import java.util.regex.Pattern;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.supercsv.cellprocessor.ift.CellProcessor;

import com.github.mygreen.supercsv.annotation.CsvBean;
import com.github.mygreen.supercsv.annotation.CsvColumn;
import com.github.mygreen.supercsv.annotation.PatternFlag;
import com.github.mygreen.supercsv.annotation.conversion.CsvRegexReplace;
import com.github.mygreen.supercsv.builder.AnnotationComparator;
import com.github.mygreen.supercsv.builder.ProcessorBuilderResolver;
import com.github.mygreen.supercsv.builder.BuildCase;
import com.github.mygreen.supercsv.builder.Configuration;
import com.github.mygreen.supercsv.builder.FieldAccessor;
import com.github.mygreen.supercsv.builder.standard.StringProcessorBuilder;
import com.github.mygreen.supercsv.cellprocessor.format.TextFormatter;
import com.github.mygreen.supercsv.exception.SuperCsvInvalidAnnotationException;

/**
 * {@link RegexReplaceFactory}のテスタ
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class RegexReplaceFactoryTest {
    
    @Rule
    public TestName name = new TestName();
    
    private RegexReplaceFactory factory;
    
    private Configuration config;
    
    private final Class<?>[] groupEmpty = new Class[]{};
    private final AnnotationComparator comparator = new AnnotationComparator();
    private final ProcessorBuilderResolver builderResolver = new ProcessorBuilderResolver();
    
    @Before
    public void setUp() throws Exception {
        this.factory = new RegexReplaceFactory();
        
        this.config = new Configuration();
    }
    
    @CsvBean
    private static class TestCsv {
        
        @CsvColumn(number=1)
        @CsvRegexReplace(regex="([0-9]{4})/([0-9]{1,2})/([0-9]{1,2})", replacement="$1年$2月$3日", flags=PatternFlag.CASE_INSENSITIVE)
        private String col_default;
        
    }
    
    @CsvBean
    private static class ErrorCsv {
        
        @CsvRegexReplace(regex="test)test", replacement="wrong")
        private String col_wrong_pattern;
        
    }
    
    @Test
    public void testCreate_default() {
        
        FieldAccessor field = getFieldAccessor(TestCsv.class, "col_default", comparator);
        StringProcessorBuilder builder = (StringProcessorBuilder) builderResolver.resolve(String.class);
        TextFormatter<String> formatter = builder.getFormatter(field, config);
        
        CsvRegexReplace anno = field.getAnnotationsByGroup(CsvRegexReplace.class, groupEmpty).get(0);
        
        {
            //next null
            Optional<CellProcessor> processor = factory.create(anno, Optional.empty(), field, formatter, config);
            printCellProcessorChain(processor.get(), name.getMethodName());
            
            assertThat(processor.get()).isInstanceOf(RegexReplace.class);
            
            RegexReplace actual = (RegexReplace)processor.get();
            assertThat(actual.getRegex()).isEqualTo("([0-9]{4})/([0-9]{1,2})/([0-9]{1,2})");
            assertThat(actual.getReplacement()).isEqualTo("$1年$2月$3日");
            assertThat(actual.getFlags()).isEqualTo(Pattern.CASE_INSENSITIVE);
            
            {
                String input = "2016/3/12";
                String expected = "2016年3月12日";
                assertThat((Object)actual.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
            }
        }
        
        {
            //next exist
            Optional<CellProcessor> processor = factory.create(anno, Optional.of(new NextCellProcessor()), field, formatter, config);
            printCellProcessorChain(processor.get(), name.getMethodName());
            
            assertThat(processor.get()).isInstanceOf(RegexReplace.class);
            
            RegexReplace actual = (RegexReplace)processor.get();
            assertThat(actual.getRegex()).isEqualTo("([0-9]{4})/([0-9]{1,2})/([0-9]{1,2})");
            assertThat(actual.getReplacement()).isEqualTo("$1年$2月$3日");
            assertThat(actual.getFlags()).isEqualTo(Pattern.CASE_INSENSITIVE);
            
            {
                String input = "2016/3/12";
                String expected = "2016年3月12日";
                assertThat((Object)actual.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
            }
        }
        
    }
    
    @Test
    public void testCreate_wrong_pattern() {
        
        FieldAccessor field = getFieldAccessor(ErrorCsv.class, "col_wrong_pattern", comparator);
        StringProcessorBuilder builder = (StringProcessorBuilder) builderResolver.resolve(String.class);
        TextFormatter<String> formatter = builder.getFormatter(field, config);
        
        CsvRegexReplace anno = field.getAnnotationsByGroup(CsvRegexReplace.class, groupEmpty).get(0);
        try {
            factory.create(anno, Optional.empty(), field, formatter, config);
            fail();
            
        } catch(Exception e) {
            assertThat(e).isInstanceOf(SuperCsvInvalidAnnotationException.class)
                .hasMessage("'%s' において、アノテーション @CsvRegexReplace の属性 'regex' の値（test)test）は、正規表現として不正です。",
                        field.getNameWithClass());
            
//            SuperCsvInvalidAnnotationException exception = (SuperCsvInvalidAnnotationException)e;
//           e.printStackTrace();
            
        }
    }
    
}

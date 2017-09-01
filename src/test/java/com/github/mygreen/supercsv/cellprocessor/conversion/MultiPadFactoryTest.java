package com.github.mygreen.supercsv.cellprocessor.conversion;

import static org.junit.Assert.*;
import static com.github.mygreen.supercsv.tool.TestUtils.*;
import static org.assertj.core.api.Assertions.*;

import java.util.Optional;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.supercsv.cellprocessor.ift.CellProcessor;

import com.github.mygreen.supercsv.annotation.CsvBean;
import com.github.mygreen.supercsv.annotation.CsvColumn;
import com.github.mygreen.supercsv.annotation.conversion.CsvMultiPad;
import com.github.mygreen.supercsv.builder.AnnotationComparator;
import com.github.mygreen.supercsv.builder.Configuration;
import com.github.mygreen.supercsv.builder.FieldAccessor;
import com.github.mygreen.supercsv.builder.ProcessorBuilderResolver;
import com.github.mygreen.supercsv.builder.standard.StringProcessorBuilder;
import com.github.mygreen.supercsv.cellprocessor.format.TextFormatter;
import com.github.mygreen.supercsv.exception.SuperCsvInvalidAnnotationException;

/**
 * {@link MultiPadFactory}のテスタ。
 *
 * @since 2.1
 * @author T.TSUCHIE
 *
 */
public class MultiPadFactoryTest {

    @Rule
    public TestName name = new TestName();

    private MultiPadFactory factory;

    private Configuration config;

    private final Class<?>[] groupEmpty = new Class[]{};
    private final AnnotationComparator comparator = new AnnotationComparator();
    private final ProcessorBuilderResolver builderResolver = new ProcessorBuilderResolver();

    @Before
    public void setUp() throws Exception {
        this.factory = new MultiPadFactory();

        this.config = new Configuration();
    }

    @CsvBean
    private static class TestCsv {

        @CsvColumn(number=1)
        @CsvMultiPad(size=5)
        private String col_default;

        @CsvColumn(number=2)
        @CsvMultiPad(size=5, padChar='-', rightAlign=true)
        private String col_rightPad;

        @CsvColumn(number=3)
        @CsvMultiPad(size=5, chopped=true)
        private String col_chopped;

        @CsvColumn(number=4)
        @CsvMultiPad(size=5, chopped=true, paddingProcessor=CharWidthPaddingProcessor.class)
        private String col_paddingProcessor;

    }
    
    @CsvBean
    private static class ErrorCsv {
        
        @CsvColumn(number=1)
        @CsvMultiPad(size=0)
        private String col_wrong_size;
        
    }

    @Test
    public void testCreate_default() {

        FieldAccessor field = getFieldAccessor(TestCsv.class, "col_default", comparator);
        StringProcessorBuilder builder = (StringProcessorBuilder) builderResolver.resolve(String.class);
        TextFormatter<String> formatter = builder.getFormatter(field, config);

        CsvMultiPad anno = field.getAnnotationsByGroup(CsvMultiPad.class, groupEmpty).get(0);

        {
            //next null
            Optional<CellProcessor> processor = factory.create(anno, Optional.empty(), field, formatter, config);
            printCellProcessorChain(processor.get(), name.getMethodName());

            assertThat(processor.get()).isInstanceOf(MultiPad.class);

            MultiPad actual = (MultiPad)processor.get();
            assertThat(actual.getSize()).isEqualTo(5);
            assertThat(actual.getPadChar()).isEqualTo(' ');
            assertThat(actual.isRightAlign()).isEqualTo(false);
            assertThat(actual.isChopped()).isEqualTo(false);
            assertThat(actual.getPaddingProcessor()).isInstanceOf(SimplePaddingProcessor.class);

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

            assertThat(processor.get()).isInstanceOf(MultiPad.class);

            MultiPad actual = (MultiPad)processor.get();
            assertThat(actual.getSize()).isEqualTo(5);
            assertThat(actual.getPadChar()).isEqualTo(' ');
            assertThat(actual.isRightAlign()).isEqualTo(false);
            assertThat(actual.isChopped()).isEqualTo(false);
            assertThat(actual.getPaddingProcessor()).isInstanceOf(SimplePaddingProcessor.class);

            {
                String input = "abc";
                String expected = "abc  ";
                assertThat((Object)actual.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
            }
        }

    }

    @Test
    public void testCreate_rightPad() {

        FieldAccessor field = getFieldAccessor(TestCsv.class, "col_rightPad", comparator);
        StringProcessorBuilder builder = (StringProcessorBuilder) builderResolver.resolve(String.class);
        TextFormatter<String> formatter = builder.getFormatter(field, config);

        CsvMultiPad anno = field.getAnnotationsByGroup(CsvMultiPad.class, groupEmpty).get(0);

        {
            //next null
            Optional<CellProcessor> processor = factory.create(anno, Optional.empty(), field, formatter, config);
            printCellProcessorChain(processor.get(), name.getMethodName());

            assertThat(processor.get()).isInstanceOf(MultiPad.class);

            MultiPad actual = (MultiPad)processor.get();
            assertThat(actual.getSize()).isEqualTo(5);
            assertThat(actual.getPadChar()).isEqualTo('-');
            assertThat(actual.isRightAlign()).isEqualTo(true);
            assertThat(actual.isChopped()).isEqualTo(false);
            assertThat(actual.getPaddingProcessor()).isInstanceOf(SimplePaddingProcessor.class);

            {
                String input = "abc";
                String expected = "--abc";
                assertThat((Object)actual.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
            }
        }

        {
            //next exist
            Optional<CellProcessor> processor = factory.create(anno, Optional.of(new NextCellProcessor()), field, formatter, config);
            printCellProcessorChain(processor.get(), name.getMethodName());

            assertThat(processor.get()).isInstanceOf(MultiPad.class);

            MultiPad actual = (MultiPad)processor.get();
            assertThat(actual.getSize()).isEqualTo(5);
            assertThat(actual.getPadChar()).isEqualTo('-');
            assertThat(actual.isRightAlign()).isEqualTo(true);
            assertThat(actual.isChopped()).isEqualTo(false);
            assertThat(actual.getPaddingProcessor()).isInstanceOf(SimplePaddingProcessor.class);

            {
                String input = "abc";
                String expected = "--abc";
                assertThat((Object)actual.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
            }
        }

    }

    @Test
    public void testCreate_chpopped() {

        FieldAccessor field = getFieldAccessor(TestCsv.class, "col_chopped", comparator);
        StringProcessorBuilder builder = (StringProcessorBuilder) builderResolver.resolve(String.class);
        TextFormatter<String> formatter = builder.getFormatter(field, config);

        CsvMultiPad anno = field.getAnnotationsByGroup(CsvMultiPad.class, groupEmpty).get(0);

        {
            //next null
            Optional<CellProcessor> processor = factory.create(anno, Optional.empty(), field, formatter, config);
            printCellProcessorChain(processor.get(), name.getMethodName());

            assertThat(processor.get()).isInstanceOf(MultiPad.class);

            MultiPad actual = (MultiPad)processor.get();
            assertThat(actual.getSize()).isEqualTo(5);
            assertThat(actual.getPadChar()).isEqualTo(' ');
            assertThat(actual.isRightAlign()).isEqualTo(false);
            assertThat(actual.isChopped()).isEqualTo(true);
            assertThat(actual.getPaddingProcessor()).isInstanceOf(SimplePaddingProcessor.class);

            {
                String input = "abcdef";
                String expected = "abcde";
                assertThat((Object)actual.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
            }
        }

        {
            //next exist
            Optional<CellProcessor> processor = factory.create(anno, Optional.of(new NextCellProcessor()), field, formatter, config);
            printCellProcessorChain(processor.get(), name.getMethodName());

            assertThat(processor.get()).isInstanceOf(MultiPad.class);

            MultiPad actual = (MultiPad)processor.get();
            assertThat(actual.getSize()).isEqualTo(5);
            assertThat(actual.getPadChar()).isEqualTo(' ');
            assertThat(actual.isRightAlign()).isEqualTo(false);
            assertThat(actual.isChopped()).isEqualTo(true);
            assertThat(actual.getPaddingProcessor()).isInstanceOf(SimplePaddingProcessor.class);

            {
                String input = "abcdef";
                String expected = "abcde";
                assertThat((Object)actual.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
            }
        }

    }

    @Test
    public void testCreate_paddingProcessor() {

        FieldAccessor field = getFieldAccessor(TestCsv.class, "col_paddingProcessor", comparator);
        StringProcessorBuilder builder = (StringProcessorBuilder) builderResolver.resolve(String.class);
        TextFormatter<String> formatter = builder.getFormatter(field, config);

        CsvMultiPad anno = field.getAnnotationsByGroup(CsvMultiPad.class, groupEmpty).get(0);

        {
            //next null
            Optional<CellProcessor> processor = factory.create(anno, Optional.empty(), field, formatter, config);
            printCellProcessorChain(processor.get(), name.getMethodName());

            assertThat(processor.get()).isInstanceOf(MultiPad.class);

            MultiPad actual = (MultiPad)processor.get();
            assertThat(actual.getSize()).isEqualTo(5);
            assertThat(actual.getPadChar()).isEqualTo(' ');
            assertThat(actual.isRightAlign()).isEqualTo(false);
            assertThat(actual.isChopped()).isEqualTo(true);
            assertThat(actual.getPaddingProcessor()).isInstanceOf(CharWidthPaddingProcessor.class);

            {
                String input = "aあbいb";
                String expected = "aあb ";
                assertThat((Object)actual.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
            }
        }

        {
            //next exist
            Optional<CellProcessor> processor = factory.create(anno, Optional.of(new NextCellProcessor()), field, formatter, config);
            printCellProcessorChain(processor.get(), name.getMethodName());

            assertThat(processor.get()).isInstanceOf(MultiPad.class);

            MultiPad actual = (MultiPad)processor.get();
            assertThat(actual.getSize()).isEqualTo(5);
            assertThat(actual.getPadChar()).isEqualTo(' ');
            assertThat(actual.isRightAlign()).isEqualTo(false);
            assertThat(actual.isChopped()).isEqualTo(true);
            assertThat(actual.getPaddingProcessor()).isInstanceOf(CharWidthPaddingProcessor.class);

            {
                String input = "aあbいb";
                String expected = "aあb ";
                assertThat((Object)actual.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
            }
        }

    }
    
    @Test
    public void testCreate_wrong_size() {
        
        FieldAccessor field = getFieldAccessor(ErrorCsv.class, "col_wrong_size", comparator);
        StringProcessorBuilder builder = (StringProcessorBuilder) builderResolver.resolve(String.class);
        TextFormatter<String> formatter = builder.getFormatter(field, config);
        
        CsvMultiPad anno = field.getAnnotationsByGroup(CsvMultiPad.class, groupEmpty).get(0);
        try {
            factory.create(anno, Optional.empty(), field, formatter, config);
            fail();
            
        } catch(Exception e) {
            assertThat(e).isInstanceOf(SuperCsvInvalidAnnotationException.class)
                .hasMessage("'%s' において、アノテーション @CsvMultiPad の属性 'size' の値（0）は、1以上の値を設定してください。",
                        field.getNameWithClass());
            
//            SuperCsvInvalidAnnotationException exception = (SuperCsvInvalidAnnotationException)e;
//           e.printStackTrace();
            
        }
    }

}

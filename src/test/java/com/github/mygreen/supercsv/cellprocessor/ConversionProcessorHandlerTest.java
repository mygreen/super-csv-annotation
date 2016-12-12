package com.github.mygreen.supercsv.cellprocessor;

import static org.junit.Assert.*;
import static org.assertj.core.api.Assertions.*;
import static com.github.mygreen.supercsv.tool.TestUtils.*;
import static com.github.mygreen.supercsv.tool.HasCellProcessorAssert.*;
import static com.github.mygreen.supercsv.tool.HasCellProcessorAssert.assertThat;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Comparator;
import java.util.Optional;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;
import org.supercsv.util.CsvContext;

import com.github.mygreen.supercsv.annotation.CsvBean;
import com.github.mygreen.supercsv.annotation.CsvColumn;
import com.github.mygreen.supercsv.annotation.constraint.CsvLengthBetween;
import com.github.mygreen.supercsv.annotation.constraint.CsvLengthExact;
import com.github.mygreen.supercsv.annotation.constraint.CsvLengthMax;
import com.github.mygreen.supercsv.annotation.constraint.CsvLengthMin;
import com.github.mygreen.supercsv.annotation.conversion.CsvConversion;
import com.github.mygreen.supercsv.annotation.conversion.CsvLeftPad;
import com.github.mygreen.supercsv.annotation.conversion.CsvLower;
import com.github.mygreen.supercsv.annotation.conversion.CsvRegexReplace;
import com.github.mygreen.supercsv.annotation.conversion.CsvRightPad;
import com.github.mygreen.supercsv.annotation.conversion.CsvUpper;
import com.github.mygreen.supercsv.builder.ProcessorBuilderResolver;
import com.github.mygreen.supercsv.builder.BuildCase;
import com.github.mygreen.supercsv.builder.Configuration;
import com.github.mygreen.supercsv.builder.FieldAccessor;
import com.github.mygreen.supercsv.builder.standard.StringProcessorBuilder;
import com.github.mygreen.supercsv.cellprocessor.constraint.LengthBetween;
import com.github.mygreen.supercsv.cellprocessor.constraint.LengthBetweenFactory;
import com.github.mygreen.supercsv.cellprocessor.constraint.LengthExact;
import com.github.mygreen.supercsv.cellprocessor.constraint.LengthExactFactory;
import com.github.mygreen.supercsv.cellprocessor.constraint.LengthMax;
import com.github.mygreen.supercsv.cellprocessor.constraint.LengthMaxFactory;
import com.github.mygreen.supercsv.cellprocessor.constraint.LengthMin;
import com.github.mygreen.supercsv.cellprocessor.constraint.LengthMinFactory;
import com.github.mygreen.supercsv.cellprocessor.conversion.LeftPad;
import com.github.mygreen.supercsv.cellprocessor.conversion.LeftPadFactory;
import com.github.mygreen.supercsv.cellprocessor.conversion.Lower;
import com.github.mygreen.supercsv.cellprocessor.conversion.LowerFactory;
import com.github.mygreen.supercsv.cellprocessor.conversion.RegexReplace;
import com.github.mygreen.supercsv.cellprocessor.conversion.RegexReplaceFactory;
import com.github.mygreen.supercsv.cellprocessor.conversion.RightPad;
import com.github.mygreen.supercsv.cellprocessor.conversion.RightPadFactory;
import com.github.mygreen.supercsv.cellprocessor.conversion.Upper;
import com.github.mygreen.supercsv.cellprocessor.conversion.UpperFactory;
import com.github.mygreen.supercsv.cellprocessor.format.TextFormatter;

/**
 * {@link ConversionProcessorHandler}のテスタ
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class ConversionProcessorHandlerTest {
    
    @Rule
    public TestName name = new TestName();
    
    private Configuration config;
    private ProcessorBuilderResolver builderResolver;
    private Comparator<Annotation> comparator;
    private ConversionProcessorHandler handlerFactory;
    
    private final Class<?>[] groupEmpty = new Class[]{};
    
    @Before
    public void setUp() throws Exception {
        this.config = new Configuration();
        this.builderResolver = config.getBuilderResolver();
        this.comparator = config.getAnnoationComparator();
        this.handlerFactory = new ConversionProcessorHandler();
        
    }
    
    @Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @Repeatable(CsvCustomConversion.List.class)
    @CsvConversion(CustomConversionFactory.class)
    public static @interface CsvCustomConversion {
        
        String text();
        
        Class<?>[] groups() default {};
        
        int order() default 0;
        
        @Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
        @Retention(RetentionPolicy.RUNTIME)
        @Documented
        @interface List {
            
            CsvCustomConversion[] value();
        }
    }
    
    private static class CustomConversionFactory implements ConversionProcessorFactory<CsvCustomConversion> {
        
        @Override
        public Optional<CellProcessor> create(CsvCustomConversion anno, Optional<CellProcessor> next,
                FieldAccessor field, TextFormatter<?> formatter, Configuration config) {
            
            final CustomConversion processor = next.map(n ->  new CustomConversion(anno.text(), n))
                    .orElseGet(() -> new CustomConversion(anno.text()));
            
            return Optional.of(processor);
            
        }
        
    }
    
    private static class CustomConversion extends CellProcessorAdaptor implements StringCellProcessor {
        
        private String text;
        
        CustomConversion(final String text) {
            super();
            checkPreconditions(text);
            this.text = text;
        }
        
        CustomConversion(final String text, final CellProcessor next) {
            super(next);
            checkPreconditions(text);
            this.text = text;
        }
        
        private static void checkPreconditions(final String text) {
            if(text == null) {
                throw new NullPointerException("text should not be null.");
            }
        }
        
        @Override
        public <T> T execute(final Object value, final CsvContext context) {
            if(value == null) {
                return next.execute(value, context);
            }
            
            // 最後尾に文字列を足す
            final String result = value.toString() + text;
            
            return next.execute(result, context);
        }
        
    }
    
    // テスト用のグループ1
    private interface Group1 { }
    
    // テスト用のグループ2
    private interface Group2 { }
    
    @CsvBean
    private static class TestCsv {
        
        @CsvColumn(number=1)
        String col_no_anno;
        
        @CsvColumn(number=2)
        @CsvRegexReplace(regex="無期限", replacement="9999/12/31")
        String col_register;
        
        @CsvColumn(number=3)
        @CsvCustomConversion(text = "Hello!")
        String col_custom;
        
        @CsvColumn(number=4)
        @CsvRegexReplace(regex="無期限", replacement="9999/12/31", groups=Group1.class)
        @CsvCustomConversion(text = "Hello!")
        String col_groups;
        
        @CsvColumn(number=5)
        @CsvCustomConversion(text="Hello!", order=1)
        @CsvCustomConversion(text="World!", order=2)
        String col_order1;
        
        @CsvColumn(number=6)
        @CsvCustomConversion(text="Hello!", order=2)
        @CsvCustomConversion(text="World!", order=1)
        String col_order2;
        
        @CsvColumn(number=7)
        @CsvLeftPad(size=5, order=1, cases={})
        @CsvRightPad(size=5, order=2, cases={BuildCase.Read})
        @CsvUpper(order=3, cases={BuildCase.Write})
        @CsvLower(order=4, cases={BuildCase.Read, BuildCase.Write})
        String col_cases;
        
    }
    
    @Test
    public void testCreate_noAnno() {
        
        FieldAccessor field = getFieldAccessor(TestCsv.class, "col_no_anno", comparator);
        StringProcessorBuilder builder = (StringProcessorBuilder) builderResolver.resolve(String.class);
        TextFormatter<String> formatter = builder.getFormatter(field, config);
        
        Optional<CellProcessor> processor = handlerFactory.create(Optional.empty(), field, formatter, config, BuildCase.Read, groupEmpty);
        
        assertThat(processor).isEmpty();
        
    }
    
    /**
     * 未登録のアノテーション
     */
    @Test
    public void testCreate_unregister() {
        
        
        FieldAccessor field = getFieldAccessor(TestCsv.class, "col_register", comparator);
        StringProcessorBuilder builder = (StringProcessorBuilder) builderResolver.resolve(String.class);
        TextFormatter<String> formatter = builder.getFormatter(field, config);
        
        Optional<CellProcessor> processor = handlerFactory.create(Optional.empty(), field, formatter, config, BuildCase.Read, groupEmpty);
        assertThat(processor).isEmpty();
        
    }
    
    /**
     * 登録済みのアノテーション
     */
    @Test
    public void testCreate_register() {
        
        
        FieldAccessor field = getFieldAccessor(TestCsv.class, "col_register", comparator);
        StringProcessorBuilder builder = (StringProcessorBuilder) builderResolver.resolve(String.class);
        TextFormatter<String> formatter = builder.getFormatter(field, config);
        
        handlerFactory.register(CsvRegexReplace.class, new RegexReplaceFactory());
        Optional<CellProcessor> processor = handlerFactory.create(Optional.empty(), field, formatter, config, BuildCase.Read, groupEmpty);
        printCellProcessorChain(processor.get(), name.getMethodName());
        
        CellProcessor actual = processor.get();
        assertThat(actual).isInstanceOf(RegexReplace.class);
        
        {
            String input = "無期限";
            String output = "9999/12/31";
            assertThat((Object)actual.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(output);
        }
        
    }
    
    /**
     * 独自のアノテーション
     */
    @Test
    public void testCreate_custom() {
        
        FieldAccessor field = getFieldAccessor(TestCsv.class, "col_custom", comparator);
        StringProcessorBuilder builder = (StringProcessorBuilder) builderResolver.resolve(String.class);
        TextFormatter<String> formatter = builder.getFormatter(field, config);
        
        Optional<CellProcessor> processor = handlerFactory.create(Optional.empty(), field, formatter, config, BuildCase.Read, groupEmpty);
        printCellProcessorChain(processor.get(), name.getMethodName());
        
        CellProcessor actual = processor.get();
        assertThat(actual).isInstanceOf(CustomConversion.class);
        
        {
            String input = "こんにちは";
            String output = "こんにちはHello!";
            assertThat((Object)actual.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(output);
        }
        
    }
    
    /**
     * 属性groupsの指定
     */
    @Test
    public void testCreate_groups() {
        
        FieldAccessor field = getFieldAccessor(TestCsv.class, "col_groups", comparator);
        StringProcessorBuilder builder = (StringProcessorBuilder) builderResolver.resolve(String.class);
        TextFormatter<String> formatter = builder.getFormatter(field, config);
        
        handlerFactory.register(CsvRegexReplace.class, new RegexReplaceFactory());
        
        {
            // グループの指定なし - デフォルトグループ
            Optional<CellProcessor> processor = handlerFactory.create(Optional.empty(), field, formatter, config, BuildCase.Read, groupEmpty);
            printCellProcessorChain(processor.get(), name.getMethodName());
            
            CellProcessor actual = processor.get();
            assertThat(actual).isInstanceOf(CustomConversion.class);
            
            String input = "こんにちは";
            String output = "こんにちはHello!";
            assertThat((Object)actual.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(output);
            
        }
        
        {
            // グループの指定あり
            Optional<CellProcessor> processor = handlerFactory.create(Optional.empty(), field, formatter, config, BuildCase.Read, new Class[]{Group1.class});
            printCellProcessorChain(processor.get(), name.getMethodName());
            
            CellProcessor actual = processor.get();
            assertThat(actual).isInstanceOf(RegexReplace.class);
            
            String input = "無期限";
            String output = "9999/12/31";
            assertThat((Object)actual.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(output);
            
        }
        
    }
    
    /**
     * 属性orderの指定 1
     */
    @Test
    public void testCreate_order1() {
        
        FieldAccessor field = getFieldAccessor(TestCsv.class, "col_order1", comparator);
        StringProcessorBuilder builder = (StringProcessorBuilder) builderResolver.resolve(String.class);
        TextFormatter<String> formatter = builder.getFormatter(field, config);
        
        handlerFactory.register(CsvRegexReplace.class, new RegexReplaceFactory());
        
        Optional<CellProcessor> processor = handlerFactory.create(Optional.empty(), field, formatter, config, BuildCase.Read, groupEmpty);
        printCellProcessorChain(processor.get(), name.getMethodName());
        
        CellProcessor actual = processor.get();
        assertThat(actual).isInstanceOf(CustomConversion.class);
        
        String input = "こんにちは";
        String output = "こんにちはHello!World!";
        assertThat((Object)actual.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(output);
        
    }
    
    /**
     * 属性orderの指定 2
     */
    @Test
    public void testCreate_order2() {
        
        FieldAccessor field = getFieldAccessor(TestCsv.class, "col_order2", comparator);
        StringProcessorBuilder builder = (StringProcessorBuilder) builderResolver.resolve(String.class);
        TextFormatter<String> formatter = builder.getFormatter(field, config);
        
        handlerFactory.register(CsvRegexReplace.class, new RegexReplaceFactory());
        
        Optional<CellProcessor> processor = handlerFactory.create(Optional.empty(), field, formatter, config, BuildCase.Read, groupEmpty);
        printCellProcessorChain(processor.get(), name.getMethodName());
        
        CellProcessor actual = processor.get();
        assertThat(actual).isInstanceOf(CustomConversion.class);
        
        String input = "こんにちは";
        String output = "こんにちはWorld!Hello!";
        assertThat((Object)actual.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(output);
        
    }
    
    /**
     * 属性casesの指定 - 読み込み時の場合
     */
    @Test
    public void testCreate_cases_read() {
        
        FieldAccessor field = getFieldAccessor(TestCsv.class, "col_cases", comparator);
        StringProcessorBuilder builder = (StringProcessorBuilder) builderResolver.resolve(String.class);
        TextFormatter<String> formatter = builder.getFormatter(field, config);
        
        handlerFactory.register(CsvLeftPad.class, new LeftPadFactory());
        handlerFactory.register(CsvRightPad.class, new RightPadFactory());
        handlerFactory.register(CsvUpper.class, new UpperFactory());
        handlerFactory.register(CsvLower.class, new LowerFactory());
        
        Optional<CellProcessor> processor = handlerFactory.create(Optional.empty(), field, formatter, config, BuildCase.Read, groupEmpty);
        printCellProcessorChain(processor, name.getMethodName());
        
        CellProcessor actual = processor.get();
        
        // 指定したcasesを持つかどうか
        assertThat(actual).hasCellProcessor(LeftPad.class)
            .hasCellProcessor(RightPad.class)
            .hasCellProcessor(Lower.class);
        
    }
    
    /**
     * 属性casesの指定 - 書き込み時の場合
     */
    @Test
    public void testCreate_cases_write() {
        
        FieldAccessor field = getFieldAccessor(TestCsv.class, "col_cases", comparator);
        StringProcessorBuilder builder = (StringProcessorBuilder) builderResolver.resolve(String.class);
        TextFormatter<String> formatter = builder.getFormatter(field, config);
        
        handlerFactory.register(CsvLeftPad.class, new LeftPadFactory());
        handlerFactory.register(CsvRightPad.class, new RightPadFactory());
        handlerFactory.register(CsvUpper.class, new UpperFactory());
        handlerFactory.register(CsvLower.class, new LowerFactory());
        
        Optional<CellProcessor> processor = handlerFactory.create(Optional.empty(), field, formatter, config, BuildCase.Write, groupEmpty);
        printCellProcessorChain(processor, name.getMethodName());
        
        CellProcessor actual = processor.get();
        
        // 指定したcasesを持つかどうか
        assertThat(actual).hasCellProcessor(LeftPad.class)
            .hasCellProcessor(Upper.class)
            .hasCellProcessor(Lower.class);
        
        
    }
    
}

package com.github.mygreen.supercsv.cellprocessor;

import static org.junit.Assert.*;
import static org.assertj.core.api.Assertions.*;
import static com.github.mygreen.supercsv.tool.TestUtils.*;
import static com.github.mygreen.supercsv.tool.HasCellProcessorAssert.*;

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
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;
import org.supercsv.exception.SuperCsvCellProcessorException;
import org.supercsv.util.CsvContext;

import com.github.mygreen.supercsv.annotation.CsvBean;
import com.github.mygreen.supercsv.annotation.CsvColumn;
import com.github.mygreen.supercsv.annotation.constraint.CsvConstraint;
import com.github.mygreen.supercsv.annotation.constraint.CsvLengthBetween;
import com.github.mygreen.supercsv.annotation.constraint.CsvLengthExact;
import com.github.mygreen.supercsv.annotation.constraint.CsvLengthMax;
import com.github.mygreen.supercsv.annotation.constraint.CsvLengthMin;
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
import com.github.mygreen.supercsv.cellprocessor.format.TextFormatter;
import com.github.mygreen.supercsv.exception.SuperCsvValidationException;

/**
 * {@link ConstraintProcessorHandler}のテスタ
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class ConstraintProcessorHandlerTest {
    
    @Rule
    public TestName name = new TestName();
    
    private Configuration config;
    private ProcessorBuilderResolver builderResolver;
    private Comparator<Annotation> comparator;
    private ConstraintProcessorHandler handlerFactory;
    
    private final Class<?>[] groupEmpty = new Class[]{};
    
    @Before
    public void setUp() throws Exception {
        this.config = new Configuration();
        this.builderResolver = config.getBuilderResolver();
        this.comparator = config.getAnnoationComparator();
        this.handlerFactory = new ConstraintProcessorHandler();
        
    }
    
    @Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @Repeatable(CsvCustomConstraint.List.class)
    @CsvConstraint(value=CustomConstraintFactory.class)
    public static @interface CsvCustomConstraint {
        
        String value();
        
        Class<?>[] groups() default {};
        
        int order() default 0;
        
        @Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
        @Retention(RetentionPolicy.RUNTIME)
        @Documented
        @interface List {
            
            CsvCustomConstraint[] value();
        }
    }
    
    private static class CustomConstraintFactory implements ConstraintProcessorFactory<CsvCustomConstraint> {
        
        @Override
        public Optional<CellProcessor> create(CsvCustomConstraint anno, Optional<CellProcessor> next,
                FieldAccessor field, TextFormatter<?> formatter, Configuration config) {
            
            if(!String.class.isAssignableFrom(field.getType())) {
                // 検証対象のクラスタイプと一致しない場合は、弾きます。
                return next;
            }
            
            // CellProcessorのインスタンスを作成します
            final CustomConstraint processor = next.map(n ->  new CustomConstraint(anno.value(), n))
                    .orElseGet(() -> new CustomConstraint(anno.value()));
            
            return Optional.of(processor);
            
        }
        
    }
    
    private static class CustomConstraint extends ValidationCellProcessor implements StringCellProcessor {
        
        private String text;
        
        CustomConstraint(final String text) {
            super();
            checkPreconditions(text);
            this.text = text;
        }
        
        CustomConstraint(final String text, final CellProcessor next) {
            super(next);
            checkPreconditions(text);
            this.text = text;
        }
        
        private static void checkPreconditions(final String text) {
            if(text == null) {
                throw new NullPointerException("text should not be null.");
            } else if(text.isEmpty()) {
                throw new NullPointerException("text should not be empty.");
            }
        }
        
        @Override
        public <T> T execute(final Object value, final CsvContext context) {
            if(value == null) {
                return next.execute(value, context);
            }
            
            final String result;
            if(value instanceof String) {
                result = (String)value;
                
            } else {
                // 検証対象のクラスタイプが不正な場合
                throw new SuperCsvCellProcessorException(String.class, value, context, this);
            }
            
            // 最後が指定した値で終了するかどうか
            if(result.endsWith(text)) {
                // 正常な値の場合、次の処理に委譲します。
                return next.execute(value, context);
            }
            
            // エラーがある場合、例外クラスを組み立てます。
            throw createValidationException(context)
                .messageFormat("Not ends with %s.", text)
                .messageVariables("suffix", text)
                .build();
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
        @CsvLengthMax(5)
        String col_register;
        
        @CsvColumn(number=3)
        @CsvCustomConstraint(".csv")
        String col_custom;
        
        @CsvColumn(number=4)
        @CsvLengthMax(value=5, groups=Group1.class)
        @CsvCustomConstraint(".csv")
        String col_groups;
        
        @CsvColumn(number=5)
        @CsvLengthMax(value=5, order=1)
        @CsvCustomConstraint(value=".csv", order=2)
        String col_order1;
        
        @CsvColumn(number=6)
        @CsvLengthMax(value=5, order=2)
        @CsvCustomConstraint(value=".csv", order=1)
        String col_order2;
        
        @CsvColumn(number=7)
        @CsvLengthMax(value=5, order=1, cases={})
        @CsvLengthMin(value=0, order=2, cases={BuildCase.Read})
        @CsvLengthBetween(min=0, max=5, order=3, cases={BuildCase.Write})
        @CsvLengthExact(value={3, 5}, order=5, cases={BuildCase.Read, BuildCase.Write})
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
        
        handlerFactory.register(CsvLengthMax.class, new LengthMaxFactory());
        Optional<CellProcessor> processor = handlerFactory.create(Optional.empty(), field, formatter, config, BuildCase.Read, groupEmpty);
        printCellProcessorChain(processor, name.getMethodName());
        
        CellProcessor actual = processor.get();
        assertThat(actual).isInstanceOf(LengthMax.class);
        
        {
            String input = "sample.txt";
            try {
                actual.execute(input, ANONYMOUS_CSVCONTEXT);
            } catch(Exception e) {
                assertThat(e).isInstanceOf(SuperCsvValidationException.class);
                
                SuperCsvValidationException validationException = (SuperCsvValidationException)e;
                assertThat(validationException.getProcessor()).isInstanceOf(LengthMax.class);
            }
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
        printCellProcessorChain(processor, name.getMethodName());
        
        CellProcessor actual = processor.get();
        assertThat(actual).isInstanceOf(CustomConstraint.class);
        
        {
            String input = "sample.txt";
            try {
                actual.execute(input, ANONYMOUS_CSVCONTEXT);
            } catch(Exception e) {
                assertThat(e).isInstanceOf(SuperCsvValidationException.class);
                
                SuperCsvValidationException validationException = (SuperCsvValidationException)e;
                assertThat(validationException.getProcessor()).isInstanceOf(CustomConstraint.class);
            }
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
        
        handlerFactory.register(CsvLengthMax.class, new LengthMaxFactory());
        
        {
            // グループの指定なし - デフォルトグループ
            Optional<CellProcessor> processor = handlerFactory.create(Optional.empty(), field, formatter, config, BuildCase.Read, groupEmpty);
            printCellProcessorChain(processor, name.getMethodName());
            
            CellProcessor actual = processor.get();
            assertThat(actual).isInstanceOf(CustomConstraint.class);
            
            
            String input = "sample.txt";
            try {
                actual.execute(input, ANONYMOUS_CSVCONTEXT);
            } catch(Exception e) {
                assertThat(e).isInstanceOf(SuperCsvValidationException.class);
                
                SuperCsvValidationException validationException = (SuperCsvValidationException)e;
                assertThat(validationException.getProcessor()).isInstanceOf(CustomConstraint.class);
            }
            
        }
        
        {
            // グループの指定あり
            Optional<CellProcessor> processor = handlerFactory.create(Optional.empty(), field, formatter, config, BuildCase.Read, new Class[]{Group1.class});
            printCellProcessorChain(processor, name.getMethodName());
            
            CellProcessor actual = processor.get();
            assertThat(actual).isInstanceOf(LengthMax.class);
            
            String input = "sample.txt";
            try {
                actual.execute(input, ANONYMOUS_CSVCONTEXT);
            } catch(Exception e) {
                assertThat(e).isInstanceOf(SuperCsvValidationException.class);
                
                SuperCsvValidationException validationException = (SuperCsvValidationException)e;
                assertThat(validationException.getProcessor()).isInstanceOf(LengthMax.class);
            }
            
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
        
        handlerFactory.register(CsvLengthMax.class, new LengthMaxFactory());
        
        Optional<CellProcessor> processor = handlerFactory.create(Optional.empty(), field, formatter, config, BuildCase.Read, groupEmpty);
        printCellProcessorChain(processor, name.getMethodName());
        
        CellProcessor actual = processor.get();
        assertThat(actual).isInstanceOf(LengthMax.class);
        
        String input = "sample.txt";
        try {
            actual.execute(input, ANONYMOUS_CSVCONTEXT);
        } catch(Exception e) {
            assertThat(e).isInstanceOf(SuperCsvValidationException.class);
            
            SuperCsvValidationException validationException = (SuperCsvValidationException)e;
            assertThat(validationException.getProcessor()).isInstanceOf(LengthMax.class);
        }
        
    }
    
    /**
     * 属性orderの指定 2
     */
    @Test
    public void testCreate_order2() {
        
        FieldAccessor field = getFieldAccessor(TestCsv.class, "col_order2", comparator);
        StringProcessorBuilder builder = (StringProcessorBuilder) builderResolver.resolve(String.class);
        TextFormatter<String> formatter = builder.getFormatter(field, config);
        
        handlerFactory.register(CsvLengthMax.class, new LengthMaxFactory());
        
        Optional<CellProcessor> processor = handlerFactory.create(Optional.empty(), field, formatter, config, BuildCase.Read, groupEmpty);
        printCellProcessorChain(processor, name.getMethodName());
        
        CellProcessor actual = processor.get();
        assertThat(actual).isInstanceOf(CustomConstraint.class);
        
        String input = "sample.txt";
        try {
            actual.execute(input, ANONYMOUS_CSVCONTEXT);
        } catch(Exception e) {
            assertThat(e).isInstanceOf(SuperCsvValidationException.class);
            
            SuperCsvValidationException validationException = (SuperCsvValidationException)e;
            assertThat(validationException.getProcessor()).isInstanceOf(CustomConstraint.class);
        }
        
    }
    
    /**
     * 属性casesの指定 - 読み込み時の場合
     */
    @Test
    public void testCreate_cases_read() {
        
        FieldAccessor field = getFieldAccessor(TestCsv.class, "col_cases", comparator);
        StringProcessorBuilder builder = (StringProcessorBuilder) builderResolver.resolve(String.class);
        TextFormatter<String> formatter = builder.getFormatter(field, config);
        
        handlerFactory.register(CsvLengthMax.class, new LengthMaxFactory());
        handlerFactory.register(CsvLengthMin.class, new LengthMinFactory());
        handlerFactory.register(CsvLengthBetween.class, new LengthBetweenFactory());
        handlerFactory.register(CsvLengthExact.class, new LengthExactFactory());
        
        Optional<CellProcessor> processor = handlerFactory.create(Optional.empty(), field, formatter, config, BuildCase.Read, groupEmpty);
        printCellProcessorChain(processor, name.getMethodName());
        
        CellProcessor actual = processor.get();
        
        // 指定したcasesを持つかどうか
        assertThat(actual).hasCellProcessor(LengthMax.class)
            .hasCellProcessor(LengthMin.class)
            .hasCellProcessor(LengthExact.class);
        
    }
    
    /**
     * 属性casesの指定 - 書き込み時の場合
     */
    @Test
    public void testCreate_cases_write() {
        
        FieldAccessor field = getFieldAccessor(TestCsv.class, "col_cases", comparator);
        StringProcessorBuilder builder = (StringProcessorBuilder) builderResolver.resolve(String.class);
        TextFormatter<String> formatter = builder.getFormatter(field, config);
        
        handlerFactory.register(CsvLengthMax.class, new LengthMaxFactory());
        handlerFactory.register(CsvLengthMin.class, new LengthMinFactory());
        handlerFactory.register(CsvLengthBetween.class, new LengthBetweenFactory());
        handlerFactory.register(CsvLengthExact.class, new LengthExactFactory());
        
        Optional<CellProcessor> processor = handlerFactory.create(Optional.empty(), field, formatter, config, BuildCase.Write, groupEmpty);
        printCellProcessorChain(processor, name.getMethodName());
        
        CellProcessor actual = processor.get();
        
        // 指定したcasesを持つかどうか
        assertThat(actual).hasCellProcessor(LengthMax.class)
            .hasCellProcessor(LengthBetween.class)
            .hasCellProcessor(LengthExact.class);
        
    }
    
}

package com.github.mygreen.supercsv.cellprocessor.conversion;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;
import static com.github.mygreen.supercsv.tool.TestUtils.*;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.supercsv.cellprocessor.ift.CellProcessor;

import com.github.mygreen.supercsv.annotation.CsvBean;
import com.github.mygreen.supercsv.annotation.CsvColumn;
import com.github.mygreen.supercsv.annotation.conversion.CsvWordReplace;
import com.github.mygreen.supercsv.builder.AnnotationComparator;
import com.github.mygreen.supercsv.builder.ProcessorBuilderResolver;
import com.github.mygreen.supercsv.builder.BuildCase;
import com.github.mygreen.supercsv.builder.Configuration;
import com.github.mygreen.supercsv.builder.FieldAccessor;
import com.github.mygreen.supercsv.builder.standard.StringProcessorBuilder;
import com.github.mygreen.supercsv.cellprocessor.format.TextFormatter;
import com.github.mygreen.supercsv.exception.SuperCsvInvalidAnnotationException;

/**
 * {@link WordReplaceFactory}のテスタ
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class WordReplaceFactoryTest {
    
    @Rule
    public TestName name = new TestName();
    
    private WordReplaceFactory factory;
    
    private Configuration config;
    
    private final Class<?>[] groupEmpty = new Class[]{};
    private final AnnotationComparator comparator = new AnnotationComparator();
    private final ProcessorBuilderResolver builderResolver = new ProcessorBuilderResolver();
    
    @Before
    public void setUp() throws Exception {
        this.factory = new WordReplaceFactory();
        
        this.config = new Configuration();
    }
    
    @CsvBean
    private static class TestCsv {
        
        @CsvColumn(number=1)
        @CsvWordReplace(words={"下さい", "御願い"}, replacements={"ください", "お願い"})
        private String col_default;
        
        @CsvColumn(number=2)
        @CsvWordReplace(provider=FileReplacedWordProvider.class)
        private String col_provider;
        
    }
    
    @CsvBean
    private static class ErrorCsv {
        
        @CsvColumn(number=1)
        @CsvWordReplace(words={"下さい", "御願い"}, replacements={"ください"})
        private String col_size_noMatch;
        
        @CsvColumn(number=1)
        @CsvWordReplace
        private String col_no_value_provider;
        
    }
    
    private static class FileReplacedWordProvider implements ReplacedWordProvider {
        
        @Override
        public Collection<Word> getReplacedWords(final FieldAccessor field) {
            
            // ファイルから語彙の定義を読み込む
            final List<String> lines;
            try {
                lines = Files.readAllLines(
                        new File("src/test/data/data_replaced_word.txt").toPath(), Charset.forName("UTF-8"));
                        
            } catch(IOException e) {
                throw new RuntimeException("fail reading the replaced words file.", e);
            }
            
            // 読み込んだ各行の値を分割して、ReplacedWord クラスに変換する。
            return lines.stream()
                    .map(l -> l.split(","))
                    .map(s -> new Word(s[0], s[1]))
                    .collect(Collectors.toList());
        }
        
    }
    
    @Test
    public void testCreate_default() {
        
        FieldAccessor field = getFieldAccessor(TestCsv.class, "col_default", comparator);
        StringProcessorBuilder builder = (StringProcessorBuilder) builderResolver.resolve(String.class);
        TextFormatter<String> formatter = builder.getFormatter(field, config);
        
        CsvWordReplace anno = field.getAnnotationsByGroup(CsvWordReplace.class, groupEmpty).get(0);
        
        {
            //next null
            Optional<CellProcessor> processor = factory.create(anno, Optional.empty(), field, formatter, config);
            printCellProcessorChain(processor.get(), name.getMethodName());
            
            assertThat(processor.get()).isInstanceOf(WordReplace.class);
            
            WordReplace actual = (WordReplace)processor.get();
            
            {
                String input = "送信をして下さい。御願い致します。";
                String expected = "送信をしてください。お願い致します。";
                assertThat((Object)actual.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
            }
        }
        
        {
            //next exist
            Optional<CellProcessor> processor = factory.create(anno, Optional.of(new NextCellProcessor()), field, formatter, config);
            printCellProcessorChain(processor.get(), name.getMethodName());
            
            assertThat(processor.get()).isInstanceOf(WordReplace.class);
            
            WordReplace actual = (WordReplace)processor.get();
            
            {
                String input = "送信をして下さい。御願い致します。";
                String expected = "送信をしてください。お願い致します。";
                assertThat((Object)actual.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
            }
        }
        
    }
    
    /**
     * 属性providerの指定
     */
    @Test
    public void testCreate_attrProvider() {
        
        FieldAccessor field = getFieldAccessor(TestCsv.class, "col_provider", comparator);
        StringProcessorBuilder builder = (StringProcessorBuilder) builderResolver.resolve(String.class);
        TextFormatter<String> formatter = builder.getFormatter(field, config);
        
        CsvWordReplace anno = field.getAnnotationsByGroup(CsvWordReplace.class, groupEmpty).get(0);
        
        {
            //next null
            Optional<CellProcessor> processor = factory.create(anno, Optional.empty(), field, formatter, config);
            printCellProcessorChain(processor.get(), name.getMethodName());
            
            assertThat(processor.get()).isInstanceOf(WordReplace.class);
            
            WordReplace actual = (WordReplace)processor.get();
            
            {
                String input = "送信をして下さい。御願い致します。";
                String expected = "送信をしてください。お願い致します。";
                assertThat((Object)actual.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
            }
        }
        
        {
            //next exist
            Optional<CellProcessor> processor = factory.create(anno, Optional.of(new NextCellProcessor()), field, formatter, config);
            printCellProcessorChain(processor.get(), name.getMethodName());
            
            assertThat(processor.get()).isInstanceOf(WordReplace.class);
            
            WordReplace actual = (WordReplace)processor.get();
            
            {
                String input = "送信をして下さい。御願い致します。";
                String expected = "送信をしてください。お願い致します。";
                assertThat((Object)actual.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
            }
        }
    }
    
    /**
     * 語彙の個数が一致しない場合
     */
    @Test
    public void testCreate_error_sizeNoMatch() {
        
        
        FieldAccessor field = getFieldAccessor(ErrorCsv.class, "col_size_noMatch", comparator);
        StringProcessorBuilder builder = (StringProcessorBuilder) builderResolver.resolve(String.class);
        TextFormatter<String> formatter = builder.getFormatter(field, config);
        
        CsvWordReplace anno = field.getAnnotationsByGroup(CsvWordReplace.class, groupEmpty).get(0);
        
        try {
            //next null
            factory.create(anno, Optional.empty(), field, formatter, config);
            fail();
            
        } catch(Exception e) {
            
            assertThat(e).isInstanceOf(SuperCsvInvalidAnnotationException.class)
                .hasMessage("'%s' において、アノテーション @CsvWordReplace の属性 'words' と 'replacements' の配列のサイズが一致しません。設定されている属性 'words' のサイズは2、'replacements' のサイズは1です。",
                        field.getNameWithClass());
//            e.printStackTrace();
            
        }
        
    }
    
    /**
     * 属性value, providerの指定がない
     */
    @Test
    public void testCreate_attrNoValueProvider() {
        
        FieldAccessor field = getFieldAccessor(ErrorCsv.class, "col_no_value_provider", comparator);
        StringProcessorBuilder builder = (StringProcessorBuilder) builderResolver.resolve(String.class);
        TextFormatter<String> formatter = builder.getFormatter(field, config);
        
        CsvWordReplace anno = field.getAnnotationsByGroup(CsvWordReplace.class, groupEmpty).get(0);
        
        try {
            factory.create(anno, Optional.empty(), field, formatter, config);
            fail();
            
        } catch(Exception e) {
            assertThat(e).isInstanceOf(SuperCsvInvalidAnnotationException.class);
            
            SuperCsvInvalidAnnotationException exception = (SuperCsvInvalidAnnotationException)e;
            assertThat(exception.getTargetAnnotation()).isInstanceOf(CsvWordReplace.class);
            
//            e.printStackTrace();
            
        }
        
    }
    
}

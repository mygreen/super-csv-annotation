package com.github.mygreen.supercsv.builder;

import static org.junit.Assert.*;
import static org.assertj.core.api.Assertions.*;
import static com.github.mygreen.supercsv.tool.TestUtils.*;
import static com.github.mygreen.supercsv.tool.HasCellProcessorAssert.*;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.supercsv.cellprocessor.FmtNumber;
import org.supercsv.cellprocessor.ParseInt;
import org.supercsv.cellprocessor.Trim;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.exception.SuperCsvCellProcessorException;
import org.supercsv.exception.SuperCsvConstraintViolationException;

import com.github.mygreen.supercsv.annotation.CsvBean;
import com.github.mygreen.supercsv.annotation.CsvColumn;
import com.github.mygreen.supercsv.localization.EncodingControl;
import com.github.mygreen.supercsv.localization.MessageResolver;
import com.github.mygreen.supercsv.localization.ResourceBundleMessageResolver;
import com.github.mygreen.supercsv.validation.CsvExceptionConverter;


/**
 * {@link ProcessorBuilder}のテスタ
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class ProcesssorBuilderTest {
    
    @Rule
    public TestName name = new TestName();
    
    private BeanMappingFactory beanMappingFactory;
    private CsvExceptionConverter exceptionConverter;
    
    private final Class<?>[] groupEmpty = new Class[]{};
    
    private MessageResolver testMessageResolver;
    
    @Before
    public void setUp() throws Exception {
        
        this.beanMappingFactory = new BeanMappingFactory();
        this.exceptionConverter = new CsvExceptionConverter();
        
        this.testMessageResolver = new ResourceBundleMessageResolver(ResourceBundle.getBundle("TestMessages", new EncodingControl("UTF-8")));
    }
    
    @CsvBean
    private static class TestCsv {
        
        @CsvColumn(number=1, builder=CustomProcessorBuilder.class)
        private Integer col_default;
        
    }
    
    private static class CustomProcessorBuilder implements ProcessorBuilder<Integer> {
        
        @Override
        public Optional<CellProcessor> buildForReading(final Class<Integer> type, final FieldAccessor field,
                final Configuration config, final Class<?>[] groups) {
            
            CellProcessor processor = new NotNull(new Trim(new ParseInt()));
            return Optional.of(processor);
        }
        
        @Override
        public Optional<CellProcessor> buildForWriting(final Class<Integer> type, final FieldAccessor field,
                final Configuration config, final Class<?>[] groups) {
            
            CellProcessor processor = new NotNull(new FmtNumber(new DecimalFormat("#,##0")));
            return Optional.of(processor);
        }
        
    }
    
    @Test
    public void testRead() {
        
        BeanMapping<TestCsv> beanMapping = beanMappingFactory.create(TestCsv.class, groupEmpty);
        ColumnMapping columnMapping = beanMapping.getColumnMapping("col_default").get();
        
        CellProcessor processor = columnMapping.getCellProcessorForReading();
        
        printCellProcessorChain(processor, name.getMethodName());
        
        {
            // null input
            String input = null;
            assertThatThrownBy(() -> processor.execute(input, testCsvContext(columnMapping, input)))
                    .isInstanceOf(SuperCsvConstraintViolationException.class);
        }
        
        {
            // valid input
            String input = " 100 ";
            Integer expected = 100;
            assertThat((Object)processor.execute(input, testCsvContext(columnMapping, input))).isEqualTo(expected);
        }
        
        {
            // wrong input - wrong format
            String input = "abc";
            try {
                processor.execute(input, testCsvContext(columnMapping, input));
                fail();
                
            } catch(Exception e) {
                assertThat(e).isInstanceOf(SuperCsvCellProcessorException.class);
                
                SuperCsvCellProcessorException processorException = (SuperCsvCellProcessorException)e;
                assertThat(processorException.getProcessor()).isInstanceOf(ParseInt.class);
                
            }
        }
        
    }
    
    @Test
    public void testWite() {
        
        BeanMapping<TestCsv> beanMapping = beanMappingFactory.create(TestCsv.class, groupEmpty);
        ColumnMapping columnMapping = beanMapping.getColumnMapping("col_default").get();
        
        CellProcessor processor = columnMapping.getCellProcessorForWriting();
        
        printCellProcessorChain(processor, name.getMethodName());
        
        {
            // null input
            Integer input = null;
            assertThatThrownBy(() -> processor.execute(input, testCsvContext(columnMapping, input)))
                    .isInstanceOf(SuperCsvConstraintViolationException.class);
        }
        
        {
            // valid input
            Integer input = 10_000;
            String expected = "10,000";
            assertThat((Object)processor.execute(input, testCsvContext(columnMapping, input))).isEqualTo(expected);
        }
        
    }
    
    @Test
    public void testErrorMessage_default() {
        
        BeanMapping<TestCsv> beanMapping = beanMappingFactory.create(TestCsv.class, groupEmpty);
        ColumnMapping columnMapping = beanMapping.getColumnMapping("col_default").get();
        
        CellProcessor processor = columnMapping.getCellProcessorForReading();
        
        printCellProcessorChain(processor, name.getMethodName());
        
        // wrong input - wrong format
        String input = "abc";
        try {
            processor.execute(input, testCsvContext(columnMapping, input));
            fail();
            
        } catch(Exception e) {
            assertThat(e).isInstanceOf(SuperCsvCellProcessorException.class);
            
            SuperCsvCellProcessorException processorException = (SuperCsvCellProcessorException)e;
            assertThat(processorException.getProcessor()).isInstanceOf(ParseInt.class);
            
            List<String> messages = exceptionConverter.convertAndFormat((SuperCsvCellProcessorException)e, beanMapping);
            assertThat(messages).hasSize(1)
                .contains("'abc' could not be parsed as an Integer");
        }
        
    }
    
    /**
     * CellProcessor名で別途定義する
     */
    @Test
    public void testErrorMessage_custom() {
        
        // メッセージの入れ替え
        exceptionConverter.setMessageResolver(testMessageResolver);
        
        BeanMapping<TestCsv> beanMapping = beanMappingFactory.create(TestCsv.class, groupEmpty);
        ColumnMapping columnMapping = beanMapping.getColumnMapping("col_default").get();
        
        CellProcessor processor = columnMapping.getCellProcessorForReading();
        
        printCellProcessorChain(processor, name.getMethodName());
        
        // wrong input - wrong format
        String input = "abc";
        try {
            processor.execute(input, testCsvContext(columnMapping, input));
            fail();
            
        } catch(Exception e) {
            assertThat(e).isInstanceOf(SuperCsvCellProcessorException.class);
            
            SuperCsvCellProcessorException processorException = (SuperCsvCellProcessorException)e;
            assertThat(processorException.getProcessor()).isInstanceOf(ParseInt.class);
            
            List<String> messages = exceptionConverter.convertAndFormat((SuperCsvCellProcessorException)e, beanMapping);
            assertThat(messages).hasSize(1)
                .contains("[2行, 1列] : 項目「col_default」の値（abc）は、整数として不正です。");
        }
        
    }
}

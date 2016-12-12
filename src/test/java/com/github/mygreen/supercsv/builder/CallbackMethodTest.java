package com.github.mygreen.supercsv.builder;

import static org.junit.Assert.*;
import static org.assertj.core.api.Assertions.*;

import java.awt.Point;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.supercsv.prefs.CsvPreference;
import org.supercsv.util.CsvContext;

import com.github.mygreen.supercsv.annotation.CsvBean;
import com.github.mygreen.supercsv.annotation.CsvColumn;
import com.github.mygreen.supercsv.annotation.CsvPostRead;
import com.github.mygreen.supercsv.annotation.CsvPostWrite;
import com.github.mygreen.supercsv.annotation.CsvPreRead;
import com.github.mygreen.supercsv.annotation.CsvPreWrite;
import com.github.mygreen.supercsv.io.CsvAnnotationBeanReader;
import com.github.mygreen.supercsv.io.CsvAnnotationBeanWriter;
import com.github.mygreen.supercsv.validation.CsvBindingErrors;
import com.github.mygreen.supercsv.validation.CsvValidator;
import com.github.mygreen.supercsv.validation.ValidationContext;

/**
 * {@link CallbackMethod}のテスタ
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class CallbackMethodTest {
    
    private BeanMappingFactory beanMappingFactory;
    
    @Before
    public void setUp() throws Exception {
        this.beanMappingFactory = new BeanMappingFactory();
    }
    
    private static final String TEST_CSV = "ID,値"
            + "\r\n" + "1,a"
            + "\r\n" + "2,b"
            + "\r\n";
    
    // テスト用のグループ
    private interface Group1 { }
    private interface Group2 { }
    
    @CsvBean(validators=TestValidator.class, listeners=TestListener.class, header=true)
    public static class TestCsv {
        
        private List<String> messages = new ArrayList<>();
        
        @CsvColumn(number=1, label="ID")
        private Integer id;
        
        @CsvColumn(number=2, label="値")
        private String value;
        
        public TestCsv() {
            
        }
        
        public TestCsv(Integer id, String value) {
            this.id = id;
            this.value = value;
        }
        
        public void addMessage(String message, TestCsv record) {
            messages.add(message + "-" + record.id);
        }
        
        @CsvPreRead
        public void handlePreRead(final TestCsv record, final CsvContext context, 
                final CsvBindingErrors bindingErrors, final Class<?>[] groups, final Point point) {
            
            addMessage("method::handlePreRead", record);
            
            assertThat(record).isNotNull()
                .hasFieldOrPropertyWithValue("id", null)
                .hasFieldOrPropertyWithValue("value", null);
            assertThat(context).isNotNull();
            assertThat(bindingErrors).isNotNull();
            assertThat(groups).containsExactly(Group1.class, Group2.class);
            assertThat(point).isNull();;
            
            
        }
        
        @CsvPostRead
        public void handlePostRead(final TestCsv record, final CsvContext context, 
                final CsvBindingErrors bindingErrors, final Class<?>[] groups, final Point point) {
            
            addMessage("method::handlePostRead", record);
            
            assertThat(record).isNotNull()
                .hasNoNullFieldsOrProperties();
            assertThat(context).isNotNull()
                .hasNoNullFieldsOrProperties();
            assertThat(bindingErrors).isNotNull();
            assertThat(groups).containsExactly(Group1.class, Group2.class);
            assertThat(point).isNull();;
            
        }
        
        @CsvPreWrite
        public void handlePreWrite(final TestCsv record, final CsvContext context, 
                final CsvBindingErrors bindingErrors, final Class<?>[] groups, final Point point) {
            
            addMessage("method::handlePreWrite", record);
            
            assertThat(record).isNotNull()
                .hasNoNullFieldsOrProperties();
            assertThat(context).isNotNull()
                .hasNoNullFieldsOrProperties();
            assertThat(bindingErrors).isNotNull();
            assertThat(groups).containsExactly(Group1.class, Group2.class);
            assertThat(point).isNull();;
            
        }
        
        @CsvPostWrite
        public void handlePostWrite(final TestCsv record, final CsvContext context, 
                final CsvBindingErrors bindingErrors, final Class<?>[] groups, final Point point) {
            
            addMessage("method::handlePostWrite", record);
            
            assertThat(record).isNotNull()
                    .hasNoNullFieldsOrProperties();
            assertThat(context).isNotNull()
                .hasNoNullFieldsOrProperties();
            assertThat(bindingErrors).isNotNull();
            assertThat(groups).containsExactly(Group1.class, Group2.class);
            assertThat(point).isNull();;
            
        }
        
        public Integer getId() {
            return id;
        }
        
        public void setId(Integer id) {
            this.id = id;
        }
        
        public String getValue() {
            return value;
        }
        
        public void setValue(String value) {
            this.value = value;
        }
    }
    
    private static class TestListener {
        
        private List<String> messages = new ArrayList<>();
        
        public void addMessage(String message, TestCsv record) {
            messages.add(message + "-" + record.id);
        }
        
        @CsvPreRead
        public void handlePreRead(final TestCsv record, final CsvContext context, 
                final CsvBindingErrors bindingErrors, final Class<?>[] groups, final Point point) {
            
            addMessage("listener::handlePreRead", record);
            record.addMessage("listener::handlePreRead", record);
            
            assertThat(record).isNotNull();
            assertThat(context).isNotNull();
            assertThat(bindingErrors).isNotNull();
            assertThat(groups).containsExactly(Group1.class, Group2.class);
            assertThat(point).isNull();;
            
        }
        
        @CsvPostRead
        public void handlePostRead(final TestCsv record, final CsvContext context, 
                final CsvBindingErrors bindingErrors, final Class<?>[] groups, final Point point) {
            
            addMessage("method::handlePostRead", record);
            record.addMessage("listener::handlePostRead", record);
            
            assertThat(record).isNotNull();
            assertThat(context).isNotNull();
            assertThat(bindingErrors).isNotNull();
            assertThat(groups).containsExactly(Group1.class, Group2.class);
            assertThat(point).isNull();;
            
        }
        
        @CsvPreWrite
        public void handlePreWrite(final TestCsv record, final CsvContext context, 
                final CsvBindingErrors bindingErrors, final Class<?>[] groups, final Point point) {
            
            addMessage("method::handlePreWrite", record);
            record.addMessage("listener::handlePreWrite", record);
            
            assertThat(record).isNotNull();
            assertThat(context).isNotNull();
            assertThat(bindingErrors).isNotNull();
            assertThat(groups).containsExactly(Group1.class, Group2.class);
            assertThat(point).isNull();;
            
        }
        
        @CsvPostWrite
        public void handlePostWrite(final TestCsv record, final CsvContext context, 
                final CsvBindingErrors bindingErrors, final Class<?>[] groups, final Point point) {
            
            addMessage("method::handlePostWrite", record);
            record.addMessage("listener::handlePostWrite", record);
            
            assertThat(record).isNotNull();
            assertThat(context).isNotNull();
            assertThat(bindingErrors).isNotNull();
            assertThat(groups).containsExactly(Group1.class, Group2.class);
            assertThat(point).isNull();;
            
        }
        
    }
    
    private static class TestValidator implements CsvValidator<TestCsv> {
        
        private List<String> messages = new ArrayList<>();
        
        @Override
        public void validate(final TestCsv record, final CsvBindingErrors bindingErrors,
                final ValidationContext<TestCsv> validationContext) {
            
            record.addMessage("validate::validate", record);
            addMessage("validate::validate", record);
            
            assertThat(record).isNotNull();
            assertThat(bindingErrors).isNotNull();
            assertThat(validationContext).isNotNull();
        }
        
        public void addMessage(String message, TestCsv record) {
            messages.add(message + "-" + record.id);
        }
        
    }
    
    @Test
    public void testCreateBeanMapping() {
        
        BeanMapping<TestCsv> beanMapping = beanMappingFactory.create(TestCsv.class, Group1.class, Group2.class);
        
        assertThat(beanMapping.getValidators()).hasSize(1);
        assertThat(beanMapping.getListeners()).hasSize(1);
        assertThat(beanMapping.getPreReadMethods()).hasSize(2);
        assertThat(beanMapping.getPostReadMethods()).hasSize(2);
        assertThat(beanMapping.getPreWriteMethods()).hasSize(2);
        assertThat(beanMapping.getPostWriteMethods()).hasSize(2);
        
        
    }
    
    @Test
    public void testRead() throws Exception {
        
        CsvAnnotationBeanReader<TestCsv> csvReader = new CsvAnnotationBeanReader<>(TestCsv.class, new StringReader(TEST_CSV),
                CsvPreference.STANDARD_PREFERENCE, Group1.class, Group2.class);
        
        List<TestCsv> list = csvReader.readAll(false);
        for(TestCsv record : list) {
            assertThat(record.messages).containsExactly(
                    "method::handlePreRead-null",
                    "listener::handlePreRead-null",
                    "validate::validate-" + record.getId(),
                    "method::handlePostRead-" + record.getId(),
                    "listener::handlePostRead-" + record.getId());
        }
        
        csvReader.close();
        
    }
    
    @Test
    public void testWrite() throws Exception {
        
        beanMappingFactory.getConfiguration().setSkipValidationOnWrite(false);
        BeanMapping<TestCsv> beanMapping = beanMappingFactory.create(TestCsv.class, Group1.class, Group2.class);
        
        StringWriter writer = new StringWriter();
        CsvAnnotationBeanWriter<TestCsv> csvWriter = new CsvAnnotationBeanWriter<>(beanMapping, writer, 
                CsvPreference.STANDARD_PREFERENCE);
        
        List<TestCsv> list = new ArrayList<>();
        list.add(new TestCsv(1, "a"));
        list.add(new TestCsv(2, "b"));
        
        csvWriter.writeAll(list, true);
        csvWriter.close();
        
        for(TestCsv record : list) {
            assertThat(record.messages).containsExactly(
                    "method::handlePreWrite-" + record.getId(),
                    "listener::handlePreWrite-" + record.getId(),
                    "validate::validate-" + record.getId(),
                    "method::handlePostWrite-" + record.getId(),
                    "listener::handlePostWrite-" + record.getId());
        }
        
        String csv = writer.toString();
        assertThat(csv).isEqualTo(TEST_CSV);
    }
    
}

package com.github.mygreen.supercsv.builder;

import static org.junit.Assert.*;
import static org.assertj.core.api.Assertions.*;
import static com.github.mygreen.supercsv.tool.TestUtils.*;
import static com.github.mygreen.supercsv.tool.HasCellProcessorAssert.*;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;
import java.util.ResourceBundle;

import javax.xml.XMLConstants;
import javax.xml.bind.DataBindingException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.xml.sax.SAXException;

import com.github.mygreen.supercsv.annotation.CsvBean;
import com.github.mygreen.supercsv.annotation.CsvColumn;
import com.github.mygreen.supercsv.annotation.format.CsvFormat;
import com.github.mygreen.supercsv.cellprocessor.format.AbstractTextFormatter;
import com.github.mygreen.supercsv.cellprocessor.format.TextFormatter;
import com.github.mygreen.supercsv.cellprocessor.format.TextParseException;
import com.github.mygreen.supercsv.cellprocessor.format.TextPrintException;
import com.github.mygreen.supercsv.exception.SuperCsvInvalidAnnotationException;
import com.github.mygreen.supercsv.exception.SuperCsvValidationException;
import com.github.mygreen.supercsv.localization.EncodingControl;
import com.github.mygreen.supercsv.localization.MessageResolver;
import com.github.mygreen.supercsv.localization.ResourceBundleMessageResolver;
import com.github.mygreen.supercsv.validation.CsvExceptionConverter;

/**
 * {@link GeneralProcessorBuilder}のテスタ
 * <p>独自のクラスタイプ</p>
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class GeneralProcessorBuilderTest {
    
    @Rule
    public TestName name = new TestName();
    
    private BeanMappingFactory beanMappingFactory;
    private CsvExceptionConverter exceptionConverter;
    
    private final Class<?>[] groupEmpty = new Class[]{};
    
    private MessageResolver testMessageResolver;
    
    @Before
    public void setUp() {
        this.beanMappingFactory = new BeanMappingFactory();
        this.exceptionConverter = new CsvExceptionConverter();
        
        this.testMessageResolver = new ResourceBundleMessageResolver(ResourceBundle.getBundle("TestMessages", new EncodingControl("UTF-8")));
        
    }
    
    private static final String TEST_VALUE_STR_VALID = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
                + "\n<sample id=\"id1\">"
                + "\n    <value>Hello World!</value>"
                + "\n</sample>"
                + "\n";
    private static final String TEST_VALUE_STR_WRONG = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
            + "\n<sample>"
            + "\n    <value>Hello World!</value>"
            + "\n</sample>"
            + "\n";
    private static final SampleObject TEST_VALUE_OBJ_VALID = new SampleObject("id1", "Hello World!");
    private static final SampleObject TEST_VALUE_OBJ_WRONG = new SampleObject(null, "Hello World!");
    
    /**
     * アノテーション{@link CsvFormat}で独自のフォーマッタを指定する
     */
    @CsvBean
    private static class TestCustomFormatterCsv {
        
        @CsvColumn(number=1)
        @CsvFormat(formatter=SampleObjectFormatter.class)
        private SampleObject col_default;
        
        @CsvColumn(number=2)
        @CsvFormat(formatter=SampleObjectFormatter.class, message="テストメッセージ")
        private SampleObject col_message;
        
        @CsvColumn(number=3, builder=SampleObjectBuilder.class)
        private SampleObject col_builder;
    }
    
    /**
     * 独自のクラスの場合 - フォーマッタの指定がない場合
     *
     */
    @CsvBean
    private static class TestErrorNotFormatterCsv {
        
        @CsvColumn(number=1)
        private SampleObject col_default;
        
    }
    
    /**
     * 独自のクラス - JAXBによるXML変換
     *
     */
    @XmlRootElement(name="sample")
    @XmlAccessorType(XmlAccessType.FIELD)
    private static class SampleObject {
        
        @XmlAttribute(required=true)
        private String id;
        
        @XmlElement(name="value")
        private String data;
        
        public SampleObject() {
            
        }
        
        public SampleObject(final String id, final String data) {
            this.id = id;
            this.data = data;
        }
        
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((data == null) ? 0 : data.hashCode());
            result = prime * result + ((id == null) ? 0 : id.hashCode());
            return result;
        }
        
        @Override
        public boolean equals(Object obj) {
            if(this == obj) {
                return true;
            }
            if(obj == null) {
                return false;
            }
            if(!(obj instanceof SampleObject)) {
                return false;
            }
            SampleObject other = (SampleObject) obj;
            if(data == null) {
                if(other.data != null) {
                    return false;
                }
            } else if(!data.equals(other.data)) {
                return false;
            }
            if(id == null) {
                if(other.id != null) {
                    return false;
                }
            } else if(!id.equals(other.id)) {
                return false;
            }
            return true;
        }
        
    }
    
    private static class SampleObjectFormatter extends AbstractTextFormatter<SampleObject> {
        
        private static Schema schema;
        static {
            try {
                SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
                schema = factory.newSchema(GeneralProcessorBuilderTest.class.getResource("SampleObject.xsd"));
            } catch (SAXException e) {
                throw new RuntimeException("fail loading schema.", e);
            }
        }
        
        private static JAXBContext context;
        static {
            try {
                context = JAXBContext.newInstance(SampleObject.class);
            } catch (JAXBException e) {
                throw new RuntimeException("fail create jaxbcontext.", e);
            }
        }
        
        @Override
        public SampleObject parse(final String text) {
            
            try {
                Unmarshaller unmashaller = context.createUnmarshaller();
                unmashaller.setSchema(schema);
                
//                SampleObject object = JAXB.unmarshal(new StringReader(text), SampleObject.class);
                SampleObject object = (SampleObject) unmashaller.unmarshal(new StringReader(text));
                return object;
            } catch(JAXBException | DataBindingException e) {
                throw new TextParseException(text, SampleObject.class);
            }
        }
        
        @Override
        public String print(final SampleObject object) {
            
            try(StringWriter writer = new StringWriter()) {
                Marshaller marshaller = context.createMarshaller();
                marshaller.setSchema(schema);
                marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                
                marshaller.marshal(object, writer);
                
//                JAXB.marshal(object, writer);
                writer.flush();
                String text = writer.toString();
                return text;
                
            } catch(IOException | JAXBException | DataBindingException e) {
                throw new TextPrintException(object, e);
            }
        }
        
    }
    
    private static class SampleObjectBuilder extends AbstractProcessorBuilder<SampleObject> {

        @Override
        protected TextFormatter<SampleObject> getDefaultFormatter(FieldAccessor field,
                Configuration config) {
            return new SampleObjectFormatter();
        }
        
    }
    
    /**
     * 独自のクラスで、@CsvFormatの指定がない場合
     */
    @Test
    public void testCustomFormatter_noFormatter() {
        
        assertThatThrownBy(() -> beanMappingFactory.create(TestErrorNotFormatterCsv.class, groupEmpty))
            .isInstanceOf(SuperCsvInvalidAnnotationException.class)
            .hasMessage("'%s' において、アノテーション @CsvFormat の付与は必須です。",
                    TestErrorNotFormatterCsv.class.getName() + "#col_default");
        
    }
    
    @Test
    public void testCustomFormatter_read() {
        
        BeanMapping<TestCustomFormatterCsv> beanMapping = beanMappingFactory.create(TestCustomFormatterCsv.class, groupEmpty);
        ColumnMapping columnMapping = beanMapping.getColumnMapping("col_default").get();
        
        CellProcessor processor = columnMapping.getCellProcessorForReading();
        
        printCellProcessorChain(processor, name.getMethodName());
        
        {
            // null input
            String input = null;
            SampleObject expected = null;
            assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
        }
        
        {
            // valid input
            String input = TEST_VALUE_STR_VALID;
            SampleObject expected = TEST_VALUE_OBJ_VALID;
            assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
        }
        
        {
            // wrong input - wrong format
            String input = "abc";
            try {
                processor.execute(input, ANONYMOUS_CSVCONTEXT);
                fail();
                
            } catch(Exception e) {
                assertThat(e).isInstanceOf(SuperCsvValidationException.class);
                
                SuperCsvValidationException validationException = (SuperCsvValidationException)e;
                assertThat(validationException.isParedError()).isTrue();
                assertThat(validationException.getRejectedValue()).isEqualTo(input);
                assertThat(validationException.getValidationMessage()).isEqualTo("{com.github.mygreen.supercsv.cellprocessor.format.ParseProcessor.violated}");
                assertThat(validationException.getMessageVariables()).isEmpty();
                
            }
        }
        
        {
            // wrong input - wrong bind
            String input = TEST_VALUE_STR_WRONG;
            try {
                processor.execute(input, ANONYMOUS_CSVCONTEXT);
                fail();
                
            } catch(Exception e) {
                assertThat(e).isInstanceOf(SuperCsvValidationException.class);
                
                SuperCsvValidationException validationException = (SuperCsvValidationException)e;
                assertThat(validationException.isParedError()).isTrue();
                assertThat(validationException.getRejectedValue()).isEqualTo(input);
                assertThat(validationException.getValidationMessage()).isEqualTo("{com.github.mygreen.supercsv.cellprocessor.format.ParseProcessor.violated}");
                assertThat(validationException.getMessageVariables()).isEmpty();
                
            }
        }
        
    }
    
    @Test
    public void testCustomFormatter_write() {
        
        BeanMapping<TestCustomFormatterCsv> beanMapping = beanMappingFactory.create(TestCustomFormatterCsv.class, groupEmpty);
        ColumnMapping columnMapping = beanMapping.getColumnMapping("col_default").get();
        
        CellProcessor processor = columnMapping.getCellProcessorForWriting();
        
        printCellProcessorChain(processor, name.getMethodName());
        
        {
            // null input
            SampleObject input = null;
            String expected = null;
            assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
        }
        
        {
            // valid input
            SampleObject input = TEST_VALUE_OBJ_VALID;
            String expected = TEST_VALUE_STR_VALID;
            assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
        }
        
        {
            // wrong input - wrong bind
            SampleObject input = TEST_VALUE_OBJ_WRONG;
            try {
                processor.execute(input, ANONYMOUS_CSVCONTEXT);
                fail();
                
            } catch(Exception e) {
                assertThat(e).isInstanceOf(SuperCsvValidationException.class);
                
                SuperCsvValidationException validationException = (SuperCsvValidationException)e;
                assertThat(validationException.isParedError()).isFalse();
                assertThat(validationException.getRejectedValue()).isEqualTo(input);
                assertThat(validationException.getValidationMessage()).isEqualTo("{com.github.mygreen.supercsv.cellprocessor.format.PrintProcessor.violated}");
                assertThat(validationException.getMessageVariables()).isEmpty();
                
            }
        }
        
    }
    
    @Test
    public void testCustomBuilder_read() {
        
        BeanMapping<TestCustomFormatterCsv> beanMapping = beanMappingFactory.create(TestCustomFormatterCsv.class, groupEmpty);
        ColumnMapping columnMapping = beanMapping.getColumnMapping("col_builder").get();
        
        CellProcessor processor = columnMapping.getCellProcessorForReading();
        
        printCellProcessorChain(processor, name.getMethodName());
        
        {
            // null input
            String input = null;
            SampleObject expected = null;
            assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
        }
        
        {
            // valid input
            String input = TEST_VALUE_STR_VALID;
            SampleObject expected = TEST_VALUE_OBJ_VALID;
            assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
        }
        
        {
            // wrong input - wrong format
            String input = "abc";
            try {
                processor.execute(input, ANONYMOUS_CSVCONTEXT);
                fail();
                
            } catch(Exception e) {
                assertThat(e).isInstanceOf(SuperCsvValidationException.class);
                
                SuperCsvValidationException validationException = (SuperCsvValidationException)e;
                assertThat(validationException.isParedError()).isTrue();
                assertThat(validationException.getRejectedValue()).isEqualTo(input);
                assertThat(validationException.getValidationMessage()).isEqualTo("{com.github.mygreen.supercsv.cellprocessor.format.ParseProcessor.violated}");
                assertThat(validationException.getMessageVariables()).isEmpty();
                
            }
        }
        
        {
            // wrong input - wrong bind
            String input = TEST_VALUE_STR_WRONG;
            try {
                processor.execute(input, ANONYMOUS_CSVCONTEXT);
                fail();
                
            } catch(Exception e) {
                assertThat(e).isInstanceOf(SuperCsvValidationException.class);
                
                SuperCsvValidationException validationException = (SuperCsvValidationException)e;
                assertThat(validationException.isParedError()).isTrue();
                assertThat(validationException.getRejectedValue()).isEqualTo(input);
                assertThat(validationException.getValidationMessage()).isEqualTo("{com.github.mygreen.supercsv.cellprocessor.format.ParseProcessor.violated}");
                assertThat(validationException.getMessageVariables()).isEmpty();
                
            }
        }
        
    }
    
    @Test
    public void testCustomBuilder_write() {
        
        BeanMapping<TestCustomFormatterCsv> beanMapping = beanMappingFactory.create(TestCustomFormatterCsv.class, groupEmpty);
        ColumnMapping columnMapping = beanMapping.getColumnMapping("col_builder").get();
        
        CellProcessor processor = columnMapping.getCellProcessorForWriting();
        
        printCellProcessorChain(processor, name.getMethodName());
        
        {
            // null input
            SampleObject input = null;
            String expected = null;
            assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
        }
        
        {
            // valid input
            SampleObject input = TEST_VALUE_OBJ_VALID;
            String expected = TEST_VALUE_STR_VALID;
            assertThat((Object)processor.execute(input, ANONYMOUS_CSVCONTEXT)).isEqualTo(expected);
        }
        
        {
            // wrong input - wrong bind
            SampleObject input = TEST_VALUE_OBJ_WRONG;
            try {
                processor.execute(input, ANONYMOUS_CSVCONTEXT);
                fail();
                
            } catch(Exception e) {
                assertThat(e).isInstanceOf(SuperCsvValidationException.class);
                
                SuperCsvValidationException validationException = (SuperCsvValidationException)e;
                assertThat(validationException.isParedError()).isFalse();
                assertThat(validationException.getRejectedValue()).isEqualTo(input);
                assertThat(validationException.getValidationMessage()).isEqualTo("{com.github.mygreen.supercsv.cellprocessor.format.PrintProcessor.violated}");
                assertThat(validationException.getMessageVariables()).isEmpty();
                
            }
        }
        
    }
    
    @Test
    public void testErrorMessage_default() {
        
        BeanMapping<TestCustomFormatterCsv> beanMapping = beanMappingFactory.create(TestCustomFormatterCsv.class, groupEmpty);
        ColumnMapping columnMapping = beanMapping.getColumnMapping("col_default").get();
        
        CellProcessor processor = columnMapping.getCellProcessorForReading();
        
        printCellProcessorChain(processor, name.getMethodName());
                
        String input = "abc";
        try {
            processor.execute(input, testCsvContext(columnMapping, input));
            fail();
            
        } catch(Exception e) {
            assertThat(e).isInstanceOf(SuperCsvValidationException.class);
            
            List<String> messages = exceptionConverter.convertAndFormat((SuperCsvValidationException)e, beanMapping);
            assertThat(messages).hasSize(1)
                    .contains("[2行, 1列] : 項目「col_default」の値（abc）の書式は不正です。");                
        }
        
    }
    
    @Test
    public void testErrorMessage_default_write() {
        
        BeanMapping<TestCustomFormatterCsv> beanMapping = beanMappingFactory.create(TestCustomFormatterCsv.class, groupEmpty);
        ColumnMapping columnMapping = beanMapping.getColumnMapping("col_default").get();
        
        CellProcessor processor = columnMapping.getCellProcessorForWriting();
        
        printCellProcessorChain(processor, name.getMethodName());
                
        SampleObject input = TEST_VALUE_OBJ_WRONG;
        try {
            processor.execute(input, testCsvContext(columnMapping, input));
            fail();
            
        } catch(Exception e) {
            assertThat(e).isInstanceOf(SuperCsvValidationException.class);
            
            List<String> messages = exceptionConverter.convertAndFormat((SuperCsvValidationException)e, beanMapping);
            assertThat(messages).hasSize(1)
                    .contains("[2行, 1列] : 項目「col_default」の値の書き込みに失敗しました。");                
        }
        
    }
    
    /**
     * アノテーションの属性messageの指定
     */
    @Test
    public void testErrorMessage_message() {
        
        BeanMapping<TestCustomFormatterCsv> beanMapping = beanMappingFactory.create(TestCustomFormatterCsv.class, groupEmpty);
        ColumnMapping columnMapping = beanMapping.getColumnMapping("col_message").get();
        
        CellProcessor processor = columnMapping.getCellProcessorForReading();
        
        printCellProcessorChain(processor, name.getMethodName());
        
        String input = "abc";
        try {
            processor.execute(input, testCsvContext(columnMapping, input));
            fail();
            
        } catch(Exception e) {
            assertThat(e).isInstanceOf(SuperCsvValidationException.class);
            
            List<String> messages = exceptionConverter.convertAndFormat((SuperCsvValidationException)e, beanMapping);
            assertThat(messages).hasSize(1)
                    .contains("テストメッセージ");
        }
        
    }
    
    /**
     * プロパティファイルのtypeMismatchからメッセージを取得する場合
     */
    @Test
    public void testErrorMessage_typeMismatch() {
        
        // 独自のメッセージに入れ替え
        exceptionConverter.setMessageResolver(testMessageResolver);
        
        BeanMapping<TestCustomFormatterCsv> beanMapping = beanMappingFactory.create(TestCustomFormatterCsv.class, groupEmpty);
        ColumnMapping columnMapping = beanMapping.getColumnMapping("col_default").get();
        
        CellProcessor processor = columnMapping.getCellProcessorForReading();
        
        printCellProcessorChain(processor, name.getMethodName());
        
        String input = "abc";
        try {
            processor.execute(input, testCsvContext(columnMapping, input));
            fail();
            
        } catch(Exception e) {
            assertThat(e).isInstanceOf(SuperCsvValidationException.class);
            
            List<String> messages = exceptionConverter.convertAndFormat((SuperCsvValidationException)e, beanMapping);
            assertThat(messages).hasSize(1)
                    .contains("[2行, 1列] : 項目「col_default」の値は、XMLの値として不正です。");                
        }
        
    }
    
    
}

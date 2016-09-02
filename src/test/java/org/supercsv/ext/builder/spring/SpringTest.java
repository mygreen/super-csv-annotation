package org.supercsv.ext.builder.spring;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;
import static org.supercsv.ext.tool.TestUtils.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.ext.builder.CsvAnnotationBeanParser;
import org.supercsv.ext.builder.CsvBeanMapping;
import org.supercsv.ext.builder.SpringCellProcessorBuilderFacatory;
import org.supercsv.ext.exception.SuperCsvRowException;
import org.supercsv.ext.io.CsvAnnotationBeanReader;
import org.supercsv.ext.localization.CsvExceptionConveter;
import org.supercsv.ext.localization.CsvMessageConverter;
import org.supercsv.ext.localization.SpringMessageResolver;
import org.supercsv.prefs.CsvPreference;

/**
 * CellProcessorBuilderをSpringから取得する。
 *
 *
 * @author T.TSUCHIE
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath:/TestContext.xml")
public class SpringTest {
    
    @Resource
    private ApplicationContext applicationContext;
    
    @Resource
    private SpringCellProcessorBuilderFacatory builderFactory;
    
    @Resource
    private SpringMessageResolver messageResolver;
    
    private CsvExceptionConveter exceptionConveter;
    private CsvMessageConverter messageConverter;
    
    @Before
    public void setUp() throws Exception {
        this.exceptionConveter = new CsvExceptionConveter();
        this.messageConverter = new CsvMessageConverter(messageResolver);
    }
    
    /**
     * BeanParserのテスト
     */
    @Test
    public void testBeanParser() {
        CsvAnnotationBeanParser beanParser = new CsvAnnotationBeanParser();
        beanParser.setBuilderFactory(builderFactory);
        
        CsvBeanMapping<UserCsv> beanMapping = beanParser.parse(UserCsv.class);
        
        CellProcessor[] processor = beanMapping.getInputCellProcessor();
        assertThat(processor, is(notNullValue()));
    }
    
    /**
     * 読み込みのテスト - 正常
     * @throws IOException
     */
    @Test
    public void testRead_normal() throws IOException {
        
        CsvAnnotationBeanParser beanParser = new CsvAnnotationBeanParser();
        beanParser.setBuilderFactory(builderFactory);
        
        File file = new File("src/test/data/test_spring_read_normal.csv");
        
        CsvAnnotationBeanReader<UserCsv> csvReader = new CsvAnnotationBeanReader<>(
                UserCsv.class,
                new InputStreamReader(new FileInputStream(file), Charset.forName("UTF-8")),
                CsvPreference.STANDARD_PREFERENCE,
                beanParser);
        
        // read header
        final String[] csvHeaders = csvReader.getHeader();
        assertThat(csvHeaders, arrayContaining("ユーザID", "ユーザ名", "メールアドレス"));
        
        List<UserCsv> list = new ArrayList<>();
        
        UserCsv bean;
        while((bean = csvReader.read()) != null) {
            list.add(bean);
            
            assertBean(bean);
        }
        
        assertThat(csvReader.hasNotError(), is(true));
        
        csvReader.close();
        
        
    }
    
    /**
     * 読み込みのテスト - 不正
     * @throws IOException
     */
    @Test
    public void testRead_error_notFound_userName() throws IOException {
        
        CsvAnnotationBeanParser beanParser = new CsvAnnotationBeanParser();
        beanParser.setBuilderFactory(builderFactory);
        
        File file = new File("src/test/data/test_spring_read_error_notFound_userName.csv");
        
        CsvAnnotationBeanReader<UserCsv> csvReader = new CsvAnnotationBeanReader<>(
                UserCsv.class,
                new InputStreamReader(new FileInputStream(file), Charset.forName("UTF-8")),
                CsvPreference.STANDARD_PREFERENCE,
                beanParser);
        
        // read header
        final String[] csvHeaders = csvReader.getHeader();
        assertThat(csvHeaders, is(notNullValue()));
        
        List<UserCsv> list = new ArrayList<>();
        
        try {
            UserCsv bean;
            while((bean = csvReader.read()) != null) {
                list.add(bean);
                
                assertBean(bean);
            }
            
            fail();
            
        } catch(Exception e) {
            
            assertThat(e, instanceOf(SuperCsvRowException.class));
            
            e.printStackTrace();
            
        }
        
        // convert error messages.
        assertThat(csvReader.hasError(), is(true));
        List<String> messages = csvReader.getCsvErrors().stream()
                .map(ce -> messageConverter.convertMessage(ce))
                .collect(Collectors.toList());
        messages.forEach(System.out::println);
        
        csvReader.close();
        
        
    }
    
    private void assertBean(final UserCsv bean) {
        
        if(bean.getId() == 1) {
            assertThat(bean.getName(), is("test"));
            assertThat(bean.getMailAddress(), is("test@example.com"));
            
        } else if(bean.getId() == 2) {
            
            assertThat(bean.getName(), is("admin"));
            assertThat(bean.getMailAddress(), is("admin@example.com"));
        }
        
    }
    
}

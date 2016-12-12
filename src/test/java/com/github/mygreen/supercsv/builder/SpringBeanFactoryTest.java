package com.github.mygreen.supercsv.builder;

import static org.junit.Assert.*;
import static org.assertj.core.api.Assertions.*;

import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.supercsv.prefs.CsvPreference;

import com.github.mygreen.supercsv.builder.spring.UserCsv;
import com.github.mygreen.supercsv.io.CsvAnnotationBeanReader;
import com.github.mygreen.supercsv.io.CsvAnnotationBeanWriter;
import com.github.mygreen.supercsv.localization.SpringMessageResolver;
import com.github.mygreen.supercsv.validation.CsvExceptionConverter;

/**
 * {@link SpringBeanFactory}のテスタ
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath:/TestContext.xml")
//@ContextConfiguration(classes=SpringTestConfig.class)
public class SpringBeanFactoryTest {
    
    @Autowired
    private SpringBeanFactory beanFactory;
    
    @Autowired
    private SpringMessageResolver messageResolver;
    
    private BeanMappingFactory beanMappingFactory;
    
    private CsvExceptionConverter exceptionConverter;
    
    @Before
    public void setUp() throws Exception {
        
        this.beanMappingFactory = new BeanMappingFactory();
        beanMappingFactory.getConfiguration().setBeanFactory(beanFactory);
        
        this.exceptionConverter = new CsvExceptionConverter();
        exceptionConverter.setMessageResolver(messageResolver);
    }
    
    @Test
    public void testRead() throws Exception {
        
        BeanMapping<UserCsv> beanMapping = beanMappingFactory.create(UserCsv.class);
        
        String csv = "id,名前,ホームページ,ホームページ（予備）,E-mail"
                + "\r\n" + "001,admin,http://mysite.com/admin.html,https://localhost/sub.html,admin@mysite.com"
                + "\r\n" + "002,sample,http://mysite.com/sample.html,https://localhost/sub.html,sample@mysite.com"
                + "\r\n" + "003,test,ftp://mysite.com/test.html,https://localhost/sub.html,test@mysite.com"
                + "\r\n" + "004,test,http://mysite.com/test.html,abc,test@mysite.com"
                + "\r\n" + "005,admin,http://mysite.com/admin.html,https://localhost/sub.html,admin_mysite.com"
                ;
        
        CsvAnnotationBeanReader<UserCsv> csvReader = new CsvAnnotationBeanReader<>(beanMapping, 
                new StringReader(csv), CsvPreference.STANDARD_PREFERENCE);
        csvReader.setExceptionConverter(exceptionConverter);
        
        List<UserCsv> list = csvReader.readAll(true);
        csvReader.close();
        
        csvReader.getErrorMessages().stream().forEach(System.out::println);
        
        assertThat(csvReader.getErrorMessages()).hasSize(4)
            .contains("[3行, 2列] : 項目「名前」の値（sample）は、存在しません。")
            .contains("[4行, 3列] : 項目「ホームページ」の値（ftp://mysite.com/test.html）のプロトコル「ftp」はサポートしていません。")
            .contains("[5行, 4列] : 項目「ホームページ（予備）」の値（abc）は、サブのホームページの値として不正です。")
            .contains("[6行, 5列] : 項目「E-mail」の値（admin_mysite.com）は、メールアドレスの書式として不正です。")
            ;
        
        assertThat(list).hasSize(1);
        assertThat(list.get(0)).hasFieldOrPropertyWithValue("id", "001");
        
    }
    
    @Test
    public void testWrite() throws Exception {
        
        BeanMapping<UserCsv> beanMapping = beanMappingFactory.create(UserCsv.class);
        
        List<UserCsv> list = new ArrayList<UserCsv>();
        list.add(new UserCsv().id("001").name("admin")
                .homepage(new URL("http://mysite.com/admin.html"))
                .homepageSub(new URL("https://localhost/sub.html"))
                .email("admin@mysite.com"));
        
        list.add(new UserCsv().id("002").name("sample")
                .homepage(new URL("http://mysite.com/sample.html"))
                .homepageSub(new URL("https://localhost/sub.html"))
                .email("sample@mysite.com"));
        
        list.add(new UserCsv().id("003").name("test")
                .homepage(new URL("ftp://mysite.com/test.html"))
                .homepageSub(new URL("https://localhost/sub.html"))
                .email("test@mysite.com"));
        
//        list.add(new UserCsv().id("004").name("test")
//                .homepage(new URL("http://mysite.com/admin.html"))
//                .homepageSub(new URL("mailto://localhost/sub.html"))
//                .email("test@mysite.com"));
        
        list.add(new UserCsv().id("005").name("admin")
                .homepage(new URL("http://mysite.com/admin.html"))
                .homepageSub(new URL("https://localhost/sub.html"))
                .email("admin_mysite.com"));
        
        
        StringWriter writer = new StringWriter();
        
        CsvAnnotationBeanWriter<UserCsv> csvWriter = new CsvAnnotationBeanWriter<>(beanMapping, 
                writer, CsvPreference.STANDARD_PREFERENCE);
        csvWriter.setExceptionConverter(exceptionConverter);
        
        csvWriter.writeAll(list, true);
        csvWriter.close();
        
        csvWriter.getErrorMessages().stream().forEach(System.out::println);
        
        assertThat(csvWriter.getErrorMessages()).hasSize(3)
            .contains("[3行, 2列] : 項目「名前」の値（sample）は、存在しません。")
            .contains("[4行, 3列] : 項目「ホームページ」の値（ftp://mysite.com/test.html）のプロトコル「ftp」はサポートしていません。")
            .contains("[5行, 5列] : 項目「E-mail」の値（admin_mysite.com）は、メールアドレスの書式として不正です。")
            ;
        
        String csv = writer.toString();
        String expected = "id,名前,ホームページ,ホームページ（予備）,E-mail"
                + "\r\n" + "001,admin,http://mysite.com/admin.html,https://localhost/sub.html,admin@mysite.com"
                + "\r\n";
        System.out.println(csv);
        assertThat(csv).isEqualTo(expected);
        
        
    
    }
}

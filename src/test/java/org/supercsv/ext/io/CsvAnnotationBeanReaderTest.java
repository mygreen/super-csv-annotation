package org.supercsv.ext.io;

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

import org.junit.Before;
import org.junit.Test;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.exception.SuperCsvException;
import org.supercsv.ext.builder.CsvAnnotationBeanParser;
import org.supercsv.ext.builder.CsvBeanMapping;
import org.supercsv.ext.exception.SuperCsvNoMatchColumnSizeException;
import org.supercsv.ext.exception.SuperCsvNoMatchHeaderException;
import org.supercsv.ext.exception.SuperCsvRowException;
import org.supercsv.ext.localization.CsvExceptionConveter;
import org.supercsv.ext.localization.CsvMessageConverter;
import org.supercsv.prefs.CsvPreference;

/**
 * {@link CsvAnnotationBeanReader}のテスタ
 *
 * @since 1.2
 * @author T.TSUCHIE
 *
 */
public class CsvAnnotationBeanReaderTest {
    
    private CsvExceptionConveter exceptionConveter;
    private CsvMessageConverter messageConverter;
    
    @Before
    public void setUp() throws Exception {
        this.exceptionConveter = new CsvExceptionConveter();
        this.messageConverter = new CsvMessageConverter();
    }
    
    /**
     * 正常系のテスト
     * @throws IOException 
     */
    @Test
    public void testRead_normal() throws IOException {
        
        File file = new File("src/test/data/test_read_normal.csv");
        
        CsvAnnotationBeanReader<SampleNormalBean> csvReader = new CsvAnnotationBeanReader<>(
                SampleNormalBean.class,
                new InputStreamReader(new FileInputStream(file), Charset.forName("UTF-8")),
                CsvPreference.STANDARD_PREFERENCE);
        
        List<SampleNormalBean> list = new ArrayList<>();
        
        final String[] expectedHeaders = new String[]{
                "id",
                "数字1", "number2",
                "string1", "string2",
                "date1", "date2",
                "enum1", "列挙型2",
                "boolean1", "boolean2"};
        
        final String[] definitionHeaders = csvReader.getDefinedHeader();
        assertThat(definitionHeaders, arrayContaining(expectedHeaders));
        
        // read header
        final String[] csvHeaders = csvReader.getHeader();
        assertThat(csvHeaders, arrayContaining(expectedHeaders));
        
        SampleNormalBean bean;
        while((bean = csvReader.read()) != null) {
            list.add(bean);
            
            assertBean(bean);
        }
        
        assertThat(csvReader.hasNotError(), is(true));
        
        csvReader.close();
        
    }
    
    private void assertBean(SampleNormalBean bean) {
        
        if(bean.getId() == 1) {
            assertThat(bean.getNumber1(), is(999110));
            assertThat(bean.getNumber2(), is(10.2d));
            
            assertThat(bean.getString1(), is("abcd"));
            assertThat(bean.getString2(), is("12345"));
            
            assertThat(bean.getDate1(), is(toDate(2000, 1, 1, 0, 1, 2)));
            assertThat(bean.getDate2(), is(toTimestamp(toDate(2000, 2, 3))));
            
            assertThat(bean.getEnum1(), is(SampleEnum.RED));
            assertThat(bean.getEnum2(), is(SampleEnum.RED));
            
            assertThat(bean.getBoolean1(), is(true));
            assertThat(bean.getBoolean2(), is(Boolean.TRUE));
            
        } else if(bean.getId() == 2) {
            
            assertThat(bean.getNumber1(), is(-12));
            assertThat(bean.getNumber2(), is(nullValue()));
            
            assertThat(bean.getString1(), is("あいうえお"));
            assertThat(bean.getString2(), is(""));
            
            assertThat(bean.getDate1(), is(toDate(2000, 2, 1, 3, 4, 5)));
            assertThat(bean.getDate2(), is(nullValue()));
            
            assertThat(bean.getEnum1(), is(SampleEnum.BLUE));
            assertThat(bean.getEnum2(), is(SampleEnum.BLUE));
            
            assertThat(bean.getBoolean1(), is(false));
            assertThat(bean.getBoolean2(), is(Boolean.FALSE));
            
        }
        
    }
    
    /**
     * ヘッダーの列数が一致しない
     */
    @Test
    public void testRead_error_header_size() throws IOException {
        
        File file = new File("src/test/data/test_read_error_header_size.csv");
        
        CsvAnnotationBeanReader<SampleNormalBean> csvReader = new CsvAnnotationBeanReader<>(
                SampleNormalBean.class,
                new InputStreamReader(new FileInputStream(file), Charset.forName("UTF-8")),
                CsvPreference.STANDARD_PREFERENCE);
        
        List<SampleNormalBean> list = new ArrayList<>();
        
        try {
            final String[] headers = csvReader.getHeader(true);
            fail();
            
        } catch(SuperCsvException e) {
            assertThat(e, instanceOf(SuperCsvNoMatchHeaderException.class));
            
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
    
    /**
     * ヘッダーの列数が一致しない(チェックを無視する場合)
     */
    @Test
    public void testRead_error_header_size_ignore() throws IOException {
        
        File file = new File("src/test/data/test_read_error_header_size.csv");
        
        CsvAnnotationBeanReader<SampleNormalBean> csvReader = new CsvAnnotationBeanReader<>(
                SampleNormalBean.class,
                new InputStreamReader(new FileInputStream(file), Charset.forName("UTF-8")),
                CsvPreference.STANDARD_PREFERENCE);
        
        List<SampleNormalBean> list = new ArrayList<>();
        
        String[] headers = csvReader.getHeader(false);
        assertThat(headers, is(notNullValue()));
        
        
        csvReader.close();
        
    }
    
    /**
     * ヘッダの値が一致しない
     */
    @Test
    public void testRead_error_header_value() throws IOException {
        
        File file = new File("src/test/data/test_read_error_header_value.csv");
        
        CsvAnnotationBeanReader<SampleNormalBean> csvReader = new CsvAnnotationBeanReader<>(
                SampleNormalBean.class,
                new InputStreamReader(new FileInputStream(file), Charset.forName("UTF-8")),
                CsvPreference.STANDARD_PREFERENCE);
        
        List<SampleNormalBean> list = new ArrayList<>();
        
        try {
            // read header
            final String headers[] = csvReader.getHeader(true);
            
            fail();
            
        } catch(SuperCsvException e) {
            assertThat(e, instanceOf(SuperCsvNoMatchHeaderException.class));
            
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
    
    /**
     * 列のサイズが一致しない
     */
    @Test
    public void testRead_error_column_size() throws IOException {
        
        File file = new File("src/test/data/test_read_error_column_size.csv");
        
        CsvAnnotationBeanReader<SampleNormalBean> csvReader = new CsvAnnotationBeanReader<>(
                SampleNormalBean.class,
                new InputStreamReader(new FileInputStream(file), Charset.forName("UTF-8")),
                CsvPreference.STANDARD_PREFERENCE);
        
        List<SampleNormalBean> list = new ArrayList<>();
        
        // read header
        final String headers[] = csvReader.getHeader();
        
        try {
            SampleNormalBean bean;
            while((bean = csvReader.read()) != null) {
                list.add(bean);
                
                assertBean(bean);
            }
            
            fail();
            
        } catch(SuperCsvException e) {
            
            assertThat(e, instanceOf(SuperCsvNoMatchColumnSizeException.class));
            
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
    
    /**
     * 列のパターンが不正名場合
     */
    @Test
    public void testRead_error_wrong_pattern() throws IOException {
        
        File file = new File("src/test/data/test_read_error_wrong_pattern.csv");
        
        CsvAnnotationBeanReader<SampleNormalBean> csvReader = new CsvAnnotationBeanReader<>(
                SampleNormalBean.class,
                new InputStreamReader(new FileInputStream(file), Charset.forName("UTF-8")),
                CsvPreference.STANDARD_PREFERENCE);
        
        List<SampleNormalBean> list = new ArrayList<>();
        
        // read header
        final String headers[] = csvReader.getHeader();
        
        try {
            SampleNormalBean bean;
            while((bean = csvReader.read()) != null) {
                list.add(bean);
                
                assertBean(bean);
            }
            
            fail();
            
        } catch(SuperCsvException e) {
            
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
    
    /**
     * 正常系のテスト - アノテーション経由でない場合
     * @throws IOException 
     */
    @Test
    public void testRead_no_annotation() throws IOException {
        
        File file = new File("src/test/data/test_read_normal.csv");
        
        final CsvAnnotationBeanParser beanParser = new CsvAnnotationBeanParser();
        CsvBeanMapping<SampleNormalBean> beanMapping = beanParser.parse(SampleNormalBean.class);
        
        CsvAnnotationBeanReader<SampleNormalBean> csvReader = new CsvAnnotationBeanReader<>(
                beanMapping,
                new InputStreamReader(new FileInputStream(file), Charset.forName("UTF-8")),
                CsvPreference.STANDARD_PREFERENCE);
        
        List<SampleNormalBean> list = new ArrayList<>();
        
        // read header
        final String[] csvHeaders = csvReader.getHeader();
        
        final Class<SampleNormalBean> beanType = beanMapping.getType();
        final String[] nameMapping = beanMapping.getNameMapping();
        final CellProcessor[] processors = beanMapping.getInputCellProcessor();
        
        SampleNormalBean bean;
        while((bean = csvReader.read(beanType,nameMapping, processors)) != null) {
            list.add(bean);
            
            assertBean(bean);
        }
        
        csvReader.close();
        
    }
    
    /**
     * 正常系のテスト - コンストラクタのテスト
     * <p> BeanParserの指定
     * @throws IOException 
     */
    @Test
    public void testConstructor1() throws IOException {
        
        File file = new File("src/test/data/test_read_normal.csv");
        
        final CsvAnnotationBeanParser beanParser = new CsvAnnotationBeanParser();
        
        CsvAnnotationBeanReader<SampleNormalBean> csvReader = new CsvAnnotationBeanReader<>(
                SampleNormalBean.class,
                new InputStreamReader(new FileInputStream(file), Charset.forName("UTF-8")),
                CsvPreference.STANDARD_PREFERENCE,
                beanParser);
        
        List<SampleNormalBean> list = new ArrayList<>();
        
        // read header
        final String[] csvHeaders = csvReader.getHeader();
        
        SampleNormalBean bean;
        while((bean = csvReader.read()) != null) {
            list.add(bean);
            
            assertBean(bean);
        }
        
        csvReader.close();
        
    }
    
    /**
     * 正常系のテスト - コンストラクタのテスト
     * <p> BeanMappingの指定
     * @throws IOException 
     */
    @Test
    public void testConstructor2() throws IOException {
        
        File file = new File("src/test/data/test_read_normal.csv");
        
        final CsvAnnotationBeanParser beanParser = new CsvAnnotationBeanParser();
        CsvBeanMapping<SampleNormalBean> beanMapping = beanParser.parse(SampleNormalBean.class);
        
        CsvAnnotationBeanReader<SampleNormalBean> csvReader = new CsvAnnotationBeanReader<>(
                beanMapping,
                new InputStreamReader(new FileInputStream(file), Charset.forName("UTF-8")),
                CsvPreference.STANDARD_PREFERENCE);
        
        List<SampleNormalBean> list = new ArrayList<>();
        
        // read header
        final String[] csvHeaders = csvReader.getHeader();
        
        SampleNormalBean bean;
        while((bean = csvReader.read()) != null) {
            list.add(bean);
            
            assertBean(bean);
        }
        
        csvReader.close();
        
    }
    
}

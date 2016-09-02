package org.supercsv.ext.io;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;
import static org.supercsv.ext.tool.TestUtils.*;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.exception.SuperCsvException;
import org.supercsv.ext.builder.CsvAnnotationBeanParser;
import org.supercsv.ext.builder.CsvBeanMapping;
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
public class CsvAnnotationBeanWriterTest {
    
    private CsvExceptionConveter exceptionConveter;
    private CsvMessageConverter messageConverter;
    
    @Before
    public void setUp() throws Exception {
        this.exceptionConveter = new CsvExceptionConveter();
        this.messageConverter = new CsvMessageConverter();
    }
    
    /**
     * 書き込みのテスト - 正常
     */
    @Test
    public void testWrite_normal() throws IOException {
        
        // テストデータの作成
        final List<SampleNormalBean> list = createNormalData();

        StringWriter strWriter = new StringWriter();
        
        CsvAnnotationBeanWriter<SampleNormalBean> csvWriter = new CsvAnnotationBeanWriter<>(
                SampleNormalBean.class,
                strWriter,
                CsvPreference.STANDARD_PREFERENCE);
        
        final String[] expectedHeaders = new String[]{
                "id",
                "数字1", "number2",
                "string1", "string2",
                "date1", "date2",
                "enum1", "列挙型2",
                "boolean1", "boolean2"};
        
        final String[] definitionHeaders = csvWriter.getDefinedHeader();
        assertThat(definitionHeaders, arrayContaining(expectedHeaders));
        
        csvWriter.writeHeader();
        csvWriter.flush();
        
        for(SampleNormalBean item : list) {
            csvWriter.write(item);
            csvWriter.flush();
        }
        
        String actual = strWriter.toString();
        System.out.println(actual);
        
        String expected = getTextFromFile("src/test/data/test_write_normal.csv", Charset.forName("UTF-8"));
        assertThat(actual, is(expected));
        
        assertThat(csvWriter.hasNotError(), is(true));
        
        csvWriter.close();
        
    }
    
    /**
     * 書き込みのテスト - 正常
     */
    @Test
    public void testWriteAll_normal() throws IOException {
        
        // テストデータの作成
        final List<SampleNormalBean> list = createNormalData();

        StringWriter strWriter = new StringWriter();
        
        CsvAnnotationBeanWriter<SampleNormalBean> csvWriter = new CsvAnnotationBeanWriter<>(
                SampleNormalBean.class,
                strWriter,
                CsvPreference.STANDARD_PREFERENCE);
        
        csvWriter.writeHeader();
        csvWriter.flush();
        
        csvWriter.writeAll(list);
        csvWriter.flush();
        
        String actual = strWriter.toString();
        System.out.println(actual);
        
        String expected = getTextFromFile("src/test/data/test_write_normal.csv", Charset.forName("UTF-8"));
        assertThat(actual, is(expected));
        
        assertThat(csvWriter.hasNotError(), is(true));
        
        csvWriter.close();
        
    }
    
    /**
     * 書き込みのテスト - アノテーション経由でない場合
     */
    @Test
    public void testWrite_no_annotaion() throws IOException {
        
        // テストデータの作成
        final List<SampleNormalBean> list = createNormalData();

        StringWriter strWriter = new StringWriter();
        
        final CsvAnnotationBeanParser beanParser = new CsvAnnotationBeanParser();
        CsvBeanMapping<SampleNormalBean> beanMapping = beanParser.parse(SampleNormalBean.class);
        
        CsvAnnotationBeanWriter<SampleNormalBean> csvWriter = new CsvAnnotationBeanWriter<>(
                beanMapping,
                strWriter,
                CsvPreference.STANDARD_PREFERENCE);
        
        csvWriter.writeHeader();
        csvWriter.flush();
        
        final String[] nameMapping = beanMapping.getNameMapping();
        final CellProcessor[] processors = beanMapping.getOutputCellProcessor();
        
        for(SampleNormalBean item : list) {
            csvWriter.write(item, nameMapping, processors);
            csvWriter.flush();
        }
        
        String actual = strWriter.toString();
        System.out.println(actual);
        
        String expected = getTextFromFile("src/test/data/test_write_normal.csv", Charset.forName("UTF-8"));
        assertThat(actual, is(expected));
        
        assertThat(csvWriter.hasNotError(), is(true));
        
        csvWriter.close();
        
    }
    
    private List<SampleNormalBean> createNormalData() {
        
     // テストデータの作成
        final List<SampleNormalBean> list = new ArrayList<>();
        
        // データ1
        final SampleNormalBean bean1 = new SampleNormalBean();
        list.add(bean1);
        bean1.setId(1);
        
        bean1.setNumber1(999110);
        bean1.setNumber2(10.2d);
        
        bean1.setString1("abcd");
        bean1.setString2("12345");
        
        bean1.setDate1(toDate(2000, 1, 1, 0, 1, 2));
        bean1.setDate2(toTimestamp(toDate(2000, 2, 3)));
        
        bean1.setEnum1(SampleEnum.RED);
        bean1.setEnum2(SampleEnum.RED);
        
        bean1.setBoolean1(true);
        bean1.setBoolean2(null);
        
        // データ2
        final SampleNormalBean bean2 = new SampleNormalBean();
        list.add(bean2);
        bean2.setId(2);
        
        bean2.setNumber1(-12);
        bean2.setNumber2(null);
        
        bean2.setString1("あいうえお");
        bean2.setString2(null);
        
        bean2.setDate1(toDate(2000, 2, 1, 3, 4, 5));
        bean2.setDate2(null);
        
        bean2.setEnum1(SampleEnum.BLUE);
        bean2.setEnum2(null);
        
        bean2.setBoolean1(false);
        bean2.setBoolean2(Boolean.FALSE);
        
        return list;
    }
    
    /**
     * 書き込みのテスト - 値が不正
     */
    @Test
    public void testWrite_error_column_value()  throws IOException {
        
     // テストデータの作成
        final List<SampleNormalBean> list = createNormalData();
        
        // データ1
        final SampleNormalBean bean1 = list.get(0);
        bean1.setNumber1(1_000_000);   // 最大値を超える
        bean1.setString1(null); // 必須
        
        StringWriter strWriter = new StringWriter();
        
        CsvAnnotationBeanWriter<SampleNormalBean> csvWriter = new CsvAnnotationBeanWriter<>(
                SampleNormalBean.class,
                strWriter,
                CsvPreference.STANDARD_PREFERENCE);
        
        csvWriter.writeHeader();
        csvWriter.flush();
        
        try {
            for(SampleNormalBean item : list) {
                csvWriter.write(item);
                csvWriter.flush();
            }
            fail();
            
        } catch(SuperCsvException e) {
            assertThat(e, instanceOf(SuperCsvRowException.class));
            e.printStackTrace();
        }
        
        // convert error messages.
        assertThat(csvWriter.hasError(), is(true));
        List<String> messages = csvWriter.getCsvErrors().stream()
                .map(ce -> messageConverter.convertMessage(ce))
                .collect(Collectors.toList());
        messages.forEach(System.out::println);
        
        csvWriter.close();
        
    }
    
    /**
     * 書き込みのテスト - 制約のチェックをスキップ
     */
    @Test
    public void testWrite_ignoreConstraint()  throws IOException {
        
        // テストデータの作成
        final List<SampleNormalBean> list = createNormalData();
        
        // データ1
        final SampleNormalBean bean1 = list.get(0);
        bean1.setNumber1(1_000_000);   // 最大値を超える
        
        StringWriter strWriter = new StringWriter();
        
        CsvAnnotationBeanWriter<SampleNormalBean> csvWriter = new CsvAnnotationBeanWriter<>(
                SampleNormalBean.class,
                strWriter,
                CsvPreference.STANDARD_PREFERENCE,
                true);
        
        csvWriter.writeHeader();
        csvWriter.flush();
        
        for(SampleNormalBean item : list) {
            csvWriter.write(item);
            csvWriter.flush();
        }
        
        String actual = strWriter.toString();
        System.out.println(actual);
        
        String expected = getTextFromFile("src/test/data/test_write_ignore_constaint.csv", Charset.forName("UTF-8"));
        assertThat(actual, is(expected));
        
        assertThat(csvWriter.hasNotError(), is(true));
        
        csvWriter.close();
        
    }
    
    /**
     * コンストラクタのテスト
     * <p>BeanParserの指定
     */
    @Test
    public void testConstructor1() throws IOException {
        
        // テストデータの作成
        final List<SampleNormalBean> list = createNormalData();

        StringWriter strWriter = new StringWriter();
        
        final CsvAnnotationBeanParser beanParser = new CsvAnnotationBeanParser();
        
        CsvAnnotationBeanWriter<SampleNormalBean> csvWriter = new CsvAnnotationBeanWriter<>(
                SampleNormalBean.class,
                strWriter,
                CsvPreference.STANDARD_PREFERENCE,
                false,
                beanParser);
        
        csvWriter.writeHeader();
        csvWriter.flush();
        
        for(SampleNormalBean item : list) {
            csvWriter.write(item);
            csvWriter.flush();
        }
        
        String actual = strWriter.toString();
        System.out.println(actual);
        
        String expected = getTextFromFile("src/test/data/test_write_normal.csv", Charset.forName("UTF-8"));
        assertThat(actual, is(expected));
        
        assertThat(csvWriter.hasNotError(), is(true));
        
        csvWriter.close();
        
    }
    
    /**
     * コンストラクタのテスト
     * <p>BeanMappingの指定
     */
    @Test
    public void testConstructor2() throws IOException {
        
        // テストデータの作成
        final List<SampleNormalBean> list = createNormalData();

        StringWriter strWriter = new StringWriter();
        
        final CsvAnnotationBeanParser beanParser = new CsvAnnotationBeanParser();
        CsvBeanMapping<SampleNormalBean> beanMapping = beanParser.parse(SampleNormalBean.class);
        
        CsvAnnotationBeanWriter<SampleNormalBean> csvWriter = new CsvAnnotationBeanWriter<>(
                beanMapping,
                strWriter,
                CsvPreference.STANDARD_PREFERENCE);
        
        csvWriter.writeHeader();
        csvWriter.flush();
        
        for(SampleNormalBean item : list) {
            csvWriter.write(item);
            csvWriter.flush();
        }
        
        String actual = strWriter.toString();
        System.out.println(actual);
        
        String expected = getTextFromFile("src/test/data/test_write_normal.csv", Charset.forName("UTF-8"));
        assertThat(actual, is(expected));
        
        assertThat(csvWriter.hasNotError(), is(true));
        
        csvWriter.close();
        
    }
    
    private static String getTextFromFile(final String path, final Charset charset) throws IOException {
        
        byte[] data = Files.readAllBytes(Paths.get(path));
        return new String(data, charset);
        
    }
}

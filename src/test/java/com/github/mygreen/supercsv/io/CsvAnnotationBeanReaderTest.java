package com.github.mygreen.supercsv.io;

import static com.github.mygreen.supercsv.tool.TestUtils.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.supercsv.exception.SuperCsvException;
import org.supercsv.prefs.CsvPreference;

import com.github.mygreen.supercsv.annotation.DefaultGroup;
import com.github.mygreen.supercsv.builder.BeanMapping;
import com.github.mygreen.supercsv.builder.BeanMappingFactory;
import com.github.mygreen.supercsv.exception.SuperCsvBindingException;
import com.github.mygreen.supercsv.exception.SuperCsvNoMatchColumnSizeException;
import com.github.mygreen.supercsv.exception.SuperCsvNoMatchHeaderException;
import com.github.mygreen.supercsv.validation.CsvExceptionConverter;


/**
 * {@link CsvAnnotationBeanReader}のテスタ。
 *
 * @version 2.3
 * @since 1.2
 * @author T.TSUCHIE
 *
 */
public class CsvAnnotationBeanReaderTest {
    
    private CsvExceptionConverter exceptionConverter;
    
    @Before
    public void setUp() throws Exception {
        this.exceptionConverter = new CsvExceptionConverter();
    }
    
    /**
     * 正常系のテスト - コンストラクタのテスト
     * <p> BeanMappingの指定</p>
     * @throws IOException 
     */
    @Test
    public void testConstructor_beanMapping() throws IOException {
        
        File file = new File("src/test/data/test_read_normal.csv");
        
        BeanMappingFactory mappingFactory = new BeanMappingFactory();
        BeanMapping<SampleNormalBean> beanMapping = mappingFactory.create(SampleNormalBean.class,
                DefaultGroup.class, SampleNormalBean.ReadGroup.class);
        
        CsvAnnotationBeanReader<SampleNormalBean> csvReader = new CsvAnnotationBeanReader<>(
                beanMapping,
                new InputStreamReader(new FileInputStream(file), Charset.forName("UTF-8")),
                CsvPreference.STANDARD_PREFERENCE);
        
        List<SampleNormalBean> list = new ArrayList<>();
        
        // read header
        final String[] csvHeaders = csvReader.getHeader(true);
        assertThat(csvHeaders).isNotNull();
        
        SampleNormalBean bean;
        while((bean = csvReader.read()) != null) {
            list.add(bean);
            
            assertBean(bean);
        }
        
        assertThat(csvReader.getErrorMessages()).hasSize(0);
        
        csvReader.close();
        
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
                CsvPreference.STANDARD_PREFERENCE,
                DefaultGroup.class, SampleNormalBean.ReadGroup.class);
        csvReader.setExceptionConverter(exceptionConverter);
        
        List<SampleNormalBean> list = new ArrayList<>();
        
        final String[] expectedHeaders = new String[]{
                "id",
                "数字1", "number2",
                "string1", "string2",
                "date1", "date2",
                "enum1", "列挙型2",
                "boolean1", "boolean2"
            };
        
        final String[] definitionHeaders = csvReader.getDefinedHeader();
        assertThat(definitionHeaders).containsExactly(expectedHeaders);
        
        // read header
        final String[] csvHeaders = csvReader.getHeader(true);
        assertThat(csvHeaders).containsExactly(expectedHeaders);
        
        SampleNormalBean bean;
        while((bean = csvReader.read()) != null) {
            list.add(bean);
            
            assertBean(bean);
        }
        
        assertThat(csvReader.getErrorMessages()).hasSize(0);
        
        csvReader.close();
        
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
                CsvPreference.STANDARD_PREFERENCE,
                DefaultGroup.class, SampleNormalBean.ReadGroup.class);
        csvReader.setExceptionConverter(exceptionConverter);
        
        List<SampleNormalBean> list = new ArrayList<>();
        
        try {
            final String[] headers = csvReader.getHeader(true);
            fail();
            
        } catch(SuperCsvException e) {
            assertThat(e).isInstanceOf(SuperCsvNoMatchColumnSizeException.class);
            
//            e.printStackTrace();
        }
        
        // convert error messages.
        List<String> messages = csvReader.getErrorMessages();
        assertThat(messages).hasSize(1)
            .contains("[1行] : 列数が不正です。 11列で設定すべきですが、実際には10列になっています。");
        messages.forEach(System.out::println);
        
        csvReader.close();
    }
    
    //TODO: XMLの機能を実装した後に、書き換える。
//    /**
//     * ヘッダーの列数が一致しない(チェックを無視する場合)
//     */
//    @Test
//    public void testRead_error_header_size_ignore() throws IOException {
//        
//        File file = new File("src/test/data/test_read_error_header_size.csv");
//        
//        CsvAnnotationBeanReader<SampleNormalBean> csvReader = new CsvAnnotationBeanReader<>(
//                SampleNormalBean.class,
//                new InputStreamReader(new FileInputStream(file), Charset.forName("UTF-8")),
//                CsvPreference.STANDARD_PREFERENCE);
//        csvReader.setExceptionConverter(exceptionConverter);
//        
//        List<SampleNormalBean> list = new ArrayList<>();
//        
//        String[] headers = csvReader.getHeader(false);
//        assertThat(headers, is(notNullValue()));
//        
//        
//        csvReader.close();
//        
//    }
    
    /**
     * ヘッダの値が一致しない
     */
    @Test
    public void testRead_error_header_value() throws IOException {
        
        File file = new File("src/test/data/test_read_error_header_value.csv");
        
        CsvAnnotationBeanReader<SampleNormalBean> csvReader = new CsvAnnotationBeanReader<>(
                SampleNormalBean.class,
                new InputStreamReader(new FileInputStream(file), Charset.forName("UTF-8")),
                CsvPreference.STANDARD_PREFERENCE,
                DefaultGroup.class, SampleNormalBean.ReadGroup.class);
        csvReader.setExceptionConverter(exceptionConverter);
        
        List<SampleNormalBean> list = new ArrayList<>();
        
        try {
            // read header
            final String headers[] = csvReader.getHeader(true);
            
            fail();
            
        } catch(SuperCsvException e) {
            assertThat(e).isInstanceOf(SuperCsvNoMatchHeaderException.class);
            
        }
        
        // convert error messages.
        List<String> messages = csvReader.getErrorMessages();
        assertThat(messages).hasSize(1)
            .contains("[1行] : ヘッダーの値「id, 間違い, number2, string1, string2, date1, date2, enum1, 列挙型2, boolean1, boolean2」は、「id, 数字1, number2, string1, string2, date1, date2, enum1, 列挙型2, boolean1, boolean2」と一致しません。");
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
                CsvPreference.STANDARD_PREFERENCE,
                DefaultGroup.class, SampleNormalBean.ReadGroup.class);
        csvReader.setExceptionConverter(exceptionConverter);
        
        List<SampleNormalBean> list = new ArrayList<>();
        
        // read header
        final String headers[] = csvReader.getHeader(true);
        
        try {
            SampleNormalBean bean;
            while((bean = csvReader.read()) != null) {
                list.add(bean);
                
                assertBean(bean);
            }
            
            fail();
            
        } catch(SuperCsvException e) {
            
            assertThat(e).isInstanceOf(SuperCsvNoMatchColumnSizeException.class);
            
//            e.printStackTrace();
            
        }
        
        // convert error messages.
        List<String> messages = csvReader.getErrorMessages();
        assertThat(messages).hasSize(1)
            .contains("[3行] : 列数が不正です。 11列で設定すべきですが、実際には13列になっています。");
        messages.forEach(System.out::println);
        
        csvReader.close();
        
    }
    
    /**
     * 列のパターンが不正な場合
     */
    @Test
    public void testRead_error_wrong_pattern() throws IOException {
        
        File file = new File("src/test/data/test_read_error_wrong_pattern.csv");
        
        CsvAnnotationBeanReader<SampleNormalBean> csvReader = new CsvAnnotationBeanReader<>(
                SampleNormalBean.class,
                new InputStreamReader(new FileInputStream(file), Charset.forName("UTF-8")),
                CsvPreference.STANDARD_PREFERENCE,
                DefaultGroup.class, SampleNormalBean.ReadGroup.class);
        csvReader.setExceptionConverter(exceptionConverter);
        
        List<SampleNormalBean> list = new ArrayList<>();
        
        // read header
        final String headers[] = csvReader.getHeader(true);
        
        try {
            SampleNormalBean bean;
            while((bean = csvReader.read()) != null) {
                list.add(bean);
                
                assertBean(bean);
            }
            
            fail();
            
        } catch(SuperCsvException e) {
            assertThat(e).isInstanceOf(SuperCsvBindingException.class);
            
//            e.printStackTrace();
            
            
        }
        
        // convert error messages.
        List<String> messages = csvReader.getErrorMessages();
        assertThat(messages).hasSize(1)
            .contains("[2行, 6列] : 項目「date1」の値（2000/01/01 00:01:02）の書式は不正です。");
        messages.forEach(System.out::println);
        
        csvReader.close();
        
    }
    
    /**
     * ELインジェクションのテスト - ヘッダーのバリデーションエラー。
     */
    @Test
    public void testRead_error_elInjection_header() throws Exception {
        
        File file = new File("src/test/data/test_read_error_elinjection_header.csv");
        
        CsvAnnotationBeanReader<SampleELInjectionBean> csvReader = new CsvAnnotationBeanReader<>(
                SampleELInjectionBean.class,
                new InputStreamReader(new FileInputStream(file), Charset.forName("UTF-8")),
                CsvPreference.STANDARD_PREFERENCE,
                DefaultGroup.class, SampleNormalBean.ReadGroup.class);
        csvReader.setExceptionConverter(exceptionConverter);
        
        try {
            // read header
            String headers[] = csvReader.getHeader(true);
           
            fail();
            
        } catch(SuperCsvException e) {
            assertThat(e).isInstanceOf(SuperCsvNoMatchHeaderException.class);
            
//            e.printStackTrace();
            
        } finally {
            csvReader.close();
        }
        
        // convert error messages.
        List<String> messages = csvReader.getErrorMessages();
        // ELインジェクション対象の式は空文字として変換される。
        assertThat(messages).hasSize(1)
            .contains("[1行] : ヘッダーの値「id, abc${''.getClass().forName('java.lang.Runtime').getRuntime().exec('notepad')}efg」は、「id, value」と一致しません。");
        messages.forEach(System.out::println);
        
    }
    
    /**
     * ELインジェクションのテスト - カラムのバリデーションエラー。
     */
    @Test
    public void testRead_error_elInjection_column() throws Exception {
        File file = new File("src/test/data/test_read_error_elinjection_column.csv");
        
        CsvAnnotationBeanReader<SampleELInjectionBean> csvReader = new CsvAnnotationBeanReader<>(
                SampleELInjectionBean.class,
                new InputStreamReader(new FileInputStream(file), Charset.forName("UTF-8")),
                CsvPreference.STANDARD_PREFERENCE,
                DefaultGroup.class, SampleNormalBean.ReadGroup.class);
        csvReader.setExceptionConverter(exceptionConverter);
        
        List<SampleELInjectionBean> list = new ArrayList<>();
        try {
            // read header
            csvReader.getHeader(true);
            
            SampleELInjectionBean bean;
            while((bean = csvReader.read()) != null) {
                list.add(bean);
            }
            
            fail();
            
        } catch(SuperCsvException e) {
            assertThat(e).isInstanceOf(SuperCsvBindingException.class);
            
//            e.printStackTrace();
            
        } finally {
            csvReader.close();
            
        }
        
        // convert error messages.
        List<String> messages = csvReader.getErrorMessages();
        // ELインジェクション対象の式は空文字として変換される。
        assertThat(messages).hasSize(1)
            .contains("[2行, 2列] : 項目「value」の値（abc${''.getClass().forName('java.lang.Runtime').getRuntime().exec('notepad')}efg）には、禁止語彙 「getRuntime」が含まれています。");
        messages.forEach(System.out::println);
        
    }
    
    /**
     * 全件読み込み（正常系のテスト）
     */
    @Test
    public void testReadAll_normal() throws IOException {
        
        File file = new File("src/test/data/test_read_normal.csv");
        
        CsvAnnotationBeanReader<SampleNormalBean> csvReader = new CsvAnnotationBeanReader<>(
                SampleNormalBean.class,
                new InputStreamReader(new FileInputStream(file), Charset.forName("UTF-8")),
                CsvPreference.STANDARD_PREFERENCE,
                DefaultGroup.class, SampleNormalBean.ReadGroup.class);
        csvReader.setExceptionConverter(exceptionConverter);
        
        List<SampleNormalBean> list = csvReader.readAll();
        assertThat(list).hasSize(2);
        
        for(SampleNormalBean bean : list) {
            assertBean(bean);
        }
        
        assertThat(csvReader.getErrorMessages()).hasSize(0);
        
        csvReader.close();
        
    }
    
    /**
     * 全件読み込み - カラムにエラーがある
     */
    @Test
    public void testReadAll_error_column() throws IOException {
        
        File file = new File("src/test/data/test_read_error_wrong_pattern.csv");
        
        CsvAnnotationBeanReader<SampleNormalBean> csvReader = new CsvAnnotationBeanReader<>(
                SampleNormalBean.class,
                new InputStreamReader(new FileInputStream(file), Charset.forName("UTF-8")),
                CsvPreference.STANDARD_PREFERENCE,
                DefaultGroup.class, SampleNormalBean.ReadGroup.class);
        csvReader.setExceptionConverter(exceptionConverter);
        
        try {
            List<SampleNormalBean> list = csvReader.readAll();
        
        } catch(Exception e) {
            assertThat(e).isInstanceOf(SuperCsvBindingException.class);
        }
        
        // convert error messages.
        List<String> messages = csvReader.getErrorMessages();
        assertThat(messages).hasSize(1)
            .contains("[2行, 6列] : 項目「date1」の値（2000/01/01 00:01:02）の書式は不正です。");
        messages.forEach(System.out::println);
        
        csvReader.close();
        
    }
    
    /**
     * 全件読み込み - カラムにエラーがある場合も処理を続ける
     */
    @Test
    public void testReadAll_error_continueOnError() throws IOException {
        
        File file = new File("src/test/data/test_read_error_wrong_pattern.csv");
        
        CsvAnnotationBeanReader<SampleNormalBean> csvReader = new CsvAnnotationBeanReader<>(
                SampleNormalBean.class,
                new InputStreamReader(new FileInputStream(file), Charset.forName("UTF-8")),
                CsvPreference.STANDARD_PREFERENCE,
                DefaultGroup.class, SampleNormalBean.ReadGroup.class);
        csvReader.setExceptionConverter(exceptionConverter);
        
        List<SampleNormalBean> list = csvReader.readAll(true);
        assertThat(list).hasSize(1);
        
        for(SampleNormalBean bean : list) {
            assertBean(bean);
        }
        
        // convert error messages.
        List<String> messages = csvReader.getErrorMessages();
        assertThat(messages).hasSize(1)
            .contains("[2行, 6列] : 項目「date1」の値（2000/01/01 00:01:02）の書式は不正です。");
        messages.forEach(System.out::println);
        
        csvReader.close();
        
    }
    
    /**
     * 全件読み込み - ヘッダーにエラーがある場合
     */
    @Test
    public void testReadAll_error_header_size() throws IOException {
        
        File file = new File("src/test/data/test_read_error_header_size.csv");
        
        CsvAnnotationBeanReader<SampleNormalBean> csvReader = new CsvAnnotationBeanReader<>(
                SampleNormalBean.class,
                new InputStreamReader(new FileInputStream(file), Charset.forName("UTF-8")),
                CsvPreference.STANDARD_PREFERENCE,
                DefaultGroup.class, SampleNormalBean.ReadGroup.class);
        csvReader.setExceptionConverter(exceptionConverter);
        
        try {
            List<SampleNormalBean> list = csvReader.readAll();
        
        } catch(Exception e) {
            assertThat(e).isInstanceOf(SuperCsvNoMatchColumnSizeException.class);
        }
        
        // convert error messages.
        List<String> messages = csvReader.getErrorMessages();
        assertThat(messages).hasSize(1)
            .contains("[1行] : 列数が不正です。 11列で設定すべきですが、実際には10列になっています。");
        messages.forEach(System.out::println);
        
        csvReader.close();
        
    }
    
    /**
     * 全件読み込み - ヘッダーにエラーがある場合 - エラーがあっても処理を続ける
     */
    @Test
    public void testReadAll_error_header_size_continueOnError() throws IOException {
        
        File file = new File("src/test/data/test_read_error_header_size.csv");
        
        CsvAnnotationBeanReader<SampleNormalBean> csvReader = new CsvAnnotationBeanReader<>(
                SampleNormalBean.class,
                new InputStreamReader(new FileInputStream(file), Charset.forName("UTF-8")),
                CsvPreference.STANDARD_PREFERENCE,
                DefaultGroup.class, SampleNormalBean.ReadGroup.class);
        csvReader.setExceptionConverter(exceptionConverter);
        
        List<SampleNormalBean> list = csvReader.readAll(true);
        assertThat(list).hasSize(2);
        
        for(SampleNormalBean bean : list) {
            assertBean(bean);
        }
        
        // convert error messages.
        List<String> messages = csvReader.getErrorMessages();
        assertThat(messages).hasSize(1)
            .contains("[1行] : 列数が不正です。 11列で設定すべきですが、実際には10列になっています。");
        messages.forEach(System.out::println);
        
        csvReader.close();
        
    }
    
    /**
     * 部分的にカラムを読み込む
     */
    @Test
    public void testRead_partial() throws IOException {
        
        File file = new File("src/test/data/test_read_normal.csv");
        
        CsvAnnotationBeanReader<SamplePartialBean> csvReader = new CsvAnnotationBeanReader<>(
                SamplePartialBean.class,
                new InputStreamReader(new FileInputStream(file), Charset.forName("UTF-8")),
                CsvPreference.STANDARD_PREFERENCE,
                DefaultGroup.class, SampleNormalBean.ReadGroup.class);
        csvReader.setExceptionConverter(exceptionConverter);
        
        List<SamplePartialBean> list = new ArrayList<>();
        
        final String[] expectedHeaders = new String[]{
                "id",
                "数字1", "number2",
                "string1", "string2",
                "date1", "date2",
                "enum1", "列挙型2",
                "boolean1", "boolean2"
            };
        
        final String[] definitionHeaders = csvReader.getDefinedHeader();
        assertThat(definitionHeaders).containsExactly(expectedHeaders);
        
        // read header
        final String[] csvHeaders = csvReader.getHeader(true);
        assertThat(csvHeaders).containsExactly(expectedHeaders);
        
        SamplePartialBean bean;
        while((bean = csvReader.read()) != null) {
            list.add(bean);
            
            assertBean(bean);
        }
        
        assertThat(csvReader.getErrorMessages()).hasSize(0);
        
        csvReader.close();
        
    }
    
    /**
     * 固定長のカラムを読み込む
     */
    @Test
    public void testRead_fixedColumn() throws IOException {
        
        File file = new File("src/test/data/test_read_fixedColumn.csv");
        
        CsvAnnotationBeanReader<SampleFixedColumnBean> csvReader = new CsvAnnotationBeanReader<>(
                SampleFixedColumnBean.class,
                new InputStreamReader(new FileInputStream(file), Charset.forName("UTF-8")),
                CsvPreference.STANDARD_PREFERENCE);
        csvReader.setExceptionConverter(exceptionConverter);
        
        List<SampleFixedColumnBean> list = new ArrayList<>();
        
        final String[] expectedHeaders = new String[]{
                "   no",
                "ユーザ名　　　　　　",
                "誕生日____",
                "コメント            "
            };
        
        final String[] definitionHeaders = csvReader.getDefinedHeader();
        assertThat(definitionHeaders).containsExactly(expectedHeaders);
        
        // read header
        final String[] csvHeaders = csvReader.getHeader(true);
        assertThat(csvHeaders).containsExactly(expectedHeaders);
        
        SampleFixedColumnBean bean;
        while((bean = csvReader.read()) != null) {
            list.add(bean);
            
            assertBean(bean);
        }
        
        assertThat(csvReader.getErrorMessages()).hasSize(0);
        
        csvReader.close();
        
    }
    
    /**
     * ハンドラ指定で読み込むテスト - 正常データのみ
     * @since 2.3
     */
    @Test
    public void testRead_withHandler_normal() throws IOException {
        
        File file = new File("src/test/data/test_read_normal.csv");
        
        CsvAnnotationBeanReader<SampleNormalBean> csvReader = new CsvAnnotationBeanReader<>(
                SampleNormalBean.class,
                new InputStreamReader(new FileInputStream(file), Charset.forName("UTF-8")),
                CsvPreference.STANDARD_PREFERENCE,
                DefaultGroup.class, SampleNormalBean.ReadGroup.class);
        csvReader.setExceptionConverter(exceptionConverter);
        
        List<SampleNormalBean> list = new ArrayList<>();
        
        String[] headers = csvReader.getHeader(true);
        assertThat(headers).hasSize(11);
        
        while(csvReader.read(
                record -> {
                    list.add(record);
                    assertBean(record);
                },
                error -> fail(error.getMessage())) != CsvReadStatus.EOF) {
            
        }
        
        assertThat(list).hasSize(2);
        assertThat(csvReader.getErrorMessages()).hasSize(0);
        
        csvReader.close();
        
    }
    
    /**
     * ハンドラ指定で読み込むテスト - エラーがある場合
     * @since 2.3
     */
    @Test
    public void testRead_withHandler_error_column() throws IOException {
        
        File file = new File("src/test/data/test_read_error_wrong_pattern.csv");
        
        CsvAnnotationBeanReader<SampleNormalBean> csvReader = new CsvAnnotationBeanReader<>(
                SampleNormalBean.class,
                new InputStreamReader(new FileInputStream(file), Charset.forName("UTF-8")),
                CsvPreference.STANDARD_PREFERENCE,
                DefaultGroup.class, SampleNormalBean.ReadGroup.class);
        csvReader.setExceptionConverter(exceptionConverter);
        
        List<SampleNormalBean> list = new ArrayList<>();
        
        String[] headers = csvReader.getHeader(true);
        assertThat(headers).hasSize(11);
        
        while(csvReader.read(
                record -> {
                    list.add(record);
                    assertBean(record);
                },
                error -> {
                    assertThat(error).isInstanceOf(SuperCsvBindingException.class);
                }
                ) != CsvReadStatus.EOF) {
            
        }
        
        assertThat(list).hasSize(1);
        
        // convert error messages.
        List<String> messages = csvReader.getErrorMessages();
        assertThat(messages).hasSize(1)
            .contains("[2行, 6列] : 項目「date1」の値（2000/01/01 00:01:02）の書式は不正です。");
        messages.forEach(System.out::println);
        
        csvReader.close();
        
    }
    
    /**
     * Streamで読み込むテスト - 正常時
     * @since 2.3
     */
    @Test
    public void testLines_normal() throws IOException {
        
        File file = new File("src/test/data/test_read_normal.csv");
        
        CsvAnnotationBeanReader<SampleNormalBean> csvReader = new CsvAnnotationBeanReader<>(
                SampleNormalBean.class,
                new InputStreamReader(new FileInputStream(file), Charset.forName("UTF-8")),
                CsvPreference.STANDARD_PREFERENCE,
                DefaultGroup.class, SampleNormalBean.ReadGroup.class);
        csvReader.setExceptionConverter(exceptionConverter);
        
        List<SampleNormalBean> list = new ArrayList<>();
        
        String[] headers = csvReader.getHeader(true);
        assertThat(headers).hasSize(11);
        
        csvReader.lines().forEach(record -> {
            list.add(record);
            assertBean(record);
        });
        
        assertThat(list).hasSize(2);
        assertThat(csvReader.getErrorMessages()).hasSize(0);
        
        // 順番の確認
        assertThat(list.get(0)).hasFieldOrPropertyWithValue("id", 1);
        assertThat(list.get(1)).hasFieldOrPropertyWithValue("id", 2);
        
        csvReader.close();
    }
    
    private void assertBean(final SampleNormalBean bean) {
        
        if(bean.getId() == 1) {
            assertThat(bean.getNumber1()).isEqualTo(999110);
            assertThat(bean.getNumber2()).isEqualTo(10.2d);
            
            assertThat(bean.getString1()).isEqualTo("abcd");
            assertThat(bean.getString2()).isEqualTo("12345");
            
            assertThat(bean.getDate1()).isEqualTo(toDate(2000, 1, 1, 0, 1, 2));
            assertThat(bean.getDate2()).isEqualTo(toTimestamp(toDate(2000, 2, 3)));
            
            assertThat(bean.getEnum1()).isEqualTo(SampleEnum.RED);
            assertThat(bean.getEnum2()).isEqualTo(SampleEnum.RED);
            
            assertThat(bean.isBoolean1()).isEqualTo(true);
            assertThat(bean.getBoolean2()).isEqualTo(Boolean.TRUE);
            
        } else if(bean.getId() == 2) {
            
            assertThat(bean.getNumber1()).isEqualTo(-12);
            assertThat(bean.getNumber2()).isNull();
            
            assertThat(bean.getString1()).isEqualTo("あいうえお");
            assertThat(bean.getString2()).isEqualTo("");
            
            assertThat(bean.getDate1()).isEqualTo(toDate(2000, 2, 1, 3, 4, 5));
            assertThat(bean.getDate2()).isNull();;
            
            assertThat(bean.getEnum1()).isEqualTo(SampleEnum.BLUE);
            assertThat(bean.getEnum2()).isEqualTo(SampleEnum.BLUE);
            
            assertThat(bean.isBoolean1()).isEqualTo(false);
            assertThat(bean.getBoolean2()).isEqualTo(Boolean.FALSE);
            
        }
        
    }
    
    private void assertBean(final SamplePartialBean bean) {
        
        if(bean.getId() == 1) {
            assertThat(bean.getNumber1()).isEqualTo(999110);
            
            assertThat(bean.getString1()).isEqualTo("abcd");
            
            assertThat(bean.getDate1()).isEqualTo(toDate(2000, 1, 1, 0, 1, 2));
            
            assertThat(bean.getEnum1()).isEqualTo(SampleEnum.RED);
            
            assertThat(bean.isBoolean1()).isEqualTo(true);
            
        } else if(bean.getId() == 2) {
            
            assertThat(bean.getNumber1()).isEqualTo(-12);
            
            assertThat(bean.getString1()).isEqualTo("あいうえお");
            
            assertThat(bean.getDate1()).isEqualTo(toDate(2000, 2, 1, 3, 4, 5));
            
            assertThat(bean.getEnum1()).isEqualTo(SampleEnum.BLUE);
            
            assertThat(bean.isBoolean1()).isEqualTo(false);
            
        }
        
    }
    
    private void assertBean(final SampleFixedColumnBean bean) {
        
        if(bean.getNo() == 1) {
            assertThat(bean.getUserName()).isEqualTo("山田　太郎");
            
            assertThat(bean.getBirthDay()).isEqualTo(LocalDate.of(1980, 1, 28));
            
            assertThat(bean.getComment()).isEqualTo("全ての項目に値が設定");
            
        } else if(bean.getNo() == 2) {
            
            assertThat(bean.getUserName()).isEqualTo("田中　次郎");
            
            assertThat(bean.getBirthDay()).isNull();
            
            assertThat(bean.getComment()).isEqualTo("誕生日の項目が空。");
            
        } else if(bean.getNo() == 3) {
            
            assertThat(bean.getUserName()).isEqualTo("鈴木　三郎");
            
            assertThat(bean.getBirthDay()).isEqualTo(LocalDate.of(2000, 3, 25));
            
            assertThat(bean.getComment()).isEqualTo("コメントを切落とす。あいう。");
            
        }
        
    }
    
    
    
}

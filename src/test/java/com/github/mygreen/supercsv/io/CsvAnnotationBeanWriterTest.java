package com.github.mygreen.supercsv.io;

import static org.junit.Assert.*;
import static org.assertj.core.api.Assertions.*;
import static com.github.mygreen.supercsv.tool.TestUtils.*;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.supercsv.exception.SuperCsvException;
import org.supercsv.prefs.CsvPreference;
import org.supercsv.quote.AlwaysQuoteMode;

import com.github.mygreen.supercsv.annotation.DefaultGroup;
import com.github.mygreen.supercsv.builder.BeanMapping;
import com.github.mygreen.supercsv.builder.BeanMappingFactory;
import com.github.mygreen.supercsv.exception.SuperCsvBindingException;
import com.github.mygreen.supercsv.validation.CsvExceptionConverter;

/**
 * {@link CsvAnnotationBeanReader}のテスタ
 *
 * @since 1.2
 * @author T.TSUCHIE
 *
 */
public class CsvAnnotationBeanWriterTest {
    
    private CsvExceptionConverter exceptionConverter;
    
    @Before
    public void setUp() throws Exception {
        this.exceptionConverter = new CsvExceptionConverter();
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
                CsvPreference.STANDARD_PREFERENCE,
                DefaultGroup.class, SampleNormalBean.WriteGroup.class);
        
        final String[] expectedHeaders = new String[]{
                "id",
                "数字1", "number2",
                "string1", "string2",
                "date1", "date2",
                "enum1", "列挙型2",
                "boolean1", "boolean2"};
        
        final String[] definitionHeaders = csvWriter.getDefinedHeader();
        assertThat(definitionHeaders).containsExactly(expectedHeaders);
        
        csvWriter.writeHeader();
        csvWriter.flush();
        
        for(SampleNormalBean item : list) {
            csvWriter.write(item);
            csvWriter.flush();
        }
        
        String actual = strWriter.toString();
        System.out.println(actual);
        
        String expected = getTextFromFile("src/test/data/test_write_normal.csv", Charset.forName("UTF-8"));
        assertThat(actual).isEqualTo(expected);
        
        assertThat(csvWriter.getErrorMessages()).hasSize(0);
        
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
                CsvPreference.STANDARD_PREFERENCE,
                DefaultGroup.class, SampleNormalBean.WriteGroup.class);
        
        csvWriter.writeAll(list);
        csvWriter.flush();
        
        String actual = strWriter.toString();
        System.out.println(actual);
        
        String expected = getTextFromFile("src/test/data/test_write_normal.csv", Charset.forName("UTF-8"));
        assertThat(actual).isEqualTo(expected);
        
        assertThat(csvWriter.getErrorMessages()).hasSize(0);
        
        csvWriter.close();
        
    }
    
    /**
     * 書き込みのテスト - 追加書き込み
     */
    @Test
    public void testWriteAll_append() throws IOException {
        
        // テストデータの作成
        final List<SampleNormalBean> list = createNormalData();
        
        StringWriter strWriter = new StringWriter();
        
        CsvAnnotationBeanWriter<SampleNormalBean> csvWriter = new CsvAnnotationBeanWriter<>(
                SampleNormalBean.class,
                strWriter,
                CsvPreference.STANDARD_PREFERENCE,
                DefaultGroup.class, SampleNormalBean.WriteGroup.class);
        
        csvWriter.writeAll(list);
        csvWriter.flush();
        
        // 追加データの作成
        final List<SampleNormalBean> append = createNormalData();
        final SampleNormalBean bean3 = append.get(0);
        bean3.setId(3);
        bean3.setNumber1(-98765);
        
        final SampleNormalBean bean4 = append.get(1);
        bean4.setId(4);
        bean4.setNumber1(0);
        
        csvWriter.writeAll(append);
        csvWriter.flush();
        
        String actual = strWriter.toString();
        System.out.println(actual);
        
        String expected = getTextFromFile("src/test/data/test_write_append.csv", Charset.forName("UTF-8"));
        assertThat(actual).isEqualTo(expected);
        
        assertThat(csvWriter.getErrorMessages()).hasSize(0);
        
        csvWriter.close();
        
    }
    
    /**
     * 全件書き込みのテスト - デフォルト設定 - エラーがある場合
     * @since 2.0.2
     */
    @Test
    public void writeAll_error_column() throws IOException {
        
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
                CsvPreference.STANDARD_PREFERENCE,
                DefaultGroup.class, SampleNormalBean.WriteGroup.class);
        
        try {
            csvWriter.writeAll(list);
            csvWriter.flush();
            
            fail();
        } catch(Exception e) {
            
            assertThat(e).isInstanceOf(SuperCsvBindingException.class);
        }
        
        // convert error messages.
        List<String> messages = csvWriter.getErrorMessages();
        assertThat(messages).hasSize(2)
            .contains("[2行, 2列] : 項目「数字1」の値（1,000,000）は、999,999以下の値でなければなりません。"
                    , "[2行, 4列] : 項目「string1」の値は必須です。");
        messages.forEach(System.out::println);
        
        csvWriter.close();
    }
    
    /**
     * 全件書き込みのテスト - デフォルト設定 - エラーがある場合
     * @since 2.0.2
     */
    @Test
    public void writeAll_error_column_continueOnError() throws IOException {
        
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
                CsvPreference.STANDARD_PREFERENCE,
                DefaultGroup.class, SampleNormalBean.WriteGroup.class);
        
        csvWriter.writeAll(list, true);
        csvWriter.flush();
        
        String actual = strWriter.toString();
        System.out.println(actual);
        
        String expected = getTextFromFile("src/test/data/test_write_error_continue.csv", Charset.forName("UTF-8"));
        assertThat(actual).isEqualTo(expected);
        
        // convert error messages.
        List<String> messages = csvWriter.getErrorMessages();
        assertThat(messages).hasSize(2)
            .contains("[2行, 2列] : 項目「数字1」の値（1,000,000）は、999,999以下の値でなければなりません。"
                    , "[2行, 4列] : 項目「string1」の値は必須です。");
        messages.forEach(System.out::println);
        
        csvWriter.close();
        
    }
    
    /**
     * 書き込みのテスト - {@link CsvPreference}のカスタマイズ
     */
    @Test
    public void testWriteAll_custom_preference() throws IOException {
        
        // テストデータの作成
        final List<SampleNormalBean> list = createNormalData();
        
        StringWriter strWriter = new StringWriter();
        
        // タブ区切り、改行コード「LF」、必ずダブルクウォートで囲む設定
        final CsvPreference preference = new CsvPreference.Builder('\"', '\t', "\n")
                .useQuoteMode(new AlwaysQuoteMode())
                .build();
        
        CsvAnnotationBeanWriter<SampleNormalBean> csvWriter = new CsvAnnotationBeanWriter<>(
                SampleNormalBean.class,
                strWriter,
                preference,
                DefaultGroup.class, SampleNormalBean.WriteGroup.class);
        
        csvWriter.writeAll(list, true);
        csvWriter.flush();
        
        String actual = strWriter.toString();
        System.out.println(actual);
        
        String expected = getTextFromFile("src/test/data/test_write_tab.csv", Charset.forName("UTF-8"));
        assertThat(actual).isEqualTo(expected);
        
        assertThat(csvWriter.getErrorMessages()).hasSize(0);
        
        csvWriter.close();
        
    }
    
    /**
     * 書き込みのテスト - 値が不正
     */
    @Test
    public void testWrite_error_column_value() throws IOException {
        
        // テストデータの作成
        final List<SampleNormalBean> list = createNormalData();
        
        // データ1
        final SampleNormalBean bean1 = list.get(0);
        bean1.setNumber1(1_000_000);   // 最大値を超える
        bean1.setString1(null); // 必須
        
        StringWriter strWriter = new StringWriter();
        
        BeanMappingFactory mappingFactory = new BeanMappingFactory();
        mappingFactory.getConfiguration().setSkipValidationOnWrite(false);
        BeanMapping<SampleNormalBean> beanMapping = mappingFactory.create(SampleNormalBean.class,
                DefaultGroup.class, SampleNormalBean.WriteGroup.class);
        
        CsvAnnotationBeanWriter<SampleNormalBean> csvWriter = new CsvAnnotationBeanWriter<>(
                beanMapping,
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
            assertThat(e).isInstanceOf(SuperCsvBindingException.class);
//            e.printStackTrace();
            
        }
        
        // convert error messages.
        List<String> messages = csvWriter.getErrorMessages();
        assertThat(messages).hasSize(2)
            .contains("[2行, 2列] : 項目「数字1」の値（1,000,000）は、999,999以下の値でなければなりません。"
                    , "[2行, 4列] : 項目「string1」の値は必須です。");
        messages.forEach(System.out::println);
        
        csvWriter.close();
        
    }
    
    /**
     * 書き込みのテスト - 制約のチェックをスキップ
     */
    @Test
    public void testWrite_ignoreValidation() throws IOException {
        
        // テストデータの作成
        final List<SampleNormalBean> list = createNormalData();
        
        // データ1
        final SampleNormalBean bean1 = list.get(0);
        bean1.setNumber1(1_000_000);   // 最大値を超える
        bean1.setString1(null); // 必須
        
        StringWriter strWriter = new StringWriter();
        
        BeanMappingFactory mappingFactory = new BeanMappingFactory();
        mappingFactory.getConfiguration().setSkipValidationOnWrite(true);
        BeanMapping<SampleNormalBean> beanMapping = mappingFactory.create(SampleNormalBean.class, 
                DefaultGroup.class, SampleNormalBean.WriteGroup.class);
        
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
        
        String expected = getTextFromFile("src/test/data/test_write_ignore_constaint.csv", Charset.forName("UTF-8"));
        assertThat(actual).isEqualTo(expected);
        
        assertThat(csvWriter.getErrorMessages()).hasSize(0);
        
        csvWriter.close();
        
    }
    
    /**
     * 書き込みのテスト - 部分的に書き込む。
     */
    @Test
    public void testWrite_partial() throws IOException {
        
        // テストデータの作成
        final List<SamplePartialBean> list = createPartialData();
        
        StringWriter strWriter = new StringWriter();
        
        CsvAnnotationBeanWriter<SamplePartialBean> csvWriter = new CsvAnnotationBeanWriter<>(
                SamplePartialBean.class,
                strWriter,
                CsvPreference.STANDARD_PREFERENCE,
                DefaultGroup.class, SampleNormalBean.WriteGroup.class);
        
        final String[] expectedHeaders = new String[]{
                "id",
                "数字1", "number2",
                "string1", "string2",
                "date1", "date2",
                "enum1", "列挙型2",
                "boolean1", "boolean2"};
        
        final String[] definitionHeaders = csvWriter.getDefinedHeader();
        assertThat(definitionHeaders).containsExactly(expectedHeaders);
        
        csvWriter.writeHeader();
        csvWriter.flush();
        
        for(SamplePartialBean item : list) {
            csvWriter.write(item);
            csvWriter.flush();
        }
        
        String actual = strWriter.toString();
        System.out.println(actual);
        
        String expected = getTextFromFile("src/test/data/test_write_partial.csv", Charset.forName("UTF-8"));
        assertThat(actual).isEqualTo(expected);
        
        assertThat(csvWriter.getErrorMessages()).hasSize(0);
        
        csvWriter.close();
        
    }
    
    /**
     * 書き込み用のデータを作成する
     * @return
     */
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
     * 部分的なカラムの書き込み用のデータを作成する
     * @return
     */
    private List<SamplePartialBean> createPartialData() {
        
        // テストデータの作成
        final List<SamplePartialBean> list = new ArrayList<>();
        
        // データ1
        final SamplePartialBean bean1 = new SamplePartialBean();
        list.add(bean1);
        bean1.setId(1);
        
        bean1.setNumber1(999110);
        
        bean1.setString1("abcd");
        
        bean1.setDate1(toDate(2000, 1, 1, 0, 1, 2));
        
        bean1.setEnum1(SampleEnum.RED);
        
        bean1.setBoolean1(true);
        
        // データ2
        final SamplePartialBean bean2 = new SamplePartialBean();
        list.add(bean2);
        bean2.setId(2);
        
        bean2.setNumber1(-12);
        
        bean2.setString1("あいうえお");
        
        bean2.setDate1(toDate(2000, 2, 1, 3, 4, 5));
        
        bean2.setEnum1(SampleEnum.BLUE);
        
        bean2.setBoolean1(false);
        
        return list;
    }
    
    /**
     * テキストファイルを読み込む。
     * @param path
     * @param charset
     * @return
     * @throws IOException
     */
    private String getTextFromFile(final String path, final Charset charset) throws IOException {
        
        byte[] data = Files.readAllBytes(Paths.get(path));
        return new String(data, charset);
        
    }
    
}

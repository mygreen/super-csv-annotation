package com.github.mygreen.supercsv.io;

import static org.junit.Assert.*;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static com.github.mygreen.supercsv.tool.TestUtils.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.supercsv.prefs.CsvPreference;

import com.github.mygreen.supercsv.builder.BeanMapping;
import com.github.mygreen.supercsv.builder.ColumnMapping;
import com.github.mygreen.supercsv.builder.LazyBeanMappingFactory;
import com.github.mygreen.supercsv.exception.SuperCsvBindingException;
import com.github.mygreen.supercsv.validation.CsvExceptionConverter;

/**
 * {@link LazyCsvAnnotationBeanWriter}のテスタ
 *
 * @since 2.1
 * @author T.TSUCHIE
 *
 */
public class LazyCsvAnnotationBeanWriterTest {
    
    /**
     * 初期化が未実施のときのエラーメッセージ
     */
    private static final String MESSAGE_NOT_INIT = "見出し情報を元にした初期化が完了していません。LazyCsvAnnotationBeanWriter#init() で初期化する必要があります。";
    
    private CsvExceptionConverter exceptionConverter;
    
    @Before
    public void setUp() throws Exception {
        this.exceptionConverter = new CsvExceptionConverter();
    }
    
    /**
     * 書き込みのテスト
     * ・ヘッダーの指定なし
     */
    @Test
    public void testWrite_noSetHeaders() throws Exception {
        
        // テストデータの作成
        final List<SampleLazyBean> list = createNormalData();
        
        StringWriter strWriter = new StringWriter();
        
        LazyCsvAnnotationBeanWriter<SampleLazyBean> csvWriter = new LazyCsvAnnotationBeanWriter<>(
                SampleLazyBean.class,
                strWriter,
                CsvPreference.STANDARD_PREFERENCE);
        
        csvWriter.init();
        
        final String[] expectedHeaders = new String[]{
                "生年月日",
                "name",
                "no",
                "備考"
                };
        
        final String[] definitionHeaders = csvWriter.getDefinedHeader();
        assertThat(definitionHeaders).containsExactly(expectedHeaders);
        
        csvWriter.writeHeader();
        csvWriter.flush();
        
        for(SampleLazyBean item : list) {
            csvWriter.write(item);
            csvWriter.flush();
        }
        
        String actual = strWriter.toString();
        System.out.println(actual);
        
        String expected = getTextFromFile("src/test/data/test_write_lazy_noSetHeader.csv", Charset.forName("UTF-8"));
        assertThat(actual).isEqualTo(expected);
        
        assertThat(csvWriter.getErrorMessages()).hasSize(0);
        
        csvWriter.close();
    }
    
    /**
     * 書き込みのテスト
     * ・ヘッダーの指定あり
     */
    @Test
    public void testWrite_setHeaders() throws Exception {
        
        // テストデータの作成
        final List<SampleLazyBean> list = createNormalData();
        
        StringWriter strWriter = new StringWriter();
        
        LazyCsvAnnotationBeanWriter<SampleLazyBean> csvWriter = new LazyCsvAnnotationBeanWriter<>(
                SampleLazyBean.class,
                strWriter,
                CsvPreference.STANDARD_PREFERENCE);
        
        csvWriter.init("no", "name", "生年月日", "備考");
        
        final String[] expectedHeaders = new String[]{
                "no",
                "name",
                "生年月日",
                "備考"
                };
        
        final String[] definitionHeaders = csvWriter.getDefinedHeader();
        assertThat(definitionHeaders).containsExactly(expectedHeaders);
        
        csvWriter.writeHeader();
        csvWriter.flush();
        
        for(SampleLazyBean item : list) {
            csvWriter.write(item);
            csvWriter.flush();
        }
        
        String actual = strWriter.toString();
        System.out.println(actual);
        
        String expected = getTextFromFile("src/test/data/test_write_lazy_setHeader.csv", Charset.forName("UTF-8"));
        assertThat(actual).isEqualTo(expected);
        
        assertThat(csvWriter.getErrorMessages()).hasSize(0);
        
        csvWriter.close();
    }
    
    /**
     * 全て書き出す - 初期化は自動的に行う。
     */
    @Test
    public void testWriteAll() throws Exception {
        
        // テストデータの作成
        final List<SampleLazyBean> list = createNormalData();
        
        StringWriter strWriter = new StringWriter();
        
        LazyCsvAnnotationBeanWriter<SampleLazyBean> csvWriter = new LazyCsvAnnotationBeanWriter<>(
                SampleLazyBean.class,
                strWriter,
                CsvPreference.STANDARD_PREFERENCE);
        
        csvWriter.writeAll(list);
        
        final String[] expectedHeaders = new String[]{
                "生年月日",
                "name",
                "no",
                "備考"
                };
        
        final String[] definitionHeaders = csvWriter.getDefinedHeader();
        assertThat(definitionHeaders).containsExactly(expectedHeaders);
        
        String actual = strWriter.toString();
        System.out.println(actual);
        
        String expected = getTextFromFile("src/test/data/test_write_lazy_noSetHeader.csv", Charset.forName("UTF-8"));
        assertThat(actual).isEqualTo(expected);
        
        assertThat(csvWriter.getErrorMessages()).hasSize(0);
        
        csvWriter.close();
        
    }
    
    /**
     * 全て書き出す - 追加に書き出す
     */
    @Test
    public void testWriteAll_append() throws Exception {
        
        // テストデータの作成
        final List<SampleLazyBean> list = createNormalData();
        
        StringWriter strWriter = new StringWriter();
        
        LazyCsvAnnotationBeanWriter<SampleLazyBean> csvWriter = new LazyCsvAnnotationBeanWriter<>(
                SampleLazyBean.class,
                strWriter,
                CsvPreference.STANDARD_PREFERENCE);
        
        csvWriter.writeAll(list);
        
        // 追加データの作成
        final List<SampleLazyBean> list2 = new ArrayList<>();
        {
            final SampleLazyBean bean = new SampleLazyBean();
            bean.setNo(3);
            bean.setName("山本花子");
            bean.setBirthday(LocalDate.of(2005, 3, 5));
            bean.setComment("かきくけこ");
            
            list2.add(bean);
            
        }
        
        {
            final SampleLazyBean bean = new SampleLazyBean();
            bean.setNo(4);
            bean.setName("佐藤三郎");
            bean.setBirthday(LocalDate.of(2011, 4, 12));
            bean.setComment("");
            
            list2.add(bean);
            
        }
        csvWriter.writeAll(list2);
        
        String actual = strWriter.toString();
        System.out.println(actual);
        
        String expected = getTextFromFile("src/test/data/test_write_lazy_noSetHeader_append.csv", Charset.forName("UTF-8"));
        assertThat(actual).isEqualTo(expected);
        
        assertThat(csvWriter.getErrorMessages()).hasSize(0);
        
        csvWriter.close();
        
    }
    
    /**
     * 全て書き出す - ヘッダーを持たない設定の場合
     */
    @Test
    public void testWriteAll_noHeader() throws Exception {
        
        // BeanMappingの作成
        LazyBeanMappingFactory factory = new LazyBeanMappingFactory();
        BeanMapping<SampleLazyBean> beanMapping = factory.create(SampleLazyBean.class);
        
        // ヘッダーを持たない設定に変更する
        beanMapping.setHeader(false);
        
        // テストデータの作成
        final List<SampleLazyBean> list = createNormalData();
        
        StringWriter strWriter = new StringWriter();
        
        LazyCsvAnnotationBeanWriter<SampleLazyBean> csvWriter = new LazyCsvAnnotationBeanWriter<>(
                beanMapping,
                strWriter,
                CsvPreference.STANDARD_PREFERENCE);
        
        csvWriter.writeAll(list);
        
        final String[] expectedHeaders = new String[]{
                "生年月日",
                "name",
                "no",
                "備考"
                };
        
        final String[] definitionHeaders = csvWriter.getDefinedHeader();
        assertThat(definitionHeaders).containsExactly(expectedHeaders);
        
        String actual = strWriter.toString();
        System.out.println(actual);
        
        String expected = getTextFromFile("src/test/data/test_write_lazy_noSetHeader_noHeader.csv", Charset.forName("UTF-8"));
        assertThat(actual).isEqualTo(expected);
        
        assertThat(csvWriter.getErrorMessages()).hasSize(0);
        
        csvWriter.close();
        
    }
    
    /**
     * 全てのデータを書き出す - エラーがある場合
     */
    @Test
    public void testWriteAll_error_column() throws Exception {
        
        // テストデータの作成
        final List<SampleLazyBean> list = createNormalData();
        
        {
            // データ1
            final SampleLazyBean bean = list.get(0);
            bean.setName(null); // 必須
        }
        
        StringWriter strWriter = new StringWriter();
        
        LazyCsvAnnotationBeanWriter<SampleLazyBean> csvWriter = new LazyCsvAnnotationBeanWriter<>(
                SampleLazyBean.class,
                strWriter,
                CsvPreference.STANDARD_PREFERENCE);
        
        assertThatThrownBy(() -> csvWriter.writeAll(list))
            .isInstanceOf(SuperCsvBindingException.class);
        
        
        // convert error messages.
        List<String> messages = csvWriter.getErrorMessages();
        assertThat(messages).hasSize(1)
            .contains("[2行, 2列] : 項目「name」の値は必須です。");
        messages.forEach(System.out::println);
        
        csvWriter.close();
        
    }
    
    /**
     * 全てのデータを書き出す - エラーがあっても続ける
     */
    @Test
    public void testWriteAll_error_column_continueOnError() throws Exception {
        
        // テストデータの作成
        final List<SampleLazyBean> list = createNormalData();
        
        {
            // データ1
            final SampleLazyBean bean = list.get(0);
            bean.setName(null); // 必須
        }
        
        StringWriter strWriter = new StringWriter();
        
        LazyCsvAnnotationBeanWriter<SampleLazyBean> csvWriter = new LazyCsvAnnotationBeanWriter<>(
                SampleLazyBean.class,
                strWriter,
                CsvPreference.STANDARD_PREFERENCE);
        
        csvWriter.writeAll(list, true);
        csvWriter.flush();
        
        String actual = strWriter.toString();
        System.out.println(actual);
        
        String expected = getTextFromFile("src/test/data/test_write_lazy_error_continue.csv", Charset.forName("UTF-8"));
        assertThat(actual).isEqualTo(expected);
        
        // convert error messages.
        List<String> messages = csvWriter.getErrorMessages();
        assertThat(messages).hasSize(1)
            .contains("[2行, 2列] : 項目「name」の値は必須です。");
        messages.forEach(System.out::println);
        
        csvWriter.close();
        
    }
    
    /**
     * 初期化が完了していない場合
     */
    @Test
    public void testWrite_notInit() throws Exception {
        
        StringWriter strWriter = new StringWriter();
        
        LazyCsvAnnotationBeanWriter<SampleLazyBean> csvWriter = new LazyCsvAnnotationBeanWriter<>(
                SampleLazyBean.class,
                strWriter,
                CsvPreference.STANDARD_PREFERENCE);
        
        // writeメソッド
        assertThatThrownBy(() -> csvWriter.write(createNormalData().get(0)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(MESSAGE_NOT_INIT);
        
        // getDefinedHeaderメソッド
        assertThatThrownBy(() -> csvWriter.getDefinedHeader())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(MESSAGE_NOT_INIT);
        
        // getBeanMappingメソッド
        assertThatThrownBy(() -> csvWriter.getBeanMapping())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(MESSAGE_NOT_INIT);
        
        // writeHeaderメソッド
        assertThatThrownBy(() -> csvWriter.writeHeader())
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining(MESSAGE_NOT_INIT);
        
        csvWriter.close();
    }
    
    /**
     * 部分的な書き込み - ヘッダーの指定あり
     */
    @Test
    public void testWrite_partial_setHeader() throws Exception {
        
        // テストデータの作成
        final List<SampleLazyPartialBean> list = createPartialData();
        
        StringWriter strWriter = new StringWriter();
        
        LazyCsvAnnotationBeanWriter<SampleLazyPartialBean> csvWriter = new LazyCsvAnnotationBeanWriter<>(
                SampleLazyPartialBean.class,
                strWriter,
                CsvPreference.STANDARD_PREFERENCE);
        
        final String[] headers = new String[]{
                "id",
                "名前",
                "誕生日",
                "電話番号",
                "住所",
                "有効期限",
                "削除フラグ",
                "備考"
            };
        
        csvWriter.init(headers);
        
        final String[] definitionHeaders = csvWriter.getDefinedHeader();
        assertThat(definitionHeaders).containsExactly(headers);
        
        {
            // カラム情報のチェック
            List<ColumnMapping> columnMappingList = csvWriter.getBeanMapping().getColumns();
            assertThat(columnMappingList).hasSize(8);
            for(int i=0; i < columnMappingList.size(); i++) {
                int number = i+1;
                ColumnMapping columnMapping = columnMappingList.get(i);
                assertThat(columnMapping.getNumber()).isEqualTo(number);
                assertThat(columnMapping.getLabel()).isEqualTo(headers[i]);
                assertThat(columnMapping.isDeterminedNumber()).isEqualTo(true);
                
                if(number == 1 || number == 2 || number == 6 || number == 8) {
                    assertThat(columnMapping.isPartialized()).isEqualTo(false);
                } else {
                    assertThat(columnMapping.isPartialized()).isEqualTo(true);
                    
                }
            }
        }
        
        csvWriter.writeHeader();
        
        for(SampleLazyPartialBean item : list) {
            csvWriter.write(item);
            csvWriter.flush();
        }
        
        String actual = strWriter.toString();
        System.out.println(actual);
        
        String expected = getTextFromFile("src/test/data/test_write_lazy_partial.csv", Charset.forName("UTF-8"));
        assertThat(actual).isEqualTo(expected);
        
        assertThat(csvWriter.getErrorMessages()).hasSize(0);
        
        csvWriter.close();
        
    }
    
    /**
     * 部分的な書き込み - ヘッダーの指定なし
     */
    @Test
    public void testWrite_partial_noSetHeader() throws Exception {
        
        // テストデータの作成
        final List<SampleLazyPartialBean> list = createPartialData();
        
        StringWriter strWriter = new StringWriter();
        
        LazyCsvAnnotationBeanWriter<SampleLazyPartialBean> csvWriter = new LazyCsvAnnotationBeanWriter<>(
                SampleLazyPartialBean.class,
                strWriter,
                CsvPreference.STANDARD_PREFERENCE);
        
        final String[] expectedHeaders = new String[]{
                "id",
                "備考",
                "誕生日",
                "名前",
                "住所",
                "有効期限",
                "column7",
                "column8",
            };
        
        csvWriter.init();
        
        final String[] definitionHeaders = csvWriter.getDefinedHeader();
        assertThat(definitionHeaders).containsExactly(expectedHeaders);
        
        {
            // カラム情報のチェック
            List<ColumnMapping> columnMappingList = csvWriter.getBeanMapping().getColumns();
            assertThat(columnMappingList).hasSize(8);
            for(int i=0; i < columnMappingList.size(); i++) {
                int number = i+1;
                ColumnMapping columnMapping = columnMappingList.get(i);
                assertThat(columnMapping.getNumber()).isEqualTo(number);
                assertThat(columnMapping.getLabel()).isEqualTo(expectedHeaders[i]);
                assertThat(columnMapping.isDeterminedNumber()).isEqualTo(true);
                
                if(number == 1 || number == 2 || number == 4 || number == 6) {
                    assertThat(columnMapping.isPartialized()).isEqualTo(false);
                } else {
                    assertThat(columnMapping.isPartialized()).isEqualTo(true);
                    
                }
            }
        }
        
        csvWriter.writeHeader();
        
        for(SampleLazyPartialBean item : list) {
            csvWriter.write(item);
            csvWriter.flush();
        }
        
        String actual = strWriter.toString();
        System.out.println(actual);
        
        String expected = getTextFromFile("src/test/data/test_write_lazy_partial_noSetHeader.csv", Charset.forName("UTF-8"));
        assertThat(actual).isEqualTo(expected);
        
        assertThat(csvWriter.getErrorMessages()).hasSize(0);
        
        csvWriter.close();
        
    }
    
    /**
     * 固定長のカラムの書き込み - ヘッダーの指定あり
     */
    @Test
    public void testWrite_fixedColumn_setHeader() throws Exception {
        
        // テストデータの作成
        final List<SampleLazyFixedColumnBean> list = createFixedColumnData();
        
        StringWriter strWriter = new StringWriter();
        
        LazyCsvAnnotationBeanWriter<SampleLazyFixedColumnBean> csvWriter = new LazyCsvAnnotationBeanWriter<>(
                SampleLazyFixedColumnBean.class,
                strWriter,
                CsvPreference.STANDARD_PREFERENCE);
        
        final String[] headers = new String[]{
                "   no",
                "ユーザ名　　　　　　",
                "誕生日____",
                "コメント            "
            };
        
        csvWriter.init(headers);
        
        final String[] definitionHeaders = csvWriter.getDefinedHeader();
        assertThat(definitionHeaders).containsExactly(headers);
        
        csvWriter.writeHeader();
        csvWriter.flush();
        
        for(SampleLazyFixedColumnBean item : list) {
            csvWriter.write(item);
            csvWriter.flush();
        }
        
        String actual = strWriter.toString();
        System.out.println(actual);
        
        String expected = getTextFromFile("src/test/data/test_write_lazy_fixedColumn_setHeader.csv", Charset.forName("UTF-8"));
        assertThat(actual).isEqualTo(expected);
        
        assertThat(csvWriter.getErrorMessages()).hasSize(0);
        
        csvWriter.close();
        
    }
    
    /**
     * 固定長のカラムの書き込み - ヘッダーの指定なし
     */
    @Test
    public void testWrite_fixedColumn_noSetHeader() throws Exception {
        
        // テストデータの作成
        final List<SampleLazyFixedColumnBean> list = createFixedColumnData();
        
        StringWriter strWriter = new StringWriter();
        
        LazyCsvAnnotationBeanWriter<SampleLazyFixedColumnBean> csvWriter = new LazyCsvAnnotationBeanWriter<>(
                SampleLazyFixedColumnBean.class,
                strWriter,
                CsvPreference.STANDARD_PREFERENCE);
        
        final String[] expectedHeaders = new String[]{
                "   no",
                "誕生日____",
                "コメント            ",
                "ユーザ名　　　　　　"
            };
        
        csvWriter.init();
        
        final String[] definitionHeaders = csvWriter.getDefinedHeader();
        assertThat(definitionHeaders).containsExactly(expectedHeaders);
        
        csvWriter.writeHeader();
        csvWriter.flush();
        
        for(SampleLazyFixedColumnBean item : list) {
            csvWriter.write(item);
            csvWriter.flush();
        }
        
        String actual = strWriter.toString();
        System.out.println(actual);
        
        String expected = getTextFromFile("src/test/data/test_write_lazy_fixedColumn_noSetHeader.csv", Charset.forName("UTF-8"));
        assertThat(actual).isEqualTo(expected);
        
        assertThat(csvWriter.getErrorMessages()).hasSize(0);
        
        csvWriter.close();
        
    }
    
    /**
     * 書き込み用のデータを作成する
     * @return
     */
    private List<SampleLazyBean> createNormalData() {
        
        // テストデータの作成
        final List<SampleLazyBean> list = new ArrayList<>();
        
        {
            final SampleLazyBean bean = new SampleLazyBean();
            bean.setNo(1);
            bean.setName("山田太郎");
            bean.setBirthday(LocalDate.of(2000, 10, 1));
            bean.setComment("あいうえお");
            
            list.add(bean);
        }
        
        {
            final SampleLazyBean bean = new SampleLazyBean();
            bean.setNo(2);
            bean.setName("鈴木次郎");
            bean.setBirthday(LocalDate.of(2012, 1, 2));
            bean.setComment(null);
            
            list.add(bean);
        }
        
        return list;
    }
    
    /**
     * 部分的な書き込み用のデータを作成する
     * @return
     */
    private List<SampleLazyPartialBean> createPartialData() {
        
        final List<SampleLazyPartialBean> list = new ArrayList<>();
        
        {
            final SampleLazyPartialBean bean = new SampleLazyPartialBean();
            bean.setId(1);
            bean.setName("山田太郎");
            bean.setExpiredDate(LocalDate.of(1980, 1, 2));
            bean.setComment("コメント1");
            
            list.add(bean);
        }
        
        {
            final SampleLazyPartialBean bean = new SampleLazyPartialBean();
            bean.setId(2);
            bean.setName("鈴木次郎");
            bean.setExpiredDate(LocalDate.of(1982, 2, 3));
            bean.setComment(null);
            
            list.add(bean);
        }
        
        {
            final SampleLazyPartialBean bean = new SampleLazyPartialBean();
            bean.setId(3);
            bean.setName("佐藤花子");
            bean.setExpiredDate(LocalDate.of(1983, 3, 4));
            bean.setComment("コメント3");
            
            list.add(bean);
        }
        
        return list;
        
    }
    
    /**
     * 固定長カラムの書き込み用のデータを作成する。
     * @return
     */
    private List<SampleLazyFixedColumnBean> createFixedColumnData() {
        
        final List<SampleLazyFixedColumnBean> list = new ArrayList<>();
        
        {
            final SampleLazyFixedColumnBean bean = new SampleLazyFixedColumnBean();
            bean.setNo(1);
            bean.setUserName("山田　太郎");
            bean.setBirthDay(LocalDate.of(1980, 1, 28));
            bean.setComment("全ての項目に値が設定");
            
            list.add(bean);
        }
        
        {
            final SampleLazyFixedColumnBean bean = new SampleLazyFixedColumnBean();
            bean.setNo(2);
            bean.setUserName("田中　次郎");
            bean.setBirthDay(null);
            bean.setComment("生日の項目が空。");
            
            list.add(bean);
        }
        
        {
            final SampleLazyFixedColumnBean bean = new SampleLazyFixedColumnBean();
            bean.setNo(1);
            bean.setUserName("鈴木　三郎");
            bean.setBirthDay(LocalDate.of(2000, 3, 25));
            bean.setComment("コメントを切落とす。あいう。");
            
            list.add(bean);
        }
        
        return list;
        
    }
    
    
}

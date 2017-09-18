package com.github.mygreen.supercsv.io;

import static org.junit.Assert.*;
import static org.assertj.core.api.Assertions.*;
import static com.github.mygreen.supercsv.tool.TestUtils.*;

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
import org.supercsv.prefs.CsvPreference;

import com.github.mygreen.supercsv.builder.BeanMapping;
import com.github.mygreen.supercsv.builder.BeanMappingFactory;
import com.github.mygreen.supercsv.builder.ColumnMapping;
import com.github.mygreen.supercsv.builder.LazyBeanMappingFactory;
import com.github.mygreen.supercsv.exception.SuperCsvBindingException;
import com.github.mygreen.supercsv.exception.SuperCsvNoMatchColumnSizeException;
import com.github.mygreen.supercsv.exception.SuperCsvNoMatchHeaderException;
import com.github.mygreen.supercsv.validation.CsvExceptionConverter;


/**
 * {@link LazyCsvAnnotationBeanReader}のテスタ
 * 
 * @since 2.1
 * @author T.TSUCHIE
 *
 */
public class LazyCsvAnnotationBeanReaderTest {
    
    private CsvExceptionConverter exceptionConverter;
    
    /**
     * 初期化が未実施のときのエラーメッセージ
     */
    private static final String MESSAGE_NOT_INIT = "見出し情報を元にした初期化が完了していません。LazyCsvAnnotationBeanReader#init() で初期化する必要があります。";
        
    @Before
    public void setUp() throws Exception {
        this.exceptionConverter = new CsvExceptionConverter();
    }
    
    /**
     * 正常系のテスト
     */
    @Test
    public void testRead() throws IOException {
        
        File file = new File("src/test/data/test_read_lazy.csv");
        
        LazyCsvAnnotationBeanReader<SampleLazyBean> csvReader = new LazyCsvAnnotationBeanReader<>(
                SampleLazyBean.class,
                new InputStreamReader(new FileInputStream(file), Charset.forName("UTF-8")),
                CsvPreference.STANDARD_PREFERENCE);
        csvReader.setExceptionConverter(exceptionConverter);
        
        List<SampleLazyBean> list = new ArrayList<>();
        
        final String[] expectedHeaders = new String[]{
                "no",
                "name",
                "生年月日",
                "備考"
            };
        
        // read header
        final String[] csvHeaders = csvReader.init();
        assertThat(csvHeaders).containsExactly(expectedHeaders);
        
        final String[] definitionHeaders = csvReader.getDefinedHeader();
        assertThat(definitionHeaders).containsExactly(expectedHeaders);
        
        SampleLazyBean bean;
        while((bean = csvReader.read()) != null) {
            list.add(bean);
            
            assertBean(bean);
        }
        
        assertThat(list).hasSize(2);
        assertThat(csvReader.getErrorMessages()).hasSize(0);
        
        csvReader.close();
        
    }
    
    /**
     * 全て読み込む - 初期化は自動的う。
     */
    @Test
    public void testReadAll() throws Exception {
        
        File file = new File("src/test/data/test_read_lazy.csv");
        
        LazyCsvAnnotationBeanReader<SampleLazyBean> csvReader = new LazyCsvAnnotationBeanReader<>(
                SampleLazyBean.class,
                new InputStreamReader(new FileInputStream(file), Charset.forName("UTF-8")),
                CsvPreference.STANDARD_PREFERENCE);
        csvReader.setExceptionConverter(exceptionConverter);
        
        List<SampleLazyBean> list = csvReader.readAll();
        assertThat(list).hasSize(2);
        
        for(SampleLazyBean bean : list) {
            assertThat(bean);
        }
        
        final String[] expectedHeaders = new String[]{
                "no",
                "name",
                "生年月日",
                "備考"
            };
        
        // read header
        final String[] definitionHeaders = csvReader.getDefinedHeader();
        assertThat(definitionHeaders).containsExactly(expectedHeaders);
        
        assertThat(csvReader.getErrorMessages()).hasSize(0);
        
        csvReader.close();
        
    }
    
    /**
     * 全て読み込み - CSVファイルにヘッダーがない場合
     */
    @Test
    public void testReadAll_noHeader() throws Exception {
        
        File file = new File("src/test/data/test_read_lazy_noHeader.csv");
        
        LazyBeanMappingFactory factory = new LazyBeanMappingFactory();
        BeanMapping<SampleLazyBean> beanMapping = factory.create(SampleLazyBean.class);
        
        // ヘッダーを持たないと設定する
        beanMapping.setHeader(false);
        
        LazyCsvAnnotationBeanReader<SampleLazyBean> csvReader = new LazyCsvAnnotationBeanReader<>(
                beanMapping,
                new InputStreamReader(new FileInputStream(file), Charset.forName("UTF-8")),
                CsvPreference.STANDARD_PREFERENCE);
        csvReader.setExceptionConverter(exceptionConverter);
        
        final String[] headers = new String[]{
                "no",
                "name",
                "生年月日",
                "備考"
            };
        
        csvReader.init(headers);
        
        List<SampleLazyBean> list = csvReader.readAll();
        assertThat(list).hasSize(2);
        
        for(SampleLazyBean bean : list) {
            assertThat(bean);
        }
        
        assertThat(csvReader.getErrorMessages()).hasSize(0);
        
        csvReader.close();
        
    }
    
    /**
     * 全て読み込み- CSVファイルにヘッダーがない場合 - 初期化をしていない場合
     */
    @Test
    public void testReadAll_noHeader_noInit() throws Exception {
        
        File file = new File("src/test/data/test_read_lazy_noHeader.csv");
        
        LazyBeanMappingFactory factory = new LazyBeanMappingFactory();
        BeanMapping<SampleLazyBean> beanMapping = factory.create(SampleLazyBean.class);
        
        // ヘッダーを持たないと設定する
        beanMapping.setHeader(false);
        
        LazyCsvAnnotationBeanReader<SampleLazyBean> csvReader = new LazyCsvAnnotationBeanReader<>(
                beanMapping,
                new InputStreamReader(new FileInputStream(file), Charset.forName("UTF-8")),
                CsvPreference.STANDARD_PREFERENCE);
        csvReader.setExceptionConverter(exceptionConverter);
        
        assertThatThrownBy(() -> csvReader.readAll())
            .isInstanceOf(IllegalStateException.class)
            .hasMessage(MESSAGE_NOT_INIT);
        
        csvReader.close();
        
    }
    
    /**
     * 全件読み込み - カラムにエラーがある場合
     */
    @Test
    public void testReadAll_error_column() throws Exception {
        
        File file = new File("src/test/data/test_read_lazy_wrong_pattern.csv");
        
        LazyCsvAnnotationBeanReader<SampleLazyBean> csvReader = new LazyCsvAnnotationBeanReader<>(
                SampleLazyBean.class,
                new InputStreamReader(new FileInputStream(file), Charset.forName("UTF-8")),
                CsvPreference.STANDARD_PREFERENCE);
        csvReader.setExceptionConverter(exceptionConverter);
        
        try {
            List<SampleLazyBean> list = csvReader.readAll();
        
        } catch(Exception e) {
            assertThat(e).isInstanceOf(SuperCsvBindingException.class);
        }
        
        // convert error messages.
        List<String> messages = csvReader.getErrorMessages();
        assertThat(messages).hasSize(1)
            .contains("[2行, 3列] : 項目「生年月日」の値（2000-10-01）は、日時の書式「uuuu/MM/dd」として不正です。");
        messages.forEach(System.out::println);
        
        csvReader.close();
        
    }
    
    /**
     * 全件読み込み - カラムにエラーがある場合も処理を続ける
     */
    @Test
    public void testReadAll_error_continueOnError() throws Exception {
        
        File file = new File("src/test/data/test_read_lazy_wrong_pattern.csv");
        
        LazyCsvAnnotationBeanReader<SampleLazyBean> csvReader = new LazyCsvAnnotationBeanReader<>(
                SampleLazyBean.class,
                new InputStreamReader(new FileInputStream(file), Charset.forName("UTF-8")),
                CsvPreference.STANDARD_PREFERENCE);
        csvReader.setExceptionConverter(exceptionConverter);
        
        List<SampleLazyBean> list = csvReader.readAll(true);
        assertThat(list).hasSize(1);
        
        for(SampleLazyBean bean : list) {
            assertBean(bean);
        }
        
        // convert error messages.
        List<String> messages = csvReader.getErrorMessages();
        assertThat(messages).hasSize(1)
            .contains("[2行, 3列] : 項目「生年月日」の値（2000-10-01）は、日時の書式「uuuu/MM/dd」として不正です。");
        messages.forEach(System.out::println);
        
        csvReader.close();
        
    }
    
    /**
     * 初期化に失敗する場合 - Beanに定義してある情報とCSVの列数が一致しない場合
     */
    @Test
    public void testInit_noMatchColumnSize() throws Exception {
        
        
        File file = new File("src/test/data/test_read_lazy.csv");
        
        LazyCsvAnnotationBeanReader<SampleLazyBean> csvReader = new LazyCsvAnnotationBeanReader<>(
                SampleLazyBean.class,
                new InputStreamReader(new FileInputStream(file), Charset.forName("UTF-8")),
                CsvPreference.STANDARD_PREFERENCE);
        csvReader.setExceptionConverter(exceptionConverter);
        
        final String[] headers = new String[]{
                "no",
                "name",
                "生年月日",
                "備考",
                "あああ"
            };
        
        assertThatThrownBy(() -> csvReader.init(headers))
            .isInstanceOf(SuperCsvNoMatchColumnSizeException.class);
        
        assertThat(csvReader.getErrorMessages())
            .hasSize(1)
            .containsExactly("[1行] : 列数が不正です。 4列で設定すべきですが、実際には5列になっています。");
    }
    
    /**
     * 初期化が完了していない場合
     */
    @Test
    public void testRead_notInit() throws Exception {
        
        File file = new File("src/test/data/test_read_lazy.csv");
        
        LazyCsvAnnotationBeanReader<SampleLazyBean> csvReader = new LazyCsvAnnotationBeanReader<>(
                SampleLazyBean.class,
                new InputStreamReader(new FileInputStream(file), Charset.forName("UTF-8")),
                CsvPreference.STANDARD_PREFERENCE);
        csvReader.setExceptionConverter(exceptionConverter);
        
        // readメソッド
        assertThatThrownBy(() -> csvReader.read())
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining(MESSAGE_NOT_INIT);
        
        // getDefinedHeaderメソッド
        assertThatThrownBy(() -> csvReader.getDefinedHeader())
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining(MESSAGE_NOT_INIT);
        
        // getBeanMappingメソッド
        assertThatThrownBy(() -> csvReader.getBeanMapping())
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining(MESSAGE_NOT_INIT);
        
    
    }
    
    /**
     * 部分的にカラムを読み込む
     */
    @Test
    public void testRead_parital() throws Exception {
        
        File file = new File("src/test/data/test_read_lazy_partial.csv");
        
        LazyCsvAnnotationBeanReader<SampleLazyPartialBean> csvReader = new LazyCsvAnnotationBeanReader<>(
                SampleLazyPartialBean.class,
                new InputStreamReader(new FileInputStream(file), Charset.forName("UTF-8")),
                CsvPreference.STANDARD_PREFERENCE);
        csvReader.setExceptionConverter(exceptionConverter);
        
        List<SampleLazyPartialBean> list = new ArrayList<>();
        
        final String[] expectedHeaders = new String[]{
                "id",
                "名前",
                "誕生日",
                "電話番号",
                "住所",
                "有効期限",
                "削除フラグ",
                "備考"
            };
        
        // read header
        final String[] csvHeaders = csvReader.init();
        assertThat(csvHeaders).containsExactly(expectedHeaders);
        
        final String[] definitionHeaders = csvReader.getDefinedHeader();
        assertThat(definitionHeaders).containsExactly(expectedHeaders);
        
        {
            // カラム情報のチェック
            List<ColumnMapping> columnMappingList = csvReader.getBeanMapping().getColumns();
            assertThat(columnMappingList).hasSize(8);
            for(int i=0; i < columnMappingList.size(); i++) {
                int number = i+1;
                ColumnMapping columnMapping = columnMappingList.get(i);
                assertThat(columnMapping.getNumber()).isEqualTo(number);
                assertThat(columnMapping.getLabel()).isEqualTo(expectedHeaders[i]);
                assertThat(columnMapping.isDeterminedNumber()).isEqualTo(true);
                
                if(number == 1 || number == 2 || number == 6 || number == 8) {
                    assertThat(columnMapping.isPartialized()).isEqualTo(false);
                } else {
                    assertThat(columnMapping.isPartialized()).isEqualTo(true);
                    
                }
            }
        }
        
        SampleLazyPartialBean bean;
        while((bean = csvReader.read()) != null) {
            list.add(bean);
            
            assertBean(bean);
        }
        
        assertThat(list).hasSize(3);
        assertThat(csvReader.getErrorMessages()).hasSize(0);
        
        csvReader.close();
        
        
    }
    
    private void assertBean(final SampleLazyBean bean) {
        
        if(bean.getNo() == 1) {
            assertThat(bean.getName()).isEqualTo("山田太郎");
            assertThat(bean.getBirthday()).isEqualTo(LocalDate.of(2000, 10, 1));
            assertThat(bean.getComment()).isEqualTo("あいうえお");
            
        } else if(bean.getNo() == 2) {
            
            assertThat(bean.getName()).isEqualTo("鈴木次郎");
            assertThat(bean.getBirthday()).isEqualTo(LocalDate.of(2012, 1, 2));
            assertThat(bean.getComment()).isNull();
            
        }
        
    }
    
    private void assertBean(final SampleLazyPartialBean bean) {
        
        if(bean.getId() == 1) {
            assertThat(bean.getName()).isEqualTo("山田太郎");
            assertThat(bean.getExpiredDate()).isEqualTo(LocalDate.of(2017, 12, 31));
            assertThat(bean.getComment()).isEqualTo("コメント1");
            
        } else if(bean.getId() == 2) {
            
            assertThat(bean.getName()).isEqualTo("鈴木次郎");
            assertThat(bean.getExpiredDate()).isEqualTo(LocalDate.of(2017, 12, 31));
            assertThat(bean.getComment()).isNull();
            
        } else if(bean.getId() == 3) {
            
            assertThat(bean.getName()).isEqualTo("佐藤花子");
            assertThat(bean.getExpiredDate()).isEqualTo(LocalDate.of(2017, 12, 31));
            assertThat(bean.getComment()).isEqualTo("コメント3");
            
        }
        
    }
}

package com.github.mygreen.supercsv.io;

import static com.github.mygreen.supercsv.tool.TestUtils.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import java.io.StringWriter;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.supercsv.exception.SuperCsvException;

import com.github.mygreen.supercsv.exception.SuperCsvFixedSizeException;
import com.github.mygreen.supercsv.validation.CsvExceptionConverter;

/**
 * 固定長のカラムの書込み処理 {@link FixedSizeCsvAnnotationBeanWriter} のテスト
 * 
 * @since 2.5
 *
 */
public class FixedSizeCsvAnnotationBeanWriterTest {
    
    private CsvExceptionConverter exceptionConverter;
    
    @Before
    public void setUp() throws Exception {
        this.exceptionConverter = new CsvExceptionConverter();
    }
    
    @Test
    public void testWrite_normal() throws Exception {
        
        // テストデータを作成する
        final List<SampleFixedColumnBean> list = createFixedColumnData();
        
        StringWriter strWriter = new StringWriter();
        
        FixedSizeCsvAnnotationBeanWriter<SampleFixedColumnBean> csvWriter = FixedSizeCsvPreference.builder(SampleFixedColumnBean.class)
                .build()
                .csvWriter(strWriter);
        
        csvWriter.setExceptionConverter(exceptionConverter);
        
        final String[] expectedHeaders = new String[]{
                "   no",
                "ユーザ名　　　　　　",
                "誕生日____",
                "コメント            "
            };
        
        final String[] definitionHeaders = csvWriter.getDefinedHeader();
        assertThat(definitionHeaders).containsExactly(expectedHeaders);
        
        csvWriter.writeHeader();
        csvWriter.flush();
        
        for(SampleFixedColumnBean item : list) {
            csvWriter.write(item);
            csvWriter.flush();
        }
        
        String actual = strWriter.toString();
        System.out.println(actual);
        
        String expected = getTextFromFile("src/test/data/test_write_fixed_normal.csv", Charset.forName("UTF-8"));
        assertThat(actual).isEqualTo(expected);
        
        assertThat(csvWriter.getErrorMessages()).hasSize(0);
        
        csvWriter.close();
        
    }
    
    @Test
    public void testWriteAll_normal() throws Exception {
        
        // テストデータの作成
        final List<SampleFixedColumnBean> list = createFixedColumnData();
        
        StringWriter strWriter = new StringWriter();
        
        FixedSizeCsvAnnotationBeanWriter<SampleFixedColumnBean> csvWriter = FixedSizeCsvPreference.builder(SampleFixedColumnBean.class)
                .build()
                .csvWriter(strWriter);
        
        csvWriter.writeAll(list);
        csvWriter.flush();
        
        String actual = strWriter.toString();
        System.out.println(actual);
        
        String expected = getTextFromFile("src/test/data/test_write_fixed_normal.csv", Charset.forName("UTF-8"));
        assertThat(actual).isEqualTo(expected);
        
        assertThat(csvWriter.getErrorMessages()).hasSize(0);
        
        csvWriter.close();
        
    }
    
    /**
     * 固定長サイズオーバーの場合 - 例外をスローすること
     */
    @Test
    public void testWrite_fixedSizeOver() throws Exception {
        

        // テストデータを作成する
        final List<SampleFixedColumnBean> list = createFixedColumnData();
        list.get(1).setUserName("１２３４５６７８９０１");   // 20文字以上のオーバー
        
        StringWriter strWriter = new StringWriter();
        
        FixedSizeCsvAnnotationBeanWriter<SampleFixedColumnBean> csvWriter = FixedSizeCsvPreference.builder(SampleFixedColumnBean.class)
                .build()
                .csvWriter(strWriter);
        
        csvWriter.setExceptionConverter(exceptionConverter);
        
        csvWriter.writeHeader();
        csvWriter.flush();
        
        try {
            for(SampleFixedColumnBean item : list) {
                csvWriter.write(item);
                csvWriter.flush();
            }
            
            fail();
        
        } catch(SuperCsvException e) {
            assertThat(e).isInstanceOf(SuperCsvFixedSizeException.class);
//            e.printStackTrace();
        }
        
        List<String> messages = csvWriter.getErrorMessages();
        assertThat(messages).hasSize(1)
            .contains("[3行, 2列] : 固定長カラムの項目「ユーザ名」は、定義サイズ（20） に対して、実際のサイズ（22）が超過しています。");
        messages.forEach(System.out::println);
        
        csvWriter.close();
        
    }
    
    /**
     * カラムの改行コードを含む場合 - 例外をスローすること
     */
    @Test
    public void testWrite_containsLineBreak() throws Exception {
        

        // テストデータを作成する
        final List<SampleFixedColumnBean> list = createFixedColumnData();
        list.get(1).setComment("あいう\nえお");   // 改行コードを含む
        
        StringWriter strWriter = new StringWriter();
        
        FixedSizeCsvAnnotationBeanWriter<SampleFixedColumnBean> csvWriter = FixedSizeCsvPreference.builder(SampleFixedColumnBean.class)
                .build()
                .csvWriter(strWriter);
        
        csvWriter.setExceptionConverter(exceptionConverter);
        
        csvWriter.writeHeader();
        csvWriter.flush();
        
        try {
            for(SampleFixedColumnBean item : list) {
                csvWriter.write(item);
                csvWriter.flush();
            }
            
            fail();
        
        } catch(SuperCsvException e) {
            assertThat(e).isInstanceOf(SuperCsvFixedSizeException.class);
//            e.printStackTrace();
        }
        
        List<String> messages = csvWriter.getErrorMessages();
        assertThat(messages).hasSize(1)
            .contains("[3行, 4列] : 固定長カラムの項目「コメント」は、改行コードを含めることはできません。");
        messages.forEach(System.out::println);
        
        csvWriter.close();
        
    }
    
    /**
     * 固定長＋部分的なカラムの書込み
     */
    @Test
    public void testWrite_partialColumn_normal() throws Exception {
        
        // テストデータを作成する
        final List<SampleFixedColumnPartialBean> list = createFixedColumnPartialData();
        
        StringWriter strWriter = new StringWriter();
        
        FixedSizeCsvAnnotationBeanWriter<SampleFixedColumnPartialBean> csvWriter = FixedSizeCsvPreference.builder(SampleFixedColumnPartialBean.class)
                .build()
                .csvWriter(strWriter);
        
        csvWriter.setExceptionConverter(exceptionConverter);
        
        final String[] expectedHeaders = new String[]{
                "氏名           ",
                "           給料",
                "生年月日  ",
                "color",
                "備考欄              "
            };
        
        final String[] definitionHeaders = csvWriter.getDefinedHeader();
        assertThat(definitionHeaders).containsExactly(expectedHeaders);
        
        csvWriter.writeHeader();
        csvWriter.flush();
        
        for(SampleFixedColumnPartialBean item : list) {
            csvWriter.write(item);
            csvWriter.flush();
        }
        
        String actual = strWriter.toString();
        System.out.println(actual);
        
        String expected = getTextFromFile("src/test/data/test_write_fixed_partial_normal.csv", Charset.forName("UTF-8"));
        assertThat(actual).isEqualTo(expected);
        
        assertThat(csvWriter.getErrorMessages()).hasSize(0);
        
        csvWriter.close();
        
    }
    
    /**
     * 固定長のカラムの書き込み用のデータを作成する。
     */
    private List<SampleFixedColumnBean> createFixedColumnData() {
        
        final List<SampleFixedColumnBean> list = new ArrayList<>();
        
        {
            final SampleFixedColumnBean bean = new SampleFixedColumnBean();
            
            bean.setNo(1);
            bean.setUserName("山田　太郎");
            bean.setBirthDay(LocalDate.of(1980, 1, 28));
            bean.setComment("全ての項目に値が設定");
            
            list.add(bean);
        }
        
        {
            final SampleFixedColumnBean bean = new SampleFixedColumnBean();
            
            bean.setNo(2);
            bean.setUserName("田中　次郎");
            bean.setBirthDay(null); // 値がnullの場合
            bean.setComment("誕生日の項目が空。");
            
            list.add(bean);
        }
        
        {
            final SampleFixedColumnBean bean = new SampleFixedColumnBean();
            
            bean.setNo(3);
            bean.setUserName("鈴木　三郎");
            bean.setBirthDay(LocalDate.of(2000, 3, 25));
            bean.setComment("コメントを切落とす。あいう。");  // サイズオーバーの場合
            
            list.add(bean);
        }
        
        return list;
        
    }
    
    private List<SampleFixedColumnPartialBean> createFixedColumnPartialData() {
        
        final List<SampleFixedColumnPartialBean> list = new ArrayList<>();
        
        {
            final SampleFixedColumnPartialBean bean = new SampleFixedColumnPartialBean();
            
            bean.setUserName("山田　太郎");
            bean.setSalary(100000);
            bean.setColor(SampleEnum.RED);
            
            list.add(bean);
        }
        
        {
            final SampleFixedColumnPartialBean bean = new SampleFixedColumnPartialBean();
            
            bean.setUserName("田中　次郎");
            bean.setSalary(200000);
            bean.setColor(null);
            
            list.add(bean);
        }
        
        {
            final SampleFixedColumnPartialBean bean = new SampleFixedColumnPartialBean();
            
            bean.setUserName("鈴木　三郎");
            bean.setSalary(300000);
            bean.setColor(SampleEnum.YELLOW);
            
            list.add(bean);
        }
        
        return list;
    }
}

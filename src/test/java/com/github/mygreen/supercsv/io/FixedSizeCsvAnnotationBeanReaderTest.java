package com.github.mygreen.supercsv.io;

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

import com.github.mygreen.supercsv.exception.SuperCsvFixedSizeException;
import com.github.mygreen.supercsv.validation.CsvExceptionConverter;

/**
 * 固定長のカラムの読み込み処理 {@link FixedSizeCsvAnnotationBeanReader} のテスト
 *
 * @since 2.5
 *
 */
public class FixedSizeCsvAnnotationBeanReaderTest {

    private CsvExceptionConverter exceptionConverter;

    @Before
    public void setUp() throws Exception {
        this.exceptionConverter = new CsvExceptionConverter();
    }

    @Test
    public void testRead_normal() throws IOException {

        File file = new File("src/test/data/test_read_fixed_normal.csv");

        FixedSizeCsvAnnotationBeanReader<SampleFixedColumnBean> csvReader = FixedSizeCsvPreference.builder(SampleFixedColumnBean.class)
                .build()
                .csvReader(new InputStreamReader(new FileInputStream(file), Charset.forName("UTF-8")));

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
     * 列のサイズが不足している場合
     */
    @Test
    public void testRead_sizeInsufficient() throws IOException {
        
        File file = new File("src/test/data/test_read_fixed_sizeInsufficient.csv");
        
        FixedSizeCsvAnnotationBeanReader<SampleFixedColumnBean> csvReader = FixedSizeCsvPreference.builder(SampleFixedColumnBean.class)
                .build()
                .csvReader(new InputStreamReader(new FileInputStream(file), Charset.forName("UTF-8")));

        csvReader.setExceptionConverter(exceptionConverter);
        
        List<SampleFixedColumnBean> list = new ArrayList<>();
        
        // read header
        final String headers[] = csvReader.getHeader(true);
        
        try {
            SampleFixedColumnBean bean;
            while((bean = csvReader.read()) != null) {
                list.add(bean);
                
                assertBean(bean);
            }
            
            fail();
            
        } catch(SuperCsvException e) {
            assertThat(e).isInstanceOf(SuperCsvFixedSizeException.class);
//            e.printStackTrace();
            
        }
        
        List<String> messages = csvReader.getErrorMessages();
        assertThat(messages).hasSize(1)
            .contains("[4行, 4列] : 固定長カラムの項目「コメント」は、定義サイズ（20）に対して、実際のサイズ（18）が不足しています。");
        messages.forEach(System.out::println);
        
        csvReader.close();
        
    }
    
    /**
     * 固定長＋部分的なカラムの読み見込み
     */
    @Test
    public void testRead_partialColumn_normal() throws Exception {
        
        File file = new File("src/test/data/test_read_fixed_partial_normal.csv");

        FixedSizeCsvAnnotationBeanReader<SampleFixedColumnPartialBean> csvReader = FixedSizeCsvPreference.builder(SampleFixedColumnPartialBean.class)
                .build()
                .csvReader(new InputStreamReader(new FileInputStream(file), Charset.forName("UTF-8")));

        csvReader.setExceptionConverter(exceptionConverter);

        List<SampleFixedColumnPartialBean> list = new ArrayList<>();

        final String[] expectedHeaders = new String[]{
                "氏名           ",
                "           給料",
                "生年月日  ",
                "color",
                "備考欄              "
            };

        final String[] definitionHeaders = csvReader.getDefinedHeader();
        assertThat(definitionHeaders).containsExactly(expectedHeaders);

        // read header
        final String[] csvHeaders = csvReader.getHeader(true);
        assertThat(csvHeaders).containsExactly(expectedHeaders);

        SampleFixedColumnPartialBean bean;
        while((bean = csvReader.read()) != null) {
            list.add(bean);

            assertBean(bean);
        }

        assertThat(csvReader.getErrorMessages()).hasSize(0);

        csvReader.close();
        
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

            assertThat(bean.getComment()).isEqualTo("コメントを切落とす。");  // 切り落とされること。⇒TODO: エラーになるのでオプションにする？

        }

    }
    
    private void assertBean(final SampleFixedColumnPartialBean bean) {
    
    }


}

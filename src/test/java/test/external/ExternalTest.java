package test.external;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.junit.Assert.*;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import org.supercsv.exception.SuperCsvException;
import org.supercsv.prefs.CsvPreference;

import com.github.mygreen.supercsv.annotation.CsvBean;
import com.github.mygreen.supercsv.annotation.CsvColumn;
import com.github.mygreen.supercsv.exception.SuperCsvInvalidAnnotationException;
import com.github.mygreen.supercsv.io.CsvAnnotationBeanReader;


/**
 * 外部パッケージのテスト
 *
 * @author T.TSUCHIE
 *
 */
public class ExternalTest {
    
    @BeforeClass
    public static void setup() {
        // パッケージの許可設定はなくても使用可能。
//        System.setProperty("supercsv.annotation.jexlPermissions", "test.external.*");
    }
    
    @Test
    public void testReadNormal() throws Exception {
        
        File file = new File("src/test/data/test_read_external_normal.csv");
        
        CsvAnnotationBeanReader<ExternalCsv> csvReader = new CsvAnnotationBeanReader<>(ExternalCsv.class,
                Files.newBufferedReader(file.toPath(), Charset.forName("UTF-8")),
                CsvPreference.STANDARD_PREFERENCE);
        
        List<ExternalCsv> list = csvReader.readAll();
        assertThat(list).hasSize(2);
        assertThat(csvReader.getErrorMessages()).hasSize(0);

        csvReader.close();
        
    }

    @Test
    public void testReadValidationError_Enum() throws Exception {
        
        File file = new File("src/test/data/test_read_external_error_enum.csv");
        
        CsvAnnotationBeanReader<ExternalCsv> csvReader = new CsvAnnotationBeanReader<>(ExternalCsv.class,
                Files.newBufferedReader(file.toPath(), Charset.forName("UTF-8")),
                CsvPreference.STANDARD_PREFERENCE);
        
        try {
            csvReader.readAll();
            fail("not error");
        
        } catch(SuperCsvException e) {
            
            List<String> errors = csvReader.getErrorMessages();
            assertThat(errors).hasSize(1);
            assertThat(errors.get(0)).isEqualTo("[2行, 4列] : 項目「ロール」の値（aaa）は、何れかの値「Admin, Developer, Repoter」である必要があります。");
            
        } finally {
            csvReader.close();
        }
        
    }
    
    @Test
    public void testReadValidationError_CustomType() throws Exception {
        
        File file = new File("src/test/data/test_read_external_error_type.csv");
        
        CsvAnnotationBeanReader<ExternalCsv> csvReader = new CsvAnnotationBeanReader<>(ExternalCsv.class,
                Files.newBufferedReader(file.toPath(), Charset.forName("UTF-8")),
                CsvPreference.STANDARD_PREFERENCE);
        
        try {
            csvReader.readAll();
            fail("not error");
        
        } catch(SuperCsvException e) {
            
            List<String> errors = csvReader.getErrorMessages();
            assertThat(errors).hasSize(1);
            assertThat(errors.get(0)).isEqualTo("[2行, 5列] : 項目「郵便番号」の値（0011111）は、郵便番号の形式として不正です。");
            
        } finally {
            csvReader.close();
        }
        
    }
    
    @Test
    public void testReadAnnoError() throws Exception {

        File file = new File("src/test/data/test_read_external_normal.csv");
        
        SuperCsvInvalidAnnotationException exception = assertThrows(SuperCsvInvalidAnnotationException.class, () -> {
            CsvAnnotationBeanReader<ExternalWrongAnnoCsv> csvReader = new CsvAnnotationBeanReader<>(ExternalWrongAnnoCsv.class,
                    Files.newBufferedReader(file.toPath(), Charset.forName("UTF-8")),
                    CsvPreference.STANDARD_PREFERENCE);
            csvReader.close();
        });
        
        assertThat(exception.getMessage()).isEqualTo("'test.external.ExternalTest$ExternalWrongAnnoCsv' において、アノテーション @CsvColumn の属性 'number' の値（[1]）が重複しています。");
        
    }
    
    
    /**
     * アノテーションの定義間違い。
     * <p>number が重複
     *
     */
    @CsvBean(header = true)
    public static class ExternalWrongAnnoCsv {
        
        @CsvColumn(number = 1)
        public String id;
        
        @CsvColumn(number = 1)
        public String name;
        
    }
}

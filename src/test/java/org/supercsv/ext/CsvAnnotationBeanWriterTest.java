package org.supercsv.ext;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.exception.SuperCsvConstraintViolationException;
import org.supercsv.exception.SuperCsvException;
import org.supercsv.ext.builder.CsvAnnotationBeanParser;
import org.supercsv.ext.builder.CsvBeanMapping;
import org.supercsv.ext.io.CsvAnnotationBeanReader;
import org.supercsv.ext.io.ValidatableCsvBeanReader;
import org.supercsv.ext.localization.CsvExceptionConveter;
import org.supercsv.ext.localization.CsvMessage;
import org.supercsv.ext.localization.MessageConverter;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;


public class CsvAnnotationBeanWriterTest {
    
    @Before
    public void setUp() throws Exception {
    }
    
    @Test
    public void testNotAnnotation() throws IOException {
        
        List<SampleBean1> list = new ArrayList<SampleBean1>();
        
        SampleBean1 bean1 = new SampleBean1();
        bean1.setInteger1(1);
        bean1.setInteger2(11);
        bean1.setString1("string value 1");
        bean1.setString2("123456");
        list.add(bean1);
        
        SampleBean1 bean2 = new SampleBean1();
        bean2.setInteger1(2);
        bean2.setInteger2(12);
        bean2.setString1("string value 2");
        list.add(bean2);
        
        StringWriter strWriter = new StringWriter();
        ICsvBeanWriter csvWriter = null;
        try {
            csvWriter = new CsvBeanWriter(strWriter, CsvPreference.STANDARD_PREFERENCE);
            
            final String[] header = new String[]{"integer1", "integer2", "string1"};
            
            csvWriter.writeHeader(header);
            for(final SampleBean1 item : list) {
                csvWriter.write(item, header);
                csvWriter.flush();
            }
            
        } catch(Exception e) {
            e.printStackTrace();
            fail();
        } finally {
            csvWriter.close();
        }
        
        System.out.println(strWriter.toString());
        
    }
    
    @Test
    public void testAnnotationWriter() throws IOException {
        
        List<SampleBean1> list = new ArrayList<SampleBean1>();
        
        SampleBean1 bean1 = new SampleBean1();
        bean1.setInteger1(1);
        bean1.setInteger2(110000000);
        bean1.setString1("string value 1");
        bean1.setString2("123456");
        bean1.setDate1(Timestamp.valueOf("2000-01-01 01:00:00.000"));
        bean1.setDate2(Timestamp.valueOf("2000-01-02 02:00:00.000"));
        list.add(bean1);
        
        SampleBean1 bean2 = new SampleBean1();
        bean2.setInteger1(2);
        bean2.setInteger2(12);
        bean2.setString1("string value 2");
        bean2.setDate1(Timestamp.valueOf("2000-02-01 01:00:00.000"));
        bean2.setDate2(Timestamp.valueOf("2000-02-02 02:00:00.000"));
        bean2.setAvaialble(Boolean.TRUE);

        list.add(bean2);
        
        CsvAnnotationBeanParser helper = new CsvAnnotationBeanParser();
        CsvBeanMapping<SampleBean1> mappingBean = helper.parse(SampleBean1.class, false);
        
        String[] nameMapping = mappingBean.getNameMapping();
        CellProcessor[] cellProcessors = mappingBean.getOutputCellProcessor();
        
        StringWriter strWriter = new StringWriter();
        ICsvBeanWriter csvWriter = null;
        try {
            csvWriter = new CsvBeanWriter(strWriter, CsvPreference.STANDARD_PREFERENCE);

            
            csvWriter.writeHeader(mappingBean.getHeader());
            for(final SampleBean1 item : list) {
                csvWriter.write(item, nameMapping, cellProcessors);
                csvWriter.flush();
            }
            
        } catch(SuperCsvConstraintViolationException e) {
            e.printStackTrace();
            CsvExceptionConveter exceptionConveter = new CsvExceptionConveter();
            MessageConverter messageConverter = new MessageConverter();
            List<CsvMessage> csvErrors= exceptionConveter.convertCsvError(e);
            List<String> messages = messageConverter.convertMessage(csvErrors);
            for(String str : messages) {
                System.err.println(str);
            }
            
        } catch(Exception e) {
            e.printStackTrace();
            fail();
        } finally {
            csvWriter.close();
        }
        
        System.out.println(strWriter.toString());
        
    }
    
    @Test
    public void testRead() throws Exception {
        
//        File inputFile = new File("src/test/data/test_error3.csv");
//        File inputFile = new File("src/test/data/test_error2.csv");
        File inputFile = new File("src/test/data/test_error.csv");
//        File inputFile = new File("src/test/data/test.csv");
        ICsvBeanReader csvReader = new ValidatableCsvBeanReader(
                new InputStreamReader(new FileInputStream(inputFile), "Windows-31j"),
                CsvPreference.STANDARD_PREFERENCE);
        
        CsvAnnotationBeanParser helper = new CsvAnnotationBeanParser();
        CsvBeanMapping<SampleBean1> mappingBean = helper.parse(SampleBean1.class);
        
        String[] nameMapping = mappingBean.getNameMapping();
        CellProcessor[] processors = mappingBean.getInputCellProcessor();
//        CellProcessor[] processors = new CellProcessor[] {
//                new NotNull(new ParseInt()),
//                new Optional(new ParseInt()),
//                new Optional(),
//                new Optional()
//        };
        
        List<SampleBean1> list = new ArrayList<SampleBean1>();
        SampleBean1 bean1;
        try {
            String[] headers = csvReader.getHeader(true);
            while((bean1 = csvReader.read(SampleBean1.class, nameMapping, processors)) != null) {
                System.out.println(bean1);
                list.add(bean1);
            }
        
        } catch(SuperCsvException e) {
            e.printStackTrace();
            CsvExceptionConveter exceptionConveter = new CsvExceptionConveter();
            MessageConverter messageConverter = new MessageConverter();
            List<CsvMessage> csvErrors= exceptionConveter.convertCsvError(e);
            List<String> messages = messageConverter.convertMessage(csvErrors);
            for(String str : messages) {
                System.err.println(str);
            }
        }
        csvReader.close();
        
    }
    
    @Test
    public void testRead2() throws Exception {
        
//        File inputFile = new File("src/test/data/test_error2.csv");
        File inputFile = new File("src/test/data/test.csv");
        CsvAnnotationBeanReader<SampleBean1> csvReader = new CsvAnnotationBeanReader<SampleBean1>(
                SampleBean1.class,
                new InputStreamReader(new FileInputStream(inputFile), "Windows-31j"),
                CsvPreference.STANDARD_PREFERENCE);
        
        List<SampleBean1> list = new ArrayList<SampleBean1>();
        SampleBean1 bean1;
        try {
            String[] headers = csvReader.getHeader();
            while((bean1 = csvReader.read()) != null) {
                System.out.println(bean1);
                list.add(bean1);
            }
        
        } catch(SuperCsvException e) {
            e.printStackTrace();
            CsvExceptionConveter exceptionConveter = new CsvExceptionConveter();
            MessageConverter messageConverter = new MessageConverter();
            List<CsvMessage> csvErrors= exceptionConveter.convertCsvError(e);
            List<String> messages = messageConverter.convertMessage(csvErrors);
            for(String str : messages) {
                System.err.println(str);
            }
        }
        csvReader.close();
        
    }
    
    @Test
    public void testRead3() throws Exception {
        
//        File inputFile = new File("src/test/data/test_error2.csv");
        File inputFile = new File("src/test/data/test.csv");
        CsvAnnotationBeanReader<SampleBean1> csvReader = new CsvAnnotationBeanReader<SampleBean1>(
                SampleBean1.class,
                new InputStreamReader(new FileInputStream(inputFile), "Windows-31j"),
                CsvPreference.STANDARD_PREFERENCE);
        
        List<SampleBean1> list = new ArrayList<SampleBean1>();
        SampleBean1 bean1;
        String[] headers = csvReader.getHeader();
        while(true) {
            try {
                bean1 = csvReader.read();
                if(bean1 == null) {
                    break;
                }
                if(csvReader.hasNotError()) {
                    // エラーがなければ読み込む
                    list.add(bean1);
                }
            } catch(SuperCsvException e) { }
        }
        csvReader.close();
        
        if(csvReader.hasError()) {
            // エラーを取得して、メッセージに変換する
            MessageConverter messageConverter = new MessageConverter();
            List<String> messages = messageConverter.convertMessage(csvReader.getCsvErrors());
            for(String str : messages) {
                System.err.println(str);
            }
            
        }

        
    }
    
    @Test
    public void testEnum() {
        
        System.out.println((Color.RED instanceof Enum));
        System.out.println((Color.class.isAssignableFrom(Enum.class)));
        
        Class<?> type = Color.class;
        System.out.println(type);
    }
}

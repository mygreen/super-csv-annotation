package org.supercsv.ext.builder;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.supercsv.ext.annotation.CsvBean;
import org.supercsv.ext.annotation.CsvColumn;
import org.supercsv.ext.builder.CsvAnnotationBeanParser;
import org.supercsv.ext.builder.CsvBeanMapping;
import org.supercsv.ext.exception.SuperCsvInvalidAnnotationException;


public class CsvAnnotationBeanParserTest {
    
    @Before
    public void setUp() throws Exception {
    }
    
    @Test
    public void test_success_allModifires() {
        
        try {
            CsvAnnotationBeanParser parser = new CsvAnnotationBeanParser();
            CsvBeanMapping<AllModifiresBean> mapping = parser.parse(AllModifiresBean.class);
            
            assertNotNull(mapping);
        } catch(Exception e) {
            e.printStackTrace();
            fail(); 
        }
    }
    
    @Test
    public void test_fail_duplicate_position() {
        
        try {
            CsvAnnotationBeanParser parser = new CsvAnnotationBeanParser();
            CsvBeanMapping<DuplicatePositionBean> mapping = parser.parse(DuplicatePositionBean.class);
            
            fail(); 
        } catch(Exception e) {
            assertEquals(SuperCsvInvalidAnnotationException.class, e.getClass());
//            e.printStackTrace();
//            System.out.println(e.getMessage());
            
            
        }
    }
    
    @Test
    public void test_fail_lack_position() {
        
        try {
            CsvAnnotationBeanParser parser = new CsvAnnotationBeanParser();
            CsvBeanMapping<LackPositionBean1> mapping = parser.parse(LackPositionBean1.class);
            
            fail(); 
        } catch(Exception e) {
            assertEquals(SuperCsvInvalidAnnotationException.class, e.getClass());
//            e.printStackTrace();
//            System.out.println(e.getMessage());
            
            
        }
    }
    
    @Test
    public void test_fail_wrong_position() {
        
        try {
            CsvAnnotationBeanParser parser = new CsvAnnotationBeanParser();
            CsvBeanMapping<WrongPositionBean1> mapping = parser.parse(WrongPositionBean1.class);
            
            fail(); 
        } catch(Exception e) {
            assertEquals(SuperCsvInvalidAnnotationException.class, e.getClass());
//            e.printStackTrace();
//            System.out.println(e.getMessage());
            
            
        }
    }
    
    @CsvBean
    class AllModifiresBean {
        
        @CsvColumn(position=0)
        public String fpublic;
        
        @CsvColumn(position=1)
        protected String fprotected;
        
        @CsvColumn(position=2)
        String fdefault;
        
        @CsvColumn(position=3)
        private String fprivate;
        
        public String getFpublic() {
            return fpublic;
        }
        
        public void setFpublic(String fpublic) {
            this.fpublic = fpublic;
        }
        
        public String getFprotected() {
            return fprotected;
        }
        
        public void setFprotected(String fprotected) {
            this.fprotected = fprotected;
        }
        
        public String getFdefault() {
            return fdefault;
        }
        
        public void setFdefault(String fdefault) {
            this.fdefault = fdefault;
        }
        
        public String getFprivate() {
            return fprivate;
        }
        
        public void setFprivate(String fprivate) {
            this.fprivate = fprivate;
        }
    }
    
    @CsvBean
    class DuplicatePositionBean {
        
        @CsvColumn(position=0)
        private String field1;
        
        @CsvColumn(position=1)
        private String field2;
        
        @CsvColumn(position=0)
        private String field3;
        
        public String getField1() {
            return field1;
        }
        
        public void setField1(String field1) {
            this.field1 = field1;
        }
        
        public String getField2() {
            return field2;
        }
        
        public void setField2(String field2) {
            this.field2 = field2;
        }
        
        public String getField3() {
            return field3;
        }
        
        public void setField3(String field3) {
            this.field3 = field3;
        }
        
    }
    
    @CsvBean
    class LackPositionBean1 {
        
        @CsvColumn(position=1)
        private String field1;
        
        @CsvColumn(position=3)
        private String field2;
        
        public String getField1() {
            return field1;
        }
        
        public void setField1(String field1) {
            this.field1 = field1;
        }
        
        public String getField2() {
            return field2;
        }
        
        public void setField2(String field2) {
            this.field2 = field2;
        }
        
    }
    
    @CsvBean
    class WrongPositionBean1 {
        
        @CsvColumn(position=3)
        private String field1;
        
        @CsvColumn(position=0)
        private String field2;
        
        @CsvColumn(position=2)
        private String field3;
        
        public String getField1() {
            return field1;
        }
        
        public void setField1(String field1) {
            this.field1 = field1;
        }
        
        public String getField2() {
            return field2;
        }
        
        public void setField2(String field2) {
            this.field2 = field2;
        }
        
        public String getField3() {
            return field3;
        }
        
        public void setField3(String field3) {
            this.field3 = field3;
        }
        
    }
}

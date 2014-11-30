package org.supercsv.ext.spring;

import static org.junit.Assert.*;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.ext.builder.CsvAnnotationBeanParser;
import org.supercsv.ext.builder.CsvBeanMapping;
import org.supercsv.ext.builder.SpringCellProcessorBuilderFacatory;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath:/ApplicationContext.xml")
public class SpringTest {
    
    @Resource
    private ApplicationContext applicationContext;
    
    @Resource
    SpringCellProcessorBuilderFacatory builderFactory;
    
    @Before
    public void setUp() throws Exception {
    }
    
    @After
    public void tearDown() throws Exception {
    }
    
    @Test
    public void test() {
        CsvAnnotationBeanParser beanParser = new CsvAnnotationBeanParser();
        beanParser.setBuilderFactory(builderFactory);
        
        try {
            CsvBeanMapping<UserCsv> beanMapping = beanParser.parse(UserCsv.class);
            
            CellProcessor[] processor = beanMapping.getInputCellProcessor();
            
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}

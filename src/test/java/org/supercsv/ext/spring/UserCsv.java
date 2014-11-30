package org.supercsv.ext.spring;

import java.net.URL;

import org.supercsv.ext.annotation.CsvBean;
import org.supercsv.ext.annotation.CsvColumn;
import org.supercsv.ext.annotation.CsvStringConverter;


@CsvBean(header=true)
public class UserCsv {
    
    @CsvColumn(position=0, label="ユーザID", builderClass=UserIdCellProcessorBuilder.class)
    private String id;
    
    @CsvColumn(position=1, label="ユーザ名")
    @CsvStringConverter(maxLength=10)
    private String name;
    
    @CsvColumn(position=2, label="ユーザ名")
    private URL url;
    
}

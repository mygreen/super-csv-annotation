package com.github.mygreen.supercsv.cellprocessor.format;

import static org.junit.Assert.*;
import static org.assertj.core.api.Assertions.*;
import static com.github.mygreen.supercsv.tool.TestUtils.*;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Before;
import org.junit.Test;

import com.github.mygreen.supercsv.cellprocessor.format.DateFormatWrapper;
import com.github.mygreen.supercsv.cellprocessor.format.SimpleDateFormatBuilder;
import com.github.mygreen.supercsv.cellprocessor.format.TextParseException;

/**
 * Tests the {@link DateFormatWrapper}.
 *
 * @since 1.2
 * @author T.TSUCHIE
 *
 */
public class DateFormatWrapperTest {
    
    private DateFormatWrapper<Date> formatter;
    
    private DateFormatWrapper<Date> utilDateFormatter;
    private DateFormatWrapper<java.sql.Date> sqlDateFormatter;
    private DateFormatWrapper<Timestamp> timestampFormatter;
    private DateFormatWrapper<Time> timeFormatter;
    
    private Date normalDateValue = toDate(2016, 2, 29, 9, 13, 1);
    private String normalDateStr = "2016/02/29 09:13:01";
    
    private Date utilDateValue = toDate(2016, 2, 29, 9, 13, 1);
    private String utilDateStr = "2016-02-29 09:13:01";
    
    private Date sqlDateValue = toSqlDate(2016, 2, 29);
    private String sqlDateStr = "2016-02-29";
    
    private Date timeValue = toTime(9, 13, 1);
    private String timeStr = "09:13:01";
    
    private Date timestampValue = toTimestamp(2016, 2, 29, 9, 13, 1, 123);
    private String timestampStr = "2016-02-29 09:13:01.123";
    
    /**
     * Sets up the formatter for the test using Combinations
     */
    @Before
    public void setUp() {
        this.formatter = new DateFormatWrapper<>(SimpleDateFormatBuilder.create("yyyy/MM/dd HH:mm:ss")
                .lenient(true)
                .build(), Date.class);
        
        this.utilDateFormatter = new DateFormatWrapper<>(Date.class);
        this.sqlDateFormatter = new DateFormatWrapper<>(java.sql.Date.class);
        this.timestampFormatter = new DateFormatWrapper<>(Timestamp.class);
        this.timeFormatter = new DateFormatWrapper<>(Time.class);
    }
    
    @Test(expected=NullPointerException.class)
    public void testConstructorAtFormatterNull() {
        new DateFormatWrapper<>((DateFormat) null, Date.class);
        fail();
    }
    
    @Test(expected=NullPointerException.class)
    public void testConstructorAtTypeNull() {
        new DateFormatWrapper<>((Class<Date>) null);
        fail();
    }
    
    @Test
    public void testGetPattern() {
        assertThat(formatter.getPattern().get()).isEqualTo("yyyy/MM/dd HH:mm:ss");
        
        assertThat(utilDateFormatter.getPattern().get()).isEqualTo("yyyy-MM-dd HH:mm:ss");
        assertThat(sqlDateFormatter.getPattern().get()).isEqualTo("yyyy-MM-dd");
        assertThat(timestampFormatter.getPattern().get()).isEqualTo("yyyy-MM-dd HH:mm:ss.SSS");
        assertThat(timeFormatter.getPattern().get()).isEqualTo("HH:mm:ss");
    }
    
    @Test
    public void testPrintWithValid() {
        assertThat(formatter.print(normalDateValue)).isEqualTo(normalDateStr);
        
        assertThat(utilDateFormatter.print(utilDateValue)).isEqualTo(utilDateStr);
        assertThat(sqlDateFormatter.print(sqlDateValue)).isEqualTo(sqlDateStr);
        assertThat(timeFormatter.print(timeValue)).isEqualTo(timeStr);
        assertThat(timestampFormatter.print(timestampValue)).isEqualTo(timestampStr);
    }
    
    @Test(expected=NullPointerException.class)
    public void testPrintWithInvalidAtNull() {
        formatter.print(null);
        fail();
    }
    
    @Test(expected=TextParseException.class)
    public void testParse_empty() {
        formatter.parse("");
    }
    
    @Test
    public void testParseWithValid() throws Exception {
        
        assertThat(formatter.parse(normalDateStr)).isEqualTo(normalDateValue);
        
        assertThat(utilDateFormatter.parse(utilDateStr)).isEqualTo(utilDateValue);
        assertThat(sqlDateFormatter.parse(sqlDateStr)).isEqualTo(sqlDateValue);
        assertThat(timeFormatter.parse(timeStr)).isEqualTo(timeValue);
        assertThat(timestampFormatter.parse(timestampStr)).isEqualTo(timestampValue);
        
    }
    
    @Test
    public void testParseAndPrintWithMultiThread() {
        
        Date baseDate = toDate(2000, 1, 1);
        
        List<Date> list = IntStream.range(0, 99)
                .mapToObj(i -> plusDays(baseDate, i))
                .collect(Collectors.toList());
        
        // with parallelStream
        list.parallelStream().forEach(d -> {
            try {
                String str = formatter.print(d);
                Date date = formatter.parse(str);
                assertThat(date).isEqualTo(d);
                
            } catch (Exception e) {
                e.printStackTrace();
                fail();
            }
        });
        
        // with executor
        ExecutorService executor = Executors.newFixedThreadPool(5);
        try {
            final CountDownLatch countDown = new CountDownLatch(list.size());
            list.stream().forEach(d -> {
                
                executor.submit(() -> {
                    try {
                        String str = formatter.print(d);
                        Date date = formatter.parse(str);
                        assertThat(date).isEqualTo(d);
                        
                    } catch (Exception e) {
                        fail();
                    } finally {
                        countDown.countDown();
                    }
                });
                
            });
            
            countDown.await();
            
        } catch (Exception e) {
            e.printStackTrace();
            fail();
            
        } finally {
            executor.shutdown();
        }
        
    }
    
    @Test(expected=TextParseException.class)
    public void testParseWithInvalidAtWrong() throws Exception {
        formatter.parse("abc");
        fail();
    }
}

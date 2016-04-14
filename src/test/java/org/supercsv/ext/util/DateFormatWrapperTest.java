package org.supercsv.ext.util;

import static org.junit.Assert.*;
import static org.supercsv.ext.tool.TestUtils.*;
import static org.hamcrest.Matchers.*;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests the {@link DateFormatWrapper}.
 *
 * @since 1.2
 * @author T.TSUCHIE
 *
 */
public class DateFormatWrapperTest {
    
    private DateFormatWrapper formatter;
    
    private DateFormatWrapper utilDateFormatter;
    private DateFormatWrapper sqlDateFormatter;
    private DateFormatWrapper timestampFormatter;
    private DateFormatWrapper timeFormatter;
    
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
        this.formatter = new DateFormatWrapper(SimpleDateFormatBuilder.create("yyyy/MM/dd HH:mm:ss")
                .lenient(true)
                .build());
        
        this.utilDateFormatter = new DateFormatWrapper(java.util.Date.class);
        this.sqlDateFormatter = new DateFormatWrapper(java.sql.Date.class);
        this.timestampFormatter = new DateFormatWrapper(Timestamp.class);
        this.timeFormatter = new DateFormatWrapper(Time.class);
    }
    
    @Test(expected=NullPointerException.class)
    public void testConstructorAtFormatterNull() {
        new DateFormatWrapper((DateFormat) null);
        fail();
    }
    
    @Test(expected=NullPointerException.class)
    public void testConstructorAtTypeNull() {
        new DateFormatWrapper((Class<Date>) null);
        fail();
    }
    
    @Test
    public void testGetPattern() {
        assertThat(formatter.getPattern(), is("yyyy/MM/dd HH:mm:ss"));
        
        assertThat(utilDateFormatter.getPattern(), is("yyyy-MM-dd HH:mm:ss"));
        assertThat(sqlDateFormatter.getPattern(), is("yyyy-MM-dd"));
        assertThat(timestampFormatter.getPattern(), is("yyyy-MM-dd HH:mm:ss.SSS"));
        assertThat(timeFormatter.getPattern(), is("HH:mm:ss"));
    }
    
    @Test
    public void testFormatWithValid() {
        assertThat(formatter.format(normalDateValue), is(normalDateStr));
        
        assertThat(utilDateFormatter.format(utilDateValue), is(utilDateStr));
        assertThat(sqlDateFormatter.format(sqlDateValue), is(sqlDateStr));
        assertThat(timeFormatter.format(timeValue), is(timeStr));
        assertThat(timestampFormatter.format(timestampValue), is(timestampStr));
    }
    
    @Test(expected=NullPointerException.class)
    public void testFormatWithInvalidAtNull() {
        formatter.format(null);
        fail();
    }
    
    @Test
    public void testParseWithValid() throws Exception {
        
        assertThat(formatter.parse(normalDateStr), is(normalDateValue));
        
        assertThat(utilDateFormatter.parse(utilDateStr), is(utilDateValue));
        assertThat(sqlDateFormatter.parse(sqlDateStr), is(sqlDateValue));
        assertThat(timeFormatter.parse(timeStr), is(timeValue));
        assertThat(timestampFormatter.parse(timestampStr), is(timestampValue));
        
    }
    
    @Test
    public void testParseAndFormatWithMultiThread() {
        
        Date baseDate = toDate(2000, 1, 1);
        
        List<Date> list = IntStream.range(0, 99)
                .mapToObj(i -> plusDays(baseDate, i))
                .collect(Collectors.toList());
        
        // with parallelStream
        list.parallelStream().forEach(d -> {
            try {
                String str = formatter.format(d);
                Date date = formatter.parse(str);
                assertThat(date, is(d));
                
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
                        String str = formatter.format(d);
                        Date date = formatter.parse(str);
                        assertThat(date, is(d));
                        
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
    
    @Test(expected=ParseException.class)
    public void testParseWithInvalidAtWrong() throws Exception {
        formatter.parse("abc");
        fail();
    }
}

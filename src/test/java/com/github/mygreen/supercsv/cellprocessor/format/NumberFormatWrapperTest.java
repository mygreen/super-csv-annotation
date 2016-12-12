package com.github.mygreen.supercsv.cellprocessor.format;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Before;
import org.junit.Test;

import com.github.mygreen.supercsv.cellprocessor.format.NumberFormatWrapper;
import com.github.mygreen.supercsv.cellprocessor.format.TextParseException;

/**
 * Tests the {@link NumberFormatWrapper}.
 *
 * @since 1.2
 * @author T.TSUCHIE
 *
 */
public class NumberFormatWrapperTest {
    
    private NumberFormatWrapper<Integer> formatter;
    private NumberFormatWrapper<Integer> lenientFormatter;
    private NumberFormatWrapper<Integer> parseBigDecimalFormatter;
    private NumberFormatWrapper<Integer> lenientParseBigDecimalFormatter;
    
    /**
     * Sets up the formatter for the test using Combinations
     */
    @Before
    public void setUp() {
        DecimalFormat df1 = new DecimalFormat("#,##0.0##");
        this.formatter = new NumberFormatWrapper<>(df1, Integer.class);
        this.lenientFormatter = new NumberFormatWrapper<>(df1, Integer.class, true);
        
        DecimalFormat df2 = new DecimalFormat("#,##0.0##");
        df2.setParseBigDecimal(true);
        this.parseBigDecimalFormatter = new NumberFormatWrapper<>(df2, Integer.class);
        this.lenientParseBigDecimalFormatter = new NumberFormatWrapper<>(df2, Integer.class, true);
    }
    
    @Test(expected=NullPointerException.class)
    public void testConstructorAtFormatterNull() {
        new NumberFormatWrapper<>((NumberFormat) null, Integer.class);
        fail();
    }
    
    @Test
    public void testGetPattern() {
        assertThat(formatter.getPattern().get(), is("#,##0.0##"));
        assertThat(lenientFormatter.getPattern().get(), is("#,##0.0##"));
        
    }
    
    @Test
    public void testPrintWithValid() {
        
        assertThat(formatter.print(123456), is("123,456.0"));
        assertThat(formatter.print(-2.4567), is("-2.457"));
        
        assertThat(lenientFormatter.print(123456), is("123,456.0"));
        assertThat(lenientFormatter.print(-2.4567), is("-2.457"));
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testPrintWithInvalidAtNull() {
        formatter.print((Integer)null);
        fail();
    }
    
    @Test
    public void testParseWithValid() throws Exception {
        
        assertThat(formatter.parse(int.class, "123,456.0"), is(123456));
        assertThat(formatter.parse(double.class, "-2.456"), is(-2.456));
        
        assertThat(lenientFormatter.parse(int.class, "123,456.0"), is(123456));
        assertThat(lenientFormatter.parse(double.class, "-2.456"), is(-2.456));
        
        assertThat(lenientFormatter.parse(int.class, "123,456.0abc"), is(123456));
        assertThat(lenientFormatter.parse(double.class, "-2.456abc"), is(-2.456));
        
    }
    
    @Test(expected=NullPointerException.class)
    public void testParseWithInvalidAtNull() throws Exception {
        formatter.parse(int.class, null);
        fail();
    }
    
    @Test(expected=TextParseException.class)
    public void testParseWithInvalidAtWrong() throws Exception {
        assertThat(formatter.isLenient(), is(false));
        
        formatter.parse(int.class, "123,456.0abc");
        fail();
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testParseWithInvalidAtNotSupportType() throws Exception {
        formatter.parse(AtomicInteger.class, "123");
        fail();
    }
    
    @Test
    public void testParseWithValidAtType() throws Exception {
        
        assertThat(formatter.parse(Byte.class, "123.456"), is(new Byte((byte)123)));
        assertThat(formatter.parse(byte.class, "123.456"), is((byte)123));
        
        assertThat(formatter.parse(Short.class, "123.456"), is(new Short((short)123)));
        assertThat(formatter.parse(short.class, "123.456"), is((short)123));
        
        assertThat(formatter.parse(Integer.class, "123.456"), is(new Integer(123)));
        assertThat(formatter.parse(int.class, "123.456"), is(123));
        
        assertThat(formatter.parse(Long.class, "123.456"), is(new Long(123L)));
        assertThat(formatter.parse(long.class, "123.456"), is(123L));
        
        assertThat(formatter.parse(Float.class, "123.456"), is(new Float(123.456f)));
        assertThat(formatter.parse(float.class, "123.456"), is(123.456f));
        
        assertThat(formatter.parse(Double.class, "123.456"), is(new Double(123.456)));
        assertThat(formatter.parse(double.class, "123.456"), is(123.456));
        
        assertThat(formatter.parse(BigInteger.class, "123"), is(new BigInteger("123")));
        assertThat(formatter.parse(BigDecimal.class, "123.456"), is(new BigDecimal("123.456")));
        
    }
    
    @Test
    public void testParseWithValidAtTypeBigDecimal() throws Exception {
        
        assertThat(parseBigDecimalFormatter.parse(Byte.class, "123"), is(new Byte((byte)123)));
        assertThat(parseBigDecimalFormatter.parse(byte.class, "123"), is((byte)123));
        
        assertThat(parseBigDecimalFormatter.parse(Short.class, "123"), is(new Short((short)123)));
        assertThat(parseBigDecimalFormatter.parse(short.class, "123"), is((short)123));
        
        assertThat(parseBigDecimalFormatter.parse(Integer.class, "123"), is(new Integer(123)));
        assertThat(parseBigDecimalFormatter.parse(int.class, "123"), is(123));
        
        assertThat(parseBigDecimalFormatter.parse(Long.class, "123"), is(new Long(123L)));
        assertThat(parseBigDecimalFormatter.parse(long.class, "123"), is(123L));
        
        assertThat(parseBigDecimalFormatter.parse(Float.class, "123.456"), is(new Float(123.456f)));
        assertThat(parseBigDecimalFormatter.parse(float.class, "123.456"), is(123.456f));
        
        assertThat(parseBigDecimalFormatter.parse(Double.class, "123.456"), is(new Double(123.456)));
        assertThat(parseBigDecimalFormatter.parse(double.class, "123.456"), is(123.456));
        
        assertThat(parseBigDecimalFormatter.parse(BigInteger.class, "123"), is(new BigInteger("123")));
        assertThat(parseBigDecimalFormatter.parse(BigDecimal.class, "123.456"), is(new BigDecimal("123.456")));
        
    }
    
    @Test(expected=TextParseException.class)
    public void testParseWithInvalidAtBigDecimalWrong() throws Exception {
        parseBigDecimalFormatter.parse(int.class, "123,456.789");
        fail();
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testParseWithInvalidAtBigDecimalNotSupportType() throws Exception {
        parseBigDecimalFormatter.parse(AtomicInteger.class, "123");
        fail();
    }
    
    @Test
    public void testParseWithValidAtTypeLenientBigDecimal() throws Exception {
        
        assertThat(lenientParseBigDecimalFormatter.isLenient(), is(true));
        
        assertThat(lenientParseBigDecimalFormatter.parse(Byte.class, "123.456"), is(new Byte((byte)123)));
        assertThat(lenientParseBigDecimalFormatter.parse(byte.class, "123.456"), is((byte)123));
        
        assertThat(lenientParseBigDecimalFormatter.parse(Short.class, "123.456"), is(new Short((short)123)));
        assertThat(lenientParseBigDecimalFormatter.parse(short.class, "123.456"), is((short)123));
        
        assertThat(lenientParseBigDecimalFormatter.parse(Integer.class, "123.456"), is(new Integer(123)));
        assertThat(lenientParseBigDecimalFormatter.parse(int.class, "123.456"), is(123));
        
        assertThat(lenientParseBigDecimalFormatter.parse(Long.class, "123.456"), is(new Long(123L)));
        assertThat(lenientParseBigDecimalFormatter.parse(long.class, "123.456"), is(123L));
        
        assertThat(lenientParseBigDecimalFormatter.parse(Float.class, "123.456"), is(new Float(123.456f)));
        assertThat(lenientParseBigDecimalFormatter.parse(float.class, "123.456"), is(123.456f));
        
        assertThat(lenientParseBigDecimalFormatter.parse(Double.class, "123.456"), is(new Double(123.456)));
        assertThat(lenientParseBigDecimalFormatter.parse(double.class, "123.456"), is(123.456));
        
        assertThat(lenientParseBigDecimalFormatter.parse(BigInteger.class, "123"), is(new BigInteger("123")));
        assertThat(lenientParseBigDecimalFormatter.parse(BigDecimal.class, "123.456"), is(new BigDecimal("123.456")));
        
    }
    
    @Test
    public void testParseAndFormatWithMultiThread() {
        
        final int baseNumber = 1000;
        List<Integer> list = IntStream.range(0, 99)
                .mapToObj(i -> baseNumber + i)
                .collect(Collectors.toList());
        
        // with parallelStream
        list.parallelStream().forEach(i -> {
            try {
                
                String str = formatter.print(i);
                int num = formatter.parse(int.class, str);
                assertThat(num, is(i));
                
            } catch (Exception e) {
                e.printStackTrace();
                fail();
            }
        });
        
        // with executor
        ExecutorService executor = Executors.newFixedThreadPool(5);
        try {
            final CountDownLatch countDown = new CountDownLatch(list.size());
            list.stream().forEach(i -> {
                
                executor.submit(() -> {
                    try {
                        String str = formatter.print(i);
                        int num = formatter.parse(int.class, str);
                        assertThat(num, is(i));
                        
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
}

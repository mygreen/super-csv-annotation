package com.github.mygreen.supercsv.cellprocessor.format;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;

import static org.assertj.core.api.Assertions.*;
import static com.github.mygreen.supercsv.tool.TestUtils.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

/**
 * {@link SimpleNumberFormatter}のテスタ
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
@RunWith(Enclosed.class)
public class SimpleNumberFormatterTest {
    
    public static class CommonTest {
        
        private SimpleNumberFormatter<Integer> formatter;
        private SimpleNumberFormatter<Integer> formatterLenient;
        private SimpleNumberFormatter<Integer> formatterPrecision;
        
        @Before
        public void setUp() throws Exception {
            this.formatter = new SimpleNumberFormatter<>(Integer.class, false);
            this.formatterLenient = new SimpleNumberFormatter<>(Integer.class, true);
            this.formatterPrecision = new SimpleNumberFormatter<>(Integer.class, true, new MathContext(4, RoundingMode.DOWN));
            
        }
        
        @Test(expected=NullPointerException.class)
        public void testConstructor_typeNull() {
            
            new SimpleNumberFormatter<>(null, false);
            
        }
        
        @Test
        public void testValidationMessage() {
            
            assertThat(formatter.getValidationMessage()).isEmpty();
            
            // set message
            formatter.setValidationMessage("テストメッセージ");
            assertThat(formatter.getValidationMessage().get()).isEqualTo("テストメッセージ");
            
        }
        
        @Test
        public void testMessageVariables() {
            
            assertThat(formatter.getMessageVariables()).isEmpty();
            
            assertThat(formatterLenient.getMessageVariables()).isEmpty();
            
            assertThat(formatterPrecision.getMessageVariables()).containsEntry("precision", 4);
        }
        
        @Test
        public void testIsLeneint() {
            
            assertThat(formatter.isLenient()).isEqualTo(false);
            assertThat(formatterLenient.isLenient()).isEqualTo(true);
            assertThat(formatterPrecision.isLenient()).isEqualTo(true);
            
        }
        
        @Test
        public void testGetMathContext() {
            
            assertThat(formatter.getMathContext()).isNull();
            assertThat(formatterLenient.getMathContext()).isNull();
            assertThat(formatterPrecision.getMathContext()).isEqualTo(new MathContext(4, RoundingMode.DOWN));
            
        }
        
    }
    
    public static class WrapperIntegerTest {
        
        private SimpleNumberFormatter<Integer> formatter;
        private SimpleNumberFormatter<Integer> formatterLenient;
        private SimpleNumberFormatter<Integer> formatterPrecision;
        
        @Before
        public void setUp() throws Exception {
            this.formatter = new SimpleNumberFormatter<>(Integer.class, false);
            this.formatterLenient = new SimpleNumberFormatter<>(Integer.class, true);
            this.formatterPrecision = new SimpleNumberFormatter<>(Integer.class, true, new MathContext(4, RoundingMode.DOWN));
            
        }
        
        private static final Integer TEST_VALUE_OBJ = toInteger("12345");
        private static final String TEST_VALUE_STR = "12345";
        private static final String TEST_VALUE_STR_LENIENT = "12345.678";
        
        private static final Integer TEST_VALUE_OBJ_PRECISION = toInteger("12340");
        private static final String TEST_VALUE_STR_PRECISION = "12340";
        
        @Test
        public void testParse() {
            
            // valid input
            assertThat(formatter.parse(TEST_VALUE_STR)).isEqualTo(TEST_VALUE_OBJ);
            
            // lenient input
            assertThatThrownBy(() -> formatter.parse(TEST_VALUE_STR_LENIENT)).isInstanceOf(TextParseException.class);
            
            // wrong input
            assertThatThrownBy(() -> formatter.parse("abcd")).isInstanceOf(TextParseException.class);
            
        }
        
        @Test
        public void testPrint() {
            
            assertThat(formatter.print(TEST_VALUE_OBJ)).isEqualTo(TEST_VALUE_STR);
            
        }
        
        @Test
        public void testParse_lenient() {
            
            // valid input
            assertThat(formatterLenient.parse(TEST_VALUE_STR)).isEqualTo(TEST_VALUE_OBJ);
            
            // lenient input
            assertThat(formatterLenient.parse(TEST_VALUE_STR_LENIENT)).isEqualTo(TEST_VALUE_OBJ);
            
            // wrong input
            assertThatThrownBy(() -> formatterLenient.parse("abcd")).isInstanceOf(TextParseException.class);
            
        }
        
        @Test
        public void testPrint_lenient() {
            
            assertThat(formatterLenient.print(TEST_VALUE_OBJ)).isEqualTo(TEST_VALUE_STR);
            
        }
        
        @Test
        public void testParse_precision() {
            
            // valid input
            assertThat(formatterPrecision.parse(TEST_VALUE_STR)).isEqualTo(TEST_VALUE_OBJ_PRECISION);
            
            // precisoin input
            assertThat(formatterPrecision.parse(TEST_VALUE_STR_LENIENT)).isEqualTo(TEST_VALUE_OBJ_PRECISION);
            
            // wrong input
            assertThatThrownBy(() -> formatterPrecision.parse("abcd")).isInstanceOf(TextParseException.class);
            
        }
        
        @Test
        public void testPrint_precison() {
            
            assertThat(formatterPrecision.print(TEST_VALUE_OBJ_PRECISION)).isEqualTo(TEST_VALUE_STR_PRECISION);
            
        }
        
    }
    
    public static class PrimitiveIntegerTest {
        
        private SimpleNumberFormatter<Integer> formatter;
        private SimpleNumberFormatter<Integer> formatterLenient;
        private SimpleNumberFormatter<Integer> formatterPrecision;
        
        @Before
        public void setUp() throws Exception {
            this.formatter = new SimpleNumberFormatter<>(int.class, false);
            this.formatterLenient = new SimpleNumberFormatter<>(int.class, true);
            this.formatterPrecision = new SimpleNumberFormatter<>(int.class, true, new MathContext(4, RoundingMode.DOWN));
            
        }
        
        private static final int TEST_VALUE_OBJ = 12345;
        private static final String TEST_VALUE_STR = "12345";
        private static final String TEST_VALUE_STR_LENIENT = "12345.678";
        
        private static final int TEST_VALUE_OBJ_PRECISION = 12340;
        private static final String TEST_VALUE_STR_PRECISION = "12340";
        
        @Test
        public void testParse() {
            
            // valid input
            assertThat(formatter.parse(TEST_VALUE_STR)).isEqualTo(TEST_VALUE_OBJ);
            
            // lenient input
            assertThatThrownBy(() -> formatter.parse(TEST_VALUE_STR_LENIENT)).isInstanceOf(TextParseException.class);
            
            // wrong input
            assertThatThrownBy(() -> formatter.parse("abcd")).isInstanceOf(TextParseException.class);
            
        }
        
        @Test
        public void testPrint() {
            
            assertThat(formatter.print(TEST_VALUE_OBJ)).isEqualTo(TEST_VALUE_STR);
            
        }
        
        @Test
        public void testParse_lenient() {
            
            // valid input
            assertThat(formatterLenient.parse(TEST_VALUE_STR)).isEqualTo(TEST_VALUE_OBJ);
            
            // lenient input
            assertThat(formatterLenient.parse(TEST_VALUE_STR_LENIENT)).isEqualTo(TEST_VALUE_OBJ);
            
            // wrong input
            assertThatThrownBy(() -> formatterLenient.parse("abcd")).isInstanceOf(TextParseException.class);
            
        }
        
        @Test
        public void testPrint_lenient() {
            
            assertThat(formatterLenient.print(TEST_VALUE_OBJ)).isEqualTo(TEST_VALUE_STR);
            
        }
        
        @Test
        public void testParse_precision() {
            
            // valid input
            assertThat(formatterPrecision.parse(TEST_VALUE_STR)).isEqualTo(TEST_VALUE_OBJ_PRECISION);
            
            // precisoin input
            assertThat(formatterPrecision.parse(TEST_VALUE_STR_LENIENT)).isEqualTo(TEST_VALUE_OBJ_PRECISION);
            
            // wrong input
            assertThatThrownBy(() -> formatterPrecision.parse("abcd")).isInstanceOf(TextParseException.class);
            
        }
        
        @Test
        public void testPrint_precison() {
            
            assertThat(formatterPrecision.print(TEST_VALUE_OBJ)).isEqualTo(TEST_VALUE_STR_PRECISION);
            
        }
        
    }
    
    public static class WrapperBigDecimalTest {
        
        private SimpleNumberFormatter<BigDecimal> formatter;
        private SimpleNumberFormatter<BigDecimal> formatterLenient;
        private SimpleNumberFormatter<BigDecimal> formatterPrecision;
        
        @Before
        public void setUp() throws Exception {
            this.formatter = new SimpleNumberFormatter<>(BigDecimal.class, false);
            this.formatterLenient = new SimpleNumberFormatter<>(BigDecimal.class, true);
            this.formatterPrecision = new SimpleNumberFormatter<>(BigDecimal.class, true, new MathContext(4, RoundingMode.DOWN));
            
        }
        
        private static final BigDecimal TEST_VALUE_OBJ = toBigDecimal("123.45");
        private static final String TEST_VALUE_STR = "123.45";
        private static final String TEST_VALUE_STR_LENIENT = "123.45";
        
        private static final BigDecimal TEST_VALUE_OBJ_PRECISION = toBigDecimal("123.4");
        private static final String TEST_VALUE_STR_PRECISION = "123.4";
        
        @Test
        public void testParse() {
            
            // valid input
            assertThat(formatter.parse(TEST_VALUE_STR)).isEqualTo(TEST_VALUE_OBJ);
            
            // lenient input
            assertThat(formatter.parse(TEST_VALUE_STR_LENIENT)).isEqualTo(TEST_VALUE_OBJ);
            
            // wrong input
            assertThatThrownBy(() -> formatter.parse("abcd")).isInstanceOf(TextParseException.class);
            
        }
        
        @Test
        public void testPrint() {
            
            assertThat(formatter.print(TEST_VALUE_OBJ)).isEqualTo(TEST_VALUE_STR);
            
        }
        
        @Test
        public void testParse_lenient() {
            
            // valid input
            assertThat(formatterLenient.parse(TEST_VALUE_STR)).isEqualTo(TEST_VALUE_OBJ);
            
            // lenient input
            assertThat(formatterLenient.parse(TEST_VALUE_STR_LENIENT)).isEqualTo(TEST_VALUE_OBJ);
            
            // wrong input
            assertThatThrownBy(() -> formatterLenient.parse("abcd")).isInstanceOf(TextParseException.class);
            
        }
        
        @Test
        public void testPrint_lenient() {
            
            assertThat(formatterLenient.print(TEST_VALUE_OBJ)).isEqualTo(TEST_VALUE_STR);
            
        }
        
        @Test
        public void testParse_precision() {
            
            // valid input
            assertThat(formatterPrecision.parse(TEST_VALUE_STR)).isEqualTo(TEST_VALUE_OBJ_PRECISION);
            
            // precisoin input
            assertThat(formatterPrecision.parse(TEST_VALUE_STR_LENIENT)).isEqualTo(TEST_VALUE_OBJ_PRECISION);
            
            // wrong input
            assertThatThrownBy(() -> formatterPrecision.parse("abcd")).isInstanceOf(TextParseException.class);
            
        }
        
        @Test
        public void testPrint_precison() {
            
            assertThat(formatterPrecision.print(TEST_VALUE_OBJ)).isEqualTo(TEST_VALUE_STR);
            
        }
        
    }
    
    public static class WrapperBigIntegerTest {
        
        private SimpleNumberFormatter<BigInteger> formatter;
        private SimpleNumberFormatter<BigInteger> formatterLenient;
        private SimpleNumberFormatter<BigInteger> formatterPrecision;
        
        @Before
        public void setUp() throws Exception {
            this.formatter = new SimpleNumberFormatter<>(BigInteger.class, false);
            this.formatterLenient = new SimpleNumberFormatter<>(BigInteger.class, true);
            this.formatterPrecision = new SimpleNumberFormatter<>(BigInteger.class, true, new MathContext(4, RoundingMode.DOWN));
            
        }
        
        private static final BigInteger TEST_VALUE_OBJ = toBigInteger("12345");
        private static final String TEST_VALUE_STR = "12345";
        private static final String TEST_VALUE_STR_LENIENT = "12345.678";
        
        private static final BigInteger TEST_VALUE_OBJ_PRECISION = toBigInteger("12340");
        private static final String TEST_VALUE_STR_PRECISION = "12340";
        
        @Test
        public void testParse() {
            
            // valid input
            assertThat(formatter.parse(TEST_VALUE_STR)).isEqualTo(TEST_VALUE_OBJ);
            
            // lenient input
            assertThatThrownBy(() -> formatter.parse(TEST_VALUE_STR_LENIENT)).isInstanceOf(TextParseException.class);
            
            // wrong input
            assertThatThrownBy(() -> formatter.parse("abcd")).isInstanceOf(TextParseException.class);
            
        }
        
        @Test
        public void testPrint() {
            
            assertThat(formatter.print(TEST_VALUE_OBJ)).isEqualTo(TEST_VALUE_STR);
            
        }
        
        @Test
        public void testParse_lenient() {
            
            // valid input
            assertThat(formatterLenient.parse(TEST_VALUE_STR)).isEqualTo(TEST_VALUE_OBJ);
            
            // lenient input
            assertThat(formatterLenient.parse(TEST_VALUE_STR_LENIENT)).isEqualTo(TEST_VALUE_OBJ);
            
            // wrong input
            assertThatThrownBy(() -> formatterLenient.parse("abcd")).isInstanceOf(TextParseException.class);
            
        }
        
        @Test
        public void testPrint_lenient() {
            
            assertThat(formatterLenient.print(TEST_VALUE_OBJ)).isEqualTo(TEST_VALUE_STR);
            
        }
        
        @Test
        public void testParse_precision() {
            
            // valid input
            assertThat(formatterPrecision.parse(TEST_VALUE_STR)).isEqualTo(TEST_VALUE_OBJ_PRECISION);
            
            // precisoin input
            assertThat(formatterPrecision.parse(TEST_VALUE_STR_LENIENT)).isEqualTo(TEST_VALUE_OBJ_PRECISION);
            
            // wrong input
            assertThatThrownBy(() -> formatterPrecision.parse("abcd")).isInstanceOf(TextParseException.class);
            
        }
        
        @Test
        public void testPrint_precison() {
            
            assertThat(formatterPrecision.print(TEST_VALUE_OBJ)).isEqualTo(TEST_VALUE_STR_PRECISION);
            
        }
        
    }
    
    public static class WrapperByteTest {
        
        private SimpleNumberFormatter<Byte> formatter;
        private SimpleNumberFormatter<Byte> formatterLenient;
        private SimpleNumberFormatter<Byte> formatterPrecision;
        
        @Before
        public void setUp() throws Exception {
            this.formatter = new SimpleNumberFormatter<>(Byte.class, false);
            this.formatterLenient = new SimpleNumberFormatter<>(Byte.class, true);
            this.formatterPrecision = new SimpleNumberFormatter<>(Byte.class, true, new MathContext(2, RoundingMode.DOWN));
            
        }
        
        private static final Byte TEST_VALUE_OBJ = toByte("123");
        private static final String TEST_VALUE_STR = "123";
        private static final String TEST_VALUE_STR_LENIENT = "123.45";
        
        private static final Byte TEST_VALUE_OBJ_PRECISION = toByte("120");
        private static final String TEST_VALUE_STR_PRECISION = "120";
        
        @Test
        public void testParse() {
            
            // valid input
            assertThat(formatter.parse(TEST_VALUE_STR)).isEqualTo(TEST_VALUE_OBJ);
            
            // lenient input
            assertThatThrownBy(() -> formatter.parse(TEST_VALUE_STR_LENIENT)).isInstanceOf(TextParseException.class);
            
            // wrong input
            assertThatThrownBy(() -> formatter.parse("abcd")).isInstanceOf(TextParseException.class);
            
        }
        
        @Test
        public void testPrint() {
            
            assertThat(formatter.print(TEST_VALUE_OBJ)).isEqualTo(TEST_VALUE_STR);
            
        }
        
        @Test
        public void testParse_lenient() {
            
            // valid input
            assertThat(formatterLenient.parse(TEST_VALUE_STR)).isEqualTo(TEST_VALUE_OBJ);
            
            // lenient input
            assertThat(formatterLenient.parse(TEST_VALUE_STR_LENIENT)).isEqualTo(TEST_VALUE_OBJ);
            
            // wrong input
            assertThatThrownBy(() -> formatterLenient.parse("abcd")).isInstanceOf(TextParseException.class);
            
        }
        
        @Test
        public void testPrint_lenient() {
            
            assertThat(formatterLenient.print(TEST_VALUE_OBJ)).isEqualTo(TEST_VALUE_STR);
            
        }
        
        @Test
        public void testParse_precision() {
            
            // valid input
            assertThat(formatterPrecision.parse(TEST_VALUE_STR)).isEqualTo(TEST_VALUE_OBJ_PRECISION);
            
            // precisoin input
            assertThat(formatterPrecision.parse(TEST_VALUE_STR_LENIENT)).isEqualTo(TEST_VALUE_OBJ_PRECISION);
            
            // wrong input
            assertThatThrownBy(() -> formatterPrecision.parse("abcd")).isInstanceOf(TextParseException.class);
            
        }
        
        @Test
        public void testPrint_precison() {
            
            assertThat(formatterPrecision.print(TEST_VALUE_OBJ)).isEqualTo(TEST_VALUE_STR_PRECISION);
            
        }
        
    }
    
    public static class PrimitiveByteTest {
        
        private SimpleNumberFormatter<Byte> formatter;
        private SimpleNumberFormatter<Byte> formatterLenient;
        private SimpleNumberFormatter<Byte> formatterPrecision;
        
        @Before
        public void setUp() throws Exception {
            this.formatter = new SimpleNumberFormatter<>(byte.class, false);
            this.formatterLenient = new SimpleNumberFormatter<>(byte.class, true);
            this.formatterPrecision = new SimpleNumberFormatter<>(byte.class, true, new MathContext(2, RoundingMode.DOWN));
            
        }
        
        private static final byte TEST_VALUE_OBJ = 123;
        private static final String TEST_VALUE_STR = "123";
        private static final String TEST_VALUE_STR_LENIENT = "123.45";
        
        private static final byte TEST_VALUE_OBJ_PRECISION = 120;
        private static final String TEST_VALUE_STR_PRECISION = "120";
        
        @Test
        public void testParse() {
            
            // valid input
            assertThat(formatter.parse(TEST_VALUE_STR)).isEqualTo(TEST_VALUE_OBJ);
            
            // lenient input
            assertThatThrownBy(() -> formatter.parse(TEST_VALUE_STR_LENIENT)).isInstanceOf(TextParseException.class);
            
            // wrong input
            assertThatThrownBy(() -> formatter.parse("abcd")).isInstanceOf(TextParseException.class);
            
        }
        
        @Test
        public void testPrint() {
            
            assertThat(formatter.print(TEST_VALUE_OBJ)).isEqualTo(TEST_VALUE_STR);
            
        }
        
        @Test
        public void testParse_lenient() {
            
            // valid input
            assertThat(formatterLenient.parse(TEST_VALUE_STR)).isEqualTo(TEST_VALUE_OBJ);
            
            // lenient input
            assertThat(formatterLenient.parse(TEST_VALUE_STR_LENIENT)).isEqualTo(TEST_VALUE_OBJ);
            
            // wrong input
            assertThatThrownBy(() -> formatterLenient.parse("abcd")).isInstanceOf(TextParseException.class);
            
        }
        
        @Test
        public void testPrint_lenient() {
            
            assertThat(formatterLenient.print(TEST_VALUE_OBJ)).isEqualTo(TEST_VALUE_STR);
            
        }
        
        @Test
        public void testParse_precision() {
            
            // valid input
            assertThat(formatterPrecision.parse(TEST_VALUE_STR)).isEqualTo(TEST_VALUE_OBJ_PRECISION);
            
            // precisoin input
            assertThat(formatterPrecision.parse(TEST_VALUE_STR_LENIENT)).isEqualTo(TEST_VALUE_OBJ_PRECISION);
            
            // wrong input
            assertThatThrownBy(() -> formatterPrecision.parse("abcd")).isInstanceOf(TextParseException.class);
            
        }
        
        @Test
        public void testPrint_precison() {
            
            assertThat(formatterPrecision.print(TEST_VALUE_OBJ)).isEqualTo(TEST_VALUE_STR_PRECISION);
            
        }
        
    }
    
    public static class WrapperShortTest {
        
        private SimpleNumberFormatter<Short> formatter;
        private SimpleNumberFormatter<Short> formatterLenient;
        private SimpleNumberFormatter<Short> formatterPrecision;
        
        @Before
        public void setUp() throws Exception {
            this.formatter = new SimpleNumberFormatter<>(Short.class, false);
            this.formatterLenient = new SimpleNumberFormatter<>(Short.class, true);
            this.formatterPrecision = new SimpleNumberFormatter<>(Short.class, true, new MathContext(4, RoundingMode.DOWN));
            
        }
        
        private static final Short TEST_VALUE_OBJ = toShort("12345");
        private static final String TEST_VALUE_STR = "12345";
        private static final String TEST_VALUE_STR_LENIENT = "12345.678";
        
        private static final Short TEST_VALUE_OBJ_PRECISION = toShort("12340");
        private static final String TEST_VALUE_STR_PRECISION = "12340";
        
        @Test
        public void testParse() {
            
            // valid input
            assertThat(formatter.parse(TEST_VALUE_STR)).isEqualTo(TEST_VALUE_OBJ);
            
            // lenient input
            assertThatThrownBy(() -> formatter.parse(TEST_VALUE_STR_LENIENT)).isInstanceOf(TextParseException.class);
            
            // wrong input
            assertThatThrownBy(() -> formatter.parse("abcd")).isInstanceOf(TextParseException.class);
            
        }
        
        @Test
        public void testPrint() {
            
            assertThat(formatter.print(TEST_VALUE_OBJ)).isEqualTo(TEST_VALUE_STR);
            
        }
        
        @Test
        public void testParse_lenient() {
            
            // valid input
            assertThat(formatterLenient.parse(TEST_VALUE_STR)).isEqualTo(TEST_VALUE_OBJ);
            
            // lenient input
            assertThat(formatterLenient.parse(TEST_VALUE_STR_LENIENT)).isEqualTo(TEST_VALUE_OBJ);
            
            // wrong input
            assertThatThrownBy(() -> formatterLenient.parse("abcd")).isInstanceOf(TextParseException.class);
            
        }
        
        @Test
        public void testPrint_lenient() {
            
            assertThat(formatterLenient.print(TEST_VALUE_OBJ)).isEqualTo(TEST_VALUE_STR);
            
        }
        
        @Test
        public void testParse_precision() {
            
            // valid input
            assertThat(formatterPrecision.parse(TEST_VALUE_STR)).isEqualTo(TEST_VALUE_OBJ_PRECISION);
            
            // precisoin input
            assertThat(formatterPrecision.parse(TEST_VALUE_STR_LENIENT)).isEqualTo(TEST_VALUE_OBJ_PRECISION);
            
            // wrong input
            assertThatThrownBy(() -> formatterPrecision.parse("abcd")).isInstanceOf(TextParseException.class);
            
        }
        
        @Test
        public void testPrint_precison() {
            
            assertThat(formatterPrecision.print(TEST_VALUE_OBJ)).isEqualTo(TEST_VALUE_STR_PRECISION);
            
        }
        
    }
    
    public static class PrimitiveShortTest {
        
        private SimpleNumberFormatter<Short> formatter;
        private SimpleNumberFormatter<Short> formatterLenient;
        private SimpleNumberFormatter<Short> formatterPrecision;
        
        @Before
        public void setUp() throws Exception {
            this.formatter = new SimpleNumberFormatter<>(short.class, false);
            this.formatterLenient = new SimpleNumberFormatter<>(short.class, true);
            this.formatterPrecision = new SimpleNumberFormatter<>(short.class, true, new MathContext(4, RoundingMode.DOWN));
            
        }
        
        private static final short TEST_VALUE_OBJ = 12345;
        private static final String TEST_VALUE_STR = "12345";
        private static final String TEST_VALUE_STR_LENIENT = "12345.678";
        
        private static final short TEST_VALUE_OBJ_PRECISION = 12340;
        private static final String TEST_VALUE_STR_PRECISION = "12340";
        
        @Test
        public void testParse() {
            
            // valid input
            assertThat(formatter.parse(TEST_VALUE_STR)).isEqualTo(TEST_VALUE_OBJ);
            
            // lenient input
            assertThatThrownBy(() -> formatter.parse(TEST_VALUE_STR_LENIENT)).isInstanceOf(TextParseException.class);
            
            // wrong input
            assertThatThrownBy(() -> formatter.parse("abcd")).isInstanceOf(TextParseException.class);
            
        }
        
        @Test
        public void testPrint() {
            
            assertThat(formatter.print(TEST_VALUE_OBJ)).isEqualTo(TEST_VALUE_STR);
            
        }
        
        @Test
        public void testParse_lenient() {
            
            // valid input
            assertThat(formatterLenient.parse(TEST_VALUE_STR)).isEqualTo(TEST_VALUE_OBJ);
            
            // lenient input
            assertThat(formatterLenient.parse(TEST_VALUE_STR_LENIENT)).isEqualTo(TEST_VALUE_OBJ);
            
            // wrong input
            assertThatThrownBy(() -> formatterLenient.parse("abcd")).isInstanceOf(TextParseException.class);
            
        }
        
        @Test
        public void testPrint_lenient() {
            
            assertThat(formatterLenient.print(TEST_VALUE_OBJ)).isEqualTo(TEST_VALUE_STR);
            
        }
        
        @Test
        public void testParse_precision() {
            
            // valid input
            assertThat(formatterPrecision.parse(TEST_VALUE_STR)).isEqualTo(TEST_VALUE_OBJ_PRECISION);
            
            // precisoin input
            assertThat(formatterPrecision.parse(TEST_VALUE_STR_LENIENT)).isEqualTo(TEST_VALUE_OBJ_PRECISION);
            
            // wrong input
            assertThatThrownBy(() -> formatterPrecision.parse("abcd")).isInstanceOf(TextParseException.class);
            
        }
        
        @Test
        public void testPrint_precison() {
            
            assertThat(formatterPrecision.print(TEST_VALUE_OBJ)).isEqualTo(TEST_VALUE_STR_PRECISION);
            
        }
        
    }
    
    public static class WrapperLongTest {
        
        private SimpleNumberFormatter<Long> formatter;
        private SimpleNumberFormatter<Long> formatterLenient;
        private SimpleNumberFormatter<Long> formatterPrecision;
        
        @Before
        public void setUp() throws Exception {
            this.formatter = new SimpleNumberFormatter<>(Long.class, false);
            this.formatterLenient = new SimpleNumberFormatter<>(Long.class, true);
            this.formatterPrecision = new SimpleNumberFormatter<>(Long.class, true, new MathContext(4, RoundingMode.DOWN));
            
        }
        
        private static final Long TEST_VALUE_OBJ = toLong("12345");
        private static final String TEST_VALUE_STR = "12345";
        private static final String TEST_VALUE_STR_LENIENT = "12345.678";
        
        private static final Long TEST_VALUE_OBJ_PRECISION = toLong("12340");
        private static final String TEST_VALUE_STR_PRECISION = "12340";
        
        @Test
        public void testParse() {
            
            // valid input
            assertThat(formatter.parse(TEST_VALUE_STR)).isEqualTo(TEST_VALUE_OBJ);
            
            // lenient input
            assertThatThrownBy(() -> formatter.parse(TEST_VALUE_STR_LENIENT)).isInstanceOf(TextParseException.class);
            
            // wrong input
            assertThatThrownBy(() -> formatter.parse("abcd")).isInstanceOf(TextParseException.class);
            
        }
        
        @Test
        public void testPrint() {
            
            assertThat(formatter.print(TEST_VALUE_OBJ)).isEqualTo(TEST_VALUE_STR);
            
        }
        
        @Test
        public void testParse_lenient() {
            
            // valid input
            assertThat(formatterLenient.parse(TEST_VALUE_STR)).isEqualTo(TEST_VALUE_OBJ);
            
            // lenient input
            assertThat(formatterLenient.parse(TEST_VALUE_STR_LENIENT)).isEqualTo(TEST_VALUE_OBJ);
            
            // wrong input
            assertThatThrownBy(() -> formatterLenient.parse("abcd")).isInstanceOf(TextParseException.class);
            
        }
        
        @Test
        public void testPrint_lenient() {
            
            assertThat(formatterLenient.print(TEST_VALUE_OBJ)).isEqualTo(TEST_VALUE_STR);
            
        }
        
        @Test
        public void testParse_precision() {
            
            // valid input
            assertThat(formatterPrecision.parse(TEST_VALUE_STR)).isEqualTo(TEST_VALUE_OBJ_PRECISION);
            
            // precisoin input
            assertThat(formatterPrecision.parse(TEST_VALUE_STR_LENIENT)).isEqualTo(TEST_VALUE_OBJ_PRECISION);
            
            // wrong input
            assertThatThrownBy(() -> formatterPrecision.parse("abcd")).isInstanceOf(TextParseException.class);
            
        }
        
        @Test
        public void testPrint_precison() {
            
            assertThat(formatterPrecision.print(TEST_VALUE_OBJ)).isEqualTo(TEST_VALUE_STR_PRECISION);
            
        }
        
    }
    
    public static class PrimitiveLongTest {
        
        private SimpleNumberFormatter<Long> formatter;
        private SimpleNumberFormatter<Long> formatterLenient;
        private SimpleNumberFormatter<Long> formatterPrecision;
        
        @Before
        public void setUp() throws Exception {
            this.formatter = new SimpleNumberFormatter<>(long.class, false);
            this.formatterLenient = new SimpleNumberFormatter<>(long.class, true);
            this.formatterPrecision = new SimpleNumberFormatter<>(long.class, true, new MathContext(4, RoundingMode.DOWN));
            
        }
        
        private static final long TEST_VALUE_OBJ = 12345;
        private static final String TEST_VALUE_STR = "12345";
        private static final String TEST_VALUE_STR_LENIENT = "12345.678";
        
        private static final long TEST_VALUE_OBJ_PRECISION = 12340;
        private static final String TEST_VALUE_STR_PRECISION = "12340";
        
        @Test
        public void testParse() {
            
            // valid input
            assertThat(formatter.parse(TEST_VALUE_STR)).isEqualTo(TEST_VALUE_OBJ);
            
            // lenient input
            assertThatThrownBy(() -> formatter.parse(TEST_VALUE_STR_LENIENT)).isInstanceOf(TextParseException.class);
            
            // wrong input
            assertThatThrownBy(() -> formatter.parse("abcd")).isInstanceOf(TextParseException.class);
            
        }
        
        @Test
        public void testPrint() {
            
            assertThat(formatter.print(TEST_VALUE_OBJ)).isEqualTo(TEST_VALUE_STR);
            
        }
        
        @Test
        public void testParse_lenient() {
            
            // valid input
            assertThat(formatterLenient.parse(TEST_VALUE_STR)).isEqualTo(TEST_VALUE_OBJ);
            
            // lenient input
            assertThat(formatterLenient.parse(TEST_VALUE_STR_LENIENT)).isEqualTo(TEST_VALUE_OBJ);
            
            // wrong input
            assertThatThrownBy(() -> formatterLenient.parse("abcd")).isInstanceOf(TextParseException.class);
            
        }
        
        @Test
        public void testPrint_lenient() {
            
            assertThat(formatterLenient.print(TEST_VALUE_OBJ)).isEqualTo(TEST_VALUE_STR);
            
        }
        
        @Test
        public void testParse_precision() {
            
            // valid input
            assertThat(formatterPrecision.parse(TEST_VALUE_STR)).isEqualTo(TEST_VALUE_OBJ_PRECISION);
            
            // precisoin input
            assertThat(formatterPrecision.parse(TEST_VALUE_STR_LENIENT)).isEqualTo(TEST_VALUE_OBJ_PRECISION);
            
            // wrong input
            assertThatThrownBy(() -> formatterPrecision.parse("abcd")).isInstanceOf(TextParseException.class);
            
        }
        
        @Test
        public void testPrint_precison() {
            
            assertThat(formatterPrecision.print(TEST_VALUE_OBJ)).isEqualTo(TEST_VALUE_STR_PRECISION);
            
        }
        
    }
    
    public static class WrapperFloatTest {
        
        private SimpleNumberFormatter<Float> formatter;
        private SimpleNumberFormatter<Float> formatterLenient;
        private SimpleNumberFormatter<Float> formatterPrecision;
        
        @Before
        public void setUp() throws Exception {
            this.formatter = new SimpleNumberFormatter<>(Float.class, false);
            this.formatterLenient = new SimpleNumberFormatter<>(Float.class, true);
            this.formatterPrecision = new SimpleNumberFormatter<>(Float.class, true, new MathContext(4, RoundingMode.DOWN));
            
        }
        
        private static final Float TEST_VALUE_OBJ = toFloat("123.45");
        private static final String TEST_VALUE_STR = "123.45";
        private static final String TEST_VALUE_STR_LENIENT = "123.45";
        
        private static final Float TEST_VALUE_OBJ_PRECISION = toFloat("123.4");
        private static final String TEST_VALUE_STR_PRECISION = "123.4";
        
        @Test
        public void testParse() {
            
            // valid input
            assertThat(formatter.parse(TEST_VALUE_STR)).isEqualTo(TEST_VALUE_OBJ);
            
            // lenient input
            assertThat(formatter.parse(TEST_VALUE_STR_LENIENT)).isEqualTo(TEST_VALUE_OBJ);
            
            // wrong input
            assertThatThrownBy(() -> formatter.parse("abcd")).isInstanceOf(TextParseException.class);
            
        }
        
        @Test
        public void testPrint() {
            
            assertThat(formatter.print(TEST_VALUE_OBJ)).isEqualTo(TEST_VALUE_STR);
            
        }
        
        @Test
        public void testParse_lenient() {
            
            // valid input
            assertThat(formatterLenient.parse(TEST_VALUE_STR)).isEqualTo(TEST_VALUE_OBJ);
            
            // lenient input
            assertThat(formatterLenient.parse(TEST_VALUE_STR_LENIENT)).isEqualTo(TEST_VALUE_OBJ);
            
            // wrong input
            assertThatThrownBy(() -> formatterLenient.parse("abcd")).isInstanceOf(TextParseException.class);
            
        }
        
        @Test
        public void testPrint_lenient() {
            
            assertThat(formatterLenient.print(TEST_VALUE_OBJ)).isEqualTo(TEST_VALUE_STR);
            
        }
        
        @Test
        public void testParse_precision() {
            
            // valid input
            assertThat(formatterPrecision.parse(TEST_VALUE_STR)).isEqualTo(TEST_VALUE_OBJ_PRECISION);
            
            // precisoin input
            assertThat(formatterPrecision.parse(TEST_VALUE_STR_LENIENT)).isEqualTo(TEST_VALUE_OBJ_PRECISION);
            
            // wrong input
            assertThatThrownBy(() -> formatterPrecision.parse("abcd")).isInstanceOf(TextParseException.class);
            
        }
        
        @Test
        public void testPrint_precison() {
            
            assertThat(formatterPrecision.print(TEST_VALUE_OBJ)).isEqualTo(TEST_VALUE_STR_PRECISION);
            
        }
        
    }
    
    public static class PrimitiveFloatTest {
        
        private SimpleNumberFormatter<Float> formatter;
        private SimpleNumberFormatter<Float> formatterLenient;
        private SimpleNumberFormatter<Float> formatterPrecision;
        
        @Before
        public void setUp() throws Exception {
            this.formatter = new SimpleNumberFormatter<>(float.class, false);
            this.formatterLenient = new SimpleNumberFormatter<>(float.class, true);
            this.formatterPrecision = new SimpleNumberFormatter<>(float.class, true, new MathContext(4, RoundingMode.DOWN));
            
        }
        
        private static final float TEST_VALUE_OBJ = 123.45f;
        private static final String TEST_VALUE_STR = "123.45";
        private static final String TEST_VALUE_STR_LENIENT = "123.45";
        
        private static final float TEST_VALUE_OBJ_PRECISION = 123.4f;
        private static final String TEST_VALUE_STR_PRECISION = "123.4";
        
        @Test
        public void testParse() {
            
            // valid input
            assertThat(formatter.parse(TEST_VALUE_STR)).isEqualTo(TEST_VALUE_OBJ);
            
            // lenient input
            assertThat(formatter.parse(TEST_VALUE_STR_LENIENT)).isEqualTo(TEST_VALUE_OBJ);
            
            // wrong input
            assertThatThrownBy(() -> formatter.parse("abcd")).isInstanceOf(TextParseException.class);
            
        }
        
        @Test
        public void testPrint() {
            
            assertThat(formatter.print(TEST_VALUE_OBJ)).isEqualTo(TEST_VALUE_STR);
            
        }
        
        @Test
        public void testParse_lenient() {
            
            // valid input
            assertThat(formatterLenient.parse(TEST_VALUE_STR)).isEqualTo(TEST_VALUE_OBJ);
            
            // lenient input
            assertThat(formatterLenient.parse(TEST_VALUE_STR_LENIENT)).isEqualTo(TEST_VALUE_OBJ);
            
            // wrong input
            assertThatThrownBy(() -> formatterLenient.parse("abcd")).isInstanceOf(TextParseException.class);
            
        }
        
        @Test
        public void testPrint_lenient() {
            
            assertThat(formatterLenient.print(TEST_VALUE_OBJ)).isEqualTo(TEST_VALUE_STR);
            
        }
        
        @Test
        public void testParse_precision() {
            
            // valid input
            assertThat(formatterPrecision.parse(TEST_VALUE_STR)).isEqualTo(TEST_VALUE_OBJ_PRECISION);
            
            // precisoin input
            assertThat(formatterPrecision.parse(TEST_VALUE_STR_LENIENT)).isEqualTo(TEST_VALUE_OBJ_PRECISION);
            
            // wrong input
            assertThatThrownBy(() -> formatterPrecision.parse("abcd")).isInstanceOf(TextParseException.class);
            
        }
        
        @Test
        public void testPrint_precison() {
            
            assertThat(formatterPrecision.print(TEST_VALUE_OBJ)).isEqualTo(TEST_VALUE_STR_PRECISION);
            
        }
        
    }
    
    public static class WrapperDoubleTest {
        
        private SimpleNumberFormatter<Double> formatter;
        private SimpleNumberFormatter<Double> formatterLenient;
        private SimpleNumberFormatter<Double> formatterPrecision;
        
        @Before
        public void setUp() throws Exception {
            this.formatter = new SimpleNumberFormatter<>(Double.class, false);
            this.formatterLenient = new SimpleNumberFormatter<>(Double.class, true);
            this.formatterPrecision = new SimpleNumberFormatter<>(Double.class, true, new MathContext(4, RoundingMode.DOWN));
            
        }
        
        private static final Double TEST_VALUE_OBJ = toDouble("123.45");
        private static final String TEST_VALUE_STR = "123.45";
        private static final String TEST_VALUE_STR_LENIENT = "123.45";
        
        private static final Double TEST_VALUE_OBJ_PRECISION = toDouble("123.4");
        private static final String TEST_VALUE_STR_PRECISION = "123.4";
        
        @Test
        public void testParse() {
            
            // valid input
            assertThat(formatter.parse(TEST_VALUE_STR)).isEqualTo(TEST_VALUE_OBJ);
            
            // lenient input
            assertThat(formatter.parse(TEST_VALUE_STR_LENIENT)).isEqualTo(TEST_VALUE_OBJ);
            
            // wrong input
            assertThatThrownBy(() -> formatter.parse("abcd")).isInstanceOf(TextParseException.class);
            
        }
        
        @Test
        public void testPrint() {
            
            assertThat(formatter.print(TEST_VALUE_OBJ)).isEqualTo(TEST_VALUE_STR);
            
        }
        
        @Test
        public void testParse_lenient() {
            
            // valid input
            assertThat(formatterLenient.parse(TEST_VALUE_STR)).isEqualTo(TEST_VALUE_OBJ);
            
            // lenient input
            assertThat(formatterLenient.parse(TEST_VALUE_STR_LENIENT)).isEqualTo(TEST_VALUE_OBJ);
            
            // wrong input
            assertThatThrownBy(() -> formatterLenient.parse("abcd")).isInstanceOf(TextParseException.class);
            
        }
        
        @Test
        public void testPrint_lenient() {
            
            assertThat(formatterLenient.print(TEST_VALUE_OBJ)).isEqualTo(TEST_VALUE_STR);
            
        }
        
        @Test
        public void testParse_precision() {
            
            // valid input
            assertThat(formatterPrecision.parse(TEST_VALUE_STR)).isEqualTo(TEST_VALUE_OBJ_PRECISION);
            
            // precisoin input
            assertThat(formatterPrecision.parse(TEST_VALUE_STR_LENIENT)).isEqualTo(TEST_VALUE_OBJ_PRECISION);
            
            // wrong input
            assertThatThrownBy(() -> formatterPrecision.parse("abcd")).isInstanceOf(TextParseException.class);
            
        }
        
        @Test
        public void testPrint_precison() {
            
            assertThat(formatterPrecision.print(TEST_VALUE_OBJ)).isEqualTo(TEST_VALUE_STR_PRECISION);
            
        }
        
    }
    
    public static class PrimitiveDoubleTest {
        
        private SimpleNumberFormatter<Double> formatter;
        private SimpleNumberFormatter<Double> formatterLenient;
        private SimpleNumberFormatter<Double> formatterPrecision;
        
        @Before
        public void setUp() throws Exception {
            this.formatter = new SimpleNumberFormatter<>(double.class, false);
            this.formatterLenient = new SimpleNumberFormatter<>(double.class, true);
            this.formatterPrecision = new SimpleNumberFormatter<>(double.class, true, new MathContext(4, RoundingMode.DOWN));
            
        }
        
        private static final double TEST_VALUE_OBJ = 123.45d;
        private static final String TEST_VALUE_STR = "123.45";
        private static final String TEST_VALUE_STR_LENIENT = "123.45";
        
        private static final double TEST_VALUE_OBJ_PRECISION = 123.4d;
        private static final String TEST_VALUE_STR_PRECISION = "123.4";
        
        @Test
        public void testParse() {
            
            // valid input
            assertThat(formatter.parse(TEST_VALUE_STR)).isEqualTo(TEST_VALUE_OBJ);
            
            // lenient input
            assertThat(formatter.parse(TEST_VALUE_STR_LENIENT)).isEqualTo(TEST_VALUE_OBJ);
            
            // wrong input
            assertThatThrownBy(() -> formatter.parse("abcd")).isInstanceOf(TextParseException.class);
            
        }
        
        @Test
        public void testPrint() {
            
            assertThat(formatter.print(TEST_VALUE_OBJ)).isEqualTo(TEST_VALUE_STR);
            
        }
        
        @Test
        public void testParse_lenient() {
            
            // valid input
            assertThat(formatterLenient.parse(TEST_VALUE_STR)).isEqualTo(TEST_VALUE_OBJ);
            
            // lenient input
            assertThat(formatterLenient.parse(TEST_VALUE_STR_LENIENT)).isEqualTo(TEST_VALUE_OBJ);
            
            // wrong input
            assertThatThrownBy(() -> formatterLenient.parse("abcd")).isInstanceOf(TextParseException.class);
            
        }
        
        @Test
        public void testPrint_lenient() {
            
            assertThat(formatterLenient.print(TEST_VALUE_OBJ)).isEqualTo(TEST_VALUE_STR);
            
        }
        
        @Test
        public void testParse_precision() {
            
            // valid input
            assertThat(formatterPrecision.parse(TEST_VALUE_STR)).isEqualTo(TEST_VALUE_OBJ_PRECISION);
            
            // precisoin input
            assertThat(formatterPrecision.parse(TEST_VALUE_STR_LENIENT)).isEqualTo(TEST_VALUE_OBJ_PRECISION);
            
            // wrong input
            assertThatThrownBy(() -> formatterPrecision.parse("abcd")).isInstanceOf(TextParseException.class);
            
        }
        
        @Test
        public void testPrint_precison() {
            
            assertThat(formatterPrecision.print(TEST_VALUE_OBJ)).isEqualTo(TEST_VALUE_STR_PRECISION);
            
        }
        
    }
    
}

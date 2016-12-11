package com.github.mygreen.supercsv.expression;

import static org.junit.Assert.*;
import static org.assertj.core.api.Assertions.*;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;


import org.junit.Before;
import org.junit.Test;

import com.github.mygreen.supercsv.cellprocessor.format.TextPrinter;
import com.github.mygreen.supercsv.expression.CustomFunctions;

/**
 * {@link CustomFunctions}のテスタ
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class CustomFunctionsTest {
    
    private TextPrinter<Integer> printer;
    
    @Before
    public void setUp() throws Exception {
        this.printer = (value) -> new DecimalFormat("#,##0").format(value);
        
    }
    
    @Test
    public void testDefaultString() {
        
        assertThat(CustomFunctions.defaultString(null)).isEqualTo("");
        assertThat(CustomFunctions.defaultString("")).isEqualTo("");
        assertThat(CustomFunctions.defaultString("abc")).isEqualTo("abc");
        
    }
    
    /**
     * {@link CustomFunctions#join(int[], String)}
     */
    @Test
    public void testJoin_int_array() {
        
        assertThat(CustomFunctions.join((int[])null, ", ")).isEqualTo("");
        assertThat(CustomFunctions.join(new int[]{}, ", ")).isEqualTo("");
        assertThat(CustomFunctions.join(new int[]{1,2,3}, ", ")).isEqualTo("1, 2, 3");
        assertThat(CustomFunctions.join(new int[]{1,2,3}, null)).isEqualTo("123");
        
    }
    
    /**
     * {@link CustomFunctions#join(Object[], String)}
     */
    @Test
    public void testJoin_object_array() {
        
        assertThat(CustomFunctions.join((Object[])null, ", ")).isEqualTo("");
        assertThat(CustomFunctions.join(new Object[]{}, ", ")).isEqualTo("");
        assertThat(CustomFunctions.join(new Object[]{1,2,3}, ", ")).isEqualTo("1, 2, 3");
        assertThat(CustomFunctions.join(new Object[]{1,2,3}, null)).isEqualTo("123");
        
    }
    
    /**
     * {@link CustomFunctions#join(Object[], String, TextPrinter)}
     */
    @Test
    public void testJoin_object_array_printer() {
        
        Object[] input = new Object[]{1000, 2000, 3000};
        
        assertThatThrownBy(() -> CustomFunctions.join((Object[])null, ", ", null)).isInstanceOf(NullPointerException.class);
        assertThat(CustomFunctions.join((Object[])null, ", ", printer)).isEqualTo("");
        assertThat(CustomFunctions.join(new Object[]{}, ", ", printer)).isEqualTo("");
        assertThat(CustomFunctions.join(input, ", ", printer)).isEqualTo("1,000, 2,000, 3,000");
        assertThat(CustomFunctions.join(input, null, printer)).isEqualTo("1,0002,0003,000");
        
    }
    
    /**
     * {@link CustomFunctions#join(java.util.Collection, String)}
     */
    @Test
    public void testJoin_collection() {
        
        Collection<Integer> input = Arrays.asList(1000, 2000, 3000);
        
        assertThat(CustomFunctions.join((Collection<Integer>)null, ", ")).isEqualTo("");
        assertThat(CustomFunctions.join(Collections.emptyList(), ", ")).isEqualTo("");
        assertThat(CustomFunctions.join(input, ", ")).isEqualTo("1000, 2000, 3000");
        assertThat(CustomFunctions.join(input, null)).isEqualTo("100020003000");
        
    }
    
    /**
     * {@link CustomFunctions#join(Collection, String, TextPrinter)}
     */
    @Test
    public void testJoin_collection_printer() {
        
        Collection<Integer> input = Arrays.asList(1000, 2000, 3000);
        
        assertThatThrownBy(() -> CustomFunctions.join((Object[])null, ", ", null)).isInstanceOf(NullPointerException.class);
        assertThat(CustomFunctions.join((Collection<Integer>)null, ", ", printer)).isEqualTo("");
        assertThat(CustomFunctions.join(Collections.emptyList(), ", ", printer)).isEqualTo("");
        assertThat(CustomFunctions.join(input, ", ", printer)).isEqualTo("1,000, 2,000, 3,000");
        assertThat(CustomFunctions.join(input, null, printer)).isEqualTo("1,0002,0003,000");
        
    }
}

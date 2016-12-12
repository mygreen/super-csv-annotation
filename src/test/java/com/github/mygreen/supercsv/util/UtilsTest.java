package com.github.mygreen.supercsv.util;

import static org.junit.Assert.*;
import static org.assertj.core.api.Assertions.*;
import static com.github.mygreen.supercsv.tool.TestUtils.*;

import org.junit.Test;

/**
 * {@link Utils}のテスタ
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class UtilsTest {
    
    /**
     * {@link Utils#getPrimitiveDefaultValue(Class)}
     */
    @Test
    public void testGetPrimitiveDefaultValue() {
        
        {
            // null
            assertThatThrownBy(() -> Utils.getPrimitiveDefaultValue(null)).isInstanceOf(NullPointerException.class);
        }
        
        {
            // non-primitive
            assertThat(Utils.getPrimitiveDefaultValue(Integer.class)).isNull();
        }
        
        {
            // primitive
            assertThat(Utils.getPrimitiveDefaultValue(boolean.class)).isEqualTo(false);
            assertThat(Utils.getPrimitiveDefaultValue(char.class)).isEqualTo('\u0000');
            assertThat(Utils.getPrimitiveDefaultValue(byte.class)).isEqualTo((byte)0);
            assertThat(Utils.getPrimitiveDefaultValue(short.class)).isEqualTo((short)0);
            assertThat(Utils.getPrimitiveDefaultValue(int.class)).isEqualTo(0);
            assertThat(Utils.getPrimitiveDefaultValue(long.class)).isEqualTo(0L);
            assertThat(Utils.getPrimitiveDefaultValue(float.class)).isEqualTo(0.0f);
            assertThat(Utils.getPrimitiveDefaultValue(double.class)).isEqualTo(0.0d);
            
            
        }
        
    
    }
}

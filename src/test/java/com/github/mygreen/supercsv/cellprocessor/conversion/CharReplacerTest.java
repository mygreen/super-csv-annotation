package com.github.mygreen.supercsv.cellprocessor.conversion;

import static org.junit.Assert.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.Before;

import org.junit.Test;

/**
 * {@link CharReplacer}のテスタ
 * 
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class CharReplacerTest {
    
    private CharReplacer replacer;
    
    @Before
    public void setUp() throws Exception {
        this.replacer = new CharReplacer();
    }
    
    @Test(expected=NullPointerException.class)
    public void testRegister_srcNull() {
        
        replacer.register(null, "a");
        fail();
        
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testRegister_srcEmpty() {
        
        replacer.register("", "a");
        fail();
        
    }
    
    @Test(expected=NullPointerException.class)
    public void testRegister_destNull() {
        
        replacer.register("A", null);
        fail();
        
    }
    
    @Test
    public void testRegister_destEmpty() {
        
        replacer.register("A", "");
        
    }
    
    /**
     * 置換文字が1文字の場合の置換
     */
    @Test
    public void testReplace_single() {
        
        replacer.register("a", "z");
        replacer.register("z", "Z");
        replacer.ready();
        
        // nullの場合
        assertThat(replacer.replace(null)).isEqualTo(null);
        
        // 空文字の場合
        assertThat(replacer.replace("")).isEqualTo("");
        
        // 1文字の場合 - match
        assertThat(replacer.replace("a")).isEqualTo("z");
        
        // 1文字の場合 - no match
        assertThat(replacer.replace("c")).isEqualTo("c");
        
        // 複数文字の場合 - match
        assertThat(replacer.replace("a_a_z")).isEqualTo("z_z_Z");
        
        // 複数文字の場合 - no-match
        assertThat(replacer.replace("hello")).isEqualTo("hello");
    }
    
    /**
     * 置換文字が複数文字の場合の置換
     */
    @Test
    public void testReplace_multi() {
        
        replacer.register("ab", "cd");
        replacer.register("cd", "ef");
        replacer.register("abcd", "ef");
        replacer.register("abc", "defg");
        replacer.ready();
        
        // nullの場合
        assertThat(replacer.replace(null)).isEqualTo(null);
        
        // 空文字の場合
        assertThat(replacer.replace("")).isEqualTo("");
        
        // 2文字の場合 - match
        assertThat(replacer.replace("ab")).isEqualTo("cd");
        
        // 2文字の場合 - no match
        assertThat(replacer.replace("bc")).isEqualTo("bc");
        
        // 4文字の場合 - match
        assertThat(replacer.replace("abcd")).isEqualTo("ef");
        
        // 複数文字の場合 - match
        assertThat(replacer.replace("ab_abcd_cd")).isEqualTo("cd_ef_ef");
        
        // 複数文字の場合 - no-match
        assertThat(replacer.replace("hello")).isEqualTo("hello");
    }
    
    /**
     * 置換文字が複合の場合の置換
     */
    @Test
    public void testReplace_complex() {
        
        replacer.register("a", "z");
        replacer.register("z", "Z");
        
        replacer.register("ab", "cd");
        replacer.register("cd", "ef");
        replacer.register("abcd", "ef");
        
        replacer.ready();
        
        // nullの場合
        assertThat(replacer.replace(null)).isEqualTo(null);
        
        // 空文字の場合
        assertThat(replacer.replace("")).isEqualTo("");
        
        // match
        assertThat(replacer.replace("test_aa_zz_abcd_ef")).isEqualTo("test_zz_ZZ_ef_ef");
        
        // no match
        assertThat(replacer.replace("hello")).isEqualTo("hello");
    }
    
    /**
     * 置換語彙が重複する場合
     */
    @Test
    public void testRegisterAndReplace_duplicateWord() {
        
        replacer.register("a", "b");
        replacer.register("a", "c");
        
        replacer.register("xyz", "hello");
        replacer.register("xyz", "world");
        
        replacer.ready();
        
        assertThat(replacer.replace("abc")).isEqualTo("bbc");
        
        assertThat(replacer.replace("abc_xyz")).isEqualTo("bbc_hello");
    }
    
}

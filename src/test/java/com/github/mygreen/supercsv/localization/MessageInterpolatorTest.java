package com.github.mygreen.supercsv.localization;

import static com.github.mygreen.supercsv.tool.TestUtils.*;
import static org.assertj.core.api.Assertions.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;


/**
 * {@link MessageInterpolator}のテスタ。
 *
 * @since 2.4
 * @author T.TSUCHIE
 *
 */
public class MessageInterpolatorTest {
    
    /**
     * 変数のみ - EL式なし
     */
    @Test
    public void testInterpolate_var() {
        
        MessageInterpolator interpolator = new MessageInterpolator();
        
        String message = "{validatedValue} は、{min}～{max}の範囲で入力してください。";
        
        int validatedValue = 3;
        
        Map<String, Object> vars = new HashMap<>();
        vars.put("validatedValue", validatedValue);
        vars.put("min", 1);
        vars.put("max", 10);
        
        String actual = interpolator.interpolate(message, vars);
        assertThat(actual).isEqualTo("3 は、1～10の範囲で入力してください。");
        
    }
    
    /**
     * EL式あり - 数値のフォーマット
     */
    @Test
    public void testInterpolate_el01() {
        
        MessageInterpolator interpolator = new MessageInterpolator();
        
        String message = "${formatter.format('%1.1f', validatedValue)}は、${min}～${max}の範囲で入力してください。";
        
        double validatedValue = 3;
        
        Map<String, Object> vars = new HashMap<>();
        vars.put("validatedValue", validatedValue);
        vars.put("min", 1);
        vars.put("max", 10);
        
        String actual = interpolator.interpolate(message, vars);
        assertThat(actual).isEqualTo("3.0は、1～10の範囲で入力してください。");
        
    }
    
    /**
     * EL式あり - 日付のフォーマット
     */
    @Test
    public void testInterpolate_el02() {
        
        MessageInterpolator interpolator = new MessageInterpolator();
        
        String message = "現在の日付「${formatter.format('%1$tY/%1$tm/%1$td', validatedValue)}」は未来日です。";
        
        Date validatedValue = toTimestamp("2015-05-01 12:31:49.000");
        
        Map<String, Object> vars = new HashMap<>();
        vars.put("validatedValue", validatedValue);
        
        String actual = interpolator.interpolate(message, vars);
        assertThat(actual).isEqualTo("現在の日付「2015/05/01」は未来日です。");
//        System.out.println(actual);
        
    }
    
    /**
     * EL式中にエスケープ文字あり
     */
    @Test
    public void testInterpolate_escape01() {
        
        MessageInterpolator interpolator = new MessageInterpolator();
        
        String message = "\\${formatter.format('%1.1f',validatedValue)}は、\\{min}～${max}の範囲で入力してください。";
        
        double validatedValue = 3;
        
        Map<String, Object> vars = new HashMap<>();
        vars.put("validatedValue", validatedValue);
        vars.put("min", 1);
        vars.put("max", 10);
        
        String actual = interpolator.interpolate(message, vars);
        assertThat(actual).isEqualTo("${formatter.format('%1.1f',validatedValue)}は、{min}～10の範囲で入力してください。");
//        System.out.println(actual);
        
    }
    
    /**
     * EL式中にエスケープ文字あり
     */
    @Test
    public void testInterpolate_escape02() {
        
        MessageInterpolator interpolator = new MessageInterpolator();
        
        String message = "${'Helo World\\}' + formatter.format('%1.1f', validatedValue)}は、{min}～${max}の範囲で入力してください。";
        
        double validatedValue = 3;
        
        Map<String, Object> vars = new HashMap<>();
        vars.put("validatedValue", validatedValue);
        vars.put("min", 1);
        vars.put("max", 10);
        
        String actual = interpolator.interpolate(message, vars);
        assertThat(actual).isEqualTo("Helo World}3.0は、1～10の範囲で入力してください。");
//        System.out.println(actual);
        
    }
    
    /**
     * メッセージ中の式が途中で終わる場合
     */
    @Test
    public void testInterpolate_lack_end() {
        
        MessageInterpolator interpolator = new MessageInterpolator();
        
        String message = "${'Helo World\\}' += formatter.format('%1.1f', validatedValue)";
        
        double validatedValue = 3;
        
        Map<String, Object> vars = new HashMap<>();
        vars.put("validatedValue", validatedValue);
        vars.put("min", 1);
        vars.put("max", 10);
        
        String actual = interpolator.interpolate(message, vars);
        
        assertThat(actual).isEqualTo("${'Helo World}' += formatter.format('%1.1f', validatedValue)");
    }
    
    /**
     * 再起的にメッセージを評価する。
     * 変数の再起
     */
    @Test
    public void testInterpolate_recursive_vars() {
        
        MessageInterpolator interpolator = new MessageInterpolator();
        interpolator.setRecursiveForVar(true);
        
        String message = "{abc} : {message}";
        
        Map<String, Object> vars = new HashMap<>();
        vars.put("message", "${1+2}");
        
        String actual = interpolator.interpolate(message, vars, true);
        assertThat(actual).isEqualTo("{abc} : 3");
        
    }
    
    /**
     * 再起的にメッセージを評価する。
     * 式の再起
     */
    @Test
    public void testInterpolate_recursive_el() {
        
        MessageInterpolator interpolator = new MessageInterpolator();
        interpolator.setRecursiveForEl(true);
        
        String message = "{abc} : ${value}";
        
        Map<String, Object> vars = new HashMap<>();
        vars.put("value", "{min}");
        vars.put("min", 3);
        
        String actual = interpolator.interpolate(message, vars, true);
        assertThat(actual).isEqualTo("{abc} : 3");
        
    }
    
    /**
     * 式中の変数の値がない場合
     */
    @Test
    public void testInterpolate_no_define_vars() {
        
        MessageInterpolator interpolator = new MessageInterpolator();
        
        String message = "{rowNumber}";
        
        Map<String, Object> vars = new HashMap<>();
        
        String actual = interpolator.interpolate(message, vars, true);
        assertThat(actual).isEqualTo("{rowNumber}");
        
    }
    
    /**
     * 再起的にメッセージを評価する。
     * 再帰回数
     */
    @Test
    public void testInterpolate_recursive_maxDepth() {
        
        MessageInterpolator interpolator = new MessageInterpolator();
        interpolator.setRecursiveForVar(true);
        
        String message = "{abc} : {value}";
        
        // depth2
        {
            interpolator.setMaxRecursiveDepth(2);
            
            Map<String, Object> vars = new HashMap<>();
            vars.put("value", "{min}");
            vars.put("min", "{value}");
            
            String actual = interpolator.interpolate(message, vars, true);
            assertThat(actual).isEqualTo("{abc} : {value}");
        
        }
        
        // depth3
        {
            interpolator.setMaxRecursiveDepth(3);
            
            Map<String, Object> vars = new HashMap<>();
            vars.put("value", "{min}");
            vars.put("min", "{value}");
            
            String actual = interpolator.interpolate(message, vars, true);
            assertThat(actual).isEqualTo("{abc} : {min}");
        
        }
        
    }
    
    /**
     * 式中の変数の値がない場合
     */
    @Test
    public void testInterpolate_no_define_vars2() {
        
        MessageInterpolator interpolator = new MessageInterpolator();
        
        String message = "${rowNumber}";
        
        Map<String, Object> vars = new HashMap<>();
        
        String actual = interpolator.interpolate(message, vars, true);
        assertThat(actual).isEqualTo("");
        
    }
}

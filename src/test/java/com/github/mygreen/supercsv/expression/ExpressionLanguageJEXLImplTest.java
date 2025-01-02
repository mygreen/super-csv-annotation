package com.github.mygreen.supercsv.expression;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

/**
 * {@link ExpressionLanguageJEXLImpl}のテスタ
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class ExpressionLanguageJEXLImplTest {
    
    private ExpressionLanguageJEXLImpl el;

    @Before
    public void setUp() throws Exception {
        this.el = new ExpressionLanguageJEXLImpl();
    }
    
    @Test
    public void test_empty() {
        
        String expression = "empty(label) ? '空です' : label";
        
        {
            Map<String, Object> vars = new HashMap<>();
            
            String eval1 = (String) el.evaluate(expression, vars);
            assertThat(eval1).isEqualTo("空です");
        }
        
        {
            Map<String, Object> vars = new HashMap<>();
            
            vars.put("label", "Hello world.");
            String eval2 = (String) el.evaluate(expression, vars);
            assertThat(eval2).isEqualTo("Hello world.");
        }
        
        
    }
    
    @Test(expected=ExpressionEvaluationException.class)
    public void test_error_exp() {
        
        String expression = "aaa ?  label";
        
        Map<String, Object> vars = new HashMap<>();
        
        el.evaluate(expression, vars);
        fail();
    }
    
    /**
     * 名前空間付きの関数
     */
    @Test
    public void test_function() {
        
        String expression = "f:join(array, ', ')";
        
        Map<String, Object> vars = new HashMap<>();
        vars.put("array", new int[]{1,2,3});
        
        String eval = (String) el.evaluate(expression, vars);
        assertThat(eval).isEqualTo("1, 2, 3");
        
    }
    

    /**
     * ELインジェクション
     */
    @Test
    public void test_injection() {

        String expression = "''.getClass().forName('java.lang.Runtime').getRuntime().exec('notepad')";

        Object eval = el.evaluate(expression, Collections.emptyMap());
        
        // 評価に失敗しnullが返ってくる
        assertThat(eval).isNull();

    }
    
    
}

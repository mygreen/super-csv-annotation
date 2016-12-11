package com.github.mygreen.supercsv.expression;

import java.util.Map;

/**
 * 式言語の評価に失敗した場合にスローする例外。
 * 
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class ExpressionEvaluationException extends RuntimeException {
    
    /** serialVersionUID */
    private static final long serialVersionUID = 1L;
    
    private final String expression;
    
    private final Map<String, Object> variables;
    
    public ExpressionEvaluationException(final String message, final Throwable cause,
            final String expression, final Map<String, Object> variables) {
        super(message, cause);
        this.expression = expression;
        this.variables = variables;
        
    }
    
    /**
     * 評価に失敗した式を取得する。
     * @return 式を取得する。
     */
    public String getExpression() {
        return expression;
    }
    
    /**
     * 評価に失敗した変数のマップを取得する。
     * @return 変数のマップ
     */
    public Map<String, Object> getVariables() {
        return variables;
    }
    
}

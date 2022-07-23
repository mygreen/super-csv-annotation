package com.github.mygreen.supercsv.expression;

import java.util.Map;
import java.util.Objects;

import org.apache.commons.jexl2.Expression;
import org.apache.commons.jexl2.JexlEngine;
import org.apache.commons.jexl2.MapContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 式言語<a href="http://commons.apache.org/proper/commons-jexl/" target="_blank">JEXL(Java Expression Language)</a>の実装。
 * <p>利用する際には、JEXL2.1のライブラリが必要です。
 *
 * @version 2.3
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class ExpressionLanguageJEXLImpl implements ExpressionLanguage {
    
    private static final Logger logger = LoggerFactory.getLogger(ExpressionLanguageJEXLImpl.class);
    
    private final JexlEngine jexlEngine;
    
    private final ObjectCache<String, Expression> expressionCache = new ObjectCache<>();
    
    public ExpressionLanguageJEXLImpl() {
        JexlEngine engine = new JexlEngine();
        engine.setSilent(true);
        this.jexlEngine = engine;
    }
    
    /**
     * {@link JexlEngine}を指定するコンストラクタ。
     * @param jexlEngine JEXLの処理エンジン。
     */
    public ExpressionLanguageJEXLImpl(final JexlEngine jexlEngine) {
        this.jexlEngine = jexlEngine;
    }
    
    @Override
    public Object evaluate(final String expression, final Map<String, Object> values) {
        
        Objects.requireNonNull(expression, "expression shoud not be null.");
        Objects.requireNonNull(values, "values shoud not be null.");
        
        if(logger.isDebugEnabled()) {
            logger.debug("Evaluating JEXL expression: {}", expression);
        }
        
        try {
            Expression expr = expressionCache.get(expression);
            if (expr == null) {
                expr = jexlEngine.createExpression(expression);
                expressionCache.put(expression, expr);
            }
            
            return expr.evaluate(new MapContext((Map<String, Object>) values));
            
        } catch(Exception ex) {
            throw new ExpressionEvaluationException(String.format("Evaluating [%s] script with JEXL failed.", expression), ex,
                    expression, values);
        }
    }
    
    /**
     * {@link JexlEngine}を取得する。
     * @return
     */
    public JexlEngine getJexlEngine() {
        return jexlEngine;
    }
    
}

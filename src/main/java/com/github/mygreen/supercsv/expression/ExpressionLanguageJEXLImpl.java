package com.github.mygreen.supercsv.expression;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.jexl3.JexlBuilder;
import org.apache.commons.jexl3.JexlEngine;
import org.apache.commons.jexl3.JexlExpression;
import org.apache.commons.jexl3.MapContext;
import org.apache.commons.jexl3.introspection.JexlPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mygreen.supercsv.util.Utils;


/**
 * 式言語<a href="http://commons.apache.org/proper/commons-jexl/" target="_blank">JEXL(Java Expression Language)</a>の実装。
 * <p>利用する際には、JEXL v3.3以上のライブラリが必要です。
 * <p>JEXL v3.3から、ELインジェクション対策として、<a href="https://commons.apache.org/proper/commons-jexl/apidocs/org/apache/commons/jexl3/introspection/JexlPermissions.html)">JexlPermissions</a>によるEL式中で参照／実行可能なクラスを制限されます。
 * <p>独自のCellProcessorなどを実装しているが場合は、システムプロパティ {@literal supercsv.annotation.jexlPermissions} で指定することができます。
 *    複数指定する場合はカンマ区切りで指定します。
 * </p>
 *
 * @version 2.4
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class ExpressionLanguageJEXLImpl implements ExpressionLanguage {
    
    private static final Logger logger = LoggerFactory.getLogger(ExpressionLanguageJEXLImpl.class);
    
    /**
     * システムプロパティ - JEXLをRESTRICTモードで使用するかどうかフラグ。
     */
    public static final String PROPERTY_JEXL_RESTRICTED = "supercsv.annotation.jexlRestricted";
    
    /**
     * システムプロパティ - JEXLをRESTRICTモードで使用する場合のパーミッションを指定する
     */
    protected static final String[] LIB_PERMISSIONS = {"com.github.mygreen.supercsv.*"};
    
    /**
     * 独自のJEXLのパーミッション。
     */
    protected static final String[] USER_PERMISSIONS;
    static {
        String value = System.getProperty("supercsv.annotation.jexlPermissions");
        if(Utils.isNotEmpty(value)) {
            USER_PERMISSIONS = Arrays.stream(value.split(","))
                    .map(String::trim)
                    .filter(Utils::isNotEmpty)
                    .collect(Collectors.toList())
                    .toArray(new String[0]);
            
        } else {
            USER_PERMISSIONS = new String[] {};
        }
    }
    
    /**
     * JEXLのキャッシュサイズ。
     * <p>キャッシュする式の個数。
     */
    private static final int CACHE_SIZE = 256;
    
    private final JexlEngine jexlEngine;
    
    /**
     * JEXLのパーミッションを指定するコンストラクタ。
     * <p>関数として{@link CustomFunctions}が登録されており、接頭語 {@literal f:}で呼び出し可能です。
     * 
     * @param userPermissions JEXLのパーミッション。
     *        詳細は、<a href="https://commons.apache.org/proper/commons-jexl/apidocs/org/apache/commons/jexl3/introspection/JexlPermissions.html)">JexlPermissions</a> を参照。
     */
    public ExpressionLanguageJEXLImpl(final String... userPermissions) {
        this(Collections.emptyMap(), true, userPermissions);
        
    }
    
    /**
     * JEXLの独自のEL関数とパーミッションを指定するコンストラクタ。
     * <p>関数として{@link CustomFunctions}が登録されており、接頭語 {@literal f:}で呼び出し可能です。
     * 
     * @param userFunctions 独自のEL関数を指定します。keyは接頭語、valueはメソッドが定義されたクラス。
     * @param userPermissions JEXLのパーミッション。
     *        詳細は、<a href="https://commons.apache.org/proper/commons-jexl/apidocs/org/apache/commons/jexl3/introspection/JexlPermissions.html)">JexlPermissions</a> を参照。
     */
    public ExpressionLanguageJEXLImpl(final Map<String, Object> userFunctions, final boolean restricted, final String... userPermissions) {
        
        this.jexlEngine = new JexlBuilder()
                .namespaces(buildNamespace(userFunctions))
                .permissions(buildPermissions(restricted, userPermissions))
                .silent(true)
                .cache(CACHE_SIZE)
                .create();
    }
    
    /**
     * {@link JexlEngine}を指定するコンストラクタ。
     * @param jexlEngine JEXLの処理エンジン。
     */
    public ExpressionLanguageJEXLImpl(final JexlEngine jexlEngine) {
        this.jexlEngine = jexlEngine;
    }
    
    /**
     * EL関数の名前空間を組み立て、独自のEL関数を登録します。
     * 
     * @param userFunctions 独自のEL関数を指定します。keyは接頭語、valueはメソッドが定義されたクラス。
     * @return EL関数の名前空間
     */
    protected Map<String, Object> buildNamespace(final Map<String, Object> userFunctions) {
        
        // EL式中で使用可能な関数の登録
        Map<String, Object> functions = new HashMap<>();
        functions.put("f", CustomFunctions.class);
        
        if (Utils.isNotEmpty(userFunctions)) {
            functions.putAll(userFunctions);
        }

        
        return functions;
    }
    
    /**
     * JEXLのパーミッションを組み立てる。
     * 
     * @param userPermissions ユーザー指定のJEXLのパーミッション。
     *        詳細は、<a href="https://commons.apache.org/proper/commons-jexl/apidocs/org/apache/commons/jexl3/introspection/JexlPermissions.html)">JexlPermissions</a> を参照。
     * @return JEXLのパーミッション
     */
    protected JexlPermissions buildPermissions(final boolean restricted, final String... userPermissions) {
        
        final JexlPermissions permissions;
        if(Utils.toBoolean(System.getProperty(PROPERTY_JEXL_RESTRICTED), restricted)) {
            /*
             * EL式で本ライブラリのクラス／メソッドのアクセスを許可する。
             * ・CustomFunctions以外にも、TextPrinterを実装している各CellProcessorでも参照するため。
             * ・JEXL3からサーバーサイド・テンプレート・インジェクション、コマンドインジェクション対策のために、
             *   許可されたクラスしか参照できなくなったため、本ライブラリをEL式から参照可能に許可する。
             */
            String[] concateedUserPermission = Utils.concat(USER_PERMISSIONS, userPermissions);
            permissions = JexlPermissions.RESTRICTED
                    .compose(Utils.concat(LIB_PERMISSIONS, concateedUserPermission));
            
        } else {
            // パーミッションによる制限を行わない。
            permissions = JexlPermissions.UNRESTRICTED;
        }
        
        return permissions;
    }
    
    @Override
    public Object evaluate(final String expression, final Map<String, Object> values) {
        
        Objects.requireNonNull(expression, "expression shoud not be null.");
        Objects.requireNonNull(values, "values shoud not be null.");
        
        if(logger.isDebugEnabled()) {
            logger.debug("Evaluating JEXL expression: {}", expression);
        }
        
        try {
            JexlExpression expr = jexlEngine.createExpression(expression);
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

package com.github.mygreen.supercsv.localization;

import java.util.Formatter;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mygreen.supercsv.expression.ExpressionEvaluationException;
import com.github.mygreen.supercsv.expression.ExpressionLanguage;
import com.github.mygreen.supercsv.expression.ExpressionLanguageJEXLImpl;
import com.github.mygreen.supercsv.util.StackUtils;

/**
 * 名前付き変数のメッセージをフォーマットするクラス。
 * <p><code>{...}</code>の場合、変数を単純に置換する。
 * <p><code>${...}</code>の場合、EL式を利用し処理する。
 * <p>文字'$', '{', '}'は特殊文字のため、<code>\</code>でエスケープを行う。
 * <p>ELのパーサは、{@link ExpressionLanguage}の実装クラスで切り替え可能。
 * <p>{@link MessageResolver}を指定した場合、メッセージ中の変数<code>{...}</code>をメッセージ定義コードとして解決する。
 *    ただし、メッセージ変数で指定されている変数が優先される。
 * 
 * @version 2.4
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class MessageInterpolator {
    
    private static final Logger logger = LoggerFactory.getLogger(MessageInterpolator.class);
    
    private ExpressionLanguage expressionLanguage;
    
    /**
     * EL式を再帰的に評価するかどうか。
     */
    private boolean recursiveForEl = false;
    
    /**
     * 変数を再帰的に評価するかどうか。
     */
    private boolean recursiveForVar = false;
    
    /**
     * 再帰処理の最大回数
     */
    private int maxRecursiveDepth = 5;
    
    /**
     * デフォルトのコンストラクタ
     * <p>式言語の処理実装として、JEXLの{@link ExpressionLanguageJEXLImpl} が設定されます。
     * 
     */
    public MessageInterpolator() {
        this.expressionLanguage = new ExpressionLanguageJEXLImpl();
    }
    
    /**
     * 式言語の実装を指定するコンストラクタ
     * @param expressionLanguage EL式を評価する実装。
     */
    public MessageInterpolator(final ExpressionLanguage expressionLanguage) {
        Objects.requireNonNull(expressionLanguage, "expressionLanguage should not be null.");
        this.expressionLanguage = expressionLanguage;
    }
    
    /**
     * メッセージを引数varsで指定した変数で補完する。
     * 
     * @param message 対象のメッセージ。
     * @param vars メッセージ中の変数に対する値のマップ。
     * @return 補完したメッセージ。
     */
    public String interpolate(final String message, final Map<String, ?> vars) {
        return interpolate(message, vars, false);
    }
    
    /**
     * メッセージを引数varsで指定した変数で補完する。
     * 
     * @param message 対象のメッセージ。
     * @param vars メッセージ中の変数に対する値のマップ。
     * @param recursive 変換したメッセージに対しても再帰的に処理するかどうか。
     * @return 補完したメッセージ。
     */
    public String interpolate(final String message, final Map<String, ?> vars, boolean recursive) {
        return parse(message, vars, recursive, 0, null);
    }
    
    /**
     * メッセージを引数varsで指定した変数で補完する。
     * <p>{@link MessageResolver}を指定した場合、メッセージ中の変数をメッセージコードとして解決します。
     * 
     * @param message 対象のメッセージ。
     * @param vars メッセージ中の変数に対する値のマップ。
     * @param recursive 変換したメッセージに対しても再帰的に処理するかどうか。
     * @param messageResolver メッセージを解決するクラス。nullの場合、指定しないと同じ意味になります。
     * @return 補完したメッセージ。
     */
    public String interpolate(final String message, final Map<String, ?> vars, boolean recursive,
            final MessageResolver messageResolver) {
        return parse(message, vars, recursive, 0, messageResolver);
    }
    
    /**
     * メッセージをパースし、変数に値を差し込み、EL式を評価する。
     * @param message 対象のメッセージ。
     * @param vars メッセージ中の変数に対する値のマップ。
     * @param recursive 変換したメッセージに対しても再帰的に処理するかどうか。
     * @param currentRecursiveDepth 現在の再帰処理回数。
     * @param messageResolver メッセージを解決するクラス。nullの場合、指定しないと同じ意味になります。
     * @return 補完したメッセージ。
     */
    protected String parse(final String message, final Map<String, ?> vars, final boolean recursive, final int currentRecursiveDepth,
            final MessageResolver messageResolver) {
        
        // 評価したメッセージを格納するバッファ。
        final StringBuilder sb = new StringBuilder(message.length());
        
        /*
         * 変数とEL式を解析する際に使用する、スタック変数。
         * 式の開始が現れたらスタックに積み、式の終了が現れたらスタックから全てを取り出す。
         * スタックに積まれるのは、1つ文の変数またはEL式。
         */
        final LinkedList<String> stack = new LinkedList<String>();
        
        final int length = message.length();
        
        for(int i=0; i < length; i++) {
            final char c = message.charAt(i);
            
            if(StackUtils.equalsTopElement(stack, "\\")) {
                // 直前の文字がエスケープ文字の場合、エスケープ文字として結合する。
                String escapedChar = StackUtils.popup(stack) + c;
                
                if(!stack.isEmpty()) {
                    // 取り出した後もスタックがある場合は、式の途中であるため、再度スタックに積む。
                    stack.push(escapedChar);
                    
                } else {
                    // 取り出した後にスタックがない場合は、エスケープを解除して通常の文字として積む。
                    sb.append(c);
                    
                }
                
            } else if(c == '\\') {
                // エスケープ文字の場合はスタックに積む。
                stack.push(String.valueOf(c));
                
            } else if(c == '$') {
                stack.push(String.valueOf(c));
                
            } else if(c == '{') {
                
                if(!stack.isEmpty() && !StackUtils.equalsAnyBottomElement(stack, new String[]{"$", "{"})) {
                    // スタックの先頭が式の開始形式でない場合
                    throw new MessageParseException(message, "expression not start with '{' or '$'");
                    
                } else {
                    stack.push(String.valueOf(c));
                }
                
                
            } else if(c == '}') {
                
                if(StackUtils.equalsAnyBottomElement(stack, new String[]{"{", "$"})) {
                    // 式の終わりの場合は、式を取り出し評価する。
                    String expression = StackUtils.popupAndConcat(stack) + c;
                    
                    // エスケープを解除する
                    expression = removeEscapeChar(expression, '\\');
                    
                    String result = evaluate(expression, vars, recursive, currentRecursiveDepth, messageResolver);
                    sb.append(result);
                    
                } else {
                    sb.append(c);
                    
                }
                
            } else {
                
                if(stack.isEmpty()) {
                    sb.append(c);
                    
                } else {
                    stack.push(String.valueOf(c));
                }
                
            }
            
        }
        
        if(!stack.isEmpty()) {
            String val = StackUtils.popupAndConcat(stack);
            val = removeEscapeChar(val, '\\');
            sb.append(val);
        }
        
        return sb.toString();
    }
    
    private String evaluate(final String expression, final Map<String, ?> values, final boolean recursive,
            final int currentRecursiveDepth, final MessageResolver messageResolver) {
        
        if(expression.startsWith("{")) {
            // 変数の置換の場合
            final String varName = expression.substring(1, expression.length()-1);
            
            if(values.containsKey(varName)) {
                // 該当するキーが存在する場合
                final Object value = values.get(varName);
                final String eval = (value == null) ? "" : value.toString();
                if(!eval.isEmpty() && recursivable(recursive && recursiveForVar, maxRecursiveDepth, currentRecursiveDepth, eval)) {
                    return parse(eval, values, recursive, currentRecursiveDepth + 1, messageResolver);
                } else {
                    return eval;
                }
                
            } else if(messageResolver != null) {
                // メッセージコードをとして解決をする。
                final Optional<String> eval = messageResolver.getMessage(varName);
                if(!eval.isPresent()) {
                    // 該当するキーが存在しない場合は、値をそのまま返す。
                    return String.format("{%s}", varName);
                }
                
                if(recursivable(recursive, maxRecursiveDepth, currentRecursiveDepth, eval.get())) {
                    return parse(eval.get(), values, recursive, currentRecursiveDepth + 1, messageResolver);
                } else {
                    return eval.get();
                }
                
            } else {
                // 該当するキーが存在しない場合は、値をそのまま返す。
                return expression.toString();
            }
            
        } else if(expression.startsWith("${")) {
            // EL式で処理する
            final String expr = expression.substring(2, expression.length()-1);
            final String eval = evaluateExpression(expr, values);
            if(recursive && recursivable(recursive && recursiveForEl, maxRecursiveDepth, currentRecursiveDepth, eval)) {
                return parse(eval, values, recursive,currentRecursiveDepth + 1, messageResolver);
            } else {
                return eval;
            }
            
        }
        
        throw new MessageParseException(expression, "not support expression.");
        
    }
    
    /**
     * 現在の再帰回数が最大回数に達しているかどうか。
     * 
     * @param recursive 再帰的に処理するかどうか。
     * @param maxRecursion 最大再帰回数
     * @param currentDepth 再帰回数
     * @param expression 再帰対象のメッセージ
     * @return 最大再帰回数を超えていなければfalseを返す。
     */
    private boolean recursivable(final boolean recursive, final int maxRecursion, final int currentDepth,
            String message) {

        if(!recursive) {
            return false;
        }

        if(maxRecursion <= 0) {
            // 再帰回数の制限なし。
            return true;
        }

        if(currentDepth <= maxRecursion) {
            return true;
        }

        logger.warn("Over recursive depth : currentDepth={}, maxDepth={}, message={}.", currentDepth, maxRecursion, message);

        return false;

    }
    
    /**
     * EL式を評価する。
     * @param expression EL式
     * @param values EL式中の変数。
     * @return 評価した式。
     * @throws ExpressionEvaluationException 
     */
    protected String evaluateExpression(final String expression, final Map<String, ?> values) throws ExpressionEvaluationException {
        
        final Map<String, Object> context = new LinkedHashMap<String, Object>();
        context.putAll(values);
        
        // フォーマッターの追加
        context.computeIfAbsent("formatter", key -> new Formatter());
        
        /*
         * 以下のケースの時、評価値はnullが返されるため、空文字に変換する。
         * ・JEXLで存在しない変数名のとき。
         * ・ELインジェクション対象の式のとき
         */
        final String evalValue = Objects.toString(expressionLanguage.evaluate(expression, context), "");
        if(logger.isTraceEnabled()) {
            logger.trace("evaluate expression language: expression='{}' ===> value='{}'", expression, evalValue);
        }
        
        return evalValue;
    }
    
    /**
     * エスケープ文字を除去した文字列を取得する。
     * @param str
     * @param escapeChar
     * @return
     */
    private String removeEscapeChar(final String str, final char escapeChar) {
        
        if(str == null || str.isEmpty()) {
            return str;
        }
        
        final String escapeStr = String.valueOf(escapeChar);
        StringBuilder sb = new StringBuilder();
        
        final LinkedList<String> stack = new LinkedList<>();
        
        final int length = str.length();
        for(int i=0; i < length; i++) {
            final char c = str.charAt(i);
            
            if(StackUtils.equalsTopElement(stack, escapeStr)) {
                // スタックの一番上がエスケープ文字の場合
                StackUtils.popup(stack);
                sb.append(c);
                
            } else if(c == escapeChar) {
                // スタックに積む
                stack.push(String.valueOf(c));
                
            } else {
                sb.append(c);
            }
            
        }
        
        if(!stack.isEmpty()) {
            sb.append(StackUtils.popupAndConcat(stack));
        }
        
        return sb.toString();
        
    }
    
    /**
     * EL式を解析する実装クラスを取得する。
     * @return
     */
    public ExpressionLanguage getExpressionLanguage() {
        return expressionLanguage;
    }
    
    /**
     * EL式を解析する実装クラスを設定する。
     * @param expressionLanguage EL式の解析するクラスの実装。
     */
    public void setExpressionLanguage(ExpressionLanguage expressionLanguage) {
        this.expressionLanguage = expressionLanguage;
    }

    /**
     * EL式を再帰的に評価するかどうか判定する。
     * 
     * @since 2.4
     * @return {@literal true}のとき再帰的に処理しない。
     */
    public boolean isRecursiveForEl() {
        return recursiveForEl;
    }
    
    /**
     * EL式を再帰的に評価するかどうか設定する。
     * <p>入力値などの任意の値がEL式(<code>${...}</code>)の形式の場合、
     *    不要に再帰的に評価されてELインジェクションが発生してしまうのを防ぐために設定します。
     * <p>デフォルト値は、{@literal false} と再帰的に評価しない。
     * 
     * @since 2.4
     * @param recursiveForEl {@literal true}のとき再帰的に処理しない。
     */
    public void setRecursiveForEl(boolean recursiveForEl) {
        this.recursiveForEl = recursiveForEl;
    }
    
    /**
     * 変数を再帰的に評価するかどうか判定する。
     * 
     * @since 2.4
     * @return {@literal true}のとき再帰的評価する。
     */
    public boolean isRecursiveForVar() {
        return recursiveForVar;
    }
    
    /**
     * リソースファイルのキーを除く変数を再帰的に処理しないかどうか設定する。
     * <p>入力値などの任意の値が変数(<code>{...}</code>)の形式の場合、
     *   不要に再帰的に評価されてELインジェクションが発生してしまうのを防ぐために設定します。
     * <p>デフォルト値は、{@literal false} と再帰的に評価しない。
     * 
     * @since 2.4
     * @param recursiveForVar {@literal true}のとき再帰的評価する。
     */
    public void setRecursiveForVar(boolean recursiveForVar) {
        this.recursiveForVar = recursiveForVar;
    }
    
    /**
     * 評価した変数やEL式を再帰的に処するときの最大回数を取得します。
     * 
     * @since 2.4
     * @return 再帰的に処するときの最大回数。
     */
    public int getMaxRecursiveDepth() {
        return maxRecursiveDepth;
    }
    
    /**
     * 評価した変数やEL式を再帰的に処するときの最大回数を設定します。
     * 
     * @since 2.4
     * @param maxRecursiveDepth 再帰的に処するときの最大回数。{@literal -1} のとき制限はありません。
     */
    public void setMaxRecursiveDepth(int maxRecursiveDepth) {
        this.maxRecursiveDepth = maxRecursiveDepth;
    }
}

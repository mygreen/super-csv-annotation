package com.github.mygreen.supercsv.cellprocessor.format;

import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;
import org.supercsv.util.CsvContext;

import com.github.mygreen.supercsv.cellprocessor.ValidationCellProcessor;
import com.github.mygreen.supercsv.util.Utils;

/**
 * 文字列を解析して、各オブジェクト型に変換するCellProcessor。
 * <p>各オブジェクトに実装された{@link TextParser}で処理を行う。</p>
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class ParseProcessor<T> extends ValidationCellProcessor implements StringCellProcessor {
    
    private final Class<T> type;
    
    private final TextParser<T> parser;
    
    public ParseProcessor(final Class<T> type, final TextParser<T> parser) {
        super();
        checkPreconditions(type, parser);
        this.type = type;
        this.parser = parser;
    }
    
    public ParseProcessor(final Class<T> type, final TextParser<T> parser, final CellProcessor next) {
        super(next);
        checkPreconditions(type, parser);
        this.type = type;
        this.parser = parser;
    }
    
    /**
     * コンスタによるインスタンスを生成する際の前提条件となる引数のチェックを行う。
     * @throws NullPointerException type or parser is null.
     * 
     */
    private static <T> void checkPreconditions(final Class<T> type, final TextParser<T> parser) {
        if(type == null) {
            throw new NullPointerException("type is null.");
        }
        
        if(parser == null) {
            throw new NullPointerException("parser is null.");
        }
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public Object execute(final Object value, final CsvContext context) {
        
        final String text = (String)value;
        if(Utils.isEmpty(text)) {
            if(type.isPrimitive()) {
                // プリミティブ型の場合
                return next.execute(Utils.getPrimitiveDefaultValue(type), context);
                
            } else if(!String.class.isAssignableFrom(type)) {
                // 文字列型以外のオブジェクト
                return next.execute(null, context);
                
            }
        }
        
        try {
            final T result = parser.parse((String) value);
            return next.execute(result, context);
            
        } catch(TextParseException e) {
            throw createValidationException(context)
                .messageFormat("'%s' could not parse to %s.", value, getType().getName())
                .exception(e)
                .rejectedValue(value)
                .validationMessageIfPresent(parser.getValidationMessage())
                .messageVariables(parser.getMessageVariables())
                .parsedError(true)
                .build();
        }
    }
    
    /**
     * 変換対象のクラスタイプを取得します。
     * @return コンストラクタで渡したクラスタイプ。
     */
    public Class<T> getType() {
        return type;
    }
    
    /**
     * 設定されている文字列のパーサを取得します。
     * @return コンストラクタで渡したパーサ。
     */
    public TextParser<T> getParser() {
        return parser;
    }
    
}

package com.github.mygreen.supercsv.cellprocessor.conversion;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;
import org.supercsv.util.CsvContext;

/**
 * 値が比較対象の値と一致する場合、nullに変換する。
 * <p>nullに変換した後も処理を続行する。</p>
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class NullConvert extends CellProcessorAdaptor implements StringCellProcessor {
    
    private final Set<String> tokens = new HashSet<>();
    
    private final boolean ignoreCase;
    
    /**
     * 
     * @param tokens 比較対象の値
     * @param ignoreCase 大文字・小文字の区別を行うかどうか。
     * @throws NullPointerException {@literal tokens is null.}
     * @throws IllegalArgumentException {@literal tokens size is zero.}
     */
    public NullConvert(final Collection<String> tokens, final boolean ignoreCase) {
        super();
        checkPreconditions(tokens);
        this.tokens.addAll(toIgnoreCase(tokens, ignoreCase));
        this.ignoreCase = ignoreCase;
    }
    
    /**
     * 
     * @param tokens 比較対象の値
     * @param ignoreCase 大文字・小文字の区別を行うかどうか
     * @param next チェインとして次に実行されるCellProcessor
     * @throws NullPointerException {@literal tokens is null.}
     * @throws IllegalArgumentException {@literal tokens size is zero.}
     */
    public NullConvert(final Collection<String> tokens, final boolean ignoreCase, final StringCellProcessor next) {
        super(next);
        checkPreconditions(tokens);
        this.tokens.addAll(toIgnoreCase(tokens, ignoreCase));
        this.ignoreCase = ignoreCase;
    }
    
    private static void checkPreconditions(final Collection<String> tokens) {
        if(tokens == null) {
            throw new NullPointerException("tokens should not be null.");
            
        } else if(tokens.isEmpty()) {
            throw new IllegalArgumentException("tokens should not be empty.");
        }
    }
    
    private static Collection<String> toIgnoreCase(final Collection<String> value, boolean ignoreCase) {
        if(!ignoreCase) {
            return value;
        }
        
        return value.stream()
                .map(v -> v.toLowerCase())
                .collect(Collectors.toList());
    }
    
    private static String toIgnoreCase(final String value, boolean ignoreCase) {
        if(!ignoreCase) {
            return value;
        }
        
        return value.toLowerCase();
    }
    
    @Override
    public <T> T execute(final Object value, final CsvContext context) {
        
        if(value == null) {
            return next.execute(value, context);
        }
        
        final String str = toIgnoreCase(value.toString(), ignoreCase);
        if(tokens.contains(str)) {
            return next.execute(null, context);
        }
        
        return next.execute(value, context);
    }
    
    /**
     * 比較対象の値
     * @return ignoreCase=trueの場合、小文字に変換している。
     */
    public Set<String> getTokens() {
        return tokens;
    }
    
    /**
     * 大文字・小文字の区別を行うかどうか。
     * @return
     */
    public boolean isIgnoreCase() {
        return ignoreCase;
    }
    
}

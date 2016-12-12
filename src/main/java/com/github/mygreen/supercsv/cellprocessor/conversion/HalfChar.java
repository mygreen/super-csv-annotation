package com.github.mygreen.supercsv.cellprocessor.conversion;

import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;
import org.supercsv.util.CsvContext;

/**
 * 全角を半角に変換するCellProcessor。
 * 
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class HalfChar extends CellProcessorAdaptor implements StringCellProcessor {
    
    private final CharCategory[] categories;
    
    private final JapaneseCharReplacer replacer;
    
    public HalfChar(final CharCategory[] categories) {
        super();
        checkPreconditions(categories);
        this.categories = categories;
        this.replacer = new JapaneseCharReplacer(categories);
    }
    
    public HalfChar(final CharCategory[] categories, final StringCellProcessor next) {
        super(next);
        checkPreconditions(categories);
        this.categories = categories;
        this.replacer = new JapaneseCharReplacer(categories);
    }
    
    private static void checkPreconditions(final CharCategory[] categories) {
        if(categories == null) {
            throw new NullPointerException("categories should not be null.");
            
        } else if(categories.length == 0) {
            throw new IllegalArgumentException("categories should not be empty.");
        }
    }
    
    @Override
    public <T> T execute(final Object value, final CsvContext context) {
        
        if(value == null) {
            return next.execute(value, context);
        }
        
        final String result = replacer.replaceToHalfChar(value.toString());
        return next.execute(result, context);
    }
    
    /**
     * 変換対象の文字の種類を取得します。
     * @return コンストラクタで渡した種類です。
     */
    public CharCategory[] getCategories() {
        return categories;
    }
    
}

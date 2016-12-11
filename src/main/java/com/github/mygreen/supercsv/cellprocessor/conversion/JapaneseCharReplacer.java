package com.github.mygreen.supercsv.cellprocessor.conversion;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.github.mygreen.supercsv.util.Utils;

/**
 * 日本語の全角・半角の文字を置換する。
 * 
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class JapaneseCharReplacer {
    
    private static final Map<CharCategory, String[][]> CHAR_MAPS;
    static {
        final Map<CharCategory, String[][]> map = new HashMap<>();
        
        map.put(CharCategory.Number, new String[][]{
            {"0", "０"},
            {"1", "１"},
            {"2", "２"},
            {"3", "３"},
            {"4", "４"},
            {"5", "５"},
            {"6", "６"},
            {"7", "７"},
            {"8", "８"},
            {"9", "９"},
        });
        
        map.put(CharCategory.Alpha, new String[][]{
            {"a", "ａ"},
            {"b", "ｂ"},
            {"c", "ｃ"},
            {"d", "ｄ"},
            {"e", "ｅ"},
            {"f", "ｆ"},
            {"g", "ｇ"},
            {"h", "ｈ"},
            {"i", "ｉ"},
            {"j", "ｊ"},
            {"k", "ｋ"},
            {"l", "ｌ"},
            {"m", "ｍ"},
            {"n", "ｎ"},
            {"o", "ｏ"},
            {"p", "ｐ"},
            {"q", "ｑ"},
            {"r", "ｒ"},
            {"s", "ｓ"},
            {"t", "ｔ"},
            {"u", "ｕ"},
            {"v", "ｖ"},
            {"w", "ｗ"},
            {"x", "ｘ"},
            {"y", "ｙ"},
            {"z", "ｚ"},
            
            {"A", "Ａ"},
            {"B", "Ｂ"},
            {"C", "Ｃ"},
            {"D", "Ｄ"},
            {"E", "Ｅ"},
            {"F", "Ｆ"},
            {"G", "Ｇ"},
            {"H", "Ｈ"},
            {"I", "Ｉ"},
            {"J", "Ｊ"},
            {"K", "Ｋ"},
            {"L", "Ｌ"},
            {"M", "Ｍ"},
            {"N", "Ｎ"},
            {"O", "Ｏ"},
            {"P", "Ｐ"},
            {"Q", "Ｑ"},
            {"R", "Ｒ"},
            {"S", "Ｓ"},
            {"T", "Ｔ"},
            {"U", "Ｕ"},
            {"V", "Ｖ"},
            {"W", "Ｗ"},
            {"X", "Ｘ"},
            {"Y", "Ｙ"},
            {"Z", "Ｚ"},
            
        });
        
        map.put(CharCategory.Space, new String[][]{
            {" ", "　"},
            
        });
        
        map.put(CharCategory.Symbol, new String[][]{
            {"!", "！"},
            {"\"", "”"},
            {"#", "＃"},
            {"$", "＄"},
            {"%", "％"},
            {"&", "＆"},
            {"'", "’"},
            {"(", "（"},
            {")", "）"},
            {"=", "＝"},
            {"^", "＾"},
            {"~", "～"},
            {"|", "｜"},
            {"\\", "￥"},
            {"`", "‘"},
            {"@", "＠"},
            {"[", "［"},
            {"]", "］"},
            {"{", "｛"},
            {"}", "｝"},
            {"+", "＋"},
            {"-", "ー"},
            {";", "；"},
            {":", "："},
            {"*", "＊"},
            {"<", "＜"},
            {">", "＞"},
            {",", "，"},
            {".", "．"},
            {"?", "？"},
            {"/", "／"},
            {"_", "＿"},
            
        });
        
        map.put(CharCategory.Katakana, new String[][] {
            
            {"ｱ", "ア"},
            {"ｲ", "イ"},
            {"ｳ", "ウ"},
            {"ｴ", "エ"},
            {"ｵ", "オ"},
            
            {"ｶ", "カ"},
            {"ｷ", "キ"},
            {"ｸ", "ク"},
            {"ｹ", "ケ"},
            {"ｺ", "コ"},
            
            {"ｻ", "サ"},
            {"ｼ", "シ"},
            {"ｽ", "ス"},
            {"ｾ", "セ"},
            {"ｿ", "ソ"},
            
            {"ﾀ", "タ"},
            {"ﾁ", "チ"},
            {"ﾂ", "ツ"},
            {"ﾃ", "テ"},
            {"ﾄ", "ト"},
            
            {"ﾅ", "ナ"},
            {"ﾆ", "ニ"},
            {"ﾇ", "ヌ"},
            {"ﾈ", "ネ"},
            {"ﾉ", "ノ"},
            
            {"ﾊ", "ハ"},
            {"ﾋ", "ヒ"},
            {"ﾌ", "フ"},
            {"ﾍ", "ヘ"},
            {"ﾎ", "ホ"},
            
            {"ﾏ", "マ"},
            {"ﾐ", "ミ"},
            {"ﾑ", "ム"},
            {"ﾒ", "メ"},
            {"ﾓ", "モ"},
            
            {"ﾔ", "ヤ"},
            {"ﾕ", "ユ"},
            {"ﾖ", "ヨ"},
            
            {"ﾗ", "ラ"},
            {"ﾘ", "リ"},
            {"ﾙ", "ル"},
            {"ﾚ", "レ"},
            {"ﾛ", "ロ"},
            
            {"ﾜ", "ワ"},
            {"ｦ", "ヲ"},
            
            {"ﾝ", "ン"},
            
            {"ｳﾞ", "ヴ"},
            
            {"ﾊﾟ", "パ"},
            {"ﾋﾟ", "ピ"},
            {"ﾌﾟ", "プ"},
            {"ﾍﾟ", "ペ"},
            {"ﾎﾟ", "ポ"},
            
            {"ﾊﾞ", "バ"},
            {"ﾋﾞ", "ビ"},
            {"ﾌﾞ", "ブ"},
            {"ﾍﾞ", "ベ"},
            {"ﾎﾞ", "ボ"},
            
            {"ｧ", "ァ"},
            {"ｨ", "ィ"},
            {"ｩ", "ゥ"},
            {"ｪ", "ェ"},
            {"ｫ", "ォ"},
            
            {"ｯ", "ッ"}
            
        });
        
        CHAR_MAPS = Collections.unmodifiableMap(map);
        
    }
    
    /** 全角文字への置換処理 */
    private final CharReplacer fullCharReplacer = new CharReplacer();
    
    /** 半角文字への置換処理 */
    private final CharReplacer halfCharReplacer = new CharReplacer();
    
    public JapaneseCharReplacer(final Collection<CharCategory> categories) {
        
        Set<CharCategory> categorySet = new HashSet<>(categories);
        for(CharCategory category : categorySet) {
            final String[][] charMap = CHAR_MAPS.get(category);
            for(String[] map : charMap) {
                fullCharReplacer.register(map[0], map[1]);
                halfCharReplacer.register(map[1], map[0]);
            }
        }
        
        fullCharReplacer.ready();
        halfCharReplacer.ready();
        
    }
    
    public JapaneseCharReplacer(final CharCategory... categories) {
        this(Arrays.asList(categories));
        
    }
    
    /**
     * 半角を全角に変換する。
     * @param text 変換対象の文字列。
     * @return 変換後の値。変換対象の値がnullまたは空文字の場合は、そのまま返します。
     */
    public String replaceToFullChar(final String text) {
        if(Utils.isEmpty(text)) {
            return text;
        }
        
        return fullCharReplacer.replace(text);
    }
    
    /**
     * 全角を半角に変換する。
     * @param text 変換対象の文字列。
     * @return 変換後の値。変換対象の値がnullまたは空文字の場合は、そのまま返します。
     */
    public String replaceToHalfChar(final String text) {
        
        if(Utils.isEmpty(text)) {
            return text;
        }
        
        return halfCharReplacer.replace(text);
        
    }
    
    
}

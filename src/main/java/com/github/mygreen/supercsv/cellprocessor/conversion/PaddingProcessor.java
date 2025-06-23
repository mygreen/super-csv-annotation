package com.github.mygreen.supercsv.cellprocessor.conversion;

/**
 * 文字列をパディングする処理のインタフェース。
 *
 * @since 2.1
 * @author T.TSUCHIE
 *
 */
public interface PaddingProcessor {

    /**
     * テキストをパディングする
     * @param text パディング対象の文字
     * @param size サイズ
     * @param padChar パディングする文字。
     * @param rightAlign 右詰めするかどうか。
     * @param chopped 処理対象の文字が固定長を超えている場合に、切り出すかどうか。
     * @return パディングされた文字列
     */
    String pad(String text, int size, char padChar, boolean rightAlign, boolean chopped);

    /**
     * 引数で指定した1文字分のコードポイントの文字数をカウントします。
     * 
     * @param codePoint コードポイント。
     * @return カウントした文字数。
     */
    int count(int codePoint);
    
    /**
     * 文字列の文字数をカウントする。
     * 
     * @param text カウント対象の文字列
     * @return カウントした文字数
     * @throws NullPointerException {@literal text is null.}
     */
    int count(String text);

}

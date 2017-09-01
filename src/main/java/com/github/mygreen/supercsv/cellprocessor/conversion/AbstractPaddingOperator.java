package com.github.mygreen.supercsv.cellprocessor.conversion;

/**
 * パディング処理の抽象クラス。
 * <p>サロゲートペアを考慮します。</p>
 *
 * @since 2.1
 * @author T.TSUCHIE
 *
 */
public abstract class AbstractPaddingOperator implements PaddingProcessor {

    /**
     * コードポイントの配列に変換する。
     * @param text
     * @return
     */
    private int[] toCodePointArray(final String text) {
        final int length = text.length();
        final int codePointLength = text.codePointCount(0, length);

        int[] array = new int[codePointLength];
        for(int i=0, j=0, codePoint=0; i < length; j++, i+=Character.charCount(codePoint)) {
            codePoint = text.codePointAt(i);
            array[j] = codePoint;
        }

        return array;
    }

    @Override
    public String pad(final String text, final int size, final char padChar, final boolean rightAlign, final boolean chopped) {

        final int currentSize = count(text);
        final int padCharSize = count(String.valueOf(padChar));
        final int[] codePointArray = toCodePointArray(text);
        final int codePointSize = codePointArray.length;

        if(rightAlign) {
            // 右詰
            if(currentSize == size) {
                return text;

            } else if(currentSize > size) {
                // 指定した長さを超える場合
                if(chopped) {
                    // 切り出す場合

                    /*
                     * 左端から長さをカウントしていき、オーバした長さ分を切り取る
                     */
                    final int overLength = currentSize - size;
                    for(int i=0, chopLength=0; i < codePointSize; i++) {
                        final int codePoint = codePointArray[i];
                        chopLength += count(codePoint);
                        if(chopLength >= overLength) {
                            // substring(i+1)
                            String chopText = new String(codePointArray, i+1, codePointSize-(i+1));

                            // 切り取った後の再調整。
                            // パディング文字が全角の場合は、余分に切り取る場合があるため。
                            return pad(chopText, size, padChar, rightAlign, chopped);
                        }

                    }

                    // 全て切り出す場合
                    return "";

                } else {
                    // 切り出さない場合
                    return text;
                }
            } else {
                // 指定したサイズより少ない場合

                /*
                 * パディング文字を付与していく
                 * ・パディング文字が全角の時があり、長さが2以上になるので注意する
                 */
                final int lackLength = size - currentSize;
                int padCount = lackLength / padCharSize;

                StringBuilder appender = new StringBuilder();
                for(int i=0; i < padCount; i++) {
                    appender.append(padChar);
                }

                appender.append(text);

                return appender.toString();

            }
        } else {
            // 左詰

            if(currentSize == size) {
                return text;

            } else if(currentSize > size) {
                // 指定したサイズを超える場合
                if(chopped) {
                    // 切り出す場合

                    /*
                     * 右端から長さをカウントしていき、オーバした長さ分を切り取る
                     */
                    final int overLength = currentSize - size;
                    for(int i=codePointSize-1, chopLength=0; i >= 0; i--) {
                        final int codePoint = codePointArray[i];
                        chopLength += count(codePoint);
                        if(chopLength >= overLength) {
                            // substring(0, i)
                            String chopText = new String(codePointArray, 0, i);

                            // 切り取った後の再調整。
                            // パディング文字が全角の場合は、余分に切り取る場合があるため。
                            return pad(chopText, size, padChar, rightAlign, chopped);
                        }

                    }


                    // 全て切り出す場合
                    return "";

                } else {
                    // 切り出さない場合
                    return text;
                }
            } else {
                // 指定したサイズより少ない場合

                /*
                 * パディング文字を付与していく
                 * ・パディング文字が全角の時があり、長さが2以上になるので注意する
                 */
                final int lackLength = size - currentSize;
                int padCount = lackLength / padCharSize;

                StringBuilder appender = new StringBuilder(text);
                for(int i=0; i < padCount; i++) {
                    appender.append(padChar);
                }

                return appender.toString();
            }

        }

    }

    /**
     * 文字数をカウントする
     * @param codePoint カウント対象のコードポイント
     * @return 文字数
     */
    protected abstract int count(int codePoint);

    /**
     * 文字数をカウントする。
     * @param text カウント対象の文字列
     * @return 文字数
     * @throws NullPointerException {@literal text is null.}
     */
    protected abstract int count(String text);


}

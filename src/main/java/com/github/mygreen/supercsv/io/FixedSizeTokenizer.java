package com.github.mygreen.supercsv.io;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

import org.supercsv.comment.CommentMatcher;
import org.supercsv.io.AbstractTokenizer;
import org.supercsv.prefs.CsvPreference;
import org.supercsv.util.CsvContext;

import com.github.mygreen.supercsv.builder.BeanMapping;
import com.github.mygreen.supercsv.builder.ColumnMapping;
import com.github.mygreen.supercsv.builder.FixedSizeColumnProperty;
import com.github.mygreen.supercsv.exception.SuperCsvFixedSizeException;
import com.github.mygreen.supercsv.util.Utils;

/**
 * 固定長の行をカラムに分解するTokenizer。
 *
 * @since 2.5
 * @author T.TSUCHIE
 *
 */
public class FixedSizeTokenizer extends AbstractTokenizer {

    /** 現在の行 */
    private final StringBuilder currentRow = new StringBuilder();

    /** 空行を無視するかどうか。(CsvPreferenceで設定) */
    private final boolean ignoreEmptyLines;

    /** コメント行判定。指定しない場合はnull。(CsvPreferenceで設定) */
    private final CommentMatcher commentMatcher;

    /** カラム情報(固定長定義) */
    private final List<ColumnMapping> columnMappings;

    public FixedSizeTokenizer(Reader reader, CsvPreference preferences, BeanMapping<?> beanMapping) {
        super(reader, preferences);

        if (beanMapping.getColumns().isEmpty()) {
            throw new IllegalArgumentException("columnMappings should not be empty.");
        }

        this.ignoreEmptyLines = preferences.isIgnoreEmptyLines();
        this.commentMatcher = preferences.getCommentMatcher();
        this.columnMappings = beanMapping.getColumns();
    }

    /**
     * {@inheritDoc}
     * 
     * @throws NullPointerException {@literal if columns is null.}
     */
    @Override
    public boolean readColumns(final List<String> columns) throws IOException {

        if( columns == null ) {
            throw new NullPointerException("columns should not be null");
        }

        columns.clear();

        // 空行、コメント行の読み飛ばし
        String line;
        do {
            line = readLine();
            if( line == null ) {
                return false; // EOF
            }
        }
        while( ignoreEmptyLines && line.length() == 0 || (commentMatcher != null && commentMatcher.isComment(line)) );

        // update the untokenized CSV row
        currentRow.append(line);

        final int[] codePointArray = Utils.toCodePointArray(line);

        int pos = 0;
        for (ColumnMapping columnMapping : columnMappings) {
            
            if (pos >= codePointArray.length) {
                // 文字数が不足している場合
                break;
            }
            
            StringBuilder text = new StringBuilder();
            int lastPos = lastPosition(pos, codePointArray, text, columnMapping);

            /*
             * 固定長の場合、エスケープ文字や途中改行などは対応しない。
             * ・ライブラリ側でエスケープ文字を挿入すると文字数が変わり、固定長をオーバーしてしまうため。
             * ・エスケープは、使用者側で行う。
             */
            columns.add(text.toString());

            pos = lastPos;
        }
        
        // 文字列が余る場合は、最後のカラムとして読み込む。
        if (pos < codePointArray.length) {
            columns.add(new String(codePointArray, pos, codePointArray.length - pos));
        }

        // process each character in the line
        return true;
    }

    /**
     * カラム定義に従いカラムとして切り出し、最後の位置として引数 {@literal codePointArray} の配列のインデックスを取得する。
     * 
     * @param start カラムの開始位置。
     * @param codePointArray 取得対象のCode Pointの配列。
     * @param column カラムとして切り出した文字列を格納する。
     * @param columnProperty 固定長カラムの定義情報。
     * @return カラムの最後の位置としての引数 {@literal codePointArray} のインデックスを返す。
     * @throws SuperCsvFixedSizeInsufficientException カラムのサイズが不足している場合。
     */
    private int lastPosition(final int start, int[] codePointArray, StringBuilder column, ColumnMapping columnMapping) {

        int pos = start;
        int actualSize = 0;
        final int arrayLength = codePointArray.length;
        final FixedSizeColumnProperty fixedSizeColumnProperty = columnMapping.getFixedSizeProperty();
        while(pos < arrayLength && actualSize < fixedSizeColumnProperty.getSize()) {
            actualSize += fixedSizeColumnProperty.getPaddingProcessor().count(codePointArray[pos]);
            pos++;
        }
        
        if (actualSize < fixedSizeColumnProperty.getSize()) {
            // カラムサイズに対して文字数が不足している場合は例外をスロー。
            // rowNumberはここでは取得できないので仮値0を設定し、CsvReader側で値を補完する。
            throw new SuperCsvFixedSizeException.Builder("csvError.fixedSizeInsufficient", new CsvContext(getLineNumber(), 0, columnMapping.getNumber()))
                    .messageFormat("Insufficient column size. fixedColumnSize: %d, actualSize: %d",
                            fixedSizeColumnProperty.getSize(), actualSize)
                    .messageVariables("fixedColumnSize", fixedSizeColumnProperty.getSize())
                    .messageVariables("actualSize", actualSize)
                .build();
        }

        column.append(new String(codePointArray, start, pos - start));

        return pos;

    }

    @Override
    public String getUntokenizedRow() {
        return currentRow.toString();
    }
}

package com.github.mygreen.supercsv.io;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.Objects;

import org.supercsv.encoder.CsvEncoder;
import org.supercsv.exception.SuperCsvException;
import org.supercsv.prefs.CsvPreference;
import org.supercsv.util.CsvContext;

import com.github.mygreen.supercsv.exception.SuperCsvBindingException;

/**
 * アノテーションを元に固定長のCSVファイルを読み込むためのクラス。
 *
 * @since 2.5
 * @param <T> マッピング対象のBeanのクラスタイプ
 * @author T.TSUCHIE
 *
 */
public class FixedSizeCsvAnnotationBeanWriter<T> extends AbstractCsvAnnotationBeanWriter<T> {

    private final Writer writer;
    
    private final CsvPreference preference;
    
    private final CsvEncoder encoder;
    
    /**
     * ファイルに実際に書き込んだ行番号
     * <p>ヘッダー行を含む値。</p>
     * <p>本クラスで更新するため、AbstractCsvWriter とは別の値となります。</p>
     */
    private int lineNumber = 0;
    
    /**
     * 論理的な行番号。
     * <p>ヘッダー行を含まない値。</p>
     * <p>本クラスで更新するため、AbstractCsvWriter とは別の値となります。</p>
     */
    private int rowNumber = 0;
    
    /**
     * 書き込んだ列番号。
     * <p>1から始まり、行を書き込むごとにリセットされます。</p>
     * <p>本クラスで更新するため、AbstractCsvWriter とは別の値となります。</p>
     */
    private int columnNumber = 0;
    
    public FixedSizeCsvAnnotationBeanWriter(final Writer writer, final FixedSizeCsvPreference<T> preference) {
        super(writer, preference.getCsvPreference());
        
        this.writer = writer;
        this.preference = preference.getCsvPreference();
        this.encoder = this.preference.getEncoder();
        
        this.beanMappingCache = preference.getBeanMappingCache();
        this.validators.addAll(beanMappingCache.getOriginal().getValidators());
    }
    
    /**
     * ヘッダー情報を書き込みます。
     * <p>ただし、列番号を省略され、定義がされていないカラムは、{@literal column[カラム番号]}の形式となります。</p>
     * @throws IOException ファイルの出力に失敗した場合。
     */
    public void writeHeader() throws IOException {
        writeHeader(getDefinedHeader());
    }
    
    @Override
    protected void writeRow(final String... columns) throws IOException {
        
        if( columns == null ) {
            throw new NullPointerException(String.format("columns to write should not be null on line %d", lineNumber));
        } else if( columns.length == 0 ) {
            throw new IllegalArgumentException(String.format("columns to write should not be empty on line %d", lineNumber));
        }
        
        StringBuilder builder = new StringBuilder();
        for( int i = 0; i < columns.length; i++ ) {
            
            this.columnNumber = i + 1; // column no used by CsvEncoder
            
            // 区切り文字はなく、カラムを出力する。
            final String csvElement = columns[i];
            if (csvElement != null) {
                try {
                    final CsvContext context = new CsvContext(lineNumber, rowNumber, columnNumber);
                    final String escapedCsv = encoder.encode(csvElement, context, preference);
                    builder.append(escapedCsv);
                    this.lineNumber = context.getLineNumber(); // line number can increment when encoding multi-line columns
                } catch(SuperCsvException e) {
                    errorMessages.addAll(exceptionConverter.convertAndFormat(e, beanMappingCache.getOriginal()));
                    throw e;
                }
            }
            
        }
        
        builder.append(preference.getEndOfLineSymbols()); // EOL
        writer.write(builder.toString());
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void incrementRowAndLineNo() {
        lineNumber++;
        rowNumber++;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int getLineNumber() {
        return lineNumber;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int getRowNumber() {
        return rowNumber;
    }
    
    /**
     * レコードのデータを全て書き込みます。
     * <p>ヘッダー行も自動的に処理されます。2回目以降に呼び出した場合、ヘッダー情報は書き込まれません。</p>
     * <p>レコード処理中に例外が発生した場合、その時点で処理を終了します。</p>
     * 
     * @param sources 書き込むレコードのデータ。
     * @throws NullPointerException sources is null.
     * @throws IOException レコードの出力に失敗した場合。
     * @throws SuperCsvBindingException セルの値に問題がある場合
     * @throws SuperCsvException 設定など、その他に問題がある場合
     * 
     */
    public void writeAll(final Collection<T> sources) throws IOException {
        writeAll(sources, false);
    }
    
    /**
     * レコードのデータを全て書き込みます。
     * <p>ヘッダー行も自動的に処理されます。2回目以降に呼び出した場合、ヘッダー情報は書き込まれません。</p>
     * 
     * @param sources 書き込むレコードのデータ。
     * @param continueOnError continueOnError レコードの処理中に、
     *        例外{@link SuperCsvBindingException}が発生しても、続行するかどうか指定します。
     *        trueの場合、例外が発生しても、次の処理を行います。
     * @throws NullPointerException sources is null.
     * @throws IOException レコードの出力に失敗した場合。
     * @throws SuperCsvBindingException セルの値に問題がある場合
     * @throws SuperCsvException 設定など、その他に問題がある場合
     * 
     */
    public void writeAll(final Collection<T> sources, final boolean continueOnError) throws IOException {
        
        Objects.requireNonNull(sources, "sources should not be null.");
        
        if(beanMappingCache.getOriginal().isHeader() && getLineNumber() == 0) {
            writeHeader();
        }
        
        for(T record : sources) {
            try {
                write(record);
            } catch(SuperCsvBindingException e) {
                if(!continueOnError) {
                    throw e;
                }
            }
        }
        
        super.flush();
        
    }
}

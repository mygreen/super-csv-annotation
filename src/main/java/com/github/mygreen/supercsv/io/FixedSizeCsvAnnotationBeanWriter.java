package com.github.mygreen.supercsv.io;

import java.io.IOException;
import java.io.Writer;

import org.supercsv.encoder.CsvEncoder;
import org.supercsv.exception.SuperCsvException;
import org.supercsv.prefs.CsvPreference;
import org.supercsv.util.CsvContext;

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
}

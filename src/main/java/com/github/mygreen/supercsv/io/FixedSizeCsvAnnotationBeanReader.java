package com.github.mygreen.supercsv.io;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.supercsv.exception.SuperCsvException;

import com.github.mygreen.supercsv.exception.SuperCsvBindingException;
import com.github.mygreen.supercsv.exception.SuperCsvFixedSizeException;
import com.github.mygreen.supercsv.exception.SuperCsvNoMatchColumnSizeException;
import com.github.mygreen.supercsv.exception.SuperCsvNoMatchHeaderException;

/**
 * アノテーションを元に固定長のCSVファイルを読み込むためのクラス。
 *
 * @since 2.5
 * @param <T> マッピング対象のBeanのクラスタイプ
 * @author T.TSUCHIE
 *
 */
public class FixedSizeCsvAnnotationBeanReader<T> extends AbstractCsvAnnotationBeanReader<T> {

    public FixedSizeCsvAnnotationBeanReader(final Reader reader, final FixedSizeCsvPreference<T> preference) {
        super(preference.createTokenizer(reader), preference.getCsvPreference());

        this.beanMappingCache = preference.getBeanMappingCache();
        this.validators.addAll(beanMappingCache.getOriginal().getValidators());
    }
    
    @Override
    protected boolean readRow() throws IOException {
        try {
            return super.readRow();
        } catch(SuperCsvFixedSizeException e) {
            /*
             * FixedSizeTokenizer では行番号を取得できないので、ここで値を補完する。
             * ただし、本来ならば行番号を+1加算する必要があるが、ここでは更新不可なのでそのまま設定する。
             */
            e.getCsvContext().setRowNumber(getRowNumber());
            
            errorMessages.addAll(exceptionConverter.convertAndFormat(e, beanMappingCache.getOriginal()));
            throw e;
        }
    }
    
    /**
     * レコードを全て読み込みます。
     * <p>ヘッダー行も自動的に処理されます。</p>
     * <p>レコード処理中に例外が発生した場合、その時点で処理を終了します。</p>
     * 
     * @return 読み込んだレコード情報。
     * 
     * @throws IOException レコードの読み込みに失敗した場合。
     * @throws SuperCsvNoMatchColumnSizeException レコードのカラムサイズに問題がある場合
     * @throws SuperCsvBindingException セルの値に問題がある場合
     * @throws SuperCsvException 設定など、その他に問題がある場合
     */
    public List<T> readAll() throws IOException {
        return readAll(false);
    }
    
    /**
     * レコードを全て読み込みます。
     * <p>ヘッダー行も自動的に処理されます。</p>
     * 
     * @param continueOnError レコードの処理中に、
     *        例外{@link SuperCsvNoMatchColumnSizeException}、{@link SuperCsvNoMatchColumnSizeException}、{@link SuperCsvBindingException}
     *        が発生しても続行するかどう指定します。
     *        trueの場合、例外が発生しても、次の処理を行います。
     * @return 読み込んだレコード情報。
     * 
     * @throws IOException レコードの読み込みに失敗した場合。
     * @throws SuperCsvNoMatchColumnSizeException レコードのカラムサイズに問題がある場合
     * @throws SuperCsvBindingException セルの値に問題がある場合
     * @throws SuperCsvException 設定など、その他に問題がある場合
     */
    public List<T> readAll(final boolean continueOnError) throws IOException {
        
        if(beanMappingCache.getOriginal().isHeader()) {
            try {
                getHeader(true);
            } catch(SuperCsvNoMatchColumnSizeException | SuperCsvNoMatchHeaderException e) {
                if(!continueOnError) {
                    throw e;
                }
            }
        }
        
        final List<T> list = new ArrayList<>();
        
        while(true) {
            try {
                final T record = read();
                if(record == null) {
                    break;
                }
                list.add(record);
                
            } catch(SuperCsvNoMatchColumnSizeException | SuperCsvBindingException e) {
                if(!continueOnError) {
                    throw e;
                }
            }
        }
        
        return list;
    }
    
}

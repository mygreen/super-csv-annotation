package com.github.mygreen.supercsv.io;

import java.io.IOException;
import java.io.Reader;

import com.github.mygreen.supercsv.exception.SuperCsvFixedSizeException;

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
    
}

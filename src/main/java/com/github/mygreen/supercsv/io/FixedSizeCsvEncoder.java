package com.github.mygreen.supercsv.io;

import java.util.function.Supplier;

import org.supercsv.encoder.CsvEncoder;
import org.supercsv.prefs.CsvPreference;
import org.supercsv.util.CsvContext;

import com.github.mygreen.supercsv.builder.BeanMapping;
import com.github.mygreen.supercsv.builder.FixedSizeColumnProperty;
import com.github.mygreen.supercsv.exception.SuperCsvFixedSizeException;


/**
 * 固定長CSVを書き込むときのCsvEncoder。
 * <p>固定長サイズを超える場合は、例外 {@link SuperCsvFixedSizeException} をスローします。</p>
 * <p>改行コードを含む場合は、例外外 {@link SuperCsvFixedSizeException} をスローします。</p>
 *
 * @param <T> マッピング対象のBeanのクラスタイプ
 * @since 2.5
 * @author T.TSUCHIE
 *
 */
public class FixedSizeCsvEncoder<T> implements CsvEncoder {

    /**
     * BeanMappingの情報
     * <p>CsvEncoderのインスタンス作成時は、BeanMappingのインスタンスは未作成のため遅延評価する。
     */
    private final Supplier<BeanMapping<T>> beanMappingSupplier;
    
    /**
     * コンストラクタ。
     * 
     * @param beanMappingSupplier BeanMappingの情報を取得するためのSupplier。
     * <p>CsvEncoderのインスタンス作成時は、BeanMappingのインスタンスは未作成のため遅延評価する。</p>
     */
    public FixedSizeCsvEncoder(Supplier<BeanMapping<T>> beanMappingSupplier) {
        this.beanMappingSupplier = beanMappingSupplier;
    }
    
    /**
     * 指定した列番号のカラムの固定長プロパティを取得する。
     * <p>列番号は1から始まる。</p>
     * 
     * @param columnNumber 列番号
     * @return 固定長プロパティ
     * @throws IllegalStateException 列番号に対応するカラムの情報が見つからない場合。
     */
    private FixedSizeColumnProperty getColumnProperty(int columnNumber) {
        BeanMapping<T> beanMapping = beanMappingSupplier.get();
        return beanMapping.getColumnMapping(columnNumber)
                .orElseThrow(() -> new IllegalStateException("columnMappings not found with columnNumber=" + columnNumber))
                .getFixedSizeProperty();
    }
    
    /**
     * 
     * {@inheritDoc}
     * @throws SuperCsvFixedSizeException 固定長サイズを超えたとき、または、改行コードを含む場合。
     */
    @Override
    public String encode(final String input, final CsvContext context, final CsvPreference preference) {
        
        FixedSizeColumnProperty fixedSizeProperty = getColumnProperty(context.getColumnNumber());
        
        /*
         * 固定長サイズと一致しない場合は例外をスローする。
         * ※固定長サイズを超える場合は、使用者側で事前に切り落としておく。
         */
        int actualSize = fixedSizeProperty.getPaddingProcessor().count(input);
        if (actualSize > fixedSizeProperty.getSize()) {
            throw new SuperCsvFixedSizeException.Builder("csvError.fixedSizeOver", context)
                    .messageFormat("Over column size. fixedColumnSize: %d, actualSize: %d",
                            fixedSizeProperty.getSize(), actualSize)
                    .messageVariables("fixedColumnSize", fixedSizeProperty.getSize())
                    .messageVariables("actualSize", actualSize)
                    .messageVariables("validatedValue", input)
                    .build();
        }
        
        /*
         * 改行コードを含む場合は例外をスローする。
         * ※改行コードを含む場合は、使用者側で事前に除去しておく。
         */
        if (input.contains("\r") || input.contains("\n")) {
            throw new SuperCsvFixedSizeException.Builder("csvError.fixedSizeContainsLineBreak", context)
                    .messageFormat("Contains line break. input: [%s]", input)
                    .messageVariables("validatedValue", input)
                    .build();
        }
        
        // 囲み文字のエスケープなどは行わない。
        return input;
    }
}

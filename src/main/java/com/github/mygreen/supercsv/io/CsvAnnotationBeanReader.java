package com.github.mygreen.supercsv.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.supercsv.exception.SuperCsvException;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.ITokenizer;
import org.supercsv.prefs.CsvPreference;

import com.github.mygreen.supercsv.builder.BeanMapping;
import com.github.mygreen.supercsv.builder.BeanMappingFactory;
import com.github.mygreen.supercsv.exception.SuperCsvBindingException;
import com.github.mygreen.supercsv.exception.SuperCsvNoMatchColumnSizeException;
import com.github.mygreen.supercsv.exception.SuperCsvNoMatchHeaderException;

/**
 * アノテーションを元にCSVファイルを読み込むためのクラス。
 * 
 * @param <T> マッピング対象のBeanのクラスタイプ
 * @see CsvBeanReader
 * @version 2.１
 * @author T.TSUCHIE
 *
 */
public class CsvAnnotationBeanReader<T> extends AbstractCsvAnnotationBeanReader<T> {
    
    /**
     * Beanのクラスタイプを指定して、{@link CsvAnnotationBeanReader}を作成するコンストラクタ。
     * <p>{@link BufferedReader}にラップして実行されるため、ラップする必要はありません。</p>
     * 
     * @param beanType Beanのクラスタイプ。
     * @param reader the Reader。
     * @param preference the CSV preferences.
     * @param groups グループ情報。適用するアノテーションを切り替える際に指定します。
     * @throws NullPointerException {@literal if beanType or reader or preferences are null.}
     */
    public CsvAnnotationBeanReader(final Class<T> beanType, final Reader reader, final CsvPreference preference,
            final Class<?>... groups) {
        super(reader, preference);
        
        Objects.requireNonNull(beanType, "beanType should not be null.");
        
        BeanMappingFactory factory = new BeanMappingFactory();
        this.beanMappingCache = BeanMappingCache.create(factory.create(beanType, groups));
        this.validators.addAll(beanMappingCache.getOriginal().getValidators());
    }
    
    /**
     * Beanのマッピング情報を指定して、{@link CsvAnnotationBeanReader}を作成するコンストラクタ。
     * <p>{@link BufferedReader}にラップして実行されるため、ラップする必要はありません。</p>
     * <p>Beanのマッピング情報を独自にカスタマイズして、{@link BeanMappingFactory}から作成する場合に利用します。</p>
     * 
     * @param beanMapping Beanのマッピング情報。
     * @param reader the Reader。
     * @param preference the CSV preferences.
     * @throws NullPointerException {@literal if beanMapping or reader or preferences are null.}
     */
    public CsvAnnotationBeanReader(final BeanMapping<T> beanMapping, final Reader reader, final CsvPreference preference) {
        super(reader, preference);
        
        Objects.requireNonNull(beanMapping, "beanMapping should not be null.");
        
        this.beanMappingCache = BeanMappingCache.create(beanMapping);
        this.validators.addAll(beanMapping.getValidators());
    }
    
    /**
     * Beanのクラスタイプを指定して、{@link CsvAnnotationBeanReader}を作成するコンストラクタ。
     * <p>{@link BufferedReader}にラップして実行されるため、ラップする必要はありません。</p>
     * 
     * @param beanType Beanのクラスタイプ。
     * @param tokenizer the tokenizer.
     * @param preference the CSV preferences.
     * @param groups グループ情報。適用するアノテーションを切り替える際に指定します。
     * @throws NullPointerException {@literal if beanType or tokenizer or preferences are null.}
     */
    public CsvAnnotationBeanReader(final Class<T> beanType, final ITokenizer tokenizer, final CsvPreference preference,
            final Class<?>... groups) {
        super(tokenizer, preference);
        
        Objects.requireNonNull(beanType, "beanType should not be null.");
        
        BeanMappingFactory factory = new BeanMappingFactory();
        this.beanMappingCache = BeanMappingCache.create(factory.create(beanType, groups));
        this.validators.addAll(beanMappingCache.getOriginal().getValidators());
    }
    
    /**
     * Beanのマッピング情報を指定して、{@link CsvAnnotationBeanReader}を作成するコンストラクタ。
     * <p>{@link BufferedReader}にラップして実行されるため、ラップする必要はありません。</p>
     * <p>Beanのマッピング情報を独自にカスタマイズして、{@link BeanMappingFactory}から作成する場合に利用します。</p>
     * 
     * @param beanMapping Beanのマッピング情報。
     * @param tokenizer the tokenizer.
     * @param preferences the CSV preferences.
     * @throws NullPointerException {@literal if beanMapping or tokenizer or preferences are null.}
     */
    public CsvAnnotationBeanReader(final BeanMapping<T> beanMapping, final ITokenizer tokenizer, final CsvPreference preferences) {
        super(tokenizer, preferences);
        
        Objects.requireNonNull(beanMapping, "beanMapping should not be null.");
        
        this.beanMappingCache = BeanMappingCache.create(beanMapping);
        this.validators.addAll(beanMapping.getValidators());
    }
    
    /**
     * {@inheritDoc}
     * 
     * @param firstLineCheck 1行目の読み込み時に呼ばれるかチェックします。
     *           trueのとき、1行目の読み込み時以外に呼ばれた場合、例外{@link SuperCsvException}をスローします。
     * @return ヘッダー行の値を配列で返します。
     * @throws SuperCsvNoMatchColumnSizeException ヘッダーのサイズ（カラム数）がBean定義と一致しない場合。
     * @throws SuperCsvNoMatchHeaderException ヘッダーの値がBean定義と一致しない場合。
     * @throws SuperCsvException 引数firstLineCheck=trueのとき、このメソッドが1行目以外の読み込み時に呼ばれた場合。
     * @throws IOException ファイルの読み込みに失敗した場合。
     */
    @Override
    public String[] getHeader(boolean firstLineCheck) throws IOException {
        
        final String[] header = super.getHeader(firstLineCheck);
        if(beanMappingCache.getOriginal().isValidateHeader()) {
            try {
                validateHeader(header, beanMappingCache.getHeader());
                
            } catch(SuperCsvNoMatchColumnSizeException | SuperCsvNoMatchHeaderException e) {
                // convert exception and format to message.
                errorMessages.addAll(exceptionConverter.convertAndFormat(e, beanMappingCache.getOriginal()));
                throw e;
            }
        }
        
        return header;
        
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

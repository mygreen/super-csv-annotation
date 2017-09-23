package com.github.mygreen.supercsv.io;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.Objects;

import org.supercsv.exception.SuperCsvException;
import org.supercsv.prefs.CsvPreference;

import com.github.mygreen.supercsv.builder.BeanMapping;
import com.github.mygreen.supercsv.builder.BeanMappingFactory;
import com.github.mygreen.supercsv.exception.SuperCsvBindingException;

/**
 * アノテーションを元にCSVファイルを出力するためのクラス。
 *
 * @param <T> マッピング対象のBeanのクラスタイプ
 * @version 2.1
 * @author T.TSUCHIE
 *
 */
public class CsvAnnotationBeanWriter<T> extends AbstractCsvAnnotationBeanWriter<T> {
    
    /**
     * Beanのクラスタイプを指定して、{@link CsvAnnotationBeanWriter}を作成するコンストラクタ。
     * <p>{@link BufferedWriter}にラップして実行されるため、ラップする必要はありません。</p>
     * 
     * @param beanType Beanのクラスタイプ。
     * @param writer the writer
     * @param preference CSV preferences.
     * @param groups グループ情報。適用するアノテーションを切り替える際に指定します。
     * @throws NullPointerException {@literal if beanType or writer or preferences are null.}
     */
    public CsvAnnotationBeanWriter(final Class<T> beanType, final Writer writer, final CsvPreference preference,
            final Class<?>... groups) {
        super(writer, preference);
        
        Objects.requireNonNull(beanType, "beanType should not be null.");
        
        BeanMappingFactory factory = new BeanMappingFactory();
        this.beanMappingCache = BeanMappingCache.create(factory.create(beanType, groups));
        this.validators.addAll(beanMappingCache.getOriginal().getValidators());
        
    }
    
    /**
     * Beanのマッピング情報を指定して、{@link CsvAnnotationBeanWriter}を作成するコンストラクタ。
     * <p>{@link BufferedWriter}にラップして実行されるため、ラップする必要はありません。</p>
     * <p>Beanのマッピング情報を独自にカスタマイズして、{@link BeanMappingFactory}から作成する場合に利用します。</p>
     * 
     * @param beanMapping Beanのマッピング情報。
     * @param writer the writer
     * @param preference the CSV preferences.
     * @throws NullPointerException {@literal if beanMapping or writer or preferences are null.}
     */
    public CsvAnnotationBeanWriter(final BeanMapping<T> beanMapping, final Writer writer, final CsvPreference preference) {
        super(writer, preference);
        
        Objects.requireNonNull(beanMapping, "beanMapping should not be null.");
        
        this.beanMappingCache = BeanMappingCache.create(beanMapping);
        this.validators.addAll(beanMapping.getValidators());
    }
    
    /**
     * ヘッダー情報を書き込みます。
     * <p>ただし、列番号を省略され、定義がされていないカラムは、{@literal column[カラム番号]}の形式となります。</p>
     * @throws IOException ファイルの出力に失敗した場合。
     */
    public void writeHeader() throws IOException {
        super.writeHeader(getDefinedHeader());
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

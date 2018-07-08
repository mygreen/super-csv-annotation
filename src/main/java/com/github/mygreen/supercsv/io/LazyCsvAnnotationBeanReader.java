package com.github.mygreen.supercsv.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.supercsv.exception.SuperCsvException;
import org.supercsv.io.ITokenizer;
import org.supercsv.prefs.CsvPreference;

import com.github.mygreen.supercsv.annotation.CsvPartial;
import com.github.mygreen.supercsv.builder.BeanMapping;
import com.github.mygreen.supercsv.builder.BeanMappingFactoryHelper;
import com.github.mygreen.supercsv.builder.ColumnMapping;
import com.github.mygreen.supercsv.builder.HeaderMapper;
import com.github.mygreen.supercsv.builder.LazyBeanMappingFactory;
import com.github.mygreen.supercsv.exception.SuperCsvBindingException;
import com.github.mygreen.supercsv.exception.SuperCsvNoMatchColumnSizeException;
import com.github.mygreen.supercsv.exception.SuperCsvNoMatchHeaderException;
import com.github.mygreen.supercsv.localization.MessageBuilder;

/**
 * カラムの定義が曖昧なアノテーションを元にCSVファイルを読み込むためのクラス。
 * <p>カラム番号が指定されていないBean定義を元にマッピングします。
 *   <br>カラム番号の決定は、ヘッダー行を取得して、その情報を元に読み込み時に決定します。
 * </p>
 * 
 * @param <T> マッピング対象のBeanのクラスタイプ
 * 
 * @version 2.2
 * @since 2.1
 * @author T.TSUCHIE
 *
 */
public class LazyCsvAnnotationBeanReader<T> extends AbstractCsvAnnotationBeanReader<T> {
    
    /**
     * Beanのマッピング情報。
     * ・初期化は済んでいない場合があるため、キャッシュとは別に管理する。
     */
    private final BeanMapping<T> beanMapping;
    
    /**
     * ヘッダー情報を元に初期化済みかどうか
     */
    private boolean initialized = false;
    
    /**
     * Beanのクラスタイプを指定して、{@link LazyCsvAnnotationBeanReader}を作成するコンストラクタ。
     * <p>{@link BufferedReader}にラップして実行されるため、ラップする必要はありません。</p>
     * 
     * @param beanType Beanのクラスタイプ。
     * @param reader the Reader。
     * @param preference the CSV preferences.
     * @param groups グループ情報。適用するアノテーションを切り替える際に指定します。
     * @throws NullPointerException {@literal if beanType or reader or preferences are null.}
     */
    public LazyCsvAnnotationBeanReader(final Class<T> beanType, final Reader reader, final CsvPreference preference,
            final Class<?>... groups) {
        
        super(reader, preference);
        
        Objects.requireNonNull(beanType, "beanType should not be null.");
        
        LazyBeanMappingFactory factory = new LazyBeanMappingFactory();
        this.beanMapping = factory.create(beanType, groups);
        this.validators.addAll(beanMapping.getValidators());
    }
    
    /**
     * Beanのマッピング情報を指定して、{@link LazyCsvAnnotationBeanReader}を作成するコンストラクタ。
     * <p>{@link BufferedReader}にラップして実行されるため、ラップする必要はありません。</p>
     * <p>Beanのマッピング情報を独自にカスタマイズして、{@link LazyBeanMappingFactory}から作成する場合に利用します。</p>
     * 
     * @param beanMapping Beanのマッピング情報。
     * @param reader the Reader。
     * @param preference the CSV preferences.
     * @throws NullPointerException {@literal if beanMapping or reader or preferences are null.}
     */
    public LazyCsvAnnotationBeanReader(final BeanMapping<T> beanMapping, final Reader reader, final CsvPreference preference,
            final Class<?>... groups) {
        
        super(reader, preference);
        
        Objects.requireNonNull(beanMapping, "beanMapping should not be null.");
        
        this.beanMapping = beanMapping;
        this.validators.addAll(beanMapping.getValidators());
    }
    
    /**
     * Beanのクラスタイプを指定して、{@link LazyCsvAnnotationBeanReader}を作成するコンストラクタ。
     * <p>{@link BufferedReader}にラップして実行されるため、ラップする必要はありません。</p>
     * 
     * @param beanType Beanのクラスタイプ。
     * @param tokenizer the tokenizer.
     * @param preference the CSV preferences.
     * @param groups グループ情報。適用するアノテーションを切り替える際に指定します。
     * @throws NullPointerException {@literal if beanType or tokenizer or preferences are null.}
     */
    public LazyCsvAnnotationBeanReader(final Class<T> beanType, final ITokenizer tokenizer, final CsvPreference preference,
            final Class<?>... groups) {
        
        super(tokenizer, preference);
        
        Objects.requireNonNull(beanType, "beanType should not be null.");
        
        LazyBeanMappingFactory factory = new LazyBeanMappingFactory();
        this.beanMapping = factory.create(beanType, groups);
        this.validators.addAll(beanMapping.getValidators());
    }
    
    /**
     * Beanのマッピング情報を指定して、{@link LazyCsvAnnotationBeanReader}を作成するコンストラクタ。
     * <p>{@link BufferedReader}にラップして実行されるため、ラップする必要はありません。</p>
     * <p>Beanのマッピング情報を独自にカスタマイズして、{@link LazyBeanMappingFactory}から作成する場合に利用します。</p>
     * 
     * @param beanMapping Beanのマッピング情報。
     * @param tokenizer the tokenizer.
     * @param preference the CSV preferences.
     * @param groups グループ情報。適用するアノテーションを切り替える際に指定します。
     * @throws NullPointerException {@literal if beanMapping or tokenizer or preferences are null.}
     */
    public LazyCsvAnnotationBeanReader(final BeanMapping<T> beanMapping, final ITokenizer tokenizer, final CsvPreference preference,
            final Class<?>... groups) {
        
        super(tokenizer, preference);
        
        Objects.requireNonNull(beanMapping, "beanMapping should not be null.");
        
        this.beanMapping = beanMapping;
        this.validators.addAll(beanMapping.getValidators());
    }
    
    /**
     * １行目のレコードをヘッダー情報として読み込んで、カラム情報を初期化を行います。
     * 
     * @return 読み込んだヘッダー情報
     * @throws SuperCsvNoMatchColumnSizeException ヘッダーのサイズ（カラム数）がBean定義と一致しない場合。
     * @throws SuperCsvNoMatchHeaderException ヘッダーの値がBean定義と一致しない場合。
     * @throws SuperCsvException 引数firstLineCheck=trueのとき、このメソッドが1行目以外の読み込み時に呼ばれた場合。
     * @throws IOException ファイルの読み込みに失敗した場合。
     */
    public String[] init() throws IOException {
        // ヘッダーを元に、カラム情報の番号を補完する
        final String[] headers = getHeader(true);
        init(headers);
        
        return headers;
    }
    
    /**
     * ヘッダー情報を指定して、カラム情報の初期化を行います。
     * <p>ヘッダーの位置を元にカラムの番号を決定します。</p>
     * 
     * @param headers CSVのヘッダー情報。実際のCSVファイルの内容と一致する必要があります。
     * @throws SuperCsvNoMatchColumnSizeException ヘッダーのサイズ（カラム数）がBean定義と一致しない場合。
     * @throws SuperCsvNoMatchHeaderException ヘッダーの値がBean定義と一致しない場合。
     * @throws SuperCsvException 引数firstLineCheck=trueのとき、このメソッドが1行目以外の読み込み時に呼ばれた場合。
     */
    public void init(final String... headers) {
        
        setupMappingColumns(headers);
        this.beanMappingCache = BeanMappingCache.create(beanMapping);
        
        if(beanMappingCache.getOriginal().isValidateHeader()) {
            try {
                validateHeader(headers, beanMapping.getHeader());
                
            } catch(SuperCsvNoMatchColumnSizeException | SuperCsvNoMatchHeaderException e) {
                // convert exception and format to message.
                errorMessages.addAll(exceptionConverter.convertAndFormat(e, beanMappingCache.getOriginal()));
                throw e;
            }
        }
        
        // 初期化完了
        this.initialized = true;
    }
    
    
    /**
     * 初期化が完了していないときに呼ばれたときにスローする例外のインスタンスを作成します。
     */
    private IllegalStateException newNotInitialzedException() {
        return new IllegalStateException(MessageBuilder.create("noinit.onLazyRead").format());
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
     * @throws IllegalStateException ヘッダー行を持たないときに、{@link #init(String...)}で初期化が済んでいない場合。
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
     * @throws IllegalStateException ヘッダー行を持たないときに、{@link #init(String...)}で初期化が済んでいない場合。
     * 
     */
    public List<T> readAll(final boolean continueOnError) throws IOException {
        
        if(!initialized) {
            if(beanMapping.isHeader()) {
                // ヘッダーがファイルに存在する場合、１行目を読み込んで初期化を行う。
                try {
                    init();
                } catch(SuperCsvNoMatchColumnSizeException | SuperCsvNoMatchHeaderException e) {
                    if(!continueOnError) {
                        throw e;
                    }
                }
                
            } else {
                // ヘッダーがファイルに存在しない場合、独自にinit(header1, header2)メソッドを呼んで初期化する必要がある。
                throw newNotInitialzedException();
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
    
    /**
     * {@inheritDoc}
     * @throws IllegalStateException ヘッダーが読み込まれておらず、マッピング情報の初期か完了していない場合。
     */
    @Override
    public T read() throws IOException {
        
        // ヘッダーが読み込まれておらず、初期化が終わっていない場合
        if(!initialized) {
            throw newNotInitialzedException();
        }
        
        return super.read();
        
        
    }
    
    /**
     * 読み込んだヘッダーを元に、マッピング情報を補完する。
     * <p>カラムの位置である番号を確定する。</p>
     * <p>存在しないカラムがある場合は、部分的な読み込みとして、ダミーのカラム情報を作成する。</p>
     * @param headers
     * @param columns
     * @param SuperCsvInvalidAnnotationException
     */
    private void setupMappingColumns(final String[] headers) {
        
        final List<ColumnMapping> columnMappingList = beanMapping.getColumns();
        final HeaderMapper headerMapper = beanMapping.getHeaderMapper();
        
        // 一致するラベルがあれば、カラムの番号を補完する
        final int headerSize = headers.length;
        for(int i=0; i < headerSize;i ++) {
            
            final String header = headers[i];
            
            /*
             * 番号が決まっておらず、ラベルが一致するカラム情報を抽出する。
             * ※既に番号が決まっているが、ラベルが一致しないのものは、後からチェックする。
             */
            List<ColumnMapping> undeterminedColumnList = columnMappingList.stream()
                .filter(col -> col.getNumber() <= 0)
                .filter(col -> headerMapper.toMap(col, beanMapping.getConfiguration(), beanMapping.getGroups()).equals(header))
                .collect(Collectors.toList());
            
            final int columnNumber = i+1;
            undeterminedColumnList.forEach(col -> col.setNumber(columnNumber));
            
        }
        
        // カラムの番号順に並び変える
        columnMappingList.sort(null);
        
        // 重複しているカラム番号のチェック
        BeanMappingFactoryHelper.validateDuplicatedColumnNumber(beanMapping.getType(), columnMappingList);
        
        // 不足しているカラム番号の補完
        final Optional<CsvPartial> partialAnno = Optional.ofNullable(beanMapping.getType().getAnnotation(CsvPartial.class));
        BeanMappingFactoryHelper.supplyLackedNumberMappingColumn(beanMapping.getType(), columnMappingList, partialAnno, headers);
        
        beanMapping.setColumns(columnMappingList);
        
    }
    
    /**
     * {@inheritDoc}
     * @throws IllegalStateException {@link #init()} メソッドによる初期化が完了していない場合
     */
    @Override
    public String[] getDefinedHeader() {
        if(!initialized) {
            throw newNotInitialzedException();
        }
        
        return super.getDefinedHeader();
    }
    
    /**
     * {@inheritDoc}
     * @throws IllegalStateException {@link #init()} メソッドによる初期化が完了していない場合
     */
    @Override
    public BeanMapping<T> getBeanMapping() {
        if(!initialized) {
            throw newNotInitialzedException();
        }
        
        return super.getBeanMapping();
    }
    
    
}

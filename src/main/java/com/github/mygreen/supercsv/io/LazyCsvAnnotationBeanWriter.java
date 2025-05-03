package com.github.mygreen.supercsv.io;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.supercsv.exception.SuperCsvException;
import org.supercsv.prefs.CsvPreference;

import com.github.mygreen.supercsv.annotation.CsvPartial;
import com.github.mygreen.supercsv.builder.BeanMapping;
import com.github.mygreen.supercsv.builder.BeanMappingFactoryHelper;
import com.github.mygreen.supercsv.builder.ColumnMapping;
import com.github.mygreen.supercsv.builder.HeaderMapper;
import com.github.mygreen.supercsv.builder.LazyBeanMappingFactory;
import com.github.mygreen.supercsv.exception.SuperCsvBindingException;
import com.github.mygreen.supercsv.localization.MessageBuilder;

/**
 * カラムの定義が曖昧なアノテーションを元にCSVファイルを書き出すためのクラス。
 * <p>カラム番号が指定されていないBean定義を元にマッピングします。</p>
 * 
 * @param <T> マッピング対象のBeanのクラスタイプ
 * 
 * @version 2.2
 * @since 2.1
 * @author T.TSUCHIE
 *
 */
public class LazyCsvAnnotationBeanWriter<T> extends AbstractCsvAnnotationBeanWriter<T> {
    
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
     * Beanのクラスタイプを指定して、{@link LazyCsvAnnotationBeanWriter}を作成するコンストラクタ。
     * <p>{@link BufferedWriter}にラップして実行されるため、ラップする必要はありません。</p>
     * 
     * @param beanType Beanのクラスタイプ。
     * @param writer the writer
     * @param preference CSV preferences.
     * @param groups グループ情報。適用するアノテーションを切り替える際に指定します。
     * @throws NullPointerException {@literal if beanType or writer or preferences are null.}
     */
    public LazyCsvAnnotationBeanWriter(final Class<T> beanType, final Writer writer, final CsvPreference preference,
            final Class<?>... groups) {
        super(writer, preference);
        
        Objects.requireNonNull(beanType, "beanType should not be null.");
        
        LazyBeanMappingFactory factory = new LazyBeanMappingFactory();
        this.beanMapping = factory.create(beanType, groups);
        this.validators.addAll(beanMapping.getValidators());
        
    }
    
    /**
     * Beanのマッピング情報を指定して、{@link LazyCsvAnnotationBeanWriter}を作成するコンストラクタ。
     * <p>{@link BufferedWriter}にラップして実行されるため、ラップする必要はありません。</p>
     * <p>Beanのマッピング情報を独自にカスタマイズして、{@link LazyBeanMappingFactory}から作成する場合に利用します。</p>
     * 
     * @param beanMapping Beanのマッピング情報。
     * @param writer the writer
     * @param preference the CSV preferences.
     * @throws NullPointerException {@literal if beanMapping or writer or preferences are null.}
     */
    public LazyCsvAnnotationBeanWriter(final BeanMapping<T> beanMapping, final Writer writer, final CsvPreference preference,
            final Class<?>... groups) {
        super(writer, preference);
        
        Objects.requireNonNull(beanMapping, "beanMapping should not be null.");
        
        this.beanMapping = beanMapping;
        this.validators.addAll(beanMapping.getValidators());
        
    }
    
    /**
     * 初期化が完了していないときに呼ばれたときにスローする例外のインスタンスを作成します。
     */
    private IllegalStateException newNotInitialzedException() {
        return new IllegalStateException(MessageBuilder.create("noinit.onLazyWrite").format());
    }
    
    /**
     * ヘッダー情報を指定しないで初期化を行います。
     * <p>カラム番号が指定されていないカラムは、フィールドの名称順に空いている番号が割り振られます。</p>
     */
    public void init() {
        init(new String[]{});
    }
    
    /**
     * ヘッダー情報を指定して初期化を行います。
     * <p>カラム番号が指定されていないカラムは、フィールドの名称順に空いている番号が割り振られます。</p>
     * @param headers ヘッダー情報
     */
    public void init(final String... headers) {
        
        setupMappingColumns(headers);
        this.beanMappingCache = BeanMappingCache.create(beanMapping);
        
        // 初期化完了
        this.initialized = true;
    
    }
    
    /**
     * 指定したヘッダーを元に、マッピング情報を補完する。
     * <p>カラムの位置である番号を確定する。</p>
     * <p>存在しないカラムがある場合は、部分的な読み込みとして、ダミーのカラム情報を作成する。</p>
     * @param headers
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
                .filter(col -> !col.isDeterminedNumber())
                .filter(col -> headerMapper.toMap(col, beanMapping.getConfiguration(), beanMapping.getGroups()).equals(header))
                .collect(Collectors.toList());
            
            final int columnNumber = i+1;
            undeterminedColumnList.forEach(col -> col.setNumber(columnNumber));
            
        }
        
        // カラムの番号順に並び変える
        columnMappingList.sort(null);
        
        /* 
         * ヘッダーでは指定されていない番号が未決定のカラムについて、空いている番号に振る
         * ・@CsvPartialで指定した場合とかぶらないようにすべき？
         */
        final Optional<CsvPartial> partialAnno = Optional.ofNullable(beanMapping.getType().getAnnotation(CsvPartial.class));
        final List<ColumnMapping> undeterminedColumnList = columnMappingList.stream()
                .filter(col -> !col.isDeterminedNumber())
                .collect(Collectors.toList());
        
        if(!undeterminedColumnList.isEmpty()) {
            final Set<Integer> determinedNumbers = columnMappingList.stream()
                    .filter(col -> col.isDeterminedNumber())
                    .map(col -> col.getNumber())
                    .collect(Collectors.toSet());
            
            // @CsvParitalで定義されているカラムは、決定されている番号として処理する。
            partialAnno.ifPresent(anno -> Arrays.stream(anno.headers()).forEach(header -> determinedNumbers.add(header.number())));
            
            int counter = 1;
            for(ColumnMapping col : undeterminedColumnList) {
                while(true) {
                    if(!determinedNumbers.contains(counter)) {
                        break;
                    }
                    counter++;
                }
                
                col.setNumber(counter);
                determinedNumbers.add(counter);
            }
            
            // 第度カラムの番号順に並び変える
            columnMappingList.sort(null);
        
        }
        
        // 決定していないカラム番号のチェック
        BeanMappingFactoryHelper.validateNonDeterminedColumnNumber(beanMapping.getType(), columnMappingList, headers);
        
        // 重複しているカラム番号のチェック
        BeanMappingFactoryHelper.validateDuplicatedColumnNumber(beanMapping.getType(), columnMappingList);
        
        // 不足しているカラム番号の補完
        BeanMappingFactoryHelper.supplyLackedNumberMappingColumn(beanMapping.getType(), columnMappingList, partialAnno, headers, beanMapping.getConfiguration());
        
        beanMapping.setColumns(columnMappingList);
        
    }
    
    /**
     * ヘッダー情報を書き出します。
     * <p>ただし、列番号を省略され、定義がされていないカラムは、{@literal column[カラム番号]}の形式となります。</p>
     * @throws IOException ファイルの出力に失敗した場合。
     * @throws IllegalStateException {@link #init()} メソッドによる初期化が完了していない場合
     */
    public void writeHeader() throws IOException {
        if(!initialized) {
            throw newNotInitialzedException();
        }
        
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
        
        if(!initialized) {
            init();
        }
        
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
    
    /**
     * {@inheritDoc}
     * @throws IllegalStateException {@link #init()} メソッドによる初期化が完了していない場合
     */
    @Override
    public void write(final T source) throws IOException {
        
        if(!initialized) {
            throw newNotInitialzedException();
        }
        
        super.write(source);
        
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

package com.github.mygreen.supercsv.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.exception.SuperCsvCellProcessorException;
import org.supercsv.exception.SuperCsvException;
import org.supercsv.exception.SuperCsvReflectionException;
import org.supercsv.io.AbstractCsvReader;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.ITokenizer;
import org.supercsv.prefs.CsvPreference;
import org.supercsv.util.BeanInterfaceProxy;
import org.supercsv.util.CsvContext;
import org.supercsv.util.MethodCache;

import com.github.mygreen.supercsv.builder.BeanMapping;
import com.github.mygreen.supercsv.builder.BeanMappingFactory;
import com.github.mygreen.supercsv.builder.CallbackMethod;
import com.github.mygreen.supercsv.exception.SuperCsvBindingException;
import com.github.mygreen.supercsv.exception.SuperCsvNoMatchColumnSizeException;
import com.github.mygreen.supercsv.exception.SuperCsvNoMatchHeaderException;
import com.github.mygreen.supercsv.exception.SuperCsvRowException;
import com.github.mygreen.supercsv.validation.CsvBindingErrors;
import com.github.mygreen.supercsv.validation.CsvExceptionConverter;
import com.github.mygreen.supercsv.validation.CsvValidator;
import com.github.mygreen.supercsv.validation.CsvError;
import com.github.mygreen.supercsv.validation.ValidationContext;

/**
 * アノテーションを元にCSVファイルを読み込むためのクラス。
 * 
 * @param <T> マッピング対象のBeanのクラスタイプ
 * @see CsvBeanReader
 * @version 2.0
 * @author T.TSUCHIE
 *
 */
public class CsvAnnotationBeanReader<T> extends AbstractCsvReader {
    
    /** temporary storage of processed columns to be mapped to the bean */
    private final List<Object> processedColumns = new ArrayList<>();
    
    /** cache of methods for mapping from columns to fields */
    private final MethodCache cache = new MethodCache();
    
    private final BeanMappingCache<T> beanMapping;
    
    /** exception converter. */
    private CsvExceptionConverter exceptionConverter = new CsvExceptionConverter();
    
    /** processing error messages. */
    private final List<String> errorMessages = new ArrayList<>();
    
    /** validator */
    private final List<CsvValidator<T>> validators = new ArrayList<>();
    
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
        this.beanMapping = BeanMappingCache.create(factory.create(beanType, groups));
        this.validators.addAll(beanMapping.getOriginal().getValidators());
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
        
        this.beanMapping = BeanMappingCache.create(beanMapping);
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
        this.beanMapping = BeanMappingCache.create(factory.create(beanType, groups));
        this.validators.addAll(beanMapping.getOriginal().getValidators());
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
        
        this.beanMapping = BeanMappingCache.create(beanMapping);
        this.validators.addAll(beanMapping.getValidators());
    }
    
    /**
     * Beanクラスを元に作成したヘッダー情報を取得する。
     * @return ヘッダー一覧。
     */
    public String[] getDefinedHeader() {
        return beanMapping.getHeader();
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
        if(beanMapping.getOriginal().isValidateHeader()) {
            try {
                validateHeader(header, beanMapping.getHeader());
                
            } catch(SuperCsvNoMatchColumnSizeException | SuperCsvNoMatchHeaderException e) {
                // convert exception and format to message.
                errorMessages.addAll(exceptionConverter.convertAndFormat(e, beanMapping.getOriginal()));
                throw e;
            }
        }
        
        return header;
        
    }
    
    private void validateHeader(final String[] sourceHeader, final String[] definedHeader) {
        
        // check column size.
        if(sourceHeader.length != definedHeader.length) {
            final CsvContext context = new CsvContext(1, 1, 1);
            throw new SuperCsvNoMatchColumnSizeException(sourceHeader.length, definedHeader.length, context);
            
        }
        
        // check header value
        for(int i=0; i < sourceHeader.length; i++) {
            if(!sourceHeader[i].equals(definedHeader[i])) {
                final CsvContext context = new CsvContext(1, 1, i+1);
                throw new SuperCsvNoMatchHeaderException(sourceHeader, definedHeader, context);
            }
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
        
        if(beanMapping.getOriginal().isHeader()) {
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
    
    /**
     * 1レコード分を読み込む。
     * 
     * @return Beanのレコード。読み込むレコードがない場合は、nullを返す。
     * 
     * @throws IOException レコードの読み込みに失敗した場合。
     * @throws SuperCsvNoMatchColumnSizeException レコードのカラムサイズに問題がある場合
     * @throws SuperCsvBindingException セルの値に問題がある場合
     * @throws SuperCsvException 設定など、その他に問題がある場合
     * 
     */
    public T read() throws IOException {
        
        if(readRow()) {
            
            final T bean = instantiateBean(beanMapping.getOriginal().getType());
            final CsvBindingErrors bindingErrors = new CsvBindingErrors(beanMapping.getOriginal().getType());
            
            final CsvContext context = new CsvContext(getLineNumber(), getRowNumber(), 1);
            context.setRowSource(new ArrayList<Object>(processedColumns));
            
            Optional<SuperCsvRowException> rowException = Optional.empty();
            try {
                executeCellProcessor(processedColumns, getColumns(), beanMapping.getCellProcessorsForReading(), context);
                
            } catch(SuperCsvRowException e) {
                /*
                 * カラムごとのCellProcessorのエラーの場合、別なValidatorで値を検証するために、
                 * 後から判定を行うようにする。
                 */
                rowException = Optional.of(e);
                
                final List<CsvError> errors = exceptionConverter.convert(e, beanMapping.getOriginal());
                bindingErrors.addAllErrors(errors);
                
            } catch(SuperCsvException e) {
                errorMessages.addAll(exceptionConverter.convertAndFormat(e, beanMapping.getOriginal()));
                throw e;
            }
            
            // コールバックメソッドの実行（読み込み前）
            for(CallbackMethod callback : beanMapping.getOriginal().getPreReadMethods()) {
                callback.invoke(bean, context, bindingErrors, beanMapping.getOriginal());
            }
            
            // beanへのマッピング
            populateBean(bean, beanMapping.getNameMapping(), bindingErrors);
            
            // Bean(レコード)の入力値検証
            for(CsvValidator<T> recordValidator : validators) {
                recordValidator.validate(bean, bindingErrors, new ValidationContext<>(context, beanMapping.getOriginal()));
            }
            
            // コールバックメソッドの実行（読み込み後）
            for(CallbackMethod callback : beanMapping.getOriginal().getPostReadMethods()) {
                callback.invoke(bean, context, bindingErrors, beanMapping.getOriginal());
            }
            
            // エラーメッセージの変換
            processErrors(bindingErrors, context, rowException);
            
            return bean;
            
        }
        
        return null; // EOF
        
        
    }
    
    private void processErrors(final CsvBindingErrors bindingErrors, final CsvContext context,
            final Optional<SuperCsvRowException> rowException) {
        if(bindingErrors.hasErrors()) {
            final List<String> message = bindingErrors.getAllErrors().stream()
                    .map(error -> error.format(exceptionConverter.getMessageResolver(), exceptionConverter.getMessageInterpolator()))
                    .collect(Collectors.toList());
            errorMessages.addAll(message);
            
            final SuperCsvBindingException bindingException = new SuperCsvBindingException("has binding error.", context, bindingErrors);
            rowException.ifPresent(re -> bindingException.addAllProcessingErrors(re.getColumnErrors()));
            
            throw bindingException;
        }
    }
    
    /**
     * 指定したBeanのクラスのインスタンスを作成する。
     * 
     * @param clazz Beanのクラスタイプ。
     * @return Beanのインスタンス。
     * @throws SuperCsvReflectionException Beanのインスタンスの作成に失敗した場合。
     */
    private T instantiateBean(final Class<T> clazz) {
        
        final T bean;
        if( clazz.isInterface() ) {
            bean = BeanInterfaceProxy.createProxy(clazz);
        } else {
            try {
                bean = clazz.newInstance();
            } catch(InstantiationException e) {
                throw new SuperCsvReflectionException(String.format(
                    "error instantiating bean, check that %s has a default no-args constructor", clazz.getName()), e);
            } catch(IllegalAccessException e) {
                throw new SuperCsvReflectionException("error instantiating bean", e);
            }
        }
        
        return bean;
        
    }
    
    /**
     * 
     * @see Util#executeCellProcessors(List, List, CellProcessor[], int, int)
     * @param destination
     * @param source
     * @param processors
     * @param context
     * @throws SuperCsvNoMatchColumnSizeException カラムサイズが定義と一致しない場合
     * @throws SuperCsvRowException CellProcessor内で発生した例外
     */
    private void executeCellProcessor(final List<Object> destination, final List<String> source,
            final CellProcessor[] processors, final CsvContext context) {
        
        if(source.size() != processors.length) {
            throw new SuperCsvNoMatchColumnSizeException(source.size(), processors.length, context);
            
        }
        
        destination.clear();
        
        final SuperCsvRowException rowException = new SuperCsvRowException(
                String.format("row (%d) has errors column", context.getRowNumber()), context);
        
        for( int i = 0; i < source.size(); i++ ) {
            
            try {
                context.setColumnNumber(i + 1); // update context (columns start at 1)
                
                if( processors[i] == null ) {
                    destination.add(source.get(i)); // no processing required
                } else {
                    destination.add(processors[i].execute(source.get(i), context)); // execute the processor chain
                }
            } catch(SuperCsvCellProcessorException e) {
                rowException.addError(e);
                
                // 各カラムでエラーがあっても、後の入力値検証で処理を続けるために、仮に値を設定する。
                destination.add(source.get(i));
                
            } catch(SuperCsvException e) {
                
                throw e;
                
            }
        }
        
        if(rowException.isNotEmptyColumnErrors()) {
            throw rowException;
        }
        
    }
    
    /**
     * Beanの各フィールドに対して値を設定する。
     * @param resultBean
     * @param nameMapping
     * @param bindingErrors
     */
    private void populateBean(final T resultBean, final String[] nameMapping, final CsvBindingErrors bindingErrors) {
        
        // map each column to its associated field on the bean
        for( int i = 0; i < nameMapping.length; i++ ) {
            final String fieldName = nameMapping[i];
            final Object fieldValue = processedColumns.get(i);
            
            // don't call a set-method in the bean if there is no name mapping for the column or no result to store
            if( fieldName == null || fieldValue == null || bindingErrors.hasFieldErrors(fieldName)) {
                continue;
            }
            
            // invoke the setter on the bean
            final Method setMethod = cache.getSetMethod(resultBean, fieldName, fieldValue.getClass());
            try {
                setMethod.invoke(resultBean, fieldValue);
                
            } catch(final Exception e) {
                throw new SuperCsvReflectionException(String.format("error invoking method %s()", setMethod.getName()), e);
            }
            
        }
        
    }
    
    /**
     * Beanのマッピング情報を取得します。
     * @return Beanのマッピング情報
     */
    public BeanMapping<T> getBeanMapping() {
        return beanMapping.getOriginal();
    }
    
    /**
     * エラーメッセージを取得します。
     * @return 処理中に発生した例外をメッセージに変換した
     */
    public List<String> getErrorMessages() {
        return errorMessages;
    }
    
    /**
     * 処理中に発生した例外をメッセージに変換するクラスを取得します。
     * @return 
     */
    public CsvExceptionConverter getExceptionConverter() {
        return exceptionConverter;
    }
    
    /**
     * 処理中に発生した例外をメッセージに変換するクラスを設定します。
     * @param exceptionConverter 独自にカスタマイズした値を設定します。
     */
    public void setExceptionConverter(CsvExceptionConverter exceptionConverter) {
        this.exceptionConverter = exceptionConverter;
    }
    
    /**
     * レコードの値を検証するValidatorを追加します。
     * @param validators {@link CsvValidator}の実装クラスを設定します。
     */
    @SuppressWarnings("unchecked")
    public void addValidator(CsvValidator<T>... validators) {
        this.validators.addAll(Arrays.asList(validators));
    }
    
    /**
     * レコードの値を検証するValidatorを取得します。
     * @return {@link CsvValidator}の実装クラスを設定します。
     */
    public List<CsvValidator<T>> getValidators() {
        return validators;
    }
    
}

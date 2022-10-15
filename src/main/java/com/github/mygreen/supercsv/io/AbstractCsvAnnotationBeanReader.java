package com.github.mygreen.supercsv.io;

import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

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
import com.github.mygreen.supercsv.builder.CallbackMethod;
import com.github.mygreen.supercsv.exception.SuperCsvBindingException;
import com.github.mygreen.supercsv.exception.SuperCsvNoMatchColumnSizeException;
import com.github.mygreen.supercsv.exception.SuperCsvNoMatchHeaderException;
import com.github.mygreen.supercsv.exception.SuperCsvRowException;
import com.github.mygreen.supercsv.validation.CsvBindingErrors;
import com.github.mygreen.supercsv.validation.CsvError;
import com.github.mygreen.supercsv.validation.CsvExceptionConverter;
import com.github.mygreen.supercsv.validation.CsvValidator;
import com.github.mygreen.supercsv.validation.ValidationContext;

/**
 * アノテーションを元にCSVファイルを読み込むための抽象クラス。
 * 
 * @param <T> マッピング対象のBeanのクラスタイプ
 *
 * @see CsvBeanReader
 * @version 2.3
 * @since 2.1
 * @author T.TSUCHIE
 *
 */
public abstract class AbstractCsvAnnotationBeanReader<T> extends AbstractCsvReader {
    
    /**
     * Beanのマッピング情報のキャッシュ。
     * ・組み立てたCellProcessorをキャッシュしておきます。
     */
    protected BeanMappingCache<T> beanMappingCache;
    
    /** temporary storage of processed columns to be mapped to the bean */
    protected final List<Object> processedColumns = new ArrayList<>();
    
    /** cache of methods for mapping from columns to fields */
    protected final MethodCache cache = new MethodCache();
    
    /** exception converter. */
    protected CsvExceptionConverter exceptionConverter = new CsvExceptionConverter();
    
    /** processing error messages. */
    protected final List<String> errorMessages = new ArrayList<>();
    
    /** validator */
    protected final List<CsvValidator<T>> validators = new ArrayList<>();
    
    public AbstractCsvAnnotationBeanReader(final Reader reader, final CsvPreference preference) {
        super(reader, preference);
    }
    
    public AbstractCsvAnnotationBeanReader(final ITokenizer tokenizer, final CsvPreference preference) {
        super(tokenizer, preference);
    }
    
    /**
     * 1レコード分を読み込みます。
     * 
     * @return Beanのレコード。読み込むレコードがない場合は、nullを返します。
     * 
     * @throws IOException レコードの読み込みに失敗した場合。
     * @throws SuperCsvNoMatchColumnSizeException レコードのカラムサイズに問題がある場合
     * @throws SuperCsvBindingException セルの値に問題がある場合
     * @throws SuperCsvException 設定など、その他に問題がある場合
     * 
     */
    public T read() throws IOException {
        
        if(readRow()) {
            
            final T bean = instantiateBean(beanMappingCache.getOriginal().getType());
            final CsvBindingErrors bindingErrors = new CsvBindingErrors(beanMappingCache.getOriginal().getType());
            
            final CsvContext context = new CsvContext(getLineNumber(), getRowNumber(), 1);
            context.setRowSource(new ArrayList<Object>(processedColumns));
            
            Optional<SuperCsvRowException> rowException = Optional.empty();
            try {
                executeCellProcessor(processedColumns, getColumns(), beanMappingCache.getCellProcessorsForReading(), context);
                
            } catch(SuperCsvRowException e) {
                /*
                 * カラムごとのCellProcessorのエラーの場合、別なValidatorで値を検証するために、
                 * 後から判定を行うようにする。
                 */
                rowException = Optional.of(e);
                
                final List<CsvError> errors = exceptionConverter.convert(e, beanMappingCache.getOriginal());
                bindingErrors.addAllErrors(errors);
                
            } catch(SuperCsvException e) {
                errorMessages.addAll(exceptionConverter.convertAndFormat(e, beanMappingCache.getOriginal()));
                throw e;
            }
            
            // コールバックメソッドの実行（読み込み前）
            for(CallbackMethod callback : beanMappingCache.getOriginal().getPreReadMethods()) {
                callback.invoke(bean, context, bindingErrors, beanMappingCache.getOriginal());
            }
            
            // beanへのマッピング
            populateBean(bean, beanMappingCache.getNameMapping(), bindingErrors);
            
            // Bean(レコード)の入力値検証
            for(CsvValidator<T> recordValidator : validators) {
                recordValidator.validate(bean, bindingErrors, new ValidationContext<>(context, beanMappingCache.getOriginal()));
            }
            
            // コールバックメソッドの実行（読み込み後）
            for(CallbackMethod callback : beanMappingCache.getOriginal().getPostReadMethods()) {
                callback.invoke(bean, context, bindingErrors, beanMappingCache.getOriginal());
            }
            
            // エラーメッセージの変換
            processErrors(bindingErrors, context, rowException);
            
            return bean;
            
        }
        
        return null; // EOF
        
        
    }
    
    /**
     * 成功時、例外発生時の処理を指定して、1レコード分を読み込みます。
     * 
     * @since 2.3
     * @param successHandler 読み込み成功時の処理の実装。
     * @param errorHandler CSVに関する例外発生時の処理の実装。
     * @return CSVの読み込み処理ステータスを返します。
     * @throws IOException 致命的なレコードの読み込みに失敗した場合にスローされます。
     */
    public CsvReadStatus read(final CsvSuccessHandler<T> successHandler, final CsvErrorHandler errorHandler) throws IOException {
        
        try {
            final T bean = read();
            if(bean != null) {
                successHandler.onSuccess(bean);
                return CsvReadStatus.SUCCESS;
            } else {
                return CsvReadStatus.EOF;
            }
        
        } catch(SuperCsvException e) {
            errorHandler.onError(e);
            return CsvReadStatus.ERROR;
            
        }
        
    }
    
    /**
     * {@link Stream} を返します。要素はCSVの行をBeanにマッピングしたオブジェクトです。
     * <p>読み込む際には例外 {@link SuperCsvException} / {@link UncheckedIOException} が発生する可能性があります（読み込みを行った {@link Stream} メソッドからスローされます)。</p>
     * <p>読み込み時にスローされた {@link IOException} は、{@link UncheckedIOException} にラップされます。</p>
     * 
     * @since 2.3
     * @return 各レコードをBeanに変換した {@link Stream} を返します。
     */
    public Stream<T> lines() {
        
        Iterator<T> itr = new Iterator<T>() {
            
            T nextLine = null;
            
            @Override
            public boolean hasNext() {
                if(nextLine != null) {
                    return true;
                    
                } else {
                    try {
                        nextLine = read();
                        return (nextLine != null);
                    } catch(IOException e) {
                        throw new UncheckedIOException(e);
                    }
                }
            }

            @Override
            public T next() {
                if (nextLine != null || hasNext()) {
                    T line = nextLine;
                    nextLine = null;
                    return line;
                } else {
                    throw new NoSuchElementException();
                }
            }
            
        };
        
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(
                itr, Spliterator.ORDERED | Spliterator.NONNULL), false);

    }
    
    /**
     * CSVのヘッダーの検証を行います。
     * 
     * @param sourceHeader オリジナルのヘッダー情報。
     * @param definedHeader アノテーションなどの定義を元にしたヘッダー情報
     * @throws SuperCsvNoMatchColumnSizeException ヘッダーのサイズ（カラム数）がBean定義と一致しない場合。
     * @throws SuperCsvNoMatchHeaderException ヘッダーの値がBean定義と一致しない場合。
     */
    protected void validateHeader(final String[] sourceHeader, final String[] definedHeader) {
        
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
     * 行の例外情報をメッセージに変換したりします。
     * @param bindingErrors
     * @param context
     * @param rowException
     */
    protected void processErrors(final CsvBindingErrors bindingErrors, final CsvContext context,
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
    protected T instantiateBean(final Class<T> clazz) {
        
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
     * 行の各カラムの値に対して、CellProcessorを適用します。
     * @param destination
     * @param source
     * @param processors
     * @param context
     * @throws SuperCsvNoMatchColumnSizeException カラムサイズが定義と一致しない場合
     * @throws SuperCsvRowException CellProcessor内で発生した例外
     */
    protected void executeCellProcessor(final List<Object> destination, final List<String> source,
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
    protected void populateBean(final T resultBean, final String[] nameMapping, final CsvBindingErrors bindingErrors) {
        
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
     * Beanクラスを元に作成したヘッダー情報を取得する。
     * @return ヘッダー一覧。
     */
    public String[] getDefinedHeader() {
        return beanMappingCache.getHeader();
    }
    
    /**
     * Beanのマッピング情報を取得します。
     * @return Beanのマッピング情報
     */
    public BeanMapping<T> getBeanMapping() {
        return beanMappingCache.getOriginal();
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

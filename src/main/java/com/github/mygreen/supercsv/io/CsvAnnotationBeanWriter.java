package com.github.mygreen.supercsv.io;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.exception.SuperCsvCellProcessorException;
import org.supercsv.exception.SuperCsvException;
import org.supercsv.exception.SuperCsvReflectionException;
import org.supercsv.io.AbstractCsvWriter;
import org.supercsv.prefs.CsvPreference;
import org.supercsv.util.CsvContext;
import org.supercsv.util.MethodCache;

import com.github.mygreen.supercsv.builder.BeanMapping;
import com.github.mygreen.supercsv.builder.BeanMappingFactory;
import com.github.mygreen.supercsv.builder.CallbackMethod;
import com.github.mygreen.supercsv.exception.SuperCsvBindingException;
import com.github.mygreen.supercsv.exception.SuperCsvRowException;
import com.github.mygreen.supercsv.validation.CsvBindingErrors;
import com.github.mygreen.supercsv.validation.CsvExceptionConverter;
import com.github.mygreen.supercsv.validation.CsvValidator;
import com.github.mygreen.supercsv.validation.CsvError;
import com.github.mygreen.supercsv.validation.ValidationContext;

/**
 * アノテーションを元にCSVファイルを出力するためのクラス。
 *
 * @param <T> マッピング対象のBeanのクラスタイプ
 * @version 2.0.2
 * @author T.TSUCHIE
 *
 */
public class CsvAnnotationBeanWriter<T> extends AbstractCsvWriter {
    
    /** temporary storage of bean values */
    private final List<Object> beanValues = new ArrayList<>();
    
    /** temporary storage of processed columns to be written */
    private final List<Object> processedColumns = new ArrayList<>();
    
    /** cache of methods for mapping from fields to columns */
    private final MethodCache cache = new MethodCache();
    
    private final BeanMappingCache<T> beanMapping;
    
    /** exception converter. */
    private CsvExceptionConverter exceptionConverter = new CsvExceptionConverter();
    
    /** processing error messages. */
    private final List<String> errorMessages = new ArrayList<>();
    
    /** validator */
    private final List<CsvValidator<T>> validators = new ArrayList<>();
    
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
        this.beanMapping = BeanMappingCache.create(factory.create(beanType, groups));
        this.validators.addAll(beanMapping.getOriginal().getValidators());
        
    }
    
    /**
     * Beanのマッピング情報を指定して、{@link CsvAnnotationBeanWriter}を作成するコンストラクタ。
     * <p>{@link BufferedWriter}にラップして実行されるため、ラップする必要はありません。</p>
     * 
     * @param beanMapping Beanのマッピング情報。
     * @param writer the writer
     * @param preference the CSV preferences.
     * @throws NullPointerException {@literal if beanMapping or writer or preferences are null.}
     */
    public CsvAnnotationBeanWriter(final BeanMapping<T> beanMapping, final Writer writer, final CsvPreference preference) {
        super(writer, preference);
        
        Objects.requireNonNull(beanMapping, "beanMapping should not be null.");
        
        this.beanMapping = BeanMappingCache.create(beanMapping);
        this.validators.addAll(beanMapping.getValidators());
    }
    
    /**
     * Beanクラスを元に作成したヘッダー情報を取得する。
     * <p>ただし、列番号を省略され、定義がされていないカラムは、{@literal column[カラム番号]}の形式となります。</p>
     * @return ヘッダー一覧。
     */
    public String[] getDefinedHeader() {
        return beanMapping.getHeader();
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
        
        if(beanMapping.getOriginal().isHeader() && getLineNumber() == 0) {
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
        
    }
    
    /**
     * レコードを書き込みます。
     * 
     * @param source 書き込むレコード。
     * @throws NullPointerException source is null.
     * @throws IOException レコードの出力に失敗した場合。
     * @throws SuperCsvException レコードの値に問題がある場合
     * 
     */
    public void write(final T source) throws IOException {
        
        Objects.requireNonNull(source, "the bean to write should not be null.");
        
        // update the current row/line numbers
        super.incrementRowAndLineNo();
        
        final CsvContext context = new CsvContext(getLineNumber(), getRowNumber(), 1);
        context.setRowSource(Collections.emptyList());  // 空の値を入れる
        
        final CsvBindingErrors bindingErrors = new CsvBindingErrors(beanMapping.getOriginal().getType());
        
        // コールバックメソッドの実行（書き込み前）
        for(CallbackMethod callback : beanMapping.getOriginal().getPreWriteMethods()) {
            callback.invoke(source, context, bindingErrors, beanMapping.getOriginal());
        }
        
        // extract the bean values
        extractBeanValues(source, beanMapping.getNameMapping());
        context.setRowSource(new ArrayList<Object>(beanValues));
        
        Optional<SuperCsvRowException> rowException = Optional.empty();
        try {
            executeCellProcessors(processedColumns, beanValues, beanMapping.getCellProcessorsForWriting(), context);
            
        } catch(SuperCsvRowException e) {
            /*
             * カラムごとのCellProcessorのエラーの場合、別なValidatorで値を検証するために、
             * 後から判定を行うようにする。
             */
            rowException = Optional.of(e);
            
            final List<CsvError> errors = exceptionConverter.convert(e, beanMapping.getOriginal());
            bindingErrors.addAllErrors(errors);
            
        } catch(SuperCsvException e) {
            // convert exception and format to message.
            errorMessages.addAll(exceptionConverter.convertAndFormat(e, beanMapping.getOriginal()));
            throw e;
        }
        
        // レコード、Beanの入力値検証
        if(!beanMapping.getOriginal().isSkipValidationOnWrite()) {
            for(CsvValidator<T> validator : validators) {
                validator.validate(source, bindingErrors, new ValidationContext<>(context, beanMapping.getOriginal()));
            }
        }
        
        // エラーメッセージの変換
        processErrors(bindingErrors, context, rowException);
        
        // write the list
        super.writeRow(processedColumns);
        
        // コールバックメソッドの実行（書き込み後）
        for(CallbackMethod callback : beanMapping.getOriginal().getPostWriteMethods()) {
            callback.invoke(source, context, bindingErrors, beanMapping.getOriginal());
        }
        
        // エラーメッセージの変換
        processErrors(bindingErrors, context, rowException);
        
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
     * Extracts the bean values, using the supplied name mapping array.
     * 
     * @param source
     *            the bean
     * @param nameMapping
     *            the name mapping
     * @throws NullPointerException
     *             if source or nameMapping are null
     * @throws SuperCsvReflectionException
     *             if there was a reflection exception extracting the bean value
     */
    private void extractBeanValues(final Object source, final String[] nameMapping) throws SuperCsvReflectionException {
        
        Objects.requireNonNull(nameMapping, "the nameMapping array can't be null as it's used to map from fields to columns");
        
        beanValues.clear();
        
        for( int i = 0; i < nameMapping.length; i++ ) {
            
            final String fieldName = nameMapping[i];
            
            if( fieldName == null ) {
                beanValues.add(null); // assume they always want a blank column
                
            } else {
                Method getMethod = cache.getGetMethod(source, fieldName);
                try {
                    beanValues.add(getMethod.invoke(source));
                }
                catch(final Exception e) {
                    throw new SuperCsvReflectionException(String.format("error extracting bean value for field %s",
                        fieldName), e);
                }
            }
            
        }
        
    }
    
    /**
     * 
     * @see Util#executeCellProcessors(List, List, CellProcessor[], int, int)
     */
    private void executeCellProcessors(final List<Object> destination, final List<?> source,
            final CellProcessor[] processors, final CsvContext context) {
        
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
     * レコード用の値を検証するValidatorを追加します。
     * @param validators {@link CsvValidator}の実装クラスを設定します。
     */
    @SuppressWarnings("unchecked")
    public void addValidator(CsvValidator<T>... validators ) {
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

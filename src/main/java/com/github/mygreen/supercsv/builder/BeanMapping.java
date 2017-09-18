package com.github.mygreen.supercsv.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.supercsv.cellprocessor.ift.CellProcessor;

import com.github.mygreen.supercsv.validation.CsvValidator;

/**
 * 解析したBeanのマッピング情報です。
 *
 * @param <T> Beanのクラスタイプ
 * @version 2.1
 * @author T.TSUCHIE
 * 
 */
public class BeanMapping<T> {
    
    private final Class<T> type;
    
    private boolean header;
    
    private boolean validateHeader;
    
    private List<CsvValidator<T>> validators = new ArrayList<>();
    
    private List<Object> listeners = new ArrayList<>();
    
    private List<ColumnMapping> columns = new ArrayList<>();
    
    private List<CallbackMethod> preReadMethods = new ArrayList<>();
    
    private List<CallbackMethod> postReadMethods = new ArrayList<>();
    
    private List<CallbackMethod> preWriteMethods = new ArrayList<>();
    
    private List<CallbackMethod> postWriteMethods = new ArrayList<>();
    
    private boolean skipValidationOnWrite;
    
    private Class<?>[] groups;
    
    private HeaderMapper headerMapper;
    
    private Configuration configuration;
    
    public BeanMapping(final Class<T> type) {
        this.type = type;
    }
    
    /**
     * 指定した列番号を持つカラムのマッピング情報を取得する。
     * @param columnNumber 列番号。1から始まる。
     * @return 引数で指定した位置情報の値と一致するカラム情報。
     */
    public Optional<ColumnMapping> getColumnMapping(final int columnNumber) {
        return columns.stream()
                .filter(c -> c.getNumber() == columnNumber)
                .findFirst();
    }
    
    /**
     * 指定したカラム名を持つカラムのマッピング情報を取得する。
     * @param columnName カラム名（フィールド名）を指定します。
     * @return 引数で指定したカラム名の値と一致するカラム情報。
     */
    public Optional<ColumnMapping> getColumnMapping(final String columnName) {
        return columns.stream()
                .filter(c -> c.getName() != null && c.getName().equals(columnName))
                .findFirst();
    }
    
    /**
     * カラムのヘッダー一覧を取得する。
     * @return カラムの位置順のヘッダー一覧。
     */
    public String[] getHeader() {
        
        return columns.stream()
            .map(c -> headerMapper.toMap(c, configuration, groups))
            .toArray(n -> new String[n]);
        
    }
    
    /**
     * フィールド名の一覧を取得する。
     * <p>CSVのレコードを出力する際に利用する。</p>
     * @return カラムの位置順のフィールドの一覧。
     */
    public String[] getNameMapping() {
        
        return columns.stream()
                .map(c -> c.getName())
                .toArray(n -> new String[n]);
    }
    
    /**
     * 読み込み用の{@link CellProcessor}を取得する。
     * @return カラムの位置順に整列されている{@link CellProcessor}の一覧。
     */
    public CellProcessor[] getCellProcessorsForReading() {
        
        return columns.stream()
                .map(c -> c.getCellProcessorForReading())
                .toArray(n -> new CellProcessor[n]);
    }
    
    /**
     * 書き込み用の{@link CellProcessor}を取得する。
     * @return カラムの位置順に整列されている{@link CellProcessor}の一覧。
     */
    public CellProcessor[] getCellProcessorsForWriting() {
        
        return columns.stream()
                .map(c -> c.getCellProcessorForWriting())
                .toArray(n -> new CellProcessor[n]);
    }
    
    /**
     * 
     * @return Beanのクラスタイプを取得する。
     */
    public Class<T> getType() {
        return type;
    }
    
    /**
     * ヘッダーが存在するとして処理するかどうか。
     * @return trueの場合、ヘッダーが存在するとして処理を行う。
     */
    public boolean isHeader() {
        return header;
    }
    
    /**
     * ヘッダーが存在するとして処理するかどうか設定する。
     * @param header trueの場合、ヘッダーが存在するとして処理を行う。
     */
    public void setHeader(boolean header) {
        this.header = header;
    }
    
    public boolean isValidateHeader() {
        return validateHeader;
    }
    
    public void setValidateHeader(boolean validateHeader) {
        this.validateHeader = validateHeader;
    }
    
    public List<CsvValidator<T>> getValidators() {
        return validators;
    }
    
    public void addAllValidators(List<CsvValidator<T>> validators) {
        this.validators.addAll(validators);
    }
    
    public List<Object> getListeners() {
        return listeners;
    }
    
    public void addAllListeners(List<Object> listeners) {
        this.listeners.addAll(listeners);
    }
    
    public List<ColumnMapping> getColumns() {
        return columns;
    }
    
    public void setColumns(List<ColumnMapping> columns) {
        this.columns = columns;;
    }
    
    public void addAllColumns(List<ColumnMapping> columns) {
        this.columns.addAll(columns);
    }
    
    public List<CallbackMethod> getPreReadMethods() {
        return preReadMethods;
    }
    
    public void addPreReadMethod(final CallbackMethod method) {
        this.preReadMethods.add(method);
    }
    
    public List<CallbackMethod> getPostReadMethods() {
        return postReadMethods;
    }
    
    public void addPostReadMethod(final CallbackMethod method) {
        this.postReadMethods.add(method);
    }
    
    public List<CallbackMethod> getPreWriteMethods() {
        return preWriteMethods;
    }
    
    public void addPreWriteMethod(final CallbackMethod method) {
        this.preWriteMethods.add(method);
    }
    
    public List<CallbackMethod> getPostWriteMethods() {
        return postWriteMethods;
    }
    
    public void addPostWriteMethod(final CallbackMethod method) {
        this.postWriteMethods.add(method);
    }
    
    public boolean isSkipValidationOnWrite() {
        return skipValidationOnWrite;
    }
    
    public void setSkipValidationOnWrite(boolean skipValidationOnWrite) {
        this.skipValidationOnWrite = skipValidationOnWrite;
    }
    
    public Class<?>[] getGroups() {
        return groups;
    }
    
    public void setGroups(Class<?>[] groups) {
        this.groups = groups;
    }
    
    /**
     * カラムに対するヘッダー情報を取得するためのマッパーを取得する。
     * @since 2.1
     * @return {@link HeaderMapper}の実装クラス。
     */
    public HeaderMapper getHeaderMapper() {
        return headerMapper;
    }
    
    /**
     * カラムに対するヘッダー情報を取得するためのマッパーを設定します。
     * @since 2.1
     * @param headerMapper {@link HeaderMapper}の実装クラス。
     */
    public void setHeaderMapper(HeaderMapper headerMapper) {
        this.headerMapper = headerMapper;
    }
    
    /**
     * システム情報を取得します。
     * @return 既存のシステム情報を変更する際に取得します。
     */
    public Configuration getConfiguration() {
        return configuration;
    }
    
    /**
     * システム情報を取得します。
     * @param configuraton 新しくシステム情報を変更する際に設定します。
     */
    public void setConfiguration(Configuration configuraton) {
        this.configuration = configuraton;
    }

}

package com.github.mygreen.supercsv.builder;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.exception.SuperCsvReflectionException;

import com.github.mygreen.supercsv.annotation.CsvBean;
import com.github.mygreen.supercsv.annotation.CsvColumn;
import com.github.mygreen.supercsv.annotation.CsvPartial;
import com.github.mygreen.supercsv.annotation.CsvPostRead;
import com.github.mygreen.supercsv.annotation.CsvPostWrite;
import com.github.mygreen.supercsv.annotation.CsvPreRead;
import com.github.mygreen.supercsv.annotation.CsvPreWrite;
import com.github.mygreen.supercsv.annotation.DefaultGroup;
import com.github.mygreen.supercsv.exception.SuperCsvInvalidAnnotationException;
import com.github.mygreen.supercsv.localization.MessageBuilder;
import com.github.mygreen.supercsv.validation.CsvValidator;

/**
 * BeanからCSVのマッピング情報を作成するクラス。
 * 
 * @version 2.1
 * @author T.TSUCHIE
 *
 */
public class BeanMappingFactory {
    
    private Configuration configuration = new Configuration();
    
    /**
     * デフォルトコンストラクタ
     */
    public BeanMappingFactory() {
        
    }
    
    /**
     * Beanクラスから、CSVのマッピング情報を作成します。
     * 
     * @param <T> Beanのタイプ
     * @param beanType 作成元のBeanクラス。
     * @param groups グループ情報。
     *              アノテーションを指定したグループで切り替える際に指定します。
     *              何も指定しない場合は、デフォルトグループの{@link DefaultGroup}のクラスが指定されたとして処理します。
     * @return CSVのマッピング情報。
     * @throws NullPointerException {@literal beanType == null.}
     * @throws SuperCsvInvalidAnnotationException アノテーションの定義が不正な場合。
     */
    @SuppressWarnings({"unchecked"})
    public <T> BeanMapping<T> create(final Class<T> beanType, final Class<?>... groups) {
        
        Objects.requireNonNull(beanType);
        
        final BeanMapping<T> beanMapping = new BeanMapping<>(beanType);
        beanMapping.setConfiguration(configuration);
        
        // アノテーション @CsvBeanの取得
        final CsvBean beanAnno = beanType.getAnnotation(CsvBean.class);
        if(beanAnno == null) {
            throw new SuperCsvInvalidAnnotationException(beanAnno, MessageBuilder.create("anno.notFound")
                        .varWithClass("property", beanType)
                        .varWithAnno("anno", CsvBean.class)
                        .format());
        }
        
        beanMapping.setHeader(beanAnno.header());
        beanMapping.setValidateHeader(beanAnno.validateHeader());
        
        // CsvValidatorの取得
        final List<CsvValidator<T>> validators = Arrays.stream(beanAnno.validators())
                .map(v -> (CsvValidator<T>)configuration.getBeanFactory().create(v))
                .collect(Collectors.toList());
        beanMapping.addAllValidators(validators);
        
        // アノテーション @CsvColumn の取得
        final List<ColumnMapping> columnMappingList = new ArrayList<>();
        for(Field field : beanType.getDeclaredFields()) {
            
            final CsvColumn columnAnno = field.getAnnotation(CsvColumn.class);
            if(columnAnno != null) {
                columnMappingList.add(createColumnMapping(field, columnAnno, configuration, groups));
            }
            
        }
        
        // カラムの位置順の並び変えと、位置のチェック
        columnMappingList.sort(null);
        validateColumnAndSupplyPartialColumn(beanType, columnMappingList, Optional.ofNullable(beanType.getAnnotation(CsvPartial.class)));
        beanMapping.addAllColumns(columnMappingList);
        
        // コールバック用のメソッドの取得
        for(Method method : beanType.getDeclaredMethods()) {
            
            if(method.getAnnotation(CsvPreRead.class) != null) {
                beanMapping.addPreReadMethod(new CallbackMethod(method));
            }
            
            if(method.getAnnotation(CsvPostRead.class) != null) {
                beanMapping.addPostReadMethod(new CallbackMethod(method));
            }
            
            if(method.getAnnotation(CsvPreWrite.class) != null) {
                beanMapping.addPreWriteMethod(new CallbackMethod(method));
            }
            
            if(method.getAnnotation(CsvPostWrite.class) != null) {
                beanMapping.addPostWriteMethod(new CallbackMethod(method));
            }
        }
        
        // リスナークラスの取得
        final List<Object> listeners = Arrays.stream(beanAnno.listeners())
                .map(l -> configuration.getBeanFactory().create(l))
                .collect(Collectors.toList());
        beanMapping.addAllListeners(listeners);
        
        for(Object listener : listeners) {
            for(Method method : listener.getClass().getDeclaredMethods()) {
                if(method.getAnnotation(CsvPreRead.class) != null) {
                    beanMapping.addPreReadMethod(new ListenerCallbackMethod(listener, method));
                }
                
                if(method.getAnnotation(CsvPostRead.class) != null) {
                    beanMapping.addPostReadMethod(new ListenerCallbackMethod(listener, method));
                }
                
                if(method.getAnnotation(CsvPreWrite.class) != null) {
                    beanMapping.addPreWriteMethod(new ListenerCallbackMethod(listener, method));
                }
                
                if(method.getAnnotation(CsvPostWrite.class) != null) {
                    beanMapping.addPostWriteMethod(new ListenerCallbackMethod(listener, method));
                }
            }
        }
        
        // ヘッダーのマッピングクラスの取得
        final HeaderMapper headerMapper = (HeaderMapper) configuration.getBeanFactory().create(beanAnno.headerMapper());
        beanMapping.setHeaderMapper(headerMapper);
        
        beanMapping.getPreReadMethods().sort(null);
        beanMapping.getPostReadMethods().sort(null);
        beanMapping.getPreWriteMethods().sort(null);
        beanMapping.getPostWriteMethods().sort(null);
        
        beanMapping.setSkipValidationOnWrite(configuration.isSkipValidationOnWrite());
        beanMapping.setGroups(groups);
        
        return beanMapping;
    }
    
    @SuppressWarnings({"rawtypes", "unchecked"})
    private ColumnMapping createColumnMapping(final Field field, final CsvColumn columnAnno,
            final Configuration config, final Class<?>[] groups) {
        
        final FieldAccessor fieldAccessor = new FieldAccessor(field, config.getAnnoationComparator());
        
        final ColumnMapping columnMapping = new ColumnMapping();
        columnMapping.setField(fieldAccessor);
        columnMapping.setNumber(columnAnno.number());
        
        if(columnAnno.label().isEmpty()) {
            columnMapping.setLabel(field.getName());
        } else {
            columnMapping.setLabel(columnAnno.label());
        }
        
        // ProcessorBuilderの取得
        ProcessorBuilder builder;
        if(columnAnno.builder().length == 0) {
            
            builder = config.getBuilderResolver().resolve(fieldAccessor.getType());
            if(builder == null) {
                // 不明なタイプの場合
                builder = new GeneralProcessorBuilder();
            }
            
        } else {
            // 直接Builderクラスが指定されている場合
            try {
                builder = (ProcessorBuilder) config.getBeanFactory().create(columnAnno.builder()[0]);
                
            } catch(Throwable e) {
                throw new SuperCsvReflectionException(
                        String.format("Fail create instance of %s with attribute 'builderClass' of @CsvColumn",
                                columnAnno.builder()[0].getCanonicalName()), e);
            }
        }
        
        // CellProcessorの作成
        columnMapping.setCellProcessorForReading(
                (CellProcessor)builder.buildForReading(field.getType(), fieldAccessor, config, groups).orElse(null));
        
        columnMapping.setCellProcessorForWriting(
                (CellProcessor)builder.buildForWriting(field.getType(), fieldAccessor, config,  groups).orElse(null));
        
        if(builder instanceof AbstractProcessorBuilder) {
            columnMapping.setFormatter(((AbstractProcessorBuilder)builder).getFormatter(fieldAccessor, config));
        }
        
        return columnMapping;
        
    }
    
    /**
     * カラム情報の検証と、部分的に読み込む場合のカラム情報を補足する。
     * @param beanType Beanのクラスタイプ
     * @param list カラム番号の昇順に並び変えられたカラム情報。
     * @param partialAnno アノテーション{@link CsvPartial}の情報
     */
    private void validateColumnAndSupplyPartialColumn(final Class<?> beanType, final List<ColumnMapping> list, 
            final Optional<CsvPartial> partialAnno) {
        
        if(list.isEmpty()) {
            throw new SuperCsvInvalidAnnotationException(MessageBuilder.create("anno.notFound")
                    .varWithClass("property", beanType)
                    .varWithAnno("anno", CsvColumn.class)
                    .format());
        }
        
        // check duplicated column number value 
        final Set<Integer> checkedNumber = new TreeSet<>();
        final Set<Integer> duplicateNumbers = new TreeSet<>();
        for(ColumnMapping columnMapping : list) {
            
            if(checkedNumber.contains(columnMapping.getNumber())) {
                duplicateNumbers.add(columnMapping.getNumber());
            }
            checkedNumber.add(columnMapping.getNumber());
            
        }
        
        if(!duplicateNumbers.isEmpty()) {
            // 重複している 属性 numberが存在する場合
            throw new SuperCsvInvalidAnnotationException(MessageBuilder.create("anno.attr.duplicated")
                    .var("property", beanType.getName())
                    .varWithAnno("anno", CsvColumn.class)
                    .var("attrName", "number")
                    .var("attrValues", duplicateNumbers)
                    .format());
        }
        
        // カラム番号が1以上かのチェック
        final int minColumnNumber = list.get(0).getNumber();
        if(minColumnNumber <= 0) {
            throw new SuperCsvInvalidAnnotationException(MessageBuilder.create("anno.attr.min")
                    .var("property", beanType.getName())
                    .varWithAnno("anno", CsvColumn.class)
                    .var("attrName", "number")
                    .var("attrValue", minColumnNumber)
                    .var("min", 1)
                    .format());
            
        }
        
        // 定義されている列番号の最大値
        final int maxColumnNumber = list.get(list.size()-1).getNumber();
        
        // Beanに定義されていない欠けているカラム番号の取得
        final Set<Integer> lackNumbers = new TreeSet<Integer>();
        for(int i=1; i <= maxColumnNumber; i++) {
            if(!checkedNumber.contains(i)) {
                lackNumbers.add(i);
            }
        }
        
        // 定義されているカラム番号より、大きなカラム番号を持つカラム情報の補足
        if(partialAnno.isPresent()) {
            
            final int partialColumnSize = partialAnno.get().columnSize();
            if(maxColumnNumber > partialColumnSize) {
                throw new SuperCsvInvalidAnnotationException(partialAnno.get(), MessageBuilder.create("anno.CsvPartial.columSizeMin")
                        .var("property", beanType.getName())
                        .var("columnSize", partialColumnSize)
                        .var("maxColumnNumber", maxColumnNumber)
                        .format());
                
            }
            
            if(maxColumnNumber < partialColumnSize) {
                for(int i= maxColumnNumber+1; i <= partialColumnSize; i++) {
                    lackNumbers.add(i);
                }
            }
            
        }
        
        // 不足分のカラムがある場合は、部分的な読み書き用カラムとして追加する
        if(lackNumbers.size() > 0) {
            
            for(int number : lackNumbers) {
                list.add(createPartialColumnMapping(number, partialAnno));
            }
            
            list.sort(null);
        }
        
    }
    
    /**
     * 部分的なカラムの場合の作成
     * @param columnNumber 列番号
     * @param partialAnno
     * @return
     */
    private ColumnMapping createPartialColumnMapping(int columnNumber, final Optional<CsvPartial> partialAnno) {
        
        final ColumnMapping columnMapping = new ColumnMapping();
        columnMapping.setNumber(columnNumber);
        columnMapping.setPartialized(true);
        
        String label = String.format("column%d", columnNumber);
        if(partialAnno.isPresent()) {
            for(CsvPartial.Header header : partialAnno.get().headers()) {
                if(header.number() == columnNumber) {
                    label = header.label();
                }
            }
        }
        columnMapping.setLabel(label);
        
        return columnMapping;
        
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

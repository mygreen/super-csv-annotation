package org.supercsv.ext.builder;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.supercsv.ext.annotation.CsvBean;
import org.supercsv.ext.annotation.CsvColumn;
import org.supercsv.ext.exception.SuperCsvInvalidAnnotationException;


/**
 * クラスに定義されているアノテーション情報を解析し、CellProcessor情報を解析するクラス。
 * 
 * @version 1.1
 * @author T.TSUCHIE
 *
 */
public class CsvAnnotationBeanParser {
    
    private CellProcessorBuilderContainer builderContainer;
    
    private CellProcessorBuilderFactory builderFactory;
    
    /**
     * {@link CsvAnnotationBeanParser}を生成するコンストラクタ。
     * 
     */
    public CsvAnnotationBeanParser() {
        this.builderContainer = new CellProcessorBuilderContainer();
        
        this.builderFactory = new CellProcessorBuilderFactory() {
            
            @Override
            public <T extends CellProcessorBuilder<?>> T create(final Class<T> builderClass) throws Exception {
                return builderClass.newInstance();
            }
            
        };
    }
    
    /**
     * クラスに付与されたアノテーション情報を解析し、CSVのマッピング情報を作成する。
     * 
     * @param clazz クラスタイプ。
     * @return アノテーションを元に作成したCSVのマッピング情報。
     * @throws NullPointerException class is null.
     * @throws SuperCsvInvalidAnnotationException アノテーションの定義が不正な場合。
     *     クラスタイプにアノテーション{@link CsvBean}が付与されていない。
     *     アノテーション{@link CsvColumn}のpositionの値が不正な場合。
     */
    public <T> CsvBeanMapping<T> parse(final Class<T> clazz) {
        return parse(clazz, false);
    }
    
    /**
     * クラスに付与されたアノテーション情報を解析し、CSVのマッピング情報を作成する。
     * @param clazz クラスタイプ。
     * @param ignoreValidationProcessorOnOutput 書き込み用のCellProcessorから制約チェックを行うものを除外するかどうか。
     * @return アノテーションを元に作成したCSVのマッピング情報。
     * @throws NullPointerException class is null.
     * @throws SuperCsvInvalidAnnotationException アノテーションの定義が不正な場合。
     *     クラスタイプにアノテーション{@link CsvBean}が付与されていない。
     *     アノテーション{@link CsvColumn}のpositionの値が不正な場合。
     */
    public <T> CsvBeanMapping<T> parse(final Class<T> clazz, final boolean ignoreValidationProcessorOnOutput) {
        
        if(clazz == null) {
            throw new NullPointerException("clazz must be not null.");
        }
        
        final CsvBeanMapping<T> mappingBean = new CsvBeanMapping<T>(clazz);
        
        // @CsvBean
        final CsvBean csvBeanAnno = clazz.getAnnotation(CsvBean.class);
        if(csvBeanAnno == null) {
            throw new SuperCsvInvalidAnnotationException("not found annotation 'CsvBean'");
        }
        
        mappingBean.setHeader(csvBeanAnno.header());
        
        // @CsvColumn for all(public / private / default / protected)
        final List<CsvColumnMapping> mappingColumns = new ArrayList<>();
        for(Field field : clazz.getDeclaredFields()) {
            
            CsvColumn csvColumnAnno = field.getAnnotation(CsvColumn.class);
            if(csvColumnAnno == null) {
                continue;
            }
            
            mappingColumns.add(createColumnMapping(field, csvColumnAnno, ignoreValidationProcessorOnOutput));
            
        }
        
        // sorting column data by position value
        Collections.sort(mappingColumns, new Comparator<CsvColumnMapping>() {
            
            @Override
            public int compare(final CsvColumnMapping o1, final CsvColumnMapping o2) {
                if(o1.getPosition() > o2.getPosition()) {
                    return 1;
                } else if(o1.getPosition() == o2.getPosition()) {
                    return o1.getColumnName().compareTo(o2.getColumnName());
                } else {
                    return -1;
                }
            }
            
        });
        
        validatePosition(mappingColumns);
        mappingBean.setColumns(mappingColumns);
        
        return mappingBean;
    }
    
    private void validatePosition(final List<CsvColumnMapping> columns) {
        if(columns.isEmpty()) {
            throw new SuperCsvInvalidAnnotationException("not found column definition.");
        }
        
        // check duplicated position value 
        Set<Integer> checkedPosiiton = new TreeSet<Integer>();
        Set<Integer> duplicatePosition = new TreeSet<Integer>();
        for(CsvColumnMapping columnMapping : columns) {
            
            if(checkedPosiiton.contains(columnMapping.getPosition())) {
                duplicatePosition.add(columnMapping.getPosition());
            }
            checkedPosiiton.add(columnMapping.getPosition());
            
        }
        
        // check lacked or skipped position value 
        Set<Integer> lackPosition = new TreeSet<Integer>();
        final int maxPosition = columns.get(columns.size()-1).getPosition();
        for(int i=0; i < maxPosition; i++) {
            if(!checkedPosiiton.contains(i)) {
                lackPosition.add(i);
            }
        }
        
        if(!lackPosition.isEmpty() || !duplicatePosition.isEmpty()) {
            throw new SuperCsvInvalidAnnotationException(
                    String.format("position value is wrong. lacked position=%s, duplicated position=%s",
                            lackPosition, duplicatePosition));
        }
    }
    
    @SuppressWarnings({"rawtypes", "unchecked"})
    private CsvColumnMapping createColumnMapping(final Field field, final CsvColumn csvColumnAnno,
            final boolean ignoreValidationProcessorOnOutput) {
        
        CsvColumnMapping columnMapping = new CsvColumnMapping();
        
        columnMapping.setColumnName(field.getName());
        columnMapping.setColumnType(field.getType());
        columnMapping.setPosition(csvColumnAnno.position());
        
        if(csvColumnAnno.label() != null && !csvColumnAnno.label().isEmpty()) {
            columnMapping.setLabel(csvColumnAnno.label());
        } else {
            columnMapping.setLabel(field.getName());
        }
        
        CellProcessorBuilder builder = null;
        if(csvColumnAnno.builderClass().equals(DefaultCellProcessorBuilder.class)) {
            builder = builderContainer.getBuilder(field.getType());
            
            // if enum type
            if(builder == null && Enum.class.isAssignableFrom(field.getType())) {
                builder = builderContainer.getBuilder(Enum.class);
            }
            
            // if not found builder, use default builder.
            if(builder == null) {
                builder = DefaultCellProcessorBuilder.INSTANCE;
            }
            
        } else {
            // use custom builder class
            try {
                builder = builderFactory.create(csvColumnAnno.builderClass());
            } catch (Throwable e) {
                throw new SuperCsvInvalidAnnotationException(
                        String.format("fail create instance of %s with 'builderClass' of @CsvColumn property",
                                csvColumnAnno.builderClass().getCanonicalName()), e);
            }
        }
        
        if(builder != null) {
            columnMapping.setOutputCellProcessor(builder.buildOutputCellProcessor(
                    field.getType(), field.getAnnotations(), ignoreValidationProcessorOnOutput));
            
            columnMapping.setInputCellProcessor(builder.buildInputCellProcessor(
                    field.getType(), field.getAnnotations()));
            
        } else {
            // user default builder.
            throw new SuperCsvInvalidAnnotationException(
                    String.format("not resolve CellProecssorBuilder for field '%s#%s' and type '%s'",
                            field.getDeclaringClass().getName(), field.getName(),
                            field.getType().getClass().getName()));
        }
        
        return columnMapping;
        
    }
    
    /**
     * {@link CellProcessorBuilder}を管理するコンテナクラスを取得する。
     * @return {@link CellProcessorBuilder}のコンテナクラス。
     */
    public CellProcessorBuilderContainer getBuilderContainer() {
        return builderContainer;
    }
    
    /**
     * {@link CellProcessorBuilder}を管理するコンテナクラスを設定する。
     * @param builderContainer {@link CellProcessorBuilder}のコンテナクラス。
     */
    public void setBuilderContainer(final CellProcessorBuilderContainer builderContainer) {
        this.builderContainer = builderContainer;
    }
    
    /**
     * 独自の{@link CellProcessorBuilder}を指定したときのインスタンスを作成するクラスを取得する。
     * @return {@link CellProcessorBuilder}のファクトリクラス。
     */
    public CellProcessorBuilderFactory getBuilderFactory() {
        return builderFactory;
    }
    
    /**
     * 独自の{@link CellProcessorBuilder}を指定したときのインスタンスを作成するクラスを取得する。
     * @param builderFactory {@link CellProcessorBuilder}のファクトリクラス。
     */
    public void setBuilderFactory(CellProcessorBuilderFactory builderFactory) {
        this.builderFactory = builderFactory;
    }
    
}

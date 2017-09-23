package com.github.mygreen.supercsv.builder;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.github.mygreen.supercsv.annotation.CsvBean;
import com.github.mygreen.supercsv.annotation.CsvColumn;
import com.github.mygreen.supercsv.annotation.DefaultGroup;
import com.github.mygreen.supercsv.exception.SuperCsvInvalidAnnotationException;
import com.github.mygreen.supercsv.localization.MessageBuilder;

/**
 * カラム番号の設定、チェックを行わないで、BeanからCSVのマッピング情報を作成するクラス。
 *
 * @since 2.1
 * @author T.TSUCHIE
 *
 */
public class LazyBeanMappingFactory extends BeanMappingFactory {
    
    /**
     * デフォルトコンストラクタ
     */
    public LazyBeanMappingFactory() {
        
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
    @Override
    public <T> BeanMapping<T> create(final Class<T> beanType, final Class<?>... groups) {
        
        Objects.requireNonNull(beanType);
        
        final Configuration configuration = getConfiguration();
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
        
        // ヘッダーの設定情報の組み立て
        buildHeaderMapper(beanMapping, beanAnno);
        
        // 入力値検証の設定を組み立てます。
        buildValidators(beanMapping, beanAnno, groups);
        
        // アノテーション @CsvColumn を元にしたカラム情報の組み立て
        buildColumnMappingList(beanMapping, beanType, groups);
        
        // コールバックメソッドの設定
        buildCallbackMethods(beanMapping, beanType, beanAnno);
        
        return beanMapping;
    }
    
    /**
     * アノテーション{@link CsvColumn}を元に、カラムのマッピング情報を組み立てる。
     * <p>カラム番号の検証や、部分的なカラムのカラムの組み立てはスキップ。</p>
     * @param beanMapping Beanのマッピング情報
     * @param beanType  Beanのクラスタイプ
     * @param groups グループ情報
     */
    @Override
    protected <T> void buildColumnMappingList(final BeanMapping<T> beanMapping, final Class<T> beanType, final Class<?>[] groups) {
        
        final List<ColumnMapping> columnMappingList = new ArrayList<>();
        for(Field field : beanType.getDeclaredFields()) {
            
            final CsvColumn columnAnno = field.getAnnotation(CsvColumn.class);
            if(columnAnno != null) {
                columnMappingList.add(createColumnMapping(field, columnAnno, groups));
            }
            
        }
        
        beanMapping.addAllColumns(columnMappingList);
        
    }
    
    
}

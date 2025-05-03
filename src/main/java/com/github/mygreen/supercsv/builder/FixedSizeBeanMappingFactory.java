package com.github.mygreen.supercsv.builder;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.github.mygreen.supercsv.annotation.CsvBean;
import com.github.mygreen.supercsv.annotation.conversion.CsvFixedSize;
import com.github.mygreen.supercsv.exception.SuperCsvInvalidAnnotationException;
import com.github.mygreen.supercsv.localization.MessageBuilder;
import com.github.mygreen.supercsv.util.Utils;

/**
 * 固定長用のBean定義からCSVのマッピング情報を作成するクラス。
 *
 * @since 2.5
 * @author T.TSUCHIE
 *
 */
public class FixedSizeBeanMappingFactory extends BeanMappingFactory {

    /**
     * デフォルトコンストラクタ
     */
    public FixedSizeBeanMappingFactory() {

    }
    
    /**
     * ヘッダーの処理のデフォルトを {@link FixedSizeHeaderMapper}に設定する。
     * {@inheritDoc}
     */
    @Override
    protected <T> void buildHeaderMapper(final BeanMapping<T> beanMapping, final CsvBean beanAnno) {
        
        final HeaderMapper headerMapper;
        if (beanAnno.headerMapper().equals(DefaultHeaderMapper.class)) {
            // デフォルトのヘッダーマッパーを指定している場合、固定長用のヘッダーマッパーに置き換える
            headerMapper= (HeaderMapper) getConfiguration().getBeanFactory().create(FixedSizeHeaderMapper.class);
        } else {
            headerMapper = (HeaderMapper) getConfiguration().getBeanFactory().create(beanAnno.headerMapper());
        }
        
        beanMapping.setHeaderMapper(headerMapper);
        beanMapping.setHeader(beanAnno.header());
        beanMapping.setValidateHeader(beanAnno.validateHeader());
        
    }

    @Override
    protected void validateColumnAndSupplyPartialColumn(final Class<?> beanType, final List<ColumnMapping> list) {

        super.validateColumnAndSupplyPartialColumn(beanType, list);
        
        // 全てのカラムに固定長の設定が付与されているかどうかチェックする
        Set<String> notFixedColumnList = new LinkedHashSet<>();
        for(ColumnMapping columnMapping : list) {
            if(columnMapping.getFixedSizeProperty() == null) {
                // エラーメッセージ用のカラム名の組み立て
                final String columnName;
                if(Utils.isNotEmpty(columnMapping.getName())) {
                    columnName = String.format("%s(%d)", columnMapping.getName(), columnMapping.getNumber());
                } else {
                    columnName = String.format("%s(%d)", columnMapping.getLabel(), columnMapping.getNumber());
                }
                notFixedColumnList.add(columnName);
            }
        }
        
        if(!notFixedColumnList.isEmpty()) {
            // 固定長カラムの設定がない numberが存在する場合
            throw new SuperCsvInvalidAnnotationException(MessageBuilder.create("anno.CsvFixedSize.notFoundFixedAnno")
                    .var("property", beanType.getName())
                    .varWithAnno("anno", CsvFixedSize.class)
                    .var("columnList", notFixedColumnList)
                    .format());
        }
        
    }

}

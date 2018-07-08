package com.github.mygreen.supercsv.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.supercsv.exception.SuperCsvException;

import com.github.mygreen.supercsv.annotation.CsvColumn;
import com.github.mygreen.supercsv.annotation.CsvPartial;
import com.github.mygreen.supercsv.exception.SuperCsvInvalidAnnotationException;
import com.github.mygreen.supercsv.io.LazyCsvAnnotationBeanReader;
import com.github.mygreen.supercsv.io.LazyCsvAnnotationBeanWriter;
import com.github.mygreen.supercsv.localization.MessageBuilder;

/**
 * {@link BeanMapping}を組み立てる時のヘルパクラス。
 * 
 * @version 2.2
 * @since 2.1
 * @author T.TSUCHIE
 *
 */
public class BeanMappingFactoryHelper {
    
    /**
     * カラム番号が重複しているかチェックする。また、番号が1以上かもチェックする。
     * @param beanType Beanタイプ
     * @param list カラム情報の一覧
     * @return チェック済みの番号
     * @throws SuperCsvInvalidAnnotationException {@link CsvColumn}の定義が間違っている場合
     */
    public static TreeSet<Integer> validateDuplicatedColumnNumber(final Class<?> beanType, final List<ColumnMapping> list) {
        
        final TreeSet<Integer> checkedNumber = new TreeSet<>();
        final TreeSet<Integer> duplicatedNumbers = new TreeSet<>();
        for(ColumnMapping columnMapping : list) {
            
            if(checkedNumber.contains(columnMapping.getNumber())) {
                duplicatedNumbers.add(columnMapping.getNumber());
            }
            checkedNumber.add(columnMapping.getNumber());
            
        }
        
        if(!duplicatedNumbers.isEmpty()) {
            // 重複している 属性 numberが存在する場合
            throw new SuperCsvInvalidAnnotationException(MessageBuilder.create("anno.attr.duplicated")
                    .var("property", beanType.getName())
                    .varWithAnno("anno", CsvColumn.class)
                    .var("attrName", "number")
                    .var("attrValues", duplicatedNumbers)
                    .format());
        }
        
        // カラム番号が1以上かチェックする
        final int minColumnNumber = checkedNumber.first();
        if(minColumnNumber <= 0) {
            throw new SuperCsvInvalidAnnotationException(MessageBuilder.create("anno.attr.min")
                    .var("property", beanType.getName())
                    .varWithAnno("anno", CsvColumn.class)
                    .var("attrName", "number")
                    .var("attrValue", minColumnNumber)
                    .var("min", 1)
                    .format());
            
        }
        
        return checkedNumber;
    }
    
    /**
     * 欠けているカラム番号がある場合、その番号を持つダミーのカラムを追加する。
     * @param beanType Beanタイプ
     * @param list カラム情報の一覧
     * @param partialAnno Beanに設定されているアノテーション{@link CsvPartial}の情報。
     * @param suppliedHeaders 提供されたヘッダー。提供されてない場合は、長さ0の配列。
     * @return
     */
    public static TreeSet<Integer> supplyLackedNumberMappingColumn(final Class<?> beanType, final List<ColumnMapping> list,
            final Optional<CsvPartial> partialAnno, final String[] suppliedHeaders) {
        
        final TreeSet<Integer> checkedNumber = list.stream()
                .filter(col -> col.isDeterminedNumber())
                .map(col -> col.getNumber())
                .collect(Collectors.toCollection(TreeSet::new));
        
        // 定義されている列番号の最大値
        final int maxColumnNumber = checkedNumber.last();
        
        // Beanに定義されていない欠けているカラム番号の取得
        final TreeSet<Integer> lackedNumbers = new TreeSet<Integer>();
        for(int i=1; i <= maxColumnNumber; i++) {
            if(!checkedNumber.contains(i)) {
                lackedNumbers.add(i);
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
                    lackedNumbers.add(i);
                }
            }
            
        }
        
        // 不足分のカラムがある場合は、部分的な読み書き用カラムとして追加する
        if(lackedNumbers.size() > 0) {
            
            for(int number : lackedNumbers) {
                list.add(createPartialColumnMapping(number, partialAnno, getSuppliedHeaders(suppliedHeaders, number)));
            }
            
            list.sort(null);
        }
        
        return lackedNumbers;
        
    }
    
    /**
     * 提供されたヘッダーから該当するカラム番号のヘッダーを取得する。
     * @param suppliedHeaders 提供されたヘッダー。提供されてない場合は、長さ0の配列。
     * @param columnNumber カラム番号。1から始まる。
     * @return 該当するカラムのヘッダー。見つからない場合は空を返す。
     */
    private static Optional<String> getSuppliedHeaders(final String[] suppliedHeaders, final int columnNumber) {
        
        final int length = suppliedHeaders.length;
        if(length == 0) {
            return Optional.empty();
        }
        
        if(columnNumber < length) {
            return Optional.ofNullable(suppliedHeaders[columnNumber-1]);
        }
        
        return Optional.empty();
        
    }
    
    /**
     * 部分的なカラムの場合の作成
     * @param columnNumber 列番号
     * @param partialAnno アノテーション {@literal @CsvPartial}のインスタンス
     * @param suppliedHeader 補完対象のヘッダーの値
     * @return 部分的なカラム情報。
     */
    private static ColumnMapping createPartialColumnMapping(int columnNumber, final Optional<CsvPartial> partialAnno,
            final Optional<String> suppliedHeader) {
        
        final ColumnMapping columnMapping = new ColumnMapping();
        columnMapping.setNumber(columnNumber);
        columnMapping.setPartialized(true);
        
        String label = String.format("column%d", columnNumber);
        
        if(suppliedHeader.isPresent()) {
            label = suppliedHeader.get();
        }
        
        if(partialAnno.isPresent()) {
            for(CsvPartial.Header header : partialAnno.get().headers()) {
                if(header.number() == columnNumber) {
                    label = header.label();
                    break;
                }
            }
        }
        columnMapping.setLabel(label);
        
        return columnMapping;
        
    }
    
    /**
     * カラム番号が決定していないカラムをチェックする。
     * <p>{@link LazyCsvAnnotationBeanReader}/{@link LazyCsvAnnotationBeanWriter}において、
     *    CSVファイルや初期化時のヘッダーが不正により、該当するラベルがヘッダーに見つからないときをチェックする。
     * </p>
     * 
     * @since 2.2
     * @param beanType Beanタイプ
     * @param list カラム情報の一覧
     * @param headers ヘッダー
     * @throws SuperCsvException カラム番号が決定していないとき
     */
    public static void validateNonDeterminedColumnNumber(final Class<?> beanType, final List<ColumnMapping> list,
            String[] headers) {
        
        final List<String> nonDeterminedLabels = list.stream()
                .filter(col -> !col.isDeterminedNumber())
                .map(col -> col.getLabel())
                .collect(Collectors.toList());
        
        if(!nonDeterminedLabels.isEmpty()) {
            
            throw new SuperCsvException(MessageBuilder.create("lazy.noDeteminedColumns")
                    .var("property", beanType.getName())
                    .var("labels", nonDeterminedLabels)
                    .var("headers", headers)
                    .format());
            
        }
        
    }
    
}

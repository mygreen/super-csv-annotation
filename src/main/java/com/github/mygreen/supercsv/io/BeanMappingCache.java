package com.github.mygreen.supercsv.io;

import org.supercsv.cellprocessor.ift.CellProcessor;

import com.github.mygreen.supercsv.builder.BeanMapping;


/**
 * Beanのマッピング情報のキャッシュ。
 * <p>レコードの実行ごとに、</p>
 *
 * @version 2.0
 * @author T.TSUCHIE
 *
 */
public class BeanMappingCache<T> {
    
    private final BeanMapping<T> beanMapping;
    
    private String[] header;
    
    private String[] nameMapping;
    
    private CellProcessor[] cellProcessorsForReading;
    
    private CellProcessor[] cellProcessorsForWriting;
    
    private BeanMappingCache(final BeanMapping<T> beanMapping) {
        this.beanMapping = beanMapping;
    }
    
    public static <T> BeanMappingCache<T> create(final BeanMapping<T> beanMapping) {
        
        final BeanMappingCache<T> cache = new BeanMappingCache<>(beanMapping);
        
        cache.header = beanMapping.getHeader();
        cache.nameMapping = beanMapping.getNameMapping();
        cache.cellProcessorsForReading = beanMapping.getCellProcessorsForReading();
        cache.cellProcessorsForWriting = beanMapping.getCellProcessorsForWriting();
        
        return cache;
    }
    
    /**
     * キャッシュ元のデータを取得する。
     * @return キャッシュ元となったマッピング情報。
     */
    public BeanMapping<T> getOriginal() {
        return beanMapping;
    }
    
    
    public String[] getHeader() {
        return header;
    }
    
    public String[] getNameMapping() {
        return nameMapping;
    }
    
    public CellProcessor[] getCellProcessorsForReading() {
        return cellProcessorsForReading;
    }
    
    public CellProcessor[] getCellProcessorsForWriting() {
        return cellProcessorsForWriting;
    }
    
}

package org.supercsv.ext.builder;


/**
 * {@link CellProcessorBuilder}のインスタンスを作成するインタフェース
 * 
 * @since 1.1
 * @author T.TSUCHIE
 *
 */
public interface CellProcessorBuilderFactory {
    
    /**
     * 指定した{@link CellProcessorBuilder}のインスタンスを作成する。
     * @param builderClass 作成する builderClass
     * @return
     * @throws Exception 
     */
    <T extends CellProcessorBuilder<?>> T create(Class<T> builderClass) throws Exception;
    
}

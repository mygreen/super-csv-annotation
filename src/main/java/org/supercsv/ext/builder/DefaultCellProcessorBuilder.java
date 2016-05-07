package org.supercsv.ext.builder;

import java.lang.annotation.Annotation;
import java.util.Optional;

import org.supercsv.cellprocessor.ift.CellProcessor;


/**
 * デフォルトの{@link CellProcessorBuilder}。
 * <p>固有の{@link CellProcessor}の組み立ては行わない。</p>
 * 
 * @version 1.1
 * @author T.TSUCHIE
 *
 */
public class DefaultCellProcessorBuilder extends AbstractCellProcessorBuilder<Class<?>>{
    
    public static DefaultCellProcessorBuilder INSTANCE = new DefaultCellProcessorBuilder();
    
    @Override
    public CellProcessor buildOutputCellProcessor(final Class<Class<?>> type, final Annotation[] annos,
            CellProcessor cllProcessor, final boolean ignoreValidationProcessor) {
        return cllProcessor;
    }
    
    @Override
    public CellProcessor buildInputCellProcessor(final Class<Class<?>> type, final Annotation[] annos,
            final CellProcessor cellProcessor) {
        return cellProcessor;
    }
    
    /**
     * 空の値を返す。
     */
    @Override
    public Optional<Class<?>> parseValue(final Class<Class<?>> type, final Annotation[] annos, final String defaultValue) {
        return Optional.empty();
    }
    
}

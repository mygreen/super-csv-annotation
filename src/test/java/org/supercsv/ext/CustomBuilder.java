package org.supercsv.ext;

import java.lang.annotation.Annotation;

import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.ext.builder.AbstractCellProcessorBuilder;


public class CustomBuilder extends AbstractCellProcessorBuilder<Class> {

    @Override
    public CellProcessor buildOutputCellProcessor(Class<Class> type, Annotation[] annos,
            CellProcessor cellProcessor, final boolean ignoreValidableProcessor) {
        System.out.println("カスタムセル");
        return cellProcessor;
    }

    @Override
    public CellProcessor buildInputCellProcessor(Class<Class> type, Annotation[] annos,
            CellProcessor cellProcessor) {
        System.out.println("カスタムセル");
        return cellProcessor;
    }

    @Override
    public Class getParseValue(Class<Class> type, Annotation[] annos, String defaultValue) {
        // TODO Auto-generated method stub
        return null;
    }
    
}

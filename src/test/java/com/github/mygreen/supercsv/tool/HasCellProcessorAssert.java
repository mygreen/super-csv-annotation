package com.github.mygreen.supercsv.tool;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;

import org.assertj.core.api.AbstractAssert;
import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.cellprocessor.ift.CellProcessor;


/**
 * Assertj用の任意のCellProcessorを持っているかの判定を行う。
 * 
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class HasCellProcessorAssert extends AbstractAssert<HasCellProcessorAssert, CellProcessor> {

    public HasCellProcessorAssert(final CellProcessor actual) {
        super(actual, HasCellProcessorAssert.class);
    }
    
    /**
     * static import用のエントリポイント
     * @param actual
     * @return
     */
    public static HasCellProcessorAssert assertThat(final CellProcessor actual) {
        return new HasCellProcessorAssert(actual);
    }
    
    public HasCellProcessorAssert hasCellProcessor(final Class<? extends CellProcessor> clazz) {
        
        CellProcessor cp = actual;
        if(!(cp instanceof CellProcessorAdaptor)) {
            failWithMessage("Expected CellProcessor should be implemeted CellProcessorAdaptor, but <%s> wat not implemented",
                    cp.getClass().getName());
        }
        
        do{
            if(cp.getClass().isAssignableFrom(clazz)) {
                return this;
            }
            
            // next processor
            try {
                if(cp instanceof CellProcessorAdaptor) {
                    final Field field = CellProcessorAdaptor.class.getDeclaredField("next");
                    field.setAccessible(true);
                    cp = (CellProcessor) field.get(cp);
                } else {
                    break;
                }
                
            } catch(ReflectiveOperationException e) {
                failWithMessage("Fail get field of CellProcessor next chain. :%s", e.getMessage());
            }
            
        } while(cp != null);
        
        StringWriter writer = new StringWriter();
        TestUtils.printCellProcessorChain((CellProcessor)actual, new PrintWriter(writer));
        writer.flush();
        
        failWithMessage("Expected CellProcessor should be implemeted CellProcessorAdaptor, but <%s> wat not implemented.%s",
                cp.getClass().getName(), writer.toString());
        
        return this;
    }
    
}

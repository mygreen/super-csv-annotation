package com.github.mygreen.supercsv.tool;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.cellprocessor.ift.CellProcessor;

/**
 * 指定したCellProcessorを持つか検査するJUnitのMatcher.
 * <p>chainの先も検査する。
 * 
 * @author T.TSUCHIE
 *
 * @param <T>
 */
public class HasCellProcessor<T extends CellProcessor> extends BaseMatcher<Class<T>> {
    
    private final Class<T> clazz;
    
    private Object actual;
    
    public HasCellProcessor(final Class<T> clazz) {
        if(clazz == null) {
            throw new IllegalArgumentException("clazz should not be null.");
        }
        this.clazz = clazz;
    }
    
    @Override
    public boolean matches(final Object actual) {
        this.actual = actual;
        if(!(actual instanceof CellProcessorAdaptor)) {
            return false;
        }
        
        CellProcessor cp = (CellProcessorAdaptor)actual;
        do {
            
            if(cp.getClass().isAssignableFrom(clazz)) {
                return true;
            }
            
            // next processor
            try {
                if(cp instanceof CellProcessorAdaptor) {
                    Field field = CellProcessorAdaptor.class.getDeclaredField("next");
                    field.setAccessible(true);
                    cp = (CellProcessor) field.get(cp);
                } else {
                    break;
                }
                
            } catch(ReflectiveOperationException e) {
                return false;
            }
            
        } while(cp != null);
        
        return false;
    }
    
    @Override
    public void describeTo(final Description desc) {
        
        desc.appendValue(clazz.getName());
        if(actual != null) {
            desc.appendText(" but actual is \n");
            StringWriter writer = new StringWriter();
            TestUtils.printCellProcessorChain((CellProcessor)actual, new PrintWriter(writer));
            writer.flush();
            
            desc.appendText(writer.toString());
        }
        
    }
    
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T> org.hamcrest.Matcher<T> hasCellProcessor(final Class<? extends CellProcessor> clazz) {
        return (Matcher<T>) new HasCellProcessor(clazz);
    }

    
}

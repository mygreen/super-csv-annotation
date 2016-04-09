/*
 * ParseFloat.java
 * created in 2013/03/06
 *
 * (C) Copyright 2003-2013 GreenDay Project. All rights reserved.
 */
package org.supercsv.ext.cellprocessor;

import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.cellprocessor.ift.DoubleCellProcessor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;
import org.supercsv.exception.SuperCsvCellProcessorException;
import org.supercsv.util.CsvContext;


/**
 *
 *
 * @author T.TSUCHIE
 *
 */
public class ParseFloat extends CellProcessorAdaptor implements StringCellProcessor {
    
    public ParseFloat() {
        super();
    }
    
    public ParseFloat(final DoubleCellProcessor next) {
        super(next);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public Object execute(final Object value, final CsvContext context) {
        
        validateInputNotNull(value, context);
        
        final Float result;
        if( value instanceof Float) {
            result = (Float) value;
            
        } else if( value instanceof String ) {
            try {
                result = Float.valueOf((String) value);
            } catch(final NumberFormatException e) {
                throw new SuperCsvCellProcessorException(String.format("'%s' could not be parsed as an Float", value),
                    context, this, e);
            }
        } else {
            final String actualClassName = value.getClass().getName();
            throw new SuperCsvCellProcessorException(String.format(
                "the input value should be of type Float or String but is of type %s", actualClassName), context, this);
        }
        
        return next.execute(result, context);
    }
}

/*
 * ParseByte.java
 * created in 2013/03/06
 *
 * (C) Copyright 2003-2013 GreenDay Project. All rights reserved.
 */
package org.supercsv.ext.cellprocessor;

import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.cellprocessor.ift.LongCellProcessor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;
import org.supercsv.exception.SuperCsvCellProcessorException;
import org.supercsv.util.CsvContext;


/**
 *
 *
 * @author T.TSUCHIE
 *
 */
public class ParseByte extends CellProcessorAdaptor implements StringCellProcessor {
    
    public ParseByte() {
        super();
    }
    
    public ParseByte(final LongCellProcessor next) {
        super(next);
    }
    
    @Override
    public Object execute(final Object value, final CsvContext context) {
        
        validateInputNotNull(value, context);
        
        final Byte result;
        if( value instanceof Byte ) {
            result = (Byte) value;
            
        } else if( value instanceof String ) {
            try {
                result = Byte.valueOf((String) value);
            } catch(final NumberFormatException e) {
                throw new SuperCsvCellProcessorException(String.format("'%s' could not be parsed as an Byte", value),
                    context, this, e);
            }
        } else {
            final String actualClassName = value.getClass().getName();
            throw new SuperCsvCellProcessorException(String.format(
                "the input value should be of type Byte or String but is of type %s", actualClassName), context, this);
        }
        
        return next.execute(result, context);
    }
}

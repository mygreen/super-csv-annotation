/*
 * ParseBigInteger.java
 * created in 2013/03/06
 *
 * (C) Copyright 2003-2013 GreenDay Project. All rights reserved.
 */
package org.supercsv.ext.cellprocessor;

import java.math.BigInteger;

import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;
import org.supercsv.exception.SuperCsvCellProcessorException;
import org.supercsv.util.CsvContext;


/**
 *
 *
 * @author T.TSUCHIE
 *
 */
public class ParseBigInteger extends CellProcessorAdaptor implements StringCellProcessor {
    
    public ParseBigInteger() {
        super();
    }
    
    public ParseBigInteger(final CellProcessor next) {
        super(next);
    }
    
    @Override
    public Object execute(final Object value, final CsvContext context) {
        
        validateInputNotNull(value, context);
        
        final BigInteger result;
        if( value instanceof BigInteger ) {
            result = (BigInteger) value;
            
        } else if( value instanceof String ) {
            final String s = (String) value;
            try {
                result = new BigInteger(s);
            } catch(final NumberFormatException e) {
                throw new SuperCsvCellProcessorException(String.format("'%s' could not be parsed as an BigInteger", value),
                    context, this, e);
            }
        } else {
            final String actualClassName = value.getClass().getName();
            throw new SuperCsvCellProcessorException(String.format(
                "the input value should be of type BigInteger or String but is of type %s", actualClassName), context, this);
        }
        
        return next.execute(result, context);
    }
}

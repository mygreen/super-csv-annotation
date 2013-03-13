/*
 * ConvertNullToNext.java
 * created in 2013/03/12
 *
 * (C) Copyright 2003-2013 GreenDay Project. All rights reserved.
 */
package org.supercsv.ext.cellprocessor;

import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.cellprocessor.ift.BoolCellProcessor;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.ift.DateCellProcessor;
import org.supercsv.cellprocessor.ift.DoubleCellProcessor;
import org.supercsv.cellprocessor.ift.LongCellProcessor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;
import org.supercsv.util.CsvContext;


/**
 *
 *
 * @author T.TSUCHIE
 *
 */
public class ConvertNullToNext extends CellProcessorAdaptor implements BoolCellProcessor, DateCellProcessor,
        DoubleCellProcessor, LongCellProcessor, StringCellProcessor {
    
    protected final Object returnValue;
    
    public ConvertNullToNext(final Object returnValue) {
        super();
        this.returnValue = returnValue;
    }
    
    public ConvertNullToNext(final Object returnValue, final CellProcessor next) {
        super(next);
        this.returnValue = returnValue;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Object execute(final Object value, final CsvContext context) {
        if( value == null ) {
            return next.execute(returnValue, context);
        }
        
        return next.execute(value, context);
    }
    
    
}
